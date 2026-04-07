"""Tests for parse_mbox.py."""

import json
import subprocess
import sys
from pathlib import Path

PARSE_SCRIPT = Path(__file__).parent.parent / "parse_mbox.py"


def run_parser(mbox_path: Path, output_path: Path) -> subprocess.CompletedProcess:
    """Invoke parse_mbox.py as a subprocess."""
    return subprocess.run(
        [sys.executable, str(PARSE_SCRIPT), str(mbox_path), str(output_path)],
        capture_output=True,
        text=True,
        check=False,
    )


def test_parses_all_five_fixture_messages(synthetic_mbox_path, tmp_path):
    output = tmp_path / "emails.jsonl"
    result = run_parser(synthetic_mbox_path, output)
    assert result.returncode == 0, f"parser failed: {result.stderr}"
    lines = output.read_text().strip().split("\n")
    assert len(lines) == 5
    records = [json.loads(line) for line in lines]
    subjects = [r["subject"] for r in records]
    assert "problema con el ladder" in subjects
    assert "clan ban appeal" in subjects


def test_thread_id_preserved(synthetic_mbox_path, tmp_path):
    output = tmp_path / "emails.jsonl"
    run_parser(synthetic_mbox_path, output)
    records = [json.loads(line) for line in output.read_text().strip().split("\n")]
    juan_thread = [r for r in records if r["subject"].endswith("problema con el ladder")]
    assert len(juan_thread) == 2
    assert juan_thread[0]["thread_id"] == juan_thread[1]["thread_id"]
    assert juan_thread[0]["thread_id"] != ""


def test_html_message_stripped_to_text(synthetic_mbox_path, tmp_path):
    output = tmp_path / "emails.jsonl"
    run_parser(synthetic_mbox_path, output)
    records = [json.loads(line) for line in output.read_text().strip().split("\n")]
    otro = next(r for r in records if r["subject"] == "otro problema")
    body = otro["body"]
    assert "<html>" not in body
    assert "<p>" not in body
    assert "[CHi]Predator" in body
    assert "torneo" in body


def test_attachment_filenames_logged_not_inlined(synthetic_mbox_path, tmp_path):
    output = tmp_path / "emails.jsonl"
    run_parser(synthetic_mbox_path, output)
    records = [json.loads(line) for line in output.read_text().strip().split("\n")]
    att_msg = next(r for r in records if r["subject"] == "attachment test")
    assert att_msg["attachments"] == ["screenshot.png"]
    # The base64 binary blob must NOT be in the body
    assert "iVBORw0KGgo" not in att_msg["body"]
    assert "ignore the binary blob" in att_msg["body"]


def test_missing_mbox_returns_nonzero(tmp_path):
    output = tmp_path / "emails.jsonl"
    result = run_parser(tmp_path / "does-not-exist.mbox", output)
    assert result.returncode != 0
    assert "not found" in result.stderr.lower()
