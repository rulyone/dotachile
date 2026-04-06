# Per-feature CLAUDE.md Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add 10 small CLAUDE.md files (1 root + 9 per-feature) so each Claude Code session lazy-loads only the domain context for the features it touches.

**Architecture:** One CLAUDE.md per substantive feature in `src/java/com/dotachile/<feature>/`, plus a root `CLAUDE.md`. Each file follows a fixed 3-section template (Glossary, Rules, Entry points), targets ≤ 80 lines, uses Spanish for domain terms and English for prose, and is sourced by reading entities + services + controllers + git history. Lazy-loading is the native Claude Code mechanism — no `@import` or `.claude/rules` required.

**Tech Stack:** Markdown only. The codebase being documented is Java 8 / JSF 2 / EJB / JPA 2 / Maven / Payara 5; the plan does not modify any code.

**Spec:** `docs/superpowers/specs/2026-04-06-claude-md-per-feature-design.md`

---

## Conventions (read once before starting)

These rules apply to every feature CLAUDE.md and to the root CLAUDE.md. They are the project's CLAUDE.md style guide.

### Per-feature template

Every feature CLAUDE.md uses exactly this structure. Replace `<feature-name>` with the Spanish feature folder name (e.g. `torneos`).

```markdown
# <feature-name>

One-sentence purpose.

## Glossary

- **TerminoEspañol** — short English explanation. Class refs in code-style: `Clan`, `Confirmacion`.

## Rules

Business rules that are NOT obvious from reading the code. Phrased as positive imperatives.
- Always set `confirmado = false` when …
- A clan with zero `peones` becomes inactive on the next …
- The entity/method that enforces each rule is named in code-style.

## Entry points

The 2–4 classes a session most often starts from.
- `ClanService` — all clan mutations
- `VerClanMB` — clan profile read path

<!-- maintainer notes (stripped from context) go in HTML comments -->
```

### Writing rules

- **Imperative + concrete + verifiable.** Not "be careful with dates" but "Always normalize fechas to UTC at the service boundary."
- **Glossary terms in Spanish; prose in English.** Class/field names always in code formatting.
- **Do not restate anything derivable from code** (field lists, getter signatures, table names that match class names, etc.).
- **Positive phrasing.** "Always do Y" beats "Don't do X."
- **HTML comments for maintainer-only notes.** `<!-- TODO: confirm with Pablo -->` is invisible to Claude but visible to humans.
- **Hard cap: 80 lines per file.** If you need more, the file is doing too much — split or trim.

### Sourcing process (apply to every feature task)

1. List the entities in `src/java/com/dotachile/<feature>/entity/` — class names are glossary candidates; relationships and `@NamedQuery` strings reveal vocabulary.
2. Read the service in `src/java/com/dotachile/<feature>/service/` — verbs (mutations), guards (`BusinessLogicException` throws), and conditional logic become Rules.
3. List controllers in `src/java/com/dotachile/<feature>/controller/` — pick the 2–4 most useful starting points for a maintainer.
4. Run `git log --oneline -- src/java/com/dotachile/<feature>/` and the equivalent path before the refactor (e.g. `git log --oneline --follow -- src/java/model/services/ClanService.java` for ClanService) — past commit messages reveal non-obvious rules and historical bugs.
5. Draft the CLAUDE.md following the template. Glossary first (Spanish terms), Rules second (positive imperatives), Entry points last.
6. Anything you cannot confirm from the code goes inside an HTML comment so it is preserved for the human reviewer but not loaded into Claude's context.

### Verification (every task)

After writing each file:
- `wc -l <path/CLAUDE.md>` — must report ≤ 80.
- Visually skim that the three section headings exist in order: `## Glossary`, `## Rules`, `## Entry points`.
- `git status` should show exactly the new file (and nothing else).

---

## Task 1: comentarios

**Files:**
- Read: `src/java/com/dotachile/comentarios/Comentario.java`
- Read: `src/java/com/dotachile/comentarios/ComentarioFacade.java`
- Read: `src/java/com/dotachile/comentarios/ComentariosService.java`
- Create: `src/java/com/dotachile/comentarios/CLAUDE.md`

- [ ] **Step 1: Read source files**

Run: `wc -l src/java/com/dotachile/comentarios/*.java` and then read each with the Read tool. Note the entity fields, the service mutation methods, and what other features call into `ComentariosService` (this is a horizontal capability used by many features).

- [ ] **Step 2: Check git history for past fixes**

Run:
```bash
git log --oneline --follow -- src/java/com/dotachile/comentarios/ComentariosService.java
git log --oneline --follow -- src/java/com/dotachile/comentarios/Comentario.java
```
Look for fixes that hint at non-obvious rules (timezone, ownership, ordering).

- [ ] **Step 3: Draft `src/java/com/dotachile/comentarios/CLAUDE.md`**

Follow the per-feature template in Conventions. Suggested glossary entries to consider: `Comentario`, `autor`, `fechaComentario`. Suggested Rules: who owns a comment, how comments are attached to other entities (Clan, Torneo, Game, GameMatch, Ronda, Desafio — comments are a horizontal capability), whether comments are mutable, and any rule the service enforces about who can post.

- [ ] **Step 4: Verify line count**

Run: `wc -l src/java/com/dotachile/comentarios/CLAUDE.md`
Expected: a single number ≤ 80.

- [ ] **Step 5: Commit**

```bash
git add src/java/com/dotachile/comentarios/CLAUDE.md
git commit -m "docs(comentarios): add feature CLAUDE.md"
```

---

## Task 2: automation

**Files:**
- Read: `src/java/com/dotachile/automation/ServiciosAutomatizados.java`
- Read: `src/java/com/dotachile/automation/FunService.java`
- Create: `src/java/com/dotachile/automation/CLAUDE.md`

- [ ] **Step 1: Read source files**

Read both files with the Read tool. ServiciosAutomatizados is a `@Schedule` EJB — note every `@Schedule` annotation and what it does (this is the canonical surface for "what runs on a timer in this app"). FunService is the catch-all — note what it actually contains.

- [ ] **Step 2: Check git history**

```bash
git log --oneline --follow -- src/java/com/dotachile/automation/ServiciosAutomatizados.java
git log --oneline --follow -- src/java/com/dotachile/automation/FunService.java
```

- [ ] **Step 3: Draft `src/java/com/dotachile/automation/CLAUDE.md`**

Follow the template. Glossary: time-driven verbs from the schedule annotations. Rules: what each scheduled job does, when it runs (schedule string in code-style), what it touches across other features (this is the only place to discover that). Entry points: `ServiciosAutomatizados`, `FunService`.

If you discover that FunService is dead or near-dead code, say so explicitly in the file (positive form: "FunService is currently unused; do not add new business logic here.").

- [ ] **Step 4: Verify line count**

Run: `wc -l src/java/com/dotachile/automation/CLAUDE.md`
Expected: ≤ 80.

- [ ] **Step 5: Commit**

```bash
git add src/java/com/dotachile/automation/CLAUDE.md
git commit -m "docs(automation): add feature CLAUDE.md"
```

---

## Task 3: noticias

**Files:**
- Read: `src/java/com/dotachile/noticias/entity/Noticia.java`
- Read: `src/java/com/dotachile/noticias/entity/Categoria.java`
- Read: `src/java/com/dotachile/noticias/facade/NoticiaFacade.java`
- Read: `src/java/com/dotachile/noticias/facade/CategoriaFacade.java`
- Read: `src/java/com/dotachile/noticias/service/NoticiasService.java`
- Skim: `src/java/com/dotachile/noticias/controller/*.java` (6 files)
- Create: `src/java/com/dotachile/noticias/CLAUDE.md`

- [ ] **Step 1: Read entities and facades**

Read both entities. Note `@NamedQuery` strings (these reveal common access patterns and the Spanish vocabulary).

- [ ] **Step 2: Read NoticiasService**

Look for: who can publish, draft state vs. published state, category constraints, archival rules.

- [ ] **Step 3: Skim controllers, pick entry points**

Run: `ls src/java/com/dotachile/noticias/controller/` then Read 1–2 most likely candidates. Pick 2–4 controllers a maintainer would most often open first. `VerNoticiaMB`, `AgregarNoticiaMB`, `ArchivoNoticiasMB` are likely candidates — confirm.

- [ ] **Step 4: Check git history**

```bash
git log --oneline -- src/java/com/dotachile/noticias/
git log --oneline --follow -- src/java/com/dotachile/noticias/service/NoticiasService.java
```

- [ ] **Step 5: Draft `src/java/com/dotachile/noticias/CLAUDE.md`**

Follow the template. Glossary likely includes: `Noticia`, `Categoria`, `borrador`, `publicada`, `archivada`. Rules from the service. Entry points from step 3.

- [ ] **Step 6: Verify line count**

Run: `wc -l src/java/com/dotachile/noticias/CLAUDE.md`
Expected: ≤ 80.

- [ ] **Step 7: Commit**

```bash
git add src/java/com/dotachile/noticias/CLAUDE.md
git commit -m "docs(noticias): add feature CLAUDE.md"
```

---

## Task 4: encuestas

**Files:**
- Read: `src/java/com/dotachile/encuestas/entity/Encuesta.java`
- Read: `src/java/com/dotachile/encuestas/entity/OpcionEncuesta.java`
- Read: `src/java/com/dotachile/encuestas/facade/EncuestaFacade.java`
- Read: `src/java/com/dotachile/encuestas/facade/OpcionEncuestaFacade.java`
- Read: `src/java/com/dotachile/encuestas/service/EncuestaService.java`
- Skim: `src/java/com/dotachile/encuestas/controller/*.java` (2 files)
- Read: `src/java/com/dotachile/encuestas/converter/OpcionEncuestaConverter.java`
- Create: `src/java/com/dotachile/encuestas/CLAUDE.md`

- [ ] **Step 1: Read entities, facade, service**

Pay attention to: how votes are recorded (per-user uniqueness?), how an active encuesta is determined (date-based? flag?), and the relationship between Encuesta and OpcionEncuesta.

- [ ] **Step 2: Skim controllers**

Read both controllers (`CrearEncuesta`, `VerUltimaEncuestaMB`). The "última" pattern is a hint for a Rule about how the homepage finds the active encuesta.

- [ ] **Step 3: Check git history**

```bash
git log --oneline -- src/java/com/dotachile/encuestas/
```

- [ ] **Step 4: Draft `src/java/com/dotachile/encuestas/CLAUDE.md`**

Glossary likely includes: `Encuesta`, `OpcionEncuesta`, `voto`, `fechaInicio`, `fechaTermino`. Rules: one vote per user per encuesta, how the active encuesta is selected, lifecycle.

- [ ] **Step 5: Verify line count**

Run: `wc -l src/java/com/dotachile/encuestas/CLAUDE.md`
Expected: ≤ 80.

- [ ] **Step 6: Commit**

```bash
git add src/java/com/dotachile/encuestas/CLAUDE.md
git commit -m "docs(encuestas): add feature CLAUDE.md"
```

---

## Task 5: torneossingle

**Files:**
- Read: `src/java/com/dotachile/torneossingle/entity/TorneoSingle.java`
- Read: `src/java/com/dotachile/torneossingle/entity/RondaSingle.java`
- Read: `src/java/com/dotachile/torneossingle/entity/SingleMatch.java`
- Read: `src/java/com/dotachile/torneossingle/facade/TorneoSingleFacade.java`
- Read: `src/java/com/dotachile/torneossingle/facade/RondaSingleFacade.java`
- Read: `src/java/com/dotachile/torneossingle/facade/SingleMatchFacade.java`
- Read: `src/java/com/dotachile/torneossingle/service/SingleTorneoService.java`
- Create: `src/java/com/dotachile/torneossingle/CLAUDE.md`

- [ ] **Step 1: Read entities**

Note how TorneoSingle differs from the regular `Torneo` (player-based vs clan-based, single-elimination structure). The differences are the entire reason this feature exists separately — they ARE the glossary.

- [ ] **Step 2: Read service**

Look for the bracket-generation method, advancement rules, and how byes are handled.

- [ ] **Step 3: Check git history**

```bash
git log --oneline -- src/java/com/dotachile/torneossingle/
```

- [ ] **Step 4: Draft `src/java/com/dotachile/torneossingle/CLAUDE.md`**

Glossary: `TorneoSingle`, `RondaSingle`, `SingleMatch`, `bracket`, `bye`, `eliminado`. Rules: how brackets are seeded, when rondas advance, what closes a torneo. Entry points: `SingleTorneoService` and the dominant controller (note: there are no dedicated controllers for torneossingle in the controller tree — call this out in an HTML comment for the maintainer).

- [ ] **Step 5: Verify line count**

Run: `wc -l src/java/com/dotachile/torneossingle/CLAUDE.md`
Expected: ≤ 80.

- [ ] **Step 6: Commit**

```bash
git add src/java/com/dotachile/torneossingle/CLAUDE.md
git commit -m "docs(torneossingle): add feature CLAUDE.md"
```

---

## Task 6: ladder

**Files:**
- Read: `src/java/com/dotachile/ladder/entity/Ladder.java`
- Read: `src/java/com/dotachile/ladder/entity/FaseLadder.java`
- Read: `src/java/com/dotachile/ladder/entity/Desafio.java`
- Read: `src/java/com/dotachile/ladder/facade/LadderFacade.java`
- Read: `src/java/com/dotachile/ladder/facade/DesafioFacade.java`
- Read: `src/java/com/dotachile/ladder/service/LadderService.java`
- Skim: `src/java/com/dotachile/ladder/controller/*.java` (6 files)
- Create: `src/java/com/dotachile/ladder/CLAUDE.md`

- [ ] **Step 1: Read entities**

Ladder, FaseLadder, Desafio. Note `@NamedQuery` strings in Desafio (e.g. `Desafio.findDesafiosNoAceptados`) — those name the workflow states explicitly.

- [ ] **Step 2: Read LadderService**

This is a large service. Focus on: how a ladder is started/closed, how a desafío is created, how it advances through states (propuesto → aceptado → reportado → cerrado or similar), how ELO is recalculated (this calls `EloSystem.calculoVariacion`), and any rules about who can desafiar whom (rank distance? cooldown?).

- [ ] **Step 3: Skim controllers**

```bash
ls src/java/com/dotachile/ladder/controller/
```
Pick 2–4: likely `VerLadderMB`, `DesafiarClanMB`, `VerDesafioMB`, `ReportarGameLadderWizardMB`.

- [ ] **Step 4: Check git history**

```bash
git log --oneline -- src/java/com/dotachile/ladder/
git log --oneline --follow -- src/java/com/dotachile/ladder/service/LadderService.java
```
The recent commit `6dcc69d implementados comentarios en los desafios ladder` is a strong hint for a Rule.

- [ ] **Step 5: Draft `src/java/com/dotachile/ladder/CLAUDE.md`**

Glossary: `Ladder`, `FaseLadder`, `Desafio`, `desafiante`, `desafiado`, `factorK`, `cooldown`. Rules: state transitions, ELO trigger points, comment lifecycle. Entry points from step 3.

- [ ] **Step 6: Verify line count**

Run: `wc -l src/java/com/dotachile/ladder/CLAUDE.md`
Expected: ≤ 80.

- [ ] **Step 7: Commit**

```bash
git add src/java/com/dotachile/ladder/CLAUDE.md
git commit -m "docs(ladder): add feature CLAUDE.md"
```

---

## Task 7: clanes

**Files:**
- Read: `src/java/com/dotachile/clanes/entity/Clan.java`
- Read: `src/java/com/dotachile/clanes/entity/ClanBan.java`
- Read: `src/java/com/dotachile/clanes/entity/Confirmacion.java`
- Read: `src/java/com/dotachile/clanes/facade/ClanFacade.java`
- Read: `src/java/com/dotachile/clanes/facade/ClanBanFacade.java`
- Read: `src/java/com/dotachile/clanes/facade/ConfirmacionFacade.java`
- Read: `src/java/com/dotachile/clanes/service/ClanService.java`
- Read: `src/java/com/dotachile/clanes/helper/ComparacionClanes.java`
- Skim: `src/java/com/dotachile/clanes/controller/*.java` (12 files)
- Create: `src/java/com/dotachile/clanes/CLAUDE.md`

- [ ] **Step 1: Read entities**

Clan has the most complex relationships in the codebase. Note the role collections (`chieftain`, `shamanes`, `peones`, `grunts`), the tournament inscription join (`Confirmacion`), and the avatar/imagen relationship.

- [ ] **Step 2: Read ClanService**

Look for: lifecycle (crear, desarmar, revivir), role mutations (promover, demoter, kick, traspasar chieftain), invitation flow, ban policy, and any rule about minimum/maximum members.

- [ ] **Step 3: Read ComparacionClanes helper**

Tiny but important — it defines the canonical sort/compare for clans. Note what it sorts by (likely ELO).

- [ ] **Step 4: Skim controllers, pick entry points**

```bash
ls src/java/com/dotachile/clanes/controller/
```
12 controllers. Pick 2–4 maintainers would open first: likely `VerClanMB`, `CrearClanMB`, `InvitarPlayerMB`.

- [ ] **Step 5: Check git history**

```bash
git log --oneline -- src/java/com/dotachile/clanes/
git log --oneline --follow -- src/java/com/dotachile/clanes/service/ClanService.java
```
Look for chieftain/shamanes/peones-related commits — there are several recent ones in the pre-refactor history.

- [ ] **Step 6: Draft `src/java/com/dotachile/clanes/CLAUDE.md`**

Glossary: `Clan`, `chieftain`, `shamanes`, `peones`, `grunts`, `tag`, `desarmado`, `Confirmacion`, `ClanBan`. Rules: role hierarchy, who can promote whom, minimum members for tournament inscription, how `desarmar` and `revivir` work, what `Confirmacion` represents (clan accepts torneo invite). Entry points from step 4.

- [ ] **Step 7: Verify line count**

Run: `wc -l src/java/com/dotachile/clanes/CLAUDE.md`
Expected: ≤ 80.

- [ ] **Step 8: Commit**

```bash
git add src/java/com/dotachile/clanes/CLAUDE.md
git commit -m "docs(clanes): add feature CLAUDE.md"
```

---

## Task 8: auth

**Files:**
- Read: `src/java/com/dotachile/auth/entity/Usuario.java`
- Read: `src/java/com/dotachile/auth/entity/Perfil.java`
- Read: `src/java/com/dotachile/auth/entity/Grupo.java`
- Read: `src/java/com/dotachile/auth/entity/Ban.java`
- Read: `src/java/com/dotachile/auth/entity/BanHistory.java`
- Read: `src/java/com/dotachile/auth/entity/PreRegistro.java`
- Read: `src/java/com/dotachile/auth/facade/UsuarioFacade.java`
- Read: `src/java/com/dotachile/auth/facade/PerfilFacade.java`
- Read: `src/java/com/dotachile/auth/facade/GrupoFacade.java`
- Read: `src/java/com/dotachile/auth/facade/BanFacade.java`
- Read: `src/java/com/dotachile/auth/service/AdminService.java`
- Skim: `src/java/com/dotachile/auth/controller/*.java` (13 files)
- Read: `src/java/com/dotachile/shared/PvpgnHash.java`
- Read: `web/WEB-INF/web.xml` (security-role section, form-login-config)
- Create: `src/java/com/dotachile/auth/CLAUDE.md`

- [ ] **Step 1: Read entities**

Note: `Usuario` is the primary key holder for many tables (USERNAME is the FK in many joins). `Grupo` is the role/group entity. `Ban` and `BanHistory` are separate — current ban vs. audit log.

- [ ] **Step 2: Read AdminService**

This is the user/role admin surface. Look for: account creation flow, password hashing (calls `PvpgnHash` for the integration with the legacy PVP.GN game server), group assignment, ban issuance, pre-registration flow.

- [ ] **Step 3: Read PvpgnHash and the web.xml security section**

PvpgnHash is the password hashing function — note WHY it exists (compatibility with the external PVP.GN auth realm). web.xml lists the security roles: `ADMIN_ROOT`, `ADMIN_DOTA`, `ADMIN_TORNEO`, `ESCRITOR`, `MODERADOR`, `BANEADO`, `ADMIN_LADDER`. These are the canonical role names — they must appear in the glossary.

- [ ] **Step 4: Skim controllers, pick entry points**

```bash
ls src/java/com/dotachile/auth/controller/
```
13 controllers. Pick 2–4: likely `LoginMB`, `RegistrarseMB`, `VerPerfilMB`, `BanearUsuarioMB`.

- [ ] **Step 5: Check git history**

```bash
git log --oneline -- src/java/com/dotachile/auth/
git log --oneline --follow -- src/java/com/dotachile/auth/service/AdminService.java
git log --oneline --follow -- src/java/com/dotachile/shared/PvpgnHash.java
```

- [ ] **Step 6: Draft `src/java/com/dotachile/auth/CLAUDE.md`**

Glossary: `Usuario`, `Perfil`, `Grupo`, `Ban`, `BanHistory`, `PreRegistro`, plus all 7 security roles. Rules: password hashing scheme and why (PVP.GN compat), who can ban, how a ban affects login, the pre-registration flow, how `Grupo` controls access. Entry points from step 4.

- [ ] **Step 7: Verify line count**

Run: `wc -l src/java/com/dotachile/auth/CLAUDE.md`
Expected: ≤ 80. If you blow the budget, drop the security-role list to a single line ("see web.xml security-role declarations") and reference it.

- [ ] **Step 8: Commit**

```bash
git add src/java/com/dotachile/auth/CLAUDE.md
git commit -m "docs(auth): add feature CLAUDE.md"
```

---

## Task 9: torneos

**Files:**
- Read: `src/java/com/dotachile/torneos/entity/Torneo.java`
- Read: `src/java/com/dotachile/torneos/entity/Ronda.java`
- Read: `src/java/com/dotachile/torneos/entity/FaseTorneo.java`
- Read: `src/java/com/dotachile/torneos/entity/Game.java`
- Read: `src/java/com/dotachile/torneos/entity/GameMatch.java`
- Read: `src/java/com/dotachile/torneos/entity/Resultado.java`
- Read: `src/java/com/dotachile/torneos/entity/TipoTorneo.java`
- Read: `src/java/com/dotachile/torneos/entity/FactorK.java`
- Read: `src/java/com/dotachile/torneos/entity/TemporadaModificacion.java`
- Read: `src/java/com/dotachile/torneos/entity/Modificacion.java`
- Read: `src/java/com/dotachile/torneos/entity/TipoModificacion.java`
- Read: `src/java/com/dotachile/torneos/facade/TorneoFacade.java`
- Read: `src/java/com/dotachile/torneos/facade/RondaFacade.java`
- Read: `src/java/com/dotachile/torneos/facade/GameFacade.java`
- Read: `src/java/com/dotachile/torneos/facade/GameMatchFacade.java`
- Read: `src/java/com/dotachile/torneos/facade/ModificacionFacade.java`
- Read: `src/java/com/dotachile/torneos/facade/TemporadaModificacionFacade.java`
- Read: `src/java/com/dotachile/torneos/service/TorneoService.java`
- Read: `src/java/com/dotachile/torneos/service/BasicService.java`
- Read: `src/java/com/dotachile/torneos/helper/Standing.java`
- Skim: `src/java/com/dotachile/torneos/controller/*.java` (17 files)
- Create: `src/java/com/dotachile/torneos/CLAUDE.md`

- [ ] **Step 1: Read all 11 entities**

This is the largest feature. Focus on the relationships: `Torneo` 1—N `Ronda`, `Ronda` 1—N `GameMatch`, `GameMatch` 1—N `Game`, `Game` 1—1 `Resultado`. `FaseTorneo` enumerates lifecycle states (this is the source of the canonical state names — write them down). `TipoTorneo` enumerates tournament kinds. `TemporadaModificacion` + `Modificacion` + `TipoModificacion` are the rule-mod system (admins can adjust ELO mid-season).

- [ ] **Step 2: Read TorneoService and BasicService**

TorneoService is the largest service in the project. Focus on: tournament lifecycle (`crearTorneo`, `inscribirClan`, `iniciarTorneo`, `finalizarTorneo`), match reporting flow (this is where `ReportarGameWizardMB` writes), how `EloSystem.calculoVariacion` is invoked at match closure, the `pareo` (pairing) generation logic, and the `dobleEspia` mechanic if present. Note any timezone handling (the recent commit `aa7ad10 fixed desfase 3 horas` is a strong signal — find what changed and write a Rule about it).

BasicService is the lower-level helper. Note the boundary between BasicService and TorneoService.

- [ ] **Step 3: Read Standing helper**

Defines the canonical tournament standings calculation. Note what it sorts by and how ties are broken.

- [ ] **Step 4: Skim controllers, pick entry points**

```bash
ls src/java/com/dotachile/torneos/controller/
```
17 controllers. Pick 2–4: likely `VerTorneoMB`, `CrearTorneoMB`, `VerMatchMB`, `ReportarGameWizardMB`.

- [ ] **Step 5: Check git history**

```bash
git log --oneline -- src/java/com/dotachile/torneos/
git log --oneline --follow -- src/java/com/dotachile/torneos/service/TorneoService.java
git log --oneline --follow -- src/java/com/dotachile/torneos/entity/GameMatch.java
```
Specifically read the message of `aa7ad10 fixed desfase 3 horas en sistema de propuesta de fechas` — that bug history becomes a Rule.

- [ ] **Step 6: Draft `src/java/com/dotachile/torneos/CLAUDE.md`**

Glossary: `Torneo`, `Ronda`, `FaseTorneo` (with the actual phase enum values), `Game`, `GameMatch`, `Resultado`, `pareo`, `dobleEspia`, `TipoTorneo` values, `TemporadaModificacion`, `Modificacion`, `Standing`. Rules: lifecycle order, match-reporting requirements, ELO trigger, timezone normalization (from the desfase fix), pairing constraints (`AgregarPareoMB`, `EliminarPareoMB`, `ModificarBestOfMB`), modificación scope. Entry points from step 4.

This file is the most likely to blow the 80-line budget. If you do, drop FaseTorneo enum values to a one-line "see `FaseTorneo` source" reference and trim the Glossary aggressively.

- [ ] **Step 7: Verify line count**

Run: `wc -l src/java/com/dotachile/torneos/CLAUDE.md`
Expected: ≤ 80.

- [ ] **Step 8: Commit**

```bash
git add src/java/com/dotachile/torneos/CLAUDE.md
git commit -m "docs(torneos): add feature CLAUDE.md"
```

---

## Task 10: root CLAUDE.md

**Files:**
- Read: `pom.xml`
- Read: `Dockerfile`
- Read: `docker-compose.yml`
- Read: `dev-sync.sh`
- Read: `web/WEB-INF/web.xml`
- Read: `web/WEB-INF/faces-config.xml`
- Read: All 9 feature CLAUDE.md files written in tasks 1–9 (for cross-referencing the cross-cutting glossary)
- Create: `CLAUDE.md` (project root)

- [ ] **Step 1: Read build and deployment files**

Confirm the exact commands. From the recent refactor session we know:
- `mvn -o package` → `target/DotaCL.war`
- `docker compose up -d --build` → full rebuild
- `./dev-sync.sh java` → hot Java sync (does NOT sync descriptors)
- `./dev-sync.sh views` → hot XHTML/CSS sync

Verify these by reading the files.

- [ ] **Step 2: Re-read the 9 feature CLAUDE.md files**

Quickly skim each feature CLAUDE.md to identify cross-cutting glossary terms — terms that show up in multiple features and therefore deserve a top-level entry instead of being repeated.

- [ ] **Step 3: Draft `CLAUDE.md` (project root)**

Five sections, ≤ 80 lines total:

1. **Stack** — single short paragraph: Java 8, JSF 2 + PrimeFaces 4, EJB Stateless, JPA 2 (EclipseLink), Maven WAR, Payara 5 in Docker.
2. **Build & deploy** — exact commands, one per line, with what each produces.
3. **Layout map** — terse: `src/java/com/dotachile/<feature>/{controller,entity,facade,service,...}` and `web/web/<feature>/`. Name the cross-cutting modules: `shared`, `infrastructure.web`, `comentarios`, `media`, `elo`, `automation`.
4. **Cross-cutting glossary** — top-level Spanish terms used by every feature: `Usuario`, `Clan`, `Torneo`, `Ladder`, `Comentario`, `Confirmacion`, `Perfil`, `Grupo`. One line each.
5. **Spanish-language rule** — single bullet, exactly: "Always keep package, class, entity, view, and database names in Spanish to match the existing codebase. Do not propose English renames."

- [ ] **Step 4: Verify line count**

Run: `wc -l CLAUDE.md`
Expected: ≤ 80.

- [ ] **Step 5: Commit**

```bash
git add CLAUDE.md
git commit -m "docs(root): add project CLAUDE.md"
```

---

## Task 11: Aggregate verification

**Files:**
- Read: every CLAUDE.md created in tasks 1–10

- [ ] **Step 1: Verify all 10 files exist and are within budget**

Run:
```bash
wc -l CLAUDE.md src/java/com/dotachile/*/CLAUDE.md
```
Expected output: 11 lines (10 files + total). Every individual count must be ≤ 80.

- [ ] **Step 2: Verify the section structure of every feature file**

Run:
```bash
for f in src/java/com/dotachile/*/CLAUDE.md; do
  echo "=== $f ==="
  grep -E '^## ' "$f"
done
```
Expected: each feature file shows exactly three lines: `## Glossary`, `## Rules`, `## Entry points` — in that order.

- [ ] **Step 3: Verify no contradictions across files**

Run:
```bash
grep -rn "Spanish\|English" CLAUDE.md src/java/com/dotachile/*/CLAUDE.md
```
Verify only the root file mentions the Spanish-language rule. Feature files should not restate it.

- [ ] **Step 4: Verify exactly 10 CLAUDE.md files exist**

Run:
```bash
ls CLAUDE.md src/java/com/dotachile/*/CLAUDE.md 2>/dev/null | wc -l
```
Expected output: `10` (whitespace-padded is fine).

Then verify each is tracked:
```bash
git ls-files CLAUDE.md 'src/java/com/dotachile/*/CLAUDE.md' | wc -l
```
Expected: `10`.

- [ ] **Step 5: No final commit needed**

All 10 commits already exist (one per task). This task is verification only — if any check fails, return to the failing feature task and amend.

---

## Out of scope (do not implement in this plan)

- Twin CLAUDE.md files under `web/web/<feature>/` (`@import` shims). Possible future addition.
- `.claude/rules/*.md` with `paths:` frontmatter. Considered and rejected in the spec.
- CLAUDE.md files for the 8 deliberately-skipped tiny features (`videos`, `seleccion`, `buscador`, `inhouse`, `elo`, `media`, `shared`, `infrastructure/web`).
- Coding-style or formatting rules.
- Per-class or per-method documentation (belongs in javadoc, not CLAUDE.md).
