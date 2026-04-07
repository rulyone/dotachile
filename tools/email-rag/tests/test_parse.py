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
