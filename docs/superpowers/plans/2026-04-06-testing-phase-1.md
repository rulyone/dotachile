# Testing Phase 1 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Bring DotaChile from zero tests to a runnable test baseline with high-value business tests on the most load-bearing domain logic, plus a JaCoCo coverage report that drives Phase 2 planning.

**Architecture:** JUnit 5 + Mockito 3.12.4 (last Java 8-compatible) + AssertJ + JaCoCo, configured in `pom.xml` alongside the existing custom `<sourceDirectory>src/java</sourceDirectory>`. Tests live in standard `src/test/java`. Service tests use Mockito mocks for `EntityManager` / facades; no container, no database.

**Tech Stack:** Java 8, Maven, JUnit Jupiter 5.10.2, Mockito 3.12.4, AssertJ 3.25.3, Surefire 3.2.5, JaCoCo 0.8.11.

**Spec:** `docs/superpowers/specs/2026-04-06-testing-phase-1-design.md`

---

## File Structure

### Files to create

```
src/test/java/.gitkeep
src/test/java/com/dotachile/elo/EloSystemTest.java
src/test/java/com/dotachile/shared/PvpgnHashTest.java
src/test/java/com/dotachile/shared/UtilTest.java
src/test/java/com/dotachile/shared/BusinessLogicExceptionTest.java
src/test/java/com/dotachile/ladder/service/LadderServiceTest.java
src/test/java/com/dotachile/torneos/service/TorneoServiceTest.java
src/test/java/com/dotachile/clanes/service/ClanServiceTest.java
docs/superpowers/specs/2026-04-06-testing-phase-2-design.md  (Task 9 only)
```

### Files to modify

- `pom.xml` — add test dependencies, Surefire 3.x, JaCoCo plugin, JaCoCo Phase 1 exclusions.

### Files NOT to modify

Production code is **not** modified in Phase 1. If a target method turns out to be untestable without a refactor, the rule is to drop it from Phase 1 and document in the Phase 2 spec — not to refactor on the side. The one exception: if a *trivial* visibility change (e.g. package-private constructor) is required and obviously safe, it goes in the same commit as the test that needs it, with the reason in the commit message.

---

## Important context the implementer needs

**Spanish-language rule.** Class, entity, and field names are Spanish. Test class names mirror production class names. Test method names use English `should...` JUnit convention.

**Custom Maven layout.** `pom.xml` already declares `<sourceDirectory>src/java</sourceDirectory>` for production code. **Do not** override `<testSourceDirectory>` — Maven still picks up `src/test/java` as the default test root. Verified: this is how Maven `<sourceDirectory>` works; the test root is independently configured.

**Offline build.** The project's normal build is `mvn -o package`. After the infra commit, the implementer must run `mvn package` (no `-o`) **once** with network access to seed `~/.m2`. After that, `mvn -o test` and `mvn -o verify` work as usual.

**EJB / JPA service classes** are `@Stateless` beans with `@EJB`-injected facade fields. For tests, instantiate the service with `new`, then use Mockito to create facade mocks and assign them via reflection (the fields are `private`). Helper pattern shown in Task 5.

**EloSystem public surface.** Only one public method: `EloSystem.calculoVariacion(int score, int scoreOponente, boolean gano, int factorK)`. Returns an `int` rating delta. The expected-score table is private — test it indirectly through `calculoVariacion`.

**Util public surface usable without FacesContext** (the only ones we test in Phase 1):
- `String cambiarSlashes(String)`
- `String hashPassword(String rawpassword, String username)`
- `Date dateSinMillis(Date)`
- `Date dateSinTime(Date)`
- `boolean caracteresValidosPvpgn(String)`

The other Util methods (`addErrorMessage`, `addInfoMessage`, `addFatalMessage`, `addWarnMessage`, `keepMessages`, `navigate`, `getUsuarioLogeado`) all touch `FacesContext` or JNDI and are excluded from Phase 1. They go in Phase 2 alongside the MB work.

**`Torneo.getStandings`** — standings logic lives on the entity, not the service. The standings test in `TorneoServiceTest` constructs a `Torneo` with a fixed list of `GameMatch`/`Resultado` and asserts the produced standings ordering. This is the only Phase 1 test that exercises an entity directly; it's bundled into `TorneoServiceTest` because it covers the spec's "standings calculation" requirement and renaming the file would be confusing.

---

## Task 1: Test infrastructure (pom.xml + JaCoCo)

**Files:**
- Modify: `pom.xml`
- Create: `src/test/java/.gitkeep`

- [ ] **Step 1: Add test dependencies to `pom.xml`**

Insert these into the existing `<dependencies>` block, after the existing entries:

```xml
        <!-- Test dependencies -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.10.2</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <version>3.12.4</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-junit-jupiter</artifactId>
            <version>3.12.4</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.assertj</groupId>
            <artifactId>assertj-core</artifactId>
            <version>3.25.3</version>
            <scope>test</scope>
        </dependency>
```

Mockito 3.12.4 is the last line that supports Java 8. Do **not** bump it. AssertJ 3.x supports Java 8.

- [ ] **Step 2: Add Surefire 3.x and JaCoCo plugins to `pom.xml`**

Insert into the existing `<plugins>` block, after `maven-compiler-plugin`:

```xml
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.2.5</version>
            </plugin>

            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <version>0.8.11</version>
                <configuration>
                    <excludes>
                        <!-- Phase 1 exclusions; revisited in Phase 2 -->
                        <exclude>**/*MB.class</exclude>
                        <exclude>com/dotachile/**/entity/**</exclude>
                        <exclude>com/dotachile/**/facade/**</exclude>
                    </excludes>
                </configuration>
                <executions>
                    <execution>
                        <id>prepare-agent</id>
                        <goals>
                            <goal>prepare-agent</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>report</id>
                        <phase>verify</phase>
                        <goals>
                            <goal>report</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
```

- [ ] **Step 3: Create the test source root**

```bash
mkdir -p src/test/java
touch src/test/java/.gitkeep
```

- [ ] **Step 4: Seed local Maven cache with test deps (online, one time)**

Run: `mvn package`
Expected: BUILD SUCCESS. New artifacts pulled: junit-jupiter, mockito-core 3.12.4, assertj-core 3.25.3, jacoco-maven-plugin 0.8.11, maven-surefire-plugin 3.2.5.
If this fails because a transitive dep can't be resolved, fix it before continuing — do not proceed with broken infra.

- [ ] **Step 5: Verify zero-test build is green offline**

Run: `mvn -o test`
Expected: BUILD SUCCESS, "Tests run: 0".

- [ ] **Step 6: Verify JaCoCo report generation works**

Run: `mvn -o verify`
Expected: BUILD SUCCESS. File exists: `target/site/jacoco/index.html`. The report will show 0% on most things — that is fine; we just need the wiring to work.

- [ ] **Step 7: Commit infrastructure only**

```bash
git add pom.xml src/test/java/.gitkeep
git commit -m "$(cat <<'EOF'
chore(test): add JUnit 5, Mockito, AssertJ, JaCoCo to build

Establishes the test stack for the Phase 1 testing initiative. Phase 1
JaCoCo exclusions cover MBs, entities, and facades; these are revisited
in Phase 2.

Co-Authored-By: Claude Opus 4.6 (1M context) <noreply@anthropic.com>
EOF
)"
```

This commit MUST contain only `pom.xml` and the `.gitkeep`. No test code yet.

---

## Task 2: EloSystemTest

**Files:**
- Create: `src/test/java/com/dotachile/elo/EloSystemTest.java`

**What this test verifies (per spec section "EloSystemTest"):**
- The only public method is `EloSystem.calculoVariacion(int score, int scoreOponente, boolean gano, int factorK)`. Returns an `int` rating delta.
- The expected-score table is a private switch on rating differences in 7-point bands. Test it indirectly: at equal ratings the delta is `K * (1 - 0.5) = K/2` for a win and `K * (0 - 0.5) = -K/2` for a loss. At very large gaps the favorite wins almost zero and the underdog wins almost K.

- [ ] **Step 1: Write the full test class**

```java
package com.dotachile.elo;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class EloSystemTest {

    private static final int K = 32;

    @Test
    void winnerAtEqualRatingGainsHalfK() {
        int delta = EloSystem.calculoVariacion(1500, 1500, true, K);
        assertThat(delta).isEqualTo(16); // (int)(32 * (1 - 0.5))
    }

    @Test
    void loserAtEqualRatingLosesHalfK() {
        int delta = EloSystem.calculoVariacion(1500, 1500, false, K);
        assertThat(delta).isEqualTo(-16); // (int)(32 * (0 - 0.5))
    }

    @Test
    void winnerGainAndLoserLossAreEqualMagnitudeAtEqualRating() {
        int win = EloSystem.calculoVariacion(1500, 1500, true, K);
        int loss = EloSystem.calculoVariacion(1500, 1500, false, K);
        assertThat(win).isEqualTo(-loss);
    }

    @Test
    void favoriteWinningGainsLessThanHalfK() {
        // 200-point favorite. Expected score per table at dif 198..206 is 0.76.
        // gain = (int)(32 * (1 - 0.76)) = (int)(7.68) = 7
        int delta = EloSystem.calculoVariacion(1700, 1500, true, K);
        assertThat(delta).isEqualTo(7);
    }

    @Test
    void underdogWinningGainsMoreThanHalfK() {
        // 200-point underdog. Expected score for low side at dif 198..206 is 0.24.
        // gain = (int)(32 * (1 - 0.24)) = (int)(24.32) = 24
        int delta = EloSystem.calculoVariacion(1500, 1700, true, K);
        assertThat(delta).isEqualTo(24);
    }

    @Test
    void favoriteLosingLosesAlmostFullK() {
        // 200-point favorite losing. lose = (int)(32 * (0 - 0.76)) = (int)(-24.32) = -24
        int delta = EloSystem.calculoVariacion(1700, 1500, false, K);
        assertThat(delta).isEqualTo(-24);
    }

    @Test
    void extremeFavoriteWinsAlmostNothing() {
        // dif >= 736: expected = 1.00 for favorite. gain = (int)(32 * 0) = 0
        int delta = EloSystem.calculoVariacion(3000, 1000, true, K);
        assertThat(delta).isZero();
    }

    @Test
    void extremeUnderdogWinningGainsFullK() {
        // dif >= 736 for underdog: expected = 0.00. gain = (int)(32 * 1) = 32
        int delta = EloSystem.calculoVariacion(1000, 3000, true, K);
        assertThat(delta).isEqualTo(32);
    }

    @Test
    void extremeFavoriteLosingLosesFullK() {
        int delta = EloSystem.calculoVariacion(3000, 1000, false, K);
        assertThat(delta).isEqualTo(-32);
    }

    @Test
    void zeroKFactorAlwaysReturnsZero() {
        assertThat(EloSystem.calculoVariacion(1500, 1500, true, 0)).isZero();
        assertThat(EloSystem.calculoVariacion(1500, 1700, false, 0)).isZero();
    }

    @Test
    void smallRatingGapBelowFourBehavesLikeEqual() {
        // dif <= 3: expected = 0.50 either way
        int win = EloSystem.calculoVariacion(1500, 1502, true, K);
        assertThat(win).isEqualTo(16);
    }

    @Test
    void variationDoesNotProduceNaN() {
        // Pure-int output, but make sure huge negative gap doesn't blow up.
        int delta = EloSystem.calculoVariacion(0, 5000, false, K);
        assertThat(delta).isLessThanOrEqualTo(0);
    }
}
```

- [ ] **Step 2: Run the test**

Run: `mvn -o test -Dtest=EloSystemTest`
Expected: BUILD SUCCESS, "Tests run: 12, Failures: 0".

If a delta assertion fails, the spec table values are correct — re-check arithmetic against the actual `EloSystem.java` lines. Do not alter the production code; adjust the test expectation to match production reality and document the discrepancy in the commit message if it's surprising.

- [ ] **Step 3: Commit**

```bash
git add src/test/java/com/dotachile/elo/EloSystemTest.java
git commit -m "$(cat <<'EOF'
test(shared): cover EloSystem.calculoVariacion

Locks in ELO rating delta math used by ladder and tournaments. Tests
hit equal-rating, moderate-gap, and extreme-gap branches via the
public calculoVariacion entry point.

Co-Authored-By: Claude Opus 4.6 (1M context) <noreply@anthropic.com>
EOF
)"
```

---

## Task 3: PvpgnHashTest

**Files:**
- Create: `src/test/java/com/dotachile/shared/PvpgnHashTest.java`

**What this test verifies:** PvPGN-compatible password hash, locked against regression because breakage = mass auth lockout.

The test must not embed magic hex strings without first capturing them from the live production code. The procedure: write the test with a placeholder, run it, copy the *actual* output into the test as the locked value, run it again to confirm green. From then on any change to production hashing fails this test.

- [ ] **Step 1: Write the test class with capture-then-lock procedure**

```java
package com.dotachile.shared;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PvpgnHashTest {

    // These three vectors are regression anchors. They were captured once from
    // the current production implementation. If any of these break, PvPGN auth
    // is broken — investigate before "fixing" the test.
    private static final String VECTOR_PASSWORD_HASH = "__REPLACE_WITH_CAPTURED_VALUE__";
    private static final String VECTOR_EMPTY_HASH = "__REPLACE_WITH_CAPTURED_VALUE__";
    private static final String VECTOR_LONG_HASH = "__REPLACE_WITH_CAPTURED_VALUE__";

    @Test
    void hashOfKnownPasswordMatchesLockedValue() {
        String hash = PvpgnHash.GetHash("password");
        assertThat(hash).isEqualTo(VECTOR_PASSWORD_HASH);
    }

    @Test
    void hashOfEmptyStringMatchesLockedValue() {
        String hash = PvpgnHash.GetHash("");
        assertThat(hash).isEqualTo(VECTOR_EMPTY_HASH);
    }

    @Test
    void hashOfLongPasswordMatchesLockedValue() {
        String hash = PvpgnHash.GetHash("this-is-a-much-longer-password-with-some-numbers-1234567890");
        assertThat(hash).isEqualTo(VECTOR_LONG_HASH);
    }

    @Test
    void hashIsCaseInsensitiveForAscii() {
        // PvPGN hash explicitly lowercases ASCII before hashing (toLowerUnicode)
        assertThat(PvpgnHash.GetHash("PASSWORD")).isEqualTo(PvpgnHash.GetHash("password"));
        assertThat(PvpgnHash.GetHash("Password")).isEqualTo(PvpgnHash.GetHash("password"));
    }

    @Test
    void hashOutputIsAlwaysFortyHexCharacters() {
        // SHA1-based, 20 bytes = 40 hex chars (asHex pads odd-length runs)
        assertThat(PvpgnHash.GetHash("a")).hasSize(40);
        assertThat(PvpgnHash.GetHash("password")).hasSize(40);
        assertThat(PvpgnHash.GetHash("")).hasSize(40);
    }

    @Test
    void byteArrayOverloadProducesTwentyBytes() {
        byte[] result = PvpgnHash.GetHash("password".getBytes());
        assertThat(result).hasSize(20);
    }

    @Test
    void differentInputsProduceDifferentHashes() {
        assertThat(PvpgnHash.GetHash("password1"))
                .isNotEqualTo(PvpgnHash.GetHash("password2"));
    }

    @Test
    void inputOver1024BytesThrows() {
        StringBuilder huge = new StringBuilder();
        for (int i = 0; i < 1100; i++) huge.append('a');
        assertThatThrownBy(() -> PvpgnHash.GetHash(huge.toString()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
```

- [ ] **Step 2: Capture the regression vectors**

Run: `mvn -o test -Dtest=PvpgnHashTest#hashOfKnownPasswordMatchesLockedValue`
Expected: FAIL with an assertion that includes the actual computed hash for `"password"`.

Copy the actual value into `VECTOR_PASSWORD_HASH`. Repeat for `VECTOR_EMPTY_HASH` and `VECTOR_LONG_HASH` by running each test individually and pasting the captured value into the constant.

- [ ] **Step 3: Run all tests in the class to confirm green**

Run: `mvn -o test -Dtest=PvpgnHashTest`
Expected: BUILD SUCCESS, "Tests run: 8, Failures: 0".

- [ ] **Step 4: Commit**

```bash
git add src/test/java/com/dotachile/shared/PvpgnHashTest.java
git commit -m "$(cat <<'EOF'
test(shared): cover PvpgnHash with regression-locked vectors

Three input/output vectors captured from the current implementation
serve as anchors. Any future change to PvpgnHash that would alter
its output fails these tests loudly — auth breakage protection.

Co-Authored-By: Claude Opus 4.6 (1M context) <noreply@anthropic.com>
EOF
)"
```

---

## Task 4: UtilTest + BusinessLogicExceptionTest

**Files:**
- Create: `src/test/java/com/dotachile/shared/UtilTest.java`
- Create: `src/test/java/com/dotachile/shared/BusinessLogicExceptionTest.java`

**Scope reminder:** Only the five FacesContext-free `Util` methods are in Phase 1. JSF helpers go to Phase 2.

- [ ] **Step 1: Write `BusinessLogicExceptionTest.java`**

```java
package com.dotachile.shared;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class BusinessLogicExceptionTest {

    @Test
    void noArgConstructorHasNullMessage() {
        BusinessLogicException ex = new BusinessLogicException();
        assertThat(ex.getMessage()).isNull();
    }

    @Test
    void messageConstructorPropagatesMessage() {
        BusinessLogicException ex = new BusinessLogicException("clan banned");
        assertThat(ex.getMessage()).isEqualTo("clan banned");
    }

    @Test
    void isACheckedException() {
        // Important: callers expect this to be checked so business rules are
        // enforced at compile time on service signatures.
        assertThat(Exception.class).isAssignableFrom(BusinessLogicException.class);
        assertThat(RuntimeException.class.isAssignableFrom(BusinessLogicException.class)).isFalse();
    }
}
```

- [ ] **Step 2: Write `UtilTest.java`**

```java
package com.dotachile.shared;

import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.assertj.core.api.Assertions.assertThat;

class UtilTest {

    // ----- cambiarSlashes -----

    @Test
    void cambiarSlashesReplacesBackslashesWithForwardSlashes() {
        assertThat(Util.cambiarSlashes("c:\\foo\\bar.txt")).isEqualTo("c:/foo/bar.txt");
    }

    @Test
    void cambiarSlashesIsIdentityForStringWithoutBackslashes() {
        assertThat(Util.cambiarSlashes("/already/clean")).isEqualTo("/already/clean");
    }

    @Test
    void cambiarSlashesPreservesEmptyString() {
        assertThat(Util.cambiarSlashes("")).isEqualTo("");
    }

    // ----- hashPassword(String, String) -----

    @Test
    void hashPasswordIsDeterministic() {
        String h1 = Util.hashPassword("secret", "alice");
        String h2 = Util.hashPassword("secret", "alice");
        assertThat(h1).isEqualTo(h2);
    }

    @Test
    void hashPasswordDiffersWhenUsernameDiffers() {
        assertThat(Util.hashPassword("secret", "alice"))
                .isNotEqualTo(Util.hashPassword("secret", "bob"));
    }

    @Test
    void hashPasswordDiffersWhenPasswordDiffers() {
        assertThat(Util.hashPassword("secretA", "alice"))
                .isNotEqualTo(Util.hashPassword("secretB", "alice"));
    }

    @Test
    void hashPasswordOutputIsLowercaseHex32Chars() {
        String h = Util.hashPassword("secret", "alice");
        assertThat(h).hasSize(32);
        assertThat(h).matches("[0-9a-f]{32}");
    }

    // ----- dateSinMillis -----

    @Test
    void dateSinMillisZeroesTheMillisecondField() {
        Calendar cal = new GregorianCalendar(2026, Calendar.APRIL, 6, 14, 30, 45);
        cal.set(Calendar.MILLISECOND, 789);
        Date stripped = Util.dateSinMillis(cal.getTime());

        Calendar check = new GregorianCalendar();
        check.setTime(stripped);
        assertThat(check.get(Calendar.MILLISECOND)).isZero();
        assertThat(check.get(Calendar.SECOND)).isEqualTo(45);
        assertThat(check.get(Calendar.MINUTE)).isEqualTo(30);
        assertThat(check.get(Calendar.HOUR_OF_DAY)).isEqualTo(14);
    }

    // ----- dateSinTime -----

    @Test
    void dateSinTimeZeroesAllTimeFields() {
        Calendar cal = new GregorianCalendar(2026, Calendar.APRIL, 6, 14, 30, 45);
        cal.set(Calendar.MILLISECOND, 789);
        Date stripped = Util.dateSinTime(cal.getTime());

        Calendar check = new GregorianCalendar();
        check.setTime(stripped);
        assertThat(check.get(Calendar.MILLISECOND)).isZero();
        assertThat(check.get(Calendar.SECOND)).isZero();
        assertThat(check.get(Calendar.MINUTE)).isZero();
        assertThat(check.get(Calendar.HOUR_OF_DAY)).isZero();
        assertThat(check.get(Calendar.DAY_OF_MONTH)).isEqualTo(6);
        assertThat(check.get(Calendar.MONTH)).isEqualTo(Calendar.APRIL);
        assertThat(check.get(Calendar.YEAR)).isEqualTo(2026);
    }

    // ----- caracteresValidosPvpgn -----

    @Test
    void caracteresValidosPvpgnAcceptsLettersDigitsAndAllowedSymbols() {
        assertThat(Util.caracteresValidosPvpgn("Player_1")).isTrue();
        assertThat(Util.caracteresValidosPvpgn("a-b_c^d.e[f]g")).isTrue();
        assertThat(Util.caracteresValidosPvpgn("ABCxyz123")).isTrue();
    }

    @Test
    void caracteresValidosPvpgnRejectsSpaces() {
        assertThat(Util.caracteresValidosPvpgn("hello world")).isFalse();
    }

    @Test
    void caracteresValidosPvpgnRejectsPunctuationOutsideAllowList() {
        assertThat(Util.caracteresValidosPvpgn("hi!")).isFalse();
        assertThat(Util.caracteresValidosPvpgn("name@host")).isFalse();
        assertThat(Util.caracteresValidosPvpgn("a/b")).isFalse();
    }

    @Test
    void caracteresValidosPvpgnReturnsTrueForEmptyString() {
        // Vacuously true: the loop never runs.
        assertThat(Util.caracteresValidosPvpgn("")).isTrue();
    }
}
```

- [ ] **Step 3: Run both test classes**

Run: `mvn -o test -Dtest=UtilTest,BusinessLogicExceptionTest`
Expected: BUILD SUCCESS. Tests run: 17, Failures: 0.

- [ ] **Step 4: Commit**

```bash
git add src/test/java/com/dotachile/shared/UtilTest.java src/test/java/com/dotachile/shared/BusinessLogicExceptionTest.java
git commit -m "$(cat <<'EOF'
test(shared): cover Util pure helpers and BusinessLogicException

Covers the FacesContext-free subset of Util (cambiarSlashes,
hashPassword, dateSinMillis, dateSinTime, caracteresValidosPvpgn)
and the BusinessLogicException constructors. JSF-bound helpers in
Util are deferred to Phase 2 alongside the MB work.

Co-Authored-By: Claude Opus 4.6 (1M context) <noreply@anthropic.com>
EOF
)"
```

---

## Task 5: LadderServiceTest

**Files:**
- Create: `src/test/java/com/dotachile/ladder/service/LadderServiceTest.java`

**Business rules covered (from `com/dotachile/ladder/CLAUDE.md`):**
- `crearLadderYComenzarlo`: null guards, single-ladder constraint, starts as `STARTED`.
- `desafiarClan`: chief/shaman authorization, both clans ≥5 members, neither banned, neither side over 10 pending challenges.
- `aceptarDesafio`: chief/shaman authorization, both clans ≥5 members, neither banned.
- `confirmarResultadoDesafio`: only losing clan's chief/shaman; ELO recalculated via `EloSystem`; DOBLE_WO yields no ELO change and is admin-only.
- `pausarLadder`/`despausarLadder`: phase transitions.

**Mocking pattern:** `LadderService` is `@Stateless` with seven `@EJB private` facade fields. Tests instantiate `new LadderService()` and inject Mockito mocks via reflection. The pattern below is the canonical one — use it for every service test in this plan.

- [ ] **Step 1: Write the test class with the full reflection-injection helper and 4 fully-coded canonical tests**

```java
package com.dotachile.ladder.service;

import com.dotachile.auth.entity.Usuario;
import com.dotachile.auth.facade.UsuarioFacade;
import com.dotachile.clanes.entity.Clan;
import com.dotachile.clanes.entity.ClanBan;
import com.dotachile.clanes.facade.ClanBanFacade;
import com.dotachile.clanes.facade.ClanFacade;
import com.dotachile.ladder.entity.Desafio;
import com.dotachile.ladder.entity.FaseLadder;
import com.dotachile.ladder.entity.Ladder;
import com.dotachile.ladder.facade.DesafioFacade;
import com.dotachile.ladder.facade.LadderFacade;
import com.dotachile.media.ReplayFacade;
import com.dotachile.shared.BusinessLogicException;
import com.dotachile.torneos.entity.FactorK;
import com.dotachile.torneos.facade.GameFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.ejb.SessionContext;
import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LadderServiceTest {

    private LadderService service;
    private UsuarioFacade userFac;
    private ClanFacade clanFac;
    private DesafioFacade desafioFac;
    private LadderFacade ladderFac;
    private GameFacade gameFac;
    private ReplayFacade replayFac;
    private ClanBanFacade clanBanFac;
    private SessionContext ctx;

    @BeforeEach
    void setUp() throws Exception {
        service = new LadderService();
        userFac = mock(UsuarioFacade.class);
        clanFac = mock(ClanFacade.class);
        desafioFac = mock(DesafioFacade.class);
        ladderFac = mock(LadderFacade.class);
        gameFac = mock(GameFacade.class);
        replayFac = mock(ReplayFacade.class);
        clanBanFac = mock(ClanBanFacade.class);
        ctx = mock(SessionContext.class);

        inject(service, "userFac", userFac);
        inject(service, "clanFac", clanFac);
        inject(service, "desafioFac", desafioFac);
        inject(service, "ladderFac", ladderFac);
        inject(service, "gameFac", gameFac);
        inject(service, "replayFac", replayFac);
        inject(service, "clanBanFac", clanBanFac);
        inject(service, "ctx", ctx);
    }

    /** Reflection-set a private field on a target. */
    private static void inject(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }

    // ===== crearLadderYComenzarlo =====

    // NOTE on FactorK constants: the FactorK enum holds K-factor values 10..100
    // step 10. The enum constant names below (FactorK.K_30) are the assumed
    // naming. Before running, the implementer opens FactorK.java and replaces
    // these constants with the real names if they differ (e.g. FactorK.THIRTY).

    @Test
    void crearLadderYComenzarloThrowsWhenNombreIsNull() {
        when(ladderFac.count()).thenReturn(0);
        assertThatThrownBy(() -> service.crearLadderYComenzarlo(null, "info", FactorK.K_30))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("requerido");
    }

    @Test
    void crearLadderYComenzarloThrowsWhenALadderAlreadyExists() {
        when(ladderFac.count()).thenReturn(1);
        assertThatThrownBy(() -> service.crearLadderYComenzarlo("L1", "info", FactorK.K_30))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("solo se puede tener 1 ladder");
    }

    @Test
    void crearLadderYComenzarloPersistsLadderInStartedPhase() throws Exception {
        when(ladderFac.count()).thenReturn(0);

        service.crearLadderYComenzarlo("L1", "info", FactorK.K_30);

        ArgumentCaptor<Ladder> captor = ArgumentCaptor.forClass(Ladder.class);
        verify(ladderFac).create(captor.capture());
        Ladder created = captor.getValue();
        assertThat(created.getNombre()).isEqualTo("L1");
        assertThat(created.getInformacion()).isEqualTo("info");
        assertThat(created.getFactorK()).isEqualTo(FactorK.K_30);
        assertThat(created.getFaseLadder()).isEqualTo(FaseLadder.STARTED);
    }

    // ===== desafiarClan: representative bann-check test =====
    //
    // The full set of desafiarClan tests is listed below as a single TODO list.
    // The pattern: build a Usuario, build two Clans (desafiador / rival), wire
    // up findByUsername / find by tag, set the offending precondition, call
    // desafiarClan, assert BusinessLogicException with the expected message.

    @Test
    void desafiarClanThrowsWhenDesafiadorClanIsBanned() {
        Usuario chief = chieftainOf("clan-A", 5);
        Clan rival = clanWith("clan-B", 5);
        when(userFac.findByUsername("alice")).thenReturn(chief);
        when(clanFac.findByTag("clan-B")).thenReturn(rival);
        when(clanBanFac.findByClan(chief.getClan())).thenReturn(new ClanBan());
        when(clanBanFac.findByClan(rival)).thenReturn(null);

        assertThatThrownBy(() -> service.desafiarClan("alice", "clan-B"))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("baneado");

        verify(desafioFac, never()).create(any(Desafio.class));
    }

    // ----- helpers (build minimal entities for the tests above) -----

    /** Build a Usuario who is chieftain of a Clan with the given tag and integrante count. */
    private Usuario chieftainOf(String tag, int integranteCount) {
        Clan clan = clanWith(tag, integranteCount);
        Usuario user = new Usuario();
        // The exact setter names depend on the Usuario entity. The implementer
        // adapts the next two lines after a quick glance at Usuario.java.
        user.setUsername("alice");
        user.setClan(clan);
        clan.setChieftain(user);
        return user;
    }

    /** Build a Clan with the given tag and exactly N integrantes (chieftain not yet attached). */
    private Clan clanWith(String tag, int integranteCount) {
        Clan clan = new Clan();
        clan.setTag(tag);
        java.util.List<Usuario> roster = new java.util.ArrayList<>();
        for (int i = 0; i < integranteCount; i++) {
            Usuario u = new Usuario();
            u.setUsername(tag + "-member-" + i);
            roster.add(u);
        }
        clan.setIntegrantes(roster);
        return clan;
    }
}
```

- [ ] **Step 2: Verify the canonical test class compiles and the 4 tests pass**

Run: `mvn -o test -Dtest=LadderServiceTest`
Expected: BUILD SUCCESS, "Tests run: 4, Failures: 0".

If a setter name (e.g. `setIntegrantes`, `setChieftain`) does not exist on the entity, the implementer must read `Clan.java` / `Usuario.java` and adjust the helper to match the real API. **Do not modify the entity to add a setter** — adapt the helper instead.

If a method this test calls does not exist on `LadderService` (e.g. `desafiarClan(String, String)` has a different signature), the implementer must read `LadderService.java`, find the actual signature, and adjust the test call site. **Do not modify the service.**

- [ ] **Step 3: Add the remaining 11 tests for `LadderService`**

Each bullet below is one test. The implementer follows the canonical pattern from Step 1 (build minimal entities, stub the relevant facades, call the service, assert outcome). Method names and exact behavior come from `com/dotachile/ladder/CLAUDE.md` and `LadderService.java`.

  - **`desafiarClanThrowsWhenRivalClanIsBanned`** — symmetric to the canonical test, ban on the rival side.
  - **`desafiarClanThrowsWhenDesafiadorIsNotChieftainOrShaman`** — caller is a peon/grunt; expect "chieftain" or "shaman" in the error message.
  - **`desafiarClanThrowsWhenDesafiadorHasFewerThanFiveIntegrantes`** — set roster to 4; expect "5" in the error message.
  - **`desafiarClanThrowsWhenRivalHasFewerThanFiveIntegrantes`** — symmetric.
  - **`desafiarClanThrowsWhenDesafiadorAlreadyHasTenPendingChallenges`** — stub `desafioFac.findPendingByClan(...)` (or whichever method the service calls) to return a list of size 10; expect rejection.
  - **`desafiarClanThrowsWhenRivalAlreadyHasTenPendingChallenges`** — symmetric.
  - **`desafiarClanPersistsDesafioOnHappyPath`** — all preconditions satisfied; capture the persisted `Desafio`, assert `desafiador`, `rival`, `fechaDesafio` non-null, both flags false.
  - **`aceptarDesafioThrowsWhenCallerIsNotRivalChieftainOrShaman`**.
  - **`aceptarDesafioFlipsAceptadoFlagOnHappyPath`** — capture the merged `Desafio`, assert `desafioAceptado == true`.
  - **`pausarLadderTransitionsToPaused`** — stub `ladderFac.findAll()` (or single-ladder accessor); call `pausarLadder`; verify the merged ladder has `FaseLadder.PAUSED`.
  - **`despausarLadderTransitionsToStarted`** — symmetric.

For each: write the test, run it, ensure green, then move to the next. Do not add extra tests not on this list — Phase 2 will pick up the rest.

- [ ] **Step 4: Run the full LadderServiceTest**

Run: `mvn -o test -Dtest=LadderServiceTest`
Expected: BUILD SUCCESS, "Tests run: 15, Failures: 0".

- [ ] **Step 5: Commit**

```bash
git add src/test/java/com/dotachile/ladder/service/LadderServiceTest.java
git commit -m "$(cat <<'EOF'
test(ladder): cover LadderService rules

Tests cover ladder creation guards, desafiarClan preconditions
(chief/shaman, ≥5 members, no bans, ≤10 pending), aceptarDesafio,
and pause/resume phase transitions. Mocks injected via reflection;
no container required.

Co-Authored-By: Claude Opus 4.6 (1M context) <noreply@anthropic.com>
EOF
)"
```

---

## Task 6: TorneoServiceTest

**Files:**
- Create: `src/test/java/com/dotachile/torneos/service/TorneoServiceTest.java`

**In scope (per spec, narrowed by `com/dotachile/torneos/CLAUDE.md`):**
1. `crearTorneo` — null guards; `maxCantidadClanes >= 2`; `minCantidadClanes <= maxCantidadClanes`; `minCantidadClanes >= 2`.
2. `startTorneo` — count within `[min, max]`; no banned enrolled clans; transitions to `STARTED`; creates first `Ronda`.
3. `agregarPareo` — only allowed in `STARTED`; clan may not appear twice in a round; `bestOf` must be a positive odd integer.
4. `confirmarMatch` — only losing clan's chief/shaman (or admin); ELO via `EloSystem.calculoVariacion`; DOBLE_WO yields zero ELO change.
5. `finalizarTorneo` — all matches confirmed; declared champion is enrolled; sets `clanCampeon`; transitions to `FINISHED`.
6. `inscribirClanTorneo` — closed phase rejected; duplicate clan rejected.
7. `Torneo.getStandings` — fixed result list, assert ordering by (matches won desc, played desc, lost asc, games won desc, games lost asc, tag asc).

**Out of scope for Phase 1:** every other public method on `TorneoService`. Listed for Phase 2.

**Pre-implementation read:** Before writing tests, the implementer reads `TorneoService.java` end-to-end (it is 1816 lines). Goal: confirm the seven target methods are isolatable with mock-injected facades. **If any of them is too entangled to test cleanly** (e.g. it calls another `TorneoService` method that needs container state), drop it from Phase 1 and add a note to the Phase 2 spec (Task 9). Renegotiate scope BEFORE writing the test, not after.

- [ ] **Step 1: Read `TorneoService.java` and confirm scope**

Run: `wc -l src/java/com/dotachile/torneos/service/TorneoService.java` — sanity check (~1816 lines).
Read the file. For each of the seven target methods, locate the method body and verify it only calls injected facades and pure helpers. If any method depends on `Util.getUsuarioLogeado()` or other static `FacesContext` calls, that method is dropped from Phase 1 (recorded in Task 9's Phase 2 spec).

- [ ] **Step 2: Write the test class skeleton with reflection injection (same pattern as Task 5)**

```java
package com.dotachile.torneos.service;

import com.dotachile.auth.facade.UsuarioFacade;
import com.dotachile.clanes.facade.ClanBanFacade;
import com.dotachile.clanes.facade.ClanFacade;
import com.dotachile.comentarios.ComentarioFacade;
import com.dotachile.media.ReplayFacade;
import com.dotachile.torneos.facade.GameFacade;
import com.dotachile.torneos.facade.GameMatchFacade;
import com.dotachile.torneos.facade.ModificacionFacade;
import com.dotachile.torneos.facade.RondaFacade;
import com.dotachile.torneos.facade.TemporadaModificacionFacade;
import com.dotachile.torneos.facade.TorneoFacade;
import org.junit.jupiter.api.BeforeEach;

import javax.ejb.SessionContext;
import java.lang.reflect.Field;

import static org.mockito.Mockito.mock;

class TorneoServiceTest {

    private TorneoService service;
    private TorneoFacade torneoFac;
    private UsuarioFacade userFac;
    private ClanFacade clanFac;
    private RondaFacade rondaFac;
    private GameMatchFacade matchFac;
    private GameFacade gameFac;
    private TemporadaModificacionFacade tempFac;
    private ModificacionFacade modFac;
    private ReplayFacade replayFac;
    private ClanBanFacade clanBanFac;
    private ComentarioFacade comFac;
    private SessionContext ctx;

    @BeforeEach
    void setUp() throws Exception {
        service = new TorneoService();
        torneoFac = mock(TorneoFacade.class);
        userFac = mock(UsuarioFacade.class);
        clanFac = mock(ClanFacade.class);
        rondaFac = mock(RondaFacade.class);
        matchFac = mock(GameMatchFacade.class);
        gameFac = mock(GameFacade.class);
        tempFac = mock(TemporadaModificacionFacade.class);
        modFac = mock(ModificacionFacade.class);
        replayFac = mock(ReplayFacade.class);
        clanBanFac = mock(ClanBanFacade.class);
        comFac = mock(ComentarioFacade.class);
        ctx = mock(SessionContext.class);

        inject("torneoFac", torneoFac);
        inject("userFac", userFac);
        inject("clanFac", clanFac);
        inject("rondaFac", rondaFac);
        inject("matchFac", matchFac);
        inject("gameFac", gameFac);
        inject("tempFac", tempFac);
        inject("modFac", modFac);
        inject("replayFac", replayFac);
        inject("clanBanFac", clanBanFac);
        inject("comFac", comFac);
        inject("ctx", ctx);
    }

    private void inject(String fieldName, Object value) throws Exception {
        Field f = TorneoService.class.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(service, value);
    }
}
```

- [ ] **Step 3: Add `crearTorneo` guard tests (5 tests)**

Each as a `@Test` method on `TorneoServiceTest`. All should expect `BusinessLogicException`. Use the patterns:

  - **`crearTorneoThrowsWhenNombreIsNull`** — pass `null` for `nombre`.
  - **`crearTorneoThrowsWhenMaxClanesBelowTwo`** — `maxCantidadClanes = 1`.
  - **`crearTorneoThrowsWhenMinClanesBelowTwo`** — `minCantidadClanes = 1`.
  - **`crearTorneoThrowsWhenMinExceedsMax`** — `min=8, max=4`.
  - **`crearTorneoPersistsTorneoOnHappyPath`** — all valid; capture the persisted `Torneo`, assert phase = `REGISTRATION`.

Run: `mvn -o test -Dtest=TorneoServiceTest`
Expected: 5 tests pass.

- [ ] **Step 4: Add `startTorneo` tests (3 tests)**

  - **`startTorneoThrowsWhenEnrolledCountBelowMin`** — torneo with `min=4`, only 3 clans enrolled.
  - **`startTorneoThrowsWhenAnEnrolledClanIsBanned`** — stub `clanBanFac` to return a ban for one enrolled clan.
  - **`startTorneoTransitionsToStartedAndCreatesFirstRonda`** — happy path; verify torneo phase = `STARTED` and `rondaFac.create(any(Ronda.class))` called once.

- [ ] **Step 5: Add `agregarPareo` tests (3 tests)**

  - **`agregarPareoThrowsWhenTorneoNotInStartedPhase`** — torneo in `REGISTRATION`.
  - **`agregarPareoThrowsWhenSameClanAppearsTwice`** — pass the same clan as both sides.
  - **`agregarPareoThrowsWhenBestOfIsEvenOrZero`** — `bestOf = 2`, then `bestOf = 0`.

- [ ] **Step 6: Add `inscribirClanTorneo` tests (2 tests)**

  - **`inscribirClanTorneoRejectsWhenPhaseIsNotRegistration`** — torneo in `STARTED`.
  - **`inscribirClanTorneoRejectsDuplicateClan`** — clan already in `enrolledClanes`.

- [ ] **Step 7: Add `Torneo.getStandings` test (1 test)**

The standings logic lives on `Torneo` (per CLAUDE.md). Construct a `Torneo` directly, attach a fixed list of confirmed `GameMatch` instances with known `Resultado`s, call `getStandings()`, and assert the ordering matches the documented sort: matches won (desc), matches played (desc), matches lost (asc), games won (desc), games lost (asc), then tag (asc).

  - **`standingsAreOrderedByMatchesWonThenGamesWonThenTag`** — three clans with deliberately constructed records that exercise the multi-key sort.

- [ ] **Step 8: Add `confirmarMatch` and `finalizarTorneo` tests (1 each)**

  - **`confirmarMatchAppliesEloDeltaPerGameForNonWoResults`** — match with two non-WO games; verify the loser's clan rating dropped and the winner's rose by amounts consistent with `EloSystem.calculoVariacion` (just assert sign and equal-magnitude, since the exact value is `EloSystem`'s contract from Task 2).
  - **`finalizarTorneoSetsChampionAndTransitionsToFinished`** — torneo with all matches confirmed and a valid enrolled champion; verify `clanCampeon` set and phase = `FINISHED`.

- [ ] **Step 9: Run the full TorneoServiceTest**

Run: `mvn -o test -Dtest=TorneoServiceTest`
Expected: BUILD SUCCESS, "Tests run: 15, Failures: 0" (or fewer if any methods were dropped in Step 1's renegotiation — record dropped tests in the commit message).

- [ ] **Step 10: Commit**

```bash
git add src/test/java/com/dotachile/torneos/service/TorneoServiceTest.java
git commit -m "$(cat <<'EOF'
test(torneos): cover TorneoService lifecycle, standings, enrollment

Scoped Phase 1 tests on TorneoService: crearTorneo guards,
startTorneo phase transition, agregarPareo invariants,
inscribirClanTorneo guards, confirmarMatch ELO application,
finalizarTorneo champion declaration, plus Torneo.getStandings
multi-key sort. Out-of-scope methods deferred to Phase 2.

Co-Authored-By: Claude Opus 4.6 (1M context) <noreply@anthropic.com>
EOF
)"
```

---

## Task 7: ClanServiceTest

**Files:**
- Create: `src/test/java/com/dotachile/clanes/service/ClanServiceTest.java`

**In scope (per spec, narrowed by `com/dotachile/clanes/CLAUDE.md`):**
1. `crearClan` — only users with no clan; sets initial ELO 1000; `nombre` ≤ 50, `tag` ≤ 5.
2. `revivirClan` — only the last `chieftain` may revive; user must currently have no clan.
3. `invitarPlayer` — only chieftain or shaman of the inviting clan; inviter's clan must not be banned.
4. `aceptarInvitacion` — clan cap (12 from `web.xml`); blocked if active tournament + no open `TemporadaModificacion`; new joiner added to `peones`.
5. `kickearPlayer` — chieftain may kick anyone; shaman may kick peones/grunts but not other shamanes or the chieftain; blocked if banned.
6. `desarmarClan` — chieftain only remaining member AND no clan in `FaseTorneo.REGISTRATION`; blocked if banned.

**Pre-implementation read:** Read `ClanService.java`. Same rule as Task 6: if any target method needs `Util.getUsuarioLogeado()` or other `FacesContext`, drop it from Phase 1 and record in Task 9.

- [ ] **Step 1: Read `ClanService.java` and confirm scope**

Run: `wc -l src/java/com/dotachile/clanes/service/ClanService.java` (~754 lines).
Read it. Confirm the six target methods are isolatable. Drop any that aren't; record drops for Task 9.

- [ ] **Step 2: Write test skeleton with reflection injection**

```java
package com.dotachile.clanes.service;

import com.dotachile.auth.facade.UsuarioFacade;
import com.dotachile.clanes.facade.ClanBanFacade;
import com.dotachile.clanes.facade.ClanFacade;
import com.dotachile.clanes.facade.ConfirmacionFacade;
import com.dotachile.clanes.facade.MovimientoFacade;
import com.dotachile.torneos.facade.ModificacionFacade;
import com.dotachile.torneos.facade.TemporadaModificacionFacade;
import org.junit.jupiter.api.BeforeEach;

import javax.ejb.SessionContext;
import java.lang.reflect.Field;

import static org.mockito.Mockito.mock;

class ClanServiceTest {

    private ClanService service;
    private ClanFacade clanFac;
    private UsuarioFacade userFac;
    private TemporadaModificacionFacade tempFac;
    private ModificacionFacade modFac;
    private ConfirmacionFacade confirmacionFac;
    private ClanBanFacade clanBanFac;
    private MovimientoFacade movFac;
    private SessionContext ctx;

    @BeforeEach
    void setUp() throws Exception {
        service = new ClanService();
        clanFac = mock(ClanFacade.class);
        userFac = mock(UsuarioFacade.class);
        tempFac = mock(TemporadaModificacionFacade.class);
        modFac = mock(ModificacionFacade.class);
        confirmacionFac = mock(ConfirmacionFacade.class);
        clanBanFac = mock(ClanBanFacade.class);
        movFac = mock(MovimientoFacade.class);
        ctx = mock(SessionContext.class);

        inject("clanFac", clanFac);
        inject("userFac", userFac);
        inject("tempFac", tempFac);
        inject("modFac", modFac);
        inject("confirmacionFac", confirmacionFac);
        inject("clanBanFac", clanBanFac);
        inject("movFac", movFac);
        inject("ctx", ctx);
    }

    private void inject(String fieldName, Object value) throws Exception {
        Field f = ClanService.class.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(service, value);
    }
}
```

Field names taken from `ClanService.java` lines 47-53: `clanFac`, `userFac`, `tempFac`, `modFac`, `confirmacionFac`, `clanBanFac`, `movFac`, plus the `@Resource SessionContext ctx`. If a name differs in the actual file, adapt.

- [ ] **Step 3: Add `crearClan` tests (3 tests)**

  - **`crearClanThrowsWhenUserAlreadyHasAClan`** — user with non-null `clan`; expect rejection.
  - **`crearClanPersistsClanWithInitialEloOneThousand`** — happy path; capture persisted `Clan`, assert `elo == 1000`.
  - **`crearClanWritesMovimientoCreoClan`** — verify `movFac.create(...)` called with a `Movimiento` of type `CREO_CLAN`.

- [ ] **Step 4: Add `revivirClan` tests (2 tests)**

  - **`revivirClanThrowsWhenUserIsNotLastChieftain`** — clan exists, user is not the chieftain.
  - **`revivirClanThrowsWhenUserAlreadyHasAClan`** — user with non-null current clan.

- [ ] **Step 5: Add `invitarPlayer` tests (2 tests)**

  - **`invitarPlayerThrowsWhenInviterIsNotChieftainOrShaman`** — caller is a peon.
  - **`invitarPlayerThrowsWhenInviterClanIsBanned`** — `clanBanFac.findByClan(...)` returns a `ClanBan`.

- [ ] **Step 6: Add `aceptarInvitacion` tests (2 tests)**

  - **`aceptarInvitacionThrowsWhenClanRosterAtCap`** — clan with 12 integrantes; expect rejection.
  - **`aceptarInvitacionAddsNewMemberToPeones`** — clan with 5 integrantes, no active tournament; verify the persisted `Clan` has the new user in its `peones` list.

- [ ] **Step 7: Add `kickearPlayer` tests (2 tests)**

  - **`kickearPlayerAllowsShamanToKickPeon`** — caller is a shaman, target is a peon; verify removal.
  - **`kickearPlayerForbidsShamanFromKickingChieftain`** — caller is a shaman, target is the chieftain; expect rejection.

- [ ] **Step 8: Add `desarmarClan` test (1 test)**

  - **`desarmarClanThrowsWhenClanInRegistrationTournament`** — clan enrolled in a torneo with phase `REGISTRATION`; expect rejection.

- [ ] **Step 9: Run the full ClanServiceTest**

Run: `mvn -o test -Dtest=ClanServiceTest`
Expected: BUILD SUCCESS, "Tests run: 12, Failures: 0".

- [ ] **Step 10: Commit**

```bash
git add src/test/java/com/dotachile/clanes/service/ClanServiceTest.java
git commit -m "$(cat <<'EOF'
test(clanes): cover ClanService creation, revival, membership, bans

Scoped Phase 1 tests on ClanService: crearClan guards and initial
ELO, revivirClan last-chieftain rule, invitarPlayer authorization
and ban check, aceptarInvitacion roster cap and peones assignment,
kickearPlayer role hierarchy, desarmarClan REGISTRATION block.

Co-Authored-By: Claude Opus 4.6 (1M context) <noreply@anthropic.com>
EOF
)"
```

---

## Task 8: Run full suite and capture coverage baseline

**Files:** none (read-only)

- [ ] **Step 1: Run the full test suite offline**

Run: `mvn -o test`
Expected: BUILD SUCCESS. Tests run: ~75 (12 EloSystem + 8 PvpgnHash + 14 Util + 3 BusinessLogicException + 15 LadderService + 15 TorneoService + 12 ClanService = 79). Failures: 0.

Note: count may differ slightly if any service methods were dropped in Tasks 6/7 scope renegotiation.

- [ ] **Step 2: Generate the JaCoCo coverage report**

Run: `mvn -o verify`
Expected: BUILD SUCCESS. File exists: `target/site/jacoco/index.html`.

- [ ] **Step 3: Capture coverage percentages per package**

Open `target/site/jacoco/index.html` in a browser (or run `cat target/site/jacoco/jacoco.csv` if a CSV report is generated — bind it in the plugin config if not). Record line and branch coverage percentages for at minimum these packages:
- `com.dotachile.elo`
- `com.dotachile.shared`
- `com.dotachile.ladder.service`
- `com.dotachile.torneos.service`
- `com.dotachile.clanes.service`

These numbers go into the Phase 2 spec written in Task 9.

---

## Task 9: Phase 2 spec document

**Files:**
- Create: `docs/superpowers/specs/2026-04-06-testing-phase-2-design.md`

This is the deliverable that closes Phase 1: a written plan for the next session, grounded in the actual JaCoCo numbers from Task 8.

- [ ] **Step 1: Write the Phase 2 spec**

Use this skeleton, filled in with real numbers from Task 8 and real method names from any drops in Tasks 6 / 7:

```markdown
# Testing Initiative — Phase 2 Design

**Date:** 2026-04-06
**Status:** Awaiting user approval before implementation

## Phase 1 baseline (from JaCoCo report)

| Package | Line coverage | Branch coverage |
|---|---|---|
| com.dotachile.elo | __% | __% |
| com.dotachile.shared | __% | __% |
| com.dotachile.ladder.service | __% | __% |
| com.dotachile.torneos.service | __% | __% |
| com.dotachile.clanes.service | __% | __% |

## Methods explicitly dropped from Phase 1

(List any methods dropped during Tasks 6 / 7 scope renegotiation, with the reason.)

## Phase 2 goals

1. Push service-layer coverage above a per-package floor (proposed: 70% line, 50% branch).
2. Bring ManagedBeans into the coverage report by:
   - Removing the `**/*MB.class` exclusion from JaCoCo.
   - Either: (a) introducing a thin `FacesContext` test helper that mocks `FacesContext.getCurrentInstance()` for the duration of a test, or (b) extracting business logic out of MBs into testable collaborators where the MB is too tangled with JSF lifecycle.
3. Cover the Util JSF helpers (`addErrorMessage`, etc.) using the same `FacesContext` test helper.
4. Decide per package whether to test, exclude with rationale, or defer.

## Per-package plan

(For each remaining package, decide: test / exclude / defer.)

- `com.dotachile.auth.service` (AdminService, 247 LoC): TEST
- `com.dotachile.torneos.service.BasicService` (563 LoC): TEST
- `com.dotachile.encuestas.service` (176 LoC): TEST
- `com.dotachile.noticias.service` (158 LoC): TEST
- `com.dotachile.automation` (159 LoC, scheduled jobs): TEST — high value, isolated
- `com.dotachile.**.entity`: EXCLUDE — JPA boilerplate, no logic worth covering
- `com.dotachile.**.facade`: EXCLUDE — pure AbstractFacade passthroughs
- `com.dotachile.infrastructure.web` (filters, servlets): TEST — security-critical
- `com.dotachile.**.mb` / all MBs: TEST via FacesContext helper, prioritized by complexity
- (extend per the actual JaCoCo gaps)

## Out of scope for Phase 2

- Literal 100% coverage. The new policy is per-package floors, not a global percentage.
- Container/integration tests with a real Payara or Postgres.
- PowerMock under any circumstances.
```

- [ ] **Step 2: Commit Phase 2 spec**

```bash
git add docs/superpowers/specs/2026-04-06-testing-phase-2-design.md
git commit -m "$(cat <<'EOF'
docs(superpowers): record Phase 1 baseline and Phase 2 plan

Captures the JaCoCo coverage numbers produced at the end of
Phase 1 and lays out the per-package decisions for Phase 2,
including the FaceContext-helper approach for ManagedBeans.

Co-Authored-By: Claude Opus 4.6 (1M context) <noreply@anthropic.com>
EOF
)"
```

---

## Definition of done

- 8 commits total: infra, EloSystem, PvpgnHash, Util+BLE, LadderService, TorneoService, ClanService, Phase 2 spec.
- `mvn -o test` is green from a clean checkout (after one online seed).
- `mvn -o verify` produces `target/site/jacoco/index.html`.
- Phase 2 spec file exists with real numbers filled in.
- Production code is unmodified except for at most a handful of obviously-safe visibility tweaks, each justified in its own commit message.