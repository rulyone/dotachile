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
- **Grupo** — role/group entity; `groupname` maps to `security-role` in `web.xml`.

## Spanish-language rule

- Always keep package, class, entity, view, and database names in Spanish to match the existing codebase. Do not propose English renames.