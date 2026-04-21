# [Replace: Session NN — Título en español]

**Fecha:** [Replace: YYYY-MM-DD]
**Presentador:** Pablo Martínez

> **Cómo usar este template:**
> 1. Copiar a `docs/presentations/YYYY-MM-DD-NN-<slug>/MANIFEST.md` al iniciar el plan-phase de la sesión.
> 2. Rellenar los campos `[Replace: ...]` pre-sesión (título, fecha, SHA de `session-NN-pre`, tabla de versiones rehearsal, slices de CONCERNS.md).
> 3. Completar los campos post-sesión (SHA de `session-NN-post`, URL de `compare`, columna "Versión (delivery)", filas finales del mapa slide→commit, known follow-ups definitivos) después de pushear el tag `session-NN-post`.
> 4. Antes del delivery, verificar que no queden `[Replace:` pendientes de los campos pre-sesión:
>    ```bash
>    grep -c "\[Replace:" docs/presentations/YYYY-MM-DD-NN-<slug>/MANIFEST.md
>    ```
>    Debe retornar `0` al cierre de la sesión (tras rellenar los post-fields).

## Tags

- **Pre:** `session-NN-pre` → SHA `[Replace: git rev-parse session-NN-pre — pre-sesión]`
- **Post:** `session-NN-post` → SHA `[Replace: git rev-parse session-NN-post — post-sesión]` *(post-session)*
- **Compare:** `https://github.com/[Replace: org]/dotachile/compare/session-NN-pre...session-NN-post` *(post-session)*

## Slide → Commit map

| # | Slide | Commit SHA | Notas |
|---|-------|------------|-------|
| 1 | [Replace: slide title] | `[Replace: SHA]` | [Replace: notas] |
| 2 | [Replace: slide title] | `[Replace: SHA]` | [Replace: notas] |
| 3 | [Replace: slide title] | `[Replace: SHA]` | [Replace: notas] |

*(agregar filas según necesidad — una por cada commit live que corresponda a una slide)*

## Recovery command

```bash
git reset --hard session-NN-pre
docker compose restart app
```

*Uso: si el demo live sale mal y el estado del repo queda sucio, este comando devuelve la máquina al punto exacto donde empezó la sesión. El restart de `app` limpia el classloader de Payara.*

## Versions (QUAL-12)

| Componente | Versión (rehearsal) | Versión (delivery) |
|------------|---------------------|--------------------|
| Claude Code CLI | `[Replace: claude --version]` | `[Replace: claude --version]` |
| Model ID | `[Replace: e.g., claude-opus-4-7]` | `[Replace: e.g., claude-opus-4-7]` |
| Ollama | `[Replace: ollama --version]` | `[Replace: ollama --version]` |
| Marp CLI | `[Replace: npx @marp-team/marp-cli --version]` | `[Replace: npx @marp-team/marp-cli --version]` |
| Mermaid CLI | `[Replace: npx @mermaid-js/mermaid-cli --version]` | `[Replace: npx @mermaid-js/mermaid-cli --version]` |
| MCP servers | `[Replace: nombres y versiones — o "ninguno" si la sesión no usa MCP]` | `[Replace: nombres y versiones]` |

## Slices de CONCERNS.md tocados

- [Replace: e.g., XSS-01, XSS-02 — o "N/A Session 2 excepción"] *(cross-ref con [CONCERNS-MAPPING.md](CONCERNS-MAPPING.md))*

## Known follow-ups (QUAL-08)

- [Replace: issue link o "ninguno" si no es migration-slice]

<!-- see QUAL-GATES.md §QUAL-01, §QUAL-08, §QUAL-12 -->
