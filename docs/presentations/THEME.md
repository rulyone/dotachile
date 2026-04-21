# Marp Theme — Canonical frontmatter

Copia este bloque tal cual al inicio de cada deck de sesión. No lo modifiques;
cualquier deriva local quiebra el contrato visual del arco. Elegimos copy-paste
en vez de `--theme-set` porque Marp no tiene sistema de `include` en Markdown
(verificado vía Context7 `/marp-team/marp-cli`, 2026-04). Aceptamos el trade-off
para las 9 sesiones del arco; si la serie crece a 20+, revisar en v2.

## Bloque canónico (light — default)

```yaml
---
marp: true
theme: gaia
class: lead
paginate: true
backgroundColor: "#fff"
color: "#1e1e2e"
title: "[Replace: Session NN — Título en español]"
author: "Pablo Martínez"
---
```

### Reglas

- `marp: true` — activa el rendering de Marp sobre el `.md`.
- `theme: gaia` — theme built-in de Marp; los 2 decks pre-serie lo usan. NO
  substituir por CSS custom en el frontmatter: Marp CLI resuelve `theme:` por
  **nombre** (lookup contra themes registrados), no por path. Si algún día hace
  falta un theme propio, se registra vía `--theme` / `--theme-set` al render y
  se referencia por su `/* @theme nombre */`.
- `class: lead` — aplica al slide de portada. Slides individuales pueden
  sobreescribirlo con `<!-- _class: lead -->` (patrón ya en uso en los decks
  pre-serie).
- `paginate: true` — numera cada slide. Excepción documentada: `GLOSSARY.md`
  usa `paginate: false` porque es referencia no-lineal, no una presentación
  secuencial (Pitfall 9).
- `backgroundColor: "#fff"` + `color: "#1e1e2e"` — light default; fondo blanco
  con texto casi-negro. Usar esta variación cuando los diagramas embebidos
  tienen líneas oscuras.
- `title: "[Replace: Session NN — Título en español]"` — placeholder rellenado
  por cada sesión en español, incluyendo el número (`01`..`09`).
- `author: "Pablo Martínez"` — locked; constante a lo largo del arco. Ambos
  decks pre-serie ya lo usan así.

## Variación: dark (para decks con diagramas de líneas claras)

Cuando el deck embebe diagramas SVG con líneas claras (p.ej. `.svg` exportados
con fondo transparente y trazos blancos), el light default produce contraste
invisible. Usa la variación dark: reemplaza solo las dos líneas relevantes del
bloque canónico; el resto queda igual.

```yaml
backgroundColor: "#1e1e2e"
color: "#cdd6f4"
```

El deck pre-serie `2026-04-08-mas-alla-del-hype.md` usa esta variación. El
resto del frontmatter (`marp`, `theme`, `class`, `paginate`, `title`, `author`)
se mantiene idéntico — solo cambian estas dos líneas.

## Pitfalls

- **NO apuntes `theme:` a un path relativo** (ej.: `theme: ../THEME.css`).
  Marp CLI resuelve theme **names**, no paths desde el frontmatter. Si
  necesitas un CSS custom, pásalo por flag (`--theme` o `--theme-set`) al
  render; el `theme:` del frontmatter entonces referencia el nombre declarado
  en el comentario `/* @theme nombre */` del CSS.
- **NO confíes en Mermaid runtime JS.** Pre-rendered SVG es la regla del
  repo: HTMLPreview.github.io reescribe los `<script>` tags a través de su
  propio loader, lo que quiebra ES modules y el carga async. Todos los
  diagramas viven como `.svg` pre-renderizado junto al `.mmd` fuente.
- **NO agregues `html: true` al frontmatter.** El flag `--html` del render se
  encarga de permitir HTML inline (los `<!-- -->` de speaker notes no
  necesitan nada extra). Los 2 decks pre-serie renderan sin `html:` en el
  frontmatter y funcionan correctamente.

## Referencias

- Marp CLI docs: `npx @marp-team/marp-cli@4.3.1 --help`
- [`docs/presentations/CLAUDE.md`](CLAUDE.md) — convenciones de render + workflow Mermaid
- Deck de referencia light: [`docs/presentations/2026-04-10-ai-driven-development/`](2026-04-10-ai-driven-development/)
- Deck de referencia dark: [`docs/presentations/2026-04-08-mas-alla-del-hype/`](2026-04-08-mas-alla-del-hype/)
