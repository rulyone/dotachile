# AI Driven Development Presentation — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Write a Marp presentation (`docs/presentations/2026-04-10-ai-driven-development.md`) that walks a developer workshop through three chained live demos: RAG discovery → Plane ticket via Skills+MCP → implementation via Superpowers.

**Architecture:** Single Marp markdown file. Gaia theme, light background, paginated. Speaker notes in HTML comments. ~23 slides across 5 sections. Based on the style and tone of the existing deck at `docs/presentations/2026-04-08-mas-alla-del-hype.md`.

**Tech Stack:** Marp (Markdown Presentation Ecosystem), Gaia theme.

---

## Reference: existing deck conventions

The reference deck (`docs/presentations/2026-04-08-mas-alla-del-hype.md`) uses:

- Frontmatter: `marp: true`, `theme: gaia`, `class: lead`, `paginate: true`, custom `backgroundColor`/`color`
- Section divider slides: `<!-- _class: lead -->` before `#` + `##` headings
- Speaker notes: `<!-- ... -->` blocks after slide content, addressed to the presenter ("Presentador:", "Mencionar que...", "ALT-TAB AL TERMINAL")
- Blockquotes (`>`) for memorable one-liners per slide
- Code fences for terminal examples
- Bullet points kept short (one line each)

For the **light theme**, replace the dark colors:
- `backgroundColor: "#fff"` (white)
- `color: "#1e1e2e"` (dark text — reuse the old background as text color)

---

### Task 1: Create the file with frontmatter + Sección 1 (Apertura)

**Files:**
- Create: `docs/presentations/2026-04-10-ai-driven-development.md`

- [ ] **Step 1: Write frontmatter + slide 1 (título)**

```markdown
---
marp: true
theme: gaia
class: lead
paginate: true
backgroundColor: "#fff"
color: "#1e1e2e"
title: "AI Driven Development — De la idea al commit en una sesión"
author: "Pablo Martínez"
---

<!-- _class: lead -->

# AI Driven Development

## De la idea al commit en **una sesión**

### Vamos a comprobarlo.

Pablo Martínez · Workshop · 2026-04-10

<!--
Bienvenida breve. "Hoy vamos a hacer algo distinto: en vez de hablar sobre
lo que la IA puede hacer, vamos a abrir un terminal y conducir un ciclo
completo de desarrollo — desde descubrir qué construir hasta hacer el commit.
Tres demos, un solo flujo, todo real."
-->
```

- [ ] **Step 2: Write slide 2 (tesis)**

```markdown
---

# ¿Qué es AI Driven Development?

- **No es** "pídele a la IA que escriba código y copia-pega"
- **Sí es:** la IA conduce el **ciclo completo** — descubrir, planificar, construir
- Hoy lo hacemos en vivo: 3 demos encadenados, cada uno alimenta al siguiente
- Al final hay commits, tickets, y specs reales que puedes ir a revisar

<!--
Plantar la tesis: AI Driven Development es un cambio de mentalidad, no una
herramienta. La IA no es tu autocomplete — es tu copiloto en todo el ciclo.
El flujo de hoy: Descubrir (RAG) → Planificar (Skills + MCP) → Construir
(Superpowers: metodología y/o framework). Todo conectado.
-->
```

- [ ] **Step 3: Write slide 3 (setup)**

```markdown
---

# Antes de empezar: ¿qué necesito?

1. **Terminal** con `claude` (Claude Code CLI) corriendo en este repo
2. **Plane** local (`http://localhost/dotachile`) — para la demo 2 ("JIRA" open source)
3. **Editor favorito** — solo para mirar diffs, no vamos a tipear casi nada
4. El repo `dotachile` clonado y `docker compose up -d` listo
5. El corpus de correos de la casilla dotachile.com@gmail.com
```

- [ ] **Step 4: Verify Marp renders**

Run: `npx @marp-team/marp-cli@latest docs/presentations/2026-04-10-ai-driven-development.md -o /tmp/ai-driven-dev-check.html`
Expected: HTML file generated without errors. Open to verify 3 slides render with light background.

- [ ] **Step 5: Commit**

```bash
git add docs/presentations/2026-04-10-ai-driven-development.md
git commit -m "docs(presentations): add AI Driven Development deck — apertura"
```

---

### Task 2: Sección 2 — Demo 1: Descubrir (RAG)

**Files:**
- Modify: `docs/presentations/2026-04-10-ai-driven-development.md` (append)

- [ ] **Step 1: Write section divider slide**

Append to the file:

```markdown
---

<!-- _class: lead -->

# Demo 1
## Descubrir qué construir

**RAG + búsqueda híbrida**
```

- [ ] **Step 2: Write "RAG en 1 slide"**

```markdown
---

# RAG en 1 slide

- **Retrieval Augmented Generation**: dale a la IA contexto que NO está en el código
- **No es** fine-tuning. **No es** memoria infinita.
- **Sí es:** un índice (BM25 + embeddings semánticos en este caso) que devuelve los contextos relevantes
- La IA solo agrega los chunks que la búsqueda recuperó en su `context window` — no todo el corpus

> RAG = búsqueda inteligente + un LLM que sabe leer los resultados.

<!--
Aclaración importante: la gente confunde RAG con fine-tuning o con "Claude
con memoria infinita". Ninguna de las dos. RAG es búsqueda. La magia está
en que la búsqueda híbrida (léxica + semántica) encuentra cosas que un grep
o un simple CTRL + F no encontraría.
-->
```

- [ ] **Step 3: Write "El corpus de dotachile"**

```markdown
---

# El corpus de dotachile

- **Cientos de threads** de mail histórico de `dotachile.com@gmail.com`
- Construido desde **Gmail Takeout** + un pipeline de redacción de PII
- Indexado con **BM25 + embeddings multilingües** (ES + EN)
- Consulta: `tools/email-rag/search.py "<query en español>"`

<!--
Mencionar la privacidad: el corpus está PII-redactado ([REDACTED_PERSON],
USER_NNNN, etc) y el archivo mbox vive en un sparse bundle encriptado que NO
se monta en sesiones normales. Nadie está filtrando datos personales a Claude.
-->
```

- [ ] **Step 4: Write slide "El demo"**

```markdown
---

# El demo: top 3 issues del corpus

- **Mi pregunta:** "busca en los emails los 3 problemas más mencionados que hoy podríamos arreglar"
- **Lo que hace Claude:**
  1. Llama a `search.py` con queries en español
  2. Lee los hilos más relevantes
  3. Agrupa, prioriza, propone 3 ideas concretas

<!--
ALT-TAB AL TERMINAL. Steerear la query hacia temas donde el corpus tiene
yield: cuentas, registro, soporte, organización de torneos por cibercafés.
NO preguntar "bugs del ladder" — eso vivía en la base de datos, no en mail.
-->
```

- [ ] **Step 5: Write recap + selection slide**

```markdown
---

# Lo que acabamos de ver

- El corpus tenía **contexto que ni la DB ni el git history tienen**: voz de usuarios reales
- La búsqueda híbrida encontró threads donde la palabra exacta NO aparecía
- Claude leyó los threads completos antes de proponer
- **Las 3 ideas son accionables hoy** — vamos a convertir una en ticket ahora mismo

> Esto es lo que distingue RAG de "ctrl+F sobre un mbox".

<!--
Transición a Demo 2: "de estas 3 ideas, vamos a elegir una y convertirla
en un ticket bien escrito en Plane, sin abrir el browser."
-->
```

- [ ] **Step 6: Verify renders**

Run: `npx @marp-team/marp-cli@latest docs/presentations/2026-04-10-ai-driven-development.md -o /tmp/ai-driven-dev-check.html`
Expected: 8 slides total, no render errors.

- [ ] **Step 7: Commit**

```bash
git add docs/presentations/2026-04-10-ai-driven-development.md
git commit -m "docs(presentations): add Demo 1 — RAG discovery section"
```

---

### Task 3: Sección 3 — Demo 2: Planificar (Skills + MCP)

**Files:**
- Modify: `docs/presentations/2026-04-10-ai-driven-development.md` (append)

- [ ] **Step 1: Write section divider slide**

Append to the file:

```markdown
---

<!-- _class: lead -->

# Demo 2
## Planificar con precisión

**Skills + MCP**
```

- [ ] **Step 2: Write "MCP en 1 slide"**

```markdown
---

# MCP en 1 slide

- **Model Context Protocol** — el estándar abierto para que las IA hablen con herramientas externas
- "Tools" expuestas por un servidor MCP: Plane, GitHub, Postgres, Slack, lo que sea
- Claude las llama como funciones; los resultados vuelven al contexto
- **Tú ya tienes uno:** el servidor MCP de Plane corre en tu localhost y Claude lo usa

> Sin MCP, Claude te dice "abre Plane y crea un ticket". Con MCP, lo crea él.

<!--
Mencionar que MCP es un spec abierto, no propietario de Anthropic. Hay
decenas de servidores MCP comunitarios. El de Plane lo configuramos
previamente — hoy lo usamos.
-->
```

- [ ] **Step 3: Write "Skills en 1 slide"**

```markdown
---

# Skills en 1 slide

- Un **skill** es un manual que Claude carga a demanda
- Vienen con Claude Code, con Superpowers, o los escribes tú
- Hoy usamos dos de la comunidad:
  - **PRD Generator** — genera un documento de requisitos completo
  - **user-story** — formato PM: "Como X quiero Y para Z" + criterios de aceptación + edge cases

> Skills > prompts largos. El skill se versiona, se comparte, se mejora.

<!--
Los skills son el "npm de conocimiento" para Claude. En vez de escribir un
prompt de 200 líneas cada vez, empaquetas el conocimiento en un skill y lo
invocas. Hoy vamos a ver dos en acción, uno encima del otro.
-->
```

- [ ] **Step 4: Write demo slide**

```markdown
---

# El demo: de idea a ticket sin abrir el browser

- **Input mío:** "crea un ticket para [el issue que elegimos]"
- **Lo que hace Claude:**
  1. Carga el skill **PRD Generator** → genera un PRD estructurado
  2. Carga el skill **user-story** → refina en formato de ticket con criterios de aceptación
  3. Llama al **MCP de Plane** → crea el ticket directamente
- **Yo nunca abro el browser.** Reviso el resultado en Plane al final.

<!--
ALT-TAB AL TERMINAL. La clave de esta demo es la composición: dos skills
dan la forma (PRD + user story) y MCP lo materializa. Si solo tuviera MCP,
el ticket sería un párrafo de texto. Si solo tuviera skills, tendría que
copy-pastear yo mismo a Plane.
-->
```

- [ ] **Step 5: Write recap slide**

```markdown
---

# Lo que acabamos de ver

- **PRD Generator dictó la *estructura*** — requisitos, contexto, alcance
- **user-story dictó el *formato*** — criterios de aceptación, edge cases, tono PM
- **MCP lo *creó*** en Plane sin que yo tocara el browser
- **Yo revisé** — sigo siendo el filtro humano, pero saltándome el typing

> Composición: skill + skill + MCP > cualquiera por separado.

<!--
Si hay PMs en la audiencia: esto NO los reemplaza — los libera de formatear.
Su trabajo sigue siendo decidir qué construir. Lo que cambia es que la
barrera de costo entre "tengo la idea" y "tengo el ticket bien escrito" baja
a cero.
-->
```

- [ ] **Step 6: Verify renders**

Run: `npx @marp-team/marp-cli@latest docs/presentations/2026-04-10-ai-driven-development.md -o /tmp/ai-driven-dev-check.html`
Expected: 13 slides total, no render errors.

- [ ] **Step 7: Commit**

```bash
git add docs/presentations/2026-04-10-ai-driven-development.md
git commit -m "docs(presentations): add Demo 2 — Skills + MCP planning section"
```

---

### Task 4: Sección 4 — Demo 3: Construir (Superpowers)

**Files:**
- Modify: `docs/presentations/2026-04-10-ai-driven-development.md` (append)

- [ ] **Step 1: Write section divider slide**

Append to the file:

```markdown
---

<!-- _class: lead -->

# Demo 3
## Construir con método

**Superpowers: Spec Driven Development + TDD**
```

- [ ] **Step 2: Write "Superpowers en 1 slide"**

```markdown
---

# Superpowers en 1 slide

- Una **metodología** de desarrollo asistido por IA, no solo un plugin
- Define un flujo disciplinado: brainstorming → spec → plan → TDD → implementación
- Se materializa como plugin de Claude Code con **skills que fuerzan cada paso**
- Claude ya no puede saltarse pasos — la disciplina está embebida en el proceso

> La diferencia entre "IA que adivina" e "IA que sigue un método".

<!--
Concepto clave: Superpowers es una metodología primero, una herramienta
después. Los skills convierten al modelo de un autocomplete glorificado en
un colaborador con método. Es open source y cualquiera puede escribir sus
propios skills o extender los existentes.
-->
```

- [ ] **Step 3: Write "Spec Driven Development en 1 slide"**

```markdown
---

# Spec Driven Development en 1 slide

- Antes de escribir código, Claude escribe una **spec** que tú apruebas
- La spec define: qué se construye, por qué, cómo, y qué NO se hace
- El diseño se valida **antes de la primera línea de código**
- La spec queda como documentación viva del feature

> Código sin spec = construir sin plano. La spec es el plano.

<!--
SDD es el paso que más tiempo ahorra a largo plazo. Sin spec, Claude
escribe lo que cree que quieres. Con spec, escribe lo que acordaron.
La diferencia se nota en la code review: con spec, el reviewer tiene
contra qué comparar.
-->
```

- [ ] **Step 4: Write "TDD en 1 slide"**

```markdown
---

# TDD en 1 slide

- **Test-Driven Development**: tests primero, implementación después
- Claude escribe el test que falla → implementa el mínimo para que pase → refactoriza
- Cada ciclo red-green-refactor produce un commit atómico
- La red de seguridad es permanente — no solo para hoy, para siempre

> TDD no es "escribir tests". Es diseñar desde el comportamiento esperado.

<!--
TDD forzado por un skill es más disciplinado que TDD humano. Claude no se
cansa, no se salta el paso de "primero verifico que falla", no dice "después
escribo el test". El skill no lo deja avanzar sin test.
-->
```

- [ ] **Step 5: Write demo slide**

```markdown
---

# El demo: de ticket a commit

- **Input mío:** "implementa [el feature del ticket que creamos]"
- **Lo que hace Claude con Superpowers:**
  1. **Brainstorming** → pregunta intent, no asume
  2. **Spec** → escribe el diseño, yo apruebo
  3. **Plan** → pasos concretos con archivos y código, divide y vencerás
  4. **TDD** → test que falla → implementación → test que pasa
  5. **Commit** → mensaje conventional, cambios atómicos

<!--
ALT-TAB AL TERMINAL. Recordar: si el issue de RAG era simple, se
implementa. Si no, usar el fallback pre-seleccionado. La audiencia no
necesita saber esto — solo ven el flujo Superpowers en acción.
-->
```

- [ ] **Step 6: Write recap slide**

```markdown
---

# Lo que acabamos de ver

- **Brainstorming** hizo preguntas que yo no había pensado
- **La spec** documentó el diseño antes de escribir una línea
- **TDD** escribió el test ANTES del código → red de seguridad permanente
- **El commit es chico**: un PR que cualquiera puede revisar en minutos
- **El proceso es reproducible**: mañana el mismo flujo te da otro feature

<!--
Recap honesto. Si algo en la demo salió raro o tuvimos que corregir a
Claude, mencionarlo aquí. La audiencia confía más en los talleres que
admiten fricción real.
-->
```

- [ ] **Step 7: Verify renders**

Run: `npx @marp-team/marp-cli@latest docs/presentations/2026-04-10-ai-driven-development.md -o /tmp/ai-driven-dev-check.html`
Expected: 19 slides total, no render errors.

- [ ] **Step 8: Commit**

```bash
git add docs/presentations/2026-04-10-ai-driven-development.md
git commit -m "docs(presentations): add Demo 3 — Superpowers build section"
```

---

### Task 5: Sección 5 — Cierre

**Files:**
- Modify: `docs/presentations/2026-04-10-ai-driven-development.md` (append)

- [ ] **Step 1: Write "El ciclo completo" slide**

Append to the file:

```markdown
---

# El ciclo completo

```
Descubrir          Planificar              Construir
 (RAG)        (Skills + MCP)      (Superpowers: Spec → TDD)
   │                 │                       │
   ▼                 ▼                       ▼
 Corpus de      PRD Generator           Brainstorming
  emails       + user-story              → Spec
    │           + MCP Plane              → Plan
    ▼                │                   → TDD
 Top 3 ideas         ▼                      │
    │          Ticket en Plane               ▼
    └────────────────┴───────────────── Commit real
```

> Una sesión. Cero tab-switching. De la idea al commit.

<!--
La slide de "todo se conecta". Dejar que la audiencia absorba que los 3
demos no fueron independientes — cada output alimentó al siguiente.
-->
```

- [ ] **Step 2: Write honesty slide**

```markdown
---

# Seamos honestos

- **Lo que SÍ condujo la IA:**
  - Buscar contexto en fuentes que no son código (RAG)
  - Estructurar requisitos con formato profesional (Skills)
  - Crear artefactos en herramientas externas sin cambiar de ventana (MCP)
  - Forzar disciplina: spec antes de código, test antes de implementación (Superpowers)
- **Lo que NO condujo la IA:**
  - Decidir qué construir
  - Aprobar la spec — el filtro humano sigue siendo necesario
  - Code review, ownership, accountability

<!--
La slide más importante. Decirla despacio. AI Driven Development no es
"AI Does Everything". Es "AI conduce las tareas mecánicas para que tú
te enfoques en las decisiones que importan".
-->
```

- [ ] **Step 3: Write takeaways slide**

```markdown
---

# Lo que te llevas hoy

1. **RAG es para contexto que no vive en código.** Mails, docs, Slack, tickets viejos — búscalos, no los ignores.
2. **Skills + MCP componen.** Skills dan la forma, MCP ejecuta la acción. Juntos eliminan el copy-paste entre pestañas.
3. **Superpowers es metodología, no magia.** Spec Driven Development + TDD + commits chicos, todos los días.
4. **El ciclo completo importa.** Descubrir → Planificar → Construir. La productividad real está en el flujo, no en un paso aislado.

> El taller termina cuando abres tu propio repo y aplicas una de estas 4 hoy.

<!--
Cuatro takeaways concretos. Si recuerdan uno, valió la pena. Si recuerdan
los cuatro, este taller fue un éxito.
-->
```

- [ ] **Step 4: Write closing slide**

```markdown
---

<!-- _class: lead -->

# Gracias

**Esta charla:** `docs/presentations/2026-04-10-ai-driven-development.md`

## ¿Preguntas?

<!--
Q&A. Si nadie pregunta los primeros 30 segundos, sembrar una pregunta
propia: "una que me hacen seguido es '¿y si la IA se equivoca en la spec?'
— por eso la spec la apruebas tú antes de que se escriba una línea."
-->
```

- [ ] **Step 5: Final Marp render check**

Run: `npx @marp-team/marp-cli@latest docs/presentations/2026-04-10-ai-driven-development.md -o /tmp/ai-driven-dev-check.html`
Expected: 23 slides total, no render errors. Light background throughout.

- [ ] **Step 6: Commit**

```bash
git add docs/presentations/2026-04-10-ai-driven-development.md
git commit -m "docs(presentations): add cierre section — complete AI Driven Development deck"
```

---

### Task 6: Generate HTML render

**Files:**
- Create: `docs/presentations/2026-04-10-ai-driven-development.html`

- [ ] **Step 1: Generate final HTML**

Run: `npx @marp-team/marp-cli@latest docs/presentations/2026-04-10-ai-driven-development.md -o docs/presentations/2026-04-10-ai-driven-development.html`
Expected: HTML file generated successfully.

- [ ] **Step 2: Commit**

```bash
git add docs/presentations/2026-04-10-ai-driven-development.html
git commit -m "docs(presentations): add HTML render of AI Driven Development deck"
```