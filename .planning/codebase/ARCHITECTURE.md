# Architecture

_Last mapped: 2026-04-19_

## Pattern

Classic **Java EE 6 multi-tier web application** running on Payara 5:

```
Browser
  ↓  HTTP
┌──────────────────────────────────────────────────────┐
│  Presentation: JSF 2 Facelets (XHTML) + PrimeFaces 4 │   web/web/<feature>/*.xhtml
├──────────────────────────────────────────────────────┤
│  Controller: ManagedBeans (*MB.java, @ViewScoped /    │   src/java/com/dotachile/<feature>/controller/
│              @SessionScoped / @RequestScoped)         │
├──────────────────────────────────────────────────────┤
│  Service: @Stateless EJB (business logic, tx)         │   src/java/com/dotachile/<feature>/service/
│  Scheduler: @Singleton EJB (@Schedule timers)         │   src/java/com/dotachile/automation/
├──────────────────────────────────────────────────────┤
│  Facade: generic CRUD wrappers over EntityManager     │   src/java/com/dotachile/<feature>/facade/
│          extending AbstractFacade<T>                  │   src/java/model/entities/AbstractFacade.java
├──────────────────────────────────────────────────────┤
│  Entity: JPA 2 (@Entity) on EclipseLink               │   src/java/com/dotachile/<feature>/entity/
├──────────────────────────────────────────────────────┤
│  Database: PostgreSQL 15 (Docker)                     │   docker/
└──────────────────────────────────────────────────────┘
```

There is no DTO/API layer — ManagedBeans consume Services and Facades directly and bind JPA entities straight to the view. There is no REST tier; the app is server-rendered through `FacesServlet`.

## Layers

| Layer | Responsibility | Location |
|-------|----------------|----------|
| **View** | XHTML Facelets, PrimeFaces widgets, EL bindings to `#{xxxMB.*}` | `web/web/<feature>/*.xhtml`, `web/resources/` |
| **Controller (MB)** | Form backing, action methods, navigation, flash messages | `com.dotachile.<feature>.controller.*MB` |
| **Service (EJB)** | Transactional business logic, auth checks, cross-facade orchestration | `com.dotachile.<feature>.service.*Service` |
| **Facade** | Thin `EntityManager` wrappers (CRUD + feature queries) | `com.dotachile.<feature>.facade.*Facade` |
| **Entity** | JPA `@Entity` classes; relationships modeled here | `com.dotachile.<feature>.entity.*` |
| **Converter** | JSF `Converter` for dropdown/selectOne binding | `com.dotachile.<feature>.converter.*Converter` |
| **Helper** | Value objects / DTO-ish structures (e.g. `Standing`, `ComparacionClanes`) | `com.dotachile.<feature>.helper.*` |
| **Infrastructure** | Filters, servlets, exception handler, session listener | `com.dotachile.infrastructure.web.*` |
| **Scheduler** | Time-driven cleanup jobs | `com.dotachile.automation.ServiciosAutomatizados` |
| **Shared** | `Util`, `BusinessLogicException`, `BaseMB`, `PvpgnHash` | `com.dotachile.shared.*` |

## Data flow (typical request)

Example: user reports a ladder game.

```
1. Browser POSTs form → *.jsf
2. FacesServlet dispatches → ReportarGameLadderWizardMB.confirmar()
3. MB calls @EJB LadderService (transaction begins — REQUIRED by default)
4. LadderService loads Desafio/Clan via DesafioFacade, ClanFacade
5. LadderService applies EloSystem math, mutates entities
6. LadderService persists via *Facade.edit() / .create()
7. LadderService throws BusinessLogicException on rule violation
   (@ApplicationException rollback=true in shared/BusinessLogicException.java)
8. Transaction commits → EclipseLink flushes to PostgreSQL
9. MB adds FacesMessage, returns outcome string for navigation
10. JSF renders updated view
```

## Abstractions

- **`AbstractFacade<T>`** (`src/java/model/entities/AbstractFacade.java`) — parent of every feature facade. Provides `create`, `edit`, `remove`, `find`, `findAll`, `findRange`, `count`. Subclasses inject `EntityManager` and pin the concrete type.
- **`BaseMB`** (`src/java/com/dotachile/shared/BaseMB.java`) — shared ManagedBean utilities (flash messages, principal lookup).
- **`BusinessLogicException`** (`src/java/com/dotachile/shared/BusinessLogicException.java`) — single checked exception the service layer throws on rule violations. Annotated `@ApplicationException(rollback=true, inherited=true)` so the EJB container rolls back the transaction automatically.
- **`EloSystem`** (`src/java/com/dotachile/elo/EloSystem.java`) — pure-Java rating math, used by ladder and torneos.
- **`Util`** (`src/java/com/dotachile/shared/Util.java`) — catch-all helper (date math, string sanitization, request helpers).
- **`ApplicationMB`** (`src/java/com/dotachile/shared/ApplicationMB.java`) — `@ApplicationScoped` MB for cross-page globals.

## Entry points

| Kind | Wiring | Purpose |
|------|--------|---------|
| HTTP request | `FacesServlet` mapped to `*.jsf` in `web/WEB-INF/web.xml:90-93` | All page rendering / form submissions |
| HTTP request | `FilesServlet` mapped to `/uploads/*` in `web/WEB-INF/web.xml:86-89` | Serves user uploads from `/home/dotachile/UPLOADS` (container path) |
| Filter | `CharacterEncodingFilter` on `/*` (`src/java/com/dotachile/infrastructure/web/filters/CharacterEncodingFilter.java`) | Forces UTF-8 on request/response |
| Filter | `CacheControlFilter` on `/static/*` (`src/java/com/dotachile/infrastructure/web/filters/CacheControlFilter.java`) | Long-cache static assets |
| Filter | PrimeFaces `FileUploadFilter` on Faces Servlet (`web.xml:49-51`) | Multipart parsing |
| Exception handler | `CustomExceptionHandler` wired via `DefaultExceptionHandlerFactory` in `web/WEB-INF/faces-config.xml:11-13` | Maps uncaught exceptions to `web/web/errores/*.xhtml` |
| Session listener | `SessionManagerBean` (`src/java/com/dotachile/infrastructure/web/listeners/SessionManagerBean.java`) | Tracks logged-in users |
| Scheduler | `@Singleton ServiciosAutomatizados` — 4 `@Schedule` methods at 05:00 / 06:00 / 06:00 / 07:00 (`src/java/com/dotachile/automation/ServiciosAutomatizados.java`) | Prune pre-registros, expire bans, clear clan invitations, remove stale challenges |
| Auth | Container-managed FORM login — `web.xml:102-109`; login page `/login.jsf`, error `/login-error.jsf`, realm `flexibleRealm` | Payara realm validates against `Usuario` + `PvpgnHash` |

## Security model

- **Auth method:** Servlet 3.0 FORM login (`<login-config>` in `web/WEB-INF/web.xml`).
- **Roles declared in `web.xml:110-137`:** `ADMIN_ROOT`, `ADMIN_DOTA`, `ADMIN_TORNEO`, `ESCRITOR`, `MODERADOR`, `BANEADO`, `ADMIN_LADDER`.
- **Role mapping:** `Grupo.groupname` in the DB maps to these `security-role` names (per `CLAUDE.md` glossary).
- **Enforcement:** Fine-grained checks happen inside `@Stateless` services via `SessionContext.isCallerInRole(...)` rather than `@RolesAllowed`; that is why tests inject a mock `SessionContext` into services (see `src/test/java/com/dotachile/clanes/service/ClanServiceTest.java`).

## Cross-cutting concerns

- **Transactions:** Container-managed via EJB defaults (`REQUIRED`). A thrown `BusinessLogicException` triggers rollback.
- **Logging:** `java.util.logging` (`Logger.getLogger(...)`) used throughout — no SLF4J/Logback.
- **Encoding:** UTF-8 enforced by `CharacterEncodingFilter`.
- **Validation:** Mix of JSF component validators, EL-based `required=true`, and imperative checks inside services that throw `BusinessLogicException`.
- **Error presentation:** `CustomExceptionHandler` captures `ViewExpired`/uncaught and redirects to `web/web/errores/`.
- **Background work:** Only `ServiciosAutomatizados` (`@Singleton` + `@Schedule`). No message queues, no async executors.

## Deployment topology

- Single WAR: `target/DotaCL.war` (Maven `<finalName>DotaCL</finalName>` in `pom.xml:120`).
- Deployed to Payara 5 container via `docker compose up -d --build`.
- PostgreSQL 15 runs in a sibling container (see `docker/`).
- Dev sync: `./dev-sync.sh java|views|all` hot-pushes compiled classes or XHTML into the running container without a full redeploy.

## Legacy/non-canonical folders

- `src/java/controller/` — contains `TESTMAIN.java`, `PsuMB.java`, `TestMB.java`. Pre-refactor leftovers; **not** part of the feature-based architecture.
- `src/java/model/entities/` — holds only `AbstractFacade.java` (the shared generic facade parent). All feature entities live under `com.dotachile.<feature>.entity`.
- `com.dotachile.automation.FunService` — per `src/java/com/dotachile/automation/CLAUDE.md`, this class is "currently unused; do not add new business logic here".
