# Project Research Summary

**Project:** DotaChile AI-SWE Workshop Series
**Domain:** Live technical workshop series — AI-assisted software engineering on a legacy Java EE codebase
**Researched:** 2026-04-19
**Confidence:** HIGH for repo-local decisions; MEDIUM for external ecosystem claims

---

## Executive Summary

This is a finite arc of 4-8 one-hour live workshops teaching AI-assisted software engineering to a mixed-seniority Spanish-speaking audience (working devs, senior/staff engineers, near-graduates). The teaching surface is the DotaChile legacy Java EE codebase — Java 8, JSF 2, PrimeFaces 4, EJB 3, JPA 2, Payara 5, PostgreSQL 15 — which is end-of-life and genuinely messy. That messiness is the asset: every demo that survives contact with TorneoService.java (1800 LOC, 10 TODOs) is more credible than a greenfield equivalent.

The arc opens with a live end-to-end bug fix on the real codebase before any theory is introduced — the strongest possible hook for a mixed-seniority audience ("we're not going to tell you what AI can do; we're going to show you in the first five minutes"). Session 2 then rewinds to provide the conceptual scaffolding: context window mechanics, why it dictates Skills and context-refreshing agents, how to make LLM-assisted scripts deterministic through structured outputs and confidence validation. Subsequent sessions cover one primitive per session (RAG, MCP, Skill, Agent, Hook, Command) building toward a capstone that composes them. Every session lands a real commit on master, tagged session-NN-pre/session-NN-post, traceable via MANIFEST.md in the session folder.

The toolchain is settled: Marp + pre-rendered Mermaid SVG for slides, git annotated tags for traceability, asciinema/VHS for demo artifacts, Ollama for local LLM sidebars, and the existing tools/email-rag/ pipeline as the RAG demo surface. The primitives curriculum is grounded in what is actually installed in this repo at commit 17ac744 — GSD system (35 agents, 40+ commands), skills, working hook pipeline. The top risks are time management, AI flake mid-demo, and multi-primitive overload. All three have concrete single-instrument mitigations enforced at roadmap time.

One open question is not settled: whether the capstone/meta-workflow comparison should be anchored on GSD, Superpowers, or an external lightweight option (Spec Kit, or another tool that has emerged). This must be resolved at capstone-session planning time based on what the audience has seen and what tooling is mature by then. The research documents all three shapes; no winner is declared.

---

## Key Findings

### Recommended Stack

The stack is largely pre-decided: the Marp pipeline already exists and has two reference decks to draw from; the Claude Code primitives are already installed in .claude/; the RAG pipeline is already in tools/email-rag/; Docker Compose already runs the DotaChile app. Nothing needs to be chosen — the work is to establish authoring conventions and a per-session folder structure on top of what exists.

**Core technologies:**

- **Marp CLI ^4.x + Mermaid CLI ^11.x:** Markdown to HTML/PDF slides + pre-rendered SVG diagrams. Already adopted (docs/presentations/CLAUDE.md). Mermaid MUST be pre-rendered — HTMLPreview.github.io rewrites script tags, breaking runtime Mermaid. Non-negotiable.
- **Claude Code CLI (latest stable):** The AI the workshop teaches. Skills, Agents (Task tool), Hooks, MCP, and slash Commands all present in this repo at commit 17ac744. The live demo surface IS the teaching surface.
- **Ollama ^0.5.x + qwen2.5:7b:** Local LLM runtime for RAG sidebars. Spanish-friendly; fits a 16 GB MacBook. No cloud credentials for follow-along.
- **rank_bm25 + sentence-transformers (multilingual MiniLM):** The existing tools/email-rag/search.py hybrid pipeline — 200 lines, BM25 + semantic leg, Spanish-first, no cloud. Teach RAG by subtraction from this script, not construction from scratch.
- **asciinema ^2.4 + VHS (charmbracelet):** Terminal recording. asciinema for live captures; VHS for scripted pre-recorded fallbacks. Both output diff-able text files.
- **GSD workflow system (.claude/get-shit-done/, hook v1.38.0):** A capstone comparison subject (one of three shapes). 35 agents, 40+ commands, hook-enforced invariants. Whether it is the *primary* capstone demo vs Superpowers vs another tool is an open question — see Gaps below.

**What NOT to use:** LangChain/LlamaIndex/Chroma (bury the RAG mental model), reveal.js/Slidev (requires re-authoring two existing decks), cloud-only LLM APIs in demos (excludes participants, reliability hazard), long-lived teaching branches (explicit PROJECT.md out-of-scope).

**Verify before each session:** Ollama model tags drift monthly; Claude Code primitive behavior re-check against docs.claude.com before each relevant session; Spec Kit 2026 state is LOW confidence.

---

### Expected Features

Every session must ship 11 table-stakes items; the series must ship 10 cross-series items.

**Must have per session (S-series):**
- **S1 — Pinned SHA on title slide:** audience can git checkout the exact pre-demo state. Without this, "commits are real" is unverifiable.
- **S3 — Real commit landed on master live:** the project core value. Sessions with zero commits are slides-only.
- **S4 — Backup asciinema + pre-baked fallback:** live AI flake is not an if, it is a when. Every demo has a pre-recorded fallback checked in next to the deck. The switch must be rehearsed.
- **S10 — Demo task pre-sized to 45 min execution + 15 min setup/recap:** over-scoping is the #1 cause of blown budgets. Tasks sourced from .planning/codebase/CONCERNS.md.
- **S11 — "Lo que la IA NO hizo" slide:** honesty beat near the end; the 2026-04-10 deck "Quien hace que?" slide is the model. Required for credibility with seniors.

**Must have across the series (A-series):**
- **A4 — Slides-to-code traceability:** session-NN-pre/session-NN-post annotated git tags + MANIFEST.md per session folder.
- **A8 — Pre-sliced demo task bank from CONCERNS.md:** every HIGH/MED item pre-claimed by a session before the roadmap is finalized. The backlog exists; use it.
- **A9 — Capstone session composing all primitives:** the arc needs a finale that shows composition, not just individual primitives.

**Should have (differentiators):**
- **D1 — Live bug fix on a real legacy file:** at least one CONCERNS.md-sourced file per session. Show grep TODO on screen when opening a legacy file.
- **D5 — Explicit CONCERNS.md tie-in on-screen:** "this is the escape=false XSS site inventoried last month; today we fix one."
- **D6 — Cross-session composition chain:** RAG surfaces an issue; MCP files a ticket; Skills writes the spec; Agents implements it. Requires arc-level planning.
- **D8 — Honesty slide per session:** "Seamos honestos" beat from the two reference decks is a brand differentiator. Bake it into every session.

**Session 2 must additionally cover (based on project owner input):**
- Context window mechanics — what it is, why it is the fundamental constraint, how it changes what you prompt
- Why Skills exist — loading context on demand vs bloating the system prompt
- Why context-refreshing agents matter — subagents that start fresh vs parent context that fills up
- How to write deterministic LLM-assisted scripts — structured output formats, confidence scoring, validation layers, when to refuse vs when to proceed

**Defer to v2+:** workshop-in-a-box, English translation, non-DotaChile version, recorded/broadcast/LMS hosting (explicit PROJECT.md out-of-scope).

**Anti-features (deliberately excluded):** greenfield/toy demos, prompt-engineering-only content, slide-only sessions with no real commit, cloud-only credentials in demos, drive-by modernization, multi-primitive salad in one session (one primitive per session is the rule).

---

### Architecture Approach

The series has a three-tier content architecture: **Series tier** (shared index, setup guide, theming), **Session tier** (one self-contained folder per session), and **Code tier** (DotaChile master mutated live, bookmarked with pre/post git tags). The MANIFEST.md per session folder is the single source of truth linking slides to commits to tags — it doubles as the recovery plan.

**Major components:**

1. **Series index** — docs/presentations/README.md (new, ES): session table with date, slug, status, abstract.
2. **Shared setup guide** — docs/presentations/SETUP.md (new, ES): one doc every session links to for Docker, Claude Code, MCP config.
3. **Per-session folder** — docs/presentations/YYYY-MM-DD-NN-slug/: deck .md + .html + .mmd/.svg + MANIFEST.md + HANDOUT.md + REHEARSAL.md. The NN- numeric infix after the date gives canonical ordering. The two pre-existing decks (2026-04-08-*, 2026-04-10-*) stay un-numbered as "raw material."
4. **Demo manifest** — MANIFEST.md in each session folder: pre-session tag, slide-to-commit map, recovery commands (git reset --hard session-NN-pre), post-session tag, GitHub compare URL. Authored before the deck; updated after the live session with real SHAs.
5. **Git tags** — annotated session-NN-pre and session-NN-post on master. The single traceability mechanism. Tags are immutable — for re-teaching a cohort, branch off session-NN-pre rather than retagging.
6. **Codebase .planning/** — existing, orthogonal to session folders. CONCERNS.md is the demo-task bank.

**Slides-to-code traceability — how it works:** Before each session, the presenter tags master with session-NN-pre. Demo commits land on master live. After the session, session-NN-post is applied. MANIFEST.md maps slide section names to commit SHAs or tag ranges. The audience handout (HANDOUT.md) carries the GitHub compare URL .../compare/session-NN-pre...session-NN-post. A viewer three months later does: git clone; git fetch --tags; git checkout session-NN-pre; git log session-NN-pre..session-NN-post. Deck slides never mention bare SHAs — they reference demo names; MANIFEST.md is the lookup. Alternatives (inline SHAs in slides, long-lived branches) are named and rejected in ARCHITECTURE.md with rationale.

---

### Critical Pitfalls

The full catalog has 30 pitfalls. Five with the highest leverage across the arc:

1. **AI flakes mid-demo (Pitfall 1):** Single instrument — same-day rehearsal hard gate + cached asciinema transcript as first-class fallback, rehearsed switch-over included. Never skip this even when "the deck is ready."
2. **Hour overrun + setup eats 15 minutes (Pitfalls 6 + 8):** Session time budget explicitly allocated on paper (5 intro + 10 concept + 12+12 demos + 8 recap + 10 Q&A = 57 min). Payara + Postgres pre-warmed 10 minutes before start. No docker compose up -d as a live demo step. Pin Docker image tags before session 1.
3. **Multi-primitive salad (Pitfall 18):** One primitive per session as a default rule. Violation requires written justification in the plan. The roadmap enforces this at phase-creation time.
4. **Demo accidentally breaks master (Pitfall 16):** Rehearsal commits to a scratch branch; live session cherry-picks or replays the validated change. Post-session smoke test: mvn package && dev-sync.sh all && manually hit the affected page. Revert command pre-computed before going live.
5. **Demo looks impressive only because the code is toy (Pitfall 14):** Every session must touch at least one file from CONCERNS.md. Show grep TODO on screen when opening a legacy file.

Additional high-leverage pitfalls with roadmap implications:
- **Pitfall 21 (invisible cross-session deps):** All config added in any session must be committed to .claude//.mcp.json; a viewer at session-N-pre SHA has everything session N needs.
- **Pitfall 25 (Spanish deck, English output):** Declare the bilingual convention explicitly in session 1 — "el deck y la narracion estan en espanol; la salida de Claude esta en ingles — es el estado actual del ecosistema." Narrate English output in Spanish live.
- **Pitfall 30 (refactor session overclaims done):** Every migration-slice session shows the full migration arc and names which slice this session owns. "Known follow-ups" terminal slide required.

---

## Implications for Roadmap

### Phase 0: Series Scaffolding (pre-session-1)

**Rationale:** ARCHITECTURE.md and PITFALLS.md converge on this: series-level infrastructure must exist before session 1 is authored. Without it, each session re-invents setup, traceability, and backup discipline.

**Delivers:**
- docs/presentations/README.md — series index (ES)
- docs/presentations/SETUP.md — shared prereqs: Docker, Claude Code, email-rag corpus setup, MCP config, Node.js 20+ (ES)
- docs/presentations/THEME.md — canonical Marp frontmatter snippet
- docs/presentations/CLAUDE.md extended with: NN- numbering convention, MANIFEST.md/HANDOUT.md/REHEARSAL.md per-session requirement, tag convention, backup-asciinema rule
- Demo-task bank: triage CONCERNS.md HIGH/MED items and pre-claim each to a session
- Pin Docker image tags in docker-compose.yml (Pitfall 8 prevention)
- Shared primitive glossary slide (reusable across all sessions)

**Avoids:** Pitfall 8 (setup eats 15 min), Pitfall 21 (invisible cross-session deps), Anti-pattern 3 (SETUP duplication), Anti-pattern 4 (render-on-commit discipline).

**Research flag:** STANDARD — no per-phase research needed; all decisions derivable from existing repo state.

---

### Phase 1: Session 1 — "Primero, un demo" (Hook-first)

**Rationale:** Project owner direction: open with a full end-to-end workflow fixing a bug or implementing a small feature before any theory. The strongest possible hook for a mixed-seniority audience. Attendees see AI landing a real commit on a messy 15-year-old codebase in the first 20 minutes; then the rest of the arc has earned its theory budget.

**Delivers:**
- Session folder docs/presentations/YYYY-MM-DD-01-demo-primero/
- Demo (first 25 min): select a pre-sliced CONCERNS.md HIGH/MED item (e.g., one of the five escape=false XSS sites, or a specific TODO in TorneoService.java), run the full AI-assisted workflow live — spec it, fix it, test it, commit it. Real commit on master.
- After the commit: a "how did we just do that?" decomposition slide showing which primitives were used (vocabulary introduction without teaching — Pitfall 18 guard)
- session-01-pre and session-01-post git tags
- Series arc-preview slide naming all upcoming sessions

**Addresses:** S1-S11, D1 (live bug fix on a real legacy file), D5 (explicit CONCERNS.md tie-in), D8 (honesty slide). Establishes bilingual convention (Pitfall 25).

**Avoids:** Pitfall 12 (overclaiming), Pitfall 18 (arc preview names primitives without teaching), Pitfall 26 (tiered jargon via glossary slide).

**Research flag:** STANDARD. Demo task selection from CONCERNS.md must be finalized in Phase 0 (demo-task pre-claiming gap).

---

### Phase 2: Session 2 — Intro: Contexto, LLMs, y la Ventana

**Rationale:** After seeing the demo in session 1, the audience wants to understand WHY it works. Session 2 provides the conceptual scaffolding: context window mechanics (the fundamental constraint driving all subsequent primitive design), LLM evolution from ChatGPT to agents, and critically — how to write LLM-assisted scripts that produce deterministic, trustworthy results rather than hallucinated answers.

**Delivers:**
- Session folder docs/presentations/YYYY-MM-DD-02-intro-contexto-llms/
- Theory block (30 min): context window anatomy (tokens, limits, what goes in, what falls off); why this matters for code assistance at scale; LLM evolution arc (ChatGPT Nov 2022, transformers 2017, chain-of-thought, instruction-tuning, tool use, agents)
- Practical block (25 min): live demo of a script that uses a confidence factor or structured output schema to make LLM assistance deterministic — e.g., a small Python script that queries Claude for a code review and only acts on the result if confidence score exceeds a threshold; or uses JSON schema validation to ensure the output is machine-actionable
- Why Skills exist: managing what goes into the context window on demand
- Why context-refreshing agents matter: isolated subagent contexts vs a parent that fills up
- The "when to trust, when to verify, when to refuse" mental model

**Addresses:** S1-S11. Provides vocabulary for all subsequent sessions. Satisfies project owner's explicit request for context window and determinism coverage.

**Avoids:** Pitfall 12 (overclaiming — "transformers work this way" stays honest about what we don't know), Pitfall 26 (tiered jargon — define once, use freely after), Anti-feature X11 ("Build your own ChatGPT" framing — stay at the application layer).

**Research flag:** STANDARD. Context window mechanics are well-documented. Confidence/structured-output demo patterns are training-data stable. No external research needed.

---

### Phase 3: Session 3 — RAG

**Rationale:** Most self-contained primitive. tools/email-rag/ is prebuilt; corpus is available; demo surface is a real Spanish-language community archive. Low blast radius for session 3 while the audience is still calibrating trust. Connects naturally to the context window discussion from session 2: RAG is how you inject relevant context without filling the window with everything.

**Delivers:**
- Session folder docs/presentations/YYYY-MM-DD-03-rag/
- Demo: tools/email-rag/search.py "problemas login" to surface PvpgnHash weakness thread to cross-reference CONCERNS.md HIGH (do NOT implement the fix — that is the agent session job)
- Committed prompt file + replay recipe (exact starting SHA, required services, corpus note)
- Toy corpus fixture in the session folder for audience replay (Pitfall 17 prevention)

**Teaches:** BM25 vs semantic leg by subtraction; "ctrl+F sobre un mbox" comparison; hybrid search beats BM25-only on Spanish short queries. Ties back to session 2: RAG is selective context injection.

**Avoids:** Pitfall 5 (curated thread list for demo, not free corpus search), X12/X13 (presenter corpus only; toy corpus for audience replay), Pitfall 13 (smoke-test slide for 420 MB model load).

**Research flag:** STANDARD for tools/email-rag/ implementation (HIGH). Verify Ollama model tags the week of the session.

---

### Phase 4: Session 4 — MCP

**Rationale:** "Same mental model as RAG but for actions." Extends the "bring your own context" framing. Wires a Postgres MCP to the DotaChile DB — stronger than a toy DB. After this session, Skills can be explained without hand-waving about what they call out to.

**Delivers:**
- Session folder docs/presentations/YYYY-MM-DD-04-mcp/
- Demo: wire local MCP server (server-sqlite against the DotaChile Postgres), have Claude list tools, call one, show tool-call JSON in transcript
- .mcp.json committed to the repo (Pitfall 21 prevention)
- Stretch: micro-MCP server wrapping dev-sync.sh (shows server-side authoring)

**Teaches:** MCP open spec (not Anthropic-proprietary), tool-call JSON anatomy, mcp__server__tool naming convention, scoping/allow-list model.

**Avoids:** Pitfall 20 (cite Claude Code + MCP spec version), Pitfall 15 (show timing comparison to manual path).

**Research flag:** NEEDS RE-VERIFICATION. Re-read docs.claude.com/en/docs/claude-code/mcp before authoring — .mcp.json schema, transport options, official server package names. MEDIUM confidence on current schema.

---

### Phase 5: Session 5 — Skills

**Rationale:** After MCP, Skills become "named, versioned recipes that orchestrate MCP calls." After session 2's context window discussion, Skills also become "the mechanism for loading context on demand without bloating the window." Both framings reinforce each other. Skills are the most common prerequisite for Agents, Hooks, and Commands — scheduling them before those three is load-bearing.

**Delivers:**
- Session folder docs/presentations/YYYY-MM-DD-05-skills/
- Demo: live-author DotaChile-specific Skill (escape=false site guard, referencing the 5 XSS sites in CONCERNS.md) — show behavior before/after
- Composition preview: consume installed user-story skill and compose with new skill
- Committed custom Skill under .claude/skills/ as the session real artifact

**Teaches:** description field as routing trigger; project-scoped vs user-scoped; community skills; "loaded on demand" vs system prompt; ties back to session 2 context window mental model.

**Avoids:** Pitfall 20 (cite Claude Code version for Skills), Pitfall 11 (show Skill frontmatter evolution as prompt-iteration evidence).

**Research flag:** STANDARD — Skill shape fully verified (HIGH). No external research needed.

---

### Phase 6: Session 6 — Agents

**Rationale:** Sub-agents load skills; explaining agents before Skills requires hand-waving. After session 2's context-refreshing discussion, agents now have a clear "why": isolated contexts that start fresh, not a parent that accumulates. The TorneoService.java 1800-LOC god-class is the ideal investigation target — toy repos have nothing comparable.

**Delivers:**
- Session folder docs/presentations/YYYY-MM-DD-06-agentes/
- Demo: spawn subagent via Task tool to read TorneoService.java, list the 10 TODOs with file:line references and 1-line classification (done / stale / actionable)
- Parent reads return value, produces summary — ground-truth verified against the actual file on screen
- Stretch: parallel subagents — one per CONCERNS.md section (security / performance / fragile) — three reports, triaged first-cuts list

**Teaches:** Task tool invocation, agent frontmatter, bug anthropics/claude-code#13898 (MCP-strip when tools: declared — teach as "the ecosystem is still raw" moment), token cost mental model. Ties back to session 2: each subagent gets a fresh context window; the parent only sees the return value.

**Avoids:** Pitfall 18 (agents use Skills from session 5 — composition, not re-introduction), Pitfall 14 (the whole point is investigating the 1800-LOC legacy file).

**Research flag:** STANDARD for agent frontmatter (HIGH, verified against live gsd-domain-researcher.md). If session includes Claude Agent SDK, re-verify SDK against its GitHub repo.

---

### Phase 7: Session 7 — Hooks

**Rationale:** Hooks wrap things. Their value is most visible wrapping an Agent boundary. Java/Maven long build-feedback loop makes the PostToolUse to mvn compile hook genuinely useful — JS hot-reload would make the same hook a gimmick.

**Delivers:**
- Session folder docs/presentations/YYYY-MM-DD-07-hooks/
- Demo A: PostToolUse hook running mvn -o compile on every Java edit, surfacing compile errors back to Claude — watch Claude self-correct
- Demo B: PreToolUse hook blocking edits to off-limits legacy folders (src/java/controller/, com.dotachile.automation.FunService) per CLAUDE.md guidance
- Both hooks committed to .claude/settings.json + .claude/hooks/ (Pitfall 21 prevention)

**Teaches:** STDIN JSON payload contract, exit-code semantics (0 = allow, non-zero = block for PreToolUse), $CLAUDE_PROJECT_DIR env var, matcher pipe-delimited syntax. References existing GSD hooks as "production examples." Ties back to session 2 determinism discussion: hooks are the deterministic layer around stochastic LLM output.

**Avoids:** Pitfall 28 (legacy ergonomics framing — Maven hook usefulness IS the legacy stack advantage), Pitfall 20 (full event catalog needs verification — see Research Flag).

**Research flag:** NEEDS RE-VERIFICATION. Full hook event catalog beyond the three verified in this repo (SessionStart, PreToolUse, PostToolUse) — re-read docs.claude.com/en/docs/claude-code/hooks before authoring. Training data suggests UserPromptSubmit, Notification, Stop, SubagentStop, PreCompact exist; only three are confirmed in-repo.

---

### Phase 8: Session 8 — Slash Commands

**Rationale:** Commands are the top-level user-facing entry point composing Skills, Agents, and Hooks. Teaching Commands last (before the capstone) delivers a payoff demo where a single command uses all three primitives.

**Delivers:**
- Session folder docs/presentations/YYYY-MM-DD-08-comandos/
- Demo: author /dota-audit-xss — loads CONCERNS.md, finds the 5 escape=false sites, runs triage prompt, outputs per-site table
- Composition demo: show GSD system existing slash commands (.claude/commands/gsd/) as multi-command workflow; use /gsd-new-milestone live
- New command committed under .claude/commands/ as the session artifact

**Teaches:** argument-hint in autocomplete, allowed-tools, @/absolute/path include syntax, $ARGUMENTS pattern, difference between command (user-triggered entry point) vs skill (loaded behavior on demand).

**Avoids:** Pitfall 18 (commands compose — prior primitives visible in composition, not re-introduced from scratch).

**Research flag:** STANDARD — slash command shape verified against live gsd/do.md (HIGH). No external research needed.

---

### Phase 9: Session 9 — Capstone (Plugin / Workflow System)

**Rationale:** By construction, last. Composes every primitive from Sessions 1-8. The meta-workflow comparison (GSD vs Superpowers vs Spec Kit or equivalent) is the teaching point: different weight classes for different team sizes. The PRIMARY demo vehicle — whether to walk GSD live, Superpowers live, or a newer alternative — is an open question to be resolved at planning time (see Gaps).

**Delivers:**
- Session folder docs/presentations/YYYY-MM-DD-09-capstone-plugin/
- Comparison: three shapes of "AI workflow system": GSD (phase-driven, .planning/ state, heavy), Superpowers (spec+plan pairs, docs/superpowers/, lightweight), and a third comparator (Spec Kit or another tool that is mature by capstone time)
- Live demo: walk whichever system the planning decision picks — use it to plan a CONCERNS.md item, commit the planning artifacts, show the folder structure as plugin state
- Optional: build minimum viable micro-plugin live (one skill + one agent + one hook + one command + one MCP wire)
- Architectural diagram: how all primitives compose in a workflow

**Teaches:** Plugin as a distribution format; "adopt an existing system first; build your own only when none fit"; how durable state enables multi-session planning continuity.

**Avoids:** Anti-pattern 5 (capstone uses only primitives taught in Sessions 1-8), Pitfall 30 (if migration slice shown, "known follow-ups" slide required). Do NOT pick a winner among the three workflow systems — teach the tradeoffs.

**Research flag:** NEEDS RE-VERIFICATION. Plugin packaging/marketplace story is LOW confidence; Spec Kit 2026 state is LOW confidence; the question of which meta-workflow system to demo is OPEN — see Gap 7 below.

---

### Cut Paths

**6 sessions (minimum for reasonable primitive coverage — starts to sacrifice):**
- Drop standalone Hooks session; fold Hooks into Commands as "Commands with safety rails."
- Merge Agents + Skills into one session ("Skills y Agentes").
- Resulting arc: S1 Demo, S2 Intro/Context, S3 RAG, S4 MCP, S5 Skills+Agents, S6 Commands+Hooks, S7 Capstone.

**4 sessions (viable only if the audience is experienced; sacrifices pedagogical rhythm badly):**
- S1: Demo-first (hook) + context window intro combined
- S2: RAG + MCP (both "external context" primitives)
- S3: Skills + Agents + Hooks + Commands
- S4: Capstone

**8+ sessions (extended arc with migration topics):**
- Add a migration-slice session: PvpgnHash to bcrypt dual-hash rollover, N+1 fix in TorneoService, or Jakarta namespace flip. Must follow Pitfall 30 discipline — name the slice, open follow-up issues.
- Add a mid-arc composition session after Sessions 4-5 to show RAG + MCP + Skills composing before the capstone.

---

### Phase Ordering Rationale

Five concrete dependency chains drive the ordering:

1. Demo-first (Session 1) before theory — earns the audience's trust; provides concrete referents for all subsequent vocabulary.
2. Context window and determinism theory (Session 2) before the primitive sessions — all primitives (Skills, Agents, Hooks) are explained in terms of context window management after this session.
3. RAG before MCP — both are "external context" primitives; RAG first lets MCP be framed as "same idea but for actions." RAG is also lowest blast-radius.
4. MCP before Skills — Skills frequently call MCP tools; hand-waving about what they call out to defeats the teaching.
5. Skills before Agents, Hooks, and Commands — all three load or wrap skills; the mental model is foundational.
6. Agents before Hooks — Hooks are most vivid wrapping an Agent boundary.
7. Hooks before Commands — Commands compose Skills + Agents + Hooks; teaching Commands last delivers the payoff.
8. Capstone last — by construction.

---

### Research Flags

**Needs re-verification at phase-planning time:**
- **Session 4 (MCP):** .mcp.json schema, transport details, official server package names. Re-read docs.claude.com/en/docs/claude-code/mcp. MEDIUM confidence.
- **Session 7 (Hooks):** Full hook event catalog. Re-read docs.claude.com/en/docs/claude-code/hooks. MEDIUM confidence on events beyond the three verified in-repo.
- **Session 9 (Capstone):** Plugin packaging/marketplace story, Spec Kit 2026 state, and the meta-workflow comparison system selection. All LOW confidence or OPEN.
- **Any session using Claude Agent SDK:** Python/TypeScript SDK version and API shape. Verify against SDK GitHub.

**Standard patterns (skip research-phase):**
- **Phase 0 (scaffolding):** All decisions derivable from existing repo state. HIGH confidence.
- **Session 1 (Demo-first):** Demo task from CONCERNS.md is HIGH confidence; task selection is Phase 0 work.
- **Session 2 (Intro/Context):** Context window mechanics are well-documented. HIGH confidence.
- **Session 3 (RAG):** tools/email-rag/ verified and working. HIGH confidence; verify Ollama tags week-of.
- **Session 5 (Skills):** Fully verified in-repo. HIGH confidence.
- **Session 6 (Agents):** Frontmatter verified against live gsd-domain-researcher.md. HIGH confidence.
- **Session 8 (Commands):** Shape verified against live gsd/do.md. HIGH confidence.

---

## Confidence Assessment

| Area | Confidence | Notes |
|------|------------|-------|
| Stack | HIGH | Marp pipeline, email-rag, Claude Code primitives all verified against live repo files. Ollama model tags and Spec Kit are MEDIUM/LOW — explicitly flagged. |
| Features | HIGH | S-series and A-series items grounded in direct inspection of two reference decks and CONCERNS.md. Per-primitive matrices grounded in installed repo state at commit 17ac744. Session 2 context-window and determinism scope is HIGH — well-documented domain. |
| Architecture | HIGH | Three-tier model, folder convention, tag convention, MANIFEST.md pattern — all grounded in existing docs/presentations/ structure and PROJECT.md constraints. Industry-wide workshop conventions are MEDIUM (no external verification this session). |
| Pitfalls | HIGH (live-demo/timing/codebase), MEDIUM (series-level) | Live-demo pitfalls grounded in existing deck structure and prior-run evidence. Series-level pitfalls extrapolated from two one-off sessions — no series run yet to validate from. |

**Overall confidence:** HIGH for the decision set that matters (folder structure, tag convention, session ordering, toolchain, per-session requirements). MEDIUM for external ecosystem claims that require re-verification per session. The capstone demo-system selection is deliberately OPEN.

---

### Gaps to Address During Phase Planning

1. **Hook event catalog completeness:** Training data suggests UserPromptSubmit, Notification, Stop, SubagentStop, PreCompact exist beyond the three in this repo. Re-verify before Session 7 is authored. Plan the session assuming only the three verified events; add new ones after verification.
2. **MCP server schema (2026 state):** .mcp.json schema, transport options, and current official server package names — re-read from docs.claude.com before Session 4 is authored. Default to server-filesystem (most stable); upgrade if verified.
3. **Spec Kit current state:** Active repo, commands may have renamed. Plan the capstone comparison without assuming specific Spec Kit commands; verify and fill in at planning time.
4. **Claude Agent SDK (programmatic):** If Session 6 or the capstone teaches agent authoring via the Python/TypeScript SDK (not just .claude/agents/*.md frontmatter), the SDK version and API shape need to be verified. Default to frontmatter-only approach (verified); SDK approach is an enhancement added only after verification.
5. **Migration sizing for session slices:** CONCERNS.md identifies PvpgnHash to bcrypt and N+1 in TorneoService as HIGH-priority items. The exact scope of a 45-minute slice for each needs to be verified against the actual code at planning time — not pre-sizeable without a dry run.
6. **Demo-task pre-claiming:** CONCERNS.md HIGH/MED items must be mapped to sessions during Phase 0. This mapping does not yet exist and is a prerequisite for Sessions 1-9 planning.
7. **OPEN: Which meta-workflow system to demo in the capstone?** GSD is the heaviest option (35 agents, 40+ commands — impressive but intimidating). Superpowers is the lightest (spec+plan pairs — accessible but might feel thin for a capstone). Spec Kit is neutral scaffolding (unknown 2026 state). A fourth option may have emerged by capstone time. Decision criteria: (a) which system the audience has actually seen used across the arc; (b) which is most mature by delivery date; (c) whether a live "build a mini-plugin" demo is more compelling than a "walk an existing system" demo. Do NOT pre-decide this in the research — decide at capstone phase-planning time.

---

## Sources

### Primary (HIGH confidence — direct file inspection)

- .planning/PROJECT.md — project charter, constraints, scope, key decisions
- .planning/codebase/CONCERNS.md — legacy-debt inventory sourcing all demo tasks
- .planning/codebase/ARCHITECTURE.md — lab surface the demos mutate
- docs/presentations/CLAUDE.md — authoritative Marp/Mermaid/HTML convention
- docs/presentations/2026-04-08-mas-alla-del-hype/2026-04-08-mas-alla-del-hype.md — prior deck, table-stakes pattern reference
- docs/presentations/2026-04-10-ai-driven-development/2026-04-10-ai-driven-development.md — prior deck, composition pattern reference
- .claude/settings.json — verified hook event names, matcher format, command shape
- .claude/skills/git-commit/SKILL.md — verified SKILL.md frontmatter
- .claude/agents/gsd-domain-researcher.md — verified agent frontmatter + bug-13898 workaround
- .claude/commands/gsd/do.md — verified slash-command frontmatter
- .claude/hooks/gsd-prompt-guard.js — verified hook I/O contract
- tools/email-rag/ (via root CLAUDE.md) — confirmed BM25 + sentence-transformers pipeline working on Chilean corpus
- Project owner input (2026-04-19): Session 1 should demo first; Session 2 should cover context window mechanics, determinism patterns, and why Skills/refreshing-agents exist; capstone meta-workflow system selection is open.

### Secondary (MEDIUM confidence — training data, consistent with repo evidence)

- Ollama version/model landscape — stable ecosystem, model tags drift monthly
- asciinema ^2.4 + VHS status — stable; APIs do not drift
- @modelcontextprotocol/server-filesystem, server-sqlite, server-git package names
- Full hook event catalog beyond the three verified in this repo
- Workshop-series convention (date-prefixed folders, per-session self-contained dirs, pre/post tags)
- Structured output / confidence scoring patterns for LLM-assisted scripts — well-established, multiple implementations

### Tertiary (LOW confidence — requires re-verification before use)

- Spec Kit (github.com/github/spec-kit) 2026 state — active repo, commands may have renamed
- Plugin marketplace / plugin-as-first-class-format story — moving fast in 2025-2026
- Claude Agent SDK (Python/TypeScript) version and API shape
- Spanish-optimal Ollama model tags — qwen2.5:7b recommended; verify ollama.com/library the week of the RAG session
- Which meta-workflow system is best-suited for the capstone demo — OPEN question, not resolvable from research alone

---

*Research completed: 2026-04-19*
*Ready for roadmap: yes*
