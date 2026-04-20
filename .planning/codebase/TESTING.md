# Testing

_Last mapped: 2026-04-19_

## Framework

| Tool | Version | Source |
|------|---------|--------|
| JUnit Jupiter | 5.10.2 | `pom.xml:92-96` |
| Mockito Core | 3.12.4 | `pom.xml:98-102` |
| Mockito JUnit Jupiter | 3.12.4 | `pom.xml:104-108` |
| AssertJ Core | 3.25.3 | `pom.xml:110-114` |
| Maven Surefire | 3.2.5 | `pom.xml:147-150` |
| JaCoCo | 0.8.11 | `pom.xml:152-179` |

No Arquillian, no embedded container, no Spring Test. Tests are plain JVM unit tests that stub the EJB container manually.

## Running tests

```
mvn test                   # unit tests only
mvn verify                 # tests + JaCoCo report (target/site/jacoco/index.html)
mvn -o test                # offline (use after warm local .m2)
```

## Layout

Tests mirror the production package structure under `src/test/java/`:

```
src/test/java/com/dotachile/
├── auth/controller/RegistrarseMBTest.java
├── clanes/service/ClanServiceTest.java
├── elo/EloSystemTest.java
├── ladder/service/LadderServiceTest.java
├── shared/BusinessLogicExceptionTest.java
├── shared/PvpgnHashTest.java
├── shared/UtilTest.java
└── torneos/service/TorneoServiceTest.java
```

**8 test files total.** Coverage is focused on the service/shared layer, not on MBs, Facades, or entities.

## Test pattern: manual `@EJB` injection via reflection

Because the EJB container is not booted for unit tests, services under test have their `@EJB` fields wired in by hand using reflection. Canonical example — `src/test/java/com/dotachile/clanes/service/ClanServiceTest.java:56-60`:

```java
@BeforeEach
void setUp() throws Exception {
    service = new ClanService();
    clanFac = mock(ClanFacade.class);
    userFac = mock(UsuarioFacade.class);
    // ...
    Field f = ClanService.class.getDeclaredField("clanFac");
    f.setAccessible(true);
    f.set(service, clanFac);
    // ... repeat per injected dependency
}
```

`SessionContext` is also mocked and injected the same way to simulate role checks (`isCallerInRole`) and principal lookup. This pattern is load-bearing — if you refactor `@EJB` field names in a service, the matching test's reflection lookup will silently break.

## Assertion style

AssertJ fluent assertions throughout:

```java
assertThat(result).isNotNull();
assertThat(result.getMiembros()).containsExactly(u1, u2);
assertThatThrownBy(() -> service.crearClan(...))
    .isInstanceOf(BusinessLogicException.class)
    .hasMessageContaining("ya existe");
```

Mockito `ArgumentCaptor` is used to verify what was persisted via the facade (see `ClanServiceTest`).

## What's covered

| Area | Covered by |
|------|-----------|
| ELO math (pure) | `EloSystemTest.java` |
| Date/string/request utilities | `UtilTest.java` |
| PvPGN legacy password hashing | `PvpgnHashTest.java` |
| Business exception wiring | `BusinessLogicExceptionTest.java` |
| Clan service (creation, transfer, bans, leaving) | `ClanServiceTest.java` |
| Ladder service (challenge lifecycle, game reporting) | `LadderServiceTest.java` |
| Tournament service (enrollment, brackets) | `TorneoServiceTest.java` |
| Auth registration flow | `RegistrarseMBTest.java` (rare MB-level test) |

## What's NOT covered

- **ManagedBeans** (except `RegistrarseMB`) — explicitly excluded from JaCoCo (`pom.xml:159` `<exclude>**/*MB.class</exclude>`).
- **Facades** — excluded from JaCoCo (`pom.xml:161` `<exclude>com/dotachile/**/facade/**</exclude>`). No facade unit tests exist.
- **Entities** — excluded from JaCoCo (`pom.xml:160` `<exclude>com/dotachile/**/entity/**</exclude>`).
- **Scheduled jobs** — `ServiciosAutomatizados` has no tests; each `@Schedule` method mutates global state with no abstraction seam.
- **Filters / servlets / exception handler** — no `infrastructure/web` tests.
- **XHTML / JSF navigation** — no Selenium, no integration tests, no browser coverage.
- **JPA queries and lazy-loading boundaries** — no persistence-layer tests; no in-memory PG, no `@DataJpaTest` equivalent.
- **`TorneoService`** — the service has a test file, but the class is ~1800 LOC with 10 open TODOs; many branches are not exercised.
- **`EncuestaService`, `ComentariosService`, `SearchService`, `AdminService`, `SingleTorneoService`, `InhouseService`, `FunService`** — no dedicated tests.

JaCoCo coverage excludes are labelled `Phase 1 exclusions; revisited in Phase 2` (`pom.xml:157-162`) — an explicit signal that coverage of those layers is deferred work.

## Mocking conventions

- Mock every `@EJB`-injected facade and `SessionContext` with `Mockito.mock(...)`.
- Inject via reflection on the concrete `Field` name (see pattern above) — do not add setters to production code just for tests.
- When a test builds multiple `Clan` entities, give them unique synthetic IDs — `Clan.equals()` compares by `id` and two `id=0` entities collide (see the `nextClanId` counter in `ClanServiceTest.java:53`, which encodes this gotcha).
- Prefer `ArgumentCaptor` over `verify(...).method(eq(expected))` when asserting on object state.

## CI

No GitHub Actions / CI config is present in the repo (`.github/workflows/` is absent). Tests run on developer machines and (presumably) in `docker compose up --build`. There is no automated merge gate.

`dev-sync.sh` is a developer hot-reload tool (compile + push classes into the running container), not a test driver — it does not execute `mvn test`.

## Gotchas when adding tests

- **Don't mock via Spring** — there is no Spring context; stick to the reflection-injection pattern already used.
- **Don't test against a real Payara** — no Arquillian config exists; add-ons would need to come with their own runner.
- **Entity `equals`/`hashCode` pitfalls** — JPA entities typically compare by `id`; pre-persist entities all share `id=0`. Bump IDs manually in tests.
- **Spanish identifiers in tests** — test data (`clan.setNombre("Los Tigres")`, etc.) must stay in Spanish to match production fixtures and regex validators.
- **Time-sensitive code** — services that depend on `new Date()` do not currently inject a clock. Wrap in `Util` helpers if you need determinism, or refactor before testing.