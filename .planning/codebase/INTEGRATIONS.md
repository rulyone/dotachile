# External Integrations

**Analysis Date:** 2026-04-19

## APIs & External Services

**Email Notification:**
- **Proton Mail SMTP** - Transactional email for account registration and activation
  - Protocol: SMTP over TLS (port 587)
  - Host: `smtp.protonmail.ch`
  - Auth: Basic auth with `SMTP_EMAIL` / `SMTP_PASSWORD` env vars
  - Usage: User registration verification, account activation links
  - Implementation: `javax.mail.Session` injected into `BasicService` via JNDI resource `mail/dotachileSession`
  - Client: JavaMail (`javax.mail.*`)

**PvPGN Game Server Integration:**
- **PvPGN Legacy Game Server** - DotA game client accounts
  - Purpose: Player authentication via PvPGN password hashes; tournament bracket integration with game server rankings
  - Hash Algorithm: Custom PvPGN SHA-1 variant (not standard SHA-1) implemented in `com.dotachile.shared.PvpgnHash`
  - Database: Separate PostgreSQL database `pvpgn` on same server
  - Connection: JDBC datasource `PvpgnPool` configured in Payara
  - Fields: `Perfil` entity holds `nickw3`, `uid`, `botw3`, `uidBot` to link web accounts to PvPGN accounts
  - Location: `src/java/com/dotachile/shared/PvpgnHash.java` (Java port of C/PHP hash, GPL-licensed)

## Data Storage

**Databases:**
- **PostgreSQL 15** (primary)
  - Datasource: `jdbc/dotachile` (JTA-managed, connection pool via `DotachilePool`)
  - Connection string: `jdbc:postgresql://db:5432/dotachile`
  - User: `dotachile` (default, set via `POSTGRES_PASSWORD` env var)
  - Client: JPA 2.0 with EclipseLink ORM
  - Tables (auto-created via JPA DDL): 33 entity classes listed in `src/conf/persistence.xml`
    - Core: `usuario`, `grupo`, `usuario_grupo` (auth)
    - Clans: `clan`, `clan_ban`, `confirmacion`
    - Tournaments: `torneo`, `ronda`, `game_match`, `game`, `resultado`, `modificacion`, `temporada_modificacion`
    - Ladder: `ladder`, `desafio`
    - Content: `noticia`, `categoria`, `encuesta`, `opcion_encuesta`, `comentario`
    - Media: `imagen`, `replay`
    - Bans: `ban`, `ban_history`

- **PostgreSQL 15** (secondary - PvPGN)
  - Datasource: `jdbc/pvpgnDatasource` (read-only for player linkage validation)
  - Connection string: `jdbc:postgresql://db:5432/pvpgn`
  - User: `dotachile` (same credentials)
  - Purpose: Query player accounts and validate PvPGN hash compatibility
  - Accessed via raw JDBC in `BasicService` (no ORM abstraction)

**File Storage:**
- **Local filesystem** - Docker volume mounted at `/home/dotachile/UPLOADS`
  - Uploads served by `com.dotachile.infrastructure.web.servlets.FilesServlet`
  - URL pattern: `/uploads/*`
  - Supported: Clan avatars (max 200 KB via `com.tarreo.dota.clan.avatarMaxSizeKB`), game replays (max 2000 KB via `com.tarreo.dota.replay.maxSizeKB`)
  - Persistence: Docker volume `uploads:` mounted read-write
  - No cloud storage integration (local only)

**Caching:**
- JPA L2 Cache via EclipseLink (enabled, mode: `ALL` shared cache)
- JSF page output caching disabled (default render, no output caching library)

## Authentication & Identity

**Auth Provider:**
- **Custom JDBC Realm** (Payara container-managed)
  - Realm name: `flexibleRealm` (configured in `post-boot-commands.asadmin`)
  - Login flow: Form-based (`form-login-page=/login.jsf`)
  - User table: `usuario` (PK: `username`)
  - Password column: `password` (MD5 hashes stored as hex)
  - Password hash: **Custom MD5(password + username) with salt** - Not standard JDBCRealm MD5
    - Hash function: `com.dotachile.shared.Util.hashPassword(String password, String username)`
    - Compatibility: Matches PvPGN BNET account hashes for game server linkage
  - Group table: `usuario_grupo` (join table mapping users to roles)
  - Group column: `GROUPNAME` (maps to security-role in web.xml)
  - Session tracking: `Usuario.logins` static map stores active `HttpSession` objects per user

**Authorization:**
- Role-based access control (RBAC) via servlet security roles:
  - `ADMIN_ROOT` - Full platform admin
  - `ADMIN_DOTA` - Tournament and game server admin
  - `ADMIN_TORNEO` - Tournament-specific admin (limited scope)
  - `ADMIN_LADDER` - Ladder/ELO ranking admin
  - `ESCRITOR` - Content author (news, blog)
  - `MODERADOR` - User moderation (bans, comments)
  - `BANEADO` - Banned user (restricted access)
- Container-managed `@RolesAllowed` annotations on EJB service methods
- Logout: `HttpServletRequest.logout()` + manual session invalidation via `Usuario.logins`

## Monitoring & Observability

**Error Tracking:**
- **None detected** - Errors logged to Java logging system only
  - Framework: `java.util.logging` (Logger instances in services)
  - Level: SEVERE for critical operations (mail failures, database errors)

**Logs:**
- **Java Logging (JUL)** - Default to Payara console output
- **Application-level:**
  - `BasicService` logs mail send failures (SEVERE)
  - `AdminService` logs ban/unban operations
  - Service failures typically throw `BusinessLogicException` (custom unchecked exception)
  - No structured logging, no log aggregation

**Performance Monitoring:**
- **JaCoCo Code Coverage** - Maven plugin integrated, reports generated at `target/site/jacoco/index.html`
- **EclipseLink Query Profiling** (disabled in persistence.xml but config present):
  - `eclipselink.profiler` property supports QueryMonitor or PerformanceProfiler (commented out)

## CI/CD & Deployment

**Hosting:**
- **Docker Compose** (local development)
- **Payara 5 Container** (production-ready image: `payara/server-full:5.2022.5`)
- **PostgreSQL 15 Container** (same compose stack)

**CI Pipeline:**
- **None detected** - No GitHub Actions, Jenkins, or CI config files found
- Build: Manual via `mvn package` → produces `target/DotaCL.war`
- Deploy: Manual Docker image build via Dockerfile → push to registry → run docker-compose

**Build Process:**
- Multi-stage Dockerfile:
  - Stage 1: Maven 3.8 + JDK 11 image - compiles `pom.xml`, downloads deps, builds WAR
  - Stage 2: Payara 5.2022.5 full profile - copies PostgreSQL JDBC driver, copies WAR, runs post-boot-commands
- PostgreSQL driver injected at build time: `postgresql-42.6.0.jar` copied to Payara domain lib
- JSF development mode: Enabled in Docker via `JVM_ARGS="-Djavax.faces.PROJECT_STAGE=Development"`

## Environment Configuration

**Required env vars:**
- `POSTGRES_PASSWORD` - Database password (used by both datasources)
  - Default: `dotachile` (insecure; should be overridden in production)
  - Scope: db service + app service
- `SMTP_EMAIL` - Email sender address
  - Default: `noreply@example.com`
  - Format: Must be a valid Proton Mail account
- `SMTP_PASSWORD` - Proton Mail SMTP password
  - Default: `changeme` (insecure; must be overridden)
  - Scope: Payara JavaMail session

**Secrets location:**
- Docker Compose: Environment variables passed via `environment:` section
- Production: Should be injected via Docker secrets or external configuration (not currently in code)
- WARNING: Defaults in `docker-compose.yml` are placeholder values; no `.env.example` file exists

## Webhooks & Callbacks

**Incoming:**
- None detected

**Outgoing:**
- **Email notifications** - Transactional only (account activation, password resets not implemented)
- **No webhooks** - Application is self-contained; no external callback handlers

---

*Integration audit: 2026-04-19*
