# Password Confirmation on Registration — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a "repetir password" field to the registration form so users can't accidentally create an account with a CapsLock-mangled password.

**Architecture:** Form-layer change only. The XHTML view gets a second `<p:password>` with PrimeFaces' built-in `match` attribute (client-side inline feedback). The `RegistrarseMB` JSF managed bean gets a `passwordConfirm` field and a pure `passwordsMatch()` helper, with `register()` short-circuiting via the existing `Util.addErrorMessage` flow before calling `BasicService.register`. No service, entity, or schema changes. The pure helper is unit-tested in isolation; the rest is verified manually because the project has no FacesContext mocking infrastructure.

**Tech Stack:** JSF 2 + PrimeFaces 4 (`<p:password match="...">`), Java 8, JUnit 5, AssertJ.

**Spec:** `docs/superpowers/specs/2026-04-08-password-confirmation-registro-design.md`

---

## File map

- **Modify** `src/java/com/dotachile/auth/controller/RegistrarseMB.java` — add `passwordConfirm` field/getter/setter, `passwordsMatch()` helper, mismatch guard in `register()`, and reset in success path.
- **Modify** `web/web/registro/Registrarse.xhtml` — replace `<h:inputSecret>` for the password with `<p:password feedback="false">`, add a second `<p:password match="password" feedback="false">` row labeled "Repetir password:".
- **Create** `src/test/java/com/dotachile/auth/controller/RegistrarseMBTest.java` — JUnit 5 + AssertJ tests for `passwordsMatch()`.

No changes to: `BasicService`, `PreRegistroFacade`, `PreRegistro`, `Usuario`, DB schema, `auth/CLAUDE.md`.

---

## Task 1: Unit-test the `passwordsMatch()` helper (TDD: failing test)

**Files:**
- Create: `src/test/java/com/dotachile/auth/controller/RegistrarseMBTest.java`

- [ ] **Step 1: Write the failing test file**

Create `src/test/java/com/dotachile/auth/controller/RegistrarseMBTest.java` with these exact contents:

```java
package com.dotachile.auth.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrarseMBTest {

    @Test
    void passwordsMatchReturnsTrueWhenBothFieldsEqual() {
        RegistrarseMB mb = new RegistrarseMB();
        mb.setPassword("hunter2");
        mb.setPasswordConfirm("hunter2");
        assertThat(mb.passwordsMatch()).isTrue();
    }

    @Test
    void passwordsMatchReturnsFalseWhenFieldsDifferByCase() {
        // The actual CapsLock case the feature exists to catch.
        RegistrarseMB mb = new RegistrarseMB();
        mb.setPassword("hunter2");
        mb.setPasswordConfirm("Hunter2");
        assertThat(mb.passwordsMatch()).isFalse();
    }

    @Test
    void passwordsMatchReturnsFalseWhenConfirmIsNull() {
        RegistrarseMB mb = new RegistrarseMB();
        mb.setPassword("hunter2");
        assertThat(mb.passwordsMatch()).isFalse();
    }

    @Test
    void passwordsMatchReturnsFalseWhenPasswordIsNull() {
        RegistrarseMB mb = new RegistrarseMB();
        mb.setPasswordConfirm("hunter2");
        assertThat(mb.passwordsMatch()).isFalse();
    }

    @Test
    void passwordsMatchReturnsFalseWhenBothNull() {
        RegistrarseMB mb = new RegistrarseMB();
        assertThat(mb.passwordsMatch()).isFalse();
    }
}
```

- [ ] **Step 2: Run the test to verify it fails to compile**

Run: `mvn -o test -Dtest=RegistrarseMBTest`

Expected: **compilation failure** — `setPasswordConfirm` and `passwordsMatch` are not yet defined on `RegistrarseMB`. This is the failing-test state for TDD; we move on to implementation.

- [ ] **Step 3: Commit the failing test**

```bash
git add src/test/java/com/dotachile/auth/controller/RegistrarseMBTest.java
git commit -m "test(auth): add passwordsMatch helper tests for RegistrarseMB"
```

---

## Task 2: Add `passwordConfirm` field, helper, and guard to `RegistrarseMB`

**Files:**
- Modify: `src/java/com/dotachile/auth/controller/RegistrarseMB.java`

- [ ] **Step 1: Add the `passwordConfirm` field**

In `src/java/com/dotachile/auth/controller/RegistrarseMB.java`, find:

```java
    private String username;
    private String password;
    private String email;
```

Replace with:

```java
    private String username;
    private String password;
    private String passwordConfirm;
    private String email;
```

- [ ] **Step 2: Add getter and setter for `passwordConfirm`**

In the same file, find the existing `password` getter/setter:

```java
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
```

Immediately after the `setPassword` method, add:

```java
    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    boolean passwordsMatch() {
        return password != null && password.equals(passwordConfirm);
    }
```

(`passwordsMatch()` is package-private on purpose so the unit test in the same package can reach it without exposing it as part of the bean's public JSF surface.)

- [ ] **Step 3: Guard `register()` against mismatch**

In the same file, find the `register` method:

```java
    public void register(ActionEvent e) {
        try {
            basicService.register(username, password, email);
            String detalle = "Debes revisar tu correo para poder activar y utilizar tu cuenta en nuestros servicios, "
                    + "las cuentas NO activadas son eliminadas todos los dias a las 5am automáticamente. RECUERDA REVISAR TU CORREO SPAM.";
            Util.addInfoMessage("Cuenta creada satisfactoriamente.", detalle);
            this.username = null;
            this.password = null;
            this.email = null;
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage(ex.getMessage(), null);
        }
    }
```

Replace with:

```java
    public void register(ActionEvent e) {
        if (!passwordsMatch()) {
            Util.addErrorMessage("Las passwords no coinciden.", null);
            return;
        }
        try {
            basicService.register(username, password, email);
            String detalle = "Debes revisar tu correo para poder activar y utilizar tu cuenta en nuestros servicios, "
                    + "las cuentas NO activadas son eliminadas todos los dias a las 5am automáticamente. RECUERDA REVISAR TU CORREO SPAM.";
            Util.addInfoMessage("Cuenta creada satisfactoriamente.", detalle);
            this.username = null;
            this.password = null;
            this.passwordConfirm = null;
            this.email = null;
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage(ex.getMessage(), null);
        }
    }
```

Two changes: the mismatch guard at the top, and `this.passwordConfirm = null;` added to the success-path reset block.

- [ ] **Step 4: Run the unit tests and verify they pass**

Run: `mvn -o test -Dtest=RegistrarseMBTest`

Expected: **5 tests, 0 failures, 0 errors.** Output should include `Tests run: 5, Failures: 0, Errors: 0, Skipped: 0`.

If a test fails, re-read the diff against the snippets above — most likely `passwordsMatch()` was not added or `passwordConfirm` was not declared.

- [ ] **Step 5: Run the full test suite to confirm no regressions**

Run: `mvn -o test`

Expected: **BUILD SUCCESS**, all existing tests still pass. The set should now include `RegistrarseMBTest` alongside the existing `UtilTest`, `PvpgnHashTest`, `EloSystemTest`, `ClanServiceTest`, `LadderServiceTest`, `TorneoServiceTest`, `BusinessLogicExceptionTest`.

- [ ] **Step 6: Commit**

```bash
git add src/java/com/dotachile/auth/controller/RegistrarseMB.java
git commit -m "feat(auth): add passwordConfirm field and mismatch guard to RegistrarseMB"
```

---

## Task 3: Add the second password input to the registration form

**Files:**
- Modify: `web/web/registro/Registrarse.xhtml`

- [ ] **Step 1: Replace the password input and add the confirm input**

In `web/web/registro/Registrarse.xhtml`, find:

```xml
                            <h:outputLabel value="Password:" />
                            <h:inputSecret id="password" value="#{registrarseMB.password}" required="true">
                                <f:validateLength minimum="6" />
                            </h:inputSecret>
```

Replace with:

```xml
                            <h:outputLabel value="Password:" />
                            <p:password id="password" value="#{registrarseMB.password}" feedback="false" required="true">
                                <f:validateLength minimum="6" />
                            </p:password>

                            <h:outputLabel value="Repetir password:" />
                            <p:password id="passwordConfirm" value="#{registrarseMB.passwordConfirm}" match="password" feedback="false" required="true">
                                <f:validateLength minimum="6" />
                            </p:password>
```

Notes for the engineer:
- `feedback="false"` disables PrimeFaces' password-strength popup. We only want the equality check, not the strength meter.
- `match="password"` references the sibling `<p:password>` by its `id` (PrimeFaces 4 resolves it within the same form). It produces an inline mismatch message and blocks submit on most browsers.
- The `<p:password>` namespace prefix `p` is already declared on the root `<html>` element (`xmlns:p="http://primefaces.org/ui"` — line 5 of the same file). No new xmlns declaration needed.
- We keep `<f:validateLength minimum="6" />` on both fields so a too-short confirmation is reported the same way as a too-short password.

- [ ] **Step 2: Build the WAR and push the view**

Run: `./dev-sync.sh views`

Expected: the script copies XHTML/CSS/images into the running container without a redeploy. Confirm no errors are printed. (If you don't have the container running yet, run `docker compose up -d --build` first.)

- [ ] **Step 3: Manually verify in the browser**

1. Open `/registro/Registrarse.xhtml` while logged out.
2. Enter `username`, `email`, password `hunter2`, confirm `Hunter2`.
3. Tab out of the confirm field. **Expected:** PrimeFaces shows an inline "Passwords do not match" message on the confirm field.
4. Click Confirmar. **Expected:** server-side guard fires too — message banner shows `"Las passwords no coinciden."`.
5. Change confirm to `hunter2`. Click Confirmar. **Expected:** account is created, the existing success message appears: `"Cuenta creada satisfactoriamente."`.
6. Try a too-short matching pair (`abc` / `abc`). **Expected:** the existing length validator fires on both fields.

If any of these don't behave as expected, do not commit — diagnose first.

- [ ] **Step 4: Commit**

```bash
git add web/web/registro/Registrarse.xhtml
git commit -m "feat(auth): require password confirmation in registration form"
```

---

## Self-review against the spec

- **Spec §Changes 1 (XHTML)** → Task 3.
- **Spec §Changes 2 (RegistrarseMB)** → Task 2 (field, helper, guard, reset).
- **Spec §Changes 3 (untouched)** → no task needed; nothing to do.
- **Spec §Error handling** → covered by Task 2 (server guard) and Task 3 step 3 (manual verification of client + server + length validator).
- **Spec §Unit test** → Tasks 1 + 2 (TDD: failing test first, then implementation flips it green).
- **Spec §Manual verification** → Task 3 step 3.

No placeholders, no TBDs, no "similar to" cross-references. Method names match across tasks (`passwordsMatch`, `setPasswordConfirm`, `getPasswordConfirm`). The XHTML id `passwordConfirm` matches the bean property name and the `match="password"` reference.