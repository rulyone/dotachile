# DotaChile AI-SWE Workshop Series

## What This Is

A short arc of 4–8 one-hour live workshops that teach working devs, senior/staff engineers, and about-to-graduate students how to apply AI to real software engineering — using the DotaChile legacy Java EE codebase as the demo surface. Each session pairs a Marp deck (Spanish) with real work that lands on `master`: a feature, fix, refactor, or migration slice done in under an hour with AI assistance. The codebase isn't a returning product; it's the lab.

## Core Value

**The demos are believable.** AI-assisted work is performed live on a genuinely messy, end-of-life Java 8 / JSF 2 / PrimeFaces 4 codebase — not a toy repo — so the audience walks away convinced the techniques survive first contact with production code.

## Requirements

### Validated

<!-- Existing platform infrastructure inferred from the codebase. -->

- ✓ **Marp presentation pipeline** in `docs/presentations/YYYY-MM-DD-<slug>/` (source `.md` + rendered `.html` + Mermaid `.mmd`/`.svg`) — existing (see `docs/presentations/CLAUDE.md`)
- ✓ **DotaChile Java EE codebase** available as demo surface — existing, mapped in `.planning/codebase/` on 2026-04-19
- ✓ **Two reference decks** (Marp/Spanish) on the AI-SWE theme: `2026-04-08-mas-alla-del-hype`, `2026-04-10-ai-driven-development` — existing (raw material to be re-split and extended)
- ✓ **Superpowers spec/plan archive** in `docs/superpowers/{specs,plans}/` — existing prior art from earlier AI-driven refactors (testing, email-rag, claude-md-per-feature, password-confirmation, presentation planning)
- ✓ **GSD workflow system** installed under `.claude/get-shit-done/` and `.claude/commands/gsd/` — existing (commit `21e8938`)
- ✓ **Email RAG tool** (`tools/email-rag/`) as a pre-built demo artifact for RAG topics — existing, per-developer corpus at `../dotachile-emails/corpus`

### Active

<!-- Hypotheses until delivered. The roster of topics & session count is decided in REQUIREMENTS.md / ROADMAP.md. -->

- [ ] Deliver 4–8 one-hour workshop sessions as Marp decks in Spanish under `docs/presentations/`
- [ ] Each session ships a real, committed demo on `master` (feature / bug fix / refactor / migration slice) — not a toy
- [ ] Cover an **intro session** on LLM evolution (ChatGPT → transformers paper → chain-of-thought → today) with hands-on practice (e.g., write a couple of agents and skills live)
- [ ] Cover **agentic primitives** across sessions: RAGs, MCPs, Skills, Agents, Hooks, Commands
- [ ] Cover a **capstone-style topic** on building a full AI workflow/plugin system (GSD-like, Superpowers-like, Spec Kit-like)
- [ ] Establish a per-session artifact convention that links the deck to its demo commits (slides ↔ code traceability)
- [ ] Each session's demo task is sliced small enough to run live in one hour — bigger arcs (e.g., PvpgnHash → bcrypt rollover, N+1 fix in `TorneoService`, XSS-surface audit) are split across multiple sessions

### Out of Scope

- **Reviving DotaChile as a live product** — it's a lab, not a returning service. No production deployment target.
- **Recording / broadcasting / LMS hosting** — delivery is live; post-production, editing, and hosting pipelines are out of scope for this arc
- **English-language decks or docs** — Spanish-first rule applies (see root `CLAUDE.md`); planning artifacts may stay English
- **Using a different / cleaner codebase** — the messiness of DotaChile *is* the value; switching repos defeats the core credibility claim
- **Renaming Spanish packages/entities/views to English** — Spanish-language rule, durable
- **Full modernization as a goal** — migration moves are welcome only when they *are* the teaching topic of a session; wholesale modernization is not a project outcome

## Context

- **Brownfield lab.** The codebase is end-of-life across the stack (Java 8, JSF 2.0, PrimeFaces 4.0, Java EE 6 on `javax.*`, Payara 5, PostgreSQL 15) — exactly the kind of repo working devs are paid to deal with. Full technical debt & security surface inventory lives in `.planning/codebase/CONCERNS.md` (e.g., weak `PvpgnHash`, stored-XSS surface with `escape="false"` in 5 views, N+1 risk in `TorneoService`, unpinned Docker tags).
- **Prior teaching runs.** Two Spanish-language Marp decks already exist ("Más allá del hype", "AI-Driven Development"). They cover ground the new arc will reuse *partially*, but the new sessions will split/extend the material rather than replay it.
- **Prior AI-driven work in the repo.** `docs/superpowers/` contains spec+plan pairs from earlier AI-assisted work — concrete evidence the methodology has been practiced on this codebase, useful as teaching material itself.
- **Audience spans seniority.** Working devs, senior/staff engineers, and about-to-graduate students in the same rooms. Sessions must be grounded enough for graduates but not patronizing for staff — the "messy legacy" framing does that work naturally.
- **Deliverables are checked-in artifacts.** Decks render to HTML via Marp; demo commits land on `master`; everything is consumable by cloning the repo at a given SHA.

## Constraints

- **Session budget**: Each workshop is 1 hour live. Any demo task that can't fit in one hour must be sliced into session-sized chunks during planning. — Hard audience constraint.
- **Language**: Presentation content, diagrams, and speaker notes in Spanish. — Root `CLAUDE.md` Spanish-language rule.
- **Marp toolchain**: Decks authored as `.md` with Marp directives; rendered `.html` committed alongside `.md`. Mermaid diagrams stored as `.mmd` and pre-rendered to `.svg` (HTMLPreview rewrites `<script>` tags). — See `docs/presentations/CLAUDE.md`.
- **Branching**: Demo commits land on `master`, same repo. No long-lived teaching branches. — Repo-wide convention.
- **Stack**: Demos run on the existing Java 8 / JSF 2 / PrimeFaces 4 / EJB 3 / JPA 2 / Payara 5 / PostgreSQL 15 stack. Migration moves (e.g., Jakarta namespace flip, bcrypt) are welcome only as explicit session topics, not as drive-by changes.
- **Tool/security model**: Auth is container-managed FORM login against `flexibleRealm`; roles declared in `web/WEB-INF/web.xml:110-137` (ADMIN_ROOT, ADMIN_DOTA, ADMIN_TORNEO, ESCRITOR, MODERADOR, BANEADO, ADMIN_LADDER). Demo refactors must not silently weaken auth.
- **Legacy folders are off-limits for extensions**: `src/java/controller/` (TESTMAIN/PsuMB/TestMB) and `com.dotachile.automation.FunService` are pre-refactor scratch; no new logic belongs there.

## Key Decisions

| Decision | Rationale | Outcome |
|----------|-----------|---------|
| Scope = finite arc of 4–8 one-hour sessions (not open-ended program) | Bounded scope forces a concrete roadmap and a clear "done" | — Pending |
| Core value = "demos are believable" over polish | Audience mostly experienced; credibility is the scarce resource, not production sheen | — Pending |
| Primary teaching axis = tool mastery + workflow patterns | Matches the topic pool (RAGs, MCPs, agents, hooks, commands, plugin-building) and the existing two decks' direction | — Pending |
| EOL stack migration IS allowed as a session topic (not walled off) | Walking a legacy-stack migration live is itself high-value teaching; the 1-hour budget keeps it honest via slicing | — Pending |
| Demo code lives on `master` in the same repo | Slides ↔ code traceability; clone-and-run self-service; no branch-management overhead | — Pending |
| Existing `docs/presentations/` convention is the starting home for decks | Already works, already has CLAUDE.md guidance; artifact-link convention to be defined during roadmap | — Pending |
| Spanish for deliverables; English OK for planning docs | Presentation rule is durable; GSD planning artifacts aren't audience-facing | — Pending |

## Evolution

This document evolves at phase transitions and milestone boundaries.

**After each phase transition** (via `/gsd-transition`):
1. Requirements invalidated? → Move to Out of Scope with reason
2. Requirements validated? → Move to Validated with phase reference
3. New requirements emerged? → Add to Active
4. Decisions to log? → Add to Key Decisions
5. "What This Is" still accurate? → Update if drifted

**After each milestone** (via `/gsd-complete-milestone`):
1. Full review of all sections
2. Core Value check — still the right priority?
3. Audit Out of Scope — reasons still valid?
4. Update Context with current state

---
*Last updated: 2026-04-19 after initialization*