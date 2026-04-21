---
phase: 00-series-scaffolding
plan: 06
subsystem: infra
tags: [marp, theme, conventions, claude-md, numbering, sidecar, scaf-03, scaf-04]

requires:
  - phase: 00-series-scaffolding
    provides: sidecar templates (MANIFEST/REHEARSAL/HANDOUT) from plan 00-05 — extend points for CLAUDE.md sidecar section
provides:
  - docs/presentations/THEME.md — canonical Marp frontmatter (light + dark variants)
  - docs/presentations/CLAUDE.md extended with NN-slug numbering convention and sidecar-per-session section
affects: [01-session-1, 02-session-2, 03-session-3, 04-session-4, 05-session-5, 06-session-6, 07-session-7, 08-session-8, 09-session-9]

tech-stack:
  added: []
  patterns:
    - "Copy-paste theme convention: THEME.md holds the frontmatter block verbatim; no `--theme-set` include — Marp has no Markdown include support"
    - "NN-slug naming: `YYYY-MM-DD-NN-<slug>/` where NN is zero-padded session number (01..09) and slug is kebab-case Spanish"
    - "Pre-serie decks (`2026-04-08-*`, `2026-04-10-*`) stay un-numbered — not retro-renamed; identity as historical reference preserved"

key-files:
  created:
    - docs/presentations/THEME.md
  modified:
    - docs/presentations/CLAUDE.md

key-decisions:
  - "Copy-paste frontmatter block instead of theme-include: Marp has no Markdown `include` directive (verified via Context7 `/marp-team/marp-cli`). Accept the trade-off for 9 sessions; revisit in v2 if the series grows"
  - "Two variants (light/dark) both canonicalized in THEME.md — light default, dark for visually intensive code demos"
  - "Pitfalls section names three don'ts explicitly: no theme paths, no runtime Mermaid JS, no `html: true`"
  - "Zero-padded NN (`01`..`09`) so `ls` sort matches arc order"

patterns-established:
  - "THEME.md is copy-paste source; no theme includes across decks"
  - "NN-slug session folder naming is the arc-invariant directory convention"

requirements-completed:
  - SCAF-03
  - SCAF-04

duration: ~22min
completed: 2026-04-21
---

# Plan 00-06: THEME + CLAUDE.md Extension Summary

**Canonical Marp theme snippet (light + dark variants) and docs/presentations/CLAUDE.md extension covering NN-slug numbering and per-session sidecar convention — establishes SCAF-03 + SCAF-04 baselines.**

## Performance

- **Tasks:** 2
- **Files created:** 1
- **Files modified:** 1

## Accomplishments
- Authored `docs/presentations/THEME.md` (83 lines): one copy-pasteable frontmatter block with light and dark variations, Pitfalls section, guidance on when to use each variant
- Extended `docs/presentations/CLAUDE.md` (+122 lines) with two new H2 sections:
  - `## Convención de numeración NN-<slug>` — naming format, zero-padding rule, pre-serie carve-out
  - `## Sidecar per sesión` — links to MANIFEST/HANDOUT/REHEARSAL templates, describes the fill-in contract

## Task Commits

1. **Task 1: THEME.md with light + dark variants** — `188c319` (feat)
2. **Task 2: CLAUDE.md extension (NN-slug + sidecar sections)** — `19e1065` (feat)

## Files Created/Modified
- `docs/presentations/THEME.md` (new, 83 lines)
- `docs/presentations/CLAUDE.md` (mutated, +122 lines appended — no pre-existing sections changed)

## Decisions Made
- Copy-paste theme over include (Marp limitation)
- Zero-padded NN for `ls` sort stability
- Pre-serie decks not retro-renamed

## Deviations from Plan

Recovered from worktree agent that reported "mutation commands blocked" and stalled after 600s of inactivity. Investigation showed the agent had authored `THEME.md` (committed `188c319` before the stall) and appended the two CLAUDE.md sections in-worktree but never committed the `docs/presentations/CLAUDE.md` modification. Orchestrator committed the pending modification (`19e1065`) and authored this SUMMARY post-hoc.

**Total deviations:** 0 scope
**Impact on plan:** Both tasks delivered; only the SUMMARY and the second task commit were rescued by the orchestrator.

## Issues Encountered
- Executor stall ("stream watchdog did not recover"; agent reported it could not execute further mutation commands). Orchestrator recovered by committing the in-worktree CLAUDE.md modification + authoring this SUMMARY.

## User Setup Required
None — plan `user_setup: []`.

## Next Phase Readiness
- SCAF-03 + SCAF-04 satisfied. Session plan-phases 1–9 copy the THEME.md frontmatter block into their deck frontmatter; session folder directories use the NN-slug convention.
- docs/presentations/CLAUDE.md now carries the series-level conventions alongside the pre-existing Marp/Mermaid/Language sections — future contributions reference a single convention doc.

---
*Phase: 00-series-scaffolding*
*Completed: 2026-04-21*
