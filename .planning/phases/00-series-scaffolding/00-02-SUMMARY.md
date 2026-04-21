---
phase: 00-series-scaffolding
plan: 02
subsystem: infra
tags: [documentation, concerns, traceability, demo-task-bank, spanish]

# Dependency graph
requires:
  - phase: 00-series-scaffolding
    provides: CONCERNS.md inventory at .planning/codebase/CONCERNS.md (pre-existing; consumed as source)
provides:
  - Master demo-task bank at docs/presentations/CONCERNS-MAPPING.md
  - 9 greppable slice IDs (XSS-01..XSS-05, PVPGN-PREP, TORNEO-GODCLASS, TORNEO-N1, DOCKER-PINS) plus 4 additional v2-deferred IDs (PVPGN-ROLLOVER, PVPGN-FINALIZE, EOL-STACK, TODOS-SWEEP, FILES-TRAVERSAL, CSRF-ABSENT, FORM-AUTH-TLS, SCHED-BATCH, SCHED-CONTENTION, DEV-SYNC-FRAGILE, ENTITY-EQUALS)
  - Authoritative claims: XSS-01..04 → Session 8, TORNEO-GODCLASS → Session 6, DOCKER-PINS → Phase 0
  - Candidates (to be finalized at per-session plan-phase): XSS-05, PVPGN-PREP, PVPGN-ROLLOVER, TORNEO-N1
  - Forward-link-only convention (D-08): ROADMAP → CONCERNS-MAPPING, never the reverse
  - Forward reference to QUAL-GATES.md §QUAL-05 (authored in plan 00-05)
affects:
  - 00-05-sidecar-templates-and-qual-gates (QUAL-GATES.md §QUAL-05 will back-fill)
  - 00-07-series-index (README.md may reference the claims table)
  - Phases 1, 5, 6, 8 (per-session plan-phases will finalize candidate claims)

# Tech tracking
tech-stack:
  added: []
  patterns:
    - "Slice-ID-as-primary-key: XSS-01, PVPGN-PREP, TORNEO-GODCLASS — stable, greppable, used verbatim by all downstream consumers"
    - "Single-master-table pattern (D-05): one authoritative list; deferred items inline under ## Deferred / Out-of-Scope"
    - "Unidirectional link convention (D-08): authoritative doc receives forward-links, never back-links"
    - "Mixed-language convention: Spanish prose + English slice IDs (matches .planning/codebase/CONCERNS.md)"

key-files:
  created:
    - "docs/presentations/CONCERNS-MAPPING.md"
  modified: []

key-decisions:
  - "Pitfall 7 applied: only 6 rows locked (XSS-01..04 to S8, TORNEO-GODCLASS to S6, DOCKER-PINS to P0); 4 candidate rows (XSS-05, PVPGN-PREP, PVPGN-ROLLOVER, TORNEO-N1) preserve per-session plan-phase flexibility"
  - "20 HIGH/MED rows in claims table (not 14) because D-06 slice granularity expands 5 XSS entries and 3 PvpgnHash stages from single CONCERNS.md rows; all 10 deferred items appear in BOTH claims table (status=deferred) AND Deferred section per D-07"
  - "Forward-reference comment `<!-- see QUAL-GATES.md §QUAL-05 -->` added per D-04; QUAL-GATES.md is authored in plan 00-05 (forward-valid, will resolve when 00-05 commits)"

patterns-established:
  - "Slice ID naming: English uppercase with hyphen separator (XSS-01, PVPGN-PREP, TORNEO-GODCLASS, TORNEO-N1, DOCKER-PINS)"
  - "Claim status triad: claimed / candidate / deferred — downstream plan-phases shift state by editing this file only"
  - "Deferred items carry `Revisitar` marker (v2) for audit trail per research Pitfall 'Don't drop slice IDs once deferred'"

requirements-completed: [SCAF-06]

# Metrics
duration: 2min
completed: 2026-04-21
---

# Phase 00 Plan 02: Concerns Mapping Summary

**Single-source demo-task bank at `docs/presentations/CONCERNS-MAPPING.md` pre-claiming all HIGH/MED CONCERNS.md items to sessions (6 claimed / 4 candidate / 10 deferred) with greppable slice IDs.**

## Performance

- **Duration:** 2 min
- **Started:** 2026-04-21T02:33:44Z
- **Completed:** 2026-04-21T02:35:22Z
- **Tasks:** 1 / 1
- **Files created:** 1
- **Files modified:** 0

## Accomplishments

- Authored `docs/presentations/CONCERNS-MAPPING.md` (64 lines) with the master "Demo-Task Bank — HIGH/MED Claims" table and the `## Deferred / Out-of-Scope` section
- Locked the 3 requirement-coupled claim clusters: XSS-01..04 → Session 8 (per S08-01), TORNEO-GODCLASS → Session 6 (per S06-01), DOCKER-PINS → Phase 0 (per SCAF-05 / plan 00-01)
- Preserved per-session flexibility on 4 candidate slices (XSS-05, PVPGN-PREP, PVPGN-ROLLOVER, TORNEO-N1) — no over-locking per CONTEXT Pitfall 7
- Established slice-ID-as-grep-target convention: `grep -r XSS-02 docs/presentations/` will work across every later session deck and MANIFEST
- Established forward-reference to QUAL-GATES.md §QUAL-05 (authored in plan 00-05)

## Task Commits

Each task was committed atomically (with `--no-verify` per worktree protocol):

1. **Task 1: Author docs/presentations/CONCERNS-MAPPING.md master table + deferred section** — `32e208a` (docs)

_No separate plan-metadata commit in worktree mode — orchestrator handles final commit after merge._

## Files Created/Modified

- `docs/presentations/CONCERNS-MAPPING.md` — new 64-line authoritative claims table + deferred section. Provides single-source mapping from HIGH/MED CONCERNS.md items to Session claims with slice-level granularity (XSS-01..XSS-05, PVPGN-PREP/ROLLOVER/FINALIZE, TORNEO-GODCLASS/TORNEO-N1, DOCKER-PINS, etc.). Includes update rules and a forward-link to QUAL-GATES.md §QUAL-05.

## Claims Distribution

| Status | Count | Slice IDs |
|--------|-------|-----------|
| claimed | 6 | XSS-01, XSS-02, XSS-03, XSS-04 (Session 8); TORNEO-GODCLASS (Session 6); DOCKER-PINS (Phase 0) |
| candidate | 4 | XSS-05 (S8 or S5); PVPGN-PREP (S5 or S6); PVPGN-ROLLOVER (S6 or deferred); TORNEO-N1 (S5 or S6) |
| deferred | 10 | PVPGN-FINALIZE, EOL-STACK, TODOS-SWEEP, FILES-TRAVERSAL, CSRF-ABSENT, FORM-AUTH-TLS, SCHED-BATCH, SCHED-CONTENTION, DEV-SYNC-FRAGILE, ENTITY-EQUALS |
| **Total HIGH/MED rows** | **20** | all CONCERNS.md HIGH/MED items, expanded by D-06 slice granularity |

**Deferred section rows:** 10 (summary of all `status=deferred` items with `Revisitar` markers pointing to v2 arc).

## Greppable Slice IDs (required by VALIDATION.md SCAF-06)

All 9 canonical IDs are present and greppable against the file:

- `XSS-01`, `XSS-02`, `XSS-03`, `XSS-04`, `XSS-05`
- `PVPGN-PREP`
- `TORNEO-GODCLASS`, `TORNEO-N1`
- `DOCKER-PINS`

Plus 12 additional slice IDs covering the remaining deferred surface (PVPGN-ROLLOVER, PVPGN-FINALIZE, EOL-STACK, TODOS-SWEEP, FILES-TRAVERSAL, CSRF-ABSENT, FORM-AUTH-TLS, SCHED-BATCH, SCHED-CONTENTION, DEV-SYNC-FRAGILE, ENTITY-EQUALS) for complete CONCERNS.md coverage.

## Acceptance Criteria Results

All 11 plan acceptance criteria verified (PASS):

| # | Check | Expected | Actual |
|---|-------|----------|--------|
| 1 | `test -f docs/presentations/CONCERNS-MAPPING.md` | succeeds | PASS |
| 2 | Session-claim rows | ≥5 | 9 |
| 3 | All 9 canonical slice IDs present | empty missing list | all 9 present |
| 4 | `## Demo-Task Bank — HIGH/MED Claims` heading count | 1 | 1 |
| 5 | `## Deferred / Out-of-Scope` heading count | 1 | 1 |
| 6 | `Session 8 \| claimed` row count | ≥4 | 4 |
| 7 | `Session 6 \| claimed` row count | ≥1 | 1 |
| 8 | `Phase 0 \| claimed` row count | ≥1 | 1 |
| 9 | `candidate` mentions | ≥3 | 5 |
| 10 | `QUAL-05` cross-reference | present | present |
| 11 | `v2` markers (deferred revisit) | ≥5 | 20 |

## Decisions Made

- **20 rows in claims table vs. 14-row starting point in RESEARCH.md §"CONCERNS → Session Mapping":** The research table enumerated 14 distinct CONCERNS.md items with slice IDs. The authored claims table has 20 rows because D-07 requires every deferred item to appear in BOTH the main claims table (with `status=deferred`) AND the Deferred section. All 10 deferred slice IDs thus appear twice by design — no scope expansion, just the D-07 two-place convention expressed. Candidate/claimed counts (4 + 6 = 10) match research expectations exactly.
- **Pitfall 7 honored:** Only requirement-coupled slices are locked (XSS-01..04 → S8 because S08-01 names `/dota-audit-xss` across 5 XSS sites; TORNEO-GODCLASS → S6 because S06-01 names `TorneoService.java` subagent investigation; DOCKER-PINS → P0 because SCAF-05 satisfies it directly). Everything else left candidate/deferred to preserve per-session plan-phase flexibility.
- **Forward-reference to QUAL-GATES.md §QUAL-05:** Added per D-04 cross-reference convention. QUAL-GATES.md is authored in plan 00-05 (parallel wave); the link is forward-valid and will resolve when that plan lands.

## Deviations from Plan

None — plan executed exactly as written. The `<action>` block specified verbatim file content; it was transcribed without modification. All 11 acceptance criteria, both VALIDATION.md SCAF-06 rows, and all 5 `<constraints>` (D-05, D-06, D-07, D-08, Pitfall 7) verified clean.

## Issues Encountered

None.

## Threat Flags

None — docs-only artifact. The new file transforms an existing inventory (`.planning/codebase/CONCERNS.md`) into session claims; no code or config changes, no runtime surface. Matches the plan's `<threat_model>`.

## User Setup Required

None — no external service configuration required.

## Next Phase Readiness

- `docs/presentations/CONCERNS-MAPPING.md` is ready to be referenced by:
  - Plan 00-05 (QUAL-GATES.md §QUAL-05 will cite this file as the source of "every session touches ≥1 CONCERNS artifact")
  - Plan 00-07 (series README.md may summarize the claim status column in its session table)
  - Phase 1 plan-phase (Session 1's live-bug-fix demo target must be a `claimed` or `candidate` slice from this table — XSS-01 is the named candidate for a live fix per the claims table `Notes`)
  - Phases 5/6/8 plan-phases (candidates for XSS-05, PVPGN-PREP, PVPGN-ROLLOVER, TORNEO-N1 finalize here; claimed XSS-01..04 / TORNEO-GODCLASS are the demo-task anchors)
- No blockers or concerns for downstream plans.
- Orchestrator responsibilities on merge: update STATE.md (mark SCAF-06 complete, advance plan counter), update ROADMAP.md (Phase 0 Success Criterion #5 satisfied — "every HIGH/MED CONCERNS item has claimed-by / deferred annotation"), mark REQUIREMENTS.md SCAF-06 complete.

## Self-Check: PASSED

- `docs/presentations/CONCERNS-MAPPING.md` exists (commit `32e208a`)
- Commit `32e208a` on branch — verified via `git log --oneline -1` returning `32e208a docs(00-02): author CONCERNS-MAPPING master demo-task bank`
- All 11 plan acceptance criteria verified (see "Acceptance Criteria Results" table above)
- Both VALIDATION.md SCAF-06 rows verified (9 session-claim rows, 0 missing slice IDs from the 9 canonical set)
- No destructive git operations performed; no STATE.md / ROADMAP.md modifications (worktree protocol honored)

---

*Phase: 00-series-scaffolding*
*Completed: 2026-04-21*
