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
