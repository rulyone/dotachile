# automation

Schedule timer-based cleanup jobs to remove expired data: inactive pre-registrations, expired bans, stale clan invitations, and unaccepted challenges.

## Glossary

- **Automatización** — time-driven job executed via `@Schedule` EJB timer.
- **Baneado** — a `Usuario` member of the BANEADO group; ban state expires after N days unless infinite.
- **PreRegistro** — unactivated user signup; removed by `removerCuentasNoActivadas`.
- **BanHistory** — immutable audit trail; created when a ban expires via `checkearBansYRemoverDeGrupoBaneados`.
- **Desafío** — challenge; removed if never accepted within retention window via `eliminarDesafiosNoAceptados`.

## Rules

- All pre-registrations are removed at 05:00 server local time each day via `removerCuentasNoActivadas`.
- When a ban expires, remove the user from the BANEADO group, create a `BanHistory` record, and force logout via `checkearBansYRemoverDeGrupoBaneados`.
- Ban expiry is determined by adding `ban.getDiasBan()` to `ban.getFechaBan()`; infinite bans (diasBan ≤ 0) never expire.
- All pending clan invitations across every clan are cleared at 06:00 server local time each day via `eliminarInvitacionesDeClan`.
- Unaccepted challenges are removed at 07:00 server local time each day via `eliminarDesafiosNoAceptados`.

## Entry points

- `ServiciosAutomatizados` — `@Singleton` EJB that owns all four `@Schedule` timer methods.
- `FunService` — currently unused; do not add new business logic here.

<!-- Historical refactors moved automation to com.dotachile.automation in commit 34495b7. -->