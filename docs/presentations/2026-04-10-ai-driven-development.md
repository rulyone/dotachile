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