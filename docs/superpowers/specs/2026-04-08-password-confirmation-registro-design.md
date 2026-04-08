# Password confirmation field on registration

## Goal

Prevent users from creating accounts with a typo'd password (CapsLock,
fat-finger) by requiring them to enter it twice on the registration form.

## Scope

- In scope: the registration form (`Registrarse.xhtml` + `RegistrarseMB`).
- Out of scope: `CambiarPasswordMB` (password change), login, password reset.

## Changes

### 1. `web/web/registro/Registrarse.xhtml`

- Replace the existing `<h:inputSecret id="password">` with
  `<p:password id="password" feedback="false" required="true">`, keeping
  `<f:validateLength minimum="6" />`.
- Add a new row below it:
  `<p:password id="passwordConfirm" value="#{registrarseMB.passwordConfirm}"
   feedback="false" required="true">` with the same length validator and
  label `"Repetir password:"`.
- **No client-side `match` validator.** The original design called for
  PrimeFaces' `<p:password match="password">` to provide inline mismatch
  feedback, but PF4's `Password.validate()` compares against the sibling
  field's *model value*, not its *submitted value*. The match check runs
  during JSF Process Validations, before Update Model Values, so on a
  `@RequestScoped` bean the sibling's `getValue()` is always `null` and
  the validator rejects every submission. Mismatch is detected by the
  server-side `passwordsMatch()` guard described in section 2.

### 2. `RegistrarseMB.java`

- Add field + getter/setter:
  ```java
  private String passwordConfirm;
  ```
- Add a package-private pure helper:
  ```java
  boolean passwordsMatch() {
      return password != null && password.equals(passwordConfirm);
  }
  ```
- In `register(ActionEvent)`, before calling `basicService.register(...)`:
  ```java
  if (!passwordsMatch()) {
      Util.addErrorMessage("Las passwords no coinciden.", null);
      return;
  }
  ```
- In the success path, reset `passwordConfirm = null;` alongside the other
  fields.

### 3. Untouched

- `BasicService.register`, `PreRegistro` entity, DB schema, `auth/CLAUDE.md`
  rules. The confirmation field is purely a controller-layer concern; the
  service still receives a single password.

## Error handling

- **Mismatch:** `register()` rejects via `Util.addErrorMessage`, the same
  flow used by every other registration error today. The existing form
  already renders messages; no new component required. (See section 1 for
  why no client-side validator.)
- **Empty confirm field:** caught by `required="true"` on the `<p:password>`,
  same as the existing password field.

## Unit test

### Constraint

Existing tests instantiate beans/services directly and mock collaborators
(see `ClanServiceTest`). `RegistrarseMB.register()` calls
`Util.addErrorMessage`, which dereferences `FacesContext.getCurrentInstance()`
and would NPE under plain JUnit. The project pins `mockito-core` (not
`mockito-inline`), so static mocking of `FacesContext` is not available.

### Approach

Test the pure `passwordsMatch()` helper directly, the same way `UtilTest`
exercises pure functions in `Util`. The helper carries the entire decision
the feature adds; `register()` just routes on its result.

### File

`src/test/java/com/dotachile/auth/controller/RegistrarseMBTest.java`

JUnit 5 + AssertJ, no mocks. Test cases:

1. `passwordsMatchReturnsTrueWhenBothFieldsEqual` — both `"hunter2"`, expect
   `true`.
2. `passwordsMatchReturnsFalseWhenFieldsDiffer` — `"hunter2"` vs `"Hunter2"`
   (the actual CapsLock case the feature exists to catch), expect `false`.
3. `passwordsMatchReturnsFalseWhenConfirmIsNull` — `password` only, expect
   `false`.
4. `passwordsMatchReturnsFalseWhenPasswordIsNull` — `passwordConfirm` only,
   expect `false`.
5. `passwordsMatchReturnsFalseWhenBothNull` — fresh bean, expect `false`.
   (`required="true"` should prevent this on the form, but the server-side
   guard still rejects it.)

### What is intentionally not tested

`register()` end-to-end, because it requires a `FacesContext` and the
project has no infrastructure for mocking it. Adding `mockito-inline` for a
single method is not justified at this scope.

## Manual verification

1. Build & deploy: `./dev-sync.sh all`.
2. Open `/registro/Registrarse.xhtml` while logged out.
3. Enter mismatched passwords and submit → see server-side
   `"Las passwords no coinciden."`.
4. Enter matching passwords → account created, activation email sent
   (existing flow).