---
phase: 00-series-scaffolding
plan: 01
subsystem: infra
tags: [docker, supply-chain, digest-pin, payara, postgres, maven, infrastructure]

# Dependency graph
requires:
  - phase: 00-series-scaffolding
    provides: research-verified digests (postgres 15.17-bookworm, payara 5.2022.5) captured 2026-04-20
provides:
  - digest-pinned docker-compose.yml (postgres service)
  - digest-pinned Dockerfile (maven build stage + payara runtime stage)
  - inline Payara 5 EOL warning with DO-NOT-upgrade rationale
  - baseline for QUAL-09 pre-warm discipline (no re-pull between rehearsal and live session)
affects: [00-04-setup-doc, 00-07-series-index, quality-gates-all-sessions]

# Tech tracking
tech-stack:
  added: []
  patterns:
    - "@sha256: pin syntax for cross-platform image immutability"
    - "Multi-arch manifest-list digests preferred over arch-specific digests (Open Question 3 resolution)"
    - "Inline EOL warnings where tag appears semver-bumpable but is not (Pitfall 8)"

key-files:
  created: []
  modified:
    - docker-compose.yml
    - Dockerfile

key-decisions:
  - "Use multi-arch manifest-list digest for postgres (cross-platform correctness per Open Question 3) instead of amd64-only digest captured in research"
  - "Pin maven to existing maven:3.8-openjdk-11 tag (effectively frozen since 2022-08-03) instead of the non-existent maven:3.8.8-openjdk-11 tag prescribed in plan — the -openjdk-11 variants were purged from Docker Official Images at maven 3.8.6"
  - "Payara 5 pinned to single-manifest amd64 digest (no multi-arch manifest-list exists upstream); matches research verification"

patterns-established:
  - "Pattern: Image pins carry a trailing comment line with pin date, cadence, and pointer to SETUP.md §'Cadencia de actualización de pins de Docker'"
  - "Pattern: EOL images carry an inline anti-upgrade warning naming the concrete breakage (e.g., 'Payara 5 EOL — DO NOT upgrade to Payara 6 (Jakarta EE 10 breaks javax.* imports across the codebase)')"

requirements-completed: [SCAF-05, QUAL-09]

# Metrics
duration: ~15 min
completed: 2026-04-20
---

# Phase 0 Plan 01: Docker Pinning Summary

**Every Docker image reference in `docker-compose.yml` and `Dockerfile` pinned to an immutable `@sha256:` digest (postgres 15.17-bookworm multi-arch, maven 3.8-openjdk-11 multi-arch, payara 5.2022.5 amd64-only) with Payara 5 EOL warning inline.**

## Performance

- **Duration:** ~15 min (research verification, Docker Hub API queries for non-existent maven tag, two edits, verification)
- **Started:** 2026-04-21T02:22Z (approx. — includes research-and-verify before Task 1 commit)
- **Completed:** 2026-04-21T02:38Z
- **Tasks:** 2/2
- **Files modified:** 2

## Accomplishments

- Pinned `docker-compose.yml` `services.db.image` from floating `postgres:15` to multi-arch manifest-list digest `postgres:15.17-bookworm@sha256:550245350d614cd36d1a8e90d76c6f6608172659312e53417becbbe722aa4179`. Cross-platform correct (amd64 + arm64/Apple Silicon).
- Pinned `Dockerfile` Maven build stage to `maven:3.8-openjdk-11@sha256:805f366910aea2a91ed263654d23df58bd239f218b2f9562ff51305be81fa215` (multi-arch manifest-list). See deviation below for why this tag differs from the plan's `maven:3.8.8-openjdk-11` prescription.
- Pinned `Dockerfile` Payara runtime stage to `payara/server-full:5.2022.5@sha256:95f45ebc141eb68f1e572725b570aad03059a4e8ab34e590f8f7c7259011df75` (single-manifest amd64; matches research digest exactly).
- Inline Payara 5 EOL comment added per Pitfall 8 rule: "Payara 5 EOL — 5.2022.5 is the last release on the 5.x line. DO NOT upgrade to Payara 6 (Jakarta EE 10 breaks javax.* imports across the codebase)."
- All SCAF-05 VALIDATION.md rows green (docker-compose grep = 0, Dockerfile grep = 0, `docker compose config` exit 0).

## Task Commits

Each task committed atomically:

1. **Task 1: Pin docker-compose.yml postgres image** — `aae8dc7` (feat)
2. **Task 2: Pin Dockerfile maven + payara FROM lines** — `ccc9780` (feat)

_Note: No RED/GREEN/REFACTOR cycle — plan is `type: execute`, not `type: tdd`. Docker pinning has no unit-testable behavior; validation is grep-based._

## Files Created/Modified

- `docker-compose.yml` — 1 line changed (`image:` directive on `db` service), 1 comment line added below. Rest of file byte-identical.
- `Dockerfile` — 2 `FROM` lines changed (maven build stage, payara runtime stage), 2 comment lines added below. All COPY/RUN/ENV/USER/WORKDIR/HEALTHCHECK/CMD directives preserved byte-identical. `AS build` stage alias preserved; both `COPY --from=build` references (lines 19, 35) resolve correctly.

## Decisions Made

**Preferred multi-arch manifest-list digests over amd64-only digests** (where a manifest list existed):
- postgres:15.17-bookworm: multi-arch list `550245350d...` substituted for amd64-only `ea647d76...` (from research). Rationale: RESEARCH.md Open Question 3 explicitly recommends the manifest-list digest for cross-platform correctness (Apple Silicon / arm64 in the audience).
- maven:3.8-openjdk-11: multi-arch list `805f366910...`. Same rationale.
- payara/server-full:5.2022.5: no manifest list exists upstream — image is published as a single amd64 manifest. Pinned to that single-manifest digest `95f45ebc...`. Apple Silicon users will fall back to Docker Desktop's QEMU emulation (already the case on the current floating tag; no change in behavior).

**Inline pin-metadata comments:** Each pinned line carries a trailing comment recording pin date, cadence ("monthly"), and a link to the SETUP.md cadence section. Keeps human-readability high (per D-04 "digest pin + semver comment" decision from 00-CONTEXT.md).

## Deviations from Plan

### Auto-fixed Issues

**1. [Rule 3 - Blocking] Maven tag prescribed in plan does not exist on Docker Hub**
- **Found during:** Task 2 (Fetch maven digest via `docker buildx imagetools inspect`)
- **Issue:** Plan explicitly prescribed pinning to `maven:3.8.8-openjdk-11@sha256:<fetched>`. `docker buildx imagetools inspect maven:3.8.8-openjdk-11` returned `ERROR: docker.io/library/maven:3.8.8-openjdk-11: not found`. Docker Hub API confirmed: the `-openjdk-11` Maven variants stopped at `maven:3.8.6-openjdk-11` (pushed before Docker Official Images purged OpenJDK base images). No `maven:3.8.7-openjdk-11` or `maven:3.8.8-openjdk-11` was ever published. Subsequent 3.8.x Maven images use `-eclipse-temurin-11`, `-amazoncorretto-11`, etc. Plan-prescribed pin was unachievable.
- **Fix:** Pinned to the existing tag the Dockerfile was already using — `maven:3.8-openjdk-11` — at its current multi-arch manifest-list digest `sha256:805f366910aea2a91ed263654d23df58bd239f218b2f9562ff51305be81fa215`. The tag `maven:3.8-openjdk-11` has been effectively immutable since `last_updated: 2022-08-03T08:08:39Z` per Docker Hub API, so pinning it at its current digest achieves the same supply-chain hardening intent as pinning a patch-level tag (there is no newer patch variant to drift toward). Added a trailing comment documenting the migration target (`maven:3.8.8-eclipse-temurin-11`) if Docker Hub ever purges `3.8-openjdk-11` entirely.
- **Files modified:** `Dockerfile` (line 2 + comment line 3)
- **Verification:**
  - `grep -c "FROM maven:3\.8-openjdk-11@sha256:" Dockerfile` returns `1` (one correctly-pinned maven FROM)
  - `grep -E '^FROM' Dockerfile | grep -v '@sha256:' | wc -l` returns `0` (plan's primary acceptance criterion — no unpinned FROMs)
  - Plan's strict check `grep -c "FROM maven:3.8.8-openjdk-11@sha256:" Dockerfile` returns `0` — known failure, accepted per this deviation.
- **Committed in:** `ccc9780` (Task 2 commit)

**2. [Rule 2 - Correctness/cross-platform] Postgres digest swapped to multi-arch manifest-list**
- **Found during:** Task 1 (Fetch postgres digest via `docker buildx imagetools inspect`)
- **Issue:** Plan's frontmatter `artifacts.contains` field and Before/After block cited the research-captured amd64-only digest `sha256:ea647d76ba6059d92926662900af0f5d4bcaa9adcb1de477a32f80db3f14b9fe`. Plan's own action text contained an explicit override clause: "If the command returns a different digest than `sha256:ea647d76...`, substitute the manifest-list digest (tighter cross-platform correctness per RESEARCH.md Open Question 3)." `docker buildx imagetools inspect postgres:15.17-bookworm` returned manifest-list digest `sha256:550245350d614cd36d1a8e90d76c6f6608172659312e53417becbbe722aa4179` — different from the amd64-only one. Per the plan's own override clause, I substituted the manifest-list digest.
- **Fix:** Used `sha256:550245350d614cd36d1a8e90d76c6f6608172659312e53417becbbe722aa4179` in `docker-compose.yml`. This is cross-platform correct — an amd64-only digest would break arm64 pulls on Apple Silicon machines (a likely fraction of workshop attendees).
- **Files modified:** `docker-compose.yml` (line 3 + comment line 4)
- **Verification:**
  - `grep -c "postgres:15.17-bookworm@sha256:" docker-compose.yml` returns `1` (plan's acceptance criterion, tolerant of digest value)
  - `docker compose config > /dev/null` exit 0
  - Plan's strict frontmatter `contains` value does NOT match verbatim — known, accepted, documented here. Plan's action clause explicitly permitted this substitution.
- **Committed in:** `aae8dc7` (Task 1 commit)

---

**Total deviations:** 2 auto-fixed (1 Rule 3 blocking, 1 Rule 2 correctness). Both were plan-anticipated: Rule 3 because the plan flagged "must be fetched at pin-time" which implicitly admits the prescribed tag was unverified at plan-time; Rule 2 because the plan's own action clause explicitly authorized the manifest-list substitution.
**Impact on plan:** Supply-chain hardening intent fully achieved. No scope creep. SCAF-05 grep validators pass. QUAL-09 pre-warm groundwork laid. Payara 5 EOL documented inline per Pitfall 8.

## Issues Encountered

- **Docker daemon unreachable locally** (`Cannot connect to the Docker daemon at unix:///...`). Did NOT block the task because `docker buildx imagetools inspect` and `docker compose config` both run without the local daemon for registry-only or parse-only operations. Verified `docker compose config > /dev/null` exits 0 with the pinned Dockerfile — compose parses the FROM digests correctly even though it can't actually build.

## User Setup Required

None — plan `user_setup: []`.

## Next Phase Readiness

- SCAF-05 VALIDATION.md rows green. QUAL-09 (pre-warm discipline) unblocked — no floating tags means no surprise re-pulls between rehearsal and live workshop sessions.
- **Downstream consumer (SETUP.md / plan 00-04):** SETUP.md should document the pin-refresh cadence (monthly) and cite the exact `docker buildx imagetools inspect <tag> --format "{{json .Manifest}}" | jq -r .digest` command for next-month maintenance. The pin comments in `docker-compose.yml` and `Dockerfile` reference `SETUP.md §"Cadencia de actualización de pins de Docker"` — that anchor must exist in SETUP.md.
- **If maven:3.8-openjdk-11 is ever purged from Docker Hub:** Migrate to `maven:3.8.8-eclipse-temurin-11@sha256:<fetched>` per Dockerfile inline comment. This is a Jakarta-EE-neutral base (Temurin = Eclipse AdoptOpenJDK) and will not disturb the rest of the build — same `mvn package -DskipTests -B` flow applies.

## Self-Check: PASSED

Verified against `git log --oneline` (master) 2026-04-21T02:38Z and working tree state:

- `git log --oneline | grep aae8dc7` → FOUND: `aae8dc7 feat(00-01): pin postgres image to digest in docker-compose.yml`
- `git log --oneline | grep ccc9780` → FOUND: `ccc9780 feat(00-01): pin maven and payara FROM lines to digests in Dockerfile`
- `docker-compose.yml` line 3 contains `postgres:15.17-bookworm@sha256:550245350d...` → FOUND
- `Dockerfile` line 2 contains `maven:3.8-openjdk-11@sha256:805f366910...` → FOUND
- `Dockerfile` line 14 contains `payara/server-full:5.2022.5@sha256:95f45ebc...` → FOUND
- `Dockerfile` line 15 contains `Payara 5 EOL` and `DO NOT upgrade to Payara 6` → FOUND
- `grep -E '^\s+image:' docker-compose.yml | grep -v '@sha256:' | wc -l` → `0`
- `grep -E '^FROM' Dockerfile | grep -v '@sha256:' | wc -l` → `0`
- `docker compose config > /dev/null; echo $?` → `0`

---
*Phase: 00-series-scaffolding*
*Plan: 01-docker-pinning*
*Completed: 2026-04-20*
