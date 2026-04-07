# Testing Initiative — Phase 1 Design

**Date:** 2026-04-06
**Status:** Approved for implementation

## Goal

Bring the DotaChile codebase from **zero tests / no test infrastructure** to a runnable test baseline that:

1. Establishes JUnit 5 + Mockito + AssertJ + JaCoCo as the standard test stack.
2. Delivers high-value business tests on the most load-bearing domain logic.
3. Produces a JaCoCo coverage report that becomes the input for a separate Phase 2 plan targeting wider coverage (including ManagedBeans).

This is **Phase 1 of two**. Phase 1 explicitly does **not** chase a numeric coverage target. The headline output is real tests on real domain rules plus a coverage report we can reason from.

## Non-goals (Phase 1)

- 100% coverage on backend or MBs.
- Any test that requires a live container, JSF lifecycle, FacesContext, EJB injection, or a real database.
- Tests for entities (pure JPA boilerplate), facades (JPA passthroughs over `AbstractFacade`), or ManagedBeans of any kind.
- PowerMock, bytecode rewriters, or any tooling that would lock the project into hard-to-maintain magic.
- Refactoring production code beyond the minimum needed to make a target method testable. Any such refactor must be small, obviously safe, and called out in its commit message.

## Stack and infrastructure

| Component | Version | Reason |
|---|---|---|
| JUnit Jupiter | 5.10.x | Modern default; runs on Java 8. |
| Mockito Core | 3.12.4 | Last line that supports Java 8. |
| Mockito JUnit Jupiter | 3.12.4 | JUnit 5 integration matching the above. |
| AssertJ Core | 3.25.x | Fluent assertions; readable failures. |
| Maven Surefire | 3.2.x | Required for JUnit 5 discovery. |
| JaCoCo Maven Plugin | 0.8.11 | `prepare-agent` + `report` bound to `verify`. |

**Test source root:** `src/test/java`. The project's custom `<sourceDirectory>src/java</sourceDirectory>` does not break Maven's default test source resolution.

**Build commands after setup:**
- `mvn package` (online, one time) — seeds `~/.m2` with the new test dependencies.
- `mvn -o test` — runs the test suite offline (matches existing project workflow).
- `mvn -o verify` — runs tests and produces `target/site/jacoco/index.html`.

**JaCoCo Phase 1 exclusions** (re-included in Phase 2):
- `**/*MB.class` — ManagedBeans, deferred to Phase 2.
- Entity classes — to be enumerated after a quick pass during implementation; the principle is "no logic, just JPA boilerplate".

## Phase 1 test targets

All test classes mirror the production package layout under `src/test/java/com/dotachile/...`.

### Pure-logic targets (no mocks)

#### `EloSystemTest` — `com.dotachile.elo.EloSystem` (91 LoC)

Verifies core ELO math used by ladder and tournaments.

- Expected score symmetry: `expected(A,B) + expected(B,A) == 1.0` within epsilon.
- K-factor application produces expected delta on a known win/loss/draw.
- Draw handling produces equal-magnitude opposite deltas only when ratings are equal.
- Extreme rating gaps do not produce negative ratings or NaN.
- Rounding behavior on integer rating outputs matches the production formula.

Estimated tests: ~10.

#### `PvpgnHashTest` — `com.dotachile.shared.PvpgnHash` (199 LoC)

Verifies the PvPGN-compatible password hash. Breakage = mass auth lockout, so this is regression-locked.

- Two or three known `(plaintext, expected hash)` vectors generated once from the current production code and committed as anchors. Any future change that would alter the hash output fails loudly.
- Null and empty inputs behave as currently specified (or throw, whichever the code does — the test documents the contract).
- Case sensitivity matches PvPGN expectations.
- Length boundaries (1 char, very long).

Estimated tests: ~8.

#### `UtilTest` — `com.dotachile.shared.Util` (156 LoC)

One test per public helper. Each helper gets a happy-path test plus null-safety where applicable. Method list to be enumerated during implementation.

Estimated tests: ~12.

#### `BusinessLogicExceptionTest` — `com.dotachile.shared.BusinessLogicException` (31 LoC)

Constructor and message propagation. Free coverage; bundled into the same commit as `UtilTest`.

Estimated tests: ~3.

### Mock-based service targets

These use Mockito to mock `EntityManager`, facades, and any injected EJBs. No container, no database.

#### `LadderServiceTest` — `com.dotachile.ladder.service.LadderService` (577 LoC)

- Challenge creation: valid case persists a `Desafio`; self-challenge throws `BusinessLogicException`; banned challenger throws.
- Challenge expiration: expired `Desafio` is rejected.
- Result submission: valid result calls `EloSystem` and updates both `Ladder` rows.
- K-factor transition between `FaseLadder` phases: a result submitted under phase X uses phase X's K-factor.
- Idempotency: submitting a result for an already-resolved challenge throws.
- Duplicate-challenge guards.

Estimated tests: ~15.

#### `TorneoServiceTest` — `com.dotachile.torneos.service.TorneoService` (1816 LoC) — **scoped**

This class is large. Phase 1 covers a deliberate subset; the remaining methods are deferred to Phase 2 with a coverage rationale.

**In scope for Phase 1:**
- Phase lifecycle: valid `FaseTorneo` transitions succeed; invalid transitions throw.
- Standings calculation: given a fixed list of `Resultado`, the produced standings match expected ordering and tiebreakers.
- Bracket / round generation invariants: round count, match count, no clan facing itself.
- Enrollment guards: closed phase rejects enrollment; duplicate clan rejects; missing `Confirmacion` rejects.

**Out of scope for Phase 1 (explicitly Phase 2):** any method that mutates more than three collaborators in one call, anything that touches the file system, anything that emits notifications, anything that calls `ServiciosAutomatizados`. If during implementation a target method turns out to be too entangled to test without container support, it gets dropped from Phase 1 and listed in the Phase 2 plan with the reason. Renegotiation of scope happens before writing the test, not after.

Estimated tests: ~15.

#### `ClanServiceTest` — `com.dotachile.clanes.service.ClanService` (754 LoC) — **scoped**

**In scope for Phase 1:**
- Clan creation: name uniqueness check; required fields; founder assignment.
- Ban application: a `ClanBan` is persisted with the right expiration timestamp.
- Ban expiration check: an expired `ClanBan` is treated as inactive; an active one blocks operations.
- Member add/remove: roster size limits; duplicate member rejection; removing a non-member throws.

**Out of scope for Phase 1:** clan merger flows, historical movement (`Movimiento`) reconstruction, anything that crosses into `TorneoService`. Deferred to Phase 2.

Estimated tests: ~12.

### Phase 1 totals

- 7 test classes
- ~75 tests
- All green on `mvn -o test`
- JaCoCo report generated by `mvn -o verify`

## Commit plan

Test infrastructure and test code are committed separately. Each commit produces a green build in isolation.

1. **`chore(test): add JUnit 5, Mockito, AssertJ, JaCoCo to build`** — `pom.xml` only, plus `src/test/java/.gitkeep`. Verifies `mvn test` exits 0 with zero tests and `mvn verify` produces an empty JaCoCo report.
2. **`test(shared): cover EloSystem`**
3. **`test(shared): cover PvpgnHash`**
4. **`test(shared): cover Util and BusinessLogicException`**
5. **`test(ladder): cover LadderService`**
6. **`test(torneos): cover TorneoService lifecycle, standings, enrollment guards`**
7. **`test(clanes): cover ClanService validation, bans, membership`**
8. **`docs(superpowers): record Phase 1 coverage baseline and Phase 2 plan`** — commits a short summary of the JaCoCo numbers and the Phase 2 spec document.

If a test commit needs a tweak to `pom.xml` (e.g. an extra Surefire system property for a specific test), that change goes in the same commit as the test that needs it, with the reason in the commit message. The point of the rule is to keep general infrastructure setup separate from feature test code, not to forbid all subsequent build edits.

## Phase 2 (planned, executed in a separate session)

Phase 2 is a follow-up plan that **must** be approved separately after the Phase 1 coverage report is reviewed. This Phase 1 spec only commits to producing the Phase 2 plan as a deliverable, not to executing it.

The Phase 2 plan will:

- Read the actual Phase 1 JaCoCo report and list every uncovered package with line / branch counts.
- For each remaining package, decide one of: `test`, `exclude with rationale`, or `defer further`.
- Address ManagedBeans specifically: propose a thin `FacesContext` test helper, OR propose extracting business logic out of MBs into testable collaborators where the existing MB makes mocking impractical.
- Set a numeric coverage *floor* per package rather than chasing literal 100%.
- Be sized as its own implementation plan with its own commit-by-commit breakdown.

## Risks and assumptions

- **Mockito 3.12.4 pinned.** If `~/.m2` already contains a newer Mockito from another project, Maven still resolves the declared version — no conflict.
- **No PowerMock.** If a target method has untestable static calls, the response is either a minimal injection refactor or moving the method to Phase 2 with a documented reason. PowerMock is explicitly forbidden as a Java 8 maintenance trap.
- **`TorneoService` size.** At 1816 lines this class may turn out to be more entangled than expected. The implementation step starts by reading it; if the in-scope methods cannot be isolated, scope is renegotiated with the user *before* tests are written, not after.
- **Spanish-language rule.** Test class names mirror production class names (already mixed Spanish/English). Test method names use the JUnit-conventional English `should...` pattern. This is a tooling convention, not domain naming, and stays English unless the user requests otherwise.
- **Online build required once.** The first `mvn package` after the infra commit must run with network access to pull JUnit 5, Mockito, AssertJ, Surefire 3.x, and JaCoCo. After that, `mvn -o` works as before.
- **No production behavior changes.** Any refactor required to make a target testable is small, obviously safe, and called out in the commit message. If a refactor is not obviously safe, the target moves to Phase 2.

## Definition of done (Phase 1)

- `mvn -o test` is green from a clean checkout (after the one-time online seed).
- `mvn -o verify` produces `target/site/jacoco/index.html`.
- All seven test classes exist and contain real assertions.
- Eight commits land in the order specified above.
- The Phase 2 spec document exists at `docs/superpowers/specs/<date>-testing-phase-2-design.md` (date assigned at the time of writing).