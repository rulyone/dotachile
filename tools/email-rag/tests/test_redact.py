"""Tests for redact.py and redaction_rules.py."""

import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent.parent))
import redaction_rules  # noqa: E402


def test_phone_regex_matches_chilean_formats():
    pattern = redaction_rules.PHONE_PATTERN
    assert pattern.search("+56 9 1234 5678")
    assert pattern.search("(02) 234-5678")
    assert pattern.search("912345678")


def test_phone_regex_does_not_match_short_numbers():
    pattern = redaction_rules.PHONE_PATTERN
    # Bug numbers, prices, version numbers — must not be flagged
    assert not pattern.search("v1.2.3")
    assert not pattern.search("año 2016")


def test_ipv4_regex():
    pattern = redaction_rules.IPV4_PATTERN
    assert pattern.search("192.168.1.42")
    assert pattern.search("8.8.8.8")
    assert not pattern.search("999.999.999.999")


def test_ipv6_regex():
    pattern = redaction_rules.IPV6_PATTERN
    assert pattern.search("2001:db8::1")
    assert pattern.search("fe80::1ff:fe23:4567:890a")


def test_credit_card_regex_with_luhn():
    matches = redaction_rules.find_credit_cards("pago con 4111 1111 1111 1111 hoy")
    assert "4111 1111 1111 1111" in matches
    # Random 16 digits that fail Luhn must NOT be flagged
    assert redaction_rules.find_credit_cards("1234 5678 9012 3456") == []


def test_password_line_regex_spanish_and_english():
    pattern = redaction_rules.PASSWORD_LINE_PATTERN
    assert pattern.search("mi password es: hunter2")
    assert pattern.search("contraseña: secreto123")
    assert pattern.search("clave = abc")
    assert not pattern.search("la palabra clave del torneo es importante")
