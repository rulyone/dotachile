# QUAL Gates — Reference

Las 12 reglas que **cada sesión** del arco AI-SWE 2026 (DotaChile) debe cumplir para ser considerada entregable. Esta doc es la **única fuente** de la especificación de cada gate: los templates sidecar (`MANIFEST.template.md`, `REHEARSAL.template.md`, `HANDOUT.template.md`) y las validaciones de fase linkean por ID (`QUAL-NN`) y nunca duplican el texto.

**Cómo se usa:**

- Durante el plan-phase de cada sesión, el presentador revisa los gates cuyo *template asociado* menciona la sesión y confirma que cada campo del sidecar está cubierto.
- Durante el rehearsal (QUAL-03), el presentador ejecuta `Cómo se verifica` de cada gate y corrige lo que falte.
- Durante el delivery, los gates ya están verificados por construcción — rellenar los templates implica satisfacer sus QUAL gates asociados.
- Para agregar un gate nuevo: agregar una sección `## QUAL-NN` aquí + actualizar los templates que la enforzan + re-correr los greps de `00-VALIDATION.md`.

---

## QUAL-01 — MANIFEST.md completo

**Qué exige:** Cada sesión publica un `MANIFEST.md` en su carpeta con título, fecha, SHA de `session-NN-pre`, SHA de `session-NN-post`, mapa slide→commit, comando de recuperación (`git reset --hard session-NN-pre`), URL de GitHub `compare`, y sección de known follow-ups.

**Por qué existe:** FEATURES D9 (traceability) — la audiencia (presente o post-hoc) debe poder clonar el repo en `session-NN-pre`, seguir el manifest, y reproducir el demo sin adivinar qué commit corresponde a qué slide. Sin manifest, los decks viven en un mundo y los commits en otro.

**Cómo se verifica:** `grep -c "session-NN-pre\|session-NN-post\|Recovery\|compare" docs/presentations/YYYY-MM-DD-NN-<slug>/MANIFEST.md` debe retornar ≥ 4; adicionalmente `grep -c "\[Replace:" MANIFEST.md` debe retornar 0 en tiempo de delivery (todos los placeholders rellenados).

**Template asociado:** `MANIFEST.template.md`.

---

## QUAL-02 — Fallback artifact + switchover rehearsed

**Qué exige:** Cada sesión tiene un artefacto de fallback — `.cast` de asciinema (captura real) o `.tape` de VHS (scripted) — commiteado junto al deck. El switchover live → fallback fue ensayado al menos una vez y el resultado quedó registrado en `REHEARSAL.md`.

**Por qué existe:** Pitfall 1 — la IA flakea. Sin fallback pre-grabado + switchover practicado, un outage de Claude, un rate-limit, o un bug upstream convierte los 57 minutos en un incidente. Ensayar el switchover al menos una vez garantiza que el presentador sabe *cuándo* y *cómo* cortar a fallback sin pánico.

**Cómo se verifica:** `ls docs/presentations/YYYY-MM-DD-NN-<slug>/*.cast docs/presentations/YYYY-MM-DD-NN-<slug>/*.tape 2>/dev/null | wc -l` ≥ 1; además `grep -q "switchover" docs/presentations/YYYY-MM-DD-NN-<slug>/REHEARSAL.md`.

**Template asociado:** `REHEARSAL.template.md` (checklist item) + entrega física en la carpeta de sesión.

---

## QUAL-03 — Same-day rehearsal dentro de 24h

**Qué exige:** El rehearsal de la sesión se corre en la **misma máquina y misma red** que el delivery, **dentro de las 24 horas previas** a la sesión. La fecha y el resultado quedan registrados en `REHEARSAL.md`.

**Por qué existe:** Pitfall 1 — environment drift. Un rehearsal hecho una semana antes en una red distinta no dice nada sobre si Payara arranca hoy, si el WiFi del venue aguanta embeddings de 420 MB, o si una actualización de OS rompió algún CLI. El requisito de 24h + misma máquina + misma red cierra ese gap.

**Cómo se verifica:** `grep -E "Fecha del rehearsal: [0-9]{4}-[0-9]{2}-[0-9]{2}" docs/presentations/YYYY-MM-DD-NN-<slug>/REHEARSAL.md`; la fecha encontrada debe estar dentro de 24h de la fecha de la sesión (revisión humana durante `/gsd-verify-work`).

**Template asociado:** `REHEARSAL.template.md`.

---

## QUAL-04 — "Lo que la IA NO hizo" (honesty slide)

**Qué exige:** Cerca del cierre de cada sesión, una slide nombra al menos **una cosa que el presentador hizo manualmente** (no la IA) + **una cosa que requirió corrección manual durante el rehearsal**. El título canónico en el deck es "Lo que la IA NO hizo" o equivalente (también "x10 honesty" / "honestidad ×10").

**Por qué existe:** FEATURES S11 + D8 — honestidad como diferenciador. La credibilidad del arco depende de no vender magia. Mostrar las costuras (lo manual, lo corregido, el flake) hace que la audiencia confíe en lo que SÍ hizo la IA.

**Cómo se verifica:** `grep -q "Lo que la IA NO hizo\|x10 honesty\|honestidad ×10\|NO hizo" docs/presentations/YYYY-MM-DD-NN-<slug>/*.md`.

**Template asociado:** Ninguno directo (la slide vive en el deck, no en un sidecar). El `REHEARSAL.template.md` recolecta *candidates* en la sección `Flakes / correcciones manuales` como insumo para esta slide.

---

## QUAL-05 — Cada sesión toca CONCERNS.md

**Qué exige:** Cada sesión **toca al menos un archivo o artefacto** listado en `.planning/codebase/CONCERNS.md`. La Sesión 2 (theory-heavy) es excepción condicional — su script hands-on igual debe ejercitar la codebase real aunque la sesión sea conceptual.

**Por qué existe:** Pitfall 14 + FEATURES D5 — "los demos son creíbles". El core value del arco se pierde si una sesión se ejecuta sobre un toy repo. Obligar a que cada sesión toque la codebase legacy es lo que garantiza que las técnicas mostradas sobreviven al contacto con producción.

**Cómo se verifica:** Cross-reference con `docs/presentations/CONCERNS-MAPPING.md`: cada `Session NN` debe aparecer en ≥ 1 fila con `status = claimed` (o equivalente). La MANIFEST.md slide→commit map también evidencia qué archivos de la codebase fueron tocados.

**Template asociado:** Ninguno directo. `MANIFEST.template.md` evidencia vía el mapa slide→commit + sección "Slices de CONCERNS.md tocados".

---

## QUAL-06 — Convención bilingüe on-screen

**Qué exige:** Cada sesión declara al menos **una vez en pantalla** la convención bilingüe: "El deck y la narración están en español; la salida de Claude Code está en inglés — es el estado actual del ecosistema". La Sesión 1 autorea la slide canónica (S01-06); las sesiones 2-9 pueden reusarla o repetir el mensaje en el footer.

**Por qué existe:** Pitfall 25 — la audiencia chilena/hispanohablante no debe sorprenderse al ver Claude Code respondiendo en inglés después de un deck 100% español. Nombrarlo explícitamente lo vuelve un *feature* del estado del ecosistema en vez de un *bug* del presentador.

**Cómo se verifica:** `grep -q "deck y la narración\|salida de Claude Code está en inglés\|bilingüe\|convención bilingüe" docs/presentations/YYYY-MM-DD-NN-<slug>/*.md`.

**Template asociado:** `HANDOUT.template.md` (footer cita el gate por ID). La Sesión 1 autorea la slide canónica; las demás referencian.

---

## QUAL-07 — Budget 57 min on-paper

**Qué exige:** El demo-task de cada sesión está **pre-sliceado** para entrar en 57 minutos on-paper: 5 min intro + 10 min concepto + 12 min demo 1 + 12 min demo 2 + 8 min recap + 10 min Q&A = 57 min. Si el rehearsal observa que la sesión excede los 57 min, re-slice del demo en el plan-phase de la sesión (no improvisar recorte en vivo).

**Por qué existe:** FEATURES S10 + Pitfall 6 — la audiencia acordó 1 hora. Pasarse de 60 min quema confianza y corta Q&A. El budget on-paper es la primera línea de defensa; el rehearsal (QUAL-03) es la segunda.

**Cómo se verifica:** `REHEARSAL.md` tiene una sección `Timing por sección` con minutos observados por bloque; la suma debe ser ≤ 57 min. Si excede, el presentador re-slicea el demo (commit en el plan-phase) antes del delivery.

**Template asociado:** `REHEARSAL.template.md` (sección `Timing por sección (QUAL-07 — budget 57 min)`).

---

## QUAL-08 — Known follow-ups en migration-slice sessions

**Qué exige:** Cada sesión que sea una **migration-slice** (e.g., PvpgnHash → bcrypt `PVPGN-PREP`, un `escape="false"` XSS fix, un N+1 fix en `TorneoService`) incluye una slide "known follow-ups" cerca del cierre + abre ≥ 1 GitHub issue (o TODO en el repo) por cada slice diferido.

**Por qué existe:** Pitfall 30 — refactor overclaim. Una migration-slice de 57 min **no** termina la migración; termina una rebanada. Declarar explícitamente los slices pendientes evita que la audiencia se vaya pensando "listo, PvpgnHash está migrado" cuando en realidad solo se preparó el schema dual-hash.

**Cómo se verifica:** En migration-slices: `grep -q "known follow-ups\|próximos slices\|slices diferidos" docs/presentations/YYYY-MM-DD-NN-<slug>/*.md`. Además: issues enlazados en MANIFEST.md sección `Known follow-ups`.

**Template asociado:** `MANIFEST.template.md` (sección `## Known follow-ups (QUAL-08)`).

---

## QUAL-09 — Pre-warm Payara + Postgres ≥10 min antes

**Qué exige:** El presentador ejecuta `docker compose up -d` + un request dummy que fuerce el arranque de Payara + Postgres **al menos 10 minutos antes** del inicio de la sesión. `docker compose up` **nunca** es un paso en vivo dentro del deck.

**Por qué existe:** Pitfall 8 — Payara 5 tiene cold boot de 60-90 segundos. Si el presentador ejecuta `docker compose up` en vivo, la sesión empieza con 90 segundos de pantalla negra. SCAF-05 (digest pinning) elimina el riesgo de re-pull de imágenes; este gate elimina el costo de arranque visible.

**Cómo se verifica:** Checklist de `REHEARSAL.md` marcado: `- [x] Payara + Postgres pre-warm ...`. Adicionalmente `grep -q "docker compose up" docs/presentations/YYYY-MM-DD-NN-<slug>/*.md` **NO** debería encontrar el comando en el body del deck (solo en apéndice/notas como referencia opcional).

**Template asociado:** `REHEARSAL.template.md` (checklist item).

---

## QUAL-10 — Single-primitive primary teaching surface

**Qué exige:** Cada sesión enseña **una sola primitiva** en los primeros 30 minutos. La composición con primitivas previas es aceptable **solo después** de introducir la primitiva actual. Ejemplo: Sesión 7 (Hooks) puede componer con Agent (Sesión 6) solo después del minuto 30.

**Por qué existe:** Pitfall 18 — multi-primitive salad. Las sesiones sobrecargadas que mezclan RAG + MCP + Skills en los primeros 20 minutos producen audiencias que no aprenden ninguna. Single-primitive en el primer tercio garantiza que la primitiva del día se introduce antes de que la composición la haga borrosa.

**Cómo se verifica:** Revisión humana del outline de cada deck contra `REQUIREMENTS.md §S<NN>` — ningún REQ `S<NN>-*` debe obligar a introducir una primitiva distinta a la del título antes del minuto 30. Parte de `/gsd-verify-work` por fase.

**Template asociado:** Ninguno directo (es una regla de diseño del outline, no un campo del sidecar).

---

## QUAL-11 — Deck .html + Mermaid .svg committeados

**Qué exige:** Cada `.md` de Marp tiene su `.html` renderizado al lado (render-on-commit). Cada `.mmd` de Mermaid tiene su `.svg` pre-renderizado al lado. Ambos outputs commiteados en el mismo commit que la fuente.

**Por qué existe:** HTMLPreview.github.io reescribe los `<script>` tags — runtime Mermaid JS no funciona en links compartidos vía HTMLPreview. Pre-renderizar SVG elimina esa dependencia; commitear `.html` junto a `.md` permite a la audiencia abrir el deck desde cualquier viewer sin tener Marp instalado.

**Cómo se verifica:** `for f in docs/presentations/YYYY-MM-DD-NN-<slug>/*.md; do test -f "${f%.md}.html" || echo "MISSING HTML: $f"; done`; ídem para `.mmd`/`.svg`: `for f in docs/presentations/YYYY-MM-DD-NN-<slug>/*.mmd; do test -f "${f%.mmd}.svg" || echo "MISSING SVG: $f"; done`.

**Template asociado:** `REHEARSAL.template.md` (checklist items para `.html` y `.svg`).

---

## QUAL-12 — Version pins en MANIFEST

**Qué exige:** El `MANIFEST.md` de cada sesión nombra: Claude Code CLI version (`claude --version`), Model ID (e.g., `claude-opus-4-7`), Ollama version, Marp CLI version, Mermaid CLI version, y MCP servers relevantes — **tanto en rehearsal como en delivery** (dos columnas / dos timestamps si las versiones difieren entre rehearsal y delivery).

**Por qué existe:** Pitfall 1 — "funcionó ayer" se convierte en "no funciona hoy" cuando un minor update del CLI cambia el comportamiento. Pinnear versiones en el MANIFEST permite reconstruir exactamente el entorno de la sesión meses después (reproducibilidad post-delivery, CURR-04).

**Cómo se verifica:** `grep -c "Claude Code\|Model ID\|Ollama\|Marp\|Mermaid" docs/presentations/YYYY-MM-DD-NN-<slug>/MANIFEST.md` ≥ 5.

**Template asociado:** `MANIFEST.template.md` (sección `## Versions (QUAL-12)` con tabla de 6 filas).

---

## Cómo usar esta doc desde los templates

Los templates sidecar linkean a esta doc usando el ID del gate, nunca duplicando el texto. Dos formas estandarizadas:

1. **HTML comment en el footer** del template (MANIFEST y HANDOUT):

   ```markdown
   <!-- see QUAL-GATES.md §QUAL-01, §QUAL-08, §QUAL-12 -->
   ```

2. **Cross-ref inline** en items individuales (REHEARSAL checklist):

   ```markdown
   - [ ] Model ID pinneado en MANIFEST.md — ver `QUAL-GATES.md §QUAL-12`
   ```

**Regla invariante:** si el texto de un gate aparece copiado en más de un archivo, hay drift latente. Pitfall 3 lo documenta: QUAL-07 cambia → tres archivos que actualizar. Con cross-ref, QUAL-07 cambia → un archivo (este).

**Para agregar un gate nuevo (QUAL-13+):**

1. Agregar sección `## QUAL-13 — <título>` aquí con las 4 subsecciones fijas.
2. Actualizar el template que lo enforza (y solo ese) con el nuevo campo / checklist item + cross-ref por ID.
3. Re-correr los greps de `.planning/phases/00-series-scaffolding/00-VALIDATION.md` contra el nuevo conjunto.
4. Anunciar el cambio en `STATE.md` bajo "Accumulated Context → Decisions".
