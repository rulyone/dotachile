#!/usr/bin/env python3
"""Stage 2: redact emails.jsonl into a markdown corpus and chunk file.

Two redaction passes:
  Pass A: deterministic regex (phones, IPs, credit cards, passwords)
  Pass B: statistical (Presidio) for names, emails, locations

Plus deterministic sender pseudonymization with HMAC-SHA256.
"""

from __future__ import annotations

import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent))
import redaction_rules  # noqa: E402


def _apply_pass_a(text: str) -> str:
    """Run all regex-based redactions on a body string."""
    out = text
    # Order matters: replace credit cards first (longest digit runs),
    # then phones, then IPs.
    for cc in redaction_rules.find_credit_cards(out):
        out = out.replace(cc, "[REDACTED_CC]")
    out = redaction_rules.PHONE_PATTERN.sub("[REDACTED_PHONE]", out)
    out = redaction_rules.IPV4_PATTERN.sub("[REDACTED_IP]", out)
    out = redaction_rules.IPV6_PATTERN.sub("[REDACTED_IP]", out)
    out = redaction_rules.URL_TOKEN_PATTERN.sub("[REDACTED_URL_TOKEN]", out)
    out = redaction_rules.PASSWORD_LINE_PATTERN.sub("[REDACTED_PASSWORD_LINE]", out)
    return out


def _build_presidio_engine():
    """Construct a Presidio AnalyzerEngine configured for English + Spanish.

    Importing presidio is slow (loads spaCy), so this should be called once
    per process and reused.
    """
    from presidio_analyzer import AnalyzerEngine
    from presidio_analyzer.nlp_engine import NlpEngineProvider

    nlp_configuration = {
        "nlp_engine_name": "spacy",
        "models": [
            {"lang_code": "en", "model_name": "en_core_web_sm"},
            {"lang_code": "es", "model_name": "es_core_news_sm"},
        ],
    }
    provider = NlpEngineProvider(nlp_configuration=nlp_configuration)
    nlp_engine = provider.create_engine()
    return AnalyzerEngine(
        nlp_engine=nlp_engine,
        supported_languages=list(redaction_rules.PRESIDIO_LANGUAGES),
    )


def _apply_pass_b(text: str, engine, language: str = "es") -> str:
    """Run Presidio redaction on a body string.

    Replaces detected entities with [REDACTED_<ENTITY_TYPE>] markers.
    Operates back-to-front so character offsets stay valid during replacement.
    """
    results = engine.analyze(
        text=text,
        language=language,
        entities=list(redaction_rules.PRESIDIO_ENTITIES),
    )
    results.sort(key=lambda r: r.start, reverse=True)
    out = text
    for r in results:
        marker = f"[REDACTED_{r.entity_type}]"
        out = out[: r.start] + marker + out[r.end :]
    return out


def _apply_pass_b_multilingual(text: str, engine) -> str:
    """Run Pass B once per supported language and union the detections.

    The corpus is bilingual. Running only one language under-detects the
    other — e.g. the Spanish NER misses subtle English person-name casing.
    """
    all_results = []
    for lang in redaction_rules.PRESIDIO_LANGUAGES:
        try:
            found = engine.analyze(
                text=text,
                language=lang,
                entities=list(redaction_rules.PRESIDIO_ENTITIES),
            )
        except Exception:
            # If a specific language has no model loaded, skip it gracefully.
            continue
        all_results.extend(found)

    # Merge overlapping / identical spans: keep the longest span per overlap.
    all_results.sort(key=lambda r: (r.start, -(r.end - r.start)))
    merged: list = []
    for r in all_results:
        if merged and r.start < merged[-1].end:
            # Overlaps the previous — extend the previous if this one ends later.
            if r.end > merged[-1].end:
                merged[-1] = r if (r.end - r.start) > (merged[-1].end - merged[-1].start) else merged[-1]
            continue
        merged.append(r)

    # Replace back-to-front so offsets remain valid.
    merged.sort(key=lambda r: r.start, reverse=True)
    out = text
    for r in merged:
        marker = f"[REDACTED_{r.entity_type}]"
        out = out[: r.start] + marker + out[r.end :]
    return out


import hashlib
import hmac
import json
import secrets
from email.utils import parseaddr


class Pseudonymizer:
    """Stable HMAC-SHA256 → USER_NNNN mapping for sender addresses.

    On first construction with a missing mapping file, a fresh 32-byte
    HMAC key is generated. Subsequent constructions with the same path
    reuse the key so token assignments stay stable across runs.

    The mapping file is JSON with structure:
        {"key": "<hex>", "tokens": {"<hmac_hex>": "USER_0001", ...}}
    """

    def __init__(self, mapping_path: Path):
        self._path = mapping_path
        if mapping_path.exists():
            data = json.loads(mapping_path.read_text())
            self._key = bytes.fromhex(data["key"])
            self._tokens: dict[str, str] = data.get("tokens", {})
        else:
            self._key = secrets.token_bytes(32)
            self._tokens = {}
        self._next_index = len(self._tokens) + 1

    @staticmethod
    def _normalise(address: str) -> str:
        _, email = parseaddr(address)
        return email.strip().lower()

    def token_for(self, address: str) -> str:
        normalised = self._normalise(address)
        if not normalised:
            return "USER_UNKNOWN"
        digest = hmac.new(self._key, normalised.encode("utf-8"), hashlib.sha256).hexdigest()
        if digest not in self._tokens:
            self._tokens[digest] = f"USER_{self._next_index:04d}"
            self._next_index += 1
        return self._tokens[digest]

    def save(self) -> None:
        self._path.parent.mkdir(parents=True, exist_ok=True)
        self._path.write_text(
            json.dumps({"key": self._key.hex(), "tokens": self._tokens}, indent=2)
        )


import argparse
import hashlib as _hashlib
from datetime import datetime


def _parse_date(raw: str) -> datetime | None:
    from email.utils import parsedate_to_datetime

    try:
        return parsedate_to_datetime(raw)
    except (TypeError, ValueError):
        return None


def _thread_filename(thread_id: str, first_date: datetime | None) -> str:
    short = _hashlib.sha1(thread_id.encode("utf-8")).hexdigest()[:6]
    if first_date is not None:
        prefix = first_date.strftime("%Y-%m")
    else:
        prefix = "unknown"
    return f"{prefix}-thread-{short}.md"


def _redact_record(record: dict, engine, pseudonymizer: Pseudonymizer) -> dict:
    """Apply Pass A + Pass B + pseudonymization to a single email record."""
    body = _apply_pass_a(record["body"])
    body = _apply_pass_b_multilingual(body, engine)
    sender_token = pseudonymizer.token_for(record.get("from", ""))
    recipient_token = pseudonymizer.token_for(record.get("to", ""))
    return {
        "thread_id": record.get("thread_id", ""),
        "date": record.get("date", ""),
        "sender": sender_token,
        "recipient": recipient_token,
        "subject": record.get("subject", ""),
        "labels": record.get("labels", ""),
        "body": body,
    }


def _write_thread_markdown(path: Path, messages: list[dict]) -> None:
    participants = sorted({m["sender"] for m in messages} | {m["recipient"] for m in messages})
    labels_set = sorted({l.strip() for m in messages for l in m["labels"].split(",") if l.strip()})
    first = messages[0]
    frontmatter_lines = [
        "---",
        f"date: {first['date']}",
        f"participants: [{', '.join(participants)}]",
        f"labels: [{', '.join(labels_set)}]",
        f"subject: {first['subject']}",
        "---",
        "",
    ]
    body_blocks: list[str] = []
    for m in messages:
        body_blocks.append(f"## {m['sender']} — {m['date']}\n\n{m['body'].strip()}\n")
    path.write_text("\n".join(frontmatter_lines) + "\n---\n\n".join(body_blocks))


def redact(jsonl_path: Path, corpus_dir: Path, mapping_path: Path) -> int:
    if not jsonl_path.exists():
        print(f"error: input not found: {jsonl_path}", file=sys.stderr)
        return 2
    try:
        corpus_resolved = corpus_dir.resolve()
        mapping_resolved = mapping_path.resolve()
        mapping_resolved.relative_to(corpus_resolved)
        print(
            f"error: mapping file {mapping_path} must NOT live inside corpus dir {corpus_dir}",
            file=sys.stderr,
        )
        return 3
    except ValueError:
        pass  # mapping is outside corpus — good.

    corpus_dir.mkdir(parents=True, exist_ok=True)
    pseudonymizer = Pseudonymizer(mapping_path)
    engine = _build_presidio_engine()

    # Group records by thread_id, preserving file order.
    threads: dict[str, list[dict]] = {}
    with jsonl_path.open("r", encoding="utf-8") as f:
        for line in f:
            record = json.loads(line)
            redacted = _redact_record(record, engine, pseudonymizer)
            threads.setdefault(redacted["thread_id"] or "no-thread", []).append(redacted)

    # Sort messages inside each thread by parsed date.
    for tid, msgs in threads.items():
        msgs.sort(key=lambda m: _parse_date(m["date"]) or datetime.min)

    # Write markdown + collect chunks.
    chunks: list[dict] = []
    for tid, msgs in threads.items():
        first_date = _parse_date(msgs[0]["date"])
        filename = _thread_filename(tid, first_date)
        _write_thread_markdown(corpus_dir / filename, msgs)
        for idx, msg in enumerate(msgs):
            chunks.append(
                {
                    "thread_file": filename,
                    "message_index": idx,
                    "body": msg["body"],
                }
            )

    chunks_path = corpus_dir / "corpus_chunks.jsonl"
    with chunks_path.open("w", encoding="utf-8") as f:
        for chunk in chunks:
            f.write(json.dumps(chunk, ensure_ascii=False) + "\n")

    pseudonymizer.save()
    print(
        f"wrote {len(threads)} threads, {len(chunks)} chunks to {corpus_dir}",
        file=sys.stderr,
    )
    return 0


def main() -> int:
    parser = argparse.ArgumentParser(description="Redact emails.jsonl into a corpus.")
    parser.add_argument("input_jsonl", type=Path)
    parser.add_argument("corpus_dir", type=Path)
    parser.add_argument("--mapping", type=Path, required=True, help="path to mapping.json (must be outside corpus_dir)")
    args = parser.parse_args()
    return redact(args.input_jsonl, args.corpus_dir, args.mapping)


if __name__ == "__main__":
    sys.exit(main())
