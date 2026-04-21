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
