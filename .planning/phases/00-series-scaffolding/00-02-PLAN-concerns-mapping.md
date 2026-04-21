---
phase: 00-series-scaffolding
plan: 02
type: execute
wave: 1
depends_on: []
files_modified:
  - docs/presentations/CONCERNS-MAPPING.md
autonomous: true
requirements:
  - SCAF-06
tags:
  - documentation
  - concerns
  - traceability

must_haves:
  truths:
    - "Every HIGH/MED item in .planning/codebase/CONCERNS.md has a slice ID and a claim status (claimed / candidate / deferred) in CONCERNS-MAPPING.md"
    - "The 5 XSS sites are split into XSS-01..XSS-05 and claimed to Session 8 (per S08-01)"
    - "TORNEO-GODCLASS is claimed to Session 6 (per S06-01); TORNEO-N1 is separate"
    - "Deferred items live in the same file under ## Deferred / Out-of-Scope — no second doc"
    - "Per-session plan-phases can finalize candidate claims without structural drift (only 3 rows are locked: XSS-01..04 to S8, TORNEO-GODCLASS to S6, DOCKER-PINS to Phase 0)"
  artifacts:
    - path: "docs/presentations/CONCERNS-MAPPING.md"
      provides: "Authoritative master table of HIGH/MED CONCERNS slices → session claims + deferred list"
      contains: "Demo-Task Bank — HIGH/MED Claims"
  key_links:
    - from: "docs/presentations/CONCERNS-MAPPING.md"
      to: ".planning/codebase/CONCERNS.md"
      via: "slice IDs (XSS-01..XSS-05, PVPGN-PREP/ROLLOVER/FINALIZE, TORNEO-GODCLASS, TORNEO-N1, DOCKER-PINS)"
      pattern: "Slice ID"
    - from: ".planning/ROADMAP.md Phase 6 notes"
      to: "docs/presentations/CONCERNS-MAPPING.md TORNEO-GODCLASS row"
      via: "forward-link (ROADMAP → CONCERNS-MAPPING, per D-08)"
      pattern: "TORNEO-GODCLASS"
---

<objective>
Author the master demo-task bank table at `docs/presentations/CONCERNS-MAPPING.md` that pre-claims every HIGH/MED CONCERNS.md item to a specific session (claimed / candidate) or explicitly defers it. Without this file, every session plan-phase would re-argue "what do we demo?", and CURR-06 (migration-slice discipline) + QUAL-05 (every session touches CONCERNS) + QUAL-07 (57-min budget) would have no stable anchor.

**Purpose:** Single-source demo-task bank. Satisfies SCAF-06 and ROADMAP.md Phase 0 Success Criterion #5 ("every HIGH/MED CONCERNS item has claimed-by / deferred annotation; no HIGH/MED unclaimed when Session 1 begins"). Locks XSS-01..04 to Session 8 (per S08-01 which specifies `/dota-audit-xss` across 5 XSS sites) and TORNEO-GODCLASS to Session 6 (per S06-01 which names `TorneoService.java` subagent investigation). Everything else is `candidate` (to be finalized at per-session plan-phase) or `deferred` (out of scope for this arc).

**Output:** One new file (`docs/presentations/CONCERNS-MAPPING.md`) with two sections — `## Demo-Task Bank — HIGH/MED Claims` (14 slice rows) and `## Deferred / Out-of-Scope` (subset rows with reason + revisit marker).
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
@.planning/codebase/CONCERNS.md
@./CLAUDE.md
</context>

<tasks>

<task type="auto" tdd="false">
  <name>Task 1: Author docs/presentations/CONCERNS-MAPPING.md master table + deferred section</name>
  <files>docs/presentations/CONCERNS-MAPPING.md</files>
  <read_first>
    - .planning/codebase/CONCERNS.md (full file — the HIGH/MED items feeding every row)
    - .planning/phases/00-series-scaffolding/00-CONTEXT.md §"CONCERNS Demo-Task Mapping" (D-05..D-08 locked decisions)
    - .planning/phases/00-series-scaffolding/00-RESEARCH.md §"CONCERNS → Session Mapping" (14-slice enumeration with rationale)
    - .planning/phases/00-series-scaffolding/00-PATTERNS.md §"docs/presentations/CONCERNS-MAPPING.md (master table — partial analog)" (exact table structure + Pitfall 7 over-locking warning)
    - .planning/phases/00-series-scaffolding/00-VALIDATION.md (SCAF-06 grep rows — claim table must have ≥5 Session-claim rows; all 9 specific slice IDs must appear)
  </read_first>
  <action>
    Create a new file at `docs/presentations/CONCERNS-MAPPING.md` with the exact content below. Content is Spanish-prose with English IDs/session names per the mixed convention of `.planning/codebase/CONCERNS.md` (CONTEXT D-05 + PATTERNS §Spanish-language rule). Do not translate slice IDs — they must stay greppable (per D-06 stability requirement).

    **Write this file verbatim:**

    ```markdown
    # CONCERNS → Session mapping

    **Fuente:** `.planning/codebase/CONCERNS.md`. Esta doc es autoritativa para claims de demo-task.

    Por decisión D-06, los claims son a nivel de **slice**, no a nivel de concern completo.
    Los IDs de slice (`XSS-01`..`XSS-05`, `PVPGN-PREP` / `PVPGN-ROLLOVER` / `PVPGN-FINALIZE`,
    `TORNEO-GODCLASS`, `TORNEO-N1`, `DOCKER-PINS`) son **estables y grepeables** —
    `grep -r XSS-02 docs/presentations/` debe retornar cada deck y MANIFEST que lo use.

    Por D-08, esta doc es **autoritativa**: `ROADMAP.md` forward-linkea aquí, no al revés.
    Por D-07, los items diferidos viven abajo en `## Deferred / Out-of-Scope`, no en otro archivo.

    ## Demo-Task Bank — HIGH/MED Claims

    | Severity | CONCERNS section/ID | Slice ID | Claimed by | Status | Notes |
    |----------|---------------------|----------|------------|--------|-------|
    | HIGH | Security §XSS (`VerNoticia.xhtml`) | XSS-01 | Session 8 | claimed | Audited live vía `/dota-audit-xss` (S08-01). Candidato alternativo para Session 1 fix en vivo. |
    | HIGH | Security §XSS (`VerTorneo.xhtml`) | XSS-02 | Session 8 | claimed | Audited vía `/dota-audit-xss` (S08-01). |
    | HIGH | Security §XSS (`VerVideos.xhtml`) | XSS-03 | Session 8 | claimed | Audited vía `/dota-audit-xss` (S08-01). |
    | HIGH | Security §XSS (`Seleccion.xhtml`) | XSS-04 | Session 8 | claimed | Audited vía `/dota-audit-xss` (S08-01). |
    | HIGH | Security §XSS (`index.xhtml`) | XSS-05 | Session 8 | candidate | Auditado por `/dota-audit-xss`; también candidato para Session 5 como target del Skill `escape-false-guard`. Decisión final en Session 5 plan-phase. |
    | HIGH | Security §PvpgnHash | PVPGN-PREP | Session 5 o 6 | candidate | Setup de columnas dual-hash + plan de migración. Decisión final en Session 5 plan-phase (Skill `pvpgn-hash-safety`) o Session 6 (Agent implementa la migración). |
    | HIGH | Security §PvpgnHash | PVPGN-ROLLOVER | Session 6 o deferred | candidate | Dual-hash on login; slice de mitad de migración. Si CURR-01 chain selecciona PvpgnHash, cae en Session 6. |
    | HIGH | Security §PvpgnHash | PVPGN-FINALIZE | deferred | deferred | Remover legacy hash; trabajo operacional multi-semana. v2. |
    | MED | Tech debt §TorneoService god-class | TORNEO-GODCLASS | Session 6 | claimed | Target del subagent (S06-01) — "read `src/java/com/dotachile/torneos/service/TorneoService.java` and return structured TODO report". |
    | HIGH | Performance §N+1 en TorneoService | TORNEO-N1 | Session 5 o 6 | candidate | Slice distinto de `TORNEO-GODCLASS` — 1-2 commits, `@EntityGraph` / fetch-joins en standings. Decisión final en Session 5/6 plan-phase. |
    | MED | Fragile §Docker pins | DOCKER-PINS | Phase 0 | claimed | Satisfecho por SCAF-05 (plan 00-01) — `docker-compose.yml` + `Dockerfile` pinneados a `@sha256:`. |
    | MED | Tech debt §TODOs scattered (39 instancias) | TODOS-SWEEP | deferred | deferred | Grep target útil; material para Session 6 subagent stretch demo si hay tiempo. No es slice primario. |
    | MED | Security §FilesServlet path traversal | FILES-TRAVERSAL | deferred | deferred | Auditoría multi-hora; no es demo-shape. v2. |
    | MED | Security §CSRF ausente en JSF forms | CSRF-ABSENT | deferred | deferred | Config + auditoría; multi-hora. v2. |
    | MED | Security §FORM auth + plaintext en tránsito | FORM-AUTH-TLS | deferred | deferred | Concern operacional (TLS termination); no es demo-shape. v2. |
    | HIGH | Tech debt §Stack EOL | EOL-STACK | deferred | deferred | Estructural — no cabe en una sesión. Material de framing para Session 2 ("por qué legacy stacks son topic de enseñanza"). v2 si el arco se extiende. |
    | MED | Performance §Scheduled jobs sin batching | SCHED-BATCH | deferred | deferred | Multi-file, baja urgencia (volumen actual bajo). v2. |
    | MED | Performance §06:00 scheduler contention | SCHED-CONTENTION | deferred | deferred | Operacional; no es demo-shape. v2. |
    | MED | Fragile §`dev-sync.sh` load-bearing | DEV-SYNC-FRAGILE | deferred | deferred | Documentación / hardening; no es demo. v2. |
    | MED | Fragile §Entity `equals`/`hashCode` | ENTITY-EQUALS | deferred | deferred | Teaching topic interesante pero no session-sized. v2. |

    ## Deferred / Out-of-Scope

    Estos slices NO son parte del arco 2026 (9 sesiones). Se mantienen en el
    trail para v2 o para resurgimiento explícito vía `/gsd-insert-phase`.

    | Slice ID | Razón | Revisitar |
    |----------|-------|-----------|
    | EOL-STACK | Estructural; ningún slice de una sesión lo resuelve | v2 arc |
    | PVPGN-FINALIZE | Operacional multi-semana | v2 |
    | FILES-TRAVERSAL | Auditoría multi-hora, no demo | v2 |
    | CSRF-ABSENT | Config + auditoría, no session-sized | v2 |
    | FORM-AUTH-TLS | Concern operacional (TLS) | v2 |
    | SCHED-BATCH | Multi-file, baja urgencia | v2 |
    | SCHED-CONTENTION | Operacional | v2 |
    | DEV-SYNC-FRAGILE | Hardening, no demo | v2 |
    | TODOS-SWEEP | Grep fodder, no slice primario | v2 (o Session 6 stretch) |
    | ENTITY-EQUALS | Teaching topic, no session-sized | v2 |

    ## Reglas de actualización

    1. **Cambios de claim** (candidate → claimed, deferred → candidate, etc.) se hacen
       **en esta doc**, en el plan-phase de la sesión correspondiente.
    2. **No** se edita `.planning/codebase/CONCERNS.md` desde aquí — es la fuente.
    3. **No** se duplican slice IDs — un slice vive en exactamente una fila de la tabla de claims.
    4. `ROADMAP.md` forward-linkea a este archivo (e.g., "Session 6 uses `TORNEO-GODCLASS` from CONCERNS-MAPPING.md"); este archivo NO back-linkea a ROADMAP.

    <!-- see docs/presentations/QUAL-GATES.md §QUAL-05 — "cada sesión toca ≥1 file/artifact de CONCERNS.md" depende de esta tabla. -->
    ```

    **Constraints per CONTEXT + PATTERNS + RESEARCH:**
    - **D-05 single master table:** No second file. Deferred items in `## Deferred / Out-of-Scope` of this same file.
    - **D-06 slice granularity:** XSS-01..XSS-05 separately; PVPGN-PREP / PVPGN-ROLLOVER / PVPGN-FINALIZE separately; TORNEO-GODCLASS vs TORNEO-N1 are distinct rows.
    - **D-07 deferred in same file:** Every deferred item appears in both the main claims table (with `status=deferred`) AND in the `## Deferred / Out-of-Scope` section with `Revisitar` column.
    - **D-08 unidirectional link:** This doc is authoritative. Do NOT add back-references to ROADMAP.md phase numbers in the table rows (rows name session numbers, not ROADMAP sections).
    - **Pitfall 7 (CONTEXT/RESEARCH):** Only XSS-01..XSS-04 to Session 8, TORNEO-GODCLASS to Session 6, and DOCKER-PINS to Phase 0 are `claimed` (tightly coupled to per-session REQs). Everything else is `candidate` or `deferred` — per-session plan-phase finalizes.
    - **Spanish-language rule:** Prose is Spanish; slice IDs and session names are English (per `.planning/codebase/CONCERNS.md` mixed convention).
    - **QUAL-GATES.md reference:** The trailing `<!-- see QUAL-GATES.md §QUAL-05 -->` comment is required by D-04 cross-reference convention. QUAL-GATES.md is authored in plan 05; the link is forward-valid.
  </action>
  <verify>
    <automated>test -f docs/presentations/CONCERNS-MAPPING.md && for s in XSS-01 XSS-02 XSS-03 XSS-04 XSS-05 PVPGN-PREP TORNEO-GODCLASS TORNEO-N1 DOCKER-PINS; do grep -q "$s" docs/presentations/CONCERNS-MAPPING.md || echo "MISSING: $s"; done</automated>
  </verify>
  <acceptance_criteria>
    - `test -f docs/presentations/CONCERNS-MAPPING.md` succeeds (file exists)
    - VALIDATION.md SCAF-06 row 1: `grep -c "| .* | .* | .* | Session" docs/presentations/CONCERNS-MAPPING.md` returns `≥5` (five+ session-claim rows)
    - VALIDATION.md SCAF-06 row 2: `for s in XSS-01 XSS-02 XSS-03 XSS-04 XSS-05 PVPGN-PREP TORNEO-GODCLASS TORNEO-N1 DOCKER-PINS; do grep -q "$s" docs/presentations/CONCERNS-MAPPING.md || echo MISSING; done` returns empty (all 9 slice IDs present)
    - `grep -c "^## Demo-Task Bank — HIGH/MED Claims" docs/presentations/CONCERNS-MAPPING.md` returns `1` (single master table per D-05)
    - `grep -c "^## Deferred / Out-of-Scope" docs/presentations/CONCERNS-MAPPING.md` returns `1` (deferred section per D-07)
    - `grep -c "Session 8 | claimed" docs/presentations/CONCERNS-MAPPING.md` returns `≥4` (XSS-01..XSS-04 locked to Session 8 per Pitfall 7)
    - `grep -c "Session 6 | claimed" docs/presentations/CONCERNS-MAPPING.md` returns `≥1` (TORNEO-GODCLASS locked to Session 6)
    - `grep -c "Phase 0 | claimed" docs/presentations/CONCERNS-MAPPING.md` returns `≥1` (DOCKER-PINS claimed by Phase 0)
    - `grep -c "candidate" docs/presentations/CONCERNS-MAPPING.md` returns `≥3` (XSS-05, PVPGN-PREP, PVPGN-ROLLOVER, TORNEO-N1 — Pitfall 7 no-over-lock guard)
    - `grep -q "QUAL-05" docs/presentations/CONCERNS-MAPPING.md` succeeds (D-04 cross-ref to QUAL-GATES.md)
    - `grep -c "v2" docs/presentations/CONCERNS-MAPPING.md` returns `≥5` (deferred items mark future-arc revisit)
  </acceptance_criteria>
  <done>
    `docs/presentations/CONCERNS-MAPPING.md` exists with the master claims table + deferred section; all 9 canonical slice IDs greppable; only 6 rows locked (XSS-01..XSS-04 to S8, TORNEO-GODCLASS to S6, DOCKER-PINS to Phase 0) — everything else candidate/deferred per Pitfall 7.
  </done>
</task>

</tasks>

<threat_model>
No security-sensitive surface — docs-only artifact. CONCERNS-MAPPING.md transforms an existing inventory (`.planning/codebase/CONCERNS.md`) into session claims; it does not introduce code or config that changes runtime behavior.
</threat_model>

<verification>
Run the VALIDATION.md SCAF-06 row set:

```bash
test -f docs/presentations/CONCERNS-MAPPING.md
grep -c "| .* | .* | .* | Session" docs/presentations/CONCERNS-MAPPING.md   # ≥5
for s in XSS-01 XSS-02 XSS-03 XSS-04 XSS-05 PVPGN-PREP TORNEO-GODCLASS TORNEO-N1 DOCKER-PINS; do
  grep -q "$s" docs/presentations/CONCERNS-MAPPING.md || echo "MISSING: $s"
done
```

All must pass; second command must return empty.
</verification>

<success_criteria>
Master claims table covers every HIGH/MED concern from `.planning/codebase/CONCERNS.md` with either a session claim (6 rows: 5 XSS to S8, TORNEO-GODCLASS to S6, DOCKER-PINS to Phase 0) or an explicit deferral. Slice IDs stable/greppable. No over-locking — candidate rows preserve per-session plan-phase flexibility.
</success_criteria>

<output>
After completion, create `.planning/phases/00-series-scaffolding/00-02-SUMMARY.md` recording:
- Count of HIGH/MED rows, count of claimed vs candidate vs deferred
- List of the 9 greppable slice IDs
- Any deviation from the 14-row starting point in RESEARCH.md §"CONCERNS → Session Mapping"
</output>
