#!/usr/bin/env python3
"""Stage 3: hybrid (BM25 + semantic) retrieval over the redacted corpus.

Both retrieval legs run on every query and their candidates are merged.
The semantic leg is built lazily on first use (model + embeddings cached
to a local pickle).
"""

from __future__ import annotations

import argparse
import json
import re
import sys
from dataclasses import dataclass
from pathlib import Path

from rank_bm25 import BM25Okapi


@dataclass
class SearchResult:
    thread_file: str
    bm25_score: float | None
    semantic_score: float | None
    snippet: str


_TOKEN = re.compile(r"\w+", re.UNICODE)


def _tokenise(text: str) -> list[str]:
    return [t.lower() for t in _TOKEN.findall(text)]


def _load_chunks(corpus_dir: Path) -> list[dict]:
    chunks_path = corpus_dir / "corpus_chunks.jsonl"
    if not chunks_path.exists():
        return []
    chunks: list[dict] = []
    for line in chunks_path.read_text().splitlines():
        line = line.strip()
        if line:
            chunks.append(json.loads(line))
    return chunks


def _snippet(body: str, query: str, window: int = 80) -> str:
    tokens = _tokenise(query)
    lower = body.lower()
    for tok in tokens:
        idx = lower.find(tok)
        if idx >= 0:
            start = max(0, idx - window // 2)
            end = min(len(body), idx + window // 2)
            return body[start:end].replace("\n", " ")
    return body[:window].replace("\n", " ")


def bm25_search(corpus_dir: Path, query: str, top_k: int = 10) -> list[SearchResult]:
    chunks = _load_chunks(corpus_dir)
    if not chunks:
        return []
    tokenised = [_tokenise(c["body"]) for c in chunks]
    bm25 = BM25Okapi(tokenised)
    scores = bm25.get_scores(_tokenise(query))
    ranked = sorted(zip(chunks, scores), key=lambda pair: pair[1], reverse=True)
    results: list[SearchResult] = []
    seen: set[str] = set()
    for chunk, score in ranked:
        if score <= 0:
            continue
        thread_file = chunk["thread_file"]
        if thread_file in seen:
            continue
        seen.add(thread_file)
        results.append(
            SearchResult(
                thread_file=thread_file,
                bm25_score=float(score),
                semantic_score=None,
                snippet=_snippet(chunk["body"], query),
            )
        )
        if len(results) >= top_k:
            break
    return results
