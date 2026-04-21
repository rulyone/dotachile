---
phase: 00-series-scaffolding
plan: 05
type: execute
wave: 1
depends_on: []
files_modified:
  - docs/presentations/QUAL-GATES.md
  - docs/presentations/MANIFEST.template.md
  - docs/presentations/REHEARSAL.template.md
  - docs/presentations/HANDOUT.template.md
autonomous: true
requirements:
  - SCAF-08
  - SCAF-09
  - SCAF-10
  - QUAL-01
  - QUAL-02
  - QUAL-03
  - QUAL-04
  - QUAL-05
  - QUAL-06
  - QUAL-07
  - QUAL-08
  - QUAL-09
  - QUAL-10
  - QUAL-11
  - QUAL-12
tags:
  - documentation
  - templates
  - quality-gates
user_setup: []

must_haves:
  truths:
    - "QUAL-GATES.md lists all 12 QUAL gates (QUAL-01..QUAL-12) each with: qué exige, por qué existe, cómo se verifica, template asociado"
    - "MANIFEST.template.md, REHEARSAL.template.md, HANDOUT.template.md all live at docs/presentations/ root"
    - "Templates reference QUAL-GATES.md by gate ID only (e.g., 'see QUAL-GATES.md §QUAL-04') — NEVER duplicate gate text"
    - "MANIFEST template has placeholder-annotated skeleton (D-01): pre/post tag SHAs, slide-to-commit map, recovery command, compare URL, version pins (QUAL-12), known follow-ups"
    - "REHEARSAL template has checklist-at-top + free-form-notes-below (D-03): model-ID pinned, fallback rehearsed, same-day date, network plan, pre-warm ritual"
    - "HANDOUT template has fixed 5 Spanish sections (D-02): ¿Qué vimos?, Comandos para probar, Link de comparación, Próxima sesión, Lecturas"
    - "No 'fake example rows' in templates — every fillable field uses [Replace: ...] prompt (D-01)"
  artifacts:
    - path: "docs/presentations/QUAL-GATES.md"
      provides: "Single source of truth for the 12 QUAL gates; templates link here by gate ID"
      contains: "## QUAL-01"
    - path: "docs/presentations/MANIFEST.template.md"
      provides: "Placeholder-annotated session manifest skeleton (QUAL-01/QUAL-12)"
      contains: "[Replace:"
    - path: "docs/presentations/REHEARSAL.template.md"
      provides: "Checklist + free-form notes for same-day rehearsal (QUAL-02/03/09/12)"
      contains: "- [ ]"
    - path: "docs/presentations/HANDOUT.template.md"
      provides: "Spanish audience takeaway with fixed 5 sections (QUAL-06)"
      contains: "¿Qué vimos?"
  key_links:
    - from: "MANIFEST.template.md footer"
      to: "QUAL-GATES.md §QUAL-01 and §QUAL-12"
      via: "HTML comment cross-ref (D-04)"
      pattern: "see QUAL-GATES.md §QUAL"
    - from: "REHEARSAL.template.md checklist"
      to: "QUAL-GATES.md §QUAL-02, §QUAL-03, §QUAL-09"
      via: "inline cross-ref per checklist item"
      pattern: "QUAL-0[239]"
    - from: "HANDOUT.template.md footer"
      to: "QUAL-GATES.md §QUAL-06"
      via: "HTML comment cross-ref"
      pattern: "QUAL-06"
---

<objective>
Author the QUAL-GATES reference doc + the three presenter sidecar templates (MANIFEST / REHEARSAL / HANDOUT). Bundled into one plan because each template links to QUAL-GATES.md by gate ID (D-04 cross-reference convention) — splitting would create a race on who names the anchor points first.

**Purpose:**
- **QUAL-GATES.md (D-04):** single source for the 12 QUAL gates. Templates cite gate IDs; never duplicate text (Pitfall 3 prevention).
- **MANIFEST.template.md (SCAF-08 + QUAL-01/QUAL-12):** placeholder-annotated skeleton so the presenter of Session NN copies it in, fills every `[Replace: ...]` prompt, and ends up with session title, pre/post tag SHAs, slide-to-commit map, recovery command, compare URL, version pins, and known follow-ups.
- **REHEARSAL.template.md (SCAF-09 + QUAL-02/03/09/12, D-03):** checklist-at-top enforcing same-day-rehearsal + fallback-rehearsed + model-ID-pinned + pre-warm-ritual + network-plan, followed by free-form notes section.
- **HANDOUT.template.md (SCAF-10 + QUAL-06, D-02):** fixed 5 Spanish sections for audience takeaway.

**Output:** 4 new files, all at `docs/presentations/` root (series-level shared artifacts, copied into each session folder per SCAF-04 convention).
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
  <name>Task 1: Author docs/presentations/QUAL-GATES.md with all 12 gates (single source of truth)</name>
  <files>docs/presentations/QUAL-GATES.md</files>
  <read_first>
    - .planning/REQUIREMENTS.md §QUAL (QUAL-01..QUAL-12) — the 12 gate specs verbatim
    - .planning/phases/00-series-scaffolding/00-CONTEXT.md §"Template Depth Philosophy → D-04" (single-source rule)
    - .planning/phases/00-series-scaffolding/00-RESEARCH.md §"QUAL-GATES.md (referenced by templates via D-04)" — structure
    - .planning/phases/00-series-scaffolding/00-PATTERNS.md §"docs/presentations/QUAL-GATES.md (reference doc — greenfield)" — exact pattern
    - .planning/phases/00-series-scaffolding/00-VALIDATION.md (QUAL-01..12 row: all 12 gate IDs must appear)
  </read_first>
  <action>
Create `docs/presentations/QUAL-GATES.md` as the single reference doc for all 12 QUAL gates. Each gate gets a dedicated `## QUAL-NN` H2 section with fixed subsections: qué exige, por qué existe, cómo se verifica, template asociado. Spanish prose; English gate IDs; gate text is authored ONCE, here.

Write the file with:

1. **Title + preamble** in Spanish explaining that this is the single source, templates link by ID, sessions verify against this doc.

2. **Exactly 12 `## QUAL-NN` sections**, each containing 4 fixed subsections in Spanish:
   - `**Qué exige:**` one-sentence summary of the requirement
   - `**Por qué existe:**` short paragraph naming the pitfall/feature being mitigated (cite Pitfall N or FEATURES D/S code where applicable)
   - `**Cómo se verifica:**` grep/shell command or cross-reference pattern
   - `**Template asociado:**` which template (MANIFEST/REHEARSAL/HANDOUT/ninguno) enforces it

3. **The 12 gates with content (Spanish prose authored from REQUIREMENTS.md §QUAL specs):**

   - `## QUAL-01 — MANIFEST.md completo` — every session's MANIFEST has title, fecha, session-NN-pre/post SHAs, slide→commit map, recovery command `git reset --hard session-NN-pre`, GitHub compare URL, known follow-ups. Verificación: `grep -c "session-NN-pre\|session-NN-post\|Recovery\|compare" MANIFEST.md` ≥ 4. Template: `MANIFEST.template.md`.

   - `## QUAL-02 — Fallback artifact + switchover rehearsed` — cada sesión tiene un `.cast` de asciinema o `.tape` de VHS junto al deck; switchover live→fallback ensayado y registrado en REHEARSAL.md. Por qué: Pitfall 1 — la IA flakea; sin fallback el budget se pierde. Verificación: `ls YYYY-MM-DD-NN-<slug>/*.cast *.tape 2>/dev/null | wc -l` ≥ 1; `grep -q "switchover" REHEARSAL.md`. Template: `REHEARSAL.template.md`.

   - `## QUAL-03 — Same-day rehearsal dentro de 24h` — rehearsal en la misma máquina y red que el delivery, dentro de 24h previas; fecha registrada en REHEARSAL.md. Por qué: Pitfall 1 — ambiente drift. Verificación: `grep -E "Fecha del rehearsal: [0-9]{4}-[0-9]{2}-[0-9]{2}" REHEARSAL.md`. Template: `REHEARSAL.template.md`.

   - `## QUAL-04 — "Lo que la IA NO hizo" (honesty slide)` — cerca del final, slide nombrando 1 cosa que el presentador hizo manualmente + 1 que requirió corrección en rehearsal. Por qué: FEATURES S11 + D8 — honestidad como diferenciador. Verificación: `grep -q "Lo que la IA NO hizo\|x10 honesty\|NO hizo" deck.md`. Template: ninguno directo; REHEARSAL.template.md colecta candidates.

   - `## QUAL-05 — Cada sesión toca CONCERNS.md` — cada sesión toca ≥1 file/artifact de `.planning/codebase/CONCERNS.md`. Session 2 excepción condicional — su script hands-on igual debe tocar la codebase. Por qué: Pitfall 14 + FEATURES D5 — core value del workshop. Verificación: cross-ref con `CONCERNS-MAPPING.md` — cada Session NN con ≥1 fila `Session NN | claimed`. Template: ninguno directo; MANIFEST slide→commit map evidencia.

   - `## QUAL-06 — Convención bilingüe on-screen` — cada sesión declara ≥1 vez on-screen: "El deck y la narración están en español; la salida de Claude Code está en inglés — estado actual del ecosistema". Por qué: Pitfall 25. Verificación: `grep -q "deck y la narración\|salida de Claude Code está en inglés\|bilingüe" deck.md`. Template: `HANDOUT.template.md` (footer cita); Session 1 autorea slide canónica.

   - `## QUAL-07 — Budget 57 min on-paper` — demo-task sliceada para 57 min: 5 intro + 10 concepto + 12+12 demo + 8 recap + 10 Q&A. Por qué: FEATURES S10 + Pitfall 6. Verificación: REHEARSAL.md "Timing por sección" observa ≤57 min totales; si excede, re-sliceá en plan-phase. Template: `REHEARSAL.template.md`.

   - `## QUAL-08 — Known follow-ups en migration-slice sessions` — cada sesión migration-slice incluye slide "known follow-ups" + ≥1 GitHub issue por slice diferido. Por qué: Pitfall 30 — refactor overclaim. Verificación (migration-slices): `grep -q "known follow-ups\|próximos slices\|slices diferidos" deck.md`. Template: `MANIFEST.template.md` tiene sección.

   - `## QUAL-09 — Pre-warm Payara + Postgres ≥10 min antes` — `docker compose up -d` + request dummy ≥10 min antes de la sesión. `docker compose up` NUNCA es paso en vivo. Por qué: Pitfall 8 — Payara 5 cold boot 60-90s; SCAF-05 digest pinning elimina re-pull risk. Verificación: REHEARSAL checklist marcado; `grep -q "docker compose up" deck.md` en el body NO encuentra. Template: `REHEARSAL.template.md` (checklist item).

   - `## QUAL-10 — Single-primitive primary teaching surface` — cada sesión enseña una sola primitiva en los primeros 30 min. Composición con previas OK solo después de introducir la actual. Por qué: Pitfall 18 — multi-primitive salad. Verificación: revisión humana del outline vs REQUIREMENTS.md per-session — ningún S<NN>-* requiere primitiva previamente no-introducida. Template: ninguno directo.

   - `## QUAL-11 — Deck .html + Mermaid .svg committeados` — cada `.md` tiene `.html` al lado; cada `.mmd` tiene `.svg` al lado (render-on-commit). Por qué: HTMLPreview reescribe `<script>` tags — runtime Mermaid JS no funciona. Verificación: `for f in */*.md; do test -f "${f%.md}.html"; done`; ídem para `.mmd`/`.svg`. Template: `REHEARSAL.template.md` (checklist).

   - `## QUAL-12 — Version pins en MANIFEST` — MANIFEST.md nombra Claude Code CLI version, model ID (e.g., `claude-opus-4-7`), Ollama / MCP / Marp / Mermaid versions en rehearsal y delivery. Por qué: Pitfall 1 — "funcionó ayer" se convierte en "no funciona hoy". Verificación: `grep -c "Claude Code\|Model ID\|Ollama\|Marp\|Mermaid" MANIFEST.md` ≥ 5. Template: `MANIFEST.template.md` (tabla "Versions").

4. **Closing section** `## Cómo usar esta doc desde los templates` explaining: templates cite by `<!-- see QUAL-GATES.md §QUAL-NN -->` or inline `ver QUAL-GATES.md §QUAL-NN`; to add a gate, add a `## QUAL-NN` section here + update templates + re-run VALIDATION.md greps.

**Constraints (per D-04 + PATTERNS + VALIDATION):**
- Exactly 12 H2 sections named `## QUAL-NN` where NN = 01..12. VALIDATION.md greps each.
- Every gate has the 4 fixed subsections in the same order.
- Spanish prose; English gate IDs ("QUAL-01" stays English as stable identifier).
- No duplicate gate text anywhere else in the repo — templates (Tasks 2-3) cite by ID only.
  </action>
  <verify>
    <automated>test -f docs/presentations/QUAL-GATES.md && for n in 01 02 03 04 05 06 07 08 09 10 11 12; do grep -q "QUAL-$n" docs/presentations/QUAL-GATES.md || echo "MISSING: QUAL-$n"; done</automated>
  </verify>
  <acceptance_criteria>
    - `test -f docs/presentations/QUAL-GATES.md` succeeds
    - VALIDATION.md QUAL-01..12 row: `for n in 01 02 03 04 05 06 07 08 09 10 11 12; do grep -q "QUAL-$n" docs/presentations/QUAL-GATES.md || echo MISSING; done` returns empty
    - 12 H2 sections: `grep -c "^## QUAL-" docs/presentations/QUAL-GATES.md` returns `12`
    - Every gate has the 4 fixed subsections: `grep -c "Qué exige:" docs/presentations/QUAL-GATES.md` returns `12`; `grep -c "Por qué existe:" docs/presentations/QUAL-GATES.md` returns `12`; `grep -c "Cómo se verifica:" docs/presentations/QUAL-GATES.md` returns `12`; `grep -c "Template asociado:" docs/presentations/QUAL-GATES.md` returns `12`
    - Spanish prose: `grep -q "sesión\|archivo\|exige\|verificación" docs/presentations/QUAL-GATES.md` succeeds
  </acceptance_criteria>
  <done>
    `docs/presentations/QUAL-GATES.md` exists with exactly 12 `## QUAL-NN` sections each containing the 4 fixed subsections; single source of truth established for template cross-references in Tasks 2-3.
  </done>
</task>

<task type="auto" tdd="false">
  <name>Task 2: Author MANIFEST.template.md and REHEARSAL.template.md with placeholder-annotated skeletons</name>
  <files>docs/presentations/MANIFEST.template.md, docs/presentations/REHEARSAL.template.md</files>
  <read_first>
    - .planning/phases/00-series-scaffolding/00-CONTEXT.md §D-01 (MANIFEST placeholder-annotated skeleton), §D-03 (REHEARSAL checklist + free-form notes), §D-04 (QUAL-GATES cross-ref convention)
    - .planning/phases/00-series-scaffolding/00-RESEARCH.md §"Sidecar Templates → MANIFEST.template.md (SCAF-08)" — required fields + ARCHITECTURE Pattern 1 reference
    - .planning/phases/00-series-scaffolding/00-RESEARCH.md §"Sidecar Templates → REHEARSAL.template.md (SCAF-09)" — checklist + free-form
    - .planning/phases/00-series-scaffolding/00-PATTERNS.md §"MANIFEST.template.md (template — greenfield)" — verbatim block pattern
    - .planning/phases/00-series-scaffolding/00-PATTERNS.md §"REHEARSAL.template.md (template — greenfield)" — verbatim block pattern
    - .planning/phases/00-series-scaffolding/00-VALIDATION.md (SCAF-08 and SCAF-09 grep rows)
    - docs/presentations/QUAL-GATES.md (just authored in Task 1 — gate IDs the template will cite)
  </read_first>
  <action>
Author two files at `docs/presentations/` root — both are copied into each session folder at session plan-phase time.

### File 1: `docs/presentations/MANIFEST.template.md`

Per D-01, every fillable field uses `[Replace: ...]` inline prompt. No fake example rows. Pre-session vs post-session fields are marked explicitly.

Write a template with:

1. **Title line:** `# [Replace: Session NN — Título en español]`
2. **Metadata block:** Fecha `[Replace: YYYY-MM-DD]`, Presentador `Pablo Martínez` (locked per PATTERNS §THEME.md)
3. **Usage blockquote:** explaining that the template is copied to `docs/presentations/YYYY-MM-DD-NN-<slug>/MANIFEST.md` at session plan-phase; fields `[Replace: ...]` get filled in; post-session fields filled only after `session-NN-post` is pushed; `grep -c "\[Replace:" MANIFEST.md` returns 0 at delivery time
4. **`## Tags` section:** three bullets with `session-NN-pre` SHA placeholder, `session-NN-post` SHA placeholder (marked *(post-session)*), GitHub compare URL placeholder
5. **`## Slide → Commit map` section:** 4-column markdown table with 3 empty rows using `[Replace: slide title] | [Replace: SHA] | [Replace: notas]` — no fictional content; instruction "(agregar filas según necesidad)"
6. **`## Recovery command` section:** bash code block with `git reset --hard session-NN-pre` + `docker compose restart app`
7. **`## Versions (QUAL-12)` section:** 6-row table with columns `Componente | Versión (rehearsal) | Versión (delivery)` — rows for Claude Code CLI, Model ID (e.g. `claude-opus-4-7`), Ollama, Marp CLI, Mermaid CLI, MCP servers — each cell uses `[Replace: ...]` placeholder that names the source command
8. **`## Slices de CONCERNS.md tocados` section:** single bullet `[Replace: e.g., XSS-01, XSS-02 — o "N/A Session 2 excepción"]` — cross-ref to CONCERNS-MAPPING.md
9. **`## Known follow-ups (QUAL-08)` section:** single bullet `[Replace: issue link o "ninguno" si no es migration-slice]`
10. **Footer HTML comment:** `<!-- see QUAL-GATES.md §QUAL-01, §QUAL-08, §QUAL-12 -->`

**Constraints per D-01 + PATTERNS:**
- Every fillable field uses `[Replace: <what-goes-here>]` — greppable so presenter can verify nothing is unfilled
- No fake example rows (Pitfall 3 warning — fake rows get forgotten and shipped)
- Pre vs post marking: compare URL annotated `*(post-session)*`; Versions table has dual-column (rehearsal + delivery)
- Footer links to QUAL-GATES.md by ID only (QUAL-01, QUAL-08, QUAL-12)
- Spanish section headers; English git commands in code blocks

### File 2: `docs/presentations/REHEARSAL.template.md`

Per D-03, checklist at top using `- [ ]` syntax (greppable per Pitfall 3), free-form notes below.

Write a template with:

1. **Title line:** `# [Replace: Session NN] — Rehearsal log`
2. **Metadata block:** Fecha del rehearsal `[Replace: YYYY-MM-DD — mismo día de la sesión o día anterior]`; Fecha de la sesión `[Replace: YYYY-MM-DD]`; Máquina y red usadas `[Replace: laptop-X + WiFi del venue / hotspot]`
3. **Usage blockquote:** copy to `docs/presentations/YYYY-MM-DD-NN-<slug>/REHEARSAL.md`; run rehearsal on same machine/network as delivery; unchecked item = blocker
4. **`## Checklist (QUAL-02, QUAL-03, QUAL-09, QUAL-11, QUAL-12)` section:** 9 checklist items using `- [ ]` syntax, each citing a QUAL gate ID inline:
   - Model ID pinneado en `MANIFEST.md` — `QUAL-GATES.md §QUAL-12`
   - Fallback artifact (`.cast` o `.tape`) existe junto al deck — `QUAL-GATES.md §QUAL-02`
   - Switchover live → fallback ensayado al menos una vez — `QUAL-GATES.md §QUAL-02`
   - Fecha de rehearsal registrada (mismo día de la sesión) — `QUAL-GATES.md §QUAL-03`
   - Plan de red: WiFi del venue probado O hotspot listo — Pitfall 1
   - Pre-warm: `docker compose up -d` + request dummy corridos ≥10 min antes — `QUAL-GATES.md §QUAL-09`
   - `.html` del deck renderiza limpio (HTMLPreview-compatible) — `QUAL-GATES.md §QUAL-11`
   - Todos los `.mmd` tienen `.svg` pre-renderizado — `QUAL-GATES.md §QUAL-11`
   - Versiones de tools (Marp, Mermaid, Ollama, Claude Code) registradas en `MANIFEST.md` — `QUAL-GATES.md §QUAL-12`
5. **Footer HTML comment after checklist:** `<!-- see QUAL-GATES.md §QUAL-02, §QUAL-03, §QUAL-09, §QUAL-11, §QUAL-12 -->`
6. **`## Notas libres` section** with 4 H3 subsections:
   - `### Timing por sección (QUAL-07 — budget 57 min)` — 7 bullets (Intro / Concepto / Demo 1 / Demo 2 / Recap / Q&A / Total) each with `[Replace: minutos observados] / <plan> planeados`
   - `### Cortes` — single bullet `[Replace: sección / slide cortada — razón]`
   - `### Flakes / correcciones manuales (candidates para QUAL-04 slide)` — single bullet `[Replace: flake observado / corrección manual aplicada]`
   - `### Otras observaciones` — single bullet `[Replace: cualquier otra cosa útil]`

**Constraints per D-03 + PATTERNS + Pitfall 3:**
- Checklist uses `- [ ]` syntax — NOT a markdown table (greppable; `grep -c "^- \[ \]"` must work)
- VALIDATION.md SCAF-09 row requires grep count ≥5; template has 9 items
- Each checklist item references a QUAL gate ID inline
- Free-form notes section below the checklist — exactly 4 H3 subsections as named above
- Spanish section titles + bullets; English gate IDs + bash code
  </action>
  <verify>
    <automated>test -f docs/presentations/MANIFEST.template.md && test -f docs/presentations/REHEARSAL.template.md && grep -q "session-NN-pre" docs/presentations/MANIFEST.template.md && grep -q "session-NN-post" docs/presentations/MANIFEST.template.md && test $(grep -c "^- \[ \]" docs/presentations/REHEARSAL.template.md) -ge 5</automated>
  </verify>
  <acceptance_criteria>
    - Both files exist: `test -f docs/presentations/MANIFEST.template.md && test -f docs/presentations/REHEARSAL.template.md` succeeds
    - VALIDATION.md SCAF-08 row: `grep -q "session-NN-pre" docs/presentations/MANIFEST.template.md && grep -q "session-NN-post" docs/presentations/MANIFEST.template.md && grep -q "Slide.*commit" docs/presentations/MANIFEST.template.md && grep -q "Recovery" docs/presentations/MANIFEST.template.md && grep -q "compare" docs/presentations/MANIFEST.template.md && grep -q "version" docs/presentations/MANIFEST.template.md -i` succeeds
    - MANIFEST placeholder-annotated (D-01): `grep -c "\[Replace:" docs/presentations/MANIFEST.template.md` returns `≥ 10`
    - MANIFEST references QUAL gate IDs only, not text: `grep -q "QUAL-01" docs/presentations/MANIFEST.template.md && grep -q "QUAL-12" docs/presentations/MANIFEST.template.md` succeeds
    - MANIFEST has versions table per QUAL-12: `grep -q "Claude Code CLI\|Model ID\|Ollama" docs/presentations/MANIFEST.template.md` succeeds
    - VALIDATION.md SCAF-09 row 1 (checklist): `test $(grep -c "^- \[ \]" docs/presentations/REHEARSAL.template.md) -ge 5` succeeds (template has 9)
    - VALIDATION.md SCAF-09 row 2 (QUAL cross-refs): `grep -q "QUAL-02" docs/presentations/REHEARSAL.template.md && grep -q "QUAL-03" docs/presentations/REHEARSAL.template.md && grep -q "QUAL-09" docs/presentations/REHEARSAL.template.md` succeeds
    - REHEARSAL free-form section present: `grep -q "## Notas libres" docs/presentations/REHEARSAL.template.md` succeeds
    - REHEARSAL timing section (QUAL-07): `grep -q "Timing por sección" docs/presentations/REHEARSAL.template.md` succeeds
    - No duplicated gate text (D-04 + Pitfall 3): `grep -c "cada sesión pre-warm\|deck y la narración están en español" docs/presentations/MANIFEST.template.md docs/presentations/REHEARSAL.template.md` returns `0` (gate text lives only in QUAL-GATES.md)
  </acceptance_criteria>
  <done>
    `docs/presentations/MANIFEST.template.md` has placeholder-annotated skeleton (pre/post SHAs, slide→commit map, recovery cmd, compare URL, versions table, known follow-ups) with ≥10 `[Replace:` prompts; `docs/presentations/REHEARSAL.template.md` has 9-item checklist using `- [ ]` syntax + 4-subsection free-form notes below; both files cite QUAL gates by ID only (no duplicated gate text).
  </done>
</task>

<task type="auto" tdd="false">
  <name>Task 3: Author HANDOUT.template.md with fixed 5 Spanish sections per D-02</name>
  <files>docs/presentations/HANDOUT.template.md</files>
  <read_first>
    - .planning/phases/00-series-scaffolding/00-CONTEXT.md §D-02 (fixed 5 Spanish sections)
    - .planning/phases/00-series-scaffolding/00-RESEARCH.md §"Sidecar Templates → HANDOUT.template.md (SCAF-10)" — verbatim block §540-575
    - .planning/phases/00-series-scaffolding/00-PATTERNS.md §"HANDOUT.template.md (template — greenfield)" — verbatim block
    - .planning/phases/00-series-scaffolding/00-VALIDATION.md (SCAF-10 row — all 5 Spanish section headings must appear)
    - docs/presentations/QUAL-GATES.md (for §QUAL-06 cross-ref)
  </read_first>
  <action>
Create `docs/presentations/HANDOUT.template.md`. Per D-02, this template has EXACTLY 5 fixed Spanish section headings, with `[Replace: ...]` placeholders instead of sample bullets (sample bullets explicitly rejected per D-02).

**Write this file verbatim:**

```markdown
# [Replace: Session NN — Título en español]

**Fecha:** [Replace: YYYY-MM-DD]
**Deck:** [Replace: link al deck .html, e.g., ./YYYY-MM-DD-NN-<slug>.html]

> **Cómo usar este template:**
> 1. Copiar a `docs/presentations/YYYY-MM-DD-NN-<slug>/HANDOUT.md` al iniciar el plan-phase.
> 2. Rellenar las 5 secciones fijas abajo. Cada sección tiene un rol específico —
>    no las renombres, el script de verificación busca los headings exactos.
> 3. Publicar el `HANDOUT.md` para la audiencia al cierre de la sesión
>    (linkeado desde el arco index `README.md`).

## ¿Qué vimos?

- [Replace: bullet 1 — lo más memorable de la sesión en 1 línea]
- [Replace: bullet 2]
- [Replace: bullet 3]

## Comandos para probar

Comandos que la audiencia puede correr post-sesión para reproducir el demo.

```bash
[Replace: comandos concretos — clone + checkout del tag + comando del demo]
```

## Link de comparación

Diff entre pre y post del demo:

- Pre → Post: `https://github.com/[Replace: org/repo]/compare/session-NN-pre...session-NN-post`

## Próxima sesión

- [Replace: Session NN+1 — slug + fecha + 1-liner de qué va a pasar]
- Prereqs adicionales: [Replace: "ninguno más allá de SETUP.md" o listar adiciones]

## Lecturas

Links relevantes para profundizar en el tema de esta sesión:

- [Replace: link 1 — docs oficial / blog post / artículo canónico]
- [Replace: link 2]

<!-- see QUAL-GATES.md §QUAL-06 para convención bilingüe; §QUAL-02 para fallback artifact link -->
```

**Constraints per D-02 + PATTERNS:**
- **Exactly 5 H2 section headings** in this order: `## ¿Qué vimos?`, `## Comandos para probar`, `## Link de comparación`, `## Próxima sesión`, `## Lecturas`. VALIDATION.md SCAF-10 row greps each.
- **No generic sample bullets** (D-02 explicitly rejects). Every list item is a `[Replace: ...]` placeholder.
- **Spanish section titles are LOCKED** — presenters must NOT rename them (greppers depend on exact text).
- **Prose is Spanish; bash code blocks are English** — matches convention.
- **Footer cites QUAL-06 and QUAL-02 by ID** per D-04 cross-ref convention.
  </action>
  <verify>
    <automated>test -f docs/presentations/HANDOUT.template.md && grep -q "¿Qué vimos?" docs/presentations/HANDOUT.template.md && grep -q "Comandos para probar" docs/presentations/HANDOUT.template.md && grep -q "Link de comparación" docs/presentations/HANDOUT.template.md && grep -q "Próxima sesión" docs/presentations/HANDOUT.template.md && grep -q "Lecturas" docs/presentations/HANDOUT.template.md</automated>
  </verify>
  <acceptance_criteria>
    - `test -f docs/presentations/HANDOUT.template.md` succeeds
    - VALIDATION.md SCAF-10 row (all 5 sections): all 5 grep commands succeed simultaneously — `¿Qué vimos?`, `Comandos para probar`, `Link de comparación`, `Próxima sesión`, `Lecturas`
    - Sections at H2 level: `grep -c "^## " docs/presentations/HANDOUT.template.md` returns `5`
    - Placeholder-annotated (D-02 + D-01): `grep -c "\[Replace:" docs/presentations/HANDOUT.template.md` returns `≥ 7`
    - No sample bullets (D-02): `grep -v "^-" docs/presentations/HANDOUT.template.md | grep -c "^-"` returns `0` — this is a tautological check; more practically, every `^- ` bullet should contain a `[Replace:` prompt: `grep "^- " docs/presentations/HANDOUT.template.md | grep -vc "\[Replace:\|Pre →"` should return `0` (only the compare URL bullet is exempt because its placeholder is inline in the URL)
    - QUAL-06 cross-ref present: `grep -q "QUAL-06" docs/presentations/HANDOUT.template.md` succeeds
    - Spanish prose (no English sample text): `grep -q "bullet 1\|bullet 2\|sample" docs/presentations/HANDOUT.template.md` — DOES match `bullet 1` as placeholder prompt, which is OK (placeholder language); critical check is that no real English prose content exists outside `[Replace:]` brackets
  </acceptance_criteria>
  <done>
    `docs/presentations/HANDOUT.template.md` exists with exactly the 5 Spanish section headings locked by D-02, every fillable slot as `[Replace: ...]` prompt (no sample bullets), QUAL-06 cross-ref in footer.
  </done>
</task>

</tasks>

<threat_model>
No security-sensitive surface — docs-only artifact. Templates produce per-session sidecar docs; QUAL-GATES.md is a reference doc. None introduce code, config, or data that changes runtime behavior.
</threat_model>

<verification>
Run all VALIDATION.md rows for SCAF-08, SCAF-09, SCAF-10, and QUAL-01..12:

```bash
# QUAL-GATES.md — all 12 gate IDs present
for n in 01 02 03 04 05 06 07 08 09 10 11 12; do
  grep -q "QUAL-$n" docs/presentations/QUAL-GATES.md || echo "MISSING: QUAL-$n"
done

# SCAF-08 MANIFEST.template.md placeholder fields
grep -q "session-NN-pre" docs/presentations/MANIFEST.template.md
grep -q "session-NN-post" docs/presentations/MANIFEST.template.md
grep -q "Slide.*commit" docs/presentations/MANIFEST.template.md
grep -q "Recovery" docs/presentations/MANIFEST.template.md
grep -q "compare" docs/presentations/MANIFEST.template.md
grep -qi "version" docs/presentations/MANIFEST.template.md

# SCAF-09 REHEARSAL.template.md checklist + QUAL refs
test $(grep -c "^- \[ \]" docs/presentations/REHEARSAL.template.md) -ge 5
grep -q "QUAL-02" docs/presentations/REHEARSAL.template.md
grep -q "QUAL-03" docs/presentations/REHEARSAL.template.md
grep -q "QUAL-09" docs/presentations/REHEARSAL.template.md

# SCAF-10 HANDOUT.template.md 5 Spanish sections
grep -q "¿Qué vimos?" docs/presentations/HANDOUT.template.md
grep -q "Comandos para probar" docs/presentations/HANDOUT.template.md
grep -q "Link de comparación" docs/presentations/HANDOUT.template.md
grep -q "Próxima sesión" docs/presentations/HANDOUT.template.md
grep -q "Lecturas" docs/presentations/HANDOUT.template.md
```

All must pass (no MISSING output; all greps exit 0).
</verification>

<success_criteria>
4 new files exist at `docs/presentations/` root. Every QUAL gate (QUAL-01..QUAL-12) is documented once in QUAL-GATES.md and referenced by ID from templates. The three sidecar templates contain placeholder-annotated skeletons so a Session NN presenter can copy them into the session folder, fill in every `[Replace:]` prompt, and end up with QUAL-compliant sidecars by construction. Sessions 1-9 inherit these templates — no per-session re-authoring.
</success_criteria>

<output>
After completion, create `.planning/phases/00-series-scaffolding/00-05-SUMMARY.md` recording:
- File inventory (4 files: QUAL-GATES.md + 3 `.template.md`)
- Count of `[Replace:]` placeholders per template
- Count of `^- \[ \]` checklist items in REHEARSAL (expected: 9)
- QUAL gate IDs referenced from each template
</output>
