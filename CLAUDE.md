# DotaChile

## Stack

Java 8, JSF 2 + PrimeFaces 4, EJB Stateless/Singleton, JPA 2 (EclipseLink),
Maven WAR (`DotaCL.war`), Payara 5 in Docker, PostgreSQL 15.

## Build & deploy

```
mvn -o package                      # offline build → target/DotaCL.war
docker compose up -d --build        # full image rebuild + start
./dev-sync.sh java                  # compile + push classes → triggers redeploy (no descriptor sync)
./dev-sync.sh views                 # push XHTML/CSS/images → instant, no redeploy
./dev-sync.sh all                   # java then views
```

## Layout map

```
src/java/com/dotachile/
  auth/             Usuario, Perfil, Grupo, Ban, PreRegistro, PvpgnHash
  clanes/           Clan, ClanBan, Confirmacion, Movimiento
  comentarios/      Comentario (cross-cutting; no UI MB)
  encuestas/        Encuesta, OpcionEncuesta
  ladder/           Ladder, Desafio, FactorK, FaseLadder
  noticias/         Noticia, Categoria
  torneos/          Torneo, Ronda, GameMatch, Game, Resultado, Standing
  torneossingle/    TorneoSingle, RondaSingle, SingleMatch
  automation/       ServiciosAutomatizados (@Singleton scheduled jobs)
  shared/           Util, EloSystem, BusinessLogicException
  infrastructure/
    web/            filters, servlets, exception handler

web/WEB-INF/        web.xml, faces-config.xml
web/web/<feature>/  XHTML views per feature
web/resources/      CSS, JS, images (PrimeFaces theme: trontastic)
```

## Cross-cutting glossary

- **Usuario** — platform user; PK `username`; present in every feature.
- **Clan** — player-owned team; ELO-ranked; used by torneos, ladder, clanes.
- **Torneo** — multi-clan tournament aggregate with `FaseTorneo` lifecycle.
- **Ladder** — global ELO ranking board; drives `Desafio` challenges.
- **Comentario** — user-generated comment attachable to any major entity.
- **Confirmacion** — one-per-clan token used during tournament enrollment.
- **Perfil** — `Usuario` extended profile (PvPGN linkage, avatar).
- **Grupo** — role/group entity; `groupname` maps to `security-role` in `web.xml`: `ADMIN_ROOT`, `ADMIN_DOTA`, `ADMIN_TORNEO`, `ESCRITOR`, `MODERADOR`, `BANEADO`, `ADMIN_LADDER`.

## Spanish-language rule

- Always keep package, class, entity, view, and database names in Spanish to match the existing codebase. Do not propose English renames.

## Searchable historical email corpus (`tools/email-rag`)

A redacted, hybrid-searchable corpus of `dotachile.com@gmail.com`'s
historical mail lives in a **sibling directory of this repo**, at
`../dotachile-emails/corpus` (217 thread `.md` files +
`corpus_chunks.jsonl`). Use it when the user asks "what did people
say about X", "who applied for Y", "find emails about Z" — anything
that needs voice/context from real users that isn't in the database
or git history.

The corpus directory is **auto-mounted** for every Claude Code session
in this repo via `.claude/settings.json` (`additionalDirectories`),
using the sibling-dir convention above. You can therefore
`Read ../dotachile-emails/corpus/<file>.md` directly — no launch
flags, no `/add-dir`, no prompting.

If a developer keeps the corpus somewhere else, they can override the
path in their personal, gitignored `.claude/settings.local.json`
(scopes are merged; local wins). The corpus is per-developer
anyway — each contributor builds their own from their own Gmail
Takeout, see `tools/email-rag/README.md`.

How to query (run via Bash from the repo root):

```
tools/email-rag/.venv/bin/python tools/email-rag/search.py "<query>"
```

- Output is one line per matching thread:
  `<bm25_score>  <semantic_score>  <thread_file> :: <80-char snippet>`
- After search, Read the most relevant `../dotachile-emails/corpus/<thread_file>` to get full context.
- Add `--bm25-only` to skip the semantic leg (faster, no model load).
- The semantic leg loads a 420 MB multilingual model on first call of a
  session (~5–15 s); subsequent calls are sub-second.
- If `search.py` exits with `error: corpus dir not found`, the
  developer hasn't built their corpus yet — point them at
  `tools/email-rag/README.md` for the setup steps.

Querying tips:

- **Query in Spanish.** The corpus is Chilean/Spanish-language mail.
  `"torneo"` works, `"tournament"` mostly doesn't.
- **Hybrid beats BM25-only** for short queries — semantic similarity
  surfaces threads where the literal word isn't present.
- **Coverage is sparse for in-game topics.** The Gmail address was
  used mostly for personal mail, notifications, and external comms;
  in-game ladder/clan/torneo activity lived in the database, not in
  email. Best yield is on community-business topics that flowed
  through email: account requests, complaints, signup issues,
  cybercafé tournament hosting, founding-era history.

Privacy model: the corpus is already PII-redacted (`[REDACTED_PERSON]`,
`[REDACTED_EMAIL_ADDRESS]`, `[REDACTED_PHONE]`, `USER_NNNN` sender
pseudonyms, `[REDACTED_SIGNATURE_BLOCK]`). The raw mbox and
de-anonymization mapping live in an encrypted sparse bundle that is
**not** mounted during normal sessions. See `tools/email-rag/CLAUDE.md`
for the non-negotiable PII handling rule when fixing the redactor
itself.