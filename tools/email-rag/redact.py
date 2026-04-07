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
    # Sort by start descending so earlier indices are not invalidated by later replacements.
    results.sort(key=lambda r: r.start, reverse=True)
    out = text
    for r in results:
        marker = f"[REDACTED_{r.entity_type}]"
        out = out[: r.start] + marker + out[r.end :]
    return out
