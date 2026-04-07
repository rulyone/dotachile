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


def test_pass_a_redacts_phones_ips_cards_passwords():
    from redact import _apply_pass_a

    text = (
        "Hola, mi telefono es +56 9 1234 5678 y mi IP es 192.168.1.42.\n"
        "Mi password es: hunter2\n"
        "Pago con 4111 1111 1111 1111\n"
        "IPv6: 2001:db8::1"
    )
    redacted = _apply_pass_a(text)
    assert "+56 9 1234 5678" not in redacted
    assert "192.168.1.42" not in redacted
    assert "hunter2" not in redacted
    assert "4111 1111 1111 1111" not in redacted
    assert "2001:db8::1" not in redacted
    assert "[REDACTED" in redacted  # something replaced


def test_pass_b_redacts_person_names_and_emails():
    from redact import _build_presidio_engine, _apply_pass_b

    engine = _build_presidio_engine()
    text = "Soy Juan Pérez y mi correo es juan.perez.fake@example.com"
    redacted = _apply_pass_b(text, engine, language="es")
    assert "Juan Pérez" not in redacted
    assert "juan.perez.fake@example.com" not in redacted
    assert "[REDACTED" in redacted


def test_pass_b_preserves_dotachile_username():
    from redact import _build_presidio_engine, _apply_pass_b

    engine = _build_presidio_engine()
    text = "Mi usuario es [CHi]Predator y juego ladder"
    redacted = _apply_pass_b(text, engine, language="es")
    assert "[CHi]Predator" in redacted
