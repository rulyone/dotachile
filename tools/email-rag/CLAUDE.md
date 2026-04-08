# email-rag

Local RAG toolkit that turns a Gmail Takeout `.mbox` for `dotachile.com@gmail.com`
into a redacted, hybrid-searchable corpus Claude Code can grep/read.

**Independent of the surrounding Java app** — different stack, different rules.
The repo root `/CLAUDE.md` (Java/JSF/PrimeFaces) does **not** apply here.

## Stack

Python 3.11+, Presidio (PII NER, en+es), spaCy, rank-bm25,
sentence-transformers (`paraphrase-multilingual-mpnet-base-v2`), pytest.
No network at runtime after first model download. No database.

## Pipeline

```
Gmail Takeout (.mbox)
  → parse_mbox.py     → emails.jsonl                (extract, no redaction)
  → redact.py         → corpus/*.md + corpus_chunks.jsonl + vault/mapping.json
  → search.py         → ranked thread snippets (BM25 ∪ semantic)
```

Stage 2 is two-pass: regex (`redaction_rules.py`) then Presidio NER run once
per language; offsets processed back-to-front, longest span wins on overlap.
Senders are pseudonymized to stable `USER_NNNN` via HMAC-SHA256; the key +
mapping live in `vault/mapping.json` and **must be outside `corpus_dir`** —
the redactor refuses to run otherwise.

## Layout

```
tools/email-rag/
  parse_mbox.py        Stage 1: mbox → JSONL (Gmail X-GM-THRID threading,
                       multipart MIME, custom HTML→text, drops attachments)
  redaction_rules.py   Declarative regex + Presidio config (phones CL, IPs,
                       Luhn-checked CCs, password lines, URL credentials)
  redact.py            Stage 2: regex pass A + Presidio pass B + pseudonymizer
  search.py            Stage 3: BM25 + cached semantic embeddings, merged
  tests/               pytest; conftest exposes synthetic.mbox + planted PII
  README.md            Full setup, security model, query examples (read this!)
```

## Setup, build, test

```
cd tools/email-rag
python3 -m venv .venv && source .venv/bin/activate
pip install -e ".[dev]"
python -m spacy download en_core_web_sm
python -m spacy download es_core_news_sm   # both required for bilingual pass

python parse_mbox.py <input.mbox> emails.jsonl
python redact.py emails.jsonl <corpus_dir> --mapping <vault>/mapping.json
python search.py "query"                   # or --bm25-only, or --corpus PATH
pytest                                     # all synthetic, no real emails
```

## Gotchas

- **spaCy models are not pip deps** — download `en_core_web_sm` and
  `es_core_news_sm` explicitly or Presidio fails with a cryptic "no model".
- **First semantic search downloads ~420 MB** from Hugging Face and writes
  `<corpus>/embeddings.pkl`. Delete that file if you hand-edit the corpus.
- **Bilingual redaction**: Pass B runs once per language and merges spans;
  don't "optimize" it back to a single language.
- **Mapping safety**: `vault/mapping.json` is the de-anonymization key. Keep
  it outside `corpus_dir`. The redactor enforces this; do not bypass.
- **Tests use subprocess** to invoke `parse_mbox.py` / `redact.py` — they
  cover CLI contracts, not just importable functions.
- **Spanish-language rule** from the root CLAUDE.md does not apply here:
  this tool's identifiers are English; user-facing strings are bilingual.

## PII handling — non-negotiable

The whole point of this tool is to handle real PII responsibly. When
debugging, fixing, or testing the redactor against the real corpus:

- **Never inline literal PII into commits, commit messages, PR titles,
  PR bodies, or terminal output the user can scrollback to.** That
  includes phone numbers, real names, addresses, email addresses,
  credentials, even single tokens. Once it lives in git history or a
  PR body, it is effectively published — GitHub preserves PR edit
  history and orphaned commits stay reachable by SHA after deletion.
- **Use synthetic stand-ins** in tests and examples: pick values that
  match the same shape but are clearly fabricated (e.g. `01-2345678`
  for a Chilean phone, `juan.perez.fake@example.com` for an email).
  Mark them as synthetic in a nearby comment so future readers don't
  mistake them for real data.
- **When verification surfaces a real leak**, describe it abstractly
  to the user — "a 9-digit phone in legacy `0X-XXXXXXX` form at
  `<file>:<line>`" — and never paste the digits anywhere written.
  Fix the regex/recognizer using a synthetic shape, rebuild the
  corpus, and re-grep.
- The README's "do not proceed" rule after grep verification exists
  precisely to prevent leaked PII from propagating into search
  indexes, embeddings caches, or downstream artifacts.
