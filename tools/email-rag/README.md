# email-rag

Local toolkit that turns a frozen Gmail Takeout export of
`dotachile.com@gmail.com` into a redacted, hybrid-searchable corpus that
Claude Code can query during sessions.

See `docs/superpowers/specs/2026-04-07-email-rag-design.md` for the design
rationale.

## What it does, in three stages

```
Gmail Takeout (.mbox)
    │
    ▼  parse_mbox.py
emails.jsonl
    │
    ▼  redact.py  (regex pass + Presidio pass + sender pseudonymization)
corpus/*.md  +  corpus/corpus_chunks.jsonl  +  vault/mapping.json
    │
    ▼  search.py  (BM25 + multilingual mpnet embeddings, merged)
ranked candidates → Claude reads them via Read/Grep
```

## One-time setup

### 1. Install Python deps

```bash
cd tools/email-rag
python3 -m venv .venv
source .venv/bin/activate
pip install -e ".[dev]"
python -m spacy download en_core_web_sm
python -m spacy download es_core_news_sm
```

### 2. Request the Gmail Takeout export

1. Sign in to <https://accounts.google.com> as `dotachile.com@gmail.com`.
2. Visit <https://takeout.google.com>.
3. Click **Deselect all**, then check **Mail** only.
4. Open **All Mail data included** and confirm format **MBOX**.
5. Click **Next step**, choose **Send download link via email**, file
   type **.zip**, max size **2 GB**.
6. Click **Create export**.
7. When the email arrives, download the archive and unzip it. Inside
   you'll find `Takeout/Mail/All mail Including Spam and Trash.mbox`.

### 3. Create the encrypted sparse bundle

This is where the raw mbox, intermediate JSONL, and the de-anonymization
mapping live. The redacted corpus stays outside the bundle so Claude can
read it without you mounting anything.

```bash
hdiutil create \
    -size 2g \
    -type SPARSEBUNDLE \
    -fs APFS \
    -encryption AES-256 \
    -volname dotachile-emails-vault \
    ~/Documents/dojo/dotachile-emails-vault
```

You'll be prompted for a password — pick a strong one and store it in
your password manager. Without it, the bundle is unrecoverable.

### 4. Mount the bundle and drop in the export

```bash
hdiutil attach ~/Documents/dojo/dotachile-emails-vault.sparsebundle
# Enter the password when prompted.
mkdir -p /Volumes/dotachile-emails-vault/raw
mkdir -p /Volumes/dotachile-emails-vault/intermediate
cp "/path/to/Takeout/Mail/All mail Including Spam and Trash.mbox" \
   /Volumes/dotachile-emails-vault/raw/dotachile.mbox
```

### 5. Create the public corpus directory

```bash
mkdir -p ~/Documents/dojo/dotachile-emails/corpus
chmod 700 ~/Documents/dojo/dotachile-emails
```

## Building the corpus

With the bundle mounted and the venv active:

```bash
cd tools/email-rag
source .venv/bin/activate

python parse_mbox.py \
    /Volumes/dotachile-emails-vault/raw/dotachile.mbox \
    /Volumes/dotachile-emails-vault/intermediate/emails.jsonl

python redact.py \
    /Volumes/dotachile-emails-vault/intermediate/emails.jsonl \
    ~/Documents/dojo/dotachile-emails/corpus \
    --mapping /Volumes/dotachile-emails-vault/mapping.json
```

The redactor refuses to run if `--mapping` points inside the corpus dir.

### Verify redaction manually before trusting the corpus

Run a few greps over the corpus directory looking for things that should
**not** be there:

```bash
cd ~/Documents/dojo/dotachile-emails/corpus

# Email-shaped strings that slipped through:
grep -E '[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}' *.md | head

# Phone-shaped strings:
grep -E '\+?[0-9]{2,4}[ -]?[0-9]{3,4}[ -]?[0-9]{4}' *.md | head

# IPs:
grep -E '\b([0-9]{1,3}\.){3}[0-9]{1,3}\b' *.md | head
```

If anything real-looking appears, **do not proceed**. Open an issue in
your notes about which pattern was missed, tighten the rules in
`redaction_rules.py`, re-run `redact.py`, and re-grep.

### Unmount the bundle when done

```bash
hdiutil detach /Volumes/dotachile-emails-vault
```

## Querying from Claude Code

1. Open the dotachile repo in Claude Code.
2. Add the corpus dir as an additional working directory:
   - Pass `--add-dir ~/Documents/dojo/dotachile-emails` when launching Claude Code, or use the in-app command.
3. Ask Claude something like:
   > What did users complain about in the ladder system around 2018?
4. Claude will run `python tools/email-rag/search.py "..."` via Bash and
   then Read the most relevant `corpus/*.md` files.

The encrypted bundle stays unmounted during normal sessions. Claude only
ever touches the redacted markdown.

### Manual semantic-search smoke test

The first time you run `search.py` after rebuilding the corpus, the
semantic leg downloads the
`paraphrase-multilingual-mpnet-base-v2` model (~420 MB) and embeds every
chunk. Expect ~30 seconds of one-time cost. Subsequent searches are
sub-second.

If you want to skip the semantic leg entirely (faster, no model load):

```bash
python search.py --bm25-only "ladder ELO complaint"
```

If you ever need to invalidate the embeddings cache (for example after
manually editing files in the corpus):

```bash
rm ~/Documents/dojo/dotachile-emails/corpus/embeddings.pkl
```

## Running the tests

```bash
cd tools/email-rag
source .venv/bin/activate
pytest
```

All tests run against synthetic fixture data. No network, no external
services, no real emails. Runtime ~10-30 seconds.
