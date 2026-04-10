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
