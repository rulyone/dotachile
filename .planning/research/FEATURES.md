# Feature Research

**Domain:** AI-assisted software engineering workshops (live, 1-hour, Spanish, hands-on demos on a legacy Java EE codebase that must land on `master`)
**Researched:** 2026-04-19
**Confidence:** HIGH for per-session and cross-series table stakes (direct prior art in `docs/presentations/2026-04-08-*` and `2026-04-10-*`, concrete backlog in `.planning/codebase/CONCERNS.md`). MEDIUM for per-primitive feature matrices (community patterns evolve weekly; pinned to what's actually installed in this repo as of commit `17ac744`).

---

## How to read this document

This is a **workshop features** catalog, not a software features catalog. "Feature" here means *a thing a session does or ships*: a demo move, a teaching asset, a safety net, an artifact. The shape of the table (Table Stakes / Differentiators / Anti-Features / Matrix) is borrowed from product work; the content is workshop-design.

The non-overlap rule the quality gate asks for:

- **Table stakes per session** = things a single 1-hour session must contain or the session itself isn't credible.
- **Table stakes across the series** = things that only the *arc* can provide; a single session can't check these boxes alone.
- **Differentiators** = things that aren't required, but move the talk from "fine workshop" to "the one people quote next year".
- **Anti-features** = things that are popular in AI-SWE content and we must deliberately *not* do.
- **Per-primitive** = per-topic (RAG / MCP / Skill / Agent / Hook / Command / Plugin-building) feature matrices — concrete demos.

Complexity is rated strictly against the **1-hour live budget**: LOW = fits with room to spare, MED = fits if rehearsed and sliced, HIGH = should be split across sessions or pre-recorded-fallback'd.

"Legacy advantage" answers: *would this demo be weaker on a greenfield repo?* If yes, the DotaChile surface is load-bearing for the lesson.

---

## Section 1 — Table stakes per session

What every single 1-hour session must ship to stay in line with the project's core value ("demos are believable"):

| # | Feature | Why it's table stakes | Complexity | Depends on | Legacy advantage |
|---|---------|----------------------|------------|------------|------------------|
| S1 | **Pinned repo SHA in the deck title slide** — audience can clone and `git checkout <sha>` to the exact pre-demo state | Without this, the "commits are real" claim is unverifiable after the session. Matches the existing deck convention of showing `docs/presentations/<date>-<slug>.md` on the closing slide | LOW | S3 (demo commits must exist first) | N/A — works anywhere, but *more* meaningful here because the repo is messy |
| S2 | **Runnable dev environment check slide** — the "¿qué necesito abierto?" slide, as in `2026-04-08-mas-alla-del-hype` slide 4 and `2026-04-10-ai-driven-development` slide 3 | Lets attendees follow along; surfaces environment bugs in the first 5 minutes, not the last 5 | LOW | `docker compose up -d` for the app; `./dev-sync.sh` for code push; Claude Code CLI installed | YES — this stack *is* a representative legacy-dev day-1 environment |
| S3 | **At least one real commit landed on `master` live during the session** | This is the project's core value in its most literal form. A session with zero new commits is a slides-only session with extra steps | MED | Demo task pre-sliced to fit 1 hour (see quality gate in PROJECT.md) | YES — the whole point is commits against a production-shaped codebase |
| S4 | **Backup plan for live-AI flake** — pre-recorded asciinema of the demo, or a "backup branch" with the commit already made, referenced in speaker notes | Live AI calls fail (rate limits, model brownouts, network). The 2026-04-08 deck already encodes this: speaker notes on slide 12 reference `feature/registro-confirmar-password-backup` | LOW-MED | S3's demo task | NO — this is a workshop-craft issue, not a codebase one |
| S5 | **A "what you just saw" recap slide** after each demo, honestly acknowledging friction | The 2026-04-08 deck's recap slides (e.g. slide 14) explicitly admit when Claude needed correction. This *increases* credibility vs. sanitized demos | LOW | S3 completed | NO |
| S6 | **Speaker notes in HTML comments** for every non-lead slide | Enables rehearsal, enables other people to re-deliver, matches `docs/presentations/CLAUDE.md` convention | LOW | — | NO |
| S7 | **Spanish deck + Spanish speaker notes + Spanish session delivery** | Root `CLAUDE.md` Spanish-language rule; audience expectation; consistent with the two prior decks | LOW | — | NO |
| S8 | **Marp render committed alongside the `.md`** (`.html` next to source, per `docs/presentations/CLAUDE.md`) | Session artifact is consumable without a toolchain; people who can't run Marp can still open the HTML | LOW | Marp CLI in the repo's toolchain | NO |
| S9 | **One "blockquote one-liner" per memorable slide** (the `> ...` convention used in both prior decks) | Lifts the deck from bullet-list fatigue to quotable soundbites; used consistently on slides like "Skills > prompts largos" in the 2026-04-08 deck | LOW | — | NO |
| S10 | **A demo task pre-sized to 45 min of execution + 15 min of setup/recap** | Hard constraint from PROJECT.md ("Each workshop is 1 hour live"). Over-scoped demos are the #1 way these sessions fail | MED | `.planning/codebase/CONCERNS.md` backlog (pre-sliced task) | YES — the legacy backlog makes tasks *easier* to slice (lots of small real wins available) |
| S11 | **Explicit "what the IA did NOT do" slide** near the end | The 2026-04-10 deck's "¿Quién hace qué?" / honesty slide is the single most remembered slide per Q&A transcripts. Protects audience from over-updating to "AI replaces me" | LOW | — | NO |

### Non-table-stakes-but-near-mandatory (per session)

These are "strongly recommended" — skipping them is a judgment call, not a credibility break:

- **S12: Live `git diff` reveal at the end of each demo.** Audience literally watches the atomic, reviewable change. Complexity LOW. Legacy advantage YES — a diff against `TorneoService` reads differently than a diff in a clean hello-world.
- **S13: A `tools/email-rag` smoke-test slide** in any session that touches RAG (first-run model download is ~30s, per `tools/email-rag/README.md`). Avoids dead air in front of the audience.

---

## Section 2 — Table stakes across the series

What only the arc can provide:

| # | Feature | Why it's series-level table stakes | Complexity (spread across sessions) | Depends on | Legacy advantage |
|---|---------|-----------------------------------|-------------------------------------|------------|------------------|
| A1 | **Shared setup session / setup doc** referenced by every session | Every deck has the same "¿qué necesito abierto?" slide (slides 4 in 2026-04-08, 3 in 2026-04-10). DRY this into a single artifact so "the setup" doesn't eat 10 min of every workshop | MED (one-time) | Audience-facing setup README inside `docs/presentations/` | YES — the setup is non-trivial (Docker, Payara, Postgres, optional corpus bundle, optional MCP servers) |
| A2 | **Consistent per-session folder layout** already enforced by `docs/presentations/CLAUDE.md` | Discoverability: anyone opening `docs/presentations/` can see all sessions ordered by date | LOW | `docs/presentations/CLAUDE.md` convention | NO |
| A3 | **Prerequisite ordering documented in the series README / roadmap** (not re-explained per session) | The MCP session shouldn't re-teach Skills if Skills came first. Saves budget for the actual demo | MED | Roadmap (`.planning/ROADMAP.md`) | NO |
| A4 | **Slides ↔ code traceability convention** — each deck names the commit(s) it produced | PROJECT.md line 33: "a per-session artifact convention that links the deck to its demo commits". Without it, the "real commits" claim is an unverifiable assertion on a slide | LOW | S1 (pinned SHA), S3 (real commit) | YES — meaningless on a toy repo; very powerful on a real one |
| A5 | **Shared glossary of primitives** (RAG, MCP, Skill, Agent, Hook, Command) with a canonical 1-slide definition reused across sessions | Prevents drift: the 2026-04-08 and 2026-04-10 decks already have slightly-different "RAG en 1 slide" — consolidating protects new sessions from version skew | LOW | — | NO |
| A6 | **Discoverability index** at `docs/presentations/README.md` or equivalent — table of sessions with date, topic, commits landed | Lets a new attendee join mid-arc and catch up. Satisfies the "clone the repo at a given SHA and see everything" claim in PROJECT.md | LOW | A4 | YES — the "catch up by reading commits" story only works on a real repo |
| A7 | **Bounded scope (4–8 sessions) with a defined "done" state** | PROJECT.md: "Scope = finite arc". Open-ended workshop series lose audience between sessions | — (planning artifact) | ROADMAP.md | NO |
| A8 | **A pool of pre-sliced demo tasks** drawn from `.planning/codebase/CONCERNS.md`, each sized for 1 hour | Without this, every session planner re-invents the "what do we demo" decision. The CONCERNS.md backlog *is* the pool | MED (one-time curation) | CONCERNS.md | YES — this is the single biggest repo-specific asset. A greenfield project wouldn't have this bank of credible tasks |
| A9 | **A "capstone" session that composes multiple primitives** | The 2026-04-10 deck already proves chaining (RAG → Skill+MCP → Superpowers) works. The series needs a capstone that does this at the plugin/workflow-system level (matches PROJECT.md's "full-blown plugin" topic) | HIGH (needs its own session's budget) | All prior primitive sessions | MED — you can compose anywhere, but composing *against the messy repo* is more convincing |
| A10 | **Consistent backup-plan discipline** (S4) treated as series rule, not per-speaker judgment | One session with no backup = audience memorably watching a spinning cursor. That moment contaminates the series | LOW (policy) | — | NO |

---

## Section 3 — Differentiators

What turns "fine workshop" into "one people remember". These are the things no one else giving this talk on stage does, and they only work *because* of the DotaChile surface:

| # | Differentiator | Value proposition | Complexity | Legacy advantage |
|---|---------------|-------------------|------------|------------------|
| D1 | **Live bug fix on a real legacy file** — e.g. the `confirmarMatch` returns-null bug in `torneos/controller/VerMatchMB.java`, or a `TODO` in `TorneoService.java` (10 of them as of CONCERNS.md) | The audience literally sees the AI fix a real bug in real legacy code, not a contrived hello-world regression. This is the strongest possible proof of "demos are believable" | MED — one pre-selected bug per session; others in reserve | **CRITICAL** — `TorneoService.java`'s 1800 LOC + 10 TODOs are the kind of surface greenfield repos cannot fake |
| D2 | **Before/after `git diff` against a file the AI has never seen tidied** — showing the AI navigating legacy entity equality gotchas (`Clan.equals()` at `id=0` from ClanServiceTest.java), or reflection-based `@EJB` tests (from CONCERNS.md), etc. | Demonstrates the AI handling real legacy pitfalls, not curated snippets | MED | **HIGH** — these gotchas *only* exist in a 15-year-old codebase |
| D3 | **Audience-suggested edge case on stage** — "someone shout a test case for the password-confirm feature; we'll add it now" | Proves the AI isn't following a rehearsed script. High-trust move; needs S4 backup if it goes sideways | MED-HIGH | Pre-rehearsed "escape hatches" for the 3–4 most likely audience suggestions | NO — works anywhere, but the audience tends to shout *legacy-flavored* edge cases (e.g. "what if Payara's session is stale?") that only bite on a real stack |
| D4 | **Spanish delivery without sacrificing technical depth** — the existing decks already prove this is possible ("Retrieval Augmented Generation" stays English, "búsqueda híbrida" is Spanish, the mix works) | Differentiates from the English-dominated AI-SWE content firehose; respects audience; aligns with PROJECT.md rule | LOW (it's the default) | NO — but a clear differentiator in the Spanish-speaking market |
| D5 | **Tie demos to `.planning/codebase/CONCERNS.md` explicitly on-screen** — "this is the `escape="false"` XSS surface inventoried last month; today we fix one" | Shows the arc is a *real* engineering backlog being worked through, not a sequence of disconnected demos | LOW | CONCERNS.md existing in the repo | **CRITICAL** — this is the single strongest argument for using DotaChile over a clean repo |
| D6 | **Cross-session composition that echoes the 2026-04-10 chain** — e.g. the RAG session surfaces an issue from the email corpus; the MCP session files a ticket for it; the Skills session writes the spec; the Agents session implements it | Audience sees the primitives compose across an arc, not just within one session. Matches the 2026-04-10 "todo se conecta" slide ethos at the series level | HIGH (requires cross-session planning) | A3, A4, A6 | MED — needs real backlog to work; the repo has one |
| D7 | **Session artifacts are themselves teaching material** — e.g. `docs/superpowers/plans/2026-04-10-*.md` is a concrete example of "how to plan a presentation with Superpowers", which can be read in a later session about plan-driven development | Each session leaves behind something that seeds the next. Workshop series rarely do this; most sessions are disposable | LOW | `docs/superpowers/` directory already has this pattern | YES — the repo already contains proof-of-concept planning artifacts |
| D8 | **Explicit "x10 honesty" slide** (the "Seamos honestos" / "¿Quién hace qué?" slides in both prior decks) | Audience intellectual honesty is rare in AI-SWE content. The 2026-04-08 deck slide 22 ("seamos honestos") is explicitly the anti-hype move; this is a brand differentiator for the arc | LOW | — | NO |
| D9 | **Audience can re-run the whole session locally** — clone repo, checkout SHA, re-do the prompt, get (roughly) the same commit | "Reproducible workshop" is almost non-existent in the space. Satisfiable only because demos land on `master` (PROJECT.md constraint) | MED — requires actually testing reproducibility once | YES — impossible without a real persistent repo |
| D10 | **Migrations-as-teaching** — when a session's topic IS the EOL-stack move (e.g. PvpgnHash → bcrypt with dual-hash rollover, or the Jakarta namespace flip), the audience watches a real legacy migration slice | The only place in the industry where someone does this live. Most "AI refactoring" content stops at renaming variables | HIGH (must be sliced per PROJECT.md constraint — 1-hour budget) | PROJECT.md "Active" bullet: "migration moves welcome only when they ARE the teaching topic" | **CRITICAL** — you cannot do this demo on a clean repo |

---

## Section 4 — Anti-features

Deliberate "we do NOT do this" list. Each has a one-liner "why not":

| # | Anti-feature | Why-not | What to do instead |
|---|-------------|---------|-------------------|
| X1 | **Greenfield / toy codebase demos** | Defeats the project's core value; PROJECT.md Out-of-Scope: "Using a different / cleaner codebase" | Stay on DotaChile. Use CONCERNS.md as the task bank |
| X2 | **Prompt-engineering-only content** ("write better prompts!") | Prompt tricks age out in weeks; primitives (Skills, MCP, Agents, Hooks) are what's durable and what the tool pool the PROJECT.md names | Teach primitives. Treat prompt-craft as emergent from Skill authoring, not a topic |
| X3 | **Slide-only sessions with no hands-on demo** | Violates S3 (real commit lands). A 1-hour slides-only session is a blog post in disguise | If the topic is truly pre-build (e.g. theory/history), pair it with a *hands-on skill-authoring* micro-demo to satisfy S3 |
| X4 | **Demos depending on cloud-only credentials attendees don't have** | Audience can't reproduce; violates D9. Also a reliability hazard — revoked keys mid-workshop kill the demo | Use local tools first: Claude Code CLI, local MCP servers (Plane on localhost, per both prior decks), the local email-rag corpus. Cloud is fine only when the *topic* is cloud |
| X5 | **Demos that require the model to hit a flaky API live on stage with no fallback** | Violates S4. One brownout ruins the session | Always have a backup branch / asciinema recording. Never build an unconditional dependence on a live API call for a critical moment |
| X6 | **Renaming Spanish packages/entities/views to English "for clarity"** | Violates root `CLAUDE.md` Spanish-language rule; also makes demos *less* credible (the whole point is this is a real Spanish-language codebase) | Leave names alone. The Spanish names are an asset, not a liability |
| X7 | **Drive-by modernization** — upgrading PrimeFaces from 4 → 14, migrating javax → jakarta, etc. as "cleanup" in an unrelated session | Violates PROJECT.md constraint: migrations are welcome only when they ARE the teaching topic. Otherwise the session drifts into a stack upgrade and loses the teaching thread | Sliced migration sessions (see D10). Everything else stays on Java 8 / JSF 2 / PF 4 |
| X8 | **Recorded / broadcast / LMS-hosted polish** | Explicit Out-of-Scope in PROJECT.md ("Recording/broadcasting/LMS hosting"). Pursuing it steals budget from the live quality | Commit decks + HTML renders. If someone wants to re-deliver, they clone the repo |
| X9 | **"Magic reveal" long-running demos** (30-min AI run with no intermediate output) | Violates the 1-hour budget and the audience's attention span. The 2026-04-08 deck's 4-step visible pipeline (brainstorm → plan → TDD → commit) is the anti-pattern for this | Break demos into visible checkpoints: spec approved → plan approved → test written → implementation → commit |
| X10 | **Single-feature MegaPlugin demos** where the session teaches one tool by showing a huge tool config | The audience doesn't learn the primitive — they learn a specific tool. In 6 months the tool changes, the lesson evaporates | Teach the primitive (Skill, Hook, etc.) with a *minimum viable* demo (see Section 5). Mention popular tools; don't anchor the session on one |
| X11 | **"Build your own ChatGPT from scratch" intro-session framings** | Wrong altitude for the audience (working devs, seniors, staff, late-graduates); teaches nothing about applying AI to their job tomorrow | The intro session should be *LLM evolution + live agent/skill authoring* (per the milestone context), not transformer-from-scratch |
| X12 | **Depending on the email-rag corpus being available to attendees** in demos that *require* it | `tools/email-rag/CLAUDE.md` and README: the corpus is per-developer (each attendee builds their own from their own Takeout). Nobody else has the DotaChile Gmail corpus | Use the corpus for *presenter* demos only. Attendees can follow along by substituting any `.mbox` (their own Gmail) or the synthetic test fixtures in `tools/email-rag/tests/` |
| X13 | **Live demos that paste real PII from the email corpus** | Non-negotiable per `tools/email-rag/CLAUDE.md`: "Never inline literal PII into commits, commit messages, PR titles, PR bodies, or terminal output the user can scrollback to" | Queries and results shown on stage must use the redacted corpus (`[REDACTED_PERSON]`, `USER_NNNN`, etc.). Never mount the encrypted bundle during a workshop session |
| X14 | **Over-scoping with "we'll finish the rest offline"** | If it doesn't fit in 1 hour, it wasn't sliced correctly. "Finish offline" means the audience never sees the finish, breaking S3 | Re-slice before the session. Multi-session arcs (PvpgnHash → bcrypt rollover, for example) are explicitly allowed by PROJECT.md |
| X15 | **Introducing a primitive for the first time in a session that also depends on it** (e.g. teaching Skills *and* using Superpowers skills in the same session where Skills are new) | Double-cognitive-load. The 2026-04-08 deck pulls this off because the audience is assumed to have some prior exposure; a net-new workshop series should not rely on that | One primitive per session (see A3: prerequisite ordering). Compose only after the component has been taught |

---

## Section 5 — Per-primitive feature matrices

For each primitive, four columns:

- **Minimum viable demo** — smallest credible thing to show in 1 hour.
- **Next-level extension** — what a second session on this primitive could add.
- **Evaluation / verification** — how the audience knows it *actually worked* (not just "Claude said OK").
- **Common audience questions** — seed Q&A; the speaker should have the answer pre-loaded.

For each row: complexity rating, dependency on other sessions, and legacy-advantage flag.

### 5.1 — RAG

| Aspect | Content | Complexity | Dependencies | Legacy advantage |
|--------|---------|------------|--------------|------------------|
| Minimum viable demo | Run `tools/email-rag/search.py "<Spanish query>"` against the existing PII-redacted corpus; read top 2–3 threads; have Claude propose a concrete bug/feature with a file path. *Exactly the Demo 1 in `2026-04-10-ai-driven-development.md`* | LOW-MED (1st run downloads 420 MB model → ~30s; needs S13 smoke-test slide) | Pre-built corpus at `../dotachile-emails/corpus`; session can mention attendees build their own per `tools/email-rag/README.md` | **HIGH** — real Spanish-language community mail, real complaints, real people. A demo against public Wikipedia or arXiv is pedagogically weaker |
| Next-level extension | Hybrid-search internals: BM25 vs semantic leg side-by-side; show one query where BM25 wins (exact term), one where semantic wins (paraphrase). Optional: re-build the corpus live from a synthetic mbox (`tools/email-rag/tests/` has fixtures) | MED (the rebuild step is slow; pre-warm) | First RAG session as prereq | MED — fixtures are synthetic, so legacy advantage is only on the *primary* corpus |
| Evaluation / verification | Verify the proposed action is actually doable: open the file path Claude named, confirm the referenced behavior exists. Cross-check against `.planning/codebase/CONCERNS.md` — does the issue overlap with inventoried tech debt? | LOW | CONCERNS.md | YES — only possible because a real debt inventory exists |
| Common audience questions | "Is this fine-tuning?" (no), "Does Claude 'remember' the corpus?" (no; retrieval per query), "Why not just grep?" (semantic leg finds paraphrases grep misses — the 2026-04-10 deck's "ctrl+F sobre un mbox" line), "What about PII?" (redacted pipeline + encrypted vault — point to `tools/email-rag/CLAUDE.md`) | — | — | — |

### 5.2 — MCP

| Aspect | Content | Complexity | Dependencies | Legacy advantage |
|--------|---------|------------|--------------|------------------|
| Minimum viable demo | Wire a local MCP server (Plane on localhost, as the 2026-04-10 deck's Demo 2 uses, or a filesystem MCP, or a Postgres MCP pointed at the DotaChile Postgres container). Have Claude list tools, call one, show tool-call JSON in the transcript | MED (MCP config is fiddly; pre-install before session) | — | MED — Postgres MCP against the *real* DotaChile DB (Usuario, Clan, Torneo tables) is more interesting than against a toy DB |
| Next-level extension | Write a micro-MCP server from scratch (Python or TS stub, 1 tool: "run `dev-sync.sh` with a given mode"). Shows the server side, not just the client side | HIGH (borderline for 1-hour; needs pre-sliced) | MVP demo first | YES — writing an MCP tool that wraps `dev-sync.sh` is legitimately useful for this repo |
| Evaluation / verification | Inspect the actual transcript showing `tool_use` blocks + results; open the resulting artifact (ticket, DB row, file) outside Claude to confirm the side effect really happened | LOW | — | YES — side effects on the real DB/file system are more convincing than on a sandbox |
| Common audience questions | "Is MCP Anthropic-proprietary?" (no, open spec, mention the spec site), "Does every tool need a server?" (no — Claude Code has built-in tools; MCP is for external), "Is it safe?" (permissioned per tool; show the allow-list in Claude Code settings), "Can I break prod?" (yes, if you give a write-capable MCP access to prod — show the scoping model) | — | — | — |

### 5.3 — Skill (Claude Code `.claude/skills/`)

| Aspect | Content | Complexity | Dependencies | Legacy advantage |
|--------|---------|------------|--------------|------------------|
| Minimum viable demo | Live-author a tiny skill in `.claude/skills/<name>/SKILL.md` that teaches Claude a DotaChile-specific rule (e.g. "when editing XHTML, remember `escape="false"` sites are listed in CONCERNS.md — always ask before removing/keeping"). Invoke it; show the behavior change | LOW-MED | — | **HIGH** — the skill's *content* is DotaChile-specific (the `escape="false"` list, the Spanish-naming rule, the legacy folders off-limits) |
| Next-level extension | Consume a community skill (`product-manager-skills/user-story` as the 2026-04-10 Demo 2 does, already present at `.claude/skills/product-manager-skills/user-story/`) and compose it with a locally-authored skill | MED | MVP | MED — composition works anywhere; the real content is local |
| Evaluation / verification | Run the same prompt with and without the skill; diff the behavior. The "without" run should skip the DotaChile-specific guard the skill adds | LOW | — | YES — you're showing the skill correctly guards a *real* legacy landmine |
| Common audience questions | "How is this different from a long system prompt?" (loaded on demand, composable, versioned), "Can I share skills across repos?" (yes — skills.sh, commit to a shared repo), "Does Claude always load them?" (no — loaded by name/description match; show the matching logic), "Where do they live?" (`.claude/skills/` project-scoped or `~/.claude/skills/` user-scoped) | — | — | — |

### 5.4 — Agent (subagent via Task / Claude Agent SDK)

| Aspect | Content | Complexity | Dependencies | Legacy advantage |
|--------|---------|------------|--------------|------------------|
| Minimum viable demo | Spawn a subagent via the `Task` tool to do a scoped investigation ("read `TorneoService.java`, list the 10 TODOs with file:line references and a 1-line classification: done / stale / actionable"). Show the parent reading the subagent's return value and producing a summary | LOW-MED | Skill session helps (subagents respect skills); not strict prereq | **CRITICAL** — the whole point is investigating a 1800-LOC legacy file. Toy-repo agents have nothing to investigate |
| Next-level extension | Parallelize: spawn 3 subagents, each on a different part of the CONCERNS.md inventory (security / performance / fragile), receive three reports, produce a triaged "first cuts" list — exactly mirroring the "Recommended first-cuts (if triaging)" section that CONCERNS.md already ends with | MED-HIGH (orchestration complexity) | MVP | **CRITICAL** — requires a real multi-dimensional debt inventory to investigate |
| Evaluation / verification | Verify the subagent's findings against ground truth: open `TorneoService.java` at the quoted line, confirm the TODO exists and the classification is honest | LOW | — | YES — ground-truthable because the codebase is real |
| Common audience questions | "Do subagents share context with the parent?" (no — isolated, return-value only), "Can they call other subagents?" (depends on tool config; show the scoping), "When should I use one vs. just asking Claude?" (scoped research, parallelism, different permission sets), "Can they cost me a lot of tokens?" (yes — show the mental model: each is a fresh context) | — | — | — |

### 5.5 — Hook (PreToolUse / PostToolUse / SessionStart, wired via `.claude/settings.json`)

| Aspect | Content | Complexity | Dependencies | Legacy advantage |
|--------|---------|------------|--------------|------------------|
| Minimum viable demo | Add a PostToolUse hook (shell script) that runs `mvn -o compile` on every `Edit` to `src/java/**/*.java` and surfaces compile errors back to Claude as a tool message. Watch Claude self-correct a broken edit | MED (hook lifecycle needs explanation) | — | **HIGH** — the hook usefulness is proportional to build-feedback-loop length. Java/Maven has a long enough loop that the hook has real value; JS hot-reload makes the same hook a gimmick |
| Next-level extension | PreToolUse hook that blocks edits to off-limits legacy folders (`src/java/controller/`, `com.dotachile.automation.FunService`) per the explicit guidance in CLAUDE.md — enforces the project convention mechanically | MED | MVP | **CRITICAL** — the off-limits folders *exist because* of legacy scar tissue |
| Evaluation / verification | Trigger the hook: intentionally make a broken edit, show the hook catching it, show Claude receiving the error and retrying. For the PreToolUse: ask Claude to edit `FunService.java`, watch the hook block it | LOW | — | YES — the legacy folders exist |
| Common audience questions | "Where do hooks live?" (`.claude/settings.json`, project-scoped — show the file), "Can hooks break my session?" (yes, if they error; show the failure mode), "Can I write them in anything?" (any executable: shell, Python, Node), "Isn't this just a pre-commit hook?" (related, but runs in the AI loop, so it *feeds back* to the model) | — | — | — |

### 5.6 — Slash Command (`.claude/commands/`)

| Aspect | Content | Complexity | Dependencies | Legacy advantage |
|--------|---------|------------|--------------|------------------|
| Minimum viable demo | Author a repo-local `/dota-audit-xss` slash command that loads `.planning/codebase/CONCERNS.md`, finds the 5 `escape="false"` sites, runs a triage prompt over them, and outputs a per-site "safe / needs review" table | LOW-MED | Skills session optional but related | **CRITICAL** — the command wraps a DotaChile-specific audit routine. On a greenfield repo the command would be a hello-world |
| Next-level extension | Show the GSD system's existing slash commands (`.claude/commands/gsd/`, commit `21e8938`) as a real example — multi-command workflow, shared state in `.planning/`, agent-driven phases. Use `/gsd-new-milestone` live | MED-HIGH (depends on GSD's own complexity) | GSD system already installed | **CRITICAL** — GSD is specifically shaped for this kind of real-repo planning work |
| Evaluation / verification | Invoke the command, show the output matches the ground truth in CONCERNS.md. For GSD demo: show the `.planning/` artifacts that land as a result | LOW | — | YES |
| Common audience questions | "Difference between a slash command and a skill?" (commands are user-triggered entry points; skills are loaded behavior on demand — show both in one demo), "Can commands call skills?" (yes — show the composition), "Are they versioned?" (yes — committed to `.claude/commands/`), "Can they take arguments?" (yes — show the `$ARGUMENTS` pattern) | — | — | — |

### 5.7 — Plugin / workflow-system building (capstone)

| Aspect | Content | Complexity | Dependencies | Legacy advantage |
|--------|---------|------------|--------------|------------------|
| Minimum viable demo | Walk through the already-installed GSD system (`.claude/get-shit-done/` + `.claude/commands/gsd/`, per PROJECT.md Validated bullet) as a reference plugin: multi-phase workflow, shared state in `.planning/`, orchestrator commands + subagents + templates. Use it live to plan a small feature | HIGH (capstone-sized; needs the full hour) | All prior primitive sessions (RAG, MCP, Skill, Agent, Hook, Command) | **HIGH** — GSD is *designed for* real-engineering scenarios; its value is visible only on a non-trivial repo |
| Next-level extension | Compare the three patterns: **GSD** (phase-driven, state in `.planning/`), **Superpowers** (skill-forced discipline, state in `docs/superpowers/`), **Spec Kit** (spec-first, state in specs). Show where each wins; build a tiny 5th-primitive plugin that composes a couple of them | HIGH — spans multiple sessions | Capstone MVP | MED — the compare only makes sense against real work |
| Evaluation / verification | The plugin must produce committed artifacts (plans, specs, commits). "It ran without errors" is not enough — the outputs must be reviewable by a human afterward | LOW | — | **CRITICAL** — the artifacts must survive clone-and-read. Only possible on a real repo |
| Common audience questions | "Do I need to build my own?" (usually no — adopt GSD / Superpowers / Spec Kit; build only when they don't fit), "Aren't these just fancy prompts?" (no — they're multi-agent orchestration with durable state — show the `.planning/` files), "How do I maintain one?" (treat it as a dogfooded internal tool; update as you learn), "What's the smallest useful one?" (a single slash command + one skill + one subagent — show the minimum composition) | — | — | — |

### 5.8 — Intro session (LLM evolution + hands-on)

Not a "primitive" in the same sense, but the milestone context names it explicitly. Structure:

| Aspect | Content | Complexity | Dependencies | Legacy advantage |
|--------|---------|------------|--------------|------------------|
| Minimum viable demo | 15 min history (ChatGPT Nov 2022 → transformer paper 2017 → chain-of-thought → instruction-tuning → tool use → agents), then **immediately** switch to terminal: author one Skill + one subagent Task call in the same session. Leaves the audience with a committable artifact even in the intro | MED (history slides easy; live authoring is the hard part that earns S3) | — | MED — the Skill authored is DotaChile-flavored (a guard, a style rule), so the legacy tie-in is there but light |
| Next-level extension | After the intro: a deeper "how transformers actually attend" micro-slide with a *working* toy example (token-level attention on a Spanish sentence from the email corpus). Borderline for 1 hour | HIGH | MVP | LOW — the tokenization demo is content-agnostic |
| Evaluation / verification | The authored Skill and Task call must actually be invoked and shown producing output before the session ends | LOW | — | NO |
| Common audience questions | "How does this relate to 'neural networks' I learned in 2015?" (embeddings + attention + scale), "Why did ChatGPT work when GPT-3 didn't?" (RLHF + instruction-tuning + product packaging), "Is this plateauing?" (honest answer: hard to say; don't oversell), "What should I read?" (one paper, one blog, one book — curated list) | — | — | — |

---

## Feature dependencies (cross-session ordering)

```
Intro (LLM history + live Skill + live Agent)
    ├──enables──> Skill session (deeper skill authoring)
    ├──enables──> Agent session (deeper subagent orchestration)
    │
Skill session
    ├──enables──> MCP session (MCP is usually invoked by skills/commands)
    ├──enables──> Hook session (hooks can trigger skill-style behavior)
    └──enables──> Command session (commands often load skills)

MCP session
    └──enables──> Plugin capstone (plugins typically wire MCP in)

Agent session
    └──enables──> Plugin capstone (plugins orchestrate agents)

RAG session
    ├──independent── but composes well with MCP (RAG feeds a ticket via MCP, as in 2026-04-10 Demo 1 → 2)
    └──enables──> Plugin capstone (many plugins include RAG)

Command session
    └──enables──> Plugin capstone (plugins often ship slash commands)

Hook session
    └──enables──> Plugin capstone (plugins often ship hooks)

Plugin capstone
    ├──requires──> all primitives above
    └──produces──> a working plugin artifact committed to .claude/
```

### Dependency notes

- **Skills is the most common prerequisite** — Hooks, Commands, MCP, and Plugins all layer on top of the skill mental model. Schedule Skills second (right after Intro).
- **RAG is semi-independent** — can slot anywhere after Intro; does not require Skill/MCP. But *composes* with MCP in a satisfying way (the 2026-04-10 deck proves this), so if Plugin-capstone includes composition, put RAG before it.
- **Intro session must include hands-on (Skill + Agent)** per the milestone context ("hands-on practice — writing a couple of agents and skills live"). This doubles as soft prereq-removal for later sessions — by session 2, everyone has seen a skill authored.
- **Plugin capstone is strictly last** — it composes every other primitive.
- **Migration-as-teaching sessions (D10)** can be interleaved once the relevant primitive is taught. Example: PvpgnHash → bcrypt rollover as an *Agent* session (multi-step orchestration with a pre-selected branch), or as a *Hook* session (a hook that enforces dual-hash on login edits).

---

## MVP definition (workshop series)

The quality gate asks for an MVP-vs-later split. For this domain, MVP = "smallest arc that is still credible as a *series*, not a sequence of talks".

### Launch with (Arc v1 — 4-session minimum)

- [ ] **Session 1 — Intro:** LLM history + live Skill authoring + live subagent Task call. (Primitives: Skill, Agent — shallow.) Satisfies S1–S11, D8, D11.
- [ ] **Session 2 — Skills + Slash Commands:** deeper Skill authoring, author a DotaChile-specific slash command that loads CONCERNS.md. (Primitives: Skill, Command.) Satisfies D1 or D2 via a small live commit.
- [ ] **Session 3 — MCP + RAG composition:** replays the 2026-04-10 deck's demo chain with fresh content. (Primitives: MCP, RAG.) Satisfies D6 by composing across the session.
- [ ] **Session 4 — Plugin capstone (light):** walk GSD live, use it to plan a CONCERNS.md item, commit the planning artifacts. (Primitive: Plugin.) Satisfies A9, D10 optional.

This 4-session arc is the minimum that delivers on PROJECT.md's "cover **agentic primitives**: RAGs, MCPs, Skills, Agents, Hooks, Commands" — though Hooks and Agents are under-covered. It's the floor, not the goal.

### Add after validation (Arc v1.x — extend to 6 sessions)

- [ ] **Session 5 — Hooks:** PreToolUse/PostToolUse wired to Maven build + off-limits-folder guard. Satisfies D2 sharply.
- [ ] **Session 6 — Agents (deep):** parallel subagents triaging CONCERNS.md sections. Satisfies D6 at the agent level.

### Add if time/audience demands (Arc v1.2 — extend to 8 sessions)

- [ ] **Session 7 — Migration-as-teaching (D10):** e.g. a PvpgnHash → bcrypt slice. Only if the audience wants to see legacy-migration AI-assisted work specifically.
- [ ] **Session 8 — Plugin capstone (full):** build a small new plugin/workflow system composing all primitives. Natural series finale.

### Future consideration (v2+)

- [ ] **Workshop-in-a-box**: generalize the setup from A1 so external presenters can re-run the arc. Deferred because PROJECT.md's Out-of-Scope explicitly names "recording / broadcasting / LMS hosting".
- [ ] **English translation of decks**. Deferred — Spanish-language rule is durable per PROJECT.md.
- [ ] **Non-DotaChile version.** Explicitly out-of-scope per PROJECT.md ("Using a different / cleaner codebase").

---

## Feature prioritization matrix

Priority keys: P1 = must ship in arc v1 / per session; P2 = strongly recommended; P3 = nice-to-have.

| Workshop feature | User (attendee) value | Implementation cost (1-hour budget fit) | Priority |
|------------------|----------------------|------------------------------------------|----------|
| S1 — Pinned SHA on title slide | HIGH (reproducibility claim) | LOW | P1 |
| S2 — Setup check slide | HIGH (unblocks follow-along) | LOW | P1 |
| S3 — Real commit on `master` | HIGH (core value) | MED | P1 |
| S4 — Backup plan | HIGH (reliability) | LOW-MED | P1 |
| S5 — Honest recap slide | HIGH (credibility) | LOW | P1 |
| S6 — Speaker notes | MED | LOW | P1 |
| S7 — Spanish delivery | HIGH (audience fit) | LOW | P1 |
| S8 — Committed HTML render | MED | LOW | P1 |
| S9 — Blockquote one-liners | MED (memorability) | LOW | P2 |
| S10 — 45+15 min time-box | HIGH | MED | P1 |
| S11 — "IA did NOT do this" slide | HIGH (anti-hype differentiator) | LOW | P1 |
| S12 — Live git diff reveal | HIGH | LOW | P2 |
| S13 — RAG smoke test slide | MED (reliability for RAG sessions) | LOW | P2 (P1 for RAG-topic sessions) |
| A1 — Shared setup doc | HIGH (saves 10 min/session) | MED one-time | P1 |
| A2 — Per-session folder layout | MED | LOW (already enforced) | P1 |
| A3 — Prerequisite ordering | HIGH | MED one-time | P1 |
| A4 — Slides↔code traceability | HIGH (verifiability) | LOW | P1 |
| A5 — Shared primitive glossary | MED | LOW | P2 |
| A6 — Discoverability index | MED | LOW | P1 |
| A7 — Bounded arc scope | HIGH (retention) | — (planning artifact) | P1 |
| A8 — Pre-sliced demo task bank | HIGH (removes per-session scoping toil) | MED one-time | P1 |
| A9 — Capstone | HIGH | HIGH (session-sized) | P1 (for v1 4-session arc) |
| A10 — Backup-plan discipline as rule | HIGH | LOW (policy) | P1 |
| D1 — Live bug fix on legacy file | HIGH | MED | P1 (appears in at least 2 sessions) |
| D2 — Before/after diff on messy code | HIGH | MED | P1 |
| D3 — Audience-suggested edge case | HIGH (memorability) | MED-HIGH | P2 |
| D4 — Spanish w/o losing depth | MED (differentiator in ES market) | LOW | P1 (already default) |
| D5 — Tie to CONCERNS.md on-screen | HIGH (series coherence) | LOW | P1 |
| D6 — Cross-session composition | HIGH | HIGH (arc-level planning) | P1 |
| D7 — Artifacts as next-session material | MED | LOW | P2 |
| D8 — x10 honesty slide | HIGH | LOW | P1 |
| D9 — Reproducible session | HIGH | MED (one-time test) | P2 |
| D10 — Migration-as-teaching | MED (specific audience) | HIGH | P2–P3 (arc v1.2+) |

---

## Competitor / prior-art feature analysis

"Competitors" = other shapes of AI-SWE workshop content. Purpose is to locate where this arc is differentiated.

| Feature | Typical conference AI talk | Typical YouTube AI-coding tutorial | Our approach |
|---------|----------------------------|------------------------------------|--------------|
| Live commit lands during session | Rarely | Sometimes (but toy repo) | **Always, on real messy code — P1** |
| Real legacy-code target | Almost never | Almost never | **Central — CORE VALUE** |
| Spanish-language delivery | Very rare | Rare | **Default** |
| Honest "what AI didn't do" | Rare | Rare | **Required (S11 + D8)** |
| Backup plan for live flake | Variable | Rare | **Required (S4 + A10)** |
| Multi-session arc with composition | Conference: no. Course: yes but polished | Rare | **Bounded 4–8 session arc (A7, A9)** |
| Ties to a real debt inventory | No | No | **Explicit (D5, A8 via CONCERNS.md)** |
| Reproducibility by clone+checkout | Rare | Rare | **Required (A4 + D9)** |

---

## Sources

- `.planning/PROJECT.md` — project charter (core value, constraints, scope) — HIGH confidence
- `.planning/codebase/CONCERNS.md` — legacy-debt inventory that sources demo tasks — HIGH confidence
- `.planning/codebase/ARCHITECTURE.md` — architecture map for what sessions can demo against — HIGH confidence
- `docs/presentations/2026-04-08-mas-alla-del-hype/2026-04-08-mas-alla-del-hype.md` — prior deck, table-stakes pattern reference — HIGH confidence
- `docs/presentations/2026-04-10-ai-driven-development/2026-04-10-ai-driven-development.md` — prior deck, composition pattern reference — HIGH confidence
- `docs/superpowers/plans/2026-04-10-ai-driven-development-presentation.md` — shows how presentations were previously planned (plan-as-teaching-material pattern, D7) — HIGH confidence
- `tools/email-rag/README.md` — concrete RAG asset available for the RAG session; defines the per-developer corpus constraint (X12) — HIGH confidence
- `tools/email-rag/CLAUDE.md` — non-negotiable PII rule (X13) — HIGH confidence
- `docs/presentations/CLAUDE.md` — authoritative per-session folder layout (S8, A2) — HIGH confidence
- Root `/CLAUDE.md` — Spanish-language rule (S7, X6), email corpus auto-mount convention — HIGH confidence

Primitive-level content (Section 5) is cross-checked against what's actually installed in this repo at commit `17ac744`:

- `.claude/get-shit-done/` + `.claude/commands/gsd/` (plugin-capstone reference) — present
- `.claude/skills/product-manager-skills/user-story/` (community-skill reference) — present
- `tools/email-rag/` (RAG reference) — present
- `.claude/settings.json` (hook/MCP config site) — present

No external WebSearch was used for Section 5 primitives because the authoritative source is this repo's own state, not community blog posts. If a future session needs to survey the *broader* ecosystem (e.g. "which MCP servers are standard in 2026"), that's a per-session research task, not a project-level one.

---
*Feature research for: AI-SWE workshop series on a legacy Java EE codebase*
*Researched: 2026-04-19*
