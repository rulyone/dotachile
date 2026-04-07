"""Tests for search.py."""

import json
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent.parent))


def _seed_corpus(tmp_path: Path) -> Path:
    corpus = tmp_path / "corpus"
    corpus.mkdir()
    (corpus / "2016-03-thread-aaa.md").write_text(
        "---\ndate: 2016-03-14\nparticipants: [USER_0001]\n---\n\n"
        "## USER_0001 — 2016-03-14\n\nEl ladder ELO no me suma puntos despues del partido.\n"
    )
    (corpus / "2016-04-thread-bbb.md").write_text(
        "---\ndate: 2016-04-06\nparticipants: [USER_0002]\n---\n\n"
        "## USER_0002 — 2016-04-06\n\nMi clan fue baneado del torneo sin razon.\n"
    )
    (corpus / "2016-05-thread-ccc.md").write_text(
        "---\ndate: 2016-05-01\nparticipants: [USER_0003]\n---\n\n"
        "## USER_0003 — 2016-05-01\n\nNo puedo entrar al sitio web, error 500.\n"
    )
    chunks_path = corpus / "corpus_chunks.jsonl"
    chunks_path.write_text(
        "\n".join(
            [
                json.dumps({"thread_file": "2016-03-thread-aaa.md", "message_index": 0, "body": "El ladder ELO no me suma puntos despues del partido."}),
                json.dumps({"thread_file": "2016-04-thread-bbb.md", "message_index": 0, "body": "Mi clan fue baneado del torneo sin razon."}),
                json.dumps({"thread_file": "2016-05-thread-ccc.md", "message_index": 0, "body": "No puedo entrar al sitio web, error 500."}),
            ]
        )
        + "\n"
    )
    return corpus


def test_bm25_exact_phrase_match_ranks_first(tmp_path):
    from search import bm25_search

    corpus = _seed_corpus(tmp_path)
    results = bm25_search(corpus, "ladder ELO puntos", top_k=3)
    assert len(results) >= 1
    assert results[0].thread_file == "2016-03-thread-aaa.md"


def test_bm25_unrelated_query_returns_low_or_no_results(tmp_path):
    from search import bm25_search

    corpus = _seed_corpus(tmp_path)
    results = bm25_search(corpus, "xyz_inexistente_palabra", top_k=3)
    if results:
        assert results[0].bm25_score < 1.0


def test_bm25_empty_corpus_returns_empty(tmp_path):
    from search import bm25_search

    empty = tmp_path / "empty"
    empty.mkdir()
    (empty / "corpus_chunks.jsonl").write_text("")
    results = bm25_search(empty, "anything", top_k=3)
    assert results == []


def test_merge_unions_and_dedupes(tmp_path):
    from search import SearchResult, merge_results

    bm = [
        SearchResult("a.md", bm25_score=2.0, semantic_score=None, snippet="alpha"),
        SearchResult("b.md", bm25_score=1.5, semantic_score=None, snippet="beta"),
    ]
    sem = [
        SearchResult("b.md", bm25_score=None, semantic_score=0.9, snippet="beta"),
        SearchResult("c.md", bm25_score=None, semantic_score=0.7, snippet="gamma"),
    ]
    merged = merge_results(bm, sem)
    files = [r.thread_file for r in merged]
    assert set(files) == {"a.md", "b.md", "c.md"}
    b = next(r for r in merged if r.thread_file == "b.md")
    assert b.bm25_score == 1.5
    assert b.semantic_score == 0.9


def test_merge_preserves_both_scores_per_file(tmp_path):
    from search import SearchResult, merge_results

    bm = [SearchResult("x.md", bm25_score=3.3, semantic_score=None, snippet="x")]
    sem = [SearchResult("x.md", bm25_score=None, semantic_score=0.42, snippet="x")]
    merged = merge_results(bm, sem)
    assert len(merged) == 1
    assert merged[0].bm25_score == 3.3
    assert merged[0].semantic_score == 0.42


def test_format_result_line_handles_missing_scores():
    from search import SearchResult, format_result_line

    only_bm25 = SearchResult("a.md", bm25_score=2.5, semantic_score=None, snippet="hello")
    only_sem = SearchResult("b.md", bm25_score=None, semantic_score=0.81, snippet="hola")
    line1 = format_result_line(only_bm25)
    line2 = format_result_line(only_sem)
    assert "2.50" in line1 and " - " in line1 and "a.md" in line1
    assert "0.81" in line2 and " - " in line2 and "b.md" in line2
