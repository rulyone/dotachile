# torneos

Multi-clan structured tournament system where an encargado manages rounds, pairing matches, and game reporting across a full lifecycle from registration to champion declaration.

## Glossary

- **Torneo** — tournament aggregate; holds `FaseTorneo`, `TipoTorneo`, `FactorK`, enrolled clans, rounds, and champion.
- **FaseTorneo** — enum: `REGISTRATION` (enrollment open), `STARTED` (rounds in play), `FINISHED` (concluded).
- **TipoTorneo** — enum: `SINGLE_ELIMINATION`, `DOUBLE_ELIMINATION`, `SWISS`, `ROUND_ROBIN`, `CUSTOM`.
- **Ronda** — one round inside a `Torneo`; groups `GameMatch` instances. Created at `startTorneo` and each `avanzarRonda`.
- **GameMatch** (pareo) — a scheduled head-to-head between two clans in a round; holds `bestOf`, `resultadoConfirmado`, and scheduling fields.
- **Game** — one individual game inside a `GameMatch`; records sentinel/scourge clans, 3–5 players per side, and a `Resultado`.
- **Resultado** — enum: `WIN_SENTINEL`, `WIN_SCOURGE`, `WIN_SENTINEL_WO`, `WIN_SCOURGE_WO`, `DOBLE_WO`.
- **FactorK** — ELO K-factor enum (10–100, step 10); passed to `EloSystem.calculoVariacion` per-game on match confirmation.
- **encargado** — the `Usuario` who created the tournament; has operational authority equal to `ADMIN_DOTA` for that tournament.
- **TemporadaModificacion** — an admin-defined date window during which clan rosters may be modified while enrolled in a tournament.
- **Modificacion** — a single roster-change record within a `TemporadaModificacion`; only `TipoModificacion.AGREGAR` exists.
- **Standing** — immutable DTO ranking a clan by matches won, played, lost, then games won/lost, then tag alphabetically.
- **fechaPropuesta** — an alternate match datetime proposed by a clan chief; must be confirmed by the opposing clan.

## Rules

- `TorneoService` is `@RolesAllowed({"ADMIN_ROOT","ADMIN_DOTA","ADMIN_TORNEO"})` at class level. Methods `reportarGame`, `eliminarReporte`, `confirmarMatch`, `inscribirClanTorneo`, `cancelarInscripcionClanTorneo`, `proponerFecha`, `cancelarFechaPropuesta`, `confirmarFechaPropuesta`, `findMatchesPendientesByTag`, `getTagsClanesEnTorneo` override to `@PermitAll`. `eliminarTorneo`, `eliminarPareoInseguro`, `crearTemporadaModificaciones`, `eliminarTemporadaModificaciones`, `inscribirClanTorneoForced`, `cancelarInscripcionClanTorneoForced`, `editarTorneo` override to `@RolesAllowed({"ADMIN_DOTA","ADMIN_ROOT"})`.
- A tournament is created in `REGISTRATION`; only the `encargado`, ADMIN_DOTA, or ADMIN_ROOT may start it (`startTorneo`). Starting creates the first `Ronda` and moves the phase to `STARTED`.
- `startTorneo` checks that enrolled clan count is within `[minCantidadClanes, maxCantidadClanes]` and that no enrolled clan is banned.
- `finalizarTorneo` requires all `GameMatch` results to be confirmed and that the declared champion is one of the enrolled clans; sets `clanCampeon` and transitions phase to `FINISHED`.
- Pairing (`agregarPareo`) is only allowed while the torneo is `STARTED`; a clan may not appear more than once per ronda, and bestOf must be a positive odd integer.
- Only the winning clan's `chieftain` or `shaman` may report (`reportarGame`); DOBLE_WO may only be reported by ADMIN_DOTA/ROOT or the encargado (if not a member of either clan). Non-WO games require 3–5 players per side with no duplicates.
- ELO is adjusted via `EloSystem.calculoVariacion` per-game at the moment `confirmarMatch` is called; DOBLE_WO games yield zero ELO change. Reversal via `eliminarPareoInseguro` re-applies the inverse variation.
- The **losing** clan's chieftain/shaman must confirm (`confirmarMatch`); ADMIN_DOTA/ROOT may always confirm; the encargado may confirm unless they are a member of the winning clan; an all-DOBLE_WO match requires ADMIN_DOTA/ROOT.
- Always format proposed-date notification comments with `TimeZone.getTimeZone("America/Santiago")` and `new Locale("es","CL")`; enforced in `TorneoService.proponerFecha` since commit `aa7ad10` (fixes 3-hour offset bug).
- `TemporadaModificacion` date ranges must not overlap; `crearTemporadaModificaciones` rejects any range that intersects an existing season. Dates are stored time-stripped via `Util.dateSinTime`.
- Standings sort order: matches won (desc), matches played (desc), matches lost (asc), games won (desc), games lost (asc), then clan tag (asc); computed inline in `Torneo.getStandings`.
- `inscribirClanTorneo` reads clan roster limits from context init-params `com.tarreo.dota.torneo.minPlayersPorClan` / `maxPlayersPorClan`; `inscribirClanTorneoForced` skips this check.

## Entry points

- `VerTorneoMB` — tournament detail page; drives enroll/leave, start, advance-round, delete, and comment actions.
- `CrearTorneoMB` — creation form; always uses `TipoTorneo.CUSTOM` regardless of enum width.
- `ReportarGameWizardMB` — multi-step wizard: choose sides → pick 3–5 players per side → upload replay → submit game.
- `VerMatchMB` — per-match view; handles confirm, delete report, modify bestOf, propose/cancel/confirm schedule date.

<!-- BasicService is unrelated to torneos business logic: it handles user registration, account activation, password resets, and PvPGN W3 account provisioning. -->