# comentarios

Enable any user to post and moderators to manage comments across tournaments, matches, clans, profiles, and challenges.

## Glossary

- **Comentario** — an immutable user-generated comment. Has timestamp, author (`Usuario`), and denegado flag.
- **comentador** — the `Usuario` who authored the comment; enforced by `ComentariosService.agregarComentario*`.
- **fechaComentario** — exact timestamp comment was posted; milliseconds always stripped by `Comentario.setFechaComentario`.
- **denegado** — boolean; false when posted, set true only by `denegarComentario` (moderation action).

## Rules

- A user must be logged in to post a comment. `agregarComentario*` methods resolve the caller via `SessionContext.getCallerPrincipal()` and throw `BusinessLogicException` if not authenticated.
- Every new comment is created with `denegado = false` in `Comentario.setDenegado`.
- Strip milliseconds from `fechaComentario` when setting it; `Comentario.setFechaComentario` normalizes via `Calendar`.
- Only ADMIN_ROOT, ADMIN_DOTA, or MODERADOR can deny a comment; `denegarComentario` enforces via `@RolesAllowed`.
- Comments are attached to one of seven parent entities (Noticia, Torneo, GameMatch, Game, Ronda, Clan, Perfil, Desafio) via collection relationships; each parent entity manages its own collection.

<!-- Horizontal capability used by many features. No UI MB in this module. -->