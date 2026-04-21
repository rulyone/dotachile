# [Replace: Session NN] — Rehearsal log

**Fecha del rehearsal:** [Replace: YYYY-MM-DD — mismo día de la sesión o día anterior]
**Fecha de la sesión:** [Replace: YYYY-MM-DD]
**Máquina y red usadas:** [Replace: laptop-X + WiFi del venue / hotspot / casa]

> **Cómo usar este template:**
> 1. Copiar a `docs/presentations/YYYY-MM-DD-NN-<slug>/REHEARSAL.md` durante el plan-phase de la sesión.
> 2. Correr el rehearsal en la **misma máquina y misma red** que el delivery, dentro de las 24h previas (QUAL-03).
> 3. Marcar cada item del checklist con `x` cuando se cumpla. Un item sin marcar es un **blocker** para el delivery.
> 4. Rellenar las notas libres durante y después del rehearsal — sirven de insumo para la slide QUAL-04 ("Lo que la IA NO hizo") y para ajustar timing antes del delivery.

## Checklist (QUAL-02, QUAL-03, QUAL-09, QUAL-11, QUAL-12)

- [ ] Model ID pinneado en `MANIFEST.md` — ver `QUAL-GATES.md §QUAL-12`
- [ ] Fallback artifact (`.cast` de asciinema o `.tape` de VHS) existe junto al deck — ver `QUAL-GATES.md §QUAL-02`
- [ ] Switchover live → fallback ensayado al menos una vez — ver `QUAL-GATES.md §QUAL-02`
- [ ] Fecha de rehearsal registrada (mismo día de la sesión o día anterior) — ver `QUAL-GATES.md §QUAL-03`
- [ ] Plan de red: WiFi del venue probado O hotspot listo — Pitfall 1
- [ ] Pre-warm: `docker compose up -d` + request dummy corridos ≥10 min antes — ver `QUAL-GATES.md §QUAL-09`
- [ ] `.html` del deck renderiza limpio (HTMLPreview-compatible) — ver `QUAL-GATES.md §QUAL-11`
- [ ] Todos los `.mmd` tienen `.svg` pre-renderizado — ver `QUAL-GATES.md §QUAL-11`
- [ ] Versiones de tools (Marp, Mermaid, Ollama, Claude Code) registradas en `MANIFEST.md` — ver `QUAL-GATES.md §QUAL-12`

<!-- see QUAL-GATES.md §QUAL-02, §QUAL-03, §QUAL-09, §QUAL-11, §QUAL-12 -->

## Notas libres

### Timing por sección (QUAL-07 — budget 57 min)

- Intro: [Replace: minutos observados] / 5 planeados
- Concepto: [Replace: minutos observados] / 10 planeados
- Demo 1: [Replace: minutos observados] / 12 planeados
- Demo 2: [Replace: minutos observados] / 12 planeados
- Recap: [Replace: minutos observados] / 8 planeados
- Q&A: [Replace: minutos observados] / 10 planeados
- **Total:** [Replace: suma observada] / 57 planeados

### Cortes

- [Replace: sección / slide cortada — razón]

### Flakes / correcciones manuales (candidates para QUAL-04 slide)

- [Replace: flake observado / corrección manual aplicada]

### Otras observaciones

- [Replace: cualquier otra cosa útil]
