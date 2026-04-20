# Stack Research

**Domain:** AI-assisted software engineering workshops (Spanish, 1-hour live, legacy Java EE demo surface)
**Researched:** 2026-04-19
**Confidence:** MEDIUM-HIGH overall
- **HIGH** for Claude Code primitive shapes (verified by direct inspection of this repo's `.claude/`)
- **HIGH** for Marp / Mermaid pipeline (verified against `docs/presentations/CLAUDE.md` and existing rendered deck)
- **MEDIUM** for RAG and local-LLM stack (training-data grounded, not re-verified against 2026-04 docs)
- **MEDIUM** for live-coding/asciinema tooling (stable ecosystem, but versions are training-data pinned)
- **LOW** for 2026-specific meta-workflow additions beyond GSD / Superpowers / Spec Kit

> **Verification note.** WebSearch and WebFetch were denied in this research session. Primitive-shape
> claims are grounded in **direct file inspection** of the installed GSD system, the `.claude/`
> directory of this repo, and existing Marp deck `docs/presentations/2026-04-10-ai-driven-development/`.
> Library-version claims that aren't so grounded are explicitly flagged `LOW` and should be
> re-checked against official docs before the capstone session is authored.

---

## Recommended Stack

### 1. Presentation / slide tooling (core)

| Technology | Version | Purpose | Why Recommended |
|------------|---------|---------|-----------------|
| `@marp-team/marp-cli` | `^4.x` (latest from npm at session time) | Markdown → HTML/PDF slide rendering, speaker notes, CLI-first | **Already adopted** (see `docs/presentations/CLAUDE.md`). Speaker notes via HTML comments render natively. Gaia theme used in existing decks. Zero new toolchain to teach. |
| `@mermaid-js/mermaid-cli` (`mmdc`) | `^11.x` | Pre-render `.mmd` → `.svg` | **Required by the HTMLPreview.github.io constraint** documented in repo — inline Mermaid JS breaks there because `<script>` tags get rewritten. Pre-rendered SVG is the only portable answer. |
| Marp directive set | `theme: gaia`, `paginate: true`, `class: lead` for title slides | Deck voice already in `2026-04-10-ai-driven-development.md` | Keep continuity with the two reference decks; don't re-teach theme. |
| Speaker notes | HTML comments `<!-- ... -->` inside slides | Presenter-view notes | Marp's native convention; visible in Marp-for-VSCode preview pane during rehearsal. |

**Syntax highlighting for Java EE stacks (Java 8 / XHTML / JSF / SQL):** Marp ships Prism under the hood.
Fenced blocks with `java`, `xml` (for XHTML), `sql`, `bash`, `properties` all render cleanly. No plugin needed.
The `xml` grammar is the right one for JSF/Facelets — there's no dedicated Facelets lexer anywhere and
there doesn't need to be. Confidence: HIGH (based on existing deck rendering successfully).

**What NOT to swap Marp for:** reveal.js, Slidev, remark.js. All three are viable technically but
require re-authoring the two existing decks and re-teaching a toolchain that works. Non-starter.

### 2. Claude Code / Claude Agent SDK primitives (the curriculum surface)

This is the teaching surface. Shapes below are verified against the `.claude/` directory of *this
repo* as of 2026-04-19 (GSD installed at hook version `1.38.0`, see `.claude/hooks/gsd-prompt-guard.js`).

#### 2a. Skills — `.claude/skills/<name>/SKILL.md`

**Folder layout** (verified — see `.claude/skills/git-commit/`):
```
.claude/skills/
  <skill-name>/
    SKILL.md          # required — frontmatter + body
    <helper-files>    # optional — scripts, references, templates
```

**SKILL.md frontmatter** (verified from `git-commit/SKILL.md`):
```yaml
---
name: git-commit
description: 'Execute git commit with conventional commit message analysis...
              Use when user asks to commit changes...'
license: MIT
allowed-tools: Bash
---
```

Key fields:
- `name` — skill identifier, kebab-case, must match folder name
- `description` — triggers the skill; Claude decides when to load it based on this
- `allowed-tools` — whitelist of tools the skill can invoke (optional; defaults to inherit)
- `license` — optional, but present in community-shared skills

**Scopes** (verified convention):
- **Project**: `.claude/skills/` (checked in, team-shared)
- **User**: `~/.claude/skills/` (personal, cross-repo)
- **Plugin**: skills bundled inside a plugin package (see 2f)

**Teaching hook:** the `git-commit` skill and the `user-story` skill are already installed. Both are
teachable examples that land real commits.

Confidence: **HIGH** (verified by reading the live files).

#### 2b. Subagents / the `Task` tool — `.claude/agents/<name>.md`

**Folder layout** (verified — see `.claude/agents/`, 35 agents installed):
```
.claude/agents/
  gsd-domain-researcher.md
  gsd-code-reviewer.md
  ...
```

**Frontmatter** (verified from `gsd-domain-researcher.md`):
```yaml
---
name: gsd-domain-researcher
description: Researches the business domain... Spawned by /gsd-ai-integration-phase orchestrator.
tools: Read, Write, Bash, Grep, Glob, WebSearch, WebFetch, mcp__context7__*
color: "#A78BFA"
# hooks:                             # commented-out example of agent-level hooks
#   PostToolUse:
#     - matcher: "Write|Edit"
#       hooks:
#         - type: command
#           command: "echo '...'"
---
```

Key fields:
- `name` — must match filename
- `description` — describes when the orchestrator should spawn it (triggers the `Task` tool)
- `tools` — whitelist; omit to inherit parent's full toolset
- `color` — UI hint for the statusline/transcript
- `model` — optional override (e.g. `haiku` for cheap dispatchers); not set here but documented widely

**Invocation:** parent agents call the built-in `Task` tool with `subagent_type: <name>`. This is
exactly the primitive you want to teach in the "agents" session — spawn a researcher, get back a
report, compose the results.

**Known gotcha (teaching gold):** when you declare a `tools:` frontmatter field, upstream bug
`anthropics/claude-code#13898` strips MCP tools from the agent. The documented workaround
(see docstring in `gsd-domain-researcher.md`) is to call `npx ctx7` via Bash. Confidence: HIGH
(the bug reference is in the shipping GSD agent, so it's real enough to base a slide on).

Confidence: **HIGH** (verified by file inspection).

#### 2c. Hooks — `.claude/settings.json` + `.claude/hooks/*`

**Configuration** (verified from `.claude/settings.json`):
```json
{
  "hooks": {
    "SessionStart": [ { "hooks": [ { "type": "command", "command": "..." } ] } ],
    "PreToolUse":  [ { "matcher": "Write|Edit", "hooks": [ ... ] } ],
    "PostToolUse": [ { "matcher": "Bash|Edit|Write|MultiEdit|Agent|Task", "hooks": [ ... ] } ]
  }
}
```

**Event names verified in this repo's config:** `SessionStart`, `PreToolUse`, `PostToolUse`.

**Event names documented in public Claude Code docs (training data, LOW-MEDIUM confidence; verify
before the hooks session):** additionally `UserPromptSubmit`, `Notification`, `Stop`, `SubagentStop`,
`PreCompact`.

**Matcher semantics** (verified):
- `matcher` is a pipe-delimited tool-name regex; omit for "all tools"
- Multiple entries with the same event compose additively
- Each hook declares `type: "command"` and a shell command; `timeout` in seconds is optional

**Hook I/O contract** (from training data + live hook scripts in `.claude/hooks/`):
- STDIN: JSON payload with tool name, args, and session context
- STDOUT: optional JSON response; exit code 0 = allow, non-zero = block (PreToolUse only)
- `$CLAUDE_PROJECT_DIR` is a guaranteed env var

Confidence: **HIGH** for the event names, matcher format, and the command shape shown above;
**MEDIUM** for the full list of event names beyond what's used here.

#### 2d. Slash commands — `.claude/commands/<namespace>/<name>.md`

**Folder layout** (verified — 40+ GSD commands under `.claude/commands/gsd/`):
```
.claude/commands/
  gsd/
    do.md              → invoked as `/gsd-do` (namespace flattens with dash)
    new-project.md     → invoked as `/gsd-new-project`
    ...
```

**Frontmatter** (verified from `do.md`):
```yaml
---
name: gsd:do
description: Route freeform text to the right GSD command automatically
argument-hint: "<description of what you want to do>"
allowed-tools:
  - Read
  - Bash
  - AskUserQuestion
---
```

- `argument-hint` shows in the autocomplete menu when the user types `/`
- `allowed-tools` restricts what tools the command can use
- Body can reference files with `@/absolute/path` (verified — GSD uses this heavily)

Confidence: **HIGH** (verified by file inspection).

#### 2e. MCP servers — `.mcp.json` (project scope) or user config

**Project-scope config** (standard shape — this repo does not check one in, so this is
training-data grounded, MEDIUM confidence):
```json
{
  "mcpServers": {
    "context7": {
      "command": "npx",
      "args": ["-y", "@upstash/context7-mcp"]
    },
    "filesystem": {
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-filesystem", "/path/to/allow"]
    }
  }
}
```

**Transports:**
- `stdio` (default, local process) — what you want for workshops
- `http` / `sse` (remote servers) — skip for a local-only demo

**Tool-name shape inside Claude Code:** `mcp__<server-name>__<tool-name>`. Verified by the
`tools: Read, Write, ..., mcp__context7__*` line in GSD agents.

**Local MCP servers that teach well in < 10 minutes:**
- `@modelcontextprotocol/server-filesystem` — read/write a sandbox directory
- `@modelcontextprotocol/server-sqlite` — query local SQLite
- `@modelcontextprotocol/server-git` — git operations (note: Claude Code already has native git via Bash, so this is an "MCP wrapper vs native tool" teaching moment)

Confidence: **MEDIUM** — shapes are stable but exact package names and transport behaviour should
be re-verified from `https://docs.claude.com/en/docs/claude-code/mcp` before the MCP session.

#### 2f. Plugins

Plugins bundle skills + agents + commands + hooks + MCP server declarations into an installable
package. GSD itself ships as `.claude/get-shit-done/` (see directory structure — it has `bin/`,
`contexts/`, `templates/`, `workflows/`, `references/`, and a `VERSION` file). This is the exact
shape to reference when teaching "how to ship a plugin".

Confidence: **MEDIUM** — plugin as a first-class distribution format evolved over 2025; exact
packaging/publishing story should be verified at capstone time.

### 3. RAG demo stack (local-only, < 30 min to build live)

| Technology | Version | Purpose | Why Recommended |
|------------|---------|---------|-----------------|
| Python | 3.11+ | Runtime for the RAG pipeline | Already required for `tools/email-rag/` — no new toolchain |
| `rank_bm25` | `^0.2.2` | Lexical retrieval (BM25) | Zero-dependency pure Python; what `tools/email-rag/` already uses |
| `sentence-transformers` | `^3.x` | Semantic embeddings | Multilingual models for Spanish/English — critical for the Chilean corpus |
| Model: `paraphrase-multilingual-MiniLM-L12-v2` or `intfloat/multilingual-e5-small` | — | ~420 MB multilingual embedding model | First-session load is 5–15s, then sub-second. Already in use in `tools/email-rag/search.py`. |
| `numpy` | `^1.26` / `^2.x` | Cosine similarity for semantic leg | Standard |
| Storage | JSONL (`corpus_chunks.jsonl`) + in-memory numpy array | "Vector store" for ≤100k chunks | **No Chroma/FAISS needed at this scale.** JSONL is auditable, diffable, grep-able — ideal for teaching. |

**Why this is the right workshop RAG** (vs Chroma / LlamaIndex / LangChain):

1. **Auditable in 1 hour.** 80 lines of Python vs ~300 lines of framework glue.
2. **No cloud creds.** All embeddings local; no OpenAI API key needed.
3. **Hybrid search beats BM25-alone on short Spanish queries** — the `tools/email-rag/` pipeline
   already proves this on the Chilean corpus (`CLAUDE.md` root note: "Hybrid beats BM25-only for
   short queries").
4. **The corpus shape (Markdown thread files) is the 2026 sweet spot** — one file per thread is
   dumb and works. Don't over-engineer chunking.

**What NOT to use for the workshop-scale demo:** LangChain, LlamaIndex, Chroma, Weaviate, Qdrant.
All are good production tools; all bury the RAG mental model under abstractions that take 30
minutes to un-learn. Save for an advanced session.

**Bonus pattern (Markdown/email):** the `tools/email-rag/search.py` script IS the reference
implementation — 200 lines, BM25 + sentence-transformers, hybrid ranking, Spanish-first. Use it
verbatim as the starting artifact of the RAG session, then teach by subtraction (show each piece)
rather than construction.

Confidence: **MEDIUM-HIGH** — implementation works today in this repo; model names and exact
package versions should be pinned against the running venv (`tools/email-rag/.venv/`) at session time.

### 4. Local demo infrastructure (zero cloud creds)

| Technology | Version | Purpose | Why Recommended |
|------------|---------|---------|-----------------|
| **Claude Code CLI** | latest stable (self-updating) | The AI the workshop teaches | The whole point. Ships with `Task`, Skills, MCP, Hooks. |
| **Ollama** | `^0.5.x` (verify at session time) | Local LLM runtime for "what if we didn't use Claude?" sidebars | Fast install, GGUF model library, OpenAI-compatible `/v1/chat/completions` endpoint. Use for RAG demos that don't need Claude. |
| Ollama models for demos | `llama3.1:8b` (English heavy), `qwen2.5:7b` or `qwen2.5:14b` (better Spanish), `nomic-embed-text` | Generation + embeddings | Audience can follow along on a 16 GB MacBook. |
| Docker Desktop / Docker Engine | any 2025+ | Running the DotaChile Payara + PostgreSQL stack locally | Already required by the codebase (`docker compose up -d`). No new ask. |
| Local MCP servers | filesystem, sqlite, git (see 2e) | MCP session hands-on | All `npx`-installable; no cloud. |

**What NOT to demo:** LM Studio, llama.cpp directly, vLLM. All fine tools, none add pedagogical
value over Ollama for a 1-hour slot. Ollama is the path of least setup friction for a mixed-audience
"follow along on your laptop" moment.

Confidence: **MEDIUM** — Ollama is stable; specific model tags drift monthly, verify before each
session.

### 5. Live-coding / after-session artifact tooling

| Tool | Purpose | Notes |
|------|---------|-------|
| **asciinema** `^2.4` | Record terminal sessions as text, replay in browser | The canonical answer. Output is a tiny `.cast` JSON file, embeddable in slides via `asciinema-player`. Use for "here's what I ran live" after-artifacts. |
| **VHS** (charmbracelet/vhs) | Scripted terminal recordings (`.tape` file → `.gif`/`.webm`) | Use for *pre-recorded* demos in the deck (reproducible, no live-typing nerves). `.tape` is plain text, diff-able. |
| **tmux** | Pane layout for "editor + terminal + logs" during the live session | Save a session profile per workshop. Removes fumbling. |
| **Marp presenter view** (via `marp -s` server mode) | Split-screen notes/timer/current slide | The "pacing tools" ask — Marp's built-in server has a timer. No third-party timer needed. |
| **Git SHA badges on every slide** | Traceability slides → commits | Convention: each demo section slide names the SHA range. Enables "go clone this repo at `abc123` and see the before state". This is the core value-prop of the whole series. |

**What NOT to use:** ttyrec (abandoned), Terminalizer (works but fragile JS stack), OBS/screen
recording (defeats "everything is text" ethos). asciinema + VHS cover 100% of the need.

Confidence: **MEDIUM-HIGH** — asciinema and VHS are both stable, widely-used; exact latest versions
drift but the APIs don't.

### 6. Meta-workflow systems (capstone comparison set)

The capstone session compares, not picks a winner. The teaching value is showing *three shapes of
the same problem*.

| System | Shape | Where to point the audience |
|--------|-------|-----------------------------|
| **GSD** (Get Shit Done) | Phase-gated workflow: Project → Milestone → Phase → Wave. 40+ slash commands, 35 agents, hook-enforced invariants. Installed here at `.claude/get-shit-done/` (version from `.claude/get-shit-done/VERSION`). | Live surface in this repo. `/gsd-new-project`, `/gsd-plan-phase`, `/gsd-transition`. |
| **Superpowers** | Spec+plan document pairs living in the repo. Evidence-first; the AI writes a spec, then a plan, then lands the commit. | Prior art in `docs/superpowers/{specs,plans}/` — real examples from earlier repo work (testing, email-rag, claude-md-per-feature, password-confirmation, presentation planning). |
| **Spec Kit** (GitHub's) | Spec-driven scaffolding via `specify init`. Produces a spec/plan/tasks triptych and a slash-command flow. Lightweight vs GSD. | Reference from `github.com/github/spec-kit` (training data, LOW confidence on 2026-04 state — verify freshness at capstone time). |

**2025–2026 additions to evaluate (LOW confidence — needs verification):**
- **Claude Code Plugins marketplace** — if Anthropic has shipped a plugin registry by capstone time, mention it. As of my training (Jan 2026) it's documented but the registry story was still forming.
- **Agent OS / AgentKit-style frameworks** — the space moves fast; check what's hot at session time and *explicitly note in the deck* that the comparison set is a snapshot, not a leaderboard.

**Teaching framing (important):** do NOT pick a winner in the capstone. The honest answer is "GSD
is heavy and opinionated, Superpowers is lightweight and evidence-centric, Spec Kit is neutral
scaffolding — you pick based on team size, codebase age, and how much process you want to encode."
This is a higher-value take than a ranking.

Confidence: **MEDIUM** for GSD/Superpowers (both present in this repo, verifiable); **LOW** for
Spec Kit specifics and "what else launched in 2026"; **HIGH** for the framing recommendation.

---

## Supporting Libraries

| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| `@mermaid-js/mermaid-cli` | `^11.x` | `.mmd` → `.svg` render step | Every session with a diagram |
| `@marp-team/marp-cli` | `^4.x` | `.md` → `.html` build | Every session (core) |
| `rank_bm25` | `^0.2.2` | BM25 retrieval | RAG session, capstone if RAG re-appears |
| `sentence-transformers` | `^3.x` | Multilingual embeddings | RAG session |
| `asciinema` | `^2.4` | Terminal recording | Any session with a live demo where the artifact matters |
| `vhs` (charmbracelet) | latest | Scripted terminal gif/mp4 | Pre-recorded demo fallbacks in decks |
| `ollama` | `^0.5.x` | Local LLM server | RAG session local-inference sidebar |
| `@modelcontextprotocol/server-filesystem` | npm latest | MCP teaching example | MCP session |
| `@modelcontextprotocol/server-sqlite` | npm latest | MCP teaching example | MCP session (bonus: queries the DotaChile DB) |

---

## Development Tools

| Tool | Purpose | Notes |
|------|---------|-------|
| **Claude Code CLI** | The environment the workshop teaches | Each participant needs it installed + authenticated *before* the session, not during |
| **Node.js 20+** | Runs `npx marp`, `npx mmdc`, MCP servers | Already a dependency of the Marp pipeline |
| **Python 3.11+ venv** | RAG demos | Keep `tools/email-rag/.venv` as the canonical venv |
| **Docker + Compose** | DotaChile app stack for demo commits | Already documented in repo root |
| **Marp for VS Code** extension (`marp-team.marp-vscode`) | Author-side preview with speaker-notes pane | Optional for author; audience doesn't need it |
| `git` 2.40+ | Obvious, but pin for the rebase/worktree demos | |

---

## Installation (instructor-side, one-time)

```bash
# Presentation pipeline (uses npx; no global install required, but pre-warming helps)
npx -y @marp-team/marp-cli@latest --version
npx -y @mermaid-js/mermaid-cli --version

# RAG pipeline (already set up under tools/email-rag/)
# See tools/email-rag/README.md — do not duplicate here

# Local LLM (optional, for local-inference sidebar)
brew install ollama            # macOS — or see ollama.com for other OS
ollama pull qwen2.5:7b         # Spanish-friendly general model
ollama pull nomic-embed-text   # Embeddings

# Terminal recording
brew install asciinema
brew install vhs               # Charmbracelet VHS
```

**Audience-side requirements** (communicate before the workshop):
- Claude Code CLI installed and authenticated
- Node.js 20+
- Docker Desktop running
- Repo cloned: `git clone ... && docker compose up -d`
- (RAG session only) Python 3.11+ + `tools/email-rag/` venv set up per `tools/email-rag/README.md`

---

## Alternatives Considered

| Recommended | Alternative | When to Use Alternative |
|-------------|-------------|-------------------------|
| Marp | reveal.js / Slidev / remark | Only if the repo convention changes. Today, non-starter — two decks already authored. |
| Mermaid (pre-rendered SVG) | D2 / Graphviz / Excalidraw | If you want hand-drawn vibes (Excalidraw) for the intro LLM-history session. Still export to SVG. |
| rank_bm25 + sentence-transformers | LangChain / LlamaIndex / Chroma | Production RAG with >100k docs, access control, multi-tenant. Not a workshop. |
| Ollama | LM Studio | If the audience is Windows-heavy and prefers GUI. Same model library under the hood. |
| asciinema + VHS | OBS / screen recording | If the workshop ever adds a "watch me use the GUI" moment. Not currently planned. |
| GSD/Superpowers/Spec Kit comparison | Pick one and teach it | Only for a dedicated "how to use GSD" session. The capstone's job is breadth. |

---

## What NOT to Use

| Avoid | Why | Use Instead |
|-------|-----|-------------|
| Runtime Mermaid JS on slides | HTMLPreview.github.io rewrites `<script>` tags, breaking every Mermaid render in the browser preview — documented in `docs/presentations/CLAUDE.md` | Pre-rendered SVG via `mmdc -b transparent` |
| LangChain / LlamaIndex for the RAG session | Burns 30 min on framework glue, obscures the two real concepts (retrieval + generation). Audience leaves thinking RAG ≡ LangChain. | 80 lines of `rank_bm25 + sentence-transformers` — the existing `tools/email-rag/search.py` |
| Cloud-only LLM APIs (OpenAI, Gemini) in demos | API key management during a live session is a time sink and excludes audience members without a paid account | Claude Code (instructor already has it) + Ollama for follow-along |
| English-language decks | Violates root `CLAUDE.md` Spanish-language rule. Two existing decks set the precedent. | Spanish decks; English planning docs are fine per PROJECT.md |
| Renaming existing Marp decks to "preserve" them | Explicitly out of scope per PROJECT.md ("re-split and extended, not replayed") | Cannibalize the two 2026-04 decks for raw material, ship new decks with new SHAs |
| Live Mermaid authoring during a session | Re-rendering latency + typos eat the 1-hour budget | Pre-author `.mmd`+`.svg`, check in both, reference the `.svg` |
| Teaching subagents via the `tools:` restriction pattern without mentioning bug #13898 | Audience will hit the MCP-strip bug and blame themselves | Either omit the `tools:` field (inherit) OR teach the bug explicitly — it's a great "the ecosystem is still raw" teaching moment |
| Long-lived teaching branches | Explicitly out of scope per PROJECT.md — demo commits land on `master` | `master`-only, SHA-referenced slide-to-code traceability |
| ttyrec, Terminalizer, OBS for demo artifacts | Abandoned / fragile / overkill respectively | asciinema for live record, VHS for scripted |

---

## Stack Patterns by Variant

**If the session's topic is RAG on the email corpus:**
- Start from the checked-in `tools/email-rag/` artifact
- Teach by **subtraction** — show the script, then walk backward: remove semantic leg, show BM25-only; then remove BM25, show naive substring. Audience sees the value of each layer.
- Query in Spanish only — the corpus is Chilean/Spanish. "torneo" works, "tournament" doesn't (documented in root CLAUDE.md).

**If the session's topic is an MCP server:**
- Build a toy MCP server in Python or Node that wraps a DotaChile concern (e.g. "query top-N clans by ELO")
- Use `@modelcontextprotocol/sdk` (TypeScript) or `mcp` (Python) — pick whichever the audience is warmer on
- Config via `.mcp.json` at the repo root; DO NOT commit secrets there
- Show the `mcp__<server>__<tool>` naming convention so the audience recognizes MCP-bound tools in transcripts

**If the session's topic is Hooks:**
- Live-modify `.claude/settings.json` to add a hook (e.g. PreToolUse on `Bash` that warns before `rm`)
- Reference the existing GSD hooks (`.claude/hooks/gsd-*.js`) as "production examples" — they're battle-tested
- Teach the STDIN JSON contract and exit-code semantics; those are the whole mental model

**If the session's topic is Skills:**
- Start from the live `.claude/skills/git-commit/` skill — it's a real one the audience can use
- Walk through the `description` field as the *trigger* — the field doubles as documentation and routing
- Ship a new skill during the hour that encapsulates a DotaChile concern (e.g. `dotachile-deploy` that wraps the `dev-sync.sh` flow)

**If the session's topic is Subagents:**
- Spawn a `Task` with a tiny custom agent that scans `.planning/codebase/CONCERNS.md` and returns the top-3 highest-severity items
- Show the frontmatter → file → orchestrator-spawns-child flow end-to-end
- Mention bug `anthropics/claude-code#13898` when discussing the `tools:` field (honesty builds trust)

**If the session's topic is Meta-workflow comparison (capstone):**
- Three branches of the same task across GSD / Superpowers / Spec Kit
- Pick a small DotaChile cleanup (e.g. delete `src/java/controller/TESTMAIN.java` leftovers) and run it through each workflow
- Audience compares artifact shape, invocation cadence, and process overhead side-by-side
- Explicitly call the ordering arbitrary — don't rank

---

## Version Compatibility

| Package A | Compatible With | Notes |
|-----------|-----------------|-------|
| `@marp-team/marp-cli@^4` | Node.js 18+ | Node 20 recommended for workshop laptops |
| `@mermaid-js/mermaid-cli@^11` | Node.js 18+, headless Chromium via Puppeteer (auto-installed) | First render downloads Chromium; pre-warm before the session |
| `sentence-transformers@^3` | Python 3.9–3.12; torch 2.x | Existing `tools/email-rag/.venv` is the source of truth |
| Claude Code CLI | Skills + Subagents + Hooks + MCP all shipped and stable by 2025-H2 | Exact feature-flag status for any one release: re-verify at session time |
| Ollama `0.5.x` | Any 16 GB+ Mac/Linux laptop for 7–8B models | 14B models need 32 GB |
| Marp Gaia theme | Marp CLI `^4` | Already the theme in use in both reference decks |

**Known cross-repo gotcha:** the `tools:` field on subagents strips MCP tools (bug
`anthropics/claude-code#13898`, active as of GSD hook v1.38.0 in this repo). Mitigation in GSD
agents: CLI fallback via `npx ctx7`. **This gotcha is itself a teachable moment.**

---

## Sources

- **`/Users/pmartinez/Documents/git/quantumentangled/dotachile/.claude/settings.json`** — verified hook event names (`SessionStart`, `PreToolUse`, `PostToolUse`), matcher format, and command shape. HIGH confidence.
- **`.claude/skills/git-commit/SKILL.md`** — verified SKILL.md frontmatter (`name`, `description`, `license`, `allowed-tools`). HIGH confidence.
- **`.claude/agents/gsd-domain-researcher.md`** — verified agent frontmatter (`name`, `description`, `tools`, `color`) and the bug-#13898 workaround convention. HIGH confidence.
- **`.claude/commands/gsd/do.md`** — verified slash-command frontmatter (`name`, `description`, `argument-hint`, `allowed-tools`) and the `@/absolute/path` include syntax. HIGH confidence.
- **`.claude/hooks/gsd-prompt-guard.js`** — verified hook I/O contract (STDIN JSON, exit-code semantics, `$CLAUDE_PROJECT_DIR` env var). HIGH confidence.
- **`docs/presentations/CLAUDE.md`** — Marp + Mermaid pipeline, HTMLPreview `<script>`-rewrite constraint, Gaia theme convention. HIGH confidence.
- **`docs/presentations/2026-04-10-ai-driven-development/2026-04-10-ai-driven-development.md`** — confirms Gaia theme, Spanish-first voice, speaker-notes-in-HTML-comments convention. HIGH confidence.
- **`tools/email-rag/` (referenced via root `CLAUDE.md`)** — confirms BM25 + multilingual sentence-transformers pipeline already works on the Chilean corpus. HIGH confidence for "it works"; MEDIUM for exact library versions (not re-read in this session).
- **`.planning/codebase/STACK.md` + `.planning/codebase/CONCERNS.md`** — target-repo stack and the demo-topic pool. HIGH confidence.
- **Training data (through Jan 2026)** — Ollama versions, LangChain/LlamaIndex positioning, asciinema/VHS status, MCP standard-server names. MEDIUM confidence; explicitly flagged where load-bearing.
- **NOT consulted this session** — `docs.claude.com/en/docs/claude-code/*`, `github.com/anthropics/claude-code`, `github.com/github/spec-kit`. WebFetch and WebSearch were denied. **Any load-bearing claim about primitive behaviour not backed by a file in this repo should be re-verified against official docs before the relevant session is authored.**

---

## Gaps to Re-verify Before Each Session

Explicit honesty list — these should be re-checked when the relevant session is planned:

1. **Full list of hook event names beyond `SessionStart | PreToolUse | PostToolUse`.** Training data suggests `UserPromptSubmit`, `Notification`, `Stop`, `SubagentStop`, `PreCompact` exist; only the first three are in this repo. Re-verify at `docs.claude.com/en/docs/claude-code/hooks` before the Hooks session.
2. **Plugin packaging / marketplace story.** Moving fast in 2025–2026. Re-verify before the capstone.
3. **Exact current Ollama model tags** for Spanish — model naming drifts. Check `ollama.com/library` the week of the RAG session.
4. **Spec Kit's 2026 state.** Repo is active; commands may have renamed. Re-verify before the meta-workflow capstone.
5. **MCP transport details and the official `.mcp.json` schema** — re-read `docs.claude.com/en/docs/claude-code/mcp` before the MCP session.
6. **Claude Agent SDK (the Python/TypeScript library) version and API shape** if a session teaches agent authoring programmatically (not just via `.claude/agents/*.md`). Not covered in training data depth; verify against the SDK's GitHub repo.

---
*Stack research for: AI-SWE workshop series on a legacy Java EE codebase (Spanish, live, 1-hour)*
*Researched: 2026-04-19*
*Next review: before each session is authored; always re-verify primitive shapes against live docs at that time.*
