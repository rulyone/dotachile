# Requirements: DotaChile AI-SWE Workshop Series

**Defined:** 2026-04-19
**Core Value:** The demos are believable. AI-assisted work is performed live on a genuinely messy, end-of-life Java EE codebase so the audience walks away convinced the techniques survive first contact with production code.

**How to read this doc:** Requirements are *testable commitments* derived from research synthesis (`.planning/research/SUMMARY.md`). Categories map 1-to-1 to roadmap phases: `SCAF` → Phase 0 (series scaffolding), `S01`–`S09` → Phases 1–9 (workshop sessions), `CURR` and `QUAL` are cross-cutting (woven into every phase's success criteria).

Every requirement is a hypothesis until its phase ships. See `.planning/research/SUMMARY.md` for the reasoning behind each REQ; see PITFALLS.md IDs referenced inline for prevention rationale.

## v1 Requirements

### SCAF — Phase 0 Series Scaffolding

*Must exist before Session 1 is authored. Without these, every session re-invents setup, traceability, and fallback discipline.*

- [ ] **SCAF-01**: Presenter and audience find a Spanish-language series index at `docs/presentations/README.md` listing all 9 sessions with columns: `#`, date, slug, status (pending/rehearsed/delivered), 1-line abstract, link to session folder, `session-NN-pre`/`session-NN-post` tags when applicable
- [ ] **SCAF-02**: Presenter and audience find a shared setup guide at `docs/presentations/SETUP.md` (Spanish) covering Docker Compose, Payara 5 container, Postgres 15 init, Claude Code CLI, Node.js 20+, Marp CLI, Mermaid CLI, Ollama + `qwen2.5:7b` + `nomic-embed-text`, and `tools/email-rag/` corpus setup
- [ ] **SCAF-03**: A canonical Marp theme snippet is stored at `docs/presentations/THEME.md` and is referenced verbatim by every session deck's frontmatter
- [ ] **SCAF-04**: `docs/presentations/CLAUDE.md` is extended to document the new conventions: `YYYY-MM-DD-NN-<slug>/` folder naming (NN infix for ordered-arc sessions); per-session sidecar files `MANIFEST.md`, `HANDOUT.md`, `REHEARSAL.md`; annotated git tags `session-NN-pre` / `session-NN-post`; required cached-fallback asciinema/VHS artifact
- [ ] **SCAF-05**: Docker images in `docker-compose.yml` are pinned to immutable tags (no `latest`, no floating major-version tags) — per PITFALLS Pitfall 8
- [ ] **SCAF-06**: A `docs/presentations/CONCERNS-MAPPING.md` (or equivalent) pre-claims every HIGH/MED item in `.planning/codebase/CONCERNS.md` to a specific session (or marks it explicitly deferred/out-of-scope for this arc)
- [ ] **SCAF-07**: A shared primitives glossary slide deck (or includable Marp fragment) defines RAG, MCP, Skill, Agent, Hook, and Slash Command with one canonical slide per primitive; reused verbatim by every session that references the primitive
- [ ] **SCAF-08**: A `MANIFEST.md` template committed at `docs/presentations/MANIFEST.template.md` specifying: session title, date, pre/post tag SHAs, slide-name-to-commit map, recovery command (`git reset --hard session-NN-pre`), GitHub compare URL, known follow-ups
- [ ] **SCAF-09**: A `REHEARSAL.md` template committed at `docs/presentations/REHEARSAL.template.md` with the rehearsal checklist (model-ID pinned, same-day-run date, fallback-switch rehearsed, network plan, pre-warm steps)
- [ ] **SCAF-10**: A `HANDOUT.md` template committed at `docs/presentations/HANDOUT.template.md` — short Spanish audience takeaway with demo commit compare URL, next-session prereq, recommended reading

### S01 — Session 1: "Primero, un demo" (hook-first + primitives glossary)

- [ ] **S01-01**: Within the first 25 minutes, audience watches an end-to-end AI-assisted bug fix or small feature on a real legacy file pre-claimed from CONCERNS.md, resulting in at least one real commit landed on `master` live
- [ ] **S01-02**: Immediately after the demo, a "¿Qué acabamos de hacer?" decomposition slide names every primitive that was used (RAG, MCP, Skill, Agent, Hook, Command) without yet teaching them — vocabulary introduction only (Pitfall 18 guard)
- [ ] **S01-03**: The deck includes one explainer diagram per primitive (RAG, MCP, Skill, Agent, Hook, Command) showing data/control flow, stored as `.mmd` source with a committed pre-rendered `.svg` alongside
- [ ] **S01-04**: Diagrams support audience interactivity during live delivery — either CSS-only (hover/toggle) or local-JS-driven — AND each interactive diagram has a static pre-rendered SVG fallback so the deck remains correct on HTMLPreview.github.io (which rewrites `<script>` tags)
- [ ] **S01-05**: A series arc-preview slide names all 9 upcoming sessions with their topic + date
- [ ] **S01-06**: A bilingual-convention slide declares explicitly: "El deck y la narración están en español; la salida de Claude Code está en inglés — es el estado actual del ecosistema" (Pitfall 25)
- [ ] **S01-07**: Session folder `docs/presentations/YYYY-MM-DD-01-demo-primero/` contains: `.md` deck, rendered `.html`, Mermaid `.mmd` + pre-rendered `.svg` for each diagram, `MANIFEST.md`, `HANDOUT.md`, `REHEARSAL.md`, and a cached asciinema/VHS fallback of the live demo
- [ ] **S01-08**: Annotated git tags `session-01-pre` and `session-01-post` are pushed to the repo bracketing the live demo commit(s)

### S02 — Session 2: "Contexto, LLMs y la Ventana"

- [ ] **S02-01**: The theory block teaches context window anatomy (tokens, input-vs-output limits, what goes in, what gets evicted) illustrated by a visual diagram (pre-rendered SVG; interactive hover optional)
- [ ] **S02-02**: An LLM evolution timeline slide chains ChatGPT (Nov 2022) → transformers paper (2017, retroactively placed) → chain-of-thought → instruction-tuning (RLHF) → tool use → agentic workflows, with absolute dates
- [ ] **S02-03**: A hands-on demo ships a Python (or Node) script committed to the repo that calls Claude with structured output (JSON schema) or a confidence-scoring pattern and acts only if the schema validates / confidence exceeds a threshold; the script is run live against DotaChile code
- [ ] **S02-04**: A slide explains why Skills exist ("contexto bajo demanda, no inflar el system prompt") and refers back to the context-window diagram from S02-01
- [ ] **S02-05**: A slide explains why context-refreshing subagents matter (isolated context windows, parent sees only the return value) and previews how Session 6 will demo this on `TorneoService.java`
- [ ] **S02-06**: A "¿Cuándo confiar, cuándo verificar, cuándo rechazar?" slide lays out the mental model that governs determinism decisions for the rest of the arc
- [ ] **S02-07**: Session folder contents match S01-07 shape; tags `session-02-pre`/`session-02-post` bracket the script commit
- [ ] **S02-08**: The session does NOT claim to teach transformer internals or "how to build your own LLM" (Anti-feature X11)

### S03 — Session 3: RAG (on `tools/email-rag/`)

- [ ] **S03-01**: Live demo runs `tools/email-rag/.venv/bin/python tools/email-rag/search.py "<Spanish query>"` against the real redacted corpus at `../dotachile-emails/corpus` and surfaces a thread that cross-references a CONCERNS.md item (e.g., a PvpgnHash complaint, a signup issue mapping to AUTH debt)
- [ ] **S03-02**: The session teaches RAG by *subtraction* — reading `tools/email-rag/search.py` (~200 lines) as the canonical explainer — not by re-constructing a RAG pipeline from scratch
- [ ] **S03-03**: A contrast slide shows one query where BM25 and the semantic leg disagree (e.g., a short Spanish query where "torneo" appears literally vs. semantically), justifying hybrid search
- [ ] **S03-04**: A smoke-test slide at session start loads the 420 MB multilingual model once, on-screen, so the first live query doesn't stall for 5–15 s of dead air (Pitfall 13)
- [ ] **S03-05**: A toy corpus fixture is checked into `docs/presentations/<session-folder>/fixture/` so audience members without their own Gmail Takeout can replay the demo (Pitfall 17)
- [ ] **S03-06**: The session does NOT implement a fix for the issue RAG surfaced — it hands off to Sessions 5/6 (Skills + Agents) — demonstrating cross-session composition (CURR-01)
- [ ] **S03-07**: Session folder contents match S01-07 shape; tags `session-03-pre`/`session-03-post` bracket any demo commits
- [ ] **S03-08**: The handout (HANDOUT.md) specifies the Ollama model tag used (e.g., `nomic-embed-text:v1.5`) and notes that tags drift monthly (SUMMARY.md verify-before-session item)

### S04 — Session 4: MCP

- [ ] **S04-01**: Live demo wires a local MCP server (SQLite or Postgres MCP pointed at the DotaChile Postgres) using an `.mcp.json` file committed to the repo — not assembled live from memory
- [ ] **S04-02**: Claude Code lists tools exposed by the server on-screen and calls at least one tool (e.g., `mcp__postgres__query` listing the top 5 TODOs by entity count or similar DotaChile-shaped query)
- [ ] **S04-03**: The transcript panel shows the raw tool-call JSON payload once, explicitly labeled, so the audience sees MCP's on-the-wire shape
- [ ] **S04-04**: A framing slide positions MCP as "RAG's mental model, but for actions" — linking back to S03's external-context framing
- [ ] **S04-05**: A slide or handout line notes that `.mcp.json` schema was re-verified against docs.claude.com during the week of the session, with the checked URL and date (SUMMARY.md verify-before-session item)
- [ ] **S04-06**: A stretch demo (time-permitting) authors a minimal custom MCP server wrapping `dev-sync.sh` — shown as code, not necessarily executed — to demystify "MCP servers are just programs"
- [ ] **S04-07**: Session folder contents match S01-07 shape; `.mcp.json` is committed at repo root or `.claude/` (per repo convention); tags `session-04-pre`/`session-04-post` bracket the commit
- [ ] **S04-08**: The session names the MCP spec version and Claude Code CLI version it was authored against, pinned in the deck footer

### S05 — Session 5: Skills

- [ ] **S05-01**: Live-author a DotaChile-specific project-scoped Skill (candidate: `escape-false-guard` referencing the 5 `escape="false"` XSS sites in CONCERNS.md; or `pvpgn-hash-safety` referencing the weak-hash files) and commit it to `.claude/skills/`
- [ ] **S05-02**: A before/after demo shows that the same prompt produces different outcomes with the Skill installed vs not installed — proving Skill invocation is behavior-changing
- [ ] **S05-03**: A composition preview shows the new Skill composing with an already-installed Skill (e.g., `user-story`) — validating that Skills can chain
- [ ] **S05-04**: A teaching slide covers: `description:` frontmatter as the routing trigger, project vs user scope, `allowed-tools`, how the Skill body loads context on demand
- [ ] **S05-05**: A framing slide ties Skills back to S02's context-window mental model (loaded on demand, not stuffed into the system prompt)
- [ ] **S05-06**: Session folder contents match S01-07 shape; the new Skill directory under `.claude/skills/` is committed; tags `session-05-pre`/`session-05-post` bracket the Skill commit

### S06 — Session 6: Agents

- [ ] **S06-01**: Live demo spawns a subagent via the Task tool to read `src/java/com/dotachile/torneos/service/TorneoService.java` (the 1800-LOC god-class with 10 TODOs) and return a structured report listing each TODO with `file:line` reference and a 1-line classification (done / stale / actionable)
- [ ] **S06-02**: The parent context only ever sees the return value — this is shown explicitly on-screen (Claude Code's subagent-return view or equivalent) — illustrating the context-refreshing property from S02-05
- [ ] **S06-03**: The known bug `anthropics/claude-code#13898` (declaring `tools:` on a subagent strips MCP tools) is either demonstrated live OR named in a "the ecosystem is still raw" slide — honesty beat (Pitfall 12 prevention)
- [ ] **S06-04**: A stretch demo (time-permitting) spawns 3 parallel subagents — one per CONCERNS.md section (security / performance / fragile) — and the parent triages their 3 reports into a single "first-cuts" list
- [ ] **S06-05**: A token-cost mental model slide shows approximate context / output tokens spent per subagent invocation and compares to a monolithic-prompt alternative
- [ ] **S06-06**: Session folder contents match S01-07 shape; tags `session-06-pre`/`session-06-post` bracket any Task-tool-generated reports committed to the repo (e.g., as `.planning/reports/torneo-service-todos-<date>.md`)

### S07 — Session 7: Hooks

- [ ] **S07-01**: Live demo installs a PostToolUse hook that runs `mvn -o compile` on every Java file edit and feeds the compile errors back to Claude; the audience watches Claude self-correct a deliberate compile error
- [ ] **S07-02**: Live demo installs a PreToolUse hook blocking Edit/Write tool calls against off-limits legacy folders (`src/java/controller/` and `com.dotachile.automation.FunService` per CLAUDE.md guidance) and demonstrates the block firing
- [ ] **S07-03**: Both hooks are committed to `.claude/settings.json` and `.claude/hooks/` so a viewer at `session-07-pre` has everything session 7 needs without invisible state (Pitfall 21)
- [ ] **S07-04**: A teaching slide covers: STDIN JSON payload contract, exit-code semantics (0 = allow, non-zero = block for PreToolUse), `$CLAUDE_PROJECT_DIR` env var, matcher pipe-delimited syntax (e.g., `Edit|Write|MultiEdit`)
- [ ] **S07-05**: A slide or appendix re-verifies the full hook event catalog against docs.claude.com in the week of the session (SUMMARY.md Gap 1); only events verified in live docs are claimed on-screen
- [ ] **S07-06**: A framing slide positions hooks as "the deterministic layer around stochastic LLM output" — tying back to S02's determinism discussion
- [ ] **S07-07**: Session folder contents match S01-07 shape; tags `session-07-pre`/`session-07-post` bracket the hook commits

### S08 — Session 8: Slash Commands

- [ ] **S08-01**: Live-author a new project-scoped slash command `/dota-audit-xss` in `.claude/commands/` that loads `.planning/codebase/CONCERNS.md`, identifies the 5 `escape="false"` XSS sites, runs a triage prompt, and outputs a per-site table with recommended fix patterns
- [ ] **S08-02**: The new command is committed under `.claude/commands/`; a viewer at `session-08-pre` can run `/dota-audit-xss` after checkout
- [ ] **S08-03**: A composition demo invokes an existing GSD slash command (e.g., `/gsd-plan-phase` or `/gsd-new-milestone`) to show how commands orchestrate multiple agents and skills
- [ ] **S08-04**: A teaching slide covers: frontmatter fields (`name`, `description`, `argument-hint`, `allowed-tools`), `$ARGUMENTS` substitution, `@/absolute/path` include syntax
- [ ] **S08-05**: A contrast slide distinguishes Command (user-triggered entry point) vs Skill (loaded behavior on demand) — resolving the vocabulary ambiguity the audience will have after S05
- [ ] **S08-06**: Session folder contents match S01-07 shape; tags `session-08-pre`/`session-08-post` bracket the command commit

### S09 — Session 9: Capstone (Plugin / Workflow System)

- [ ] **S09-01**: A comparison slide deck covers three shapes of meta-workflow system — GSD (heavy, phase-driven, `.planning/` state), Superpowers (lightweight, spec+plan pairs), and a third comparator (Spec Kit if mature, or whichever tool is most credible at delivery time) — with a tradeoff rubric covering team size, overhead, learning curve, and primitive reuse
- [ ] **S09-02**: A live walk-through uses whichever system is selected at capstone planning time to plan a real CONCERNS.md item end-to-end; the planning artifacts are committed to the repo as proof of work
- [ ] **S09-03**: An architectural diagram shows how every primitive taught across Sessions 3–8 (RAG, MCP, Skills, Agents, Hooks, Commands) composes inside a meta-workflow system
- [ ] **S09-04**: An optional live-build of a minimum viable micro-plugin (1 skill + 1 agent + 1 hook + 1 command + 1 MCP wire) is attempted if session time allows — pre-recorded fallback exists per QUAL-02
- [ ] **S09-05**: The session does NOT declare a winner among GSD / Superpowers / Spec Kit — it teaches the tradeoffs; any "which is best?" question is answered with "it depends on team size and session count"
- [ ] **S09-06**: Plugin packaging / marketplace state and Spec Kit current state are re-verified 1–2 weeks before delivery; the deck footer names the verification date and sources (SUMMARY.md Gap 7)
- [ ] **S09-07**: Session folder contents match S01-07 shape; tags `session-09-pre`/`session-09-post` bracket the capstone commits
- [ ] **S09-08**: A closing slide names the "known follow-ups" for the series — what was not taught, what would be session 10+, what the audience should read next

### CURR — Curriculum-level / cross-session coverage

- [ ] **CURR-01**: A cross-session composition chain is demonstrable across the arc — an issue surfaced in Session 3 (RAG against email corpus) carries forward to Session 4 (MCP ticket creation) → Session 5 (Skill spec) → Session 6 (Agent implementation). The chain is shown explicitly, not discovered by the audience (FEATURES D6)
- [ ] **CURR-02**: Every session's opening slide references the preceding session (at minimum: "Ya vimos X, hoy vamos a Y") and every session's closing slide previews the next; the arc forms a visible spine
- [ ] **CURR-03**: The primitives glossary from SCAF-07 is reused *verbatim* across all sessions — definitions do not drift between Sessions 1, 3, 4, 5, 6, 7, 8
- [ ] **CURR-04**: A cloned repo at `session-09-post` can run every demo from every session reproducibly using the MANIFEST.md-driven setup — at least one end-to-end replay is performed after Session 9 to validate this (FEATURES D9)
- [ ] **CURR-05**: Session ordering honors the dependency chain: Demo (S1) → Theory (S2) → RAG (S3) → MCP (S4) → Skills (S5) → Agents (S6) → Hooks (S7) → Commands (S8) → Capstone (S9). Any deviation requires explicit justification in ROADMAP.md
- [ ] **CURR-06**: At least one session in the arc is a migration-slice (e.g., PvpgnHash → bcrypt dual-hash rollover, or a `escape="false"` XSS site fix, or an N+1 fix in `TorneoService`) — satisfying FEATURES D10 and PROJECT.md's "migration-as-teaching" key decision. The migration is sliced to fit one session; follow-up slices are explicitly tracked
- [ ] **CURR-07**: Audience tooling assumptions are consistent across the arc — every session assumes the setup from SCAF-02 and adds only what that session specifically requires (no "bring this new thing for today" in session 4 that wasn't in SETUP.md)

### QUAL — Quality gates (applied to every session)

- [ ] **QUAL-01**: Every session folder contains a completed `MANIFEST.md` naming: session title, date, pre-tag SHA, post-tag SHA, slide-name-to-commit map, recovery command (`git reset --hard session-NN-pre`), and GitHub compare URL
- [ ] **QUAL-02**: Every session has a pre-recorded asciinema (live capture) and/or VHS (scripted) fallback artifact committed next to the deck; the switchover from live run to fallback is rehearsed at least once, logged in REHEARSAL.md (PITFALLS Pitfall 1)
- [ ] **QUAL-03**: A same-day rehearsal (within 24 hours of delivery) is run on the same machine and network the presenter will use live; the rehearsal date and outcome are logged in REHEARSAL.md (Pitfall 1)
- [ ] **QUAL-04**: Every session includes a "Lo que la IA NO hizo" / "x10 honesty" slide near the end, naming at least one thing the presenter did (not the AI) and one thing that required manual correction during rehearsal (FEATURES S11 + D8)
- [ ] **QUAL-05**: Every session touches at least one file or artifact named in `.planning/codebase/CONCERNS.md` — Session 2 (Intro theory) is the only conditional exception, and its hands-on script should still exercise the real codebase (Pitfall 14 + FEATURES D5)
- [ ] **QUAL-06**: Every session declares the bilingual convention (deck and narration in Spanish; Claude Code tool output in English) on-screen at least once (Pitfall 25)
- [ ] **QUAL-07**: Every session's demo task is pre-sliced to fit a 57-minute on-paper budget: 5 intro + 10 concept + 12+12 demo + 8 recap + 10 Q&A (FEATURES S10 + Pitfall 6)
- [ ] **QUAL-08**: Every session that is a migration slice (per CURR-06) includes a "known follow-ups" closing slide and opens at least one GitHub issue / TODO for each deferred slice (Pitfall 30)
- [ ] **QUAL-09**: Every session pre-warms Payara + Postgres (via `docker compose up -d` and a dummy request) at least 10 minutes before session start; `docker compose up` is NEVER a live demo step (Pitfall 8)
- [ ] **QUAL-10**: Every session is strictly single-primitive in its *primary* teaching surface — composition across prior-taught primitives is allowed only after the current primitive has been introduced; multi-primitive salad in the first 30 minutes is forbidden (Pitfall 18)
- [ ] **QUAL-11**: Every session's deck is rendered to `.html` via Marp and committed alongside the `.md`; Mermaid diagrams are pre-rendered to `.svg` alongside their `.mmd` source (per `docs/presentations/CLAUDE.md` convention)
- [ ] **QUAL-12**: Every session's `MANIFEST.md` names the Claude Code CLI version, the model ID (e.g., `claude-opus-4-7`), and — where relevant — the Ollama / MCP server / Marp / Mermaid versions used at rehearsal and at delivery (Pitfall 1)

## v2 Requirements

Deferred to a future arc. Tracked so they're not mistaken for oversights.

### Extended Curriculum

- **CURR-v2-01**: A mid-arc composition session (e.g., between S05 and S06) dedicated to showing RAG + MCP + Skills composed before the capstone tackles composition at the plugin level
- **CURR-v2-02**: A standalone migration-slice session for PvpgnHash → bcrypt dual-hash rollover (currently folded into CURR-06 as a constraint, not a dedicated session)
- **CURR-v2-03**: A standalone performance session for the N+1 fix in `TorneoService` standings computation (currently reserved as a demo task bank item, not a dedicated phase)

### Audience / Format

- **AUD-v2-01**: English-language version of any session (currently out of scope — Spanish-first rule)
- **AUD-v2-02**: Recorded / edited video deliverable per session (currently out of scope — delivery is live only)
- **AUD-v2-03**: "Workshop-in-a-box" packaging — a cloneable-and-runnable archive for someone else to deliver the arc
- **AUD-v2-04**: Online asynchronous version (self-paced) with embedded LLM-graded exercises

### Meta

- **META-v2-01**: A "lessons-learned" retro session at the end of the arc, written up as a blog post — teaches arc-level lessons about running live AI-SWE workshops

## Out of Scope

Explicitly excluded. Documented here so that re-raising requires an explicit scope change.

| Feature | Reason |
|---------|--------|
| Reviving DotaChile as a live product | It's a lab, not a returning service (PROJECT.md out-of-scope) |
| Using a different / cleaner codebase for demos | Messiness IS the value (PROJECT.md out-of-scope) |
| Renaming Spanish packages/entities/views to English | Spanish-language rule, durable (CLAUDE.md) |
| Full wholesale stack modernization as a goal | Migration is a teaching topic, not a project outcome (PROJECT.md key decision) |
| Recording / broadcasting / LMS hosting pipelines | Delivery is live; post-production out of scope (PROJECT.md out-of-scope) |
| English-language decks, diagrams, or speaker notes | Spanish-first rule (PROJECT.md out-of-scope); planning docs may stay English |
| Long-lived teaching branches / alternate remotes | Demos land on `master` in this repo (PROJECT.md constraint) |
| Cloud-only LLM APIs in demos | Would exclude audience members without credentials (FEATURES Anti-feature) |
| Prompt-engineering-only content | Primitives are what's durable (FEATURES Anti-feature X2) |
| Greenfield / toy codebase demos | Defeats core value (FEATURES Anti-feature X1) |
| "Build your own ChatGPT" framing in Session 2 | Stay at the application layer (FEATURES Anti-feature X11) |
| Declaring a winner in the capstone meta-workflow comparison | Would defeat the teach-the-tradeoffs framing (S09-05) |

## Traceability

Every v1 requirement maps to exactly one phase. Cross-cutting requirements (CURR-*, QUAL-*) are assigned to the phase in which they first become enforceable and are **enforced in every subsequent phase** — see ROADMAP.md "Cross-Cutting Requirement Mapping" section for scope details.

| Requirement | Phase | Status |
|-------------|-------|--------|
| SCAF-01 | Phase 0 | Pending |
| SCAF-02 | Phase 0 | Pending |
| SCAF-03 | Phase 0 | Pending |
| SCAF-04 | Phase 0 | Pending |
| SCAF-05 | Phase 0 | Pending |
| SCAF-06 | Phase 0 | Pending |
| SCAF-07 | Phase 0 | Pending |
| SCAF-08 | Phase 0 | Pending |
| SCAF-09 | Phase 0 | Pending |
| SCAF-10 | Phase 0 | Pending |
| S01-01 | Phase 1 | Pending |
| S01-02 | Phase 1 | Pending |
| S01-03 | Phase 1 | Pending |
| S01-04 | Phase 1 | Pending |
| S01-05 | Phase 1 | Pending |
| S01-06 | Phase 1 | Pending |
| S01-07 | Phase 1 | Pending |
| S01-08 | Phase 1 | Pending |
| S02-01 | Phase 2 | Pending |
| S02-02 | Phase 2 | Pending |
| S02-03 | Phase 2 | Pending |
| S02-04 | Phase 2 | Pending |
| S02-05 | Phase 2 | Pending |
| S02-06 | Phase 2 | Pending |
| S02-07 | Phase 2 | Pending |
| S02-08 | Phase 2 | Pending |
| S03-01 | Phase 3 | Pending |
| S03-02 | Phase 3 | Pending |
| S03-03 | Phase 3 | Pending |
| S03-04 | Phase 3 | Pending |
| S03-05 | Phase 3 | Pending |
| S03-06 | Phase 3 | Pending |
| S03-07 | Phase 3 | Pending |
| S03-08 | Phase 3 | Pending |
| S04-01 | Phase 4 | Pending |
| S04-02 | Phase 4 | Pending |
| S04-03 | Phase 4 | Pending |
| S04-04 | Phase 4 | Pending |
| S04-05 | Phase 4 | Pending |
| S04-06 | Phase 4 | Pending |
| S04-07 | Phase 4 | Pending |
| S04-08 | Phase 4 | Pending |
| S05-01 | Phase 5 | Pending |
| S05-02 | Phase 5 | Pending |
| S05-03 | Phase 5 | Pending |
| S05-04 | Phase 5 | Pending |
| S05-05 | Phase 5 | Pending |
| S05-06 | Phase 5 | Pending |
| S06-01 | Phase 6 | Pending |
| S06-02 | Phase 6 | Pending |
| S06-03 | Phase 6 | Pending |
| S06-04 | Phase 6 | Pending |
| S06-05 | Phase 6 | Pending |
| S06-06 | Phase 6 | Pending |
| S07-01 | Phase 7 | Pending |
| S07-02 | Phase 7 | Pending |
| S07-03 | Phase 7 | Pending |
| S07-04 | Phase 7 | Pending |
| S07-05 | Phase 7 | Pending |
| S07-06 | Phase 7 | Pending |
| S07-07 | Phase 7 | Pending |
| S08-01 | Phase 8 | Pending |
| S08-02 | Phase 8 | Pending |
| S08-03 | Phase 8 | Pending |
| S08-04 | Phase 8 | Pending |
| S08-05 | Phase 8 | Pending |
| S08-06 | Phase 8 | Pending |
| S09-01 | Phase 9 | Pending |
| S09-02 | Phase 9 | Pending |
| S09-03 | Phase 9 | Pending |
| S09-04 | Phase 9 | Pending |
| S09-05 | Phase 9 | Pending |
| S09-06 | Phase 9 | Pending |
| S09-07 | Phase 9 | Pending |
| S09-08 | Phase 9 | Pending |
| CURR-01 | Phase 3 (enforced in Phases 3–6) | Pending |
| CURR-02 | Phase 1 (enforced in Phases 1–9) | Pending |
| CURR-03 | Phase 0 (established; enforced in Phases 1, 3–8) | Pending |
| CURR-04 | Phase 9 (post-delivery validation) | Pending |
| CURR-05 | Phase 1 (enforced in Phases 1–9) | Pending |
| CURR-06 | Phase 9 (verification; satisfied by ≥1 of Phases 1, 5, 6, 7, 8) | Pending |
| CURR-07 | Phase 1 (enforced in Phases 1–9) | Pending |
| QUAL-01 | Phase 0 (template; enforced in Phases 1–9) | Pending |
| QUAL-02 | Phase 0 (rule; enforced in Phases 1–9) | Pending |
| QUAL-03 | Phase 0 (rule; enforced in Phases 1–9) | Pending |
| QUAL-04 | Phase 0 (rule; enforced in Phases 1–9) | Pending |
| QUAL-05 | Phase 0 (rule; enforced in Phases 1–9) | Pending |
| QUAL-06 | Phase 1 (S01-06 baseline; enforced in Phases 1–9) | Pending |
| QUAL-07 | Phase 0 (rule; enforced in Phases 1–9) | Pending |
| QUAL-08 | Phase 0 (rule; enforced in migration-slice phase) | Pending |
| QUAL-09 | Phase 0 (Docker pinning + rule; enforced in Phases 1–9) | Pending |
| QUAL-10 | Phase 0 (rule; enforced in Phases 1–9) | Pending |
| QUAL-11 | Phase 0 (rule; enforced in Phases 1–9) | Pending |
| QUAL-12 | Phase 0 (template; enforced in Phases 1–9) | Pending |

**Coverage:**
- v1 requirements: 94 total (10 SCAF + 8+8+8+8+6+6+7+6+8 = 65 per-session + 7 CURR + 12 QUAL = 94)
- Mapped to phases: 94 / 94
- Unmapped: 0

---
*Requirements defined: 2026-04-19*
*Last updated: 2026-04-19 — traceability table filled with per-REQ phase mappings (roadmap creation)*
