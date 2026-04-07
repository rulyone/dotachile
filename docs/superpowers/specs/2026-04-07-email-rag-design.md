# Email RAG for dotachile.com@gmail.com

**Date:** 2026-04-07
**Status:** Design approved, ready for implementation planning

## Goal

Build a local, privacy-preserving Retrieval-Augmented system that lets Claude
Code answer two kinds of questions about historical user complaints sent to
`dotachile.com@gmail.com`:

1. **Triage & summarize** — "What did users complain about most?", "Group
   recurring issues by theme."
2. **Historical archive search** — "What did users say about clan bans in
   2018?", "Have users complained about the ladder ELO calculation?"

## Non-goals

- Live/incremental sync with Gmail. The corpus is treated as **frozen**.
- Multi-user access, hosted services, web UIs.
- Replying to emails or any kind of write access to Gmail.
- Indexing attachments. Attachment filenames are logged; binary content is
  dropped.
- Re-using the toolkit for arbitrary mailboxes. It is built for this one
  corpus and may take liberties (e.g., Spanish-first PII rules).

## Constraints

- **Corpus is small:** under 1,000 emails, frozen, no incremental updates.
- **PII must not leak into the corpus.** The mailbox contains real user
  names, email addresses, phone numbers, and occasional pasted passwords.
  Once redacted markdown exists, it must be safe to read in any Claude Code
  session without further safeguards.
- **No credentials.** No OAuth, no IMAP, no API tokens. The export is fully
  manual via Google Takeout.
- **Local-only.** No cloud services, no embeddings APIs. Everything runs on
  the developer's Mac.
- **Spanish + English** content. The dotachile community is primarily
  Spanish-speaking; PII recognizers must handle both.

## Architecture

Three independent stages, each producing files the next stage consumes. No
long-running processes; each stage is a script you run by hand.

```
Gmail Takeout (.mbox)
        │
        ▼
  [1] parse_mbox.py        → emails.jsonl   (one email per line, raw)
        │
        ▼
  [2] redact.py            → corpus/*.md           (one file per thread, redacted)
                           → corpus_chunks.jsonl   (per-message chunks for embedding)
                           → mapping.json          (USER_NNNN → HMAC, outside corpus)
        │
        ▼
  [3] search.py "query"    → hybrid ranked results (BM25 + semantic, merged)
```

**Why three stages instead of one script:** each stage is independently
testable and re-runnable. Tuning the redactor does not require re-parsing
the mbox. Tuning search does not require re-redacting. Intermediate files
are human-inspectable, which matters for verifying that PII was actually
scrubbed before any session uses the corpus.

**Claude Code's role:** Claude does not run the parser or redactor — those
are one-shot operations the developer runs after Takeout arrives. Claude
only invokes `search.py` (via Bash) and reads files from `corpus/` (via
Read/Grep) when answering questions during a session.

## Directory layout

### Tooling (versioned in dotachile repo)

```
dotachile/tools/email-rag/
  README.md                  # Takeout steps, sparse bundle steps, pipeline usage
  parse_mbox.py              # stage 1: .mbox → emails.jsonl
  redact.py                  # stage 2: emails.jsonl → corpus/*.md + mapping.json
  search.py                  # stage 3: BM25 (+ optional --semantic) over corpus
  redaction_rules.py         # regex patterns + Presidio config (data, not logic)
  tests/
    fixtures/                # synthetic mbox samples with planted PII
    test_parse.py
    test_redact.py
    test_search.py
  pyproject.toml             # presidio-analyzer, rank-bm25, sentence-transformers
```

### Sensitive data (encrypted sparse bundle, mounted on demand)

```
~/Documents/dojo/dotachile-emails-vault.sparsebundle   ← AES-256, password-protected
   (when mounted at /Volumes/dotachile-emails-vault/)
   ├── raw/dotachile.mbox          # from Google Takeout
   ├── intermediate/emails.jsonl   # parser output
   └── mapping.json                # USER_NNNN → HMAC table + HMAC key
```

### Safe-to-read data (plain filesystem, `chmod 700` on parent)

```
~/Documents/dojo/dotachile-emails/
   └── corpus/
       ├── 2015-03-thread-a1b2c3.md
       ├── 2015-03-thread-d4e5f6.md
       ├── ...
       ├── corpus_chunks.jsonl       # per-message chunks (redacted, safe)
       └── embeddings.pkl            # built lazily on first semantic search
```

Claude Code is opened with `~/Documents/dojo/dotachile-emails/` as an
**additional working directory**, so the corpus is reachable but stays
outside the dotachile git repo (`/add-dir ~/Documents/dojo/dotachile-emails/` can be used).

## Data flow

### Build the corpus (one-time)

1. Mount the sparse bundle: `hdiutil attach ~/Documents/dojo/dotachile-emails-vault.sparsebundle` (prompts for password).
2. Drop the Takeout `.mbox` into `/Volumes/dotachile-emails-vault/raw/`.
3. `python parse_mbox.py /Volumes/dotachile-emails-vault/raw/dotachile.mbox /Volumes/dotachile-emails-vault/intermediate/emails.jsonl`
4. `python redact.py /Volumes/dotachile-emails-vault/intermediate/emails.jsonl ~/Documents/dojo/dotachile-emails/corpus/ --mapping /Volumes/dotachile-emails-vault/mapping.json`
5. Spot-check several `corpus/*.md` files manually for residual PII.
6. Unmount the bundle: `hdiutil detach /Volumes/dotachile-emails-vault`.

### Query the corpus (during a Claude Code session)

1. Open the dotachile repo in Claude Code with `~/Documents/dojo/dotachile-emails/` as an additional working directory.
2. Ask Claude a question like "what did users complain about in the ladder system?"
3. Claude runs `python tools/email-rag/search.py "ladder ELO complaint"` via Bash, gets ranked file paths, then uses Read/Grep on those `corpus/*.md` files to assemble the answer.
4. The encrypted bundle stays unmounted the entire time; Claude never touches raw data.

## Components

### 1. `parse_mbox.py` — mbox → JSONL

Uses Python's stdlib `mailbox.mbox` (zero deps). For each message extracts:

- `message_id`
- `thread_id` — Gmail's `X-GM-THRID` header (preserved by Takeout)
- `date`
- `from`, `to`
- `subject`
- `labels` — `X-Gmail-Labels` header
- `body` — prefers `text/plain`, falls back to stripping HTML with stdlib `html.parser`

Skips attachments entirely (logs filenames only). One JSON object per email,
one email per line. Idempotent: same input always produces same output.

### 2. `redact.py` — JSONL → redacted markdown corpus

Two redaction passes:

**Pass A — deterministic regex** (false negatives are unacceptable here):

- Phone numbers (multi-format, including Chilean conventions)
- IPv4 and IPv6 addresses
- Credit card numbers (Luhn-validated)
- URLs containing query strings that look like tokens or keys
- "Password lines": lines containing `password|contraseña|clave` followed by a value

**Pass B — statistical (Microsoft Presidio):**

- Person names (Spanish + English recognizers loaded)
- Locations
- Email addresses missed by regex

**Sender pseudonymization:**

- The `from:` address is HMAC-SHA256'd with a key generated on first run and stored in `mapping.json`.
- Each unique HMAC is mapped to a stable token: `USER_0001`, `USER_0002`, etc.
- The same email address produces the same token across the entire corpus.
- Different addresses always produce different tokens.
- The mapping table lives **inside the encrypted sparse bundle**, never inside the corpus.

**Output format:** two parallel artifacts.

1. **Human-readable corpus:** one markdown file per `thread_id`, named
   `YYYY-MM-thread-<short-hash>.md`. This is what Claude reads and what
   humans spot-check.
2. **Embedding chunks:** a `corpus_chunks.jsonl` file, one line per
   individual message, each line containing `{thread_file, message_index, body}`.
   This is consumed only by the semantic leg of `search.py` and is not
   meant to be read by humans.

Each markdown file has YAML frontmatter:

```yaml
---
date: 2018-04-12
participants: [USER_0042, USER_0001]
labels: [INBOX, complaints]
subject: problema con ladder ELO
---
```

Followed by the redacted message bodies in chronological order, separated
by `---`. Markdown is used because Claude reads/greps it natively and
humans can spot-check it easily.

**Identity preservation:** dotachile in-game usernames (e.g.
`[CHi]Predator`) are explicitly **not** redacted. They are the most useful
linkage to the codebase and are public pseudonyms by design.

### 3. `search.py` — hybrid retrieval over the corpus

The search stage runs **both** lexical and semantic retrieval on every
query, merges the candidate sets, and returns a ranked union. Neither
strategy alone is sufficient: BM25 misses paraphrases ("the ranking is
broken" should match "ladder ELO complaint"), and embedding search misses
exact tokens (usernames, error strings, dates).

**Lexical leg (BM25):**

- In-memory index built from `corpus/*.md` at startup using `rank-bm25`.
- ~5ms for <1k docs; no persistence needed.
- Operates on whole markdown files (per-thread granularity).

**Semantic leg (embeddings):**

- Model: `paraphrase-multilingual-mpnet-base-v2` (~420MB, Apache 2.0).
  Significantly better Spanish quality than the smaller MiniLM variant.
- Embeds **per-message chunks**, not per-thread. The redactor produces a
  parallel `corpus_chunks.jsonl` alongside the markdown corpus. Each chunk
  is one email body with backreferences to its parent thread file. This
  prevents long threads from diluting the signal of the one message that
  actually matches.
- Embeddings cached as a local pickle in the corpus dir, built lazily on
  the first search after a corpus rebuild. First-time cost: ~30s (model
  download + ~1k chunk embeddings). Subsequent searches: sub-second.
- A query is embedded once and compared to all chunk embeddings via cosine
  similarity. Top-K chunks are mapped back to their parent thread files.

**Merge strategy:**

- Take top-10 from BM25 and top-10 from semantic.
- Deduplicate by file path.
- Return the merged list (up to 20 candidates) with both scores attached
  so Claude can see why each result was surfaced.

**Output format:** plain text, one result per line:

```
<bm25_score> <semantic_score> <relative-path> :: <snippet>
```

A `-` in either score column means that retriever did not surface this
file. Claude reads this from Bash output, then uses Read on the most
promising files. The agent is the final ranker — `search.py` only needs
to put the right candidates in the set.

## Error handling

- **Missing mbox file:** parser exits with non-zero status and a clear message.
- **Malformed message in mbox:** parser logs the offending message-id, skips it, continues. Final summary reports the skip count; non-zero skip count requires manual investigation but is not a hard failure.
- **Presidio model not installed:** redactor exits with installation instructions; does **not** silently fall back to regex-only.
- **Mapping file path inside corpus dir:** redactor refuses and exits with a clear error. This prevents accidental leaking of the de-anonymization table.
- **Empty corpus:** search exits with a clear "no documents indexed" message rather than returning empty results.

## Testing

Test runner: `pytest`, run with `pytest tools/email-rag/tests/`. No network,
no external services, runs in under 5 seconds.

### Fixtures

A small synthetic `.mbox` (~15 messages) hand-authored with **planted PII**
of every category we care about:

- Real-looking names (Spanish + English): "Juan Pérez", "Mary O'Brien"
- Email addresses in `From:`, `To:`, and inline in bodies
- Phone numbers in multiple formats: `+56 9 1234 5678`, `(02) 234-5678`, `912345678`
- IPs: `192.168.1.1`, `2001:db8::1`
- A planted credit card number (Luhn-valid test number, e.g., `4111 1111 1111 1111`)
- A "password line": `mi password es: hunter2`
- A dotachile username that **must not** be redacted: `[CHi]Predator`
- A multi-participant email thread to verify pseudonymization stability

### `test_parse.py`

- Runs `parse_mbox.py` on the fixture.
- Asserts JSONL output has the expected number of records.
- Asserts threading is preserved (`X-GM-THRID` round-trips).
- Asserts HTML-only messages are stripped to plain text.
- Asserts attachments are excluded from output.

### `test_redact.py` (the most important file)

- Runs `redact.py` on the parser output.
- For each planted PII item, asserts it does **not** appear anywhere in the corpus markdown (literal substring + case-insensitive scan).
- Asserts the dotachile username **does** appear unmodified.
- Asserts pseudonymization is **stable**: the same email address in two different messages produces the same `USER_NNNN` token.
- Asserts pseudonymization is **unique**: two different email addresses produce different tokens.
- Asserts `mapping.json` is written to the path passed via `--mapping` and is **not** inside the corpus directory.
- Asserts the redactor refuses to run when `--mapping` points inside the corpus dir.

### `test_search.py`

- Seeds a tiny corpus and a tiny `corpus_chunks.jsonl`.
- Tests the BM25 leg directly: asserts ranking is sane (exact-phrase match outranks tangential match).
- Tests the merge logic with **mocked embedding scores** (no real model loaded): given fixed BM25 and semantic top-10 lists, asserts the deduped union has the expected ordering and that both score columns are populated correctly.
- Does **not** load the real embedding model in CI (340MB+ download is too heavy). A manual end-to-end smoke test for semantic retrieval is documented in the README.

### Deliberately not tested

- The actual Gmail Takeout export (one-time manual step).
- The encrypted sparse bundle workflow (macOS plumbing, not our code).
- Presidio's internal accuracy (we trust the library; we test our integration).

## README contents

The `tools/email-rag/README.md` is a deliverable, not an afterthought. It
must contain step-by-step procedures for:

1. **Requesting the Gmail Takeout export**
   - Logging into `dotachile.com@gmail.com`
   - Navigating to <https://takeout.google.com>
   - Selecting only Gmail
   - Choosing `.mbox` format
   - Receiving the download link
   - Verifying the archive

2. **Creating the encrypted sparse bundle**
   - The exact `hdiutil create` command with AES-256 and a sensible initial size
   - Where to store the bundle
   - How to set a strong password

3. **Mounting and unmounting the bundle**
   - `hdiutil attach` / `hdiutil detach` commands
   - How to confirm the mount path

4. **Running the pipeline end-to-end**
   - Each script invocation with example arguments
   - Where intermediate and final outputs land

5. **Verifying redaction manually**
   - Suggested grep patterns to spot-check the corpus before trusting it
   - What to do if PII is found

6. **Querying the corpus from Claude Code**
   - How to add the corpus directory as an additional working directory
   - Example prompts that work well

7. **First-time semantic index warmup**
   - What to expect on the first `search.py` invocation after a corpus rebuild (model download + embedding pass, ~30s)
   - Where the embeddings cache pickle lives and when to delete it

## Dependencies

- Python 3.11+
- `presidio-analyzer` (PII detection — MIT)
- `rank-bm25` (lexical search — Apache 2.0)
- `sentence-transformers` (semantic search — Apache 2.0)
- `pytest` (testing)

All installable via `pip` from `pyproject.toml`. No system packages, no
Docker, no external services. The embedding model
(`paraphrase-multilingual-mpnet-base-v2`, ~420MB, Apache 2.0) is fetched
automatically on first semantic search.

## Open questions

None at design time. All decisions resolved during brainstorming.