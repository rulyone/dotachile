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


SEMANTIC_MODEL_NAME = "sentence-transformers/paraphrase-multilingual-mpnet-base-v2"
EMBEDDINGS_CACHE_NAME = "embeddings.pkl"


def _embeddings_cache_path(corpus_dir: Path) -> Path:
    return corpus_dir / EMBEDDINGS_CACHE_NAME


def _build_or_load_embeddings(corpus_dir: Path):
    """Return (model, np.ndarray of shape (n_chunks, dim), chunks list).

    Cache is keyed only on chunk count + body hashes — if the corpus is
    rebuilt, the cache is invalidated and rebuilt automatically.
    """
    import hashlib
    import pickle

    import numpy as np
    from sentence_transformers import SentenceTransformer

    chunks = _load_chunks(corpus_dir)
    if not chunks:
        return None, None, []

    cache_path = _embeddings_cache_path(corpus_dir)
    fingerprint = hashlib.sha256(
        "\n".join(c["body"] for c in chunks).encode("utf-8")
    ).hexdigest()

    if cache_path.exists():
        with cache_path.open("rb") as f:
            cached = pickle.load(f)
        if cached.get("fingerprint") == fingerprint:
            model = SentenceTransformer(SEMANTIC_MODEL_NAME)
            return model, cached["embeddings"], chunks

    print(
        f"building semantic embeddings for {len(chunks)} chunks (one-time, ~30s)...",
        file=sys.stderr,
    )
    model = SentenceTransformer(SEMANTIC_MODEL_NAME)
    embeddings = model.encode(
        [c["body"] for c in chunks], convert_to_numpy=True, show_progress_bar=False
    )
    with cache_path.open("wb") as f:
        pickle.dump({"fingerprint": fingerprint, "embeddings": embeddings}, f)
    return model, embeddings, chunks


def semantic_search(corpus_dir: Path, query: str, top_k: int = 10) -> list[SearchResult]:
    import numpy as np

    model, embeddings, chunks = _build_or_load_embeddings(corpus_dir)
    if model is None or embeddings is None:
        return []
    query_vec = model.encode([query], convert_to_numpy=True)[0]
    # Cosine similarity (model outputs are normalised by default for mpnet variants).
    norm_emb = embeddings / (np.linalg.norm(embeddings, axis=1, keepdims=True) + 1e-12)
    norm_q = query_vec / (np.linalg.norm(query_vec) + 1e-12)
    scores = norm_emb @ norm_q
    order = np.argsort(-scores)
    results: list[SearchResult] = []
    seen: set[str] = set()
    for idx in order:
        score = float(scores[idx])
        chunk = chunks[idx]
        thread_file = chunk["thread_file"]
        if thread_file in seen:
            continue
        seen.add(thread_file)
        results.append(
            SearchResult(
                thread_file=thread_file,
                bm25_score=None,
                semantic_score=score,
                snippet=_snippet(chunk["body"], query),
            )
        )
        if len(results) >= top_k:
            break
    return results


def merge_results(
    bm25_results: list[SearchResult], semantic_results: list[SearchResult]
) -> list[SearchResult]:
    """Union the two ranked lists by thread_file, keeping both scores."""
    by_file: dict[str, SearchResult] = {}
    for r in bm25_results:
        by_file[r.thread_file] = SearchResult(
            thread_file=r.thread_file,
            bm25_score=r.bm25_score,
            semantic_score=None,
            snippet=r.snippet,
        )
    for r in semantic_results:
        existing = by_file.get(r.thread_file)
        if existing is None:
            by_file[r.thread_file] = SearchResult(
                thread_file=r.thread_file,
                bm25_score=None,
                semantic_score=r.semantic_score,
                snippet=r.snippet,
            )
        else:
            existing.semantic_score = r.semantic_score
    # Order: any with both scores first, then by best individual score.
    def sort_key(r: SearchResult) -> tuple[int, float]:
        bm = r.bm25_score or 0.0
        sem = r.semantic_score or 0.0
        return (-(int(r.bm25_score is not None) + int(r.semantic_score is not None)), -(bm + sem))

    return sorted(by_file.values(), key=sort_key)


def format_result_line(r: SearchResult) -> str:
    bm = f"{r.bm25_score:.2f}" if r.bm25_score is not None else " - "
    sem = f"{r.semantic_score:.2f}" if r.semantic_score is not None else " - "
    return f"{bm}\t{sem}\t{r.thread_file} :: {r.snippet}"


def hybrid_search(corpus_dir: Path, query: str, top_k: int = 10) -> list[SearchResult]:
    bm = bm25_search(corpus_dir, query, top_k=top_k)
    sem = semantic_search(corpus_dir, query, top_k=top_k)
    return merge_results(bm, sem)


def main() -> int:
    parser = argparse.ArgumentParser(description="Hybrid search over the redacted email corpus.")
    parser.add_argument("query", help="natural-language query")
    parser.add_argument(
        "--corpus",
        type=Path,
        default=Path.home() / "Documents" / "dojo" / "dotachile-emails" / "corpus",
        help="path to corpus directory (default: ~/Documents/dojo/dotachile-emails/corpus)",
    )
    parser.add_argument("--top-k", type=int, default=10)
    parser.add_argument(
        "--bm25-only",
        action="store_true",
        help="skip the semantic leg (faster, no model load)",
    )
    args = parser.parse_args()

    if not args.corpus.exists():
        print(f"error: corpus dir not found: {args.corpus}", file=sys.stderr)
        return 2

    if args.bm25_only:
        results = bm25_search(args.corpus, args.query, top_k=args.top_k)
    else:
        results = hybrid_search(args.corpus, args.query, top_k=args.top_k)

    if not results:
        print("no documents indexed", file=sys.stderr)
        return 0

    for r in results:
        print(format_result_line(r))
    return 0


if __name__ == "__main__":
    sys.exit(main())
