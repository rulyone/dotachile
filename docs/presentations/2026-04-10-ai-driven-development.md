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
(Superpowers). Todo conectado.
-->

---

# Antes de empezar: ¿qué necesito abierto?

1. **Terminal** con `claude` (Claude Code CLI) corriendo en este repo
2. **Plane** local (`http://localhost/dotachile`) — para la demo 2 ("JIRA" open source)
3. **Editor favorito** — solo para mirar diffs, no vamos a tipear casi nada
4. El repo `dotachile` clonado y `docker compose up -d` listo
5. El corpus de correos recibidos en `dotachile.com@gmail.com`

---

<!-- _class: lead -->

# Demo 1
## Descubrir qué construir

**RAG + búsqueda híbrida**

---

# RAG en 1 slide

- **Retrieval Augmented Generation**: dale a Claude contexto que NO está en el código
- **No es** fine-tuning. **No es** memoria infinita.
- **Sí es:** un índice (BM25 + embeddings semánticos) que devuelve los pedazos relevantes
- Claude solo ve los chunks que la búsqueda recuperó — no todo el corpus

> RAG = búsqueda inteligente + un LLM que sabe leer los resultados.

<!--
Aclaración importante: la gente confunde RAG con fine-tuning o con "Claude
con memoria infinita". Ninguna de las dos. RAG es búsqueda. La magia está
en que la búsqueda híbrida (léxica + semántica) encuentra cosas que un grep
no encontraría.
-->

---

# El corpus de dotachile

- **Cientos de threads** de mail histórico de `dotachile.com@gmail.com`
- Construido desde **Gmail Takeout** + un pipeline de redacción de PII
- Indexado con **BM25 + embeddings multilingües** (ES + EN)
- Consulta: `tools/email-rag/search.py "<query en español>"`

<!--
Mencionar la privacidad: el corpus está PII-redactado ([REDACTED_PERSON],
USER_NNNN, etc) y el mbox crudo vive en un sparse bundle encriptado que NO
se monta en sesiones normales. Nadie está filtrando datos personales a Claude.
-->

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

---

<!-- _class: lead -->

# Demo 2
## Planificar con precisión

**Skills + MCP**

---

# MCP en 1 slide

- **Model Context Protocol** — el estándar abierto para que las IA hablen con herramientas externas
- "Tools" expuestas por un servidor MCP: Plane, GitHub, Postgres, Slack, lo que sea
- Claude las llama como funciones; los resultados vuelven al contexto
- **Ya tenemos** el servidor MCP de Plane configurado en localhost y Claude sabe cómo usarlo

> Sin MCP, Claude te dice "abre Plane y crea un ticket". Con MCP, lo crea él.

<!--
Mencionar que MCP es un spec abierto, no propietario de Anthropic. Hay
decenas de servidores MCP comunitarios. El de Plane lo configuramos
previamente — hoy lo usamos.
-->

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

---

<!-- _class: lead -->

# Demo 3
## Metodología de trabajo para la IA

**Superpowers: Spec Driven Development + TDD**

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

---

# Spec Driven Development en 1 slide

- Antes de escribir código, Claude escribe una **spec** que tú apruebas
- La spec define: qué se construye, por qué, cómo, y qué NO se hace
- El diseño se valida **antes de la primera línea de código**
- La spec queda como documentación viva del feature
- Después de la spec, Superpowers genera un **plan de implementación** paso a paso

> Código sin spec = construir sin plano. La spec es el plano.

<!--
SDD es el paso que más tiempo ahorra a largo plazo. Sin spec, Claude
escribe lo que cree que quieres. Con spec, escribe lo que acordaron.
La diferencia se nota en la code review: con spec, el reviewer tiene
contra qué comparar.
-->

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

---

# El demo: de ticket a commit

- **Input mío:** "implementa [el feature del ticket que creamos]"
- **Lo que hace Claude con Superpowers:**
  1. **Brainstorming** → pregunta intent, no asume
  2. **Spec** → escribe el diseño, yo apruebo
  3. **Plan** → pasos concretos con archivos y código
  4. **TDD** → test que falla → implementación → test que pasa
  5. **Commit** → mensaje conventional, cambios atómicos

<!--
ALT-TAB AL TERMINAL. Recordar: si el issue de RAG era simple, se
implementa. Si no, usar el fallback pre-seleccionado. La audiencia no
necesita saber esto — solo ven el flujo Superpowers en acción.
-->

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

---

# El ciclo completo

<pre class="mermaid">
graph LR
    A["🔍 Descubrir<br/>(RAG)"] --> B["📋 Planificar<br/>(Skills + MCP)"]
    B --> C["🔨 Construir<br/>(Superpowers)"]

    A --- A1["Corpus de emails"]
    A1 --- A2["Top 3 ideas"]

    B --- B1["PRD Generator<br/>+ user-story"]
    B1 --- B2["Ticket en Plane"]

    C --- C1["Brainstorming → Spec"]
    C1 --- C2["Plan → TDD"]
    C2 --- C3["Commit real"]
</pre>
<script src="https://cdn.jsdelivr.net/npm/mermaid@11/dist/mermaid.min.js"></script>
<script>mermaid.initialize({ startOnLoad: true, theme: 'base' });</script>

> Una sesión. De la idea al commit.

<!--
La slide de "todo se conecta". Dejar que la audiencia absorba que los 3
demos no fueron independientes — cada output alimentó al siguiente.
-->

---

# ¿Quién hace qué?

| IA | Human in the loop |
|---|---|
| Buscar contexto (RAG) | Decidir qué construir |
| Estructurar requisitos (Skills) | Aprobar la spec |
| Crear tickets (MCP) | Code review y ownership |
| Forzar disciplina (Superpowers) | Accountability |

<!--
La slide más importante. Decirla despacio. AI Driven Development no es
"AI Does Everything". Es "AI conduce las tareas mecánicas para que tú
te enfoques en las decisiones que importan".
-->

---

# Lo que te llevas hoy

1. **RAG** — busca contexto que no vive en código
2. **Skills + MCP** — dan la forma y ejecutan la acción
3. **Superpowers** — metodología, no magia (Spec + TDD + commits chicos)
4. **El ciclo completo** — Descubrir → Planificar → Construir

<!--
Cuatro takeaways concretos. Si recuerdan uno, valió la pena. Si recuerdan
los cuatro, este taller fue un éxito.
-->

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