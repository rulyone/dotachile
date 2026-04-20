# Phase 0: Series Scaffolding - Discussion Log

> **Audit trail only.** Do not use as input to planning, research, or execution agents.
> Decisions are captured in CONTEXT.md — this log preserves the alternatives considered.

**Date:** 2026-04-19
**Phase:** 00-series-scaffolding
**Areas discussed:** Template depth philosophy, CONCERNS-MAPPING structure, Primitives glossary mechanism

Gray areas presented to user: Template depth, CONCERNS-MAPPING, Primitives glossary, SETUP + Docker pinning. User selected the first three; SETUP and Docker pinning assigned to Claude's Discretion.

---

## Template Depth Philosophy

### MANIFEST — Pre-filled content level

| Option | Description | Selected |
|--------|-------------|----------|
| Placeholder-annotated | Structured skeleton + inline prompts like `[Replace: session-NN-pre SHA]` next to every field. Concrete guidance without committing a fake example. | ✓ |
| Empty skeleton | Headers + empty tables; presenter fills everything. Minimum curation, maximum flexibility. | |
| Full worked example | Fictional Session XX with example commits/tags filled in, marked 'delete before publishing'. Shows the pattern concretely. | |

**User's choice:** Placeholder-annotated (recommended).
**Notes:** No fake example rows to delete; inline prompts tell the presenter what each field holds.

### HANDOUT — Template prescriptiveness

| Option | Description | Selected |
|--------|-------------|----------|
| Standard sections | Fixed Spanish sections: ¿Qué vimos?, Comandos para probar, Link de comparación, Próxima sesión, Lecturas. | ✓ |
| Sections + sample Spanish bullets | Same sections plus sample generic Spanish bullets per section (customizable). | |
| Freeform | Just frontmatter + blank body. Presenter decides Spanish structure per session. | |

**User's choice:** Standard sections (recommended).
**Notes:** Fixed sections ensure audience-takeaway consistency across 9 sessions without prescribing phrasing.

### REHEARSAL — Shape

| Option | Description | Selected |
|--------|-------------|----------|
| Checklist + notes | Checklist at top (QUAL-02/03: model-ID pinned, same-day-run, fallback rehearsed, network, pre-warm) + free-form notes section below. | ✓ |
| QUAL-03 checklist only | Required checkboxes per QUAL-02/03 only. Compliance-focused; no notes area. | |
| Free-form log | Open notebook; presenter writes what happened during dry-run. Trust the author. | |

**User's choice:** Checklist + notes (recommended).
**Notes:** Forces QUAL-02/03 compliance without strangling dry-run observation capture.

### QUAL gates — Surfacing across templates

| Option | Description | Selected |
|--------|-------------|----------|
| Targeted surfacing | Each template surfaces only QUAL gates directly relevant to it + one separate QUAL-GATES.md reference at series root so nothing falls through. | ✓ |
| Full QUAL appendix in every template | Each template ends with 'QUAL Checklist' listing all 12 with check-or-NA per session. Bulky but exhaustive. | |
| Separate QUAL-GATES.md only | One QUAL-GATES.md reference doc; templates don't reference QUAL IDs at all — presenter consults the doc. | |

**User's choice:** Targeted surfacing (recommended).
**Notes:** Templates stay lean; QUAL-GATES.md at series root is the single consultation point during rehearsal.

---

## CONCERNS-MAPPING Structure

### Structure

| Option | Description | Selected |
|--------|-------------|----------|
| Master table | Single table: rows = CONCERNS items, columns = severity \| claimed-by-session \| slice-id \| status \| notes. | ✓ |
| Per-session lists | One section per session listing its claimed items + file paths. Matches how a planner reads it. | |
| Hybrid | Master table is authoritative + per-session summary at bottom. Two views, more upkeep. | |

**User's choice:** Master table (recommended).
**Notes:** Scannable in one view; presenter picking a demo task sees severity + slice-id + status side-by-side.

### Granularity

| Option | Description | Selected |
|--------|-------------|----------|
| Slice | Each XSS site independent; PvpgnHash split into prep/rollover/finalize; TorneoService N+1 split from god-class refactor. Matches 57-min budget (QUAL-07). | ✓ |
| Whole item | Each CONCERNS.md item claimed atomically by one session. Simpler mapping, risks oversize demos. | |
| Flexible | Presenter decides at session-plan-phase whether to slice or claim whole. | |

**User's choice:** Slice (recommended).
**Notes:** Slice-level discipline honors QUAL-07 (57-min budget) and QUAL-08 (known-follow-ups for migration slices). Sliced IDs (`XSS-01`, `PVPGN-PREP`, etc.) are greppable.

### Deferred items

| Option | Description | Selected |
|--------|-------------|----------|
| Same doc, separate section | CONCERNS-MAPPING.md has 'Claimed by session' and 'Deferred / out-of-scope' sections. One file to consult. | ✓ |
| Inline annotation | Row in master table has claimed-by = 'DEFERRED' + reason column. Most compact. | |
| Separate DEFERRED.md | Two docs: CONCERNS-MAPPING.md for claims, DEFERRED.md for rest. Cleaner separation, more navigation. | |

**User's choice:** Same doc, separate section (recommended).
**Notes:** Single file to grep; deferred reasons don't bloat the active claims table.

### ROADMAP linkage

| Option | Description | Selected |
|--------|-------------|----------|
| Forward-link only | ROADMAP.md phase notes name CONCERNS items they'll use; CONCERNS-MAPPING.md is authoritative. | ✓ |
| Cross-reference (bidirectional) | ROADMAP.md ↔ CONCERNS-MAPPING.md cross-links. Navigable both ways, drift risk. | |
| CONCERNS-MAPPING is the only source | ROADMAP.md does NOT mention CONCERNS items. | |

**User's choice:** Forward-link only (recommended).
**Notes:** Single direction prevents bidirectional drift; changing session assignments updates CONCERNS-MAPPING only.

---

## Primitives Glossary Mechanism

### Form

| Option | Description | Selected |
|--------|-------------|----------|
| Standalone Marp deck | docs/presentations/GLOSSARY/GLOSSARY.md (+ .html + one .mmd/.svg per primitive) — renderable standalone. | ✓ |
| Fragment files | docs/presentations/glossary-fragments/{rag,mcp,skill,agent,hook,command}.md — one file per slide. | |
| Shared .md + build script | Single GLOSSARY.md + a script that inlines sections into each session's deck at build time. | |

**User's choice:** Standalone Marp deck (recommended).
**Notes:** Glossary is renderable/consumable on its own; sessions reference it rather than inline it.

### Verbatim enforcement (CURR-03)

| Option | Description | Selected |
|--------|-------------|----------|
| Reference-only embed | Sessions embed the canonical .svg and LINK to the glossary deck for definition. One text, one image. | ✓ |
| Build-time hash check | Pre-commit hook or CI compares session glossary-slide bytes to GLOSSARY.md canonical. | |
| Honor system + REHEARSAL checklist | QUAL-02/03 rehearsal checklist includes 'verified glossary slides match canonical text'. | |

**User's choice:** Reference-only embed (recommended).
**Notes:** Structurally drift-proof. Resolves the Marp-has-no-include tension against CURR-03 by removing the copy step entirely.

### Ownership

| Option | Description | Selected |
|--------|-------------|----------|
| Phase 0 Plan | A Phase 0 plan 'primitives-glossary' drafts all 6 definitions + 6 diagrams before Session 1. | ✓ |
| Phase 0 scaffold + Session 1 fills text | Phase 0 establishes the structure (empty glossary deck); Session 1 writes definitions. | |
| Session 1 owns it | Session 1 drafts the canonical definitions; Phase 0 only establishes naming/location convention. | |

**User's choice:** Phase 0 Plan (recommended).
**Notes:** Aligns with SCAF-07 being a Phase 0 requirement; Session 1 consumes rather than authors.

### Diagram location

| Option | Description | Selected |
|--------|-------------|----------|
| Glossary folder is canonical | docs/presentations/GLOSSARY/{rag,mcp,skill,agent,hook,command}.mmd + .svg. Sessions reference via `../GLOSSARY/`. | ✓ |
| Session 1 folder canonical | Diagrams live in Session 1 folder; glossary deck references them. | |
| Duplicated: canonical + per-session copy | Canonical in GLOSSARY/; each session keeps a copy for folder self-containment. | |

**User's choice:** Glossary folder is canonical (recommended).
**Notes:** Zero duplication; S01-03 (Session 1 explainer diagrams) is satisfied by the same canonical files.

---

## Claude's Discretion

Not explicitly discussed; planner/executor has flexibility per CONTEXT.md `<decisions>` section:

- SETUP.md depth (SCAF-02) — default: layered quick-start + appendix
- Docker pinning strategy (SCAF-05) — default: digest + semver comment
- Series README purpose (SCAF-01) — default: Spanish audience-facing index with status column
- THEME.md shape (SCAF-03) — default: copy-pasteable Marp frontmatter + variation notes
- Phase 0 plan decomposition / parallelization — planner decides (research flags templates + Docker pinning + CONCERNS-MAPPING + glossary + THEME + CLAUDE.md extension as parallelizable; series-index follows)

## Deferred Ideas

Covered in CONTEXT.md `<deferred>` section — automation, build scripts, nested season folders, session-template scaffold, hash-check tooling, full worked-example MANIFEST, sample Spanish bullets in HANDOUT.
