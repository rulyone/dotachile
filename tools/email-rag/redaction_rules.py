"""Declarative redaction patterns and Presidio configuration.

This module is data, not logic. Imported by redact.py and by tests.
"""

from __future__ import annotations

import re

# Phone numbers: international (+CC ...), Chilean parenthetical, or 9-digit local.
# Requires at least 8 consecutive digits when whitespace/dashes/parens are stripped,
# to avoid matching version numbers, years, prices, etc.
PHONE_PATTERN = re.compile(
    r"""
    (?:
        \+\d{1,3}[\s-]?\d[\s-]?\d{4}[\s-]?\d{4}      # +56 9 1234 5678
      | \(\d{2,3}\)[\s-]?\d{3,4}[\s-]?\d{3,4}        # (02) 234-5678
      | (?<!\d)0\d[\s-]\d{7}(?!\d)                   # 09-2991185 (legacy Chilean dash)
      | (?<!\d)\d{9}(?!\d)                           # 912345678 (Chilean mobile)
    )
    """,
    re.VERBOSE,
)

IPV4_PATTERN = re.compile(
    r"\b(?:(?:25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)\.){3}(?:25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)\b"
)

# Simplified IPv6 — matches typical compressed and full forms.
IPV6_PATTERN = re.compile(
    r"\b(?:[0-9a-fA-F]{1,4}:){2,7}[0-9a-fA-F]{1,4}\b|\b(?:[0-9a-fA-F]{1,4}:){1,7}:\b|\b::(?:[0-9a-fA-F]{1,4}:){0,6}[0-9a-fA-F]{1,4}\b"
)

# Credit card candidates: 13-19 digits with optional spaces/dashes between groups.
_CC_CANDIDATE = re.compile(r"\b(?:\d[ -]?){13,19}\b")

PASSWORD_LINE_PATTERN = re.compile(
    r"(?im)^.*\b(?:password|contrase[nñ]a|clave)\b.*[:=]\s*\S+.*$"
)

# URLs whose query string contains a parameter that looks like a credential.
# Matches the ENTIRE URL so the whole thing gets scrubbed, not just the token.
URL_TOKEN_PATTERN = re.compile(
    r"https?://\S*?[?&](?:token|key|auth|sig|secret|apikey|api_key|access_token)=\S+",
    re.IGNORECASE,
)


def _luhn_valid(digits: str) -> bool:
    total = 0
    for i, ch in enumerate(reversed(digits)):
        n = int(ch)
        if i % 2 == 1:
            n *= 2
            if n > 9:
                n -= 9
        total += n
    return total % 10 == 0


def find_credit_cards(text: str) -> list[str]:
    """Return Luhn-valid credit-card-shaped strings found in text."""
    hits: list[str] = []
    for match in _CC_CANDIDATE.finditer(text):
        raw = match.group().strip()
        digits_only = re.sub(r"[ -]", "", raw)
        if 13 <= len(digits_only) <= 19 and _luhn_valid(digits_only):
            hits.append(raw)
    return hits


# Presidio configuration is described declaratively here so redact.py can
# instantiate the analyzer without making policy decisions of its own.
PRESIDIO_LANGUAGES = ("en", "es")
PRESIDIO_ENTITIES = ("PERSON", "EMAIL_ADDRESS", "LOCATION")
