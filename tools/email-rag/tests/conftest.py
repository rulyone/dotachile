"""Shared pytest fixtures for email-rag tests."""

from pathlib import Path

import pytest

FIXTURES_DIR = Path(__file__).parent / "fixtures"


@pytest.fixture
def synthetic_mbox_path() -> Path:
    """Path to the hand-crafted mbox fixture with planted PII."""
    return FIXTURES_DIR / "synthetic.mbox"


@pytest.fixture
def planted_pii() -> dict:
    """All planted PII strings, grouped by category.

    These MUST NOT appear in any redacted corpus output.
    Used by test_redact.py to assert full scrubbing.
    """
    return {
        "names": ["Juan Pérez", "Mary O'Brien"],
        "emails": [
            "juan.perez.fake@example.com",
            "soporte.fake@dotachile.test",
            "mary.obrien.fake@example.org",
            "anon.fake@example.net",
        ],
        "phones": [
            "+56 9 1234 5678",
            "(02) 234-5678",
        ],
        "ips": ["192.168.1.42", "2001:db8::1"],
        "credit_cards": ["4111 1111 1111 1111"],
        "passwords": ["hunter2"],
    }


@pytest.fixture
def preserved_strings() -> list:
    """Strings that MUST appear unchanged in the redacted corpus.

    Dotachile in-game usernames are public pseudonyms, not PII.
    """
    return ["[CHi]Predator", "[LOL]"]
