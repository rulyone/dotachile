---
phase: 00-series-scaffolding
plan: 05
subsystem: infra
tags: [qual-gates, manifest, rehearsal, handout, templates, sidecar, scaf-08, scaf-09, scaf-10]

requires:
  - phase: 00-series-scaffolding
    provides: 12 QUAL requirements from ROADMAP + PROJECT.md
provides:
  - docs/presentations/QUAL-GATES.md — single reference doc for QUAL-01..QUAL-12 with stable anchor IDs
  - docs/presentations/MANIFEST.template.md — presenter sidecar (pre/post tag SHAs, slide→commit map, version pins, recovery command)
  - docs/presentations/REHEARSAL.template.md — dry-run checklist + timing log
  - docs/presentations/HANDOUT.template.md — attendee handout (5 fixed Spanish sections)
affects: [01-session-1, 02-session-2, 03-session-3, 04-session-4, 05-session-5, 06-session-6, 07-session-7, 08-session-8, 09-session-9]

tech-stack:
  added: []
  patterns:
    - "Anchor-based gate cross-reference (D-04): every template line tied to a QUAL gate references it by stable ID `QUAL-GATES.md#qual-NN`, never duplicates the gate text"
    - "Templates carry every QUAL field by construction — filling the template satisfies the gate automatically"
    - "[Replace: ...] tokens for pre/post-session fill-in fields — explicit indication of what needs completion before delivery"

key-files:
  created:
    - docs/presentations/QUAL-GATES.md
    - docs/presentations/MANIFEST.template.md
    - docs/presentations/REHEARSAL.template.md
    - docs/presentations/HANDOUT.template.md
  modified: []

key-decisions:
  - "Bundle all four files into one plan because each template links to QUAL-GATES.md by gate ID — splitting would race on anchor naming"
  - "`[Replace: ...]` placeholder tokens make pre/post-session fill-in duty explicit"
  - "HANDOUT has 5 fixed Spanish sections (`¿Qué vimos?`, `Comandos para probar`, `Link de comparación`, `Próxima sesión`, `Lecturas`) per D-02 — audience-facing invariant"

patterns-established:
  - "QUAL-GATES.md is the single source; templates/VALIDATION docs cite by `#qual-NN` anchor"
  - "Sidecar template naming: `*.template.md` at docs/presentations/ root; session folders copy and rename to `MANIFEST.md` / `REHEARSAL.md` / `HANDOUT.md`"

requirements-completed:
  - SCAF-08
  - SCAF-09
  - SCAF-10
  - QUAL-01
  - QUAL-02
  - QUAL-03
  - QUAL-04
  - QUAL-05
  - QUAL-06
  - QUAL-07
  - QUAL-08
  - QUAL-09
  - QUAL-10
  - QUAL-11
  - QUAL-12

duration: ~20min
completed: 2026-04-21
---

# Plan 00-05: Sidecar Templates + QUAL Gates Summary

**QUAL-GATES.md reference (12 gates, stable anchors) plus three presenter sidecars (MANIFEST, REHEARSAL, HANDOUT) that encode every QUAL requirement by construction.**

## Performance

- **Tasks:** 3
- **Files created:** 4 (181 + 60 + 49 + 45 = 335 lines)

## Accomplishments
- `QUAL-GATES.md` (181 lines): 12 gates (QUAL-01..12) with stable `#qual-NN` anchors, "Cómo se verifica" block per gate, usage guidance for plan-phase / rehearsal / delivery
- `MANIFEST.template.md` (60 lines): pre/post tag SHAs, slide→commit map, recovery command, version pins for Claude Code / Model ID / Ollama / Marp CLI / Mermaid CLI / MCP servers
- `REHEARSAL.template.md` (49 lines): dry-run checklist, timing observed by section, cuts made to material, flakes/corrections for "Lo que la IA NO hizo" slide
- `HANDOUT.template.md` (45 lines): 5 fixed Spanish sections per D-02

## Task Commits

1. **Task 1: QUAL-GATES.md reference doc** — `77e029b` (docs)
2. **Task 2: MANIFEST + REHEARSAL templates** — `e2852e8` (docs)
3. **Task 3: HANDOUT template** — `2cf242f` (docs)

**Merged to master:** `f4219fc` (chore: merge 00-05 executor worktree)

## Files Created/Modified
See frontmatter `key-files`.

## Decisions Made
- Anchor IDs in stable English (`qual-01` etc.) — invariant even though prose is Spanish
- HANDOUT sections exactly 5 per D-02
- `[Replace: ...]` tokens for fill-in fields

## Deviations from Plan

Recovered from worktree agent stream-idle timeout after 67 tool uses. All 3 task commits landed on worktree branch `worktree-agent-a06696f0` — orchestrator merged the branch with `--no-ff` (merge commit `f4219fc`) and authored this SUMMARY post-hoc.

**Total deviations:** 0 scope
**Impact on plan:** Plan executed as written; recovery was purely at the orchestration layer.

## Issues Encountered
- Executor stream idle timeout prevented SUMMARY.md creation. Orchestrator merged the worktree branch and reconstructed the SUMMARY from the 3 atomic task commits + plan frontmatter.

## User Setup Required
None — plan `user_setup: []`.

## Next Phase Readiness
- SCAF-08/09/10 + QUAL-01..12 satisfied. Session plan-phases 1–9 will copy these 3 templates into their session folders; filling them satisfies QUAL gates by construction.
- QUAL-GATES.md anchors are stable — no future rename risk.

---
*Phase: 00-series-scaffolding*
*Completed: 2026-04-21*
