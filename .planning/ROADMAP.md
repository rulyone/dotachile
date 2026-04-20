# Roadmap: DotaChile AI-SWE Workshop Series

**Created:** 2026-04-19
**Granularity:** standard (10 phases — Phase 0 scaffolding + 9 session phases)
**Coverage:** 94/94 v1 requirements mapped
**Constraints reference:** Spanish-language deliverables, 1-hour live session budget (57-min on-paper), all demos land on `master`

## Overview

This is a finite arc of **9 one-hour live workshops** (Sessions 1–9) preceded by a **Phase 0 of series-level scaffolding**. Each session pairs a Marp deck (Spanish) with a real demo committed to `master` of the legacy DotaChile Java EE codebase. Phase 0 establishes the discoverable, traceable, recoverable infrastructure (series index, shared SETUP, MANIFEST/HANDOUT/REHEARSAL templates, demo-task pre-claiming from CONCERNS.md, Docker tag pinning, primitives glossary) without which every session would re-invent setup. Session 1 opens with a hook-first live bug fix and introduces the primitives glossary. Session 2 rewinds to provide the conceptual scaffolding (context window, LLM evolution, deterministic LLM-assisted scripts) that every subsequent session leans on. Sessions 3–8 cover one primitive each (RAG → MCP → Skills → Agents → Hooks → Slash Commands) in dependency order. Session 9 (Capstone) compares meta-workflow systems (GSD / Superpowers / Spec Kit-or-newer) and composes every primitive into a working plugin shape. Cross-cutting CURR-* and QUAL-* requirements are enforced from their origin phase forward.

## Phases

**Phase Numbering:**
- Integer phases (0, 1, 2, ..., 9): Planned milestone work
- Decimal phases (e.g., 4.1): Reserved for urgent insertions via `/gsd-insert-phase` (none planned)

- [ ] **Phase 0: Series Scaffolding** - Establish series index, shared SETUP, sidecar templates, demo-task pre-claiming, Docker tag pinning, primitives glossary, QUAL gates
- [ ] **Phase 1: Session 1 — "Primero, un demo"** - Hook-first end-to-end live bug fix on real legacy file + named-primitives glossary + arc preview
- [ ] **Phase 2: Session 2 — "Contexto, LLMs y la Ventana"** - Context window mechanics, LLM evolution, deterministic LLM-assisted scripts, why Skills/refreshing-agents exist
- [ ] **Phase 3: Session 3 — RAG** - Live demo of `tools/email-rag/` against the real Spanish corpus; teach RAG by subtraction; cross-session composition chain origin
- [ ] **Phase 4: Session 4 — MCP** - Wire local MCP server (Postgres) against DotaChile DB; show tool-call JSON; "RAG mental model but for actions"
- [ ] **Phase 5: Session 5 — Skills** - Live-author DotaChile-specific Skill (`escape-false-guard` or `pvpgn-hash-safety`); composition with installed `user-story` skill
- [ ] **Phase 6: Session 6 — Agents** - Spawn subagent to investigate `TorneoService.java` 1800-LOC god-class; surface bug-13898 honesty beat
- [ ] **Phase 7: Session 7 — Hooks** - PostToolUse `mvn -o compile` + PreToolUse off-limits-folder block; deterministic layer around stochastic LLM
- [ ] **Phase 8: Session 8 — Slash Commands** - Live-author `/dota-audit-xss`; compose existing GSD commands; Command vs Skill clarification
- [ ] **Phase 9: Session 9 — Capstone (Plugin / Workflow System)** - Compare GSD / Superpowers / Spec Kit-or-newer; live-walk selected system; compose all primitives; reproducibility post-validation

## Phase Details

### Phase 0: Series Scaffolding
**Goal**: Without re-inventing it per session, every session inherits a discoverable index, a shared SETUP doc, a canonical Marp theme, sidecar templates (MANIFEST/HANDOUT/REHEARSAL), pinned Docker tags, a pre-claimed CONCERNS.md → session mapping, a reusable primitives glossary, and the QUAL gates that govern Phases 1–9.
**Depends on**: Nothing (first phase)
**Requirements**: SCAF-01, SCAF-02, SCAF-03, SCAF-04, SCAF-05, SCAF-06, SCAF-07, SCAF-08, SCAF-09, SCAF-10, QUAL-01, QUAL-02, QUAL-03, QUAL-04, QUAL-05, QUAL-06, QUAL-07, QUAL-08, QUAL-09, QUAL-10, QUAL-11, QUAL-12
**Success Criteria** (what must be TRUE):
  1. A new developer cloning the repo at `session-01-pre` can open `docs/presentations/README.md` (Spanish) and see all 9 sessions listed in order with date, status, abstract, and a link to each session's folder
  2. A new developer can follow `docs/presentations/SETUP.md` (Spanish) end-to-end and reach a working state — Docker Compose up, Payara + Postgres responsive, Claude Code installed, Marp/Mermaid CLIs available, Ollama with `qwen2.5:7b` + `nomic-embed-text` pulled, `tools/email-rag/` corpus ready
  3. A presenter starting any session NN copies `MANIFEST.template.md`, `HANDOUT.template.md`, and `REHEARSAL.template.md` into the session folder and the templates carry every QUAL-01/QUAL-12 field by construction (pre/post tag SHAs, slide-to-commit map, recovery command, model/CLI versions, fallback artifact pointer)
  4. `docker compose config` after Phase 0 shows zero floating tags (no `latest`, no major-version-only) — every image references an immutable digest or pinned semver (QUAL-09 + Pitfall 8 prevention)
  5. `docs/presentations/CONCERNS-MAPPING.md` (or equivalent) lists every HIGH/MED CONCERNS.md item with a "claimed by Session NN" or "deferred / out-of-scope" annotation; no HIGH/MED item is unclaimed when Session 1 begins
  6. The Marp-renderable primitives glossary fragment defines RAG, MCP, Skill, Agent, Hook, Command in one canonical slide each, and is referenced verbatim by every later session deck — definitions cannot drift (CURR-03 baseline established here)
**Plans**: TBD — expected ~6–8 (series index + SETUP + THEME + extended CLAUDE.md + Docker pinning + CONCERNS-MAPPING + glossary deck + sidecar templates batch)
**Notes**: QUAL-* gates are *established* here; they are *enforced* in Phases 1–9 and re-checked at every phase transition. Plans within Phase 0 can largely parallelize (templates, Docker pinning, CONCERNS-MAPPING, glossary) — only the series-index plan needs to follow the others (it summarizes them).

### Phase 1: Session 1 — "Primero, un demo"
**Goal**: Audience walks out with two things they didn't have at minute 0: (a) a vivid memory of an AI-assisted bug fix landing live on a real legacy file, and (b) the *vocabulary* (RAG, MCP, Skill, Agent, Hook, Command) plus a visual mental model for each, without having been *taught* any of them yet (Pitfall 18 guard).
**Depends on**: Phase 0 (series scaffolding must exist before Session 1 is authored — series index entry, SETUP.md, sidecar templates, glossary fragment, CONCERNS.md mapping all required)
**Requirements**: S01-01, S01-02, S01-03, S01-04, S01-05, S01-06, S01-07, S01-08, CURR-02, CURR-05, CURR-07
**Success Criteria** (what must be TRUE):
  1. Within the first 25 minutes of the live session, the audience watches an end-to-end AI-assisted bug fix on a CONCERNS.md-pre-claimed file resulting in at least one real commit landed on `master` (S01-01) — bracketed by the `session-01-pre` and `session-01-post` annotated git tags (S01-08)
  2. Audience members who weren't in the room can clone the repo at `session-01-pre`, follow the deck's MANIFEST.md-driven replay recipe, and reproduce (roughly) the same commit shown live (FEATURES D9 baseline)
  3. The deck's "¿Qué acabamos de hacer?" decomposition slide names every primitive used (RAG, MCP, Skill, Agent, Hook, Command) — vocabulary only, no teaching — and the explainer diagrams for each primitive load correctly both in live delivery (CSS/JS interactive where supported) AND on HTMLPreview.github.io (static pre-rendered SVG fallback) (S01-02, S01-03, S01-04)
  4. The deck includes an arc-preview slide naming all 9 upcoming sessions with topic + date (S01-05) and a bilingual-convention slide stating that decks/narration are Spanish while Claude Code output is English (S01-06, QUAL-06)
  5. The session folder `docs/presentations/YYYY-MM-DD-01-demo-primero/` is self-contained — `.md` deck, rendered `.html`, `.mmd`+`.svg` per diagram, `MANIFEST.md`, `HANDOUT.md`, `REHEARSAL.md`, and the cached asciinema/VHS fallback artifact all present (S01-07, QUAL-02, QUAL-11)
**Plans**: TBD — expected ~4–5 (deck authoring, demo prep + dry-run, sidecar manifest/handout/rehearsal, fallback recording, session-01-pre/post tag + commit dance)
**Notes**: Deck authoring can parallelize with the cached-fallback recording. Demo task is selected from CONCERNS.md HIGH/MED items pre-claimed in Phase 0 (SCAF-06). The bilingual-convention slide is the canonical baseline for QUAL-06 going forward.

### Phase 2: Session 2 — "Contexto, LLMs y la Ventana"
**Goal**: After the Session 1 demo earned the audience's trust, this session installs the conceptual scaffolding every subsequent session leans on — context window mechanics (the fundamental constraint), LLM evolution (Nov 2022 → today), and deterministic LLM-assisted scripting patterns (structured outputs, confidence scoring). The audience leaves with the mental model that lets Sessions 3–8 talk about Skills/Agents/Hooks without hand-waving about "why".
**Depends on**: Phase 1 (Session 1 arc-preview slide bridges into this session; bilingual convention from S01-06 carries forward; primitives vocabulary from S01-02 makes "context-window-aware primitives" a referent)
**Requirements**: S02-01, S02-02, S02-03, S02-04, S02-05, S02-06, S02-07, S02-08
**Success Criteria** (what must be TRUE):
  1. After the theory block, audience can describe what a context window is, what gets evicted from it, and why this constrains both Skills (loaded on demand) and refreshing subagents (isolated context) (S02-01, S02-04, S02-05)
  2. The LLM evolution timeline slide presents an absolute-dated chain — ChatGPT (Nov 2022) → transformers paper (2017, retroactively placed) → CoT → instruction-tuning (RLHF) → tool use → agentic workflows — without claiming to teach transformer internals or "build your own LLM" (S02-02, S02-08, FEATURES Anti-feature X11)
  3. A live-run Python or Node script committed to the repo calls Claude with structured output (JSON schema) or a confidence-scoring pattern, validates the response, and acts only if the schema validates / confidence exceeds a threshold — exercised against real DotaChile code on stage (S02-03, QUAL-05 satisfied via real-codebase touch)
  4. The "¿Cuándo confiar, cuándo verificar, cuándo rechazar?" mental-model slide is the explicit referent that Session 7 (Hooks as deterministic layer) will tie back to (S02-06)
  5. Session folder `docs/presentations/YYYY-MM-DD-02-intro-contexto-llms/` matches the S01-07 shape; `session-02-pre` / `session-02-post` tags bracket the script commit (S02-07)
**Plans**: TBD — expected ~4 (deck authoring, structured-output / confidence script authoring + dry-run, sidecar manifest/handout/rehearsal, fallback recording)
**Notes**: Phase 2 is the *foundation* for Phases 3–9 — every later phase will reference S02-01 (context window), S02-04 (Skills rationale), S02-05 (refreshing-agents rationale), or S02-06 (determinism mental model). Deck authoring + script authoring can parallelize. QUAL-05 honored via the script running against real DotaChile code (Session 2 is otherwise the most theory-heavy session — explicit safeguard in the requirements).

### Phase 3: Session 3 — RAG
**Goal**: The audience sees RAG demystified as "selective context injection" — `tools/email-rag/search.py` (~200 lines) is read on stage as the canonical explainer, then run live against the real Spanish redacted corpus to surface a thread that cross-references a CONCERNS.md item. The session does NOT fix the issue it surfaces — it hands off to Sessions 5/6, planting the cross-session composition chain (CURR-01) for the audience to track.
**Depends on**: Phase 2 (RAG is framed as "selective context injection" leaning on S02-01 context window mental model and S02-04 "loaded on demand" framing); Phase 1 (vocabulary established, arc spine continues)
**Requirements**: S03-01, S03-02, S03-03, S03-04, S03-05, S03-06, S03-07, S03-08, CURR-01
**Success Criteria** (what must be TRUE):
  1. Live demo runs `tools/email-rag/.venv/bin/python tools/email-rag/search.py "<Spanish query>"` against the corpus at `../dotachile-emails/corpus` and surfaces a thread that the deck explicitly maps to a CONCERNS.md item on-screen (S03-01, FEATURES D5)
  2. A contrast slide shows BM25 and the semantic leg disagreeing on at least one short Spanish query, justifying hybrid search (S03-03); the explanation reads `search.py` itself as the artifact, not a hand-drawn pipeline (S03-02, "teach by subtraction")
  3. A toy corpus fixture is checked into the session folder under `fixture/` so audience members without their own Gmail Takeout can replay the demo (S03-05, Pitfall 17 prevention)
  4. The session opens with a smoke-test slide that triggers the 420 MB multilingual model load on-screen, eliminating the 5–15 s of dead air on the first real query (S03-04, Pitfall 13 prevention); HANDOUT.md names the exact Ollama model tag used (S03-08)
  5. The session ends with an explicit hand-off slide — the surfaced issue is named, NOT fixed, and is tagged "Sessions 5 & 6 will pick this up" — establishing the visible cross-session composition chain (S03-06, CURR-01)
  6. Session folder shape matches S01-07; `session-03-pre` / `session-03-post` tags bracket any demo commits (S03-07)
**Plans**: TBD — expected ~4 (deck authoring + smoke-test slide, hand-off slide + composition-chain artifact, sidecar templates, fallback recording)
**Notes**: **CURR-01 chain originates here** — the issue surfaced in Session 3 is the load-bearing thread that carries through Session 4 (MCP filing the ticket if the chain decision picks that variant), Session 5 (Skill spec), Session 6 (Agent implementation). The chain is *named explicitly on-screen in each session*, not left for the audience to discover. **Research gate** (SUMMARY.md): Verify `nomic-embed-text` Ollama tag the week of delivery; tags drift monthly. Run `/gsd-research-phase 3` within 2 weeks of delivery.

### Phase 4: Session 4 — MCP
**Goal**: The audience sees MCP positioned as "RAG's mental model, but for actions" — a local MCP server (SQLite or Postgres-against-DotaChile-DB) is wired via `.mcp.json` committed to the repo, tools are listed on-screen, at least one tool is called, and the raw tool-call JSON payload is shown so the audience sees MCP's on-the-wire shape. The composition chain from Session 3 either continues here (if MCP files the ticket from the surfaced issue) or is named as still-active.
**Depends on**: Phase 3 (MCP framed against the "external context" mental model from RAG, S04-04); Phase 2 (context window framing); Phase 1 (vocabulary, arc spine)
**Requirements**: S04-01, S04-02, S04-03, S04-04, S04-05, S04-06, S04-07, S04-08
**Success Criteria** (what must be TRUE):
  1. Live demo: `.mcp.json` committed to the repo wires a local MCP server (SQLite or Postgres against the DotaChile DB); Claude Code lists tools exposed on-screen and calls at least one (e.g., `mcp__postgres__query` returning DotaChile-shaped data) (S04-01, S04-02)
  2. The transcript panel shows the raw tool-call JSON payload at least once, explicitly labeled as such, so the audience sees MCP's on-the-wire shape (S04-03)
  3. A framing slide positions MCP as "RAG's mental model, but for actions" — explicit visual link back to S03 (S04-04)
  4. A slide or HANDOUT line names the MCP spec version, the Claude Code CLI version this session was authored against, AND the date+URL of the docs.claude.com re-verification done in the week of delivery (S04-05, S04-08)
  5. (Stretch — time-permitting) A minimal custom MCP server wrapping `dev-sync.sh` is shown as code, demonstrating "MCP servers are just programs" (S04-06)
  6. Session folder shape matches S01-07; `.mcp.json` is committed at repo root or `.claude/`; `session-04-pre` / `session-04-post` tags bracket the commit (S04-07)
**Plans**: TBD — expected ~4 (deck authoring, MCP wiring + dry-run, sidecar templates + manifest version-pinning, fallback recording)
**Notes**: **Research gate (SUMMARY.md Gap 2):** `.mcp.json` schema, transport options, and official server package names must be re-verified against docs.claude.com **within 2 weeks of delivery**. Run `/gsd-research-phase 4` to refresh; default to `server-filesystem` (most stable) and upgrade only after verification. The deck footer pins the verification date — Pitfall 20 prevention. Stretch demo (custom MCP wrapping `dev-sync.sh`) parallelizable with main demo prep.

### Phase 5: Session 5 — Skills
**Goal**: The audience sees a DotaChile-specific Skill live-authored and committed to `.claude/skills/`, then the same prompt produces *different* outcomes with the Skill installed vs not — proving Skill invocation is behavior-changing. A composition preview shows the new Skill chaining with an already-installed Skill (`user-story`). Skills are explicitly tied back to S02's context-window framing as "the mechanism for loading context on demand without bloating the system prompt."
**Depends on**: Phase 4 (after MCP, Skills become "named, versioned recipes that orchestrate MCP calls"); Phase 2 (context window framing, S02-04 "Skills exist for context-on-demand"); Phase 1 (vocabulary, arc spine)
**Requirements**: S05-01, S05-02, S05-03, S05-04, S05-05, S05-06
**Success Criteria** (what must be TRUE):
  1. Live demo authors and commits a DotaChile-specific Skill to `.claude/skills/` — the candidate is `escape-false-guard` (referencing the 5 `escape="false"` XSS sites in CONCERNS.md) or `pvpgn-hash-safety` (referencing the weak-hash files), pre-decided in plan-phase (S05-01, FEATURES D5 / QUAL-05)
  2. A before/after demo proves the Skill is behavior-changing — the same prompt produces a different outcome with the Skill installed vs. not (S05-02)
  3. A composition demo shows the new Skill working alongside the already-installed `user-story` skill — Skills compose, not just stand alone (S05-03)
  4. A teaching slide covers `description:` frontmatter as routing trigger, project vs user scope, `allowed-tools`, and how the Skill body loads context on demand (S05-04)
  5. A framing slide ties Skills back to S02's context-window mental model — "loaded on demand, not stuffed into system prompt" (S05-05)
  6. Session folder shape matches S01-07; the new Skill directory is committed under `.claude/skills/`; `session-05-pre` / `session-05-post` tags bracket the Skill commit (S05-06)
**Plans**: TBD — expected ~4 (deck authoring, Skill authoring + before/after dry-run, composition demo prep, sidecar + fallback)
**Notes**: Skill candidate selection (`escape-false-guard` vs `pvpgn-hash-safety`) finalized in plan-phase based on which CONCERNS.md item maps cleanest to a 25-min demo. **CURR-01 chain continues** — if Session 3's surfaced issue maps to one of the Skill targets, this is the Skill-spec step in the chain; the deck names it explicitly.

### Phase 6: Session 6 — Agents
**Goal**: The audience sees a subagent spawned via the Task tool to read `TorneoService.java` (the 1800-LOC god-class with 10 TODOs) and return a structured per-TODO report. The parent context is shown to only ever see the return value — explicit on-screen demonstration of the context-refreshing property from S02-05. The known bug `anthropics/claude-code#13898` is either demonstrated live OR named as a "the ecosystem is still raw" honesty beat. Optional stretch: 3 parallel subagents against CONCERNS.md sections.
**Depends on**: Phase 5 (subagents load Skills — composition foundation); Phase 2 (context-refreshing mental model from S02-05 is the load-bearing referent); Phase 1 (vocabulary, arc spine)
**Requirements**: S06-01, S06-02, S06-03, S06-04, S06-05, S06-06
**Success Criteria** (what must be TRUE):
  1. Live demo spawns a subagent via the Task tool to read `src/java/com/dotachile/torneos/service/TorneoService.java` and return a structured report listing each of the 10 TODOs with `file:line` reference and a 1-line classification (done / stale / actionable) (S06-01, FEATURES D5/QUAL-05)
  2. The parent context is shown explicitly to only see the return value — Claude Code's subagent-return view is on-screen — illustrating the context-refreshing property from S02-05 (S06-02)
  3. The known bug `anthropics/claude-code#13898` (declaring `tools:` on a subagent strips MCP tools) is either demonstrated live OR named in a "the ecosystem is still raw" slide — honesty beat per FEATURES D8/QUAL-04 (S06-03, Pitfall 12 prevention)
  4. A token-cost mental model slide shows approximate context/output tokens spent per subagent invocation vs. a monolithic-prompt alternative (S06-05)
  5. (Stretch — time-permitting) 3 parallel subagents — one per CONCERNS.md section (security / performance / fragile) — produce 3 reports that the parent triages into a single "first-cuts" list (S06-04)
  6. Session folder shape matches S01-07; subagent-generated reports committed (e.g., as `.planning/reports/torneo-service-todos-<date>.md`); `session-06-pre` / `session-06-post` tags bracket the report commits (S06-06)
**Plans**: TBD — expected ~4 (deck authoring, subagent script + dry-run, parallel-subagent stretch demo, sidecar + fallback)
**Notes**: **CURR-01 chain culmination** — if the Session 3 → Session 5 chain has been an implementation thread, Session 6 is where the Agent implements the fix. The Skill from Session 5 is invoked by the subagent here — composition, not re-introduction (Pitfall 18). Honesty about bug-13898 is non-negotiable per QUAL-04. Stretch (parallel subagents) parallelizable with main subagent demo prep.

### Phase 7: Session 7 — Hooks
**Goal**: The audience watches a PostToolUse hook running `mvn -o compile` on every Java edit feed compile errors back to Claude — Claude self-corrects a deliberate compile error live. A second PreToolUse hook blocks Edit/Write tool calls against off-limits legacy folders (`src/java/controller/`, `com.dotachile.automation.FunService`) per CLAUDE.md, demonstrating the deterministic guardrail. Hooks are framed as "the deterministic layer around stochastic LLM output" — explicit tie-back to S02-06.
**Depends on**: Phase 6 (Hooks are most vivid wrapping an Agent boundary; demo composes); Phase 2 (S02-06 determinism mental model is the load-bearing framing referent); Phase 1 (vocabulary, arc spine)
**Requirements**: S07-01, S07-02, S07-03, S07-04, S07-05, S07-06, S07-07
**Success Criteria** (what must be TRUE):
  1. Live demo: PostToolUse hook running `mvn -o compile` on every Java file edit feeds compile errors back to Claude; the audience watches Claude self-correct a deliberate compile error (S07-01)
  2. Live demo: PreToolUse hook blocks Edit/Write tool calls against off-limits legacy folders (`src/java/controller/`, `com.dotachile.automation.FunService` per CLAUDE.md guidance) — the block fires on stage (S07-02, FEATURES D5/QUAL-05)
  3. Both hooks are committed to `.claude/settings.json` and `.claude/hooks/` — a viewer at `session-07-pre` has everything Session 7 needs without invisible state (S07-03, Pitfall 21 prevention)
  4. A teaching slide covers STDIN JSON payload contract, exit-code semantics (0 = allow, non-zero = block for PreToolUse), `$CLAUDE_PROJECT_DIR` env var, matcher pipe-delimited syntax (`Edit|Write|MultiEdit`) (S07-04)
  5. A slide or appendix names the full hook event catalog re-verified against docs.claude.com in the week of delivery, with the date and URL on-screen — only events confirmed in live docs are claimed (S07-05)
  6. A framing slide positions hooks as "the deterministic layer around stochastic LLM output" — explicit tie-back to S02-06 (S07-06)
  7. Session folder shape matches S01-07; `session-07-pre` / `session-07-post` tags bracket the hook commits (S07-07)
**Plans**: TBD — expected ~4 (deck authoring, two hooks authoring + dry-runs, sidecar + manifest version-pinning, fallback recording)
**Notes**: **Research gate (SUMMARY.md Gap 1):** Full hook event catalog beyond the three verified in this repo (SessionStart, PreToolUse, PostToolUse) must be re-verified against docs.claude.com **within 2 weeks of delivery**. Run `/gsd-research-phase 7` to refresh; only events confirmed in live docs are claimed on-screen. Training data suggests UserPromptSubmit, Notification, Stop, SubagentStop, PreCompact exist — verify before claiming. Hook authoring (PostToolUse + PreToolUse) parallelizable.

### Phase 8: Session 8 — Slash Commands
**Goal**: The audience watches a new project-scoped slash command `/dota-audit-xss` get authored live in `.claude/commands/`, loaded against `.planning/codebase/CONCERNS.md`, and produce a per-site triage table for the 5 `escape="false"` XSS sites. A composition demo invokes an existing GSD slash command (e.g., `/gsd-plan-phase` or `/gsd-new-milestone`) to show how Commands orchestrate multiple agents and skills. The Command-vs-Skill ambiguity from Session 5 is resolved (Command = user-triggered entry point; Skill = loaded behavior on demand).
**Depends on**: Phase 7 (Commands compose Skills + Agents + Hooks — Hooks need to exist as a referent); Phases 5 & 6 (Skills + Agents are what Commands compose); Phase 1 (vocabulary, arc spine)
**Requirements**: S08-01, S08-02, S08-03, S08-04, S08-05, S08-06
**Success Criteria** (what must be TRUE):
  1. Live demo authors a new project-scoped slash command `/dota-audit-xss` in `.claude/commands/` that loads `.planning/codebase/CONCERNS.md`, identifies the 5 `escape="false"` XSS sites, runs a triage prompt, and outputs a per-site table with recommended fix patterns (S08-01, FEATURES D5/QUAL-05)
  2. The new command is committed under `.claude/commands/`; a viewer at `session-08-pre` can run `/dota-audit-xss` after checkout (S08-02, Pitfall 21 prevention)
  3. A composition demo invokes an existing GSD slash command (e.g., `/gsd-plan-phase` or `/gsd-new-milestone`) to show Commands orchestrating multiple agents and skills (S08-03)
  4. A teaching slide covers frontmatter fields (`name`, `description`, `argument-hint`, `allowed-tools`), `$ARGUMENTS` substitution, `@/absolute/path` include syntax (S08-04)
  5. A contrast slide distinguishes Command (user-triggered entry point) vs Skill (loaded behavior on demand) — resolving the vocabulary ambiguity introduced by S05 (S08-05)
  6. Session folder shape matches S01-07; `session-08-pre` / `session-08-post` tags bracket the command commit (S08-06)
**Plans**: TBD — expected ~3–4 (deck authoring, `/dota-audit-xss` authoring + dry-run, GSD-composition demo prep, sidecar + fallback)
**Notes**: Composition demo is the payoff slide of Sessions 1–7 — the existing GSD command being invoked has all primitives visible at once. UI-relevant aspect: none directly (Commands operate at CLI/transcript layer; XHTML is just the content being audited).

### Phase 9: Session 9 — Capstone (Plugin / Workflow System)
**Goal**: The audience sees three shapes of meta-workflow systems compared (GSD / Superpowers / Spec Kit-or-newer), watches whichever was selected at planning time used live to plan a real CONCERNS.md item with planning artifacts committed, sees an architectural diagram of how every primitive from Sessions 3–8 composes inside a meta-workflow system, and (optionally) watches a minimum viable micro-plugin built (1 skill + 1 agent + 1 hook + 1 command + 1 MCP wire). No winner is declared; a closing slide names known follow-ups for the series. Post-session validation: a clone at `session-09-post` can reproduce every demo from every session.
**Depends on**: All prior phases (Phases 0 through 8) — the capstone composes every primitive taught and uses scaffolding established in Phase 0
**Requirements**: S09-01, S09-02, S09-03, S09-04, S09-05, S09-06, S09-07, S09-08, CURR-04, CURR-06
**Success Criteria** (what must be TRUE):
  1. A comparison slide deck covers three shapes of meta-workflow system — GSD (heavy, phase-driven, `.planning/` state), Superpowers (lightweight, spec+plan pairs), and a third comparator (Spec Kit if mature, or whichever is most credible at delivery time) — with a tradeoff rubric covering team size, overhead, learning curve, and primitive reuse (S09-01)
  2. A live walk-through uses the selected system to plan a real CONCERNS.md item end-to-end; the planning artifacts are committed as proof of work (S09-02, FEATURES D5/QUAL-05)
  3. An architectural diagram shows how every primitive taught across Sessions 3–8 (RAG, MCP, Skills, Agents, Hooks, Commands) composes inside a meta-workflow system (S09-03)
  4. (Optional, time-permitting) A minimum viable micro-plugin (1 skill + 1 agent + 1 hook + 1 command + 1 MCP wire) is live-built; pre-recorded fallback exists per QUAL-02 (S09-04)
  5. The session does NOT declare a winner among GSD / Superpowers / Spec Kit — any "which is best?" question is answered with "it depends on team size and session count" (S09-05)
  6. A closing slide names known follow-ups for the series — what was not taught, what would be Session 10+, what the audience should read next (S09-08)
  7. Session folder shape matches S01-07; `session-09-pre` / `session-09-post` tags bracket the capstone commits (S09-07)
  8. **Post-delivery validation:** A cloned repo at `session-09-post` can run every demo from every session reproducibly using the MANIFEST.md-driven setup — at least one end-to-end replay is performed and logged (CURR-04, FEATURES D9)
  9. **Migration-slice coverage:** At least one phase across Sessions 1–9 has been a migration slice (PvpgnHash → bcrypt rollover slice, an `escape="false"` XSS fix, or N+1 fix in `TorneoService`); follow-up slices are explicitly tracked in the migration session's QUAL-08 closing slide (CURR-06, PROJECT.md "migration-as-teaching" key decision satisfied)
**Plans**: TBD — expected ~5–6 (deck authoring + comparison rubric, system-selection decision artifact, live-walkthrough demo prep, optional micro-plugin demo + fallback, closing-slide + known-follow-ups, post-delivery reproducibility validation as separate plan)
**Notes**:
- **OPEN DECISION POINT (Phase 9 plan-phase):** Which meta-workflow system to use as the *primary* live-walkthrough demo? Three candidates:
  1. **GSD** — heaviest (35 agents, 40+ commands, `.planning/` state, hook-enforced invariants); impressive but intimidating; already installed in this repo
  2. **Superpowers** — lightweight (spec+plan pairs in `docs/superpowers/`); accessible but might feel thin for a capstone; prior art exists in repo
  3. **Spec Kit** (or another tool that is mature by delivery) — neutral scaffolding; LOW confidence on 2026 state
  - **Decision criteria (SUMMARY.md Gap 7):**
    (a) Which system has the audience actually seen used across the arc?
    (b) Which is most mature by the delivery date?
    (c) Whether a live "build a mini-plugin" demo is more compelling than a "walk an existing system" demo?
  - **Decision must be made at Phase 9 plan-phase, not earlier** — ecosystem moves fast.
- **Research gate (SUMMARY.md Gap 7 + Gap 3):** Plugin packaging/marketplace state and Spec Kit current state must be re-verified **1–2 weeks before delivery**. Run `/gsd-research-phase 9` to refresh. The deck footer names the verification date and sources (S09-06).
- **CURR-04 (reproducibility) is post-delivery work** — cloned-repo end-to-end replay happens after Session 9 ships and is its own plan within Phase 9.
- **CURR-06 (migration-slice) is enforced here** but satisfied earlier — Phase 9 plan-phase verifies at least one prior phase (1, 5, 6, 7, or 8) was a migration slice; if not, Phase 9 itself adds one.
- Plans: deck authoring, comparison rubric, and architectural-diagram authoring can parallelize.

## Cross-Cutting Requirement Mapping

**CURR-* (Curriculum-level / cross-session)** — mapped to the FIRST phase in which they become enforceable; enforced in every subsequent phase:

| Requirement | Origin Phase | Enforcement Scope |
|-------------|-------------|-------------------|
| CURR-01 (cross-session composition chain) | Phase 3 (RAG surfaces issue) | Phases 3 → 4 → 5 → 6, named on-screen in each |
| CURR-02 (every session opens with "ya vimos X" / closes with "próxima") | Phase 1 | Phases 1–9 (every session deck) |
| CURR-03 (primitives glossary reused verbatim) | Phase 0 (glossary established via SCAF-07) | Phases 1, 3, 4, 5, 6, 7, 8 (every session referencing primitives) |
| CURR-04 (cloned-repo reproducibility) | Phase 9 (post-delivery validation) | Validates Phases 1–9 collectively |
| CURR-05 (session ordering honors dependency chain) | Phase 1 | Phases 1–9 (deviation requires ROADMAP.md justification — none planned) |
| CURR-06 (at least one migration-slice session) | Phase 9 (verification) | Satisfied by ≥1 of Phases 1, 5, 6, 7, 8 (decision in plan-phase) |
| CURR-07 (consistent tooling assumptions) | Phase 1 | Phases 1–9 (all assume SCAF-02 SETUP.md) |

**QUAL-* (Quality gates)** — established in Phase 0; enforced in Phases 1–9:

| Requirement | Origin Phase | Enforcement Scope |
|-------------|-------------|-------------------|
| QUAL-01 (completed MANIFEST.md per session) | Phase 0 (template) | Phases 1–9 (every session folder) |
| QUAL-02 (pre-recorded asciinema/VHS fallback + rehearsed switchover) | Phase 0 (rule) | Phases 1–9 |
| QUAL-03 (same-day rehearsal within 24h) | Phase 0 (rule) | Phases 1–9 |
| QUAL-04 ("Lo que la IA NO hizo" / x10 honesty slide) | Phase 0 (rule) | Phases 1–9 |
| QUAL-05 (every session touches CONCERNS.md file/artifact) | Phase 0 (rule) | Phases 1–9 (Phase 2 satisfied via real-codebase script per S02-03) |
| QUAL-06 (bilingual convention declared on-screen) | Phase 1 (S01-06 establishes baseline) | Phases 1–9 |
| QUAL-07 (57-min on-paper budget) | Phase 0 (rule) | Phases 1–9 |
| QUAL-08 (migration-slice "known follow-ups" slide + GitHub issues) | Phase 0 (rule) | Whichever phases are migration slices |
| QUAL-09 (Payara + Postgres pre-warmed 10min before; never `docker compose up` live) | Phase 0 (Docker tag pinning + rule) | Phases 1–9 |
| QUAL-10 (single-primitive primary teaching surface; no multi-primitive salad in first 30min) | Phase 0 (rule) | Phases 1–9 (composition allowed only after primitive introduced) |
| QUAL-11 (Marp `.html` + Mermaid `.svg` rendered + committed) | Phase 0 (rule, inherits docs/presentations/CLAUDE.md) | Phases 1–9 |
| QUAL-12 (MANIFEST names CLI version + model ID + tool versions) | Phase 0 (template) | Phases 1–9 |

## Research Gates Summary

Three phases require re-verification within 1–2 weeks of delivery (per SUMMARY.md Gaps 1, 2, 3, 7):

| Phase | Research Action | Trigger |
|-------|----------------|---------|
| Phase 3 (RAG) | Verify Ollama `nomic-embed-text` and `qwen2.5:7b` tags (drift monthly) | Run `/gsd-research-phase 3` ≤2 weeks before delivery |
| Phase 4 (MCP) | Re-read docs.claude.com `.mcp.json` schema, transport options, official server package names | Run `/gsd-research-phase 4` ≤2 weeks before delivery |
| Phase 7 (Hooks) | Re-read docs.claude.com hooks event catalog (events beyond the 3 verified in-repo) | Run `/gsd-research-phase 7` ≤2 weeks before delivery |
| Phase 9 (Capstone) | Plugin packaging/marketplace state + Spec Kit current state + meta-workflow-system selection decision | Run `/gsd-research-phase 9` 1–2 weeks before delivery |

Phases 0, 1, 2, 5, 6, 8 are STANDARD — no external research re-verification needed.

## Parallelization Notes

Per `config.json` `parallelization: true`, plans within a phase that are independent can run in parallel:

| Phase | Parallelizable Plans (candidates) |
|-------|-----------------------------------|
| 0 | Templates (MANIFEST/HANDOUT/REHEARSAL), Docker pinning, CONCERNS-MAPPING, glossary deck, THEME.md — all independent (series index plan should follow as it summarizes them) |
| 1 | Deck authoring + cached-fallback recording |
| 2 | Deck authoring + structured-output script authoring |
| 3 | Deck authoring + smoke-test slide + composition-chain hand-off slide |
| 4 | Main MCP wiring demo + stretch custom-MCP-wrapping-`dev-sync.sh` demo |
| 5 | Skill authoring + composition-with-`user-story` demo prep |
| 6 | Single-subagent demo + parallel-subagents stretch demo |
| 7 | PostToolUse hook + PreToolUse hook (independent hook scripts) |
| 8 | `/dota-audit-xss` authoring + GSD-composition demo prep |
| 9 | Comparison rubric + architectural diagram + selected-system live-walkthrough prep |

Sequential constraint across phases: phase ordering (Phase N depends on Phase N-1 minimum, plus stated upstream dependencies) must be honored — sessions are delivered live in arc order.

## Progress

**Execution Order:**
Phases execute in numeric order: 0 → 1 → 2 → 3 → 4 → 5 → 6 → 7 → 8 → 9

| Phase | Plans Complete | Status | Completed |
|-------|----------------|--------|-----------|
| 0. Series Scaffolding | 0/TBD | Not started | - |
| 1. Session 1 — Primero, un demo | 0/TBD | Not started | - |
| 2. Session 2 — Contexto, LLMs y la Ventana | 0/TBD | Not started | - |
| 3. Session 3 — RAG | 0/TBD | Not started | - |
| 4. Session 4 — MCP | 0/TBD | Not started | - |
| 5. Session 5 — Skills | 0/TBD | Not started | - |
| 6. Session 6 — Agents | 0/TBD | Not started | - |
| 7. Session 7 — Hooks | 0/TBD | Not started | - |
| 8. Session 8 — Slash Commands | 0/TBD | Not started | - |
| 9. Session 9 — Capstone | 0/TBD | Not started | - |

---

*Roadmap created: 2026-04-19*
*Coverage: 94/94 v1 requirements mapped (10 SCAF + 8+8+8+8+6+6+7+6+8 = 65 per-session + 7 CURR + 12 QUAL = 94)*
