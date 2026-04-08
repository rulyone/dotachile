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
    # Legacy 0X-XXXXXXX format common in pre-2010 Chilean signatures.
    # Synthetic test values only — not real numbers.
    assert pattern.search("01-2345678")
    assert pattern.search("01 2345678")
    # Anchored: must not be a slice of a longer digit run.
    assert not pattern.search("1201-23456780")


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


def test_pseudonymize_is_stable_for_same_email(tmp_path):
    from redact import Pseudonymizer

    mapping_path = tmp_path / "mapping.json"
    p = Pseudonymizer(mapping_path)
    token1 = p.token_for("juan.perez.fake@example.com")
    token2 = p.token_for("juan.perez.fake@example.com")
    assert token1 == token2
    assert token1.startswith("USER_")


def test_pseudonymize_distinct_for_different_emails(tmp_path):
    from redact import Pseudonymizer

    p = Pseudonymizer(tmp_path / "mapping.json")
    a = p.token_for("juan.perez.fake@example.com")
    b = p.token_for("mary.obrien.fake@example.org")
    assert a != b


def test_pseudonymize_persists_across_instances(tmp_path):
    from redact import Pseudonymizer

    mapping_path = tmp_path / "mapping.json"
    p1 = Pseudonymizer(mapping_path)
    token = p1.token_for("juan.perez.fake@example.com")
    p1.save()

    p2 = Pseudonymizer(mapping_path)
    assert p2.token_for("juan.perez.fake@example.com") == token


def test_pseudonymize_normalises_address_form(tmp_path):
    from redact import Pseudonymizer

    p = Pseudonymizer(tmp_path / "mapping.json")
    a = p.token_for("Juan Pérez <juan.perez.fake@example.com>")
    b = p.token_for("juan.perez.fake@example.com")
    c = p.token_for("JUAN.PEREZ.FAKE@EXAMPLE.COM")
    assert a == b == c


def test_full_redaction_scrubs_all_planted_pii(
    synthetic_mbox_path, planted_pii, preserved_strings, tmp_path
):
    """Run parse_mbox.py + redact.py end-to-end on the fixture."""
    import subprocess
    import sys

    parse_script = Path(__file__).parent.parent / "parse_mbox.py"
    redact_script = Path(__file__).parent.parent / "redact.py"

    jsonl = tmp_path / "emails.jsonl"
    corpus = tmp_path / "corpus"
    mapping = tmp_path / "vault" / "mapping.json"

    subprocess.run(
        [sys.executable, str(parse_script), str(synthetic_mbox_path), str(jsonl)],
        check=True,
    )
    result = subprocess.run(
        [
            sys.executable,
            str(redact_script),
            str(jsonl),
            str(corpus),
            "--mapping",
            str(mapping),
        ],
        capture_output=True,
        text=True,
        check=False,
    )
    assert result.returncode == 0, f"redact failed: {result.stderr}"

    # All planted PII must be absent from every markdown file.
    md_blob = "\n".join(p.read_text() for p in corpus.glob("*.md"))
    for category, items in planted_pii.items():
        for item in items:
            assert item not in md_blob, f"{category}: {item!r} leaked into corpus"
            assert item.lower() not in md_blob.lower(), (
                f"{category}: {item!r} leaked (case-insensitive)"
            )

    # Preserved strings (dotachile usernames) must still be present.
    for preserved in preserved_strings:
        assert preserved in md_blob, f"preserved string {preserved!r} was scrubbed"

    # Mapping file must exist and be outside the corpus dir.
    assert mapping.exists()
    assert not str(mapping).startswith(str(corpus))


def test_chunks_jsonl_is_one_per_message(synthetic_mbox_path, tmp_path):
    import json as _json
    import subprocess
    import sys

    parse_script = Path(__file__).parent.parent / "parse_mbox.py"
    redact_script = Path(__file__).parent.parent / "redact.py"

    jsonl = tmp_path / "emails.jsonl"
    corpus = tmp_path / "corpus"
    mapping = tmp_path / "vault" / "mapping.json"

    subprocess.run(
        [sys.executable, str(parse_script), str(synthetic_mbox_path), str(jsonl)],
        check=True,
    )
    subprocess.run(
        [
            sys.executable,
            str(redact_script),
            str(jsonl),
            str(corpus),
            "--mapping",
            str(mapping),
        ],
        check=True,
    )

    chunks_path = corpus / "corpus_chunks.jsonl"
    assert chunks_path.exists()
    chunks = [_json.loads(line) for line in chunks_path.read_text().strip().split("\n")]
    # Five messages in the fixture.
    assert len(chunks) == 5
    for chunk in chunks:
        assert "thread_file" in chunk
        assert "message_index" in chunk
        assert "body" in chunk
        # The thread file referenced must actually exist
        assert (corpus / chunk["thread_file"]).exists()


def test_pass_b_multilingual_catches_english_and_spanish_names():
    from redact import _build_presidio_engine, _apply_pass_b_multilingual

    engine = _build_presidio_engine()
    text = (
        "Hola, soy Juan Pérez y trabajo con Mary O'Brien. "
        "Contact us: juan.perez.fake@example.com"
    )
    redacted = _apply_pass_b_multilingual(text, engine)
    assert "Juan Pérez" not in redacted
    assert "Mary O'Brien" not in redacted
    assert "juan.perez.fake@example.com" not in redacted


def test_signature_block_rfc3676_delimiter_scrubs_everything_after():
    from redact import _redact_signature_block

    body = (
        "Hola equipo, mensaje real aquí.\n"
        "Otra línea del mensaje.\n"
        "\n"
        "-- \n"
        "JUAN PEREZ\n"
        "01-2345678\n"
        "Director de Algo"
    )
    out = _redact_signature_block(body)
    assert "JUAN PEREZ" not in out
    assert "01-2345678" not in out
    assert "Director de Algo" not in out
    assert "Hola equipo, mensaje real aquí." in out
    assert "[REDACTED_SIGNATURE_BLOCK]" in out


def test_signature_block_dash_dash_variant_no_trailing_space():
    from redact import _redact_signature_block

    body = "cuerpo del mensaje\n--\nNAME LASTNAME\n01 2345678"
    out = _redact_signature_block(body)
    assert "NAME LASTNAME" not in out
    assert "01 2345678" not in out
    assert "cuerpo del mensaje" in out
    assert "[REDACTED_SIGNATURE_BLOCK]" in out


def test_signature_block_closing_word_keeps_closing_scrubs_tail():
    from redact import _redact_signature_block

    body = (
        "Comentario sobre el ladder.\n"
        "\n"
        "Saludos,\n"
        "FAKE NAME\n"
        "01-2345678\n"
        "fake.title@example.com"
    )
    out = _redact_signature_block(body)
    assert "Saludos," in out  # closing kept
    assert "FAKE NAME" not in out
    assert "01-2345678" not in out
    assert "fake.title@example.com" not in out
    assert "[REDACTED_SIGNATURE_BLOCK]" in out


def test_signature_block_does_not_match_closing_word_in_body():
    from redact import _redact_signature_block

    body = (
        "Saludos a todos los participantes del torneo de este fin de semana.\n"
        "Recuerden que la inscripción cierra el viernes."
    )
    out = _redact_signature_block(body)
    assert out == body  # nothing scrubbed
    assert "[REDACTED_SIGNATURE_BLOCK]" not in out


def test_signature_block_closing_word_at_end_with_no_tail_is_noop():
    from redact import _redact_signature_block

    body = "mensaje\nThanks"
    out = _redact_signature_block(body)
    assert "[REDACTED_SIGNATURE_BLOCK]" not in out
    assert "Thanks" in out


def test_signature_block_handles_multiple_closing_words():
    from redact import _redact_signature_block

    body = (
        "primera parte\n"
        "Regards,\n"
        "PERSON ONE\n"
        "01-1111111\n"
        "\n"
        "segunda parte\n"
        "Atentamente\n"
        "PERSON TWO\n"
        "01-2222222"
    )
    out = _redact_signature_block(body)
    assert "PERSON ONE" not in out
    assert "01-1111111" not in out
    assert "PERSON TWO" not in out
    assert "01-2222222" not in out
    assert "primera parte" in out
    assert "segunda parte" in out


def test_pipeline_catches_allcaps_name_in_signature_block():
    """End-to-end check: an ALLCAPS name in a sig block (the failure mode that
    motivated this feature) gets redacted before Presidio sees it."""
    from redact import _redact_signature_block, _apply_pass_a, _build_presidio_engine, _apply_pass_b_multilingual

    body = (
        "Confirmo el partido del sábado a las cinco.\n"
        "\n"
        "-- \n"
        "JUAN PÉREZ\n"
        "01-2345678"
    )
    engine = _build_presidio_engine()
    out = _apply_pass_b_multilingual(_apply_pass_a(_redact_signature_block(body)), engine)
    assert "JUAN PÉREZ" not in out
    assert "01-2345678" not in out
    # Body content above the sig delimiter must survive (meaningful tokens).
    assert "partido" in out
    assert "sábado" in out
    assert "[REDACTED_SIGNATURE_BLOCK]" in out


def test_url_token_pattern_redacts_credential_urls():
    pattern = redaction_rules.URL_TOKEN_PATTERN
    assert pattern.search("https://example.com/api?token=abc123xyz")
    assert pattern.search("http://foo.test/path?key=SECRETVALUE")
    assert pattern.search("https://x.y/endpoint?a=1&auth=eyJhbGciOi")
    # Plain URLs without credential-ish params must not match
    assert not pattern.search("https://example.com/ladder?page=2")
    assert not pattern.search("https://example.com/clan?name=foo")


def test_pass_a_redacts_url_with_token():
    from redact import _apply_pass_a

    text = "Check https://api.example.com/oauth?token=eyJhbGci&state=x — it fails"
    redacted = _apply_pass_a(text)
    assert "eyJhbGci" not in redacted
    assert "token=" not in redacted
    assert "[REDACTED_URL_TOKEN]" in redacted


def test_redactor_refuses_mapping_inside_corpus(synthetic_mbox_path, tmp_path):
    import subprocess
    import sys

    parse_script = Path(__file__).parent.parent / "parse_mbox.py"
    redact_script = Path(__file__).parent.parent / "redact.py"

    jsonl = tmp_path / "emails.jsonl"
    corpus = tmp_path / "corpus"
    bad_mapping = corpus / "mapping.json"  # inside corpus — must be refused

    subprocess.run(
        [sys.executable, str(parse_script), str(synthetic_mbox_path), str(jsonl)],
        check=True,
    )
    result = subprocess.run(
        [
            sys.executable,
            str(redact_script),
            str(jsonl),
            str(corpus),
            "--mapping",
            str(bad_mapping),
        ],
        capture_output=True,
        text=True,
        check=False,
    )
    assert result.returncode != 0
    assert "mapping" in result.stderr.lower()
