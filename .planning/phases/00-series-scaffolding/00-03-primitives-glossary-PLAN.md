---
phase: 00-series-scaffolding
plan: 03
type: execute
wave: 1
depends_on: []
files_modified:
  - docs/presentations/GLOSSARY/GLOSSARY.md
  - docs/presentations/GLOSSARY/GLOSSARY.html
  - docs/presentations/GLOSSARY/rag.mmd
  - docs/presentations/GLOSSARY/rag.svg
  - docs/presentations/GLOSSARY/mcp.mmd
  - docs/presentations/GLOSSARY/mcp.svg
  - docs/presentations/GLOSSARY/skill.mmd
  - docs/presentations/GLOSSARY/skill.svg
  - docs/presentations/GLOSSARY/agent.mmd
  - docs/presentations/GLOSSARY/agent.svg
  - docs/presentations/GLOSSARY/hook.mmd
  - docs/presentations/GLOSSARY/hook.svg
  - docs/presentations/GLOSSARY/command.mmd
  - docs/presentations/GLOSSARY/command.svg
autonomous: true
requirements:
  - SCAF-07
tags:
  - marp
  - mermaid
  - glossary
  - primitives

must_haves:
  truths:
    - "docs/presentations/GLOSSARY/ contains one Marp deck (.md + rendered .html) + 6 .mmd Mermaid sources + 6 pre-rendered .svg — 14 files total"
    - "GLOSSARY.md has exactly one canonical slide per primitive: RAG, MCP, Skill, Agent, Hook, Slash Command (6 H1 headings)"
    - "GLOSSARY.md frontmatter sets paginate: false (reference doc, not a linear arc)"
    - "All slide text + speaker notes are Spanish; all Mermaid node labels are Spanish"
    - "Diagrams show data + control flow (not decorative) — each satisfies the Session 1 S01-03 gate as-is via reference-only embed"
    - "Sessions 1–9 will embed ../GLOSSARY/<primitive>.svg + link Ver GLOSSARY.html §<Primitive> — zero text duplication per D-10"
  artifacts:
    - path: "docs/presentations/GLOSSARY/GLOSSARY.md"
      provides: "Marp source with 6 primitive slides — the CURR-03 single source of truth"
      contains: "# RAG"
    - path: "docs/presentations/GLOSSARY/GLOSSARY.html"
      provides: "Rendered deck committed alongside .md (render-on-commit convention)"
    - path: "docs/presentations/GLOSSARY/rag.mmd"
      provides: "Canonical RAG data+control flow diagram"
      contains: "graph LR"
    - path: "docs/presentations/GLOSSARY/rag.svg"
      provides: "Pre-rendered SVG (HTMLPreview-safe per existing docs/presentations/CLAUDE.md convention)"
  key_links:
    - from: "docs/presentations/GLOSSARY/GLOSSARY.md"
      to: "docs/presentations/GLOSSARY/{rag,mcp,skill,agent,hook,command}.svg"
      via: "relative path image embeds"
      pattern: "!\\[.*\\]\\(\\./.*\\.svg\\)"
    - from: "Future Session 1-9 decks"
      to: "docs/presentations/GLOSSARY/*.svg + GLOSSARY.html"
      via: "D-10 reference-only embed convention (documented by plan 06 in CLAUDE.md extension)"
      pattern: "../GLOSSARY/"
---

<objective>
Author the canonical primitives glossary — a standalone Marp deck with one slide per primitive (RAG, MCP, Skill, Agent, Hook, Slash Command), each paired with a dedicated Mermaid data+control-flow diagram pre-rendered to SVG. This file set is the **CURR-03 single source of truth**: every session Phase 1-9 will reference these canonical `.svg` diagrams and link to GLOSSARY.html for spoken definitions, never duplicating text.

**Purpose (SCAF-07 + CURR-03 + D-09..D-12):** The reference-only embed pattern (D-10) makes definitional drift structurally impossible — there is one text (GLOSSARY.md) and one image per primitive (`.svg`), and session authors point at them rather than copy. Session 1's S01-02 ("¿Qué acabamos de hacer?" decomposition) + S01-03 (explainer diagram per primitive) are satisfied by referencing these canonical artifacts.

**Output:** 14 new files in a new directory `docs/presentations/GLOSSARY/`:
- 1 Marp deck source (`.md`) + 1 rendered HTML (`.html`)
- 6 Mermaid source files (`.mmd`) + 6 pre-rendered SVGs (`.svg`)

Authored Spanish-first. No English. D-11 mandates these exist BEFORE Session 1 is authored.
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
@docs/presentations/CLAUDE.md
@docs/presentations/2026-04-10-ai-driven-development/2026-04-10-ai-driven-development.md
@docs/presentations/2026-04-10-ai-driven-development/2026-04-10-ai-driven-development-ciclo.mmd
@./CLAUDE.md
</context>

<tasks>

<task type="auto" tdd="false">
  <name>Task 1: Author GLOSSARY.md + 6 Mermaid source files (.mmd) with Spanish labels</name>
  <files>docs/presentations/GLOSSARY/GLOSSARY.md, docs/presentations/GLOSSARY/rag.mmd, docs/presentations/GLOSSARY/mcp.mmd, docs/presentations/GLOSSARY/skill.mmd, docs/presentations/GLOSSARY/agent.mmd, docs/presentations/GLOSSARY/hook.mmd, docs/presentations/GLOSSARY/command.mmd</files>
  <read_first>
    - docs/presentations/2026-04-10-ai-driven-development/2026-04-10-ai-driven-development.md (lines 1-10 frontmatter, lines 22-27 speaker-note convention, lines 66-80 RAG analog, 140-148 MCP analog, 156-164 Skills analog)
    - docs/presentations/2026-04-10-ai-driven-development/2026-04-10-ai-driven-development-ciclo.mmd (the ONLY .mmd in the repo — graph LR idiom)
    - docs/presentations/CLAUDE.md (existing Marp + Mermaid conventions)
    - .planning/phases/00-series-scaffolding/00-CONTEXT.md §D-09..D-12 (glossary structure rules)
    - .planning/phases/00-series-scaffolding/00-RESEARCH.md §"Primitives Glossary Fragment (SCAF-07 + CURR-03)" — slide structure template + Pitfall 9 (paginate: false) + per-primitive definition lifts
    - .planning/phases/00-series-scaffolding/00-PATTERNS.md §"docs/presentations/GLOSSARY/GLOSSARY.md" + §"docs/presentations/GLOSSARY/{rag,mcp,skill,agent,hook,command}.mmd" (6 diagrams) — per-primitive flow sketches
  </read_first>
  <action>
    Create `docs/presentations/GLOSSARY/` directory if it does not exist, then author 7 files.

    ### File 1: `docs/presentations/GLOSSARY/GLOSSARY.md`

    Write the Marp deck with frontmatter + 6 primitive slides. Each slide follows the per-primitive template from PATTERNS.md §"GLOSSARY.md per-primitive slide pattern".

    **Frontmatter (verbatim — adapted from analog lines 1-10 with paginate: false override per Pitfall 9):**

    ```yaml
    ---
    marp: true
    theme: gaia
    class: lead
    paginate: false
    backgroundColor: "#fff"
    color: "#1e1e2e"
    title: "Glosario de primitivas — Serie AI-SWE 2026"
    author: "Pablo Martínez"
    ---
    ```

    **Title slide (first):**

    ```markdown
    <!-- _class: lead -->

    # Glosario de primitivas
    ## Serie AI-SWE 2026 — DotaChile

    Seis primitivas, una slide canónica cada una.

    Esta doc es **referencia**. Las sesiones la embeben con `![<primitive>](./<primitive>.svg)`
    y linkean con `Ver GLOSSARY.html §<Primitive>` — nunca copian el texto (CURR-03).

    <!--
    Presentador: slide de entrada. Nadie presenta este deck completo en vivo —
    sirve como HTML de referencia que carga en una tab durante las sesiones.
    Las definiciones están en español; los IDs (RAG, MCP, etc.) se quedan en inglés
    porque son términos de arte estables del ecosistema Claude Code.
    -->

    ---
    ```

    **Primitive 1 — RAG slide (definition lifted + rephrased from analog lines 66-73):**

    ```markdown
    # RAG

    **Concepto en 1 línea:** Retrieval Augmented Generation — darle a Claude contexto que NO está en el código, vía búsqueda híbrida (léxica + semántica) sobre un corpus externo.

    **Qué hace:**
    - Indexa un corpus (emails, docs, tickets, etc.) con BM25 + embeddings semánticos
    - Recibe una query; devuelve los top-k chunks más relevantes
    - Inyecta esos chunks al contexto de Claude antes de que responda
    - Claude solo ve los pedazos recuperados, no el corpus completo

    ![RAG](./rag.svg)

    > RAG no es fine-tuning ni memoria infinita — es búsqueda inteligente + un LLM que sabe leer los resultados.

    <!--
    Presentador: RAG se confunde con fine-tuning o con "IA con memoria infinita".
    Ninguna de las dos. RAG = búsqueda + inyección al contexto. El ejemplo
    vivo del arco es tools/email-rag/search.py (Session 3).
    Slide referenciado por: Sessions 1, 3 (primary), 4, 5, 6.
    -->

    ---
    ```

    **Primitive 2 — MCP slide (definition lifted from analog lines 140-148):**

    ```markdown
    # MCP

    **Concepto en 1 línea:** Model Context Protocol — el estándar abierto para que las IA invoquen **herramientas externas** (APIs, DBs, file systems) como si fueran funciones.

    **Qué hace:**
    - Un servidor MCP expone "tools" (ej. `postgres_query`, `plane_create_ticket`)
    - Claude los llama con JSON estructurado; el servidor los ejecuta; devuelve el resultado
    - El resultado vuelve al contexto de Claude como input para su siguiente paso
    - Spec abierto — hay decenas de servidores MCP comunitarios

    ![MCP](./mcp.svg)

    > Sin MCP, Claude dice "abre Plane y crea un ticket". Con MCP, lo crea él.

    <!--
    Presentador: MCP es el "cómo" de las acciones. Si RAG es "darle a Claude
    conocimiento", MCP es "darle a Claude manos". Session 4 wirea un servidor
    MCP local (SQLite o Postgres contra DotaChile) y muestra el JSON crudo del
    tool-call en la terminal.
    Slide referenciado por: Sessions 1, 4 (primary), 5, 6, 8, 9.
    -->

    ---
    ```

    **Primitive 3 — Skill slide (definition lifted from analog lines 156-164):**

    ```markdown
    # Skill

    **Concepto en 1 línea:** Un manual versionado que Claude **carga a demanda** cuando detecta un trigger, sin inflar el system prompt inicial.

    **Qué hace:**
    - Vive como `SKILL.md` en `.claude/skills/` con frontmatter (description, allowed-tools)
    - Claude detecta keyword en el prompt del usuario → carga el Skill en contexto
    - El Skill contiene los pasos, las convenciones, los comandos del caso de uso
    - Se versiona, se comparte entre devs, se compone con otros Skills

    ![Skill](./skill.svg)

    > Skills > prompts largos. Un prompt de 200 líneas se empaqueta como Skill y se invoca.

    <!--
    Presentador: Skill = "conocimiento bajo demanda". Ataca directo el problema
    del context window (Session 2): si cargas todo upfront, te quedas sin tokens;
    si cargas nada, Claude inventa. Skills cargan lo justo, cuando se necesita.
    Slide referenciado por: Sessions 1, 5 (primary), 6, 8, 9.
    -->

    ---
    ```

    **Primitive 4 — Agent slide (no analog — draft fresh from RESEARCH.md guidance):**

    ```markdown
    # Agent

    **Concepto en 1 línea:** Un **subagente** con su propio contexto y herramientas, spawneado desde el agente padre vía Task tool, que ejecuta una tarea delimitada y devuelve solo el resumen.

    **Qué hace:**
    - El padre invoca `Task(prompt, tools)` — crea un subagente aislado
    - El subagente tiene su propia ventana de contexto (no ve el contexto del padre)
    - Ejecuta su tarea (lee archivos, corre búsquedas, genera un reporte)
    - Al terminar, el padre recibe **solo el return value** — no el contexto intermedio

    ![Agent](./agent.svg)

    > Agents refrescan contexto — el padre no se inunda con los 1800 LOC que el subagente leyó.

    <!--
    Presentador: Agents = el mecanismo de "contexto desechable". Es la respuesta
    arquitectónica al context-window problem de Session 2. Session 6 lo demo
    contra TorneoService.java (1800 LOC god-class): el subagente lee el archivo
    entero, devuelve un reporte de 10 TODOs, y el padre nunca ve los 1800 LOC.
    Slide referenciado por: Sessions 1, 2 (framing), 6 (primary), 8, 9.
    -->

    ---
    ```

    **Primitive 5 — Hook slide (no analog — draft fresh):**

    ```markdown
    # Hook

    **Concepto en 1 línea:** Una **capa determinística** que corre código ordinario (scripts, CLIs) antes o después de cada tool-call de Claude, permitiendo validar, bloquear, o aumentar la acción.

    **Qué hace:**
    - Configurado en `.claude/settings.json` — matcher por tool (Edit, Write, Bash, etc.)
    - `PreToolUse` corre antes: exit code no-cero bloquea la llamada
    - `PostToolUse` corre después: puede correr validaciones (tests, linters, compilers)
    - Claude ve el output del hook como feedback y puede auto-corregirse

    ![Hook](./hook.svg)

    > Hooks = capa determinística alrededor del output estocástico del LLM.

    <!--
    Presentador: Hooks son la contraparte determinística a la naturaleza
    estocástica del LLM. Session 7 demos un PostToolUse que corre mvn -o compile
    en cada edit de Java, y un PreToolUse que bloquea escrituras en
    src/java/controller/ (ver CLAUDE.md: off-limits folder).
    Slide referenciado por: Sessions 1, 2 (framing — determinismo), 7 (primary), 8, 9.
    -->

    ---
    ```

    **Primitive 6 — Slash Command slide (no analog — draft fresh):**

    ```markdown
    # Slash Command

    **Concepto en 1 línea:** Un **punto de entrada disparado por el usuario** (`/nombre-comando args`) que compone Skills, Agents, Hooks y MCPs en un flujo nombrado y reproducible.

    **Qué hace:**
    - Vive como `.md` en `.claude/commands/` con frontmatter (description, argument-hint, allowed-tools)
    - Usuario tipea `/mi-comando arg1 arg2` en la CLI
    - Claude carga el comando → compone los pasos (invocar Skills, spawnear Agents, llamar MCP tools)
    - Es el "entry point nombrado" — Skills son invocados por keyword, Commands por invocación explícita

    ![Slash Command](./command.svg)

    > Command = user-triggered entry point. Skill = loaded behavior on demand. Resuelve la ambigüedad de Session 5.

    <!--
    Presentador: Commands vs Skills genera confusión en la audiencia después
    de Session 5. La diferencia clave: el USUARIO invoca Commands
    (/mi-comando); Claude invoca Skills automáticamente al detectar el trigger.
    Session 8 autorea /dota-audit-xss en vivo y compone con Skills + Agents.
    Slide referenciado por: Sessions 1, 5 (contrast), 8 (primary), 9.
    -->

    ---

    ## Referencias

    - `SKILL.md` anatomy: docs.claude.com/en/docs/claude-code/skills
    - MCP spec: modelcontextprotocol.io
    - Hooks event catalog: docs.claude.com (re-verificar cada sesión — catalog drifted)
    - Agents (Task tool): docs.claude.com

    <!--
    Presentador: slide de cierre. Las refs son para audiencia que quiera
    profundizar post-sesión. HANDOUT.md de cada sesión linkea aquí.
    -->
    ```

    **Constraints (per D-09, D-10, D-11, D-12, RESEARCH Pitfall 9, PATTERNS):**
    - **Exactly 6 H1 headings** for primitives (`# RAG`, `# MCP`, `# Skill`, `# Agent`, `# Hook`, `# Slash Command`) — VALIDATION.md SCAF-07 row checks these.
    - **`paginate: false`** in frontmatter (Pitfall 9 — it's a reference, not a linear arc).
    - **Every slide uses the same skeleton:** `# <primitive>` → `**Concepto en 1 línea:**` bold prefix → `**Qué hace:**` bullet block → `![<primitive>](./<primitive>.svg)` image → `>` blockquote one-liner → `<!-- presenter note -->` HTML comment.
    - **Spanish only.** No English prose. Primitive names (RAG, MCP, Skill, Agent, Hook, Slash Command) stay English as stable terms of art.
    - **Speaker notes in `<!-- ... -->` HTML comments** on every slide (pattern from analog lines 22-27) — invisible to audience, visible in Marp-rendered HTML source.

    ### Files 2-7: The 6 Mermaid diagram sources

    Create each `.mmd` file following the `graph LR` idiom from the existing analog `docs/presentations/2026-04-10-ai-driven-development/2026-04-10-ai-driven-development-ciclo.mmd`. **All node labels MUST be Spanish.** Diagrams show data + control flow per S01-03 requirement (not decorative — PATTERNS.md explicitly flags this).

    **`docs/presentations/GLOSSARY/rag.mmd`:**

    ```mermaid
    graph LR
        Q["❓ Query<br/>(usuario)"] --> E["🔢 Embedder<br/>(nomic-embed-text)"]
        E --> V["📚 Vector store<br/>+ BM25"]
        V --> K["📄 Top-k chunks"]
        K --> C["🤖 Claude<br/>(contexto aumentado)"]
        C --> A["💬 Answer"]

        Q --- Q1["'qué dijeron sobre X'"]
        V --- V1["Índice híbrido:<br/>léxico + semántico"]
        K --- K1["Solo los relevantes,<br/>no el corpus entero"]
    ```

    **`docs/presentations/GLOSSARY/mcp.mmd`:**

    ```mermaid
    graph LR
        U["👤 Usuario"] --> C["🤖 Claude<br/>(agente)"]
        C --> MC["🔌 MCP client<br/>(en Claude Code)"]
        MC -->|JSON tool-call| MS["🖥️ MCP server<br/>(local o remoto)"]
        MS --> T["🛠️ Tool<br/>(Postgres, Plane, etc.)"]
        T --> R["📦 Resultado"]
        R --> MS
        MS -->|JSON response| MC
        MC --> C

        MC --- MC1["Spec abierto<br/>modelcontextprotocol.io"]
        T --- T1["Cualquier API<br/>o proceso"]
    ```

    **`docs/presentations/GLOSSARY/skill.mmd`:**

    ```mermaid
    graph LR
        P["💬 Prompt del usuario"] --> D["🎯 Claude detecta trigger<br/>(description match)"]
        D --> L["📖 Carga SKILL.md<br/>(.claude/skills/)"]
        L --> S["📋 Sigue los pasos<br/>del manual"]
        S --> O["✅ Output"]

        L --- L1["allowed-tools:<br/>Edit, Bash, Grep"]
        S --- S1["Context on demand,<br/>no upfront bloat"]
    ```

    **`docs/presentations/GLOSSARY/agent.mmd`:**

    ```mermaid
    graph LR
        P["🤖 Agente padre<br/>(contexto A)"] -->|Task tool| S["🤖 Subagente<br/>(contexto B, aislado)"]
        S --> F["📄 Lee archivos<br/>+ corre herramientas"]
        F --> R["📝 Reporte estructurado"]
        R -->|solo el return value| P

        S --- S1["Contexto propio<br/>(no ve contexto A)"]
        R --- R1["Padre NO ve los<br/>1800 LOC leídos"]
    ```

    **`docs/presentations/GLOSSARY/hook.mmd`:**

    ```mermaid
    graph LR
        C["🤖 Claude<br/>quiere llamar tool"] --> PRE["🛡️ PreToolUse hook<br/>(script local)"]
        PRE -->|exit 0 = allow| T["🔧 Tool se ejecuta<br/>(Edit, Write, Bash)"]
        PRE -->|exit ≠ 0 = block| C
        T --> POST["✅ PostToolUse hook<br/>(mvn compile, tests, lint)"]
        POST -->|feedback| C

        PRE --- PRE1["Determinístico —<br/>bloquea lo peligroso"]
        POST --- POST1["Determinístico —<br/>valida lo hecho"]
    ```

    **`docs/presentations/GLOSSARY/command.mmd`:**

    ```mermaid
    graph LR
        U["👤 /comando args"] --> C["🤖 Claude carga<br/>.claude/commands/comando.md"]
        C --> S["📋 Invoca Skills"]
        C --> A["🤖 Spawnea Agents"]
        C --> M["🔌 Llama MCP tools"]
        C --> H["🛡️ Atraviesa Hooks"]
        S --> R["✅ Resultado nombrado<br/>+ reproducible"]
        A --> R
        M --> R
        H --> R

        U --- U1["User-triggered<br/>(vs Skill = auto)"]
    ```

    **Mermaid constraints (per PATTERNS.md §"{rag,mcp,skill,agent,hook,command}.mmd"):**
    - `graph LR` at the top of every file (left-to-right — established idiom from the single analog).
    - Node labels in **Spanish**, double-quoted, with `<br/>` for two-line labels.
    - Emojis at major-node labels (established pattern — 🔍 📋 🔨 ❓ 🤖 🛡️ ✅ etc.).
    - `-->` for causal/sequential arrows; `---` for sub-relationship annotations.
    - No theme overrides in the `.mmd` source. Render applies `-b transparent` at render time.
    - Each diagram MUST show data flow + control flow — the S01-03 gate explicitly rejects decorative diagrams.
  </action>
  <verify>
    <automated>test -d docs/presentations/GLOSSARY && test $(ls docs/presentations/GLOSSARY/*.mmd | wc -l) -eq 6 && for p in RAG MCP Skill Agent Hook "Slash Command"; do grep -q -E "^#\s*$p" docs/presentations/GLOSSARY/GLOSSARY.md || echo "MISSING: $p"; done</automated>
  </verify>
  <acceptance_criteria>
    - `test -d docs/presentations/GLOSSARY` succeeds
    - `test $(ls docs/presentations/GLOSSARY/*.mmd | wc -l) -eq 6` succeeds (6 Mermaid sources)
    - `test -f docs/presentations/GLOSSARY/GLOSSARY.md` succeeds
    - VALIDATION.md SCAF-07 row: `for p in RAG MCP Skill Agent Hook "Slash Command"; do grep -q -E "^#\s*$p" docs/presentations/GLOSSARY/GLOSSARY.md || echo MISSING; done` returns empty
    - `grep -c "paginate: false" docs/presentations/GLOSSARY/GLOSSARY.md` returns `1` (Pitfall 9)
    - `grep -c "theme: gaia" docs/presentations/GLOSSARY/GLOSSARY.md` returns `1`
    - `grep -c "graph LR" docs/presentations/GLOSSARY/*.mmd | grep -c ":1$"` returns `6` (each .mmd starts with `graph LR`)
    - `grep -c "Concepto en 1 línea" docs/presentations/GLOSSARY/GLOSSARY.md` returns `6` (Spanish skeleton on every primitive slide)
    - `grep -cE "^\!\[.*\]\(\./.*\.svg\)" docs/presentations/GLOSSARY/GLOSSARY.md` returns `6` (6 `![…](./<primitive>.svg)` image embeds)
    - `grep -c "Marp source" docs/presentations/GLOSSARY/GLOSSARY.md` returns `0` (no stray English frontmatter labels)
  </acceptance_criteria>
  <done>
    `docs/presentations/GLOSSARY/` contains `GLOSSARY.md` + 6 `.mmd` files; Marp deck has 6 canonical primitive slides with Spanish content; each `.mmd` is a `graph LR` diagram with Spanish labels showing data + control flow; paginate: false confirmed.
  </done>
</task>

<task type="auto" tdd="false">
  <name>Task 2: Render GLOSSARY.md → .html and all 6 .mmd → .svg, commit render outputs alongside sources</name>
  <files>docs/presentations/GLOSSARY/GLOSSARY.html, docs/presentations/GLOSSARY/rag.svg, docs/presentations/GLOSSARY/mcp.svg, docs/presentations/GLOSSARY/skill.svg, docs/presentations/GLOSSARY/agent.svg, docs/presentations/GLOSSARY/hook.svg, docs/presentations/GLOSSARY/command.svg</files>
  <read_first>
    - docs/presentations/CLAUDE.md (Marp build section lines 19-26; Mermaid workflow lines 35-44 — the render-on-commit rule)
    - .planning/phases/00-series-scaffolding/00-PATTERNS.md §"Shared Patterns → Render-on-commit convention"
    - .planning/phases/00-series-scaffolding/00-RESEARCH.md §"Code Examples → Marp render command / Mermaid render command"
    - Task 1 outputs: `docs/presentations/GLOSSARY/GLOSSARY.md` + 6 `.mmd` files (must exist before this task runs)
  </read_first>
  <action>
    Render all Marp and Mermaid sources into committed output files using the existing project convention from `docs/presentations/CLAUDE.md`. Both source + rendered output are committed together in the same commit (render-on-commit rule).

    **Step 1 — Render the Marp deck to HTML:**

    ```bash
    npx @marp-team/marp-cli@latest --html \
      docs/presentations/GLOSSARY/GLOSSARY.md \
      -o docs/presentations/GLOSSARY/GLOSSARY.html
    ```

    **Step 2 — Render each of the 6 Mermaid diagrams to SVG (transparent background):**

    ```bash
    for p in rag mcp skill agent hook command; do
      npx @mermaid-js/mermaid-cli \
        -i docs/presentations/GLOSSARY/${p}.mmd \
        -o docs/presentations/GLOSSARY/${p}.svg \
        -b transparent
    done
    ```

    **Constraints (per existing convention + PATTERNS.md):**
    - Use the pinned versions when possible — `@marp-team/marp-cli@4.3.1` and `@mermaid-js/mermaid-cli@11.12.0` per RESEARCH.md tool-inventory. `@latest` is acceptable in this plan because the existing project CLAUDE.md uses `@latest`; pin upgrades are a SETUP.md concern (plan 04), not this plan.
    - `-b transparent` is REQUIRED for Mermaid — matches existing repo convention (docs/presentations/CLAUDE.md line 42).
    - `--html` flag on Marp is REQUIRED — matches existing convention (line 21). The flag enables HTML passthrough for `<!-- -->` speaker notes.
    - **Do NOT modify the .md or .mmd source files in this task.** They were authored in Task 1.
    - **Do NOT add `html: true` to the Marp frontmatter** — the CLI `--html` flag handles that (PATTERNS.md explicit: existing decks use CLI flag, not frontmatter).
    - If either render fails, inspect the error (likely a Mermaid syntax issue in a `.mmd` — fix the source in `docs/presentations/GLOSSARY/<file>.mmd`, not in an intermediate transform).

    **Step 3 — Verify the render outputs are non-trivial:**

    ```bash
    test -s docs/presentations/GLOSSARY/GLOSSARY.html   # non-empty
    for p in rag mcp skill agent hook command; do
      test -s docs/presentations/GLOSSARY/${p}.svg
    done
    ```

    All must succeed (non-empty files).
  </action>
  <verify>
    <automated>test -f docs/presentations/GLOSSARY/GLOSSARY.html && test $(ls docs/presentations/GLOSSARY/*.svg | wc -l) -eq 6 && for p in rag mcp skill agent hook command; do test -s docs/presentations/GLOSSARY/${p}.svg || echo "EMPTY: ${p}.svg"; done</automated>
  </verify>
  <acceptance_criteria>
    - VALIDATION.md SCAF-07 row: `test -d docs/presentations/GLOSSARY && test $(ls docs/presentations/GLOSSARY/*.mmd | wc -l) -eq 6 && test $(ls docs/presentations/GLOSSARY/*.svg | wc -l) -eq 6 && test -f docs/presentations/GLOSSARY/GLOSSARY.md && test -f docs/presentations/GLOSSARY/GLOSSARY.html` succeeds
    - Each of the 6 SVGs is non-empty: `for p in rag mcp skill agent hook command; do test -s docs/presentations/GLOSSARY/${p}.svg; done` succeeds
    - GLOSSARY.html is non-empty: `test -s docs/presentations/GLOSSARY/GLOSSARY.html` succeeds
    - Each SVG file begins with `<svg` or `<?xml` (valid SVG output, not an error page): `for p in rag mcp skill agent hook command; do head -c 5 docs/presentations/GLOSSARY/${p}.svg | grep -qE '<svg|<\?xml' || echo "INVALID: ${p}.svg"; done` returns empty
    - GLOSSARY.html contains recognizable Marp output: `grep -q "marpit" docs/presentations/GLOSSARY/GLOSSARY.html || grep -q "<section" docs/presentations/GLOSSARY/GLOSSARY.html` succeeds
  </acceptance_criteria>
  <done>
    14 files in `docs/presentations/GLOSSARY/` total: 1 `.md` + 1 `.html` + 6 `.mmd` + 6 `.svg`. All rendered outputs non-empty and well-formed. Render-on-commit rule honored.
  </done>
</task>

</tasks>

<threat_model>
No security-sensitive surface — docs-only artifact. The glossary is a static reference deck with no code execution, no data storage, no external integrations at runtime.
</threat_model>

<verification>
Run the full VALIDATION.md SCAF-07 row set:

```bash
test -d docs/presentations/GLOSSARY
test $(ls docs/presentations/GLOSSARY/*.mmd | wc -l) -eq 6
test $(ls docs/presentations/GLOSSARY/*.svg | wc -l) -eq 6
test -f docs/presentations/GLOSSARY/GLOSSARY.md
test -f docs/presentations/GLOSSARY/GLOSSARY.html

for p in RAG MCP Skill Agent Hook "Slash Command"; do
  grep -q -E "^#\s*$p" docs/presentations/GLOSSARY/GLOSSARY.md || echo "MISSING: $p"
done
```

Both commands must succeed and the for-loop must return empty.

**Visual spot-check (manual):** Open `docs/presentations/GLOSSARY/GLOSSARY.html` in a browser; confirm 6 primitive slides render with the canonical diagrams, no broken `<img>` tags, no Marp error pages.
</verification>

<success_criteria>
CURR-03 single source of truth established at `docs/presentations/GLOSSARY/`. Sessions 1-9 can now reference via `![<primitive>](../GLOSSARY/<primitive>.svg)` + `Ver GLOSSARY.html §<Primitive>` without ever duplicating text. D-10 zero-drift property structurally enforced.
</success_criteria>

<output>
After completion, create `.planning/phases/00-series-scaffolding/00-03-SUMMARY.md` recording:
- File inventory (14 files expected: 1 `.md` + 1 `.html` + 6 `.mmd` + 6 `.svg`)
- Marp CLI version used (capture from `npx @marp-team/marp-cli --version`)
- Mermaid CLI version used (capture from `npx @mermaid-js/mermaid-cli --version`)
- Any render warning/error that was resolved
</output>
