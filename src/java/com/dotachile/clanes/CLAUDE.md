# clanes

Clan management module covering the full lifecycle of player-owned teams: creation, membership, role hierarchy, invitation flow, and admin bans.

## Glossary

- **Clan** — core entity with unique `nombre` (max 50 chars) and `tag` (max 5 chars); initial ELO is 1000.
- **chieftain** — sole leader of a `Clan`; `@OneToOne` on `Clan`. Only one per clan.
- **shamanes** — officers below chieftain; stored in `@JoinTable(name="shamanes")`.
- **grunts** — mid-rank members; stored in `@JoinTable(name="grunts")`.
- **peones** — entry rank assigned on `aceptarInvitacion`; stored in `@JoinTable(name="peones")`.
- **integrantes** — full member list (`@OneToMany mappedBy="clan"`); always includes chieftain.
- **ClanBan** — admin ban record linking a `Clan` to a reason string; presence blocks all mutations.
- **Confirmacion** — one-per-clan tournament confirmation token created by chieftain; enforced unique.
- **TemporadaModificacion** — tournament-season window that limits roster adds when clan is in an active tournament.
- **ComparacionClanes** — plain DTO (no `Comparator` logic); carries head-to-head stats (tournament wins, ladder wins) for two clans identified by tag.
- **Movimiento** — audit-log entry written on every clan event (`CREO_CLAN`, `INGRESO_CLAN`, `DEJO_CLAN`, `FUE_KICKEADO_CLAN`, `DESARMO_CLAN`, `REVIVIO_CLAN`).

## Rules

- A clan may only be created or revived by a user with no current clan; `ClanService.crearClan` and `revivirClan` both enforce this.
- Only the last chieftain may revive a disbanded clan; `revivirClan` checks `clan.getChieftain().equals(user)`.
- Only chieftain or shamanes may invite players; `invitarPlayer` enforces this. Inviting requires the invitador's clan to be unbanned.
- Invitation acceptance (`aceptarInvitacion`) is the only place roster-size and `TemporadaModificacion` limits are checked: clan cap is `com.tarreo.dota.torneo.maxPlayersPorClan` (12 in `web.xml`); if the clan is in an active tournament and no open modification season exists, acceptance is blocked.
- New joiners are added to `peones` on acceptance; they must be explicitly promoted by the chieftain.
- Promotion path is peon → grunt → shaman; only chieftain may promote or demote (`promover`/`demotear`). To elevate a shaman further, use `traspasarChieftain`.
- `traspasarChieftain` makes the new chieftain leave all role lists and adds the former chieftain to `shamanes`.
- Shamanes may kick peones and grunts but not other shamanes or the chieftain; chieftain may kick anyone; enforced in `kickearPlayer`.
- `desarmarClan` requires the chieftain to be the sole remaining member AND no clan in `FaseTorneo.REGISTRATION`; a clan in `STARTED` can be disbanded.
- Any banned clan (`ClanBan` record present) blocks `invitarPlayer`, `aceptarInvitacion`, `kickearPlayer`, `dejarClan`, `promover`, `demotear`, `desarmarClan`, and `traspasarChieftain`.
- `ClanService` has **no class-level `@RolesAllowed`**; all public methods are accessible to any authenticated user except `cambiarTag` and `cambiarNombre`, which are annotated `@RolesAllowed({"ADMIN_ROOT","ADMIN_DOTA"})` at method level.
- Avatar upload is gated in `VerClanMB.avatarUploadHandler` at the controller level (not a service call): only the chieftain or ADMIN_ROOT/ADMIN_DOTA may upload.

## Entry points

- `VerClanMB` — primary clan profile page; loads clan by tag, shows members/matches/desafíos, handles avatar upload, ban/unban (admin), tag/name change (admin), and the `confirmar` action.
- `CrearClanMB` — simple form delegating to `ClanService.crearClan`; redirects to `VerClan` on success.
- `InvitarPlayerMB` — `@RequestScoped` bean used on the clan page to send invitations with autocomplete username lookup.
- `VerInvitacionesMB` — player-facing pending-invitation list; handles `aceptarInvitacion` and `rechazarInvitacion`.

<!-- ComparacionClanes is a plain DTO with no sort/compare logic — it only holds head-to-head stat fields. -->
<!-- Confirmacion is a one-off confirmation token used for a specific tournament flow; fechaConfirmacion is Calendar not Date. -->