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
- **Tags:** cada sesión commitea `session-NN-pre` (estado antes del demo) y `session-NN-post` (estado después). Un clone en cualquiera de estos tags reproduce el demo sin magia.
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

Próxima sesión: la primera en estado `pending` con fecha asignada (ver columna `Fecha`).

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
