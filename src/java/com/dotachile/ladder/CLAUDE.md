# ladder

Clan ranking and challenge system where clans issue, play, and confirm match results to earn ELO points on a single global ladder.

## Glossary

- **Ladder** — the global ranking board. Holds `FaseLadder` state, a `FactorK`, and owns all `Desafio` instances.
- **FaseLadder** — enum: `STARTED` (active) or `PAUSED` (challenges blocked at UI level).
- **Desafio** — a challenge between two `Clan` entities (`desafiador` → `rival`). Two boolean flags track state: `desafioAceptado` and `resultadoConfirmado`.
- **desafiador** — the `Clan` that initiated the challenge via `LadderService.desafiarClan`.
- **rival** — the `Clan` that was challenged; must accept or reject via `LadderService.aceptarDesafio/rechazarDesafio`.
- **FactorK** — ELO K-factor assigned to the ladder; passed to `EloSystem.calculoVariacion`.
- **fechaDesafio** — timestamp of challenge creation; milliseconds stripped via `Util.dateSinMillis`.

## Rules

- Only one `Ladder` may exist at a time; `LadderService.crearLadderYComenzarlo` throws if `ladderFac.count() > 0`. New ladders always start as `STARTED`.
- Only a clan's `chieftain` or a `shaman` can issue, accept, or reject a challenge; enforced in `LadderService.desafiarClan`, `aceptarDesafio`, and `rechazarDesafio`.
- Both `desafiador` and `rival` must have at least 5 members to issue (`desafiarClan`) and also to accept (`aceptarDesafio`); checked at both call sites.
- Neither clan may be banned (`ClanBan` record present) at the time of challenge creation or acceptance; `desafiarClan` and `aceptarDesafio` both check both clans.
- Each clan is capped at 10 pending desafíos (not yet accepted **or** result not confirmed); `desafiarClan` rejects if either side already has 10 pending.
- Only the **winning** clan's chieftain or shaman may report the game result; `reportarGameLadder` enforces this. DOBLE_WO is the exception — either clan may report it.
- For non-WO games, each side must supply 3–5 players with no duplicates across sides; `reportarGameLadder` validates counts and uniqueness.
- The **losing** clan's chieftain or shaman must confirm the result; `confirmarResultadoDesafio` enforces this. Only ADMIN_ROOT or ADMIN_DOTA may confirm a DOBLE_WO.
- ELO is recalculated only after confirmation and only for non-DOBLE_WO results; `confirmarResultadoDesafio` calls `modificarElos`, which calls `EloSystem.calculoVariacion` with the ladder's `FactorK`.
- `LadderService` is annotated `@RolesAllowed({"ADMIN_ROOT","ADMIN_DOTA"})` at class level. Methods `desafiarClan`, `aceptarDesafio`, `rechazarDesafio`, `reportarGameLadder`, `eliminarReporteLadder`, and `confirmarResultadoDesafio` override to `@PermitAll`. `cancelarDesafioByAdmin` overrides to `@RolesAllowed({"ADMIN_ROOT","ADMIN_DOTA","ADMIN_LADDER"})`. Admin-only operations (`crearLadderYComenzarlo`, `pausarLadder`, `despausarLadder`) inherit the class-level restriction.
- `DesafioFacade.findDesafiosPendientes()` (all-clans admin view) is `@RolesAllowed({"ADMIN_ROOT","ADMIN_DOTA","ADMIN_LADDER"})`; other query methods on `DesafioFacade` have no role restriction.
- Comments can be posted on a `Desafio` via `ComentariosService.agregarComentarioDesafio`; `Desafio` holds a `@OneToMany` `comentarios` collection.

## Entry points

- `VerLadderMB` — loads the single ladder and clan ELO ranking table; also exposes pause/resume actions.
- `DesafiarClanMB` — landing page for initiating a challenge against a specific clan by tag.
- `VerDesafioMB` — per-desafío view handling accept, reject, admin-cancel, confirm-result, delete-report, and comment actions.
- `ReportarGameLadderWizardMB` — multi-step wizard for reporting a game result (sides → players → replay → submit).

<!-- Only one Ladder (ID=1) is supported. Rank ordering is driven by Clan.elo via ClanFacade.rankClanesLimit. -->
