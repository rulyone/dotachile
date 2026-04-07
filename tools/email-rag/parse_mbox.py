#!/usr/bin/env python3
"""Stage 1: convert a Gmail Takeout .mbox into one-JSON-per-line emails.jsonl.

Pure I/O conversion. No PII handling. Attachments are dropped (filenames
logged to stderr only).
"""

from __future__ import annotations

import json
import mailbox
import sys
from email.message import Message
from html.parser import HTMLParser
from pathlib import Path


class _HTMLTextExtractor(HTMLParser):
    def __init__(self) -> None:
        super().__init__()
        self._chunks: list[str] = []

    def handle_data(self, data: str) -> None:
        self._chunks.append(data)

    def get_text(self) -> str:
        return "".join(self._chunks)


def _strip_html(html: str) -> str:
    parser = _HTMLTextExtractor()
    parser.feed(html)
    return parser.get_text().strip()


def _extract_body(msg: Message) -> tuple[str, list[str]]:
    """Return (body_text, attachment_filenames)."""
    attachments: list[str] = []
    text_parts: list[str] = []
    html_parts: list[str] = []

    if msg.is_multipart():
        for part in msg.walk():
            if part.is_multipart():
                continue
            disposition = (part.get("Content-Disposition") or "").lower()
            filename = part.get_filename()
            if "attachment" in disposition or filename:
                if filename:
                    attachments.append(filename)
                continue
            ctype = part.get_content_type()
            payload = part.get_payload(decode=True)
            if payload is None:
                continue
            charset = part.get_content_charset() or "utf-8"
            try:
                text = payload.decode(charset, errors="replace")
            except LookupError:
                text = payload.decode("utf-8", errors="replace")
            if ctype == "text/plain":
                text_parts.append(text)
            elif ctype == "text/html":
                html_parts.append(text)
    else:
        ctype = msg.get_content_type()
        payload = msg.get_payload(decode=True)
        if payload is not None:
            charset = msg.get_content_charset() or "utf-8"
            try:
                text = payload.decode(charset, errors="replace")
            except LookupError:
                text = payload.decode("utf-8", errors="replace")
            if ctype == "text/plain":
                text_parts.append(text)
            elif ctype == "text/html":
                html_parts.append(text)

    if text_parts:
        return "\n".join(p.strip() for p in text_parts), attachments
    if html_parts:
        return _strip_html("\n".join(html_parts)), attachments
    return "", attachments


def _record_from_message(msg: Message) -> dict:
    body, attachments = _extract_body(msg)

    def _h(key: str) -> str:
        val = msg.get(key, "")
        return str(val) if val else ""

    return {
        "message_id": _h("Message-ID"),
        "thread_id": _h("X-GM-THRID"),
        "date": _h("Date"),
        "from": _h("From"),
        "to": _h("To"),
        "subject": _h("Subject"),
        "labels": _h("X-Gmail-Labels"),
        "body": body,
        "attachments": attachments,
    }


def parse(mbox_path: Path, output_path: Path) -> int:
    if not mbox_path.exists():
        print(f"error: mbox not found: {mbox_path}", file=sys.stderr)
        return 2
    mbox = mailbox.mbox(str(mbox_path))
    skipped = 0
    output_path.parent.mkdir(parents=True, exist_ok=True)
    with output_path.open("w", encoding="utf-8") as f:
        for msg in mbox:
            try:
                record = _record_from_message(msg)
            except Exception as exc:  # noqa: BLE001
                msg_id = msg.get("Message-ID", "<unknown>")
                print(f"warning: skipping {msg_id}: {exc}", file=sys.stderr)
                skipped += 1
                continue
            f.write(json.dumps(record, ensure_ascii=False) + "\n")
    print(f"parsed {len(mbox) - skipped} messages, skipped {skipped}", file=sys.stderr)
    return 0


def main() -> int:
    if len(sys.argv) != 3:
        print("usage: parse_mbox.py <input.mbox> <output.jsonl>", file=sys.stderr)
        return 1
    return parse(Path(sys.argv[1]), Path(sys.argv[2]))


if __name__ == "__main__":
    sys.exit(main())
