---
phase: 00-series-scaffolding
plan: 06
type: execute
wave: 1
depends_on: []
files_modified:
  - docs/presentations/THEME.md
  - docs/presentations/CLAUDE.md
autonomous: true
requirements:
  - SCAF-03
  - SCAF-04
tags:
  - documentation
  - marp
  - conventions
user_setup: []

must_haves:
  truths:
    - "docs/presentations/THEME.md has a single copy-pasteable Marp frontmatter block that every session deck copies verbatim at the top of its .md source"
    - "THEME.md documents light (default) and dark (diagrams with light lines) variations"
    - "docs/presentations/CLAUDE.md is EXTENDED (not rewritten) — existing Marp/Mermaid/Language sections preserved; 5 new sections appended"
    - "The 5 new CLAUDE.md sections are: Convención de numeración NN-slug, Sidecar per sesión, Git tags session-NN-pre/post, Fallback artifact (QUAL-02), QUAL gates (links to QUAL-GATES.md)"
    - "Embedded reference-only glossary rule (D-10) is documented inside the CLAUDE.md extension so session authors know to embed ../GLOSSARY/primitive.svg and link Ver GLOSSARY.html primitive instead of copying text"
  artifacts:
    - path: "docs/presentations/THEME.md"
      provides: "Canonical Marp frontmatter block + two theme variations"
      contains: "marp: true"
    - path: "docs/presentations/CLAUDE.md"
      provides: "Extended authoring conventions with NN-infix, sidecars, tags, fallback, QUAL link"
      contains: "Convención de numeración"
  key_links:
    - from: "docs/presentations/CLAUDE.md QUAL gates section"
      to: "docs/presentations/QUAL-GATES.md"
      via: "relative link at series root"
      pattern: "QUAL-GATES.md"
    - from: "docs/presentations/CLAUDE.md Sidecar per sesión section"
      to: "docs/presentations/MANIFEST HANDOUT REHEARSAL template.md files"
      via: "relative path reference"
      pattern: "template.md"
    - from: "Session 1-9 deck .md files (future)"
      to: "docs/presentations/THEME.md frontmatter block"
      via: "copy-paste (Marp has no include system)"
      pattern: "theme: gaia"
---

<objective>
Author the canonical Marp theme snippet (`THEME.md`) and extend the existing `docs/presentations/CLAUDE.md` with the series-level conventions that govern Phases 1-9. Bundled into one plan because both are tiny docs-about-docs and both land on `docs/presentations/` root.

**Purpose:**
- **SCAF-03 (THEME.md):** Every session deck copies the frontmatter verbatim. D-04's Claude's-Discretion default is a single copy-pasteable block (Marp has no include system per research-verified Context7 check). Two named variations: light (default) + dark (for decks with light-line diagrams).
- **SCAF-04 (CLAUDE.md extension):** The existing 57-line file already covers Marp/Mermaid/Language — those sections are load-bearing anchors (cited by research/ARCHITECTURE.md + STACK.md). This plan APPENDS new H2 sections covering: NN-infix naming, sidecar templates, git tags, fallback artifact, glossary embed rule, QUAL gates link.

**Output:** One new file (`docs/presentations/THEME.md`) + one extended file (`docs/presentations/CLAUDE.md`). The existing 57-line content of CLAUDE.md is preserved verbatim; new content appended at the end.
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
@./CLAUDE.md
</context>

<tasks>

<task type="auto" tdd="false">
  <name>Task 1: Author docs/presentations/THEME.md with canonical Marp frontmatter block + light/dark variations</name>
  <files>docs/presentations/THEME.md</files>
  <read_first>
    - docs/presentations/2026-04-10-ai-driven-development/2026-04-10-ai-driven-development.md (lines 1-10 — light frontmatter exact block — this is the CANONICAL SOURCE to copy into THEME.md)
    - docs/presentations/2026-04-08-mas-alla-del-hype/2026-04-08-mas-alla-del-hype.md (lines 1-10 — dark variation)
    - .planning/phases/00-series-scaffolding/00-CONTEXT.md §"Claude's Discretion → THEME.md shape" — single copy-pasteable block default
    - .planning/phases/00-series-scaffolding/00-RESEARCH.md §"Marp Theming (SCAF-03)" — Context7-verified Marp CLI behavior, Pitfall 5 (variations), exact frontmatter block literal text in §"Concrete pattern for THEME.md"
    - .planning/phases/00-series-scaffolding/00-PATTERNS.md §"docs/presentations/THEME.md (theme snippet)" — exact frontmatter block from analog
    - .planning/phases/00-series-scaffolding/00-VALIDATION.md (SCAF-03 row — grep for `marp: true` and `theme: gaia`)
  </read_first>
  <action>
Create `docs/presentations/THEME.md`. Per CONTEXT Claude's-Discretion default, this is a single copy-pasteable Marp frontmatter block at the top, followed by two named variations.

**Structure required** (Spanish prose unless noted; English code):

1. **Title:** `# Marp Theme — Canonical frontmatter`

2. **Intro paragraph (Spanish):** Explain that every session deck copies this block verbatim; do not modify. One sentence about why copy-paste instead of `--theme-set`: Marp has no Markdown include system (verified via Context7 `/marp-team/marp-cli` 2026-04); trade-off accepted for 9 sessions; revisit in v2 if arc grows to 20+.

3. **H2 section `## Bloque canónico (light — default)`:** A ```yaml fenced code block containing EXACTLY this Marp frontmatter content (copied from the analog file `docs/presentations/2026-04-10-ai-driven-development/2026-04-10-ai-driven-development.md` lines 1-10, with `title` replaced by a placeholder):

   - opening three-dash YAML delimiter line
   - `marp: true`
   - `theme: gaia`
   - `class: lead`
   - `paginate: true`
   - `backgroundColor: "#fff"`
   - `color: "#1e1e2e"`
   - `title: "[Replace: Session NN — Título en español]"` (placeholder for session)
   - `author: "Pablo Martínez"` (locked — constant from both existing decks)
   - closing three-dash YAML delimiter line

4. **H3 section `### Reglas`:** bullet list in Spanish explaining each field:
   - `marp: true` activa rendering
   - `theme: gaia` built-in de Marp — los 2 decks pre-serie lo usan; NO substituir por CSS custom en el frontmatter — Marp CLI resuelve `theme:` por **nombre**, no por path
   - `class: lead` aplica al slide de portada; slides individuales pueden sobreescribir con `<!-- _class: lead -->` (patrón ya en uso)
   - `paginate: true` numera las slides; excepción: GLOSSARY.md usa `paginate: false` (Pitfall 9)
   - `backgroundColor` + `color`: light default `#fff` + `#1e1e2e`
   - `title`: placeholder `[Replace: ...]` llenado por sesión en español
   - `author: "Pablo Martínez"` locked del arco

5. **H2 section `## Variación: dark (para decks con diagramas de líneas claras)`:** Spanish intro explaining when to use dark. Then a ```yaml code block containing ONLY these two lines (override of the canonical block):
   - `backgroundColor: "#1e1e2e"`
   - `color: "#cdd6f4"`

   Then a Spanish paragraph explaining "reemplaza solo esas dos líneas del bloque canónico; resto queda igual; deck pre-serie `2026-04-08-mas-alla-del-hype.md` usa esta variación".

6. **H2 section `## Pitfalls`:** 3 Spanish bullet points (the three "don'ts" from research):
   - NO apuntes `theme:` a un path relativo — Marp CLI resuelve theme **names**, no paths. Paths van por `--theme` o `--theme-set` flag
   - NO confíes en runtime Mermaid JS — pre-rendered SVG es la regla; HTMLPreview reescribe `<script>` tags
   - NO agregues `html: true` al frontmatter — el flag `--html` al render se encarga; los 2 decks pre-serie renderan sin `html:` en frontmatter

7. **H2 section `## Referencias`:** 4 bullets with relative links:
   - Marp CLI docs: `npx @marp-team/marp-cli@4.3.1 --help`
   - `docs/presentations/CLAUDE.md` (convenciones render)
   - Deck de referencia light: `docs/presentations/2026-04-10-ai-driven-development/`
   - Deck de referencia dark: `docs/presentations/2026-04-08-mas-alla-del-hype/`

**Constraints (per CONTEXT + RESEARCH + PATTERNS):**
- Single copy-pasteable YAML block at the top (Claude's-Discretion default + D-04 style).
- Two named variations (light default + dark) — Pitfall 5 prevention (line invisibility on wrong bg).
- `marp: true` and `theme: gaia` are non-negotiable (VALIDATION.md SCAF-03 row greps both verbatim).
- `title: "[Replace: ...]"` placeholder language — each session fills in.
- `author: "Pablo Martínez"` is locked (research verified both existing decks use this constant).
- NO `html: true` in frontmatter — verified against existing decks by research.
- Pitfalls section names the three "don'ts" from research (paths in `theme:`, runtime Mermaid, `html:`).
  </action>
  <verify>
    <automated>test -f docs/presentations/THEME.md && grep -q "marp: true" docs/presentations/THEME.md && grep -q "theme: gaia" docs/presentations/THEME.md</automated>
  </verify>
  <acceptance_criteria>
    - `test -f docs/presentations/THEME.md` succeeds
    - VALIDATION.md SCAF-03 row: `grep -q "marp: true" docs/presentations/THEME.md && grep -q "theme: gaia" docs/presentations/THEME.md` succeeds
    - Light + dark variation both present: `grep -c "backgroundColor" docs/presentations/THEME.md` returns `≥ 2` (default + dark override)
    - `class: lead` documented: `grep -q "class: lead" docs/presentations/THEME.md` succeeds
    - `paginate: true` default + `paginate: false` exception noted: `grep -q "paginate: true" docs/presentations/THEME.md && grep -q "paginate: false" docs/presentations/THEME.md` succeeds
    - Author locked: `grep -q 'author: "Pablo Martínez"' docs/presentations/THEME.md` succeeds
    - Pitfalls section names `--theme-set`: `grep -q "theme-set\|--theme" docs/presentations/THEME.md` succeeds
    - Spanish prose in sections outside the YAML block: `grep -q "Copia este bloque\|Variación\|Pitfalls\|Referencias" docs/presentations/THEME.md` succeeds
  </acceptance_criteria>
  <done>
    `docs/presentations/THEME.md` exists with canonical light frontmatter + dark variation + pitfalls + references. Copy-pasteable by session authors without ambiguity.
  </done>
</task>

<task type="auto" tdd="false">
  <name>Task 2: Extend docs/presentations/CLAUDE.md with new H2 sections (append — do not rewrite existing content)</name>
  <files>docs/presentations/CLAUDE.md</files>
  <read_first>
    - docs/presentations/CLAUDE.md (existing 57-line file — current state; sections: Folder structure, Marp build, Diagrams (Mermaid), Language) — READ FIRST to confirm current state before appending
    - .planning/phases/00-series-scaffolding/00-CONTEXT.md §"canonical_refs → docs/presentations/CLAUDE.md" — extends-not-replaces rule
    - .planning/phases/00-series-scaffolding/00-RESEARCH.md §"CLAUDE.md Extension Strategy (SCAF-04)" — proposed extension pattern with verbatim section content, Pitfalls (don't rewrite existing)
    - .planning/phases/00-series-scaffolding/00-PATTERNS.md §"docs/presentations/CLAUDE.md (mutate — append sections)" — existing H2 headings that must stay anchored + new H2 headings to append
    - .planning/phases/00-series-scaffolding/00-VALIDATION.md (SCAF-04 row — grep for session-NN-pre, MANIFEST.md, REHEARSAL.md, HANDOUT.md, asciinema/VHS)
  </read_first>
  <action>
Extend the existing `docs/presentations/CLAUDE.md`. **Do NOT rewrite existing content** — preserve the existing 4 H2 sections verbatim (Folder structure, Marp build, Diagrams (Mermaid), Language) and their H3 subsections (Workflow, Why pre-rendered SVG). Those are anchor points cited by research/ARCHITECTURE.md + STACK.md.

**Appending strategy:** Use Edit tool (or equivalent) to add content AT THE END of the existing file. Preserve everything in the current 57-line file; append new H2 sections below.

**New H2 sections to append (in this order), with Spanish prose and mixed-language code:**

### Section A: `## Convención de numeración NN-<slug>`

Short Spanish paragraph explaining that since AI-SWE 2026 series, numbered presentations follow `YYYY-MM-DD-NN-<slug>/` format where `NN` is session number in arc (`01`..`09`).

Two bullets:
- `NN` es **zero-padded** — `01`, `02`, ..., `09`, never `1`, `2`, ... `9`.
- `<slug>` es kebab-case en español — e.g., `demo-primero`, `intro-contexto-llms`, `rag`, `mcp`, `skills`, `agents`, `hooks`, `slash-commands`, `capstone`.

Closing paragraph: los decks pre-serie (`2026-04-08-mas-alla-del-hype/`, `2026-04-10-ai-driven-development/`) se quedan **sin NN** — material crudo, no parte del arco, no retroactivamente numerar.

### Section B: `## Sidecar per sesión`

Short Spanish intro: cada sesión numerada contiene 3 archivos sidecar además del deck.

3 bullets:
- `MANIFEST.md` — mapa slide→commit, SHAs de tags pre/post, comando de recovery, version pins
- `HANDOUT.md` — takeaway corto para audiencia (5 secciones fijas en español)
- `REHEARSAL.md` — bitácora del dry-run: checklist QUAL + timing + cortes + flakes

Template location paragraph: los templates canónicos viven en `../MANIFEST.template.md`, `../HANDOUT.template.md`, `../REHEARSAL.template.md`. Al iniciar el plan-phase de una sesión, se copian los 3 templates al folder nuevo `YYYY-MM-DD-NN-<slug>/` con los nombres finales (`MANIFEST.md`, `HANDOUT.md`, `REHEARSAL.md`) y se rellenan los placeholders `[Replace: ...]`.

### Section C: `## Git tags: session-NN-pre / session-NN-post`

Spanish intro: antes de cada sesión en vivo, crear tag pre con bash:

A ```bash fenced block with:
- `git tag -a session-NN-pre -m "Session NN — Pre-demo state"`
- `git push origin session-NN-pre`

Spanish middle paragraph: después de la sesión, crear tag post con bash:

A ```bash fenced block with:
- `git tag -a session-NN-post -m "Session NN — Post-demo state"`
- `git push origin session-NN-post`

Closing Spanish paragraph: son **annotated** tags, **no movibles**. Re-teaches a otra cohort usan branches fuera de `session-NN-pre` (e.g., `replay/session-03-cohort-N`), NO se mueve el tag. Esta convención permite que cualquiera clonando `session-NN-post` reproduzca la sesión años después (CURR-04).

### Section D: `## Fallback artifact (QUAL-02)`

Spanish intro: cada sesión DEBE tener un artefacto de fallback pre-grabado junto al deck.

2 bullets:
- **asciinema** (`.cast`) — captura en vivo del demo; reproducible con `asciinema play`.
- **VHS** (`.tape`) — script determinístico con output reproducible; `vhs < file.tape`.

Middle paragraph: el artefacto vive en el mismo folder de la sesión (e.g., `docs/presentations/2026-MM-DD-01-demo-primero/demo.cast`).

Closing paragraph: el switchover live → fallback se **ensaya al menos una vez** en REHEARSAL.md — si la demo en vivo flakea, el presentador conoce el muscle memory para pausar, abrir el fallback, y continuar sin perder el budget de 57 minutos.

### Section E: `## Embed del glosario (CURR-03 drift prevention)`

Spanish intro: cuando una sesión referencia una primitiva (RAG, MCP, Skill, Agent, Hook, Slash Command), **NO copia la definición** del glosario canónico. Embebe el diagrama y linkea a la definición.

Example block (```markdown fence) showing:
- `![RAG](../GLOSSARY/rag.svg)`
- blank line
- `> Ver \`GLOSSARY.html §RAG\` para la definición completa.`

Closing Spanish paragraph: el texto del glosario vive **una sola vez** en `docs/presentations/GLOSSARY/GLOSSARY.md`. Los diagramas viven **una sola vez** en `docs/presentations/GLOSSARY/<primitive>.svg`. Las sesiones apuntan — nunca duplican. Drift es estructuralmente imposible.

### Section F: `## QUAL gates`

Spanish intro: cada sesión debe cumplir las 12 reglas QUAL (QUAL-01..QUAL-12).

Single bullet:
- `../QUAL-GATES.md` — la doc de referencia autoritativa

Closing Spanish paragraph: los templates (`MANIFEST.template.md`, `HANDOUT.template.md`, `REHEARSAL.template.md`) enforzan los gates via checklist items o campos requeridos. `/gsd-verify-work` corre greps contra la sesión para confirmar compliance. No duplicar el texto de los gates en los sidecars — linkear por ID (e.g., `see QUAL-GATES.md §QUAL-04`).

**Constraints (per SCAF-04 + PATTERNS + RESEARCH Pitfalls):**
- **Append only — do NOT modify existing sections.** The file's current H2 headings (`## Folder structure`, `## Marp build`, `## Diagrams (Mermaid)`, `## Language`) are cited by other planning docs; breaking their line numbers causes link rot.
- **6 new H2 sections** appended in exact order A-F (as specified above): Convención NN-slug, Sidecar per sesión, Git tags, Fallback artifact, Embed del glosario, QUAL gates.
- **Bilingual convention** — new headings can be Spanish (matches "Language" existing style); inline code bash/English; prose Spanish-leaning but mixed allowed (matches existing file ratio per PATTERNS).
- **Link to `../QUAL-GATES.md`** at the end (forward-valid — plan 05 authors it in the same wave).
- **Link to sibling templates** `../MANIFEST.template.md` etc. (forward-valid — plan 05).
  </action>
  <verify>
    <automated>grep -q "session-NN-pre" docs/presentations/CLAUDE.md && grep -q "MANIFEST.md" docs/presentations/CLAUDE.md && grep -q "REHEARSAL.md" docs/presentations/CLAUDE.md && grep -q "HANDOUT.md" docs/presentations/CLAUDE.md && grep -q "asciinema\|VHS" docs/presentations/CLAUDE.md</automated>
  </verify>
  <acceptance_criteria>
    - VALIDATION.md SCAF-04 row: `grep -q "session-NN-pre" docs/presentations/CLAUDE.md && grep -q "MANIFEST.md" docs/presentations/CLAUDE.md && grep -q "REHEARSAL.md" docs/presentations/CLAUDE.md && grep -q "HANDOUT.md" docs/presentations/CLAUDE.md && grep -q "asciinema\|VHS" docs/presentations/CLAUDE.md` succeeds (all 5 greps pass)
    - Existing content preserved — count of H2 headings is now old (4) + new (6) = 10: `grep -c "^## " docs/presentations/CLAUDE.md` returns `10`
    - Existing anchor headings still present: `grep -c "^## Folder structure$\|^## Marp build$\|^## Diagrams (Mermaid)$\|^## Language$" docs/presentations/CLAUDE.md` returns `4` (none renamed, none deleted)
    - Existing H3 subsections preserved: `grep -c "^### Workflow$\|^### Why pre-rendered SVG$" docs/presentations/CLAUDE.md` returns `2`
    - New section headings present in order: `grep -n "^## " docs/presentations/CLAUDE.md` shows `Folder structure`, `Marp build`, `Diagrams`, `Language` FIRST (in original order), then the 6 new sections after them
    - QUAL-GATES.md link present: `grep -q "QUAL-GATES.md" docs/presentations/CLAUDE.md` succeeds
    - Embed del glosario section (D-10 + CURR-03): `grep -q "../GLOSSARY/" docs/presentations/CLAUDE.md && grep -q "drift\|CURR-03" docs/presentations/CLAUDE.md` succeeds
    - Existing Marp render command block still works: `grep -q "npx @marp-team/marp-cli@latest --html" docs/presentations/CLAUDE.md` succeeds (pre-existing line 21 untouched)
  </acceptance_criteria>
  <done>
    `docs/presentations/CLAUDE.md` extended with 6 new H2 sections appended after the existing 4; existing content preserved verbatim; all 5 VALIDATION.md SCAF-04 greps pass; ../QUAL-GATES.md and ../GLOSSARY/ references present.
  </done>
</task>

</tasks>

<threat_model>
No security-sensitive surface — docs-only artifact. THEME.md is a static YAML snippet; CLAUDE.md extension adds documentation for series conventions. Neither introduces runtime code nor changes existing security posture.
</threat_model>

<verification>
Run all VALIDATION.md SCAF-03 + SCAF-04 rows:

```bash
# SCAF-03 THEME.md
test -f docs/presentations/THEME.md
grep -q "marp: true" docs/presentations/THEME.md
grep -q "theme: gaia" docs/presentations/THEME.md

# SCAF-04 CLAUDE.md extension — all 5 greps
grep -q "session-NN-pre" docs/presentations/CLAUDE.md
grep -q "MANIFEST.md" docs/presentations/CLAUDE.md
grep -q "REHEARSAL.md" docs/presentations/CLAUDE.md
grep -q "HANDOUT.md" docs/presentations/CLAUDE.md
grep -q "asciinema\|VHS" docs/presentations/CLAUDE.md

# Anchor preservation (not in VALIDATION.md but critical)
grep -c "^## Folder structure$" docs/presentations/CLAUDE.md   # = 1
grep -c "^## Marp build$" docs/presentations/CLAUDE.md          # = 1
grep -c "^## Language$" docs/presentations/CLAUDE.md            # = 1
```

All must pass.
</verification>

<success_criteria>
Session authors (Phases 1-9) have a single Marp theme to copy-paste (`THEME.md`) and a single conventions doc (extended `CLAUDE.md`) documenting NN-infix naming, sidecars, git tags, fallback artifact, glossary embed rule, and QUAL gates link. Existing 57-line CLAUDE.md content preserved — no link rot.
</success_criteria>

<output>
After completion, create `.planning/phases/00-series-scaffolding/00-06-SUMMARY.md` recording:
- THEME.md final line count + whether dark variation present
- CLAUDE.md before/after H2 count (expected: 4 → 10)
- Existing H2 headings preserved (listed verbatim)
- New H2 headings appended (listed in order)
</output>
