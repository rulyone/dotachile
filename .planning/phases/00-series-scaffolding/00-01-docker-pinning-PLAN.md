---
phase: 00-series-scaffolding
plan: 01
type: execute
wave: 1
depends_on: []
files_modified:
  - docker-compose.yml
  - Dockerfile
autonomous: true
requirements:
  - SCAF-05
  - QUAL-09
tags:
  - docker
  - supply-chain
  - infrastructure
user_setup: []

must_haves:
  truths:
    - "docker-compose.yml no contiene ningún tag flotante — todas las imágenes están pinneadas por digest"
    - "Dockerfile no contiene ningún FROM con tag flotante — todas las capas están pinneadas por digest"
    - "docker compose config ejecuta sin error y resuelve los digests correctamente"
    - "Payara 5 EOL está documentado inline en el Dockerfile con warning explícito"
  artifacts:
    - path: "docker-compose.yml"
      provides: "Compose file con postgres pinneado por digest"
      contains: "postgres:15.17-bookworm@sha256:ea647d76ba6059d92926662900af0f5d4bcaa9adcb1de477a32f80db3f14b9fe"
    - path: "Dockerfile"
      provides: "Dockerfile con maven y payara pinneados por digest"
      contains: "payara/server-full:5.2022.5@sha256:95f45ebc141eb68f1e572725b570aad03059a4e8ab34e590f8f7c7259011df75"
  key_links:
    - from: "docker-compose.yml services.db.image"
      to: "Docker Hub postgres:15.17-bookworm digest"
      via: "@sha256: pin syntax"
      pattern: "image:.*postgres.*@sha256:"
    - from: "Dockerfile FROM payara/server-full"
      to: "Docker Hub payara 5.2022.5 digest"
      via: "@sha256: pin syntax"
      pattern: "FROM payara/server-full.*@sha256:"
---

<objective>
Pin every Docker image reference in `docker-compose.yml` and `Dockerfile` to an immutable `@sha256:` digest, so `docker compose up -d` never pulls a mutated image mid-workshop-arc. Satisfies SCAF-05 (digest pinning) and unblocks QUAL-09 (pre-warm discipline — no floating tags means no surprise re-pulls 10 minutes before a live session).

**Purpose:** Supply-chain hardening (STRIDE Tampering via malicious-code substitution at a floating tag). Without pinned digests, a compromised `postgres:15` tag on Docker Hub between rehearsal and live session would re-pull a different binary. This is the single highest-leverage Phase 0 artifact for Pitfall 8 prevention.

**Output:** Two existing files mutated in place (no new files). `docker-compose.yml` service `db.image` line + `Dockerfile` two `FROM` lines. Pre-verified digests come from RESEARCH.md §"Docker Tag Pinning" (captured 2026-04-20). Maven stage digest must be fetched at execute-time since it was not frozen in research.
</objective>

<execution_context>
@/Users/pmartinez/Documents/git/quantumentangled/dotachile/.claude/get-shit-done/workflows/execute-plan.md
@/Users/pmartinez/Documents/git/quantumentangled/dotachile/.claude/get-shit-done/templates/summary.md
</execution_context>

<context>
@.planning/PROJECT.md
@.planning/ROADMAP.md
@.planning/STATE.md
@.planning/phases/00-series-scaffolding/00-CONTEXT.md
@.planning/phases/00-series-scaffolding/00-RESEARCH.md
@.planning/phases/00-series-scaffolding/00-PATTERNS.md
@.planning/phases/00-series-scaffolding/00-VALIDATION.md
@.planning/codebase/STACK.md
@docker-compose.yml
@Dockerfile
@./CLAUDE.md
</context>

<tasks>

<task type="auto" tdd="false">
  <name>Task 1: Fetch multi-arch digest for maven:3.8.8-openjdk-11 and pin docker-compose.yml postgres image</name>
  <files>docker-compose.yml</files>
  <read_first>
    - docker-compose.yml — current state (line 3 has `image: postgres:15` floating)
    - .planning/phases/00-series-scaffolding/00-RESEARCH.md §"Docker Tag Pinning" — verified digests for postgres and payara (2026-04-20)
    - .planning/phases/00-series-scaffolding/00-PATTERNS.md §"`docker-compose.yml` (mutate — SCAF-05 pin)" — exact mutation pattern
    - .planning/phases/00-series-scaffolding/00-CONTEXT.md — Claude's Discretion: "digest pin + semver comment" pattern
  </read_first>
  <action>
    Modify `docker-compose.yml` — exactly one line change plus one comment line added below.

    **Before (line 3):**
    ```yaml
      db:
        image: postgres:15
    ```

    **After (replace line 3, add comment line below):**
    ```yaml
      db:
        image: postgres:15.17-bookworm@sha256:ea647d76ba6059d92926662900af0f5d4bcaa9adcb1de477a32f80db3f14b9fe
        # Pinned 2026-04-20. Update cadence: monthly. See docs/presentations/SETUP.md §"Cadencia de actualización de pins de Docker"
    ```

    **Constraints (per PATTERNS.md §"docker-compose.yml mutate"):**
    - Do NOT re-format the rest of the file. Only the `image:` line + new comment line change.
    - Do NOT change service names `db` or `app` (dev-sync.sh depends on them).
    - Do NOT add `platform: linux/amd64`. The digest cited is amd64-specific per research — BEFORE committing, run `docker buildx imagetools inspect postgres:15.17-bookworm --format "{{json .Manifest}}" | jq -r .digest` to fetch the multi-arch manifest-list digest. If the command returns a different digest than `sha256:ea647d76ba6059d92926662900af0f5d4bcaa9adcb1de477a32f80db3f14b9fe`, substitute the manifest-list digest (tighter cross-platform correctness per RESEARCH.md Open Question 3). If the command fails (no buildx available), fall back to the amd64 digest verified in research.
    - Preserve `${POSTGRES_PASSWORD:-dotachile}` env-var default syntax exactly (lines 6, 25 — DotaChile convention).
    - Do NOT touch the `app:` service block — it uses `build: .` and delegates image pinning to the Dockerfile (Task 2).

    Per D-04 decision the semver tag (`15.17-bookworm`) is kept before the `@` for human readability — digest alone is unreadable.
  </action>
  <verify>
    <automated>grep -E '^\s+image:' docker-compose.yml | grep -v '@sha256:' | wc -l</automated>
  </verify>
  <acceptance_criteria>
    - `grep -E '^\s+image:' docker-compose.yml | grep -v '@sha256:' | wc -l` returns `0` (no unpinned image directives — from VALIDATION.md SCAF-05 row)
    - `grep -c "postgres:15.17-bookworm@sha256:" docker-compose.yml` returns `1`
    - `grep -c "Pinned 2026-04-20" docker-compose.yml` returns `1`
    - `docker compose config > /dev/null` exits with status `0` (parse succeeds with pinned digests)
    - `grep -c "^  db:\|^  app:" docker-compose.yml` returns `2` (service names preserved)
    - `grep -c 'POSTGRES_PASSWORD:-dotachile' docker-compose.yml` returns `2` (env-var defaults preserved on lines 6 and 25)
  </acceptance_criteria>
  <done>
    Both services in docker-compose.yml reference immutable digests (db via pinned `image:`, app via `build: .` which inherits from the pinned Dockerfile); `docker compose config` parses cleanly; no other formatting changes.
  </done>
</task>

<task type="auto" tdd="false">
  <name>Task 2: Pin Dockerfile maven build stage and Payara runtime stage to @sha256 digests</name>
  <files>Dockerfile</files>
  <read_first>
    - Dockerfile — current state (line 2: `FROM maven:3.8-openjdk-11 AS build` / line 13: `FROM payara/server-full:5.2022.5`)
    - .planning/phases/00-series-scaffolding/00-RESEARCH.md §"Docker Tag Pinning" — payara digest verified; maven digest must be fetched
    - .planning/phases/00-series-scaffolding/00-PATTERNS.md §"Dockerfile (mutate — SCAF-05 pin)" — exact 2-line mutation pattern + Payara 5 EOL comment requirement
    - .planning/phases/00-series-scaffolding/00-CONTEXT.md — `[CITED: Pitfall 8]` Payara 5 EOL documentation rule
  </read_first>
  <action>
    Modify `Dockerfile` — exactly two FROM lines change + a comment line after each.

    **Step 1 — Fetch the Maven image digest at execute-time.** Research did NOT verify this digest; planner flagged it as "must be fetched at pin-time". Run:

    ```bash
    docker buildx imagetools inspect maven:3.8.8-openjdk-11 --format "{{json .Manifest}}" | jq -r .digest
    ```

    If `buildx imagetools` is unavailable, fall back to:

    ```bash
    docker manifest inspect maven:3.8.8-openjdk-11 | jq -r '.manifests[] | select(.platform.architecture=="amd64") | .digest'
    ```

    Capture the returned `sha256:...` and substitute for `<FETCHED_MAVEN_DIGEST>` below.

    **Before (line 2):**
    ```dockerfile
    FROM maven:3.8-openjdk-11 AS build
    ```

    **After (replace line 2, add comment line below):**
    ```dockerfile
    FROM maven:3.8.8-openjdk-11@sha256:<FETCHED_MAVEN_DIGEST> AS build
    # Pinned 2026-04-20. Tag maven:3.8.8-openjdk-11 (concrete patch). Update cadence: monthly.
    ```

    **Before (line 13):**
    ```dockerfile
    FROM payara/server-full:5.2022.5
    ```

    **After (replace line 13, add comment line below):**
    ```dockerfile
    FROM payara/server-full:5.2022.5@sha256:95f45ebc141eb68f1e572725b570aad03059a4e8ab34e590f8f7c7259011df75
    # Payara 5 EOL — 5.2022.5 is the last release on the 5.x line. DO NOT upgrade to Payara 6 (Jakarta EE 10 breaks javax.* imports across the codebase).
    ```

    **Constraints (per PATTERNS.md §"Dockerfile mutate"):**
    - Do NOT modify any line other than lines 2 and 13 + the two comment lines added below them.
    - Preserve `AS build` stage alias — lines 17 and 33 reference `--from=build`.
    - Preserve every `COPY`, `RUN`, `ENV`, `HEALTHCHECK`, `USER`, `WORKDIR`, `CMD` directive exactly as-is.
    - The Payara 5 EOL comment is non-optional (per CONTEXT D-enforcement of Pitfall 8). Include it verbatim.
    - For the Maven digest, if the fetch command returns a manifest-list digest vs an amd64-only digest, prefer the manifest-list digest (cross-platform correctness — RESEARCH.md Open Question 3).
  </action>
  <verify>
    <automated>grep -E '^FROM' Dockerfile | grep -v '@sha256:' | wc -l</automated>
  </verify>
  <acceptance_criteria>
    - `grep -E '^FROM' Dockerfile | grep -v '@sha256:' | wc -l` returns `0` (VALIDATION.md SCAF-05 Dockerfile row)
    - `grep -c "FROM payara/server-full:5.2022.5@sha256:95f45ebc141eb68f1e572725b570aad03059a4e8ab34e590f8f7c7259011df75" Dockerfile` returns `1`
    - `grep -c "FROM maven:3.8.8-openjdk-11@sha256:" Dockerfile` returns `1`
    - `grep -c "Payara 5 EOL" Dockerfile` returns `1` (EOL warning present per CONTEXT Pitfall 8 rule)
    - `grep -c "DO NOT upgrade to Payara 6" Dockerfile` returns `1` (concrete anti-upgrade guidance)
    - `grep -c "^COPY --from=build" Dockerfile` returns `2` (multi-stage build references preserved — lines 17, 33)
    - `grep -c "AS build" Dockerfile` returns `1` (stage alias preserved)
    - `docker compose config > /dev/null` exits `0` (compose still parses with the pinned Dockerfile)
  </acceptance_criteria>
  <done>
    Both Dockerfile FROM lines pin by `@sha256:` digest; multi-stage build references (`--from=build`) still resolve; Payara 5 EOL warning is inline; `docker compose config` parses cleanly.
  </done>
</task>

</tasks>

<threat_model>
## Trust Boundaries

| Boundary | Description |
|----------|-------------|
| Docker Hub → local container runtime | Untrusted upstream registry delivers binary images into the local environment |

## STRIDE Threat Register

| Threat ID | Category | Component | Disposition | Mitigation Plan |
|-----------|----------|-----------|-------------|-----------------|
| T-00-V10-01 | Tampering | `docker-compose.yml` `postgres:15` floating tag | mitigate | Pin to `postgres:15.17-bookworm@sha256:ea647d76...` — grep verification `grep -E '^\s+image:' docker-compose.yml \| grep -v '@sha256:' \| wc -l` returns 0. ASVS V10 Malicious Code. |
| T-00-V10-02 | Tampering | `Dockerfile` `FROM maven:3.8-openjdk-11` floating tag | mitigate | Pin to `maven:3.8.8-openjdk-11@sha256:<fetched>` — grep verification `grep -E '^FROM' Dockerfile \| grep -v '@sha256:' \| wc -l` returns 0. ASVS V10. |
| T-00-V10-03 | Tampering | `Dockerfile` `FROM payara/server-full:5.2022.5` — effectively immutable (EOL) but not formally digest-pinned | mitigate | Pin to `payara/server-full:5.2022.5@sha256:95f45ebc...` — defense-in-depth even though Payara 5 tag is EOL-frozen; prevents tag-deletion-and-replace attack. ASVS V10. |
| T-00-V10-04 | Tampering | Monthly digest staleness (pinned image misses upstream security patches) | accept | Documented in SETUP.md `## Cadencia de actualización de pins de Docker` — monthly re-pin procedure. Risk accepted: 1-month security-patch window is the tradeoff against in-workshop reproducibility. ASVS V10. |

**Expected secure behavior:** `docker compose config` shows zero `image:`/`FROM` references without `@sha256:`; re-running `docker compose up -d` between rehearsal and live session fetches the exact same bytes every time.
</threat_model>

<verification>
After both tasks complete, run the full VALIDATION.md SCAF-05 row set:

```bash
grep -E '^\s+image:' docker-compose.yml | grep -v '@sha256:' | wc -l    # expect 0
grep -E '^FROM' Dockerfile | grep -v '@sha256:' | wc -l                 # expect 0
docker compose config > /dev/null && echo "PASS" || echo "FAIL"         # expect PASS
```

Additionally smoke-test the container builds without error:

```bash
docker compose config | head -20   # verify services section parses
```

(Do NOT run `docker compose up -d` as part of verification — that's the QUAL-09 pre-warm ritual, not a per-task check.)
</verification>

<success_criteria>
All SCAF-05 VALIDATION.md rows green. QUAL-09 groundwork laid (no cold pull hazard at session time). No non-pinned image references anywhere in `docker-compose.yml` or `Dockerfile`. Payara 5 EOL documented inline so future readers don't attempt to bump to v6.
</success_criteria>

<output>
After completion, create `.planning/phases/00-series-scaffolding/00-01-SUMMARY.md` recording:
- The exact digests pinned (postgres, maven, payara)
- The source of each digest (research-verified 2026-04-20 vs fetched-at-execute-time)
- Any deviation (e.g., if manifest-list digest was substituted for amd64-only digest)
- `docker compose config` exit status
</output>
