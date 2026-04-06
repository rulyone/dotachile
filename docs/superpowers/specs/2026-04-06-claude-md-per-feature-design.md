# Per-feature CLAUDE.md files for dotachile

**Status:** Approved
**Date:** 2026-04-06

## Context

The dotachile codebase has no CLAUDE.md file today. After the recent feature-based refactor, the Java tree is organized as `src/java/com/dotachile/<feature>/{controller,entity,facade,service,...}` for ~16 feature modules. We want to give Claude Code sessions cheap, locally-loaded domain context so that:

- A session that touches `torneos` automatically gets the torneo glossary and business rules without paying token cost for unrelated features (auth, ladder, etc.).
- A session that opens any file gets a small root CLAUDE.md with stack/build/layout basics.
- Adding context costs near-zero per session because each file stays small (≤80 lines).

This works because Claude Code walks parent directories at startup but **lazy-loads nested CLAUDE.md files** only when it reads files in those subdirectories during a session. Source: https://code.claude.com/docs/en/memory.md

## Goal

Author 10 CLAUDE.md files (1 root + 9 per-feature) following Anthropic's official best practices for CLAUDE.md content (≤200 lines target, imperative + concrete, evergreen reference, no code-derivable restatement, positive phrasing). Glossary terms remain in Spanish to match the codebase; explanatory prose is in English.

## File list

| # | Path | Purpose |
|---|---|---|
| 1 | `CLAUDE.md` | Project root: stack, build/deploy, layout map, cross-cutting glossary, Spanish-language rule |
| 2 | `src/java/com/dotachile/auth/CLAUDE.md` | users, registro, login, perfiles, bans |
| 3 | `src/java/com/dotachile/clanes/CLAUDE.md` | clan lifecycle, roles, confirmaciones |
| 4 | `src/java/com/dotachile/torneos/CLAUDE.md` | torneos, rondas, fases, games, matches, pareos |
| 5 | `src/java/com/dotachile/ladder/CLAUDE.md` | ladder, desafíos, factor K |
| 6 | `src/java/com/dotachile/noticias/CLAUDE.md` | noticias, categorías |
| 7 | `src/java/com/dotachile/encuestas/CLAUDE.md` | encuestas, opciones, votos |
| 8 | `src/java/com/dotachile/comentarios/CLAUDE.md` | comentarios cross-feature |
| 9 | `src/java/com/dotachile/automation/CLAUDE.md` | scheduled tasks, FunService |
| 10 | `src/java/com/dotachile/torneossingle/CLAUDE.md` | single-elimination tournaments |

**Deliberately skipped:** `videos`, `seleccion`, `buscador`, `inhouse`, `elo`, `media`, `shared`, `infrastructure/web`. Each has either a single class or no domain rules worth documenting. Add later if they grow.

## Per-feature template

Every feature file uses exactly this structure. Target ≤80 lines.

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

**Writing rules (apply to every file):**

- Imperative + concrete + verifiable. Not "be careful with dates" but "Always normalize fechas to UTC at the service boundary."
- Glossary terms in Spanish; prose in English.
- No restatement of anything derivable from code (field lists, getters, etc.).
- Positive phrasing ("Always do Y") over prohibitions ("Don't do X").
- Use HTML comments `<!-- ... -->` for maintainer-only notes; they are stripped from Claude's context.
- ≤80 lines per file. Hard cap.

## Root CLAUDE.md content

Single file at the project root, also ≤80 lines. Sections:

1. **Stack** — one paragraph: Java 8, JSF 2 + PrimeFaces 4, EJB Stateless, JPA 2 (EclipseLink), Maven WAR, Payara 5.
2. **Build & deploy** — exact commands:
   - `mvn -o package` — produces `target/DotaCL.war`
   - `docker compose up -d --build` — full stack rebuild and deploy
   - `./dev-sync.sh java` — hot-reload Java classes (descriptors not synced)
   - `./dev-sync.sh views` — hot-sync XHTML/CSS
3. **Layout map** — terse diagram of `src/java/com/dotachile/<feature>/{controller,entity,facade,service,converter,helper}` and `web/web/<feature>/`. Names the cross-cutting modules (`shared`, `infrastructure.web`, `comentarios`, `media`, `elo`, `automation`).
4. **Cross-cutting glossary** — top-level Spanish terms used by every feature: `Usuario`, `Clan`, `Torneo`, `Ladder`, `Comentario`, `Confirmacion`, `Perfil`, `Grupo`.
5. **Spanish-language rule** — single bullet: "Always keep package, class, entity, view, and database names in Spanish to match the existing codebase. Do not propose English renames."

## Sourcing process (per feature)

For each of the 9 feature files, in one pass:

1. Read entities in `com/dotachile/<feature>/entity/` for class names, fields, `@NamedQuery` strings, and inline comments → glossary candidates and relationships.
2. Read the service in `<feature>/service/` for the verbs — what mutations exist, what guards them, what throws `BusinessLogicException` → Rules candidates.
3. Skim controllers in `<feature>/controller/` for the most-used 2–4 entry-point MBs → Entry points.
4. `git log --oneline -- <feature-path>` for past fixes that hint at non-obvious rules (e.g. the 3-hour timezone fix, null-render guards).
5. Write the file using the template, ≤80 lines, Spanish glossary terms, English prose, positive imperatives.
6. Anything unverifiable goes in an HTML comment `<!-- TODO: confirm with Pablo -->` (invisible to Claude, visible to humans).

## Authoring order

Smallest features first so the pattern is proven before the big ones:

1. `comentarios` (3 classes)
2. `automation` (2 classes)
3. `noticias` (10 classes)
4. `encuestas` (8 classes)
5. `torneossingle` (7 classes)
6. `ladder` (12 classes)
7. `clanes` (24 classes)
8. `auth` (28 classes)
9. `torneos` (40 classes)
10. Root `CLAUDE.md` last (it references all of them)

## Verification

After all files are written:

- Every file is ≤80 lines: `wc -l CLAUDE.md src/java/com/dotachile/*/CLAUDE.md`
- `git status` shows exactly 10 new files, no other changes.
- Markdown rendered sanity-check: open one file in the editor; bullets resolve, headings render.

No deploy or test step is needed — these are documentation files that change no runtime behavior.

## Out of scope

- Twin CLAUDE.md files under `web/web/<feature>/` for view-only sessions. Can be added later as `@import` shims if needed.
- `.claude/rules/*.md` with `paths` frontmatter — alternative mechanism considered and rejected for discoverability reasons (rule files live far from the code they document).
- Filling in CLAUDE.md for the 8 skipped features. Add only when one of them grows enough business logic to be worth documenting.
- Coding style / formatting rules. The codebase has a NetBeans-derived style and no enforced formatter; not worth restating.
- Any per-class or per-method documentation. CLAUDE.md is feature-level only; per-class doc belongs in javadoc.
