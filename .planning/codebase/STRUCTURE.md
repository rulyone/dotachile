# Structure

_Last mapped: 2026-04-19_

## Top-level layout

```
dotachile/
├── pom.xml                   # Maven build (WAR, finalName=DotaCL)
├── docker-compose.yml        # Payara 5 + PostgreSQL 15
├── docker/                   # Dockerfiles + container config
├── dev-sync.sh               # Hot-push script (java/views/all)
├── src/
│   ├── java/                 # sourceDirectory (see pom.xml:119)
│   │   ├── com/dotachile/    # ← feature-based packages (canonical)
│   │   ├── controller/       # legacy leftovers (TESTMAIN, PsuMB, TestMB)
│   │   └── model/entities/   # AbstractFacade.java only
│   ├── test/java/            # JUnit 5 tests (mirrors production package layout)
│   └── conf/                 # META-INF/ resources (persistence.xml, etc.)
├── web/
│   ├── WEB-INF/              # web.xml, faces-config.xml
│   ├── web/<feature>/        # feature XHTML views
│   ├── resources/            # PrimeFaces-resolved CSS/JS/images
│   ├── static/               # plain-served static assets
│   └── *.xhtml               # top-level pages (index, login, etc.)
├── docs/                     # presentations, specs
├── tools/email-rag/          # offline email corpus search tool
├── target/                   # Maven output (DotaCL.war)
├── nbproject/                # NetBeans project metadata
├── .planning/                # GSD workflow artifacts (this directory tree)
├── .claude/                  # Claude Code config, commands, hooks, skills
└── .agents/                  # Agent skill definitions
```

## Feature packages (`src/java/com/dotachile/`)

Each feature uses the same internal shape — subpackages `controller/`, `entity/`, `facade/`, `service/`, optionally `converter/`, `helper/`.

| Package | What it owns |
|---------|--------------|
| `auth/` | `Usuario`, `Perfil`, `Grupo`, `Ban`, `BanHistory`, `PreRegistro`; login/register/reset/activate flows; `AdminService` |
| `clanes/` | `Clan`, `ClanBan`, `Confirmacion`; clan lifecycle (crear/desarmar/revivir), membership, invites, chieftain transfer; `ClanService`; `ComparacionClanes` helper |
| `comentarios/` | `Comentario`, `ComentarioFacade`, `ComentariosService` (cross-cutting — no MB; wired in by other features) |
| `encuestas/` | `Encuesta`, `OpcionEncuesta`; poll creation and voting; `EncuestaService` |
| `ladder/` | `Ladder`, `Desafio`, `FaseLadder`; challenges, ELO updates, game reporting; `LadderService` |
| `noticias/` | `Noticia`, `Categoria`; editorial posts (admin) and user posts; paginated archive |
| `torneos/` | `Torneo`, `Ronda`, `GameMatch`, `Game`, `Resultado`, `FactorK`, `FaseTorneo`, `TipoTorneo`, `Modificacion`, `TemporadaModificacion`, `TipoModificacion`; tournament lifecycle; `TorneoService` (~1800 LOC); `Standing` helper |
| `torneossingle/` | `TorneoSingle`, `RondaSingle`, `SingleMatch`; single-player bracket variant; `SingleTorneoService` |
| `automation/` | `ServiciosAutomatizados` (`@Singleton` with `@Schedule` jobs), `FunService` (unused) |
| `elo/` | `EloSystem` (pure math), `CalculadorEloMB` (preview UI) |
| `buscador/` | `BuscadorMB`, `SearchService` (global search) |
| `inhouse/` | `InhouseService` (informal in-house games) |
| `media/` | `Imagen`, `Replay`, `Movimiento`, `TipoMovimiento` + facades (uploaded media + audit trail) |
| `seleccion/` | National selection views/logic |
| `videos/` | Video embeds/listing |
| `shared/` | `Util`, `PvpgnHash`, `BusinessLogicException`, `BaseMB`, `ApplicationMB`, `IndexMB` |
| `infrastructure/web/` | `filters/` (`CharacterEncodingFilter`, `CacheControlFilter`), `servlets/` (`FilesServlet`), `listeners/` (`SessionManagerBean`), `exceptionhandler/` (`CustomExceptionHandler`, `DefaultExceptionHandlerFactory`) |

## View layout (`web/web/`)

Directories mirror the Spanish feature names:

```
web/web/
├── clanes/         ├── noticias/
├── encuestas/      ├── registro/
├── errores/        ├── seleccion/
├── ladder/         ├── stream/
├── buscador/       ├── torneos/
├── usuarios/       ├── videos/
├── templates/      └── ...
```

`web/web/templates/` holds shared Facelets templates pulled in via `ui:composition`. `web/web/errores/` receives traffic redirected by `CustomExceptionHandler`.

## Config files

| File | Purpose |
|------|---------|
| `pom.xml` | Maven build, dependency versions, JaCoCo config |
| `web/WEB-INF/web.xml` | Servlet/filter wiring, FORM auth config, security roles, context-params (max clan size, etc.) |
| `web/WEB-INF/faces-config.xml` | Registers `DefaultExceptionHandlerFactory` |
| `src/conf/META-INF/persistence.xml` | JPA persistence unit for EclipseLink → PostgreSQL |
| `docker-compose.yml` | Runtime topology (Payara + PG) |
| `docker/` | Dockerfile(s), init scripts |
| `.claude/settings.json` | Claude Code config (mounts `../dotachile-emails/corpus`) |
| `.planning/` | GSD-generated planning docs (this directory tree) |

## Naming conventions

- **Language:** Spanish for package, class, entity, view, DB identifiers. Per `CLAUDE.md`: **do not propose English renames**.
- **ManagedBean classes:** `<Verb><Noun>MB.java` — e.g. `CrearClanMB`, `VerTorneoMB`, `ReportarGameWizardMB`. `MB` suffix is mandatory.
- **EJB service classes:** `<Feature>Service.java` — e.g. `ClanService`, `LadderService`, `TorneoService`. Single service per feature.
- **Facade classes:** `<Entity>Facade.java` — one per entity, extends `AbstractFacade<Entity>`.
- **Entity classes:** singular Spanish noun — `Clan`, `Usuario`, `Torneo`, `Desafio`, `Noticia`. Never suffixed.
- **Converters:** `<Entity>Converter.java`.
- **XHTML views:** PascalCase verbs matching their MB — `VerTorneo.xhtml`, `CrearClan.xhtml`.
- **Test classes:** `<ClassUnderTest>Test.java`, mirrors production package under `src/test/java/`.

## Where to add new code

| Task | Go here |
|------|---------|
| New DB-backed entity in an existing feature | `com/dotachile/<feature>/entity/<Entity>.java` + matching `<Entity>Facade.java` |
| Business rule change | existing `<feature>/service/<Feature>Service.java` — do **not** push logic into MBs or Facades |
| New user-facing screen | XHTML in `web/web/<feature>/` + ManagedBean in `<feature>/controller/*MB.java` |
| Scheduled/batch job | add a `@Schedule` method to `automation/ServiciosAutomatizados.java` (per `automation/CLAUDE.md`: do not add logic to `FunService`) |
| New cross-cutting filter/servlet | `infrastructure/web/filters/` or `infrastructure/web/servlets/` |
| Shared utility reused across features | `shared/Util.java` — widen existing helper before adding a new class |
| New feature (whole vertical) | `com/dotachile/<feature_es>/` with the canonical `controller/ entity/ facade/ service/` shape; view root at `web/web/<feature_es>/` |

## Key entry files for orientation

- `src/java/com/dotachile/shared/IndexMB.java` — home page MB
- `web/index.xhtml` — root template
- `src/java/com/dotachile/auth/controller/LoginMB.java` — auth flow backing bean
- `src/java/com/dotachile/automation/ServiciosAutomatizados.java` — every scheduled job
- `src/java/com/dotachile/torneos/service/TorneoService.java` — largest service, core tournament logic (~1800 LOC)
- `src/java/model/entities/AbstractFacade.java` — base class of every `*Facade`
- `src/java/com/dotachile/shared/BaseMB.java` — base class many MBs extend

## Per-package CLAUDE.md (feature notes)

A `CLAUDE.md` next to a package captures glossary + rules specific to that feature. Currently present:

- `src/java/com/dotachile/torneos/CLAUDE.md`
- `src/java/com/dotachile/automation/CLAUDE.md`

Read these before touching the corresponding package — they encode constraints (e.g. "do not add business logic to `FunService`") that are not otherwise discoverable from the code.
