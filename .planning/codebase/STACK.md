# Technology Stack

**Analysis Date:** 2026-04-19

## Languages

**Primary:**
- Java 8 (OpenJDK 11 for build, Java EE 6 compatibility) - Enterprise application server logic and EJBs
- XHTML/JSF 2.0 - UI markup with PrimeFaces components
- SQL - PostgreSQL schema and queries

**Secondary:**
- HTML/CSS/JavaScript - Frontend styling and interactions

## Runtime

**Environment:**
- Payara 5 (Full Profile) version 2022.5 - JEE6-compliant application server
- PostgreSQL 15 - Relational database engine
- Java 8 (compiled), JDK 11 (build image)

**Package Manager:**
- Maven 3.8+ - Build orchestration
- Lockfile: `pom.xml` (present, with pinned dependency versions)

## Frameworks

**Core:**
- JSF 2.0 (Java Server Faces) - Web framework via javax.faces API (provided by Payara)
- PrimeFaces 4.0 - JSF component library (Rich UI components)
- PrimeFaces Extensions 1.0.0 - Extended component suite
- EJB 3.0 (Stateless/Singleton) - Server-side business logic containers
- JPA 2.0 with EclipseLink - Object-relational mapping

**Testing:**
- JUnit 5 (Jupiter) 5.10.2 - Test framework
- Mockito 3.12.4 - Mocking framework
- AssertJ 3.25.3 - Fluent assertion library

**Build/Dev:**
- Maven Compiler Plugin 3.10.1 - Java compilation
- Maven War Plugin 3.3.2 - WAR packaging
- Maven Surefire Plugin 3.2.5 - Test execution
- JaCoCo Maven Plugin 0.8.11 - Code coverage tracking

## Key Dependencies

**Critical:**
- `javaee-api` 6.0 (provided scope) - Container-provided JEE APIs (JSF, EJB, JPA, Servlet)
- `postgresql` 42.6.0 - PostgreSQL JDBC driver (copied to Payara domain lib during build)
- `primefaces` 4.0 - UI component library (core to all XHTML views)

**Utilities:**
- `commons-fileupload` 1.2.1 - File upload handling for replay/avatar uploads
- `commons-io` 1.4 - File I/O utilities
- `commons-lang3` 3.1 - String/collection utilities
- `commons-logging` 1.1.1 - Logging bridge
- `ocpsoft-pretty-time` 1.0.7 - Human-readable time formatting (e.g., "2 hours ago")

**UI Themes:**
- `primefaces-themes` trontastic 1.0.11 (default)
- `primefaces-themes` pepper-grinder 1.0.11 (alternative)
- `primefaces-themes` rocket 1.0.11 (alternative)

## Configuration

**Environment:**
- **Docker Compose** (`docker-compose.yml`):
  - `POSTGRES_PASSWORD` - Database password (defaults to `dotachile`)
  - `SMTP_EMAIL` - Email sender address (defaults to `noreply@example.com`)
  - `SMTP_PASSWORD` - SMTP authentication password (defaults to `changeme`)

- **Payara Boot Commands** (`docker/post-boot-commands.asadmin`):
  - JDBC datasources: `DotachilePool` (main app) and `PvpgnPool` (PvPGN game server DB)
  - JavaMail session: `mail/dotachileSession` (Proton Mail SMTP)
  - Security realm: `flexibleRealm` (custom JDBC auth with MD5+salt)

- **Web Configuration** (`web/WEB-INF/web.xml`):
  - JSF development mode enabled via `javax.faces.PROJECT_STAGE` (set to Production in web.xml; overridden to Development in Docker)
  - PrimeFaces theme: `trontastic`
  - Session timeout: 30 minutes
  - File upload size limits via context params
  - Security roles: `ADMIN_ROOT`, `ADMIN_DOTA`, `ADMIN_TORNEO`, `ESCRITOR`, `MODERADOR`, `BANEADO`, `ADMIN_LADDER`

- **Persistence Configuration** (`src/conf/persistence.xml`):
  - JPA provider: EclipseLink 2.0+
  - Datasource: `jdbc/dotachile`
  - DDL generation: `create-tables` (auto-schema on startup)
  - Caching: `ALL` shared cache mode

**Build:**
- `pom.xml` - Maven configuration
- `maven-war-plugin` - Packages `src/java` classes and `web/` static assets into `DotaCL.war`
- Source layout: non-standard (source in `src/java`, resources in `src/conf`, web in `web/`)

## Platform Requirements

**Development:**
- Java 8 compiler (Maven enforces via `maven.compiler.source=1.8`)
- Maven 3.8+ with offline mode support
- Docker + Docker Compose
- PostgreSQL 15 (or containerized via docker-compose)

**Production:**
- Payara 5 (Full Profile 2022.5+)
- PostgreSQL 15+
- 512 MB JVM heap minimum recommended
- TCP ports 8080 (HTTP), 4848 (Admin Console), 5432 (DB)

**File Storage:**
- Local filesystem at `/home/dotachile/UPLOADS` - Mounted as Docker volume for persistence
- Servlet: `FilesServlet` serves uploaded files (avatars, replays) via `/uploads/*` URL pattern

---

*Stack analysis: 2026-04-19*
