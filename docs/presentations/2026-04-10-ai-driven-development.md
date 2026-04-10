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

> La diferencia entre "IA que escribe" e "IA que desarrolla".

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

> Cada developer construye su propio corpus desde su Takeout. Privado por diseño.

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