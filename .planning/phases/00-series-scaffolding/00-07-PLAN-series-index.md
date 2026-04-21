---
phase: 00-series-scaffolding
plan: 07
type: execute
wave: 2
depends_on:
  - "00-01"
  - "00-02"
  - "00-03"
  - "00-04"
  - "00-05"
  - "00-06"
files_modified:
  - docs/presentations/README.md
autonomous: true
requirements:
  - SCAF-01
tags:
  - documentation
  - index
  - spanish
user_setup: []

must_haves:
  truths:
    - "Un developer que clone el repo en session-01-pre puede abrir docs/presentations/README.md y ver las 9 sesiones listadas en orden con fecha, slug, estado, abstract, link al folder y tags pre/post"
    - "El README está en español (audience-facing)"
    - "La tabla tiene exactamente 9 filas numeradas 01..09, zero-padded"
    - "El status column usa solo valores válidos: pending | rehearsed | delivered"
    - "El README linkea a los artefactos hermanos que plans 01-06 produjeron: SETUP.md, QUAL-GATES.md, CONCERNS-MAPPING.md, THEME.md, GLOSSARY/, templates"
  artifacts:
    - path: "docs/presentations/README.md"
      provides: "Índice audience-facing de las 9 sesiones en español"
      contains: "# Serie AI-SWE 2026"
  key_links:
    - from: "docs/presentations/README.md Tabla de sesiones"
      to: "Session folder slugs (not yet created — placeholders for now)"
      via: "9 rows linking to ./YYYY-MM-DD-NN-<slug>/"
      pattern: "session-0[1-9]-pre"
    - from: "docs/presentations/README.md Pre-requisitos"
      to: "docs/presentations/SETUP.md (plan 00-04)"
      via: "relative link"
      pattern: "SETUP.md"
    - from: "docs/presentations/README.md Artefactos del arco"
      to: "docs/presentations/{QUAL-GATES.md, CONCERNS-MAPPING.md, THEME.md, GLOSSARY/, MANIFEST.template.md, HANDOUT.template.md, REHEARSAL.template.md}"
      via: "relative links"
      pattern: "QUAL-GATES|CONCERNS-MAPPING|THEME|GLOSSARY|\\.template\\.md"
---

<objective>
Author the Spanish audience-facing series index at `docs/presentations/README.md`, listing all 9 sessions in arc order with date, slug, status, abstract, folder link, and pre/post tags. Satisfies ROADMAP.md Phase 0 Success Criterion #1: "a new developer cloning at `session-01-pre` can open this file and see all 9 sessions in order".

**Why Wave 2:** This plan summarizes the outputs of plans 01-06. It links to `SETUP.md` (plan 04), `QUAL-GATES.md` + templates (plan 05), `THEME.md` + extended CLAUDE.md (plan 06), `CONCERNS-MAPPING.md` (plan 02), and `GLOSSARY/` (plan 03). Running it in Wave 1 would leave dangling references; running it in Wave 2 lets the links resolve.

**Purpose (SCAF-01):** Single entry point for a new developer or a returning presenter. Claude's-Discretion default per CONTEXT: Spanish audience-facing index with status column (pending/rehearsed/delivered). NOT a presenter dashboard — STATE.md remains authoritative for planning state.

**Output:** One new file — `docs/presentations/README.md` — replacing no prior content (the root `./README.md` is separate and stays 6 lines).
</objective>

<execution_context>
@/Users/pmartinez/Documents/git/quantumentangled/dotachile/.claude/get-shit-done/workflows/execute-plan.md
@/Users/pmartinez/Documents/git/quantumentangled/dotachile/.claude/get-shit-done/templates/summary.md
</execution_context>

<context>
@.planning/PROJECT.md
@.planning/ROADMAP.md
@.planning/STATE.md
@.planning/phases/00-series-scaffolding/00-CONTEXT.md
@.planning/phases/00-series-scaffolding/00-RESEARCH.md
@.planning/phases/00-series-scaffolding/00-PATTERNS.md
@.planning/phases/00-series-scaffolding/00-VALIDATION.md
@.planning/REQUIREMENTS.md
@./CLAUDE.md
</context>

<tasks>

<task type="auto" tdd="false">
  <name>Task 1: Author docs/presentations/README.md — Spanish audience-facing index of 9 sessions</name>
  <files>docs/presentations/README.md</files>
  <read_first>
    - .planning/ROADMAP.md §"Phases" (the 9 session titles + goals — source of abstracts)
    - .planning/REQUIREMENTS.md §S01..S09 (session-specific requirements — source of accurate 1-liner abstracts)
    - .planning/phases/00-series-scaffolding/00-CONTEXT.md §"Claude's Discretion → Series README.md purpose" (Spanish audience-facing default)
    - .planning/phases/00-series-scaffolding/00-RESEARCH.md §"Plan-Splitting Recommendation" (this plan is the follower; plans 01-06 outputs are the inputs)
    - .planning/phases/00-series-scaffolding/00-PATTERNS.md §"docs/presentations/README.md (series index — greenfield)" — exact table schema
    - .planning/phases/00-series-scaffolding/00-VALIDATION.md (SCAF-01 rows — 9 row check, Spanish keywords check, status column valid values)
  </read_first>
  <action>
Create a new file at `docs/presentations/README.md`. Spanish prose audience-facing — the first thing someone sees when they open `docs/presentations/`. Do NOT confuse with the root `./README.md` (6 lines, separate file, not modified here).

**Write this file verbatim:**

```markdown
# Serie AI-SWE 2026 — DotaChile

Workshop en vivo de desarrollo asistido por IA sobre la codebase legacy de DotaChile
(Java 8, JSF 2, PrimeFaces 4, EJB 3, JPA 2, Payara 5, PostgreSQL 15).

**9 sesiones, 1 hora cada una, todo en `master`, demos reales con commits reviewables.**

Cada sesión agrega una primitiva o concepto sobre lo anterior. La secuencia honra la
dependencia: Demo (S1) → Teoría (S2) → RAG (S3) → MCP (S4) → Skills (S5) →
Agents (S6) → Hooks (S7) → Slash Commands (S8) → Capstone (S9).

## Pre-requisitos

Antes de cualquier sesión, seguí la guía de setup:

- [`SETUP.md`](SETUP.md) — Docker Compose + Payara + Postgres + Claude Code CLI + Marp/Mermaid + Ollama + corpus email-rag (opcional).

## Convenciones

- **Idioma:** deck y narración en español; la salida de Claude Code se queda en inglés (estado actual del ecosistema — ver `QUAL-GATES.md §QUAL-06`).
- **Formato de folder:** `YYYY-MM-DD-NN-<slug>/` (NN zero-padded, 01..09). Ver `CLAUDE.md §Convención de numeración NN-<slug>`.
- **Tags:** cada sesión commitea `session-NN-pre` (estado antes del demo) y `session-NN-post` (estado después). Un clone en cualquiera de estos tags reproduce el demo sin mágia.
- **Sidecars:** cada sesión contiene `MANIFEST.md` (mapa slide→commit), `HANDOUT.md` (takeaway audiencia) y `REHEARSAL.md` (bitácora del dry-run). Templates en esta misma carpeta.

## Sesiones

| # | Fecha | Slug | Estado | Abstract | Folder | Tags |
|---|-------|------|--------|----------|--------|------|
| 01 | [Replace: YYYY-MM-DD] | demo-primero | pending | Hook-first end-to-end live bug fix sobre archivo legacy real + glosario de primitivas + preview del arco. | [Replace: ./YYYY-MM-DD-01-demo-primero/](./) | `session-01-pre` / `session-01-post` |
| 02 | [Replace: YYYY-MM-DD] | intro-contexto-llms | pending | Mecánica del context window, evolución de LLMs (Nov 2022 → hoy), scripts deterministas asistidos por LLM; por qué existen Skills y subagents. | [Replace: ./YYYY-MM-DD-02-intro-contexto-llms/](./) | `session-02-pre` / `session-02-post` |
| 03 | [Replace: YYYY-MM-DD] | rag | pending | Demo en vivo de `tools/email-rag/` contra el corpus español redactado; RAG enseñado por subtracción; origen de la cadena de composición cross-session. | [Replace: ./YYYY-MM-DD-03-rag/](./) | `session-03-pre` / `session-03-post` |
| 04 | [Replace: YYYY-MM-DD] | mcp | pending | Wire de MCP server local (Postgres) contra la DB de DotaChile; tool-call JSON on-screen; "RAG mental model pero para acciones". | [Replace: ./YYYY-MM-DD-04-mcp/](./) | `session-04-pre` / `session-04-post` |
| 05 | [Replace: YYYY-MM-DD] | skills | pending | Autoría en vivo de un Skill DotaChile-específico (`escape-false-guard` o `pvpgn-hash-safety`); composición con `user-story` skill pre-instalado. | [Replace: ./YYYY-MM-DD-05-skills/](./) | `session-05-pre` / `session-05-post` |
| 06 | [Replace: YYYY-MM-DD] | agents | pending | Spawn de subagente para investigar la god-class `TorneoService.java` (1800 LOC); evidencia del context-refreshing; mención del bug-13898 como honesty beat. | [Replace: ./YYYY-MM-DD-06-agents/](./) | `session-06-pre` / `session-06-post` |
| 07 | [Replace: YYYY-MM-DD] | hooks | pending | PostToolUse `mvn -o compile` + PreToolUse bloqueando folders off-limits; capa determinística alrededor del LLM estocástico. | [Replace: ./YYYY-MM-DD-07-hooks/](./) | `session-07-pre` / `session-07-post` |
| 08 | [Replace: YYYY-MM-DD] | slash-commands | pending | Autoría en vivo de `/dota-audit-xss`; composición con slash commands existentes de GSD; resolución Command vs Skill. | [Replace: ./YYYY-MM-DD-08-slash-commands/](./) | `session-08-pre` / `session-08-post` |
| 09 | [Replace: YYYY-MM-DD] | capstone | pending | Comparación GSD / Superpowers / Spec-Kit-or-newer; walkthrough en vivo del sistema seleccionado; composición de todas las primitivas; validación de reproducibilidad post-sesión. | [Replace: ./YYYY-MM-DD-09-capstone/](./) | `session-09-pre` / `session-09-post` |

**Estado (leyenda):** `pending` = planificada; `rehearsed` = rehearsal completo, lista para delivery; `delivered` = entregada en vivo.

Las fechas y los folder paths `[Replace: ...]` se llenan al momento de cada sesión plan-phase
(cuando se fija la fecha de delivery). Los slugs y abstracts están lockeados por ROADMAP.md.

## Artefactos del arco (Phase 0)

Documentación de referencia compartida por las 9 sesiones:

- [`SETUP.md`](SETUP.md) — guía end-to-end de instalación de toolchain (Spanish, layered quick-start + apéndice).
- [`QUAL-GATES.md`](QUAL-GATES.md) — las 12 reglas QUAL (QUAL-01..QUAL-12); templates linkean por ID.
- [`CONCERNS-MAPPING.md`](CONCERNS-MAPPING.md) — master table de slices de `.planning/codebase/CONCERNS.md` → sesiones (claimed / candidate / deferred).
- [`THEME.md`](THEME.md) — frontmatter Marp canónico + variación dark.
- [`CLAUDE.md`](CLAUDE.md) — convenciones de autoría (NN-infix, sidecars, git tags, fallback, embed del glosario).
- [`GLOSSARY/`](GLOSSARY/) — glosario Marp con una slide canónica por primitiva + 6 diagramas Mermaid pre-renderizados (CURR-03 single source of truth).
- [`MANIFEST.template.md`](MANIFEST.template.md) — template placeholder-annotado para el sidecar de cada sesión.
- [`HANDOUT.template.md`](HANDOUT.template.md) — template con 5 secciones fijas para el audience takeaway.
- [`REHEARSAL.template.md`](REHEARSAL.template.md) — template con checklist QUAL + notas libres para el dry-run.

## Decks pre-serie (material crudo, NO parte del arco)

Los decks `2026-04-08-mas-alla-del-hype/` y `2026-04-10-ai-driven-development/` son
material pre-series — no están numerados y no siguen la convención NN-infix. Quedan
como referencia histórica / raw material; no reemplazan a ninguna sesión del arco.

## Cómo reproducir cualquier demo

Para reproducir el demo de la Session NN:

```bash
# Clone el repo al estado pre-demo
git clone https://github.com/[Replace: org]/dotachile
cd dotachile
git checkout session-NN-pre

# Seguí SETUP.md si no tenés el toolchain
# Leé el MANIFEST.md de la sesión para el mapa slide→commit
cat docs/presentations/YYYY-MM-DD-NN-<slug>/MANIFEST.md

# Para ver el resultado final del demo
git checkout session-NN-post
git diff session-NN-pre session-NN-post
```

El `HANDOUT.md` de cada sesión nombra los comandos específicos a correr.
```

**Constraints (per CONTEXT + RESEARCH + PATTERNS + VALIDATION):**
- **Spanish audience-facing** per Claude's-Discretion default (CONTEXT).
- **Exactly 9 rows** in the Sesiones table, zero-padded `01`..`09`. VALIDATION.md SCAF-01 row: `test $(grep -c "^\| 0[1-9] " docs/presentations/README.md) -eq 9`.
- **Status column values MUST be** `pending | rehearsed | delivered` (from VALIDATION.md SCAF-01 row: `grep -E "^\| 0[1-9] \| .* \| .* \| (pending\|rehearsed\|delivered) \|"` must return ≥ 9).
- **Spanish keywords present** for VALIDATION.md SCAF-01 row 3: `grep -q "Próxima\|Sesión\|Fecha\|Estado"`. Table header uses `Fecha`, `Estado`, `Sesiones` heading covers `Sesión`; the table row with session 8 and session 9 reference `próxima` implicitly — add an explicit "próxima sesión" or "sesión próxima" mention if not auto-present. Current template has `## Sesiones` and `Fecha`/`Estado` column headers — verify all 4 grep-alternates match.
- **Slugs locked:** `demo-primero`, `intro-contexto-llms`, `rag`, `mcp`, `skills`, `agents`, `hooks`, `slash-commands`, `capstone` — derived from ROADMAP.md phase titles.
- **Fechas as `[Replace: YYYY-MM-DD]` placeholder** — per-session plan-phase fills in. Manual sync is an accepted cost per CONTEXT Deferred Ideas.
- **Link to sibling artifacts** — SETUP.md, QUAL-GATES.md, CONCERNS-MAPPING.md, THEME.md, CLAUDE.md, GLOSSARY/, 3 templates. All relative paths (all siblings at `docs/presentations/`).
- **Pre-series decks acknowledged** — explicit "NO parte del arco" note prevents future confusion (Open Question 4 resolution).
- **Reproducibility recipe** — the bottom "Cómo reproducir" section makes CURR-04 visible at arc level, not just at per-session level.
  </action>
  <verify>
    <automated>test -f docs/presentations/README.md && test $(grep -c "^| 0[1-9] " docs/presentations/README.md) -eq 9 && grep -q "Próxima\|Sesión\|Fecha\|Estado" docs/presentations/README.md</automated>
  </verify>
  <acceptance_criteria>
    - `test -f docs/presentations/README.md` succeeds
    - VALIDATION.md SCAF-01 row 1: `test $(grep -c "^| 0[1-9] " docs/presentations/README.md) -eq 9` succeeds (exactly 9 rows)
    - VALIDATION.md SCAF-01 row 2: `grep -cE "^\| 0[1-9] \| .* \| .* \| (pending\|rehearsed\|delivered) \|" docs/presentations/README.md` returns `9` (all status values valid)
    - VALIDATION.md SCAF-01 row 3 Spanish: `grep -q "Próxima\|Sesión\|Fecha\|Estado" docs/presentations/README.md` succeeds
    - Zero-padded numbers only: `grep -cE "^\| [0-9]\b" docs/presentations/README.md` returns `0` (no single-digit row numbers without leading zero)
    - All 9 expected slugs present: `for s in demo-primero intro-contexto-llms rag mcp skills agents hooks slash-commands capstone; do grep -q "$s" docs/presentations/README.md || echo "MISSING: $s"; done` returns empty
    - Sibling artifact links present: `for f in SETUP.md QUAL-GATES.md CONCERNS-MAPPING.md THEME.md MANIFEST.template.md HANDOUT.template.md REHEARSAL.template.md GLOSSARY/; do grep -q "$f" docs/presentations/README.md || echo "MISSING: $f"; done` returns empty
    - Pre-serie acknowledgment: `grep -q "pre-series\|pre-serie\|material crudo\|NO parte del arco" docs/presentations/README.md` succeeds
    - Tags column present for all 9 rows: `grep -c "session-0[1-9]-pre" docs/presentations/README.md` returns `9`
    - Reproducibility recipe (CURR-04 surface): `grep -q "Cómo reproducir\|session-NN-pre\|git checkout" docs/presentations/README.md` succeeds
  </acceptance_criteria>
  <done>
    `docs/presentations/README.md` exists as a 9-row Spanish audience-facing index with valid status column values, sibling artifact links, pre-serie acknowledgment, and a reproducibility recipe. All VALIDATION.md SCAF-01 rows green.
  </done>
</task>

</tasks>

<threat_model>
No security-sensitive surface — docs-only artifact. README is a static index; it does not introduce code or runtime behavior.
</threat_model>

<verification>
Run all VALIDATION.md SCAF-01 rows:

```bash
test -f docs/presentations/README.md
test $(grep -c "^| 0[1-9] " docs/presentations/README.md) -eq 9
grep -cE "^\| 0[1-9] \| .* \| .* \| (pending\|rehearsed\|delivered) \|" docs/presentations/README.md   # = 9
grep -q "Próxima\|Sesión\|Fecha\|Estado" docs/presentations/README.md
```

All must pass.

**Cross-plan sanity check (because this is Wave 2 and depends on Wave 1):**

```bash
# Every sibling artifact link in README must resolve to an existing file
for f in SETUP.md QUAL-GATES.md CONCERNS-MAPPING.md THEME.md CLAUDE.md \
         MANIFEST.template.md HANDOUT.template.md REHEARSAL.template.md GLOSSARY/GLOSSARY.md; do
  test -e "docs/presentations/$f" || echo "DANGLING LINK: $f"
done
```

All sibling files must exist. If anything is dangling, plans 01-06 didn't complete — do NOT ship this plan until they're green.
</verification>

<success_criteria>
ROADMAP.md Phase 0 Success Criterion #1 satisfied: a new developer cloning at `session-01-pre` opens `docs/presentations/README.md` and sees all 9 sessions listed in order with date, slug, status, abstract, link to session folder, and session-NN-pre/post tags. Phase 0 complete — Session 1 authoring can begin.
</success_criteria>

<output>
After completion, create `.planning/phases/00-series-scaffolding/00-07-SUMMARY.md` recording:
- Row count in the Sesiones table (expected: 9)
- All 9 slug values verified present
- All sibling artifact links verified (no dangling)
- Status column values used (expected: all `pending` since no session delivered yet)
</output>
