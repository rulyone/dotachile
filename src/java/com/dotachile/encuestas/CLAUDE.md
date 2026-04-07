# encuestas

Enable admins to author polls and all users to vote on them, with per-user voting enforcement and optional multi-select.

## Glossary

- **Encuesta** — a poll with unique question, creator, timestamps, and closure flag. `Encuesta.fechaCierre` null = open.
- **OpcionEncuesta** — one answer choice within an `Encuesta`, with text and `votadores` (user list).
- **votadores** — collection of `Usuario` entities who voted for this `OpcionEncuesta`, stored in join table `votaciones_encuesta`.
- **múltiple** — boolean flag; true allows voter to select multiple options in `Encuesta.isMultiple()`.
- **última encuesta** — active poll: the most recent by creation (`EncuestaFacade.getUltimaEncuesta()` orders by ID desc).

## Rules

- Only ADMIN_ROOT and ADMIN_DOTA may create or close encuestas; `EncuestaService` enforces via class-level `@RolesAllowed`.
- Only ADMIN_ROOT may delete an encuesta; `eliminarEncuesta` enforces via method-level `@RolesAllowed({"ADMIN_ROOT"})`.
- All authenticated users may vote; `votar` is `@PermitAll` (method-level override of class restriction).
- Each user votes at most once per encuesta; `EncuestaService.votar` queries `OpcionEncuestaFacade.findVotosUsuarioPorEncuesta` and rejects if result exists.
- Single-choice encuestas reject multiple selections; `votar` validates `idsOpciones.size() == 1` if `!encuesta.isMultiple()`.
- Poll question must be unique; `crearEncuesta` checks `EncuestaFacade.findByPregunta()` and throws if duplicate.
- Strip milliseconds from `fechaCierre` when setting; `Encuesta.setFechaCierre` calls `Util.dateSinMillis()`.

## Entry points

- `VerUltimaEncuestaMB` — fetches and displays active poll on homepage; `@PostConstruct` calls `EncuestaFacade.getUltimaEncuesta()` and populates vote counts and user-voted flag.
- `CrearEncuesta` — JSF controller for poll creation; calls `EncuestaService.crearEncuesta()`.
- `EncuestaService` — all business logic: creation, closure, deletion, voting, with role and state enforcement.

<!-- Voter ID uniqueness enforced via NamedQuery "findVotosUsuarioPorEncuesta" checking Usuario MEMBER OF votadores collection. -->