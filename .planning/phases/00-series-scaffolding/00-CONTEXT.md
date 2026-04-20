# Phase 0: Series Scaffolding - Context

**Gathered:** 2026-04-19
**Status:** Ready for planning

<domain>
## Phase Boundary

Establish series-level infrastructure (index, SETUP, templates, conventions, demo-task pre-claiming, Docker pinning, primitives glossary, QUAL gates) that every session in the 9-session arc inherits. Without this scaffolding, every session would re-invent setup, traceability, fallback discipline, and QUAL enforcement. Phase 0 is purely infrastructure — it authors no teaching content, delivers no live session. Its success is measured by what downstream sessions (Phases 1–9) do not need to figure out again.

Requirements in scope: SCAF-01..10 (10 scaffolding artifacts) + QUAL-01..12 (12 quality gates, established here and enforced in Phases 1–9) + CURR-03 (primitives glossary verbatim-reuse baseline).

Out of scope for Phase 0: authoring any numbered session deck; selecting the capstone meta-workflow system (Phase 9 decision); resolving research gates for Phases 3/4/7/9.

</domain>

<decisions>
## Implementation Decisions

### Template Depth Philosophy

- **D-01:** `MANIFEST.template.md` uses **placeholder-annotated skeleton** — structured headers and tables with inline prompts like `[Replace: session-NN-pre SHA]`, `[Replace: slide name]`, `[Replace: compare URL]` next to every field. No fake example rows (which would need to be deleted and could be forgotten). Concrete guidance without commitment to a specific fictional session.
- **D-02:** `HANDOUT.template.md` has **fixed Spanish sections**: `¿Qué vimos?`, `Comandos para probar`, `Link de comparación`, `Próxima sesión`, `Lecturas`. Presenter fills each per session. No generic sample bullets that might ossify.
- **D-03:** `REHEARSAL.template.md` has **checklist at top + free-form notes below**. Checklist forces QUAL-02/03 compliance (model-ID pinned, same-day-run date, fallback rehearsed, network plan, pre-warm steps). Notes section is open for timing, cuts, observations — what made it into the deck, what got cut.
- **D-04:** QUAL gates surface **targeted** in templates (MANIFEST → QUAL-01/12; REHEARSAL → QUAL-02/03; HANDOUT → QUAL-06) **plus** one separate `docs/presentations/QUAL-GATES.md` reference doc at series root that lists all 12 gates in one place. Templates link to specific gate IDs (`see QUAL-GATES.md §QUAL-04`). Nothing orphaned; no duplication inside templates.

### CONCERNS Demo-Task Mapping

- **D-05:** `docs/presentations/CONCERNS-MAPPING.md` is a **single master table** with columns: `severity | CONCERNS section/ID | claimed-by-session | slice-id | status | notes`. One view, scannable.
- **D-06:** Claims are at **slice level**, not whole-concern level. Each of the 5 `escape="false"` XSS sites gets its own slice ID (`XSS-01`..`XSS-05`); PvpgnHash → bcrypt migration splits into `PVPGN-PREP` / `PVPGN-ROLLOVER` / `PVPGN-FINALIZE`; TorneoService god-class refactor is distinct from the N+1 fix. Matches the 57-min session budget (QUAL-07) and migration-slice discipline (QUAL-08 "known follow-ups" requirement).
- **D-07:** **Deferred / out-of-scope items live in the same file**, under a separate `## Deferred / Out-of-Scope` section with reason per item. One place to check for claim status. Avoids a second doc that can fall out of sync.
- **D-08:** ROADMAP.md phase notes **forward-link** to CONCERNS items they'll use (e.g., "uses `XSS-01` and `XSS-03` from CONCERNS-MAPPING.md"). CONCERNS-MAPPING.md is **authoritative** — it does NOT back-reference ROADMAP phase sections. Single direction prevents bidirectional drift; changing session assignments updates CONCERNS-MAPPING only.

### Primitives Glossary (SCAF-07 + CURR-03)

- **D-09:** Glossary is a **standalone Marp deck** at `docs/presentations/GLOSSARY/GLOSSARY.md` (+ rendered `.html`, committed alongside). Renderable and viewable on its own. Each of the 6 primitives (RAG, MCP, Skill, Agent, Hook, Slash Command) has exactly one canonical slide.
- **D-10:** CURR-03 "verbatim reuse" is enforced via **reference-only embed**. Sessions that need a primitive reference do NOT copy the glossary text into their own `.md`. They:
  - embed the canonical diagram via relative path: `![RAG](../GLOSSARY/rag.svg)`, and
  - link to the glossary deck for the spoken definition: `Ver GLOSSARY.html §RAG`.
  Drift is **structurally impossible** because there is one text and one image — sessions point at them, they don't duplicate them. This resolves the tension research flagged (Marp has no include system; copy-paste is the honest answer) against CURR-03's no-drift requirement.
- **D-11:** A dedicated **Phase 0 plan** (e.g., `primitives-glossary`) drafts all 6 definitions and 6 explainer diagrams **before Session 1 is authored**. Session 1's S01-02/S01-03 ("¿Qué acabamos de hacer?" + explainer diagram per primitive) is satisfied by referencing the Phase 0 glossary; Session 1 does NOT re-author the definitions.
- **D-12:** Explainer diagrams live canonically at `docs/presentations/GLOSSARY/{rag,mcp,skill,agent,hook,command}.mmd` + pre-rendered `.svg`. Session 1 and later sessions reference by relative path (`../GLOSSARY/<primitive>.svg`). Zero duplication, zero drift. The S01-03 requirement is satisfied by the same canonical `.mmd`/`.svg` files that the glossary itself uses.

### Claude's Discretion

The following Phase 0 decisions were not explicitly discussed; planner/executor have flexibility to pick sensible defaults informed by research:

- **SETUP.md depth (SCAF-02):** Default to **layered structure** — quick-start bullets (for seniors who already have the toolchain) followed by a deep appendix (for graduates / clean machines). Spanish. Covers Docker Compose, Payara 5, Postgres 15, Claude Code CLI, Node.js 20+, Marp CLI, Mermaid CLI, Ollama + `qwen2.5:7b` + `nomic-embed-text`, `tools/email-rag/` corpus setup.
- **Docker pinning strategy (SCAF-05):** Default to **digest pin + semver comment** — `image: postgres@sha256:...  # postgres:15.6-alpine`. Digest guarantees immutability (QUAL-09 / Pitfall 8); semver comment preserves human readability and update intent. Update-cadence note added to SETUP.md or a dedicated `DOCKER-PINS.md`.
- **Series README.md purpose (SCAF-01):** Default to **Spanish audience-facing index** per PROJECT.md language rule, with a `status` column (pending / rehearsed / delivered) as a secondary layer. Not a presenter dashboard — STATE.md remains authoritative for planning state.
- **THEME.md shape (SCAF-03):** Default to **single copy-pasteable Marp frontmatter block** at the top of the file, followed by short variation notes ("use this if the deck has dark diagrams", etc.). Marp has no include system; copy-paste is the honest path per research ARCHITECTURE.md.
- **Phase 0 plan decomposition and parallelization:** Planner decides. Research ARCHITECTURE.md and ROADMAP.md notes flag these as parallelizable: templates (MANIFEST/HANDOUT/REHEARSAL), Docker pinning, CONCERNS-MAPPING, primitives glossary, THEME.md, extended `docs/presentations/CLAUDE.md`. The series-index plan (SCAF-01) should follow the others — it summarizes them.

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### Project charter and requirements

- `.planning/PROJECT.md` — series vision, core value ("demos are believable"), constraints (Spanish-language rule, 1-hour budget, master-only, Java 8 / JSF 2 / PrimeFaces 4 stack lock), key decisions log
- `.planning/REQUIREMENTS.md` §SCAF — the 10 scaffolding requirements (SCAF-01..10)
- `.planning/REQUIREMENTS.md` §QUAL — the 12 QUAL gates (QUAL-01..12) established in Phase 0, enforced in Phases 1–9
- `.planning/REQUIREMENTS.md` §CURR — 7 cross-session requirements (CURR-01..07); CURR-03 directly scoped to Phase 0
- `.planning/ROADMAP.md` §"Phase 0: Series Scaffolding" — phase goal, success criteria, parallelization candidates
- `.planning/ROADMAP.md` §"Cross-Cutting Requirement Mapping" — CURR-* origin phases, QUAL-* enforcement scope

### Research artifacts (Phase 0 research gate: STANDARD, no re-verification)

- `.planning/research/SUMMARY.md` — confidence assessment, 7 open gaps (Gap 6 "demo-task pre-claiming" is a direct Phase 0 deliverable), source inventory
- `.planning/research/ARCHITECTURE.md` — three-tier content architecture (series / session / code), folder conventions, MANIFEST pattern, pre/post-tag rationale, anti-pattern catalog (6 anti-patterns)
- `.planning/research/FEATURES.md` — feature taxonomy (S-series, A-series, differentiators, anti-features)
- `.planning/research/PITFALLS.md` — 30-pitfall catalog; Pitfall 1 (AI flakes), Pitfall 6 (hour overrun), Pitfall 8 (Docker setup), Pitfall 18 (multi-primitive salad), Pitfall 21 (invisible cross-session deps), Pitfall 25 (bilingual convention), Pitfall 30 (refactor overclaims) all shape Phase 0 deliverables
- `.planning/research/STACK.md` — Marp + Mermaid + email-rag + Ollama toolchain; pinned versions for SETUP.md

### Codebase surface

- `.planning/codebase/CONCERNS.md` — HIGH/MED/LOW inventory; source for CONCERNS-MAPPING.md (SCAF-06). HIGH items include PvpgnHash, 5 XSS sites, EOL stack; MED items include TorneoService 1800-LOC god-class, FilesServlet, CSRF absence
- `.planning/codebase/ARCHITECTURE.md` — lab surface descriptor (the thing demos mutate)
- `.planning/codebase/STACK.md` — current stack versions for SETUP.md pinning
- `.planning/codebase/STRUCTURE.md` — feature-package map (for demo-task slicing)
- `.planning/codebase/CONVENTIONS.md` — Spanish-naming rule, off-limits folders (`src/java/controller/`, `com.dotachile.automation.FunService`)
- `.planning/codebase/INTEGRATIONS.md` — Payara / Postgres / PvPGN wiring (informs SETUP.md)

### Existing presentation infrastructure (Phase 0 extends; does not replace)

- `docs/presentations/CLAUDE.md` — existing Marp / Mermaid / HTML convention + Spanish rule. SCAF-04 extends with NN-infix naming, sidecar files, tag convention, cached-fallback requirement
- `docs/presentations/2026-04-08-mas-alla-del-hype/` — pre-series deck (raw material; un-numbered, NOT part of the arc)
- `docs/presentations/2026-04-10-ai-driven-development/` — pre-series deck (raw material; un-numbered). Reference for `<!-- -->` speaker-notes pattern + stage directions style

### Infrastructure / build surface

- `docker-compose.yml` — current (floating-tag) state; SCAF-05 tightens to pinned digests
- Root `CLAUDE.md` — Java 8 / JSF 2 / PrimeFaces 4 / EJB / JPA / Payara 5 / Postgres 15 stack; Spanish-language rule; off-limits legacy folders rule; email-RAG corpus availability

</canonical_refs>

<code_context>
## Existing Code Insights

### Reusable Assets

- **`docs/presentations/CLAUDE.md`** — already documents Marp / Mermaid / HTML convention and the Spanish rule. SCAF-04 extends rather than rewrites (new subsections for NN-infix, sidecars, tags, cached-fallback rule).
- **`docs/presentations/2026-04-10-ai-driven-development/2026-04-10-ai-driven-development.md`** — pattern reference for speaker notes as `<!-- … -->` HTML comments, stage directions ("ALT-TAB AL TERMINAL"), and Marp frontmatter shape. Copy the frontmatter idiom into THEME.md; do NOT reuse the deck content.
- **`docs/superpowers/plans/2026-04-10-ai-driven-development-presentation.md`** — prior presentation-planning artifact. Reference for "how a Spanish workshop plan was structured" — useful as style inspiration, NOT as a template for Phase 0 planning.
- **`.planning/codebase/*.md`** — six codebase maps already cover the lab surface. Phase 0 does NOT need to run `/gsd-map-codebase` again.
- **GSD `.claude/`** — already installed at repo root (35 agents, 40+ commands, hook v1.38.0). Phase 0 does NOT need to reinstall. GSD's internal shape is the subject of the Phase 9 capstone comparison, not a Phase 0 artifact.

### Established Patterns

- **Render-on-commit:** Marp source `.md` and rendered `.html` committed together; same for Mermaid `.mmd` + pre-rendered `.svg` (HTMLPreview rewrites `<script>` tags — SVG must exist).
- **Speaker notes as inline HTML comments** in the deck `.md` (survives `marp-cli` rendering invisible to audience).
- **Spanish for audience-facing, English OK for planning artifacts** (per PROJECT.md).
- **Demos land on `master`; no long-lived teaching branches** (per PROJECT.md "Constraints").
- **Codebase maps live in `.planning/codebase/`** with CONCERNS.md as the debt inventory (feeds CONCERNS-MAPPING.md demo-task bank).
- **Speaker conventions:** `<!-- Presentador: ... -->` marks presenter-only text; stage directions like `<!-- ALT-TAB AL TERMINAL -->` are in-deck.

### Integration Points

Phase 0 artifacts connect to:

- **Each Phase 1–9 session folder** — MANIFEST/HANDOUT/REHEARSAL templates are copied into each `docs/presentations/YYYY-MM-DD-NN-<slug>/` on session-plan-phase; QUAL gates are checked against each session's committed artifacts.
- **`.planning/codebase/CONCERNS.md`** — CONCERNS-MAPPING.md reads the HIGH/MED items and assigns each slice to a session.
- **`docker-compose.yml`** — SCAF-05 pins image tags in place (updates in-place; no new file).
- **`docs/presentations/CLAUDE.md`** — SCAF-04 extends the conventions documented here (updates in-place).
- **`docs/presentations/GLOSSARY/`** — new folder; Session 1+ reference its `.svg` and `.html` via relative paths. No include system needed due to D-10 reference-only embed.

</code_context>

<specifics>
## Specific Ideas

- **Placeholder-annotated templates** (D-01) should feel like filling out a structured form. Every `[Replace: ...]` prompt tells the presenter exactly what goes there. No ambiguity, no deleting fake content, no "what goes in this field?" moments.
- Per research ARCHITECTURE.md Pattern 1: MANIFEST is authored **before** the deck during session planning (outline and demo commits) and **updated after** the live session with real SHAs. Templates should mark which fields are pre-session fillable vs. post-session fillable (e.g., `pre_sha` fillable at planning time; `post_sha` filled after the tag is pushed).
- QUAL-GATES.md at `docs/presentations/` series root is the single place presenters consult during rehearsal to remember all 12 gates. Templates link into it by gate ID (e.g., `see QUAL-GATES.md §QUAL-04`) rather than duplicating gate text.
- CURR-03 drift prevention drove the D-10 reference-only embed choice: research flagged "copy-paste is the honest answer for Marp" but CURR-03 makes copy-paste hazardous (six primitives × nine sessions = many drift opportunities). Reference embed resolves the tension by removing the copy step entirely.
- CONCERNS-MAPPING.md slice IDs (D-06) should be stable and greppable: `XSS-01`..`XSS-05`, `PVPGN-PREP` / `PVPGN-ROLLOVER` / `PVPGN-FINALIZE`, `TORNEO-N1` (N+1 fix) vs. `TORNEO-GODCLASS` (full refactor). Stable IDs make `grep 'XSS-02' docs/presentations/` surface every session deck and MANIFEST that references it.
- Reference-only glossary embed (D-10) needs one follow-up in Session 1 planning: the "¿Qué acabamos de hacer?" decomposition slide should link to `GLOSSARY.html` for the six primitive definitions rather than inlining them. Session 1's uniqueness is the HOOK (live bug fix), not the glossary — glossary is shared furniture.

</specifics>

<deferred>
## Deferred Ideas

Ideas that surfaced in discussion or research but belong in other phases / future arcs:

- **Automated series-README regeneration** from per-session MANIFEST front-matter (research ARCHITECTURE.md flags this as the "first thing that breaks at 6+ sessions"). Deferred — manual upkeep through 9 sessions is feasible; revisit if the arc extends to v2.
- **Pre-commit hook or CI job** that hash-checks session glossary slides against GLOSSARY.md canonical. Superseded by the D-10 reference-only embed choice (mechanical enforcement unnecessary when text is never copied).
- **`make slides` Makefile / npm script** that walks `docs/presentations/*/` and rebuilds stale `.html` / `.svg`. Deferred — research ARCHITECTURE.md "Scaling Considerations" sets 9–20 sessions threshold; manual flow works for 9.
- **`docs/presentations/_templates/SESSION_TEMPLATE/` scaffold + `new-session.sh` copy script.** Deferred — same 9–20 session threshold as the Makefile.
- **Nested "seasons" folder structure** (`docs/presentations/season-1/`). Deferred — research says 20+ sessions threshold.
- **Full worked-example MANIFEST** (e.g., a fully-populated fictional Session XX manifest committed as reference). Considered under D-01; rejected in favor of placeholder-annotated approach. Could be added as a side doc if authors find the placeholder format insufficient.
- **Sample Spanish bullets in HANDOUT.template.md** (considered under D-02; rejected). Could be added if authors find blank sections create friction — reversible decision.

</deferred>

---

*Phase: 00-series-scaffolding*
*Context gathered: 2026-04-19*
