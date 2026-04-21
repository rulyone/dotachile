---
phase: 00-series-scaffolding
plan: 03
subsystem: infra
tags: [marp, mermaid, glossary, primitives, curr-03, canonical-source]

requires:
  - phase: 00-series-scaffolding
    provides: THEME.md light-variant frontmatter (plan 00-06); Marp + Mermaid CLI convention (docs/presentations/CLAUDE.md)
provides:
  - Canonical GLOSSARY.md Marp deck (Spanish) — one slide per primitive (RAG, MCP, Skill, Agent, Hook, Slash Command)
  - GLOSSARY.html rendered output
  - 6 Mermaid .mmd sources (rag, mcp, skill, agent, hook, command) + 6 rendered .svg
  - CURR-03 single source of truth — sessions 1–9 embed these .svg files verbatim, never duplicate the text
affects: [01-session-1, 02-session-2, 03-session-3, 04-session-4, 05-session-5, 06-session-6, 07-session-7, 08-session-8, 09-session-9]

tech-stack:
  added: []
  patterns:
    - "Reference-only glossary: sessions embed `![name](./name.svg)` + link to GLOSSARY.html §Primitive — never copy the definition text (CURR-03, D-10)"
    - "graph LR Mermaid idiom — matches existing 2026-04-10-ciclo.mmd"
    - "paginate: false for reference decks (not linear arc — Pitfall 9)"
key-files:
  created:
    - docs/presentations/GLOSSARY/GLOSSARY.md
    - docs/presentations/GLOSSARY/GLOSSARY.html
    - docs/presentations/GLOSSARY/rag.mmd
    - docs/presentations/GLOSSARY/rag.svg
    - docs/presentations/GLOSSARY/mcp.mmd
    - docs/presentations/GLOSSARY/mcp.svg
    - docs/presentations/GLOSSARY/skill.mmd
    - docs/presentations/GLOSSARY/skill.svg
    - docs/presentations/GLOSSARY/agent.mmd
    - docs/presentations/GLOSSARY/agent.svg
    - docs/presentations/GLOSSARY/hook.mmd
    - docs/presentations/GLOSSARY/hook.svg
    - docs/presentations/GLOSSARY/command.mmd
    - docs/presentations/GLOSSARY/command.svg
  modified: []

key-decisions:
  - "Six primitives in the glossary exactly (RAG, MCP, Skill, Agent, Hook, Slash Command) — matches PROJECT.md's locked list"
  - "paginate: false so the reference deck renders as a single scrollable HTML (non-linear access)"
  - "Light theme variant per 00-PATTERNS.md THEME block"

patterns-established:
  - "Canonical primitives reference lives in GLOSSARY/ — later sessions NEVER redefine these terms"
  - "Each primitive .mmd uses graph LR for consistent left-to-right data-flow reading"

requirements-completed:
  - SCAF-07

duration: ~18min
completed: 2026-04-21
---

# Plan 00-03: Primitives Glossary Summary

**Canonical reference deck (Marp, Spanish) defining six primitives with dedicated Mermaid data-flow diagrams — the single source Sessions 1–9 embed for CURR-03 compliance.**

## Performance

- **Tasks:** 2
- **Files created:** 14 (GLOSSARY.md/html + 6 .mmd/.svg pairs)

## Accomplishments
- Authored `GLOSSARY.md` — one standalone slide per primitive (RAG, MCP, Skill, Agent, Hook, Slash Command) with concept one-liner, "Qué hace", Mermaid embed, aphorism, speaker notes
- Authored 6 Mermaid `.mmd` sources using `graph LR` idiom; rendered to `.svg`
- Ran Marp CLI to produce `GLOSSARY.html` reference page
- Spanish prose throughout; primitive IDs kept in English (stable terms of art)

## Task Commits

1. **Task 1: GLOSSARY.md + 6 .mmd sources** — `d4635ac` (feat)
2. **Task 2: rendered outputs (GLOSSARY.html + 6 .svg)** — `33f5e5a` (feat)

## Files Created/Modified
See frontmatter `key-files`.

## Decisions Made
- 6 primitives exactly, ordered per PROJECT.md
- Reference-only embed pattern (D-10) — future sessions link, not duplicate
- `paginate: false` because this is a reference doc, not a linear arc

## Deviations from Plan

Recovered from worktree agent that hit usage limit before committing its own SUMMARY. The .svg/.html artifacts were regenerated via CLI in-worktree but never committed by the agent — orchestrator committed them after recovery (commit `33f5e5a`).

**Total deviations:** 0 scope, 1 recovery (SUMMARY authored post-hoc by orchestrator)
**Impact on plan:** Plan scope fully delivered; only the SUMMARY file creation was rescued.

## Issues Encountered
- Executor agent usage limit hit before it could author and commit SUMMARY.md. Orchestrator reconstructed the summary from the plan frontmatter, the feat commits, and in-worktree file inspection.

## User Setup Required
None — plan `user_setup: []`.

## Next Phase Readiness
- CURR-03 baseline established. Sessions 1–9 should reference `docs/presentations/GLOSSARY/<primitive>.svg` for diagrams and link to `GLOSSARY.html#<primitive>` for definitions.
- Downstream plan 00-07 (series-index README) can reference GLOSSARY as part of the shared scaffolding.

---
*Phase: 00-series-scaffolding*
*Completed: 2026-04-21*
