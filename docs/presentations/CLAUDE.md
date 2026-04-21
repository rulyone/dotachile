# Presentations

Marp slide decks for workshops and talks.

## Folder structure

Each presentation lives in its own date-prefixed folder:

```
docs/presentations/
  YYYY-MM-DD-<slug>/
    YYYY-MM-DD-<slug>.md      # Marp source (slides + speaker notes)
    YYYY-MM-DD-<slug>.html    # Rendered HTML (commit alongside .md)
    YYYY-MM-DD-<slug>-*.mmd   # Mermaid diagram sources (optional)
    YYYY-MM-DD-<slug>-*.svg   # Pre-rendered SVG from .mmd (optional)
```

## Marp build

```bash
npx @marp-team/marp-cli@latest --html \
  docs/presentations/<folder>/<file>.md \
  -o docs/presentations/<folder>/<file>.html
```

Always regenerate and commit the `.html` alongside `.md` changes.

## Diagrams (Mermaid)

Mermaid diagrams are stored as `.mmd` source files and pre-rendered to
`.svg` because runtime Mermaid JS breaks on HTMLPreview.github.io.

### Workflow

1. Edit the `.mmd` file (standard Mermaid syntax)
2. Render to SVG:
   ```bash
   npx @mermaid-js/mermaid-cli \
     -i docs/presentations/<folder>/<name>.mmd \
     -o docs/presentations/<folder>/<name>.svg \
     -b transparent
   ```
3. Reference in the slide: `![Alt text](<name>.svg)`
4. Commit both `.mmd` and `.svg`

### Why pre-rendered SVG

HTMLPreview.github.io rewrites `<script>` tags through its own loader,
breaking ES modules and async script loading. Pre-rendered SVG avoids
any JS dependency and works everywhere (HTMLPreview, local file, GitHub
Pages).

## Language

All presentation content is in Spanish per the project's Spanish-language
rule (see root CLAUDE.md).

## Convención de numeración NN-<slug>

Desde la serie AI-SWE 2026, las presentaciones numeradas siguen el formato
`YYYY-MM-DD-NN-<slug>/` donde `NN` es el número de sesión dentro del arco
(`01`..`09`).

- `NN` es **zero-padded** — `01`, `02`, ..., `09`. Nunca `1`, `2`, ... `9`.
  El padding deja el ordenamiento alfabético de `ls` coincidiendo con el
  orden del arco.
- `<slug>` es kebab-case en español — p.ej. `demo-primero`,
  `intro-contexto-llms`, `rag`, `mcp`, `skills`, `agents`, `hooks`,
  `slash-commands`, `capstone`.

Los decks pre-serie (`2026-04-08-mas-alla-del-hype/`,
`2026-04-10-ai-driven-development/`) se quedan **sin NN** — son material
crudo, no parte del arco. No renombrar retroactivamente; preservan su
identidad como referencias históricas.

## Sidecar per sesión

Cada sesión numerada contiene **tres archivos sidecar** además del deck
`.md`/`.html`:

- `MANIFEST.md` — mapa slide→commit, SHAs de los tags pre/post, comando de
  recovery para volver al estado pre-demo, y version pins (Claude Code CLI,
  Model ID, Ollama, Marp CLI, Mermaid CLI, MCP servers).
- `HANDOUT.md` — takeaway corto para la audiencia con 5 secciones fijas en
  español: `¿Qué vimos?`, `Comandos para probar`, `Link de comparación`,
  `Próxima sesión`, `Lecturas`.
- `REHEARSAL.md` — bitácora del dry-run: checklist QUAL + timing observado
  por sección + cortes hechos al material + flakes / correcciones manuales
  para alimentar la slide "Lo que la IA NO hizo".

Los templates canónicos viven en la raíz de la serie como
[`../MANIFEST.template.md`](MANIFEST.template.md),
[`../HANDOUT.template.md`](HANDOUT.template.md) y
[`../REHEARSAL.template.md`](REHEARSAL.template.md). Al iniciar el
plan-phase de una sesión, se copian los 3 templates al folder nuevo
`YYYY-MM-DD-NN-<slug>/` con los nombres finales (`MANIFEST.md`,
`HANDOUT.md`, `REHEARSAL.md`) y se rellenan los placeholders
`[Replace: ...]`.

## Git tags: session-NN-pre / session-NN-post

Antes de cada sesión en vivo, crear el tag pre — estado del repo listo para
arrancar la demo:

```bash
git tag -a session-NN-pre -m "Session NN — Pre-demo state"
git push origin session-NN-pre
```

Después de la sesión, crear el tag post — estado del repo con los commits
de la demo aterrizados:

```bash
git tag -a session-NN-post -m "Session NN — Post-demo state"
git push origin session-NN-post
```

Son **annotated** tags y **no movibles**. Re-teaches a otra cohort usan
branches fuera de `session-NN-pre` (p.ej. `replay/session-03-cohort-2`);
no se mueve el tag. Esta convención permite que cualquiera clonando
`session-NN-post` reproduzca la sesión años después (CURR-04), y mantiene
los links del MANIFEST al comparador de GitHub
(`compare/session-NN-pre...session-NN-post`) estables.

## Fallback artifact (QUAL-02)

Cada sesión DEBE tener un artefacto de fallback pre-grabado junto al deck,
en el mismo folder de la sesión:

- **asciinema** (`.cast`) — captura en vivo del demo; reproducible con
  `asciinema play docs/presentations/YYYY-MM-DD-NN-<slug>/demo.cast`.
- **VHS** (`.tape`) — script determinístico con output reproducible;
  `vhs < docs/presentations/YYYY-MM-DD-NN-<slug>/demo.tape` regenera el
  mismo `.gif`/`.mp4` cada vez.

El artefacto vive en el mismo folder del deck — p.ej.
`docs/presentations/2026-MM-DD-01-demo-primero/demo.cast`. No en un folder
centralizado: si el deck se copia a otro repo o se archiva, el fallback
viaja con él.

El switchover live → fallback se **ensaya al menos una vez** en
`REHEARSAL.md`. Si la demo en vivo flakea (red caída, rate-limit, modelo
alucinando), el presentador ya conoce el muscle memory para pausar, abrir
el fallback, y continuar sin perder el budget de 57 minutos.

## Embed del glosario (CURR-03 drift prevention)

Cuando una sesión referencia una primitiva (RAG, MCP, Skill, Agent, Hook,
Slash Command), **NO copia la definición** del glosario canónico. Embebe
el diagrama por path relativo y linkea a la definición:

```markdown
![RAG](../GLOSSARY/rag.svg)

> Ver `GLOSSARY.html §RAG` para la definición completa.
```

El texto del glosario vive **una sola vez** en
`docs/presentations/GLOSSARY/GLOSSARY.md`. Los diagramas viven **una sola
vez** en `docs/presentations/GLOSSARY/<primitive>.svg`. Las sesiones
apuntan — nunca duplican. Drift entre definiciones es **estructuralmente
imposible** porque no hay copias que mantener sincronizadas.

## QUAL gates

Cada sesión debe cumplir las 12 reglas QUAL (`QUAL-01` .. `QUAL-12`). La
doc de referencia autoritativa es:

- [`../QUAL-GATES.md`](QUAL-GATES.md) — una sección H2 por gate, con qué
  exige, por qué existe, cómo se verifica, y qué template lo enforza.

Los templates (`MANIFEST.template.md`, `HANDOUT.template.md`,
`REHEARSAL.template.md`) enforzan los gates vía checklist items o campos
requeridos, y linkean por ID (p.ej. `see QUAL-GATES.md §QUAL-04`).
`/gsd-verify-work` corre greps contra la sesión para confirmar
compliance. **No duplicar el texto de los gates en los sidecars** —
siempre linkear por ID; si el gate se refina, el cambio aterriza en un
solo archivo.
