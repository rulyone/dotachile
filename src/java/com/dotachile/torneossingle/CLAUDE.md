# torneossingle

Player-based single-elimination tournaments with bracket-based rounds and matches.

## Glossary

- **TorneoSingle** — a single-elimination tournament for individual `Usuario` players, with min/max constraints, a tournament phase (`faseTorneo`), and champion tracking.
- **RondaSingle** — one round within a `TorneoSingle`, containing multiple `SingleMatch` entries; ordered by creation date.
- **SingleMatch** — a single competitive match between two players (`player1`, `player2`), with an optional winner (`playerGanador`) and arbiter.
- **playersInscritos** — collection of `Usuario` entities registered for a `TorneoSingle`; stored in join table `players_por_torneo`.
- **playerCampeon** — the `Usuario` who won the tournament (last match winner); null until tournament concludes.
- **faseTorneo** — tournament phase enum from `FaseTorneo`, starting at REGISTRATION.

## Rules

- Tournament creation requires non-null name, information, min, and max player counts; `SingleTorneoService.crearTorneo` throws `BusinessLogicException` if any is null.
- Max and min player counts must both be >= 2; `crearTorneo` validates both and throws if either is below 2.
- Max player count must be >= min player count; `crearTorneo` validates and throws if max < min.
- Every new tournament starts in `FaseTorneo.REGISTRATION` phase; `crearTorneo` sets phase on creation.
- Player inscriptions are stored in the `players_por_torneo` join table via the `playersInscritos` relationship on `TorneoSingle`.
- Matches within a round belong to that round via the `ronda` relationship on `SingleMatch`; matches are ordered within `RondaSingle.matches`.
- Tournament champion is set when the final match completes; `playerCampeon` tracks the winner.

## Entry points

- `SingleTorneoService` — tournament creation and validation; defines bracket constraints and phase transitions.
- `TorneoSingleFacade` — entity persistence for `TorneoSingle` (CRUD operations).
- `RondaSingleFacade` — entity persistence for `RondaSingle` (CRUD operations).
- `SingleMatchFacade` — entity persistence for `SingleMatch` (CRUD operations).

<!-- No dedicated controllers in torneossingle module; invoked via torneos controllers or external orchestration. -->
