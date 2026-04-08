---
marp: true
theme: gaia
class: lead
paginate: true
backgroundColor: "#1e1e2e"
color: "#cdd6f4"
title: "Más allá del Hype: ¿Puede la IA realmente multiplicar tu productividad x10?"
author: "Pablo Martínez"
---

<!-- _class: lead -->

# Más allá del Hype

## ¿Puede la IA realmente multiplicar tu productividad **x10**?

### Vamos a comprobarlo.

Pablo Martínez · Workshop · 2026-04-08

<!--
Presentador: bienvenida breve. "Hoy no vengo a venderles nada. Vengo a abrir
un terminal y mostrarles 3 cosas reales, en un repo real, con commits al
final que pueden ir a leer. Si al final del taller no se llevan algo concreto,
me reclaman."
-->

---

# El hype vs lo que vamos a medir hoy

- **LinkedIn dice:** "10x developer con IA en 30 días"
- **Lo que vamos a hacer:** 3 demos, en este repo (`dotachile`), con commits reales al final
- **Lo que NO vamos a hacer:** generar un boilerplate de 200 archivos y aplaudir

> Si al final no hay un commit que puedas revisar, fue magia, no productividad.

<!--
Plantar la tesis del taller: la diferencia entre "parece productivo" y "es
productivo" se mide en código que sobrevive a una code review. Todo lo que
hagamos hoy va a terminar en un PR o un ticket que existe de verdad.
-->

---

# Antes de empezar: ¿qué necesito abierto?

1. **Terminal** con `claude` (Claude Code CLI) corriendo en este repo
2. **Plane** local (`http://localhost/dotachile`) — para la demo 2
3. **Editor favorito** — solo para mirar diffs, no vamos a tipear casi nada
4. El repo `dotachile` clonado y `docker compose up -d` listo

---

<!-- _class: lead -->

# Demo 1
## Un feature pequeño, hecho con disciplina

**Superpowers + brainstorming + TDD**

---

# Superpowers: ¿qué son?

- Un **plugin** de Claude Code que agrega "skills" — manuales que Claude carga a demanda
- Cada skill es una receta: *brainstorming*, *TDD*, *systematic-debugging*, *writing-plans*
- **No es magia.** Es disciplina forzada: Claude ya no puede saltarse pasos
- Cuando le dices "agrega un feature", Superpowers le obliga a brainstormear primero

> La diferencia entre "IA que adivina" e "IA que pregunta antes de adivinar".

<!--
Concepto clave: skills convierten al modelo de un *autocomplete glorificado*
en un *colaborador con método*. Mencionar que Superpowers es open source y
que cualquiera puede escribir sus propios skills.
-->

---

# El feature de hoy: confirmar contraseña en el registro

- Hoy `RegistroMB` solo pide `password` una vez
- Vamos a agregar un campo **"confirmar contraseña"** + validación
- **Es deliberadamente trivial.** No queremos asombrarnos con el código
- Queremos ver el **proceso**: ¿cómo llega Claude desde "agrega esto" hasta un commit limpio?

<!--
Insistir en que el feature es chico A PROPÓSITO. La gente va a estar tentada
de pedir cosas grandes para "ver qué pasa". El valor del taller no está en
el tamaño del output, está en observar el flujo.
-->

---

# Lo que vas a ver en el terminal

```
1. brainstorming   → Claude pregunta intent, no asume
2. writing-plans   → Claude escribe un plan que tú apruebas
3. TDD             → tests primero, implementación después
4. commit          → un commit chico, mensaje conventional
```

> Cuatro pasos. Cero "déjame escribir 400 líneas y veamos".

<!--
Anchor slide. Después de esta, ALT-TAB AL TERMINAL. No volver hasta que el
commit esté creado. Recordatorio para mí: si la demo se cae, el respaldo
está en `feature/registro-confirmar-password-backup` (crear antes del taller).
-->

---

# Lo que acabamos de ver

- **Brainstorming** hizo preguntas que yo no había pensado
- **TDD** escribió el test ANTES del código → ahora hay una red de seguridad permanente
- **El commit es chico**: un PR de ~30 líneas que cualquiera puede revisar en 2 min
- **El proceso es reproducible**: mañana el mismo flujo te da otro feature

<!--
Recap honesto. Si algo en la demo salió raro o tuvimos que corregir a Claude,
mencionarlo aquí. La audiencia confía más en los talleres que admiten
fricción real que en los que pretenden que todo funcionó perfecto.
-->

---

<!-- _class: lead -->

# Demo 2
## Un user story en Plane, sin abrir el browser

**Skills + MCP**

---

# MCP en 1 slide

- **Model Context Protocol** — el estándar para que las IA hablen con herramientas externas
- "Tools" expuestas por un servidor MCP: Jira, Confluence, GitHub, Plane, Postgres, Slack, Linear, lo que sea
- Claude las llama como funciones; los resultados vuelven al contexto
- **Tú ya tienes uno:** el servidor MCP de Plane corre en tu localhost y Claude lo usa

> Sin MCP, Claude te dice "abre Plane y crea un ticket". Con MCP, lo crea él.

<!--
Mencionar que MCP es open spec, no propietario de Anthropic. Hay decenas
de servidores MCP comunitarios. El de Plane lo configuramos en una sesión
anterior — hoy lo usamos.
-->

---

# Skills en 1 slide

- Un **skill** es un manual cargado a demanda
- Vienen con Claude Code, con Superpowers, o los escribes tú
- Hoy usamos uno de la comunidad: **`product-manager-skills/user-story`** de skills.sh
- Le enseña a Claude el formato "Como X quiero Y para Z" + criterios de aceptación + edge cases

> Skills > prompts largos. El skill se versiona, se comparte, se mejora.

<!--
Plug honesto a skills.sh — es el "npm de skills". Mostrar el link en la
slide siguiente o de pasada en el terminal. Es relevante porque es la forma
en que la comunidad escala el conocimiento.
-->

---

# El demo: escribir un user story bien escrito

- **Input mío:** "crea un ticket para el feature de confirmar contraseña que acabamos de implementar"
- **Lo que hace Claude:**
  1. Carga el skill `user-story` → aprende el formato
  2. Llama al MCP de Plane → lista proyectos, crea el ticket
  3. Llena descripción, criterios de aceptación, edge cases — todos en formato PM
- **Yo nunca abro el browser.** Reviso el resultado en Plane al final.

<!--
ALT-TAB AL TERMINAL. La clave de esta demo es mostrar la composición:
skill (la forma) + MCP (el brazo). Una sin la otra no sirve. Si solo tuviera
MCP, el ticket sería un párrafo de texto. Si solo tuviera el skill, tendría
que copy-pastear yo mismo a Plane.
-->

---

# Lo que acabamos de ver

- **El skill dictó la *forma*** del ticket (qué campos, qué orden, qué tono)
- **El MCP lo *creó*** en Plane sin que yo tocara el browser
- **Yo revisé** — sigo siendo el filtro humano, pero saltándome el typing
- **El ticket queda permanente**, con un link, con historia, asignable

> Composición: skill + MCP > cualquiera de los dos por separado.

<!--
Si la audiencia tiene PMs, este es el momento de mirarlos a los ojos. Esto
NO los reemplaza — los libera de escribir tickets bien formateados a mano.
Su trabajo sigue siendo decidir qué construir.
-->

---

<!-- _class: lead -->

# Demo 3
## Encontrar features en un corpus de 217 emails

**RAG + búsqueda híbrida**

---

# RAG en 1 slide

- **Retrieval Augmented Generation**: dale a Claude contexto que NO está en el código
- **No es** "Claude leyendo tu disco duro al azar"
- **Sí es:** un índice (BM25 + embeddings semánticos) que devuelve los pedazos relevantes
- Claude solo ve los chunks que la búsqueda recuperó — no todo el corpus

> RAG = búsqueda inteligente + un LLM que sabe leer los resultados.

<!--
Aclaración importante: la gente confunde RAG con "fine-tuning" o con "Claude
con memoria infinita". Ninguna de las dos. RAG es búsqueda. La magia está
en que la búsqueda híbrida (léxica + semántica) encuentra cosas que un grep
no encontraría.
-->

---

# El corpus de dotachile

- **cientos de threads** de mail histórico de `dotachile.com@gmail.com`
- Construido desde **Gmail Takeout** + un pipeline de redacción de PII
- Vive en `../dotachile-emails/corpus` (auto-montado por `.claude/settings.json`)
- Indexado con **BM25 + embeddings multilingües** (ES + EN)
- Consulta: `tools/email-rag/search.py "<query en español>"`

> Cada developer construye su propio corpus desde su Takeout. Privado por diseño.

<!--
Mencionar el detalle de privacidad: el corpus está PII-redactado
([REDACTED_PERSON], USER_NNNN, etc) y el mbox crudo vive en un sparse bundle
encriptado que NO se monta en sesiones normales. Esto es importante para que
nadie en la sala piense "estoy filtrando datos personales a Claude".
-->

---

# El demo: top 3 issues que podríamos arreglar

- **Mi pregunta:** "busca en los emails los 3 problemas más mencionados que hoy podríamos arreglar como feature o bugfix"
- **Lo que hace Claude:**
  1. Llama a `search.py` con queries en español ("registro", "cuenta", "torneo")
  2. Lee los hilos más relevantes
  3. Agrupa, prioriza, propone 3 ideas concretas

<!--
ALT-TAB AL TERMINAL. Importante steerear la query hacia los temas donde el
corpus tiene yield: cuentas, registro, soporte, organización de torneos por
cibercafés, historia fundacional. NO preguntar "bugs del ladder" — el corpus
no tiene eso, vivía en la base de datos.
-->

---

# Lo que acabamos de ver

- El corpus tenía **contexto que ni la DB ni el git history tienen**: voz de usuarios reales, quejas, requests
- La búsqueda híbrida encontró threads donde la palabra exacta NO aparecía
- Claude leyó los threads completos (no solo los snippets) antes de proponer
- **Las 3 ideas son priorizables hoy** — podríamos abrir tickets ahora mismo

> Esto es lo que distingue RAG de "ctrl+F sobre un mbox".

<!--
Si queda tiempo: ofrecer abrir uno de los 3 tickets en Plane usando la
demo 2. Cierra el círculo: las 3 demos componen entre sí. Ese momento
"ah, todo se conecta" es el que la gente recuerda del taller.
-->

---

# ¿Y el x10? Seamos honestos.

- **Lo que SÍ se multiplicó:**
  - Velocidad de tareas mecánicas (escribir tickets, buscar contexto, aplicar patrones conocidos)
  - Disciplina (TDD, brainstorming, commits chicos — Claude no se los salta)
- **Lo que NO se multiplicó:**
  - Decidir qué construir
  - Entender el dominio
  - Code review, ownership, accountability
- **El multiplicador real** depende de cuán mecánico era tu trabajo a la entrada

<!--
La slide más importante del taller. Decirla despacio. La gente que vino a
escuchar "x10 garantizado" se va a desinflar; bien. Los que se queden son
los que van a usar esto en serio.
-->

---

# Lo que te llevas hoy

1. **Skills > prompts largos.** Versiona el conocimiento, no lo retipees.
2. **MCP saca a Claude del prompt** y lo mete en tus herramientas. No copies y pegues entre pestañas.
3. **RAG es para contexto que no vive en código.** Mails, docs, Slack, tickets viejos.
4. **Disciplina forzada > talento humano cansado.** TDD + brainstorming + commits chicos, todos los días.

> El taller termina cuando abres tu propio repo y aplicas una de estas 4 hoy.

<!--
Cuatro takeaways concretos. Si pueden recordar uno, vale la pena que vinieron.
Si recuerdan los cuatro, este taller fue un éxito.
-->

---

<!-- _class: lead -->

# Gracias

**Esta charla:** `docs/presentations/2026-04-08-mas-alla-del-hype.md`

## ¿Preguntas?

<!--
Q&A. Si nadie pregunta los primeros 30 segundos, sembrar una pregunta
propia: "una que me hacen seguido es '¿y si Claude se equivoca?' — la
respuesta corta es: por eso hay code review, igual que con un humano".
-->