# AI Driven Development — Presentación Marp

## Resumen

Presentación tipo workshop para desarrolladores. Tesis: la IA no solo escribe
código — conduce el ciclo completo de desarrollo. Se demuestra en vivo con
tres demos encadenados donde el output de cada uno alimenta al siguiente.

**Formato:** Marp, tema Gaia, fondo claro, speaker notes en comentarios HTML.
Idioma: español.

## Estructura

### Sección 1: Apertura (slides 1–3)

- **Título:** "AI Driven Development — De la idea al commit en una sesión"
- **Tesis:** La IA conduce el ciclo completo: descubrir qué construir,
  planificarlo, e implementarlo. Hoy lo hacemos en vivo.
- **Setup:** terminal con `claude`, Plane local, editor, docker compose up,
  corpus de correos de `dotachile.com@gmail.com`.
- Speaker notes en tono directo/honesto del deck anterior.

### Sección 2: Demo 1 — Descubrir (slides 4–8)

- **"RAG en 1 slide"** — explainer autocontenido: búsqueda inteligente + LLM
  que lee los resultados. No es fine-tuning ni memoria infinita.
- **"El corpus de dotachile"** — cientos de threads de mail, Gmail Takeout,
  PII redactado, búsqueda híbrida BM25 + embeddings, `search.py`.
- **Live demo:** "busca los 3 problemas más mencionados que podríamos arreglar."
- **Recap:** qué encontró, por qué búsqueda híbrida > grep.
- **"Elegimos uno"** — se elige un issue para alimentar Demo 2.

### Sección 3: Demo 2 — Planificar (slides 9–14)

- **"MCP en 1 slide"** — Model Context Protocol, estándar abierto, tools como
  funciones, sin copiar y pegar entre pestañas.
- **"Skills en 1 slide"** — manuales a demanda, versionables. Hoy usamos dos:
  PRD Generator (documento de requisitos) y user-story (formato PM con
  criterios de aceptación + edge cases).
- **Live demo:**
  1. PRD Generator → genera PRD estructurado para el issue elegido.
  2. user-story → refina en formato de ticket con criterios de aceptación.
  3. MCP de Plane → crea el ticket sin abrir el browser.
- **Recap:** dos skills dieron la forma, MCP lo materializó. Composición de
  skills entre sí + con MCP.

### Sección 4: Demo 3 — Construir (slides 15–20)

- **"Superpowers en 1 slide"** — una metodología de desarrollo asistido por IA.
  Define un flujo disciplinado: brainstorming → spec → plan → TDD →
  implementación. Se materializa como plugin de Claude Code con skills que
  fuerzan cada paso.
- **"Spec Driven Development en 1 slide"** — antes de código, Claude escribe
  una spec que tú apruebas. El diseño se valida antes de la primera línea.
- **"TDD en 1 slide"** — tests primero, implementación después. Red de
  seguridad permanente.
- **Live demo:** brainstorming → spec → plan → TDD → commit.
- **Recap:** proceso reproducible, commit revisable, spec como documentación viva.

Nota: el feature a implementar se decide internamente por el presentador.
Si el issue elegido en Demo 1 es simple, se implementa. Si es complejo,
se usa un feature pre-seleccionado como fallback. Esto no se expone en las
slides.

### Sección 5: Cierre (slides 21–23)

- **"El ciclo completo"** — pipeline visual: Descubrir (RAG) → Planificar
  (Skills + MCP) → Construir (Superpowers: Spec → TDD). Todo en una sesión.
- **Honestidad** — qué condujo la IA vs qué seguiste conduciendo tú.
- **Takeaways + Gracias / Q&A.**

## Decisiones de diseño

- **Orden de demos invertido vs deck original:** RAG → MCP → Superpowers
  en vez de Superpowers → MCP → RAG. La razón es narrativa: pipeline lineal
  donde cada demo alimenta al siguiente.
- **Autocontenido:** incluye explainers de 1 slide para cada concepto (RAG,
  MCP, Skills, Superpowers, SDD, TDD) para que funcione sin haber visto el
  deck anterior.
- **Fondo claro:** Marp Gaia con colores claros en vez del dark theme anterior.
- **Fallback silencioso en Demo 3:** el presentador decide internamente si
  implementar el issue de RAG o un feature pre-seleccionado, sin exponerlo.