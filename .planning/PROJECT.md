# DotaChile AI-SWE Workshop Series

## What This Is

A **9-session live workshop arc** (preceded by a Phase 0 of series-level scaffolding) that teaches working devs, senior/staff engineers, and about-to-graduate students how to apply AI to real software engineering — using the DotaChile legacy Java EE codebase as the demo surface. Each session pairs a Marp deck (Spanish) with real work that lands on `master`: a feature, fix, refactor, or migration slice done in under an hour with AI assistance. The codebase isn't a returning product; it's the lab.

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

<!-- Hypotheses until delivered. Arc shape locked after research synthesis (2026-04-19). -->

- [ ] Deliver **9 one-hour workshop sessions** as Marp decks in Spanish under `docs/presentations/YYYY-MM-DD-NN-<slug>/`, preceded by a **Phase 0** of series-level scaffolding
- [ ] Each session ships a real, committed demo on `master` (feature / bug fix / refactor / migration slice) — not a toy
- [ ] **Session 1 — "Primero, un demo"**: live end-to-end bug fix on a real legacy file as the opening hook, **followed by** a named-primitives slide plus explainer diagrams (interactive preferred where HTMLPreview compatibility allows — else CSS-only / pre-rendered fallback)
- [ ] **Session 2 — "Contexto, LLMs y la Ventana"**: context window mechanics, LLM evolution (ChatGPT → transformers → CoT → today), and patterns for deterministic LLM-assisted scripts (structured outputs, confidence scoring, validation layers)
- [ ] Sessions 3–8 cover one primitive each: **RAG, MCP, Skills, Agents, Hooks, Slash Commands** (in that order — see `research/ARCHITECTURE.md` for dependency rationale)
- [ ] **Session 9 — Capstone**: comparison of meta-workflow systems (GSD / Superpowers / Spec Kit or newer) + live composition of all primitives taught in sessions 1–8
- [ ] **Phase 0** delivers: series index (`docs/presentations/README.md`), shared setup (`SETUP.md`), extended authoring conventions (NN- infix, per-session `MANIFEST.md`/`HANDOUT.md`/`REHEARSAL.md`, `session-NN-pre`/`session-NN-post` tag discipline), Docker tag pinning, and a **CONCERNS.md demo-task-bank mapping** (every HIGH/MED item pre-claimed to a session before session 1)
- [ ] Every session's demo task is sliced to fit a 57-minute on-paper budget (5 intro + 10 concept + 12+12 demo + 8 recap + 10 Q&A) — bigger arcs (PvpgnHash → bcrypt, N+1 in `TorneoService`, XSS-surface audit) split across sessions
- [ ] Every session has a pre-baked fallback artifact (cached asciinema / VHS recording) checked in next to the deck, with a rehearsed switch-over if live AI flakes

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
| Scope = finite 9-session arc + Phase 0 scaffolding | Research synthesis (2026-04-19) surfaced a hook-first Session 1 that's pedagogically strong enough to justify exceeding the original 4–8 band by one session | — Pending |
| Core value = "demos are believable" over polish | Audience mostly experienced; credibility is the scarce resource, not production sheen | — Pending |
| Primary teaching axis = tool mastery + workflow patterns | Matches the topic pool (RAGs, MCPs, agents, hooks, commands, plugin-building) and the existing two decks' direction | — Pending |
| EOL stack migration IS allowed as a session topic (not walled off) | Walking a legacy-stack migration live is itself high-value teaching; the 1-hour budget keeps it honest via slicing | — Pending |
| Demo code lives on `master` in the same repo | Slides ↔ code traceability; clone-and-run self-service; no branch-management overhead | — Pending |
| Existing `docs/presentations/` convention extended (NN- infix + MANIFEST.md/HANDOUT.md/REHEARSAL.md sidecars + `session-NN-pre`/`session-NN-post` tags) | Keep what works (`ARCHITECTURE.md` prior art); add only the minimum to make slides↔code traceable and recovery from live failure scripted | — Pending |
| Session 1 includes named-primitives glossary + explainer diagrams (interactive preferred) | User call (2026-04-19): after the hook demo, audience should leave session 1 with vocabulary + a visual mental model of how each primitive works | — Pending |
| Diagram interactivity = CSS-only hover/toggle or local-JS for live delivery; static pre-rendered SVG fallback for HTMLPreview.github.io viewing | HTMLPreview rewrites `<script>` tags — any JS-driven SVG breaks on shared preview links. Authoring tactic decided at Phase 0 / Session 1 planning; static fallback is mandatory for reproducibility. | — Pending |
| Capstone meta-workflow-system selection (GSD vs Superpowers vs Spec Kit vs other) deliberately deferred to Session 9 phase-planning | Ecosystem moves fast; pick close to delivery based on what audience has seen + what's mature by then (`research/SUMMARY.md` Gap 7) | — Pending |
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