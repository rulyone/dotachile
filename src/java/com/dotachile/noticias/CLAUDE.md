# noticias

Enable users to publish articles in categories, with moderators to manage approval and deletion.

## Glossary

- **Noticia** — an article with title, preview (5K chars), full content (100K chars), category, writer, and approval status. Timestamp auto-set to `new Date()` on creation; `lastUpdate` tracks edits via `NoticiasService.editarNoticia`.
- **Categoria** — a named category (unique constraint) that groups articles. `categoryname` is the lookup key.
- **aprobada** — boolean flag; articles default to false on creation via `agregarNoticiaSinEdicion`, true via `agregarNoticia`. ADMIN_ROOT, ADMIN_DOTA, or ESCRITOR can approve via `NoticiasService.aprobarNoticia` (class-level `@RolesAllowed` inheritance).
- **escritor** — the `Usuario` who authored the article. Resolved via `SessionContext.getCallerPrincipal()` in `NoticiasService`; throws `BusinessLogicException` if not logged in.

## Rules

- A user must be logged in to create an article; `NoticiasService.agregarNoticia*` resolve the caller and throw if not authenticated.
- Every category must exist before assigning to an article; `agregarNoticia` and `editarNoticia` throw `BusinessLogicException` if category not found via `CategoriaFacade.findByCategoryname`.
- New articles created via `agregarNoticiaSinEdicion` are unapproved (aprobada=false); creation via `agregarNoticia` auto-approves (aprobada=true).
- Only ADMIN_ROOT or ADMIN_DOTA can delete articles via `eliminarNoticia` (method-level `@RolesAllowed`); ADMIN_ROOT, ADMIN_DOTA, or ESCRITOR can approve via `aprobarNoticia` (class-level `@RolesAllowed` inheritance) in `NoticiasService`.
- All queries return approved articles unless explicitly fetching unapproved; `Noticia.findAll` and facade methods filter by aprobada=true.

## Entry points

- `VerNoticiaMB` — displays a single article, handles comments, approve, delete actions. Called by detail view.
- `AgregarNoticiaMB` — form to create a new article with category selection.
- `ArchivoNoticiasMB` — paginated list of articles by category with lazy loading.

<!-- All six controllers follow the fetch-on-view pattern. Categories are typically managed via admin tooling (not in this module). -->