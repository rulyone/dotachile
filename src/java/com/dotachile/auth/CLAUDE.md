# auth

User registration, login, role/group management, and ban enforcement for the DotaChile platform.

## Glossary

- **Usuario** — primary entity; `username` (max 20) is the PK and FK in all downstream tables. Implements `HttpSessionBindingListener` to track live sessions in `Usuario.logins`.
- **Perfil** — extended profile linked 1:1 to `Usuario`; holds `nickw3`/`uid`/`botw3`/`uidBot` for PvPGN game-server account linkage.
- **Grupo** — role entity; `groupname` is the PK. Users hold a `@ManyToMany` list of `Grupo` instances mapped in the `usuario_grupo` join table.
- **Ban** — active ban record (one per `baneado`, enforced by `unique` FK). Holds `baneador`, `razonBan`, `fechaBan`, `diasBan`.
- **BanHistory** — immutable audit log of past bans; same schema as `Ban` but written on unban, never deleted.
- **PreRegistro** — pending registration row. Holds `codigoActivacion`; graduated to `Usuario` by `BasicService.activarCuenta`.
- **PvpgnHash** — custom SHA-1 variant required by the PvPGN game server; used so web-site passwords match the game-server's BNET account hashes (standard SHA-1 is incompatible).
- **Roles** (web.xml `security-role`): `ADMIN_ROOT`, `ADMIN_DOTA`, `ADMIN_TORNEO`, `ESCRITOR`, `MODERADOR`, `BANEADO`, `ADMIN_LADDER`.

## Rules

- `Ban.baneado` has a `unique` FK constraint: only one active `Ban` per user. `AdminService.banUser` throws `BusinessLogicException` if a record already exists; do not bypass this check.
- Banning a user adds them to the `BANEADO` `Grupo` **and** creates the `Ban` record atomically inside `AdminService.banUser`; both steps must succeed together.
- Any group change or ban immediately calls `AdminService.forzarLogout(usuario)`, which invalidates the user's live `HttpSession` via `Usuario.logins`; never skip this call after role mutations.
- `AdminService` is `@RolesAllowed({"ADMIN_ROOT"})` at class level. Exceptions: `banUser` allows `ADMIN_ROOT, ADMIN_DOTA, MODERADOR`; `banearClan` and `desbanearClan` allow `ADMIN_ROOT, ADMIN_DOTA`. All other methods (`setearGrupos`, `agregarGrupo`, `removerGrupo`, `logoutAllUsers`, `forzarLogout`, `removerFromBaneados`, `isBanned`) inherit the class-level `ADMIN_ROOT`-only restriction.
- Passwords must be stored and compared using `PvpgnHash.GetHash(String)` (40-char hex) to stay compatible with PvPGN BNET accounts; standard Java `MessageDigest` SHA-1 produces a different result.
- Pre-registration rows with unverified email are purged nightly at 05:00 by an automation job; `PreRegistro.codigoActivacion` is the only gate before `BasicService.activarCuenta` promotes the row to `Usuario`.
- `Usuario.parteDeCualquierGrupo(String grupos)` accepts a comma-separated group list and is the view-layer helper for conditional rendering; it is not a security boundary — use container-managed `@RolesAllowed` for real enforcement.

## Entry points

- `LoginMB` (`@SessionScoped`) — programmatic FORM login via `HttpServletRequest.login`; stores `Usuario` in session map under key `"user"` and updates `lastLogin`.
- `RegistrarseMB` / `ActivarCuentaMB` — two-step registration: `RegistrarseMB.register` creates a `PreRegistro`; `ActivarCuentaMB.activarCuenta` promotes it to `Usuario`.
- `BanearUsuarioMB` — admin ban form; delegates to `AdminService.banUser` which enforces the `ADMIN_ROOT / ADMIN_DOTA / MODERADOR` role check.
- `ModificarGrupoMB` / `UserManagerMB` — role assignment UI; call `AdminService.agregarGrupo` / `removerGrupo` / `setearGrupos`, all restricted to `ADMIN_ROOT`.

<!-- RegistrarseMB delegates to BasicService (torneos package) for actual persistence — follow that call chain when tracing registration bugs. -->
<!-- BanHistory is populated elsewhere (not in AdminService); search for BanHistoryFacade.create() to find where audit rows are written. -->
