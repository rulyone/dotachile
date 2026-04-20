# Concerns

_Last mapped: 2026-04-19_

Technical debt, security risks, performance concerns, and fragile areas found while mapping the codebase. Items are grouped by category and tagged with rough severity: **HIGH / MED / LOW**.

---

## Tech debt

### [HIGH] Stack is end-of-life across the board
- **Java 8** (`pom.xml:16-17`) — public updates from Oracle ended 2019; premier support via OpenJDK distros ended 2023.
- **JSF 2.0 / Facelets 2.0** (`web/WEB-INF/faces-config.xml:5`) — on the Mojarra 2.0.x line; community-maintained only.
- **PrimeFaces 4.0** (`pom.xml:33-35`) — released 2013; 11 major versions behind current.
- **PrimeFaces Extensions 1.0.0** (`pom.xml:38-42`), **Themes 1.0.11** (`pom.xml:44-58`) — earliest version on Maven Central.
- **Java EE 6 API** (`pom.xml:23-27`) — superseded by Jakarta EE 10+. Namespace is still `javax.*`.
- **Apache Commons FileUpload 1.2.1** (`pom.xml:63-66`), **Commons IO 1.4** (`pom.xml:67-71`), **Commons Lang3 3.1** (`pom.xml:78-81`), **Commons Logging 1.1.1**, **ocpsoft-pretty-time 1.0.7** — all ~2010–2012 releases.

**Impact:** no security patches upstream, hard to hire for, any upgrade is a cross-cutting effort (Jakarta namespace flip, PrimeFaces API deltas). Treat as a strategic liability, not an incremental fix.

### [MED] `TorneoService` is a ~1800-LOC god-class
- `src/java/com/dotachile/torneos/service/TorneoService.java` concentrates tournament enrollment, bracket generation, game reporting, and standings.
- **10 `TODO`/`FIXME` markers** inside this one file (per `grep` — the highest count in the project; next highest is 1).
- Testing exists (`TorneoServiceTest.java`) but many branches are unexercised.

**Impact:** any torneos change touches this file; reviewers can't easily reason about blast radius. Prime refactor target once coverage is up.

### [MED] Scattered TODOs with no issue tracker link
- 39 `TODO`/`FIXME`/`XXX`/`HACK` occurrences across 30 files (`grep TODO|FIXME|XXX|HACK src/java` — 2026-04-19).
- Notable hotspots:
  - `torneos/service/TorneoService.java` (10)
  - `torneos/controller/ModificarBestOfMB.java`, `EliminarPareoMB.java`, `VerMatchMB.java` — per-MB stubs
  - `ladder/controller/VerLadderMB.java`
  - Entity files (most carry a stale NetBeans header TODO, which is noise, not signal)

**Impact:** no way to distinguish rotted notes from live work items.

### [LOW] Legacy `src/java/controller/` survivors
- `TESTMAIN.java`, `PsuMB.java`, `TestMB.java` are outside the feature packages and appear to be pre-refactor scratch code.
- `com.dotachile.automation.FunService` is explicitly marked "currently unused; do not add new business logic here" (`src/java/com/dotachile/automation/CLAUDE.md`).

**Impact:** noise, dead imports, occasional `@ManagedBean` confusion. Candidate for deletion.

### [LOW] Manual reflection-based `@EJB` injection in tests
- Every service test (e.g. `src/test/java/com/dotachile/clanes/service/ClanServiceTest.java:56-60`) uses `Field.setAccessible(true)` to wire mocks into `@EJB` fields.
- Any rename of a service's `@EJB` field name silently breaks the corresponding test's reflection lookup (no compile error).

**Impact:** refactor friction; easy to ship a green test suite that no longer actually exercises the service.

---

## Security

### [HIGH] `PvpgnHash` — weak, salt-less legacy password hashing
- `src/java/com/dotachile/shared/PvpgnHash.java` implements the PvPGN hashing algorithm (SHA-1 variant, no salt, no stretching).
- Used at login to verify Usuario passwords (required for PvPGN client compatibility).
- A password database leak would be recovered with commodity GPU rigs in hours.

**Context:** the algorithm is load-bearing — PvPGN clients verify against it, so it can't just be swapped for bcrypt without a migration path (dual-hash on next login, gradual rotation).

### [HIGH] Stored-XSS surface — `escape="false"` in user-rendered content
Five XHTML files render HTML with `escape="false"` (`grep escape="false" web/`):

- `web/web/noticias/VerNoticia.xhtml` — news bodies (authored by `ESCRITOR` role; still user input)
- `web/web/torneos/VerTorneo.xhtml` — tournament description
- `web/web/videos/VerVideos.xhtml` — video descriptions
- `web/web/seleccion/Seleccion.xhtml` — national selection content
- `web/index.xhtml` — landing page content

**Impact:** if any of these bind to fields editable by non-trusted users (or trusted users targeting other users), it's a stored-XSS primitive. Each site needs confirmation that input is sanitized on write (e.g. Jsoup) or re-escaped on read.

### [MED] `FilesServlet` serves user uploads from disk
- `src/java/com/dotachile/infrastructure/web/servlets/FilesServlet.java` is mapped to `/uploads/*` (`web/WEB-INF/web.xml:86-89`) with `param-value=/home/dotachile/UPLOADS`.
- Needs review for: path traversal (`..` in request path), MIME sniffing, and uploaded HTML being served with executable content-type.

**Impact:** user-uploaded images/replays reachable over HTTP; a path-traversal bug here is a filesystem read primitive.

### [MED] No CSRF token on JSF forms
- Mojarra 2.0 does not enable `ProtectedViewsHandler` by default, and no explicit `<f:viewAction>` / CSRF handler is configured.
- Any state-changing `h:commandButton` is reachable from a cross-site form post if the session cookie is present.

**Mitigation option:** upgrade Mojarra, set `javax.faces.PARTIAL_STATE_SAVING` + protected views; or add a per-session nonce checked in a servlet filter.

### [MED] Form auth + `flexibleRealm` — password transmitted plaintext to server
- `web/WEB-INF/web.xml:102-109` uses `<auth-method>FORM</auth-method>` with `/login.jsf`.
- Security here hinges on HTTPS at the edge (Payara/nginx). Verify the deployment terminates TLS before the container.

### [LOW] PvPGN DB credentials leak risk
- Auth layer integrates with PvPGN — credentials are managed via Docker `env` / `docker-compose.yml`. Confirm `.env` files are gitignored and not committed.

---

## Performance

### [HIGH] N+1 query risk through JPA relationships
- Entity relationships are declared on `Clan`, `Torneo`, `Usuario`, `GameMatch` etc. with EclipseLink default `LAZY`/`EAGER` behavior.
- No `@EntityGraph` / fetch-joins are in evidence.
- `TorneoService` standings computation iterates over matches and touches lazy collections — canonical N+1 shape.

**Impact:** page render times scale super-linearly with tournament size. Affected most on `VerTorneo.xhtml` and ladder pages.

### [MED] Scheduled jobs iterate without batching
- `ServiciosAutomatizados.removerCuentasNoActivadas` (`automation/ServiciosAutomatizados.java:44-50`) loads **all** `PreRegistro` rows via `findAll()` then `remove()` in a loop.
- Same pattern in `checkearBansYRemoverDeGrupoBaneados` (loads all `Ban` rows, iterates, conditionally mutates users + groups).
- Each iteration re-flushes.

**Impact:** fine today because volume is small; will become a multi-minute stall if row counts grow. Not critical now.

### [MED] `06:00` scheduler contention
- Two `@Schedule(hour="6", minute="0")` methods run concurrently on the `@Singleton` — per the per-package CLAUDE.md, this class owns "ban expiry" and "clear clan invitations" at the same slot.
- `@Singleton` serializes by default, but both touch `Usuario`/`Clan` relationships and can interact.

**Impact:** potential lock contention on the singleton bean at the 06:00 mark; watch for timer overruns in logs.

### [LOW] Monolithic `TorneoService` harms JIT inlining
- 1800-LOC class with many cross-method calls. Low priority — noted for completeness, not an action item.

---

## Fragile areas

### [MED] `dev-sync.sh` is load-bearing for developer flow
- `dev-sync.sh java|views|all` is how developers get changes into the Payara container without a full `docker compose build`.
- Breakage here blocks everyone; there is no tested fallback documented beyond "rebuild the image".
- It depends on: container name stability, exploded WAR layout, Payara auto-redeploy semantics.

### [MED] Docker setup is not version-pinned at Payara layer
- Confirm `docker-compose.yml` / `docker/Dockerfile` pin Payara 5 to an exact tag (not `payara/server-full:5`). Floating tags mean "works today, broken tomorrow".
- `DotaCL.war` filename is hardcoded in dev-sync and deploy flows.

### [MED] Entity `equals`/`hashCode` brittleness
- JPA entities typically implement `equals` by `id` only; pre-persist entities share `id=0` and compare equal.
- `ClanServiceTest.java:46-53` documents this gotcha explicitly ("Clan.equals() compares by id (a long that defaults to 0), so two synthetic clans both at id=0 collide and break self-reference guards in production code").
- Any production code using `Set<Entity>` or `contains` with in-memory entities can silently deduplicate.

### [LOW] Email corpus tool is per-developer + gitignored
- `tools/email-rag/` requires each developer to build their own redacted corpus at `../dotachile-emails/corpus`.
- Setup docs exist (`tools/email-rag/README.md`) but the corpus is not part of the default clone — any script or plan that references it will degrade gracefully only if developers read `CLAUDE.md` first.

### [LOW] Navigation is mostly implicit (outcome strings)
- `faces-config.xml` contains only the exception handler; no `<navigation-rule>` blocks.
- ManagedBean actions return strings like `"verTorneo?faces-redirect=true"` — redirects rely on view-ID conventions being stable.
- Renaming an XHTML file silently breaks every action that redirected to it.

---

## Known specific bugs / stubs

- `torneos/controller/ModificarBestOfMB.java` — contains a TODO for incomplete error reporting.
- `torneos/controller/EliminarPareoMB.java` — TODO on error path.
- `torneos/controller/VerMatchMB.java` — `confirmarMatch` per audit returns `null` (no navigation outcome after action).
- `torneos/service/TorneoService.java` — 10 in-line TODOs, many orphaned.
- `ladder/controller/VerLadderMB.java` — TODO noted.

These are concrete targets for a `/gsd-plan-phase` cleanup pass.

---

## Recommended first-cuts (if triaging)

1. **Audit the 5 `escape="false"` sites** — confirm each input path sanitizes on write. Highest blast radius per hour of work.
2. **Pin Docker image tags** — cheapest hardening against "works on my machine".
3. **Add lazy-loading fetch strategy to `TorneoService` standings** — clearest perf win.
4. **Plan a PvpgnHash → bcrypt migration with dual-hash on login** — long project, but clock is ticking.
5. **Delete `src/java/controller/` leftovers and `FunService`** — low risk, reduces cognitive load.
