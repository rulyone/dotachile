# Phase 0: Series Scaffolding - Research

**Researched:** 2026-04-20
**Domain:** Workshop-series scaffolding on a legacy Java EE codebase (Marp pipeline, Docker pinning, presenter sidecar templates, primitives glossary, QUAL gates)
**Confidence:** HIGH (decision surface is almost entirely repo-local; external facts verified against npm, Docker Hub, and Context7)

<user_constraints>
## User Constraints (from CONTEXT.md)

### Locked Decisions

**Template Depth Philosophy**
- **D-01:** `MANIFEST.template.md` uses **placeholder-annotated skeleton** — structured headers and tables with inline prompts like `[Replace: session-NN-pre SHA]`, `[Replace: slide name]`, `[Replace: compare URL]` next to every field. No fake example rows.
- **D-02:** `HANDOUT.template.md` has **fixed Spanish sections**: `¿Qué vimos?`, `Comandos para probar`, `Link de comparación`, `Próxima sesión`, `Lecturas`. Presenter fills each per session. No generic sample bullets.
- **D-03:** `REHEARSAL.template.md` has **checklist at top + free-form notes below**. Checklist forces QUAL-02/03 compliance (model-ID pinned, same-day-run date, fallback rehearsed, network plan, pre-warm steps). Notes section is open for timing, cuts, observations.
- **D-04:** QUAL gates surface **targeted** in templates (MANIFEST → QUAL-01/12; REHEARSAL → QUAL-02/03; HANDOUT → QUAL-06) **plus** one separate `docs/presentations/QUAL-GATES.md` reference doc at series root that lists all 12 gates in one place. Templates link to specific gate IDs (`see QUAL-GATES.md §QUAL-04`).

**CONCERNS Demo-Task Mapping**
- **D-05:** `docs/presentations/CONCERNS-MAPPING.md` is a **single master table** with columns: `severity | CONCERNS section/ID | claimed-by-session | slice-id | status | notes`.
- **D-06:** Claims are at **slice level**, not whole-concern level. Each of the 5 `escape="false"` XSS sites gets its own slice ID (`XSS-01`..`XSS-05`); PvpgnHash → bcrypt migration splits into `PVPGN-PREP` / `PVPGN-ROLLOVER` / `PVPGN-FINALIZE`; TorneoService god-class refactor is distinct from the N+1 fix.
- **D-07:** **Deferred / out-of-scope items live in the same file**, under a separate `## Deferred / Out-of-Scope` section with reason per item.
- **D-08:** ROADMAP.md phase notes **forward-link** to CONCERNS items they'll use. CONCERNS-MAPPING.md is **authoritative** — single direction.

**Primitives Glossary (SCAF-07 + CURR-03)**
- **D-09:** Glossary is a **standalone Marp deck** at `docs/presentations/GLOSSARY/GLOSSARY.md` (+ rendered `.html`). Each of the 6 primitives (RAG, MCP, Skill, Agent, Hook, Slash Command) has exactly one canonical slide.
- **D-10:** CURR-03 "verbatim reuse" is enforced via **reference-only embed**. Sessions that need a primitive reference do NOT copy the glossary text into their own `.md`. They embed the canonical diagram via relative path: `![RAG](../GLOSSARY/rag.svg)`, and link to the glossary deck for the spoken definition: `Ver GLOSSARY.html §RAG`. Drift is structurally impossible.
- **D-11:** A dedicated **Phase 0 plan** (e.g., `primitives-glossary`) drafts all 6 definitions and 6 explainer diagrams **before Session 1 is authored**.
- **D-12:** Explainer diagrams live canonically at `docs/presentations/GLOSSARY/{rag,mcp,skill,agent,hook,command}.mmd` + pre-rendered `.svg`.

### Claude's Discretion

- **SETUP.md depth (SCAF-02):** Default to **layered structure** — quick-start bullets (for seniors who already have the toolchain) followed by a deep appendix (for graduates / clean machines). Spanish. Covers Docker Compose, Payara 5, Postgres 15, Claude Code CLI, Node.js 20+, Marp CLI, Mermaid CLI, Ollama + `qwen2.5:7b` + `nomic-embed-text`, `tools/email-rag/` corpus setup.
- **Docker pinning strategy (SCAF-05):** Default to **digest pin + semver comment** — `image: postgres@sha256:...  # postgres:15.17-bookworm`. Digest guarantees immutability (QUAL-09 / Pitfall 8); semver comment preserves human readability and update intent.
- **Series README.md purpose (SCAF-01):** Default to **Spanish audience-facing index** per PROJECT.md language rule, with a `status` column (pending / rehearsed / delivered) as a secondary layer.
- **THEME.md shape (SCAF-03):** Default to **single copy-pasteable Marp frontmatter block** at the top of the file, followed by short variation notes. Marp has no include system; copy-paste is the honest path.
- **Phase 0 plan decomposition and parallelization:** Planner decides.

### Deferred Ideas (OUT OF SCOPE)

- Automated series-README regeneration from per-session MANIFEST front-matter (deferred — manual upkeep through 9 sessions is feasible).
- Pre-commit hook or CI job that hash-checks session glossary slides against GLOSSARY.md canonical (superseded by D-10).
- `make slides` Makefile / npm script (deferred — 9–20 sessions threshold).
- `docs/presentations/_templates/SESSION_TEMPLATE/` scaffold + `new-session.sh` copy script (deferred — same 9–20 threshold).
- Nested "seasons" folder structure (deferred — 20+ sessions threshold).
- Full worked-example MANIFEST (considered under D-01; rejected in favor of placeholder-annotated approach).
- Sample Spanish bullets in HANDOUT.template.md (rejected under D-02).
</user_constraints>

<phase_requirements>
## Phase Requirements

| ID | Description | Research Support |
|----|-------------|------------------|
| SCAF-01 | Spanish series index at `docs/presentations/README.md` listing 9 sessions (columns: #, date, slug, status, abstract, folder link, pre/post tags) | §"Series README.md" pattern; follows Anti-Pattern 3 (no duplicate setup); table schema defined below |
| SCAF-02 | Shared `docs/presentations/SETUP.md` (Spanish) — Docker Compose, Payara 5, Postgres 15, Claude Code CLI, Node.js 20+, Marp/Mermaid CLIs, Ollama + models, email-rag corpus | §"SETUP.md end-to-end" — toolchain inventory verified against local env + Docker Hub + npm registry |
| SCAF-03 | Canonical Marp theme snippet at `docs/presentations/THEME.md` referenced verbatim by every session deck's frontmatter | §"Marp Theming" — verified `theme: gaia` is inherited convention; D-04 says copy-paste snippet, not `--theme-set` |
| SCAF-04 | Extend `docs/presentations/CLAUDE.md` with NN-infix naming, sidecar files, tag convention, cached-fallback requirement | §"CLAUDE.md Extension" — current file documents Marp/Mermaid only; append sections |
| SCAF-05 | Docker images in `docker-compose.yml` pinned to immutable tags (no `latest`, no floating major-version) | §"Docker Tag Pinning" — current compose uses `postgres:15` (floating); Dockerfile uses `payara/server-full:5.2022.5` (technically immutable — 2022 Payara 5 is EOL); digests verified |
| SCAF-06 | `docs/presentations/CONCERNS-MAPPING.md` pre-claims every HIGH/MED CONCERNS.md item to a session | §"CONCERNS-MAPPING conventions" — 11 slice IDs enumerated below, matched against CONCERNS.md inventory |
| SCAF-07 | Shared primitives glossary (Marp fragment or includable) defines RAG/MCP/Skill/Agent/Hook/Slash Command (one canonical slide each) | §"Primitives Glossary" — D-10 reference-only embed; Marp has no include system, D-12 pins canonical `.mmd`/`.svg` paths |
| SCAF-08 | `MANIFEST.template.md` with session title, date, pre/post tag SHAs, slide-to-commit map, recovery command, compare URL, known follow-ups | §"Sidecar Templates" — verified against ARCHITECTURE.md Pattern 1 Example |
| SCAF-09 | `REHEARSAL.template.md` — rehearsal checklist (model-ID, same-day-run, fallback, network, pre-warm) | §"Sidecar Templates" — checklist fields derived from QUAL-02/03/09/12 |
| SCAF-10 | `HANDOUT.template.md` — short Spanish takeaway with compare URL, next-session prereq, recommended reading | §"Sidecar Templates" — Spanish sections from D-02 |
| QUAL-01 | Every session folder contains completed MANIFEST.md (title, date, pre/post SHA, slide-to-commit map, recovery command, compare URL) | Satisfied by SCAF-08 template; §"Validation Architecture" grep-patterns verify per session |
| QUAL-02 | Every session has pre-recorded asciinema/VHS fallback committed next to deck; switchover rehearsed | Referenced in SCAF-04 extension + SCAF-09 checklist; §"CLAUDE.md Extension" adds fallback section |
| QUAL-03 | Same-day rehearsal within 24h logged in REHEARSAL.md | Embedded in SCAF-09 checklist |
| QUAL-04 | Every session includes "Lo que la IA NO hizo" honesty slide near the end | Enumerated in QUAL-GATES.md reference doc (D-04); phase-0 writes the doc, phases 1-9 enforce |
| QUAL-05 | Every session touches ≥1 file/artifact in CONCERNS.md (Session 2 exception via script) | Enabled by SCAF-06 CONCERNS-MAPPING.md — pre-claiming ensures every session has a target |
| QUAL-06 | Every session declares bilingual convention on-screen ≥1 time | Listed in QUAL-GATES.md; Session 1 authors the canonical slide per CONTEXT D-02 pattern |
| QUAL-07 | Every session's demo task pre-sliced to 57-min on-paper budget | Enumerated in QUAL-GATES.md; CONCERNS-MAPPING slice granularity (D-06) makes this enforceable |
| QUAL-08 | Migration-slice sessions include "known follow-ups" closing slide + GitHub issues | Listed in QUAL-GATES.md; applies to PVPGN-* and XSS-* and TORNEO-* slices |
| QUAL-09 | Payara + Postgres pre-warmed 10min before session; `docker compose up` never a live step | Listed in QUAL-GATES.md; SCAF-05 Docker pinning removes cold-pull risk at live time |
| QUAL-10 | Single-primitive primary teaching surface; no multi-primitive salad in first 30 min | Listed in QUAL-GATES.md (Pitfall 18); SCAF-07 primitives glossary makes it declarative rather than ad-hoc |
| QUAL-11 | Every session's deck rendered to `.html`; Mermaid rendered to `.svg` | Already encoded in `docs/presentations/CLAUDE.md`; re-surface in QUAL-GATES.md for self-containment |
| QUAL-12 | Every session's MANIFEST names Claude Code CLI version, model ID, Ollama/MCP/Marp/Mermaid versions at rehearsal + delivery | Encoded in SCAF-08 template fields |

</phase_requirements>

## Project Constraints (from CLAUDE.md)

- **Spanish-language rule:** Package, class, entity, view, and database names stay Spanish. Applies to user-facing README / SETUP / templates / glossary. Planning docs may stay English.
- **Stack:** Java 8, JSF 2 + PrimeFaces 4, EJB Stateless/Singleton, JPA 2 (EclipseLink), Maven WAR (`DotaCL.war`), Payara 5 in Docker, PostgreSQL 15. SETUP.md must reflect this exactly.
- **Build & deploy commands:** `mvn -o package`, `docker compose up -d --build`, `./dev-sync.sh java|views|all`. SETUP.md steps must use these.
- **Off-limits folders:** `src/java/controller/` and `com.dotachile.automation.FunService`. Phase 0 should not touch these; Hooks (Session 7) will block writes there.
- **Email corpus:** `../dotachile-emails/corpus` sibling dir, auto-mounted via `.claude/settings.json`. SETUP.md must document per-developer corpus-build path (`tools/email-rag/README.md`).

## Summary

Phase 0 is **pure scaffolding** — no teaching content, no live session. Everything it produces is static artifacts that either (a) get copied per-session (templates), (b) get referenced per-session (glossary, theme), or (c) get grep-checked per-session (QUAL gates, CONCERNS-MAPPING). The research surface is almost entirely **repo-local**: the Marp pipeline, the Docker compose file, the existing `docs/presentations/` convention, and the CONCERNS.md debt inventory are all in-tree. Two external facts were verified this session to eliminate the last training-data uncertainty: (1) Marp CLI's `--theme` option accepts a file path directly, but D-04 rejects that route in favor of a copy-pasteable frontmatter snippet; (2) current Postgres 15 and Payara 5 image digests are captured in this doc so the planner can pin immediately without re-querying.

The plan surface breaks cleanly into **7 independent artifacts** plus 1 series-index-plan that follows. The dependency pattern is: artifacts stand alone; the series README (SCAF-01) summarizes them and must be written last. Templates (MANIFEST/HANDOUT/REHEARSAL) can be a single plan or three — they share no data, only the common QUAL-GATES.md reference doc they link to. `QUAL-GATES.md` itself is a small reference artifact that both MANIFEST.template and REHEARSAL.template refer to — it should be authored alongside templates, not before them.

**Primary recommendation:** Plan this phase as 7 parallel plans ending in a series-index plan that reads the rest. Target plan headcount is 7-8, aligning with ROADMAP.md's expectation.

## Architectural Responsibility Map

Phase 0 artifacts are documentation only — there are no runtime tiers. Map is by **artifact ownership layer**:

| Capability | Primary Layer | Secondary Layer | Rationale |
|------------|---------------|-----------------|-----------|
| Spanish audience-facing index (SCAF-01) | Series root docs | — | Entry point for anyone opening `docs/presentations/` |
| End-to-end tool install guide (SCAF-02) | Series root docs | CLAUDE.md linkage | One canonical place; per-session MANIFEST links to it |
| Canonical Marp frontmatter (SCAF-03) | Series root docs | Deck source `.md` | Copy-pasted by each session deck's top block |
| Authoring conventions (SCAF-04) | `docs/presentations/CLAUDE.md` | Agent reads on session-plan-phase | Already-existing conventions doc extended in-place |
| Docker image pinning (SCAF-05) | `docker-compose.yml` + `Dockerfile` | SETUP.md notes pinning cadence | Mutates existing infrastructure files; no new file |
| CONCERNS → Session map (SCAF-06) | Series root docs | ROADMAP.md forward-refs | Authoritative single-source table |
| Primitives glossary (SCAF-07) | `docs/presentations/GLOSSARY/` subdir | Session decks reference via relative path | Standalone Marp deck + 6 `.mmd`/`.svg` pairs |
| Session sidecar templates (SCAF-08/09/10) | Series root docs | Copied into each session folder on session-plan-phase | Templates; consumption is per-session |
| QUAL gates reference (derived from D-04) | Series root docs | Templates link by gate ID | Single source; 12 bullets; no duplication |

**Why this matters:** Phase 0 has no runtime risk (no code mutates behavior), but the artifact layering risk is real: if glossary text is inlined in session decks instead of referenced via `../GLOSSARY/`, CURR-03 drift becomes inevitable. Likewise, if QUAL gate text is embedded verbatim in all three templates, the next gate addition requires three edits. D-04 and D-10 prevent both.

## Marp Theming (SCAF-03)

**What the planner needs to know:**

1. **Marp CLI `--theme` accepts a path directly.** Verified via Context7 `/marp-team/marp-cli`: `marp --theme custom-theme.css` works. The `@theme` CSS metadata comment is NOT required when passing a CSS file directly to the CLI. (Normal Marpit rule requires it; the CLI relaxes this.) `[VERIFIED: Context7 /marp-team/marp-cli, Theme Management section]`
2. **`--theme-set` is the "register multiple themes" path.** Accepts space-separated paths or a directory. Once registered, `theme:` frontmatter key looks up by theme name (from the CSS `/* @theme xxx */` comment). `[VERIFIED: Context7 /marp-team/marp-cli, Configure Themes section]`
3. **Existing decks use `theme: gaia`** (built-in theme). `[VERIFIED: grep of 2026-04-08-mas-alla-del-hype.md and 2026-04-10-ai-driven-development.md]`
4. **Marp has no Markdown include system.** Shared frontmatter must be copy-pasted. `[CITED: .planning/research/ARCHITECTURE.md Pattern 3]`
5. **Marp Mermaid integration pitfall:** font sizes misdetect inside `<foreignObject>` when Mermaid renders inside Marp at runtime. Pre-rendered SVG avoids this entirely. `[VERIFIED: WebSearch 2026-04, consistent with existing docs/presentations/CLAUDE.md convention]` Existing convention holds.

**Concrete pattern for THEME.md (per D-04 decision):**

```markdown
# Marp Theme — Canonical frontmatter

Copy this block to the top of every session deck. Do not modify.

\`\`\`yaml
---
marp: true
theme: gaia
class: lead
paginate: true
backgroundColor: "#fff"
color: "#1e1e2e"
title: "[Replace: Session NN — Title in Spanish]"
author: "Pablo Martínez"
---
\`\`\`

## Variations

- **Dark background (diagrams with light lines):** set `backgroundColor: "#1e1e2e"` and `color: "#cdd6f4"` — matches the `2026-04-08-mas-alla-del-hype` deck.
- **Light background (default, matches 2026-04-10 deck):** use the block above.

## Why gaia, not a custom CSS

Two existing decks already use gaia. A custom theme would require `--theme-set` at every render invocation and would risk breaking HTMLPreview compatibility. D-04 chose simplicity — single copy-pasteable block.
```

**Pitfalls:**

- **Don't point `theme:` at a relative path** in the frontmatter (e.g., `theme: ../THEME.css`) — Marp CLI resolves theme names, not paths in frontmatter. Paths go through `--theme` or `--theme-set` flag. `[VERIFIED: Context7]`
- **Don't rely on runtime Mermaid JS.** Pre-rendered SVG is the rule (already in `docs/presentations/CLAUDE.md`). HTMLPreview rewrites `<script>` tags. `[VERIFIED: existing repo convention + WebSearch 2026-04]`
- **Don't add `html: true` to frontmatter** unless speaker notes require raw HTML beyond `<!-- -->` comments. The existing decks render with `--html` CLI flag and work correctly. `[VERIFIED: grep of existing decks shows no `html:` frontmatter key]`

## Docker Tag Pinning (SCAF-05)

**What the planner needs to know:**

1. **Current state in `docker-compose.yml` (line 3):** `image: postgres:15` — a floating major-version tag. Resolves today to a 15.17 variant but may change at any `docker compose pull`. `[VERIFIED: Read of docker-compose.yml]`
2. **Current state in `Dockerfile`:**
   - Line 2: `FROM maven:3.8-openjdk-11 AS build` — semver-minor + major, reasonably stable but `3.8` still floats to latest 3.8.x. `[VERIFIED]`
   - Line 13: `FROM payara/server-full:5.2022.5` — immutable in practice because Payara 5 is EOL and no newer 5.2022.x exists. `[VERIFIED: Docker Hub API — 5.2022.5 last pushed 2022-12-13; no 5.2022.6 exists]`
3. **`docker compose config` output already resolves tags to `<image>:<tag>@sha256:<digest>` at invocation time.** `[VERIFIED this session: docker manifest inspect shows postgres:15 resolves to sha256:da25c93aae4f36f6675d505977c78a63bf3995e1ac29074d06f4453d8b5e6350 on amd64 today]` But this resolution is NOT stored in the compose file itself — the digest is re-resolved on every pull. Immutability requires committing the digest into `image:` directive.
4. **Pinning syntax:** `image: postgres:15.17-bookworm@sha256:ea647d76ba6059d92926662900af0f5d4bcaa9adcb1de477a32f80db3f14b9fe` (digest after `@`, tag before `@` preserves human readability). `[VERIFIED: Docker Compose docs 2026 and Docker Docs Image Digests page]`
5. **Concrete digests verified this session (2026-04-20):**

| Image | Current tag in repo | Recommended pinned reference | Digest (amd64) | Verified |
|-------|---------------------|------------------------------|----------------|----------|
| `postgres` | `15` (floating) | `postgres:15.17-bookworm@sha256:ea647d76ba6059d92926662900af0f5d4bcaa9adcb1de477a32f80db3f14b9fe` | `sha256:ea647d76...` | `[VERIFIED: hub.docker.com/v2/repositories/library/postgres/tags/15.17-bookworm/ 2026-04-20]` |
| `maven` | `3.8-openjdk-11` (floating minor) | `maven:3.8.8-openjdk-11@sha256:<amd64-digest>` | *must be fetched at pinning time* | `[CITED: Docker Hub]` — planner runs `docker manifest inspect maven:3.8-openjdk-11` to get digest |
| `payara/server-full` | `5.2022.5` (effectively immutable) | `payara/server-full:5.2022.5@sha256:95f45ebc141eb68f1e572725b570aad03059a4e8ab34e590f8f7c7259011df75` | `sha256:95f45ebc...` | `[VERIFIED: hub.docker.com/v2/repositories/payara/server-full/tags/5.2022.5/ 2026-04-20]` |

**Concrete pattern:**

```yaml
# docker-compose.yml (post-pin)
services:
  db:
    image: postgres:15.17-bookworm@sha256:ea647d76ba6059d92926662900af0f5d4bcaa9adcb1de477a32f80db3f14b9fe
    # Updated 2026-04-20. See docs/presentations/SETUP.md §"Docker pinning update cadence"
```

```dockerfile
# Dockerfile (post-pin)
FROM maven:3.8.8-openjdk-11@sha256:<fetched-at-pin-time> AS build
# ...
FROM payara/server-full:5.2022.5@sha256:95f45ebc141eb68f1e572725b570aad03059a4e8ab34e590f8f7c7259011df75
```

**Verification command** (for plan acceptance criteria):

```bash
docker compose config | grep -E 'image:' | grep -v '@sha256:' && echo "FAIL: unpinned image found" || echo "PASS: all images digest-pinned"
```

Note: `docker compose config` output CAN include non-pinned images even if the source file is pinned (if inherited from a base compose). The more reliable check is grep against the `docker-compose.yml` source itself.

**Pitfalls:**

- **Don't pin only `postgres:15`** — that's still floating. `[VERIFIED: 15 resolves to 15.17 today, will resolve to 15.18 tomorrow]`
- **Don't pin to `-latest` variants.** `postgres:latest` is the largest source of "works today, broken tomorrow" failures. `[CITED: PITFALLS.md Pitfall 8]`
- **Don't forget arm64.** Docker Hub returns a list of manifests per-arch. The `amd64` digest this doc cites will NOT work for Apple Silicon without a multi-arch manifest. Use the **top-level manifest-list digest** (not arch-specific) when pinning cross-platform: `docker buildx imagetools inspect postgres:15.17-bookworm --format "{{json .Manifest}}" | jq -r .digest`. `[CITED: Docker Docs, Marc Nuri blog]`
- **Document the update cadence** in SETUP.md. Monthly re-pin is a reasonable default; the plan should not leave this implicit.

## CONCERNS → Session Mapping (SCAF-06)

**What the planner needs to know:**

CONCERNS.md has the following HIGH/MED items. D-06 says claims are at slice level. The table below is the planner's starting point — the actual session assignments may shift during per-session plan-phase, but every row must be claimed or deferred before Session 1 runs.

| # | Severity | CONCERNS section | Slice ID | Suggested session | Rationale |
|---|----------|------------------|----------|-------------------|-----------|
| 1 | HIGH | Tech debt — EOL stack | `EOL-STACK` | deferred | Structural; not a one-session migration; may surface in Session 2 as framing |
| 2 | MED | Tech debt — TorneoService god-class | `TORNEO-GODCLASS` | Session 6 (Agents) | D-06 splits this from `TORNEO-N1`; Agents session investigates the god-class via subagent (per S06-01) |
| 3 | MED | Tech debt — Scattered TODOs (39) | `TODOS-SWEEP` | deferred | Grep target; useful in Session 6 as subagent fodder but not a primary slice |
| 4 | HIGH | Security — PvpgnHash weak hashing | `PVPGN-PREP` | Session 5 (Skills) or Session 6 | D-06 splits into 3; PREP = "set up dual-hash columns + migration plan" |
| 4a | HIGH | Security — PvpgnHash | `PVPGN-ROLLOVER` | deferred or Session 6 | Dual-hash-on-login; mid-migration slice |
| 4b | HIGH | Security — PvpgnHash | `PVPGN-FINALIZE` | deferred (v2) | Remove legacy hash; multi-week operational concern |
| 5 | HIGH | Security — XSS `VerNoticia.xhtml` | `XSS-01` | Session 1 or Session 8 | Session 8's `/dota-audit-xss` command enumerates all 5; Session 1 can fix one live |
| 5a | HIGH | Security — XSS `VerTorneo.xhtml` | `XSS-02` | Session 8 | Audited by `/dota-audit-xss` |
| 5b | HIGH | Security — XSS `VerVideos.xhtml` | `XSS-03` | Session 8 | Audited |
| 5c | HIGH | Security — XSS `Seleccion.xhtml` | `XSS-04` | Session 8 | Audited |
| 5d | HIGH | Security — XSS `index.xhtml` | `XSS-05` | Session 8 | Audited; candidate for Session 5 Skill target (`escape-false-guard`) |
| 6 | MED | Security — FilesServlet path traversal | `FILES-TRAVERSAL` | deferred | Multi-hour audit; not session-sized |
| 7 | MED | Security — No CSRF on JSF forms | `CSRF-ABSENT` | deferred | Config change + audit; multi-hour |
| 8 | MED | Security — FORM auth / plaintext in-transit | `FORM-AUTH-TLS` | deferred | Ops concern; not demo-shaped |
| 9 | HIGH | Performance — N+1 in TorneoService | `TORNEO-N1` | Session 5 (Skills) or Session 6 (Agents) | Distinct from `TORNEO-GODCLASS`; 1-2 commit slice |
| 10 | MED | Performance — Scheduled jobs iterate without batching | `SCHED-BATCH` | deferred | Multi-file; low urgency |
| 11 | MED | Performance — 06:00 scheduler contention | `SCHED-CONTENTION` | deferred | Operational; not session-shaped |
| 12 | MED | Fragile — `dev-sync.sh` load-bearing | `DEV-SYNC-FRAGILE` | deferred | Documentation / hardening; not demo |
| 13 | MED | Fragile — Docker not pinned at Payara layer | `DOCKER-PINS` | Phase 0 (this phase) | SCAF-05 satisfies this directly |
| 14 | MED | Fragile — Entity equals/hashCode | `ENTITY-EQUALS` | deferred | Teaching topic but not session-sized |

**Proposed claim pattern for the CONCERNS-MAPPING.md table:**

```markdown
## Demo-Task Bank — HIGH/MED Claims

| Severity | CONCERNS section/ID | Slice ID | Claimed by | Status | Notes |
|----------|---------------------|----------|------------|--------|-------|
| HIGH | Security §XSS | XSS-01 | Session 8 | claimed | Audited live via `/dota-audit-xss` |
| HIGH | Security §XSS | XSS-02 | Session 8 | claimed | Audited |
| ... |

## Deferred / Out-of-Scope

| Slice ID | Reason | Revisit |
|----------|--------|---------|
| EOL-STACK | Structural; no one-session migration fits | v2 arc |
| PVPGN-FINALIZE | Multi-week operational | v2 |
| FILES-TRAVERSAL | Multi-hour audit, not demo | v2 |
| CSRF-ABSENT | Config + audit; not session-sized | v2 |
...
```

**Pitfalls:**

- **Don't pre-assign PVPGN-PREP and TORNEO-N1 to specific sessions in Phase 0.** Per CONTEXT.md "Claude's Discretion", the planner's CONCERNS-MAPPING.md should list them as *candidates* for Sessions 5/6; final assignment happens at Session 5/6 plan-phase. Lock only XSS-* to Session 8 (tightly coupled to S08-01 requirement) and TORNEO-GODCLASS to Session 6 (tightly coupled to S06-01 requirement).
- **Don't treat the slice IDs as immutable API.** `XSS-01`..`XSS-05` need to be greppable and stable, but if a session reshuffles them (e.g., deciding XSS-01 = VerNoticia is actually easier than XSS-05 = index), the reassignment happens in one file. Cheap to fix.
- **Don't drop slice IDs once deferred.** Keep the row; mark status=deferred. Audit trail for v2.

## Primitives Glossary Fragment (SCAF-07 + CURR-03)

**What the planner needs to know:**

1. **6 primitives to define.** RAG, MCP, Skill, Agent, Hook, Slash Command. One slide each. `[VERIFIED: REQUIREMENTS.md SCAF-07, CURR-03]`
2. **Marp has no include mechanism.** `[VERIFIED: Context7 /marp-team/marp-cli — no partial/include directive exists]`
3. **D-10 reference-only embed is the only zero-drift answer.** Session decks embed the canonical diagram via relative path (`../GLOSSARY/rag.svg`) and link to the glossary deck for the spoken definition (`Ver GLOSSARY.html §RAG`). Text is never copied.
4. **Canonical file structure** (from D-09, D-12):

```
docs/presentations/GLOSSARY/
├── GLOSSARY.md            # Marp source — 6 primitive slides, one each, Spanish
├── GLOSSARY.html          # Rendered with marp-cli --html, committed
├── rag.mmd                # Mermaid source (control + data flow)
├── rag.svg                # Pre-rendered
├── mcp.mmd
├── mcp.svg
├── skill.mmd
├── skill.svg
├── agent.mmd
├── agent.svg
├── hook.mmd
├── hook.svg
├── command.mmd
└── command.svg
```

5. **Slide structure template** (per primitive):

```markdown
# <Primitive name in Spanish>

**Concepto en 1 línea:** [one-sentence definition in Spanish]

**Qué hace:**
- [bullet 1]
- [bullet 2]
- [bullet 3]

![<primitive>](./<primitive>.svg)

> <one-line teaching quote — the "memorable" phrase>

<!-- Slide referenced by: Sessions 1, 3, 4, 5, 6, 7, 8 -->
```

6. **Diagram convention** — data + control flow shown explicitly. The existing reference `2026-04-10-ai-driven-development-ciclo.mmd` (4 lines) uses `graph LR` with labeled nodes and `A --- A1` for sub-relationships. Use the same idiom.

7. **Existing reference material** for each primitive's definition:
   - **RAG:** `2026-04-10-ai-driven-development.md` has a "RAG en 1 slide" block + `tools/email-rag/search.py` as the implementation artifact. Session 3 leans on this.
   - **MCP:** Same deck has "MCP en 1 slide" — "Model Context Protocol — el estándar abierto para que las IA hablen con herramientas externas".
   - **Skill:** Same deck has "Skills en 1 slide" — "Un skill es un manual que Claude carga a demanda".
   - **Agent:** Not in existing decks; research ARCHITECTURE.md says "sub-agents with their own context/tools".
   - **Hook:** Not in existing decks; research SUMMARY.md says "deterministic layer around stochastic LLM output" (the Session 7 framing).
   - **Slash Command:** Not in existing decks; research SUMMARY.md says "user-triggered entry points composing Skills, Agents, and Hooks".

**Pitfalls:**

- **Don't inline definitions in session decks.** D-10 rejected this. Every session referencing a primitive does `![RAG](../GLOSSARY/rag.svg)` + `Ver GLOSSARY.html §RAG`. Two lines. `[DERIVED: D-10]`
- **Don't write the glossary in English.** Spanish-language rule applies. `[CITED: CLAUDE.md]`
- **Don't make the diagrams decorative.** Session 1's S01-03 requires "explainer diagrams showing data/control flow" — decorations won't satisfy that gate. Draw the real data flow.
- **Don't paginate the glossary deck.** Setting `paginate: false` in the GLOSSARY.md frontmatter is correct — it's a reference doc, not a linear presentation.

## SETUP.md End-to-End (SCAF-02)

**What the planner needs to know:**

This is the most landmine-heavy artifact. The install chain has platform-specific quirks and model-tag drift.

### Tool inventory (verified versions 2026-04-20)

| Tool | Version to pin | Verification source |
|------|---------------|---------------------|
| Docker Desktop | ≥24.0.2 (current local) | `docker --version` local check |
| Docker Compose | ≥2.19.0 (current local) | `docker compose version` local check |
| Node.js | ≥20.x (project decision), local is v22.19.0 | `node --version` |
| npm | 10.x | ships with Node |
| Marp CLI | `@marp-team/marp-cli@4.3.1` | `[VERIFIED: npm view @marp-team/marp-cli version 2026-04-20]` |
| Mermaid CLI | `@mermaid-js/mermaid-cli@11.12.0` | `[VERIFIED: npm view 2026-04-20]` |
| Claude Code CLI | latest stable | user-scoped |
| Ollama | ≥0.5.x | ecosystem stable |
| Ollama model (gen) | `qwen2.5:7b` (~4.7 GB) | `[CITED: research/STACK.md + research/SUMMARY.md; drift monthly]` |
| Ollama model (embed) | `nomic-embed-text:v1.5` (~275 MB) | `[CITED: research/STACK.md; research gate flagged for Session 3 re-verify]` |
| Python | 3.11+ (local is 3.11.3) | needed for `tools/email-rag/.venv` |
| asciinema | 2.4+ | `brew install asciinema` (macOS) |
| VHS | 0.11.0 (2026-03-10 release) | `brew install vhs` `[VERIFIED: github.com/charmbracelet/vhs]` |

### Layered structure (Claude's Discretion default)

```markdown
# SETUP.md

## Quick-start (si ya tenés el toolchain)

\`\`\`bash
docker compose up -d
./dev-sync.sh all
tools/email-rag/.venv/bin/python tools/email-rag/search.py "test"
\`\`\`

Si cualquiera de esos tres falla, lee el Apéndice.

## Apéndice: setup desde cero

### 1. Docker + Docker Compose
...

### 2. Node.js 20+ (para Marp CLI y Mermaid CLI)
...

### 3. Claude Code CLI
...

### 4. Marp CLI + Mermaid CLI
\`\`\`bash
npx @marp-team/marp-cli@4.3.1 --version  # verifica instalación
npx @mermaid-js/mermaid-cli@11.12.0 --version
\`\`\`

### 5. Ollama + modelos
\`\`\`bash
ollama pull qwen2.5:7b         # ~4.7 GB — modelo generativo en español
ollama pull nomic-embed-text   # ~275 MB — embeddings para RAG
\`\`\`

Los tags de Ollama driftean mes a mes. Verifica en ollama.com/library el día del workshop.

### 6. tools/email-rag/ — corpus
El corpus NO está en el repo. Cada developer lo construye desde su Gmail Takeout.
Ver `tools/email-rag/README.md`.

Si `search.py` sale con `error: corpus dir not found`, no construiste el corpus todavía. Los demos que usan RAG (Session 3) necesitan esto; los demás sesiones no lo requieren.

### 7. asciinema + VHS (opcional — para fallback recordings)
\`\`\`bash
brew install asciinema vhs
\`\`\`

### 8. Docker images pre-pull
Para evitar pull en vivo durante el workshop:
\`\`\`bash
docker compose pull
\`\`\`

## Cadencia de actualización de pins de Docker

Re-pinear los digests de `docker-compose.yml` mensualmente. Comando:
\`\`\`bash
docker manifest inspect postgres:15.17-bookworm | jq -r '.manifests[] | select(.platform.architecture=="amd64") | .digest'
\`\`\`
```

### Landmines to document

1. **Ollama on Apple Silicon vs Intel:** `qwen2.5:7b` is 4.7 GB and needs ~8 GB free RAM. On 8 GB Macs, the model swaps heavily. Document as a hard prerequisite, not a footnote. `[CITED: Ollama ecosystem]`
2. **Docker Desktop vs Colima vs Rancher Desktop:** the repo uses `docker compose` (v2 syntax). `docker-compose` (v1) does not understand some v2-only keys. `[CITED: docker-compose.yml uses v2 syntax — `condition: service_healthy`]`
3. **Marp CLI global install vs npx:** existing convention uses `npx @marp-team/marp-cli@latest` — ephemeral. Pin to `@4.3.1` in SETUP to avoid "works on my machine" drift. Global install creates `$PATH` conflicts with other Marp versions.
4. **Email corpus is per-developer + gitignored.** `[CITED: CONCERNS.md LOW-severity "Email corpus tool"]` Sessions that use RAG (Session 3) must include a toy fixture (S03-05). Phase 0's SETUP.md must frame this clearly.
5. **dev-sync.sh is load-bearing but fragile.** `[CITED: CONCERNS.md MED]` SETUP.md should link to `dev-sync.sh` header comments (or document the 3 subcommands explicitly) + name the failure fallback (`docker compose restart app`).
6. **Payara boot time:** `docker compose up -d` + Payara 5 boot on cold image is 60-90 seconds. QUAL-09 requires pre-warming 10 min before session. SETUP.md should name the pre-warm as a ritual, not a performance note.

## CLAUDE.md Extension Strategy (SCAF-04)

**What the planner needs to know:**

1. **Existing file is 57 lines** covering Marp rendering, Mermaid pre-rendering, Spanish rule. Published 2026-04-19. `[VERIFIED: Read of docs/presentations/CLAUDE.md]`
2. **SCAF-04 requires extension, not replacement.** The Marp/Mermaid/HTML conventions are load-bearing and already referenced by the existing two decks. `[CITED: CONTEXT.md canonical_refs + code_context]`
3. **Four new subsections must be added:**
   - `## Convención de numeración NN-<slug>` — why the NN infix, how pre-series decks are un-numbered
   - `## Sidecar per sesión: MANIFEST.md, HANDOUT.md, REHEARSAL.md` — names the three files, points at `../MANIFEST.template.md` etc.
   - `## Git tags: session-NN-pre / session-NN-post` — annotated tags, pushed, immutable
   - `## Fallback artifact (asciinema / VHS)` — QUAL-02 rule, pre-recorded next to deck, switchover rehearsed
4. **Link to QUAL-GATES.md** at the top — "Every session must satisfy the 12 QUAL gates in `QUAL-GATES.md`. This doc explains the mechanics; QUAL-GATES explains the gates."

**Proposed extension pattern** (append, don't rewrite):

```markdown
## Convención de numeración NN-<slug>

Desde la serie AI-SWE 2026, las presentaciones siguen el formato
`YYYY-MM-DD-NN-<slug>/` donde `NN` es el número de sesión en el arco (01–09).
Los decks pre-serie (`2026-04-08-mas-alla-del-hype`, `2026-04-10-ai-driven-development`)
se quedan sin NN — son material crudo, no parte del arco.

## Sidecar per sesión

Cada sesión numerada contiene:
- `MANIFEST.md` — mapa slides↔commits, plan de recuperación, versions
- `HANDOUT.md` — takeaway corto para la audiencia (español)
- `REHEARSAL.md` — bitácora del dry-run: checklist + timing + cortes

Los templates canónicos viven en:
- `../MANIFEST.template.md`
- `../HANDOUT.template.md`
- `../REHEARSAL.template.md`

Al iniciar una sesión, se copian los 3 al folder nuevo y se rellenan.

## Git tags: session-NN-pre / session-NN-post

Antes de cada sesión en vivo:
\`\`\`bash
git tag -a session-NN-pre -m "Session NN — Pre-demo state"
git push origin session-NN-pre
\`\`\`

Después de la sesión:
\`\`\`bash
git tag -a session-NN-post -m "Session NN — Post-demo state"
git push origin session-NN-post
\`\`\`

Son **annotated** tags, **no movibles**. Re-teaches usan branches fuera de `session-NN-pre`.

## Fallback artifact (QUAL-02)

Cada sesión DEBE tener un artefacto de fallback grabado previamente junto al deck:
- **asciinema** (`.cast`) — captura en vivo del demo; reproducible con `asciinema play`.
- **VHS** (`.tape`) — script determinístico; reproducible con `vhs < file.tape`.

El switchover de live → fallback se ensaya una vez en REHEARSAL.md.

## QUAL gates

Ver `../QUAL-GATES.md` — las 12 reglas que cada sesión del arco debe cumplir.
```

**Pitfalls:**

- **Don't rewrite the existing Marp/Mermaid/HTML sections.** They are already cited by other docs (research/ARCHITECTURE.md, STACK.md). Breaking the anchor positions causes link rot in planning docs.
- **Don't make this Spanish-only.** This is a presenter-facing conventions doc; English is tolerated per PROJECT.md. But examples within should be in the language they'd appear in the deck (Spanish section names, English git commands). The existing file mixes both.

## Sidecar Templates (SCAF-08/09/10)

**What the planner needs to know:**

### MANIFEST.template.md (SCAF-08)

Must contain (per QUAL-01 + QUAL-12):

- Session title and date
- Pre-tag SHA (placeholder: `[Replace: SHA after git tag -a session-NN-pre]`)
- Post-tag SHA (placeholder: `[Replace: SHA after git tag -a session-NN-post — post-live only]`)
- Slide-to-commit map (table: slide #, deck section, commit SHA, notes)
- Recovery command template (placeholder: `git reset --hard session-NN-pre`)
- GitHub compare URL template (placeholder: `https://github.com/<org>/dotachile/compare/session-NN-pre...session-NN-post`)
- Known follow-ups section
- **Version pins (QUAL-12):** Claude Code CLI version, model ID, Ollama/MCP/Marp/Mermaid versions
- Reference line: `see QUAL-GATES.md §QUAL-01, §QUAL-12`

**Reference implementation exists** at research/ARCHITECTURE.md Pattern 1 Example (lines 114-145) — use as the structural template for placeholder annotation.

### REHEARSAL.template.md (SCAF-09)

Must contain (per QUAL-02, QUAL-03, QUAL-09):

- **Checklist at top** (D-03 requirement):
  - [ ] Model ID pinned (e.g., `claude-opus-4-7`) — QUAL-12
  - [ ] Fallback artifact exists next to deck — QUAL-02
  - [ ] Fallback switchover rehearsed once — QUAL-02
  - [ ] Same-day rehearsal date recorded — QUAL-03
  - [ ] Network plan: venue WiFi tested OR hotspot ready — Pitfall 1
  - [ ] Payara + Postgres pre-warm ritual: `docker compose up -d` + dummy request, ≥10 min before session — QUAL-09
  - [ ] Deck `.html` renders cleanly (HTMLPreview compatible)
  - [ ] All `.mmd` diagrams have pre-rendered `.svg`
  - [ ] Reference: `see QUAL-GATES.md §QUAL-02, §QUAL-03, §QUAL-09`
- **Free-form notes below** (D-03 requirement):
  - Timing per section
  - Sections cut
  - Observed AI flakes / manual corrections
  - "Lo que la IA NO hizo" candidates (QUAL-04 material)

### HANDOUT.template.md (SCAF-10)

Must contain (per D-02 fixed Spanish sections + QUAL-06):

```markdown
# [Replace: Session NN — Título]

**Fecha:** [Replace: YYYY-MM-DD]
**Deck:** [Replace: link to deck .html]

## ¿Qué vimos?

- [Replace: bullet 1]
- [Replace: bullet 2]
- [Replace: bullet 3]

## Comandos para probar

\`\`\`bash
[Replace: comandos que la audiencia puede correr post-sesión]
\`\`\`

## Link de comparación

- Pre → Post: https://github.com/[Replace: org/repo]/compare/session-NN-pre...session-NN-post

## Próxima sesión

- [Replace: session NN+1 — slug + fecha + 1-liner]
- Prereqs adicionales: [Replace: or "ninguno más allá de SETUP.md"]

## Lecturas

- [Replace: link 1]
- [Replace: link 2]

<!-- see QUAL-GATES.md §QUAL-06 for bilingual-convention note -->
```

### QUAL-GATES.md (referenced by templates via D-04)

This is the 13th artifact — the single-source-of-truth gates doc. Lives at `docs/presentations/QUAL-GATES.md`. Lists all 12 QUAL gates by ID, one paragraph each (what it is, why it exists, how it's verified). Templates link to specific gate IDs rather than duplicating the text.

**Structure:**

```markdown
# QUAL Gates — Reference

Las 12 reglas que cada sesión del arco 2026 debe cumplir. Esta doc es la única fuente.
Los templates (MANIFEST/HANDOUT/REHEARSAL) linkean por ID, no duplican.

## QUAL-01 — MANIFEST.md completo

[Descripción; how it's verified; template reference]

## QUAL-02 — Fallback artifact + switchover rehearsed

[...]

...

## QUAL-12 — Version pins en MANIFEST
```

**Pitfalls:**

- **Don't put rehearsal-only content in MANIFEST.** Version pins and pre-tag SHA are pre-session; compare URL and slide-to-commit map are post-session. Template should mark which fields are pre vs post.
- **Don't make the checklist a markdown table.** Checklists need `- [ ]` syntax to be greppable (`grep '^- \[x\]' REHEARSAL.md | wc -l` vs total) — a table obscures this.
- **Don't embed placeholder commits in the slide-to-commit map.** D-01 rejected fake example rows. Use `| N | [Replace: slide name] | [Replace: SHA] | [Replace: notes] |`.

## Runtime State Inventory

> Not applicable — Phase 0 is pure documentation. No runtime state, no databases, no live services, no secrets, no build artifacts that embed a renamed identifier. The single mutation to existing files is `docker-compose.yml` image pinning (SCAF-05) and `Dockerfile` digest pinning. `docker compose down && docker compose up -d` after the pin cleanly transitions; no cached state to migrate.

## Common Pitfalls

### Pitfall 1: Glossary text copied into session decks (CURR-03 drift)

**What goes wrong:** A session author reads GLOSSARY.md, copy-pastes the "RAG" definition into their own deck for self-containedness. Three sessions later, the definitions drift.

**Why it happens:** Marp has no include system; copy-paste is the default when in a hurry.

**How to avoid:** D-10 reference-only embed pattern is explicit. Sessions embed `../GLOSSARY/rag.svg` and link `Ver GLOSSARY.html §RAG`. Text is never duplicated. The planner can enforce this via a grep check at plan-phase: `grep -l "Retrieval Augmented Generation" docs/presentations/*/` should return only GLOSSARY.

**Warning signs:** Any session's deck contains a block that looks like the glossary definition. Grep-based check works.

### Pitfall 2: Docker digest pins go stale without cadence

**What goes wrong:** Pinning to `postgres:15.17-bookworm@sha256:ea647d...` freezes the image at April 2026. Six months later, the image has security patches the pinned digest misses.

**Why it happens:** Pinning is a one-shot operation; re-pinning cadence is not institutional.

**How to avoid:** SETUP.md includes a "Cadencia de actualización" section. Re-pin monthly. Document the command in SETUP.md so the planner doesn't reinvent it per session.

**Warning signs:** Digest pinned more than 30 days ago when session N-pre tag is cut.

### Pitfall 3: QUAL gate text duplicated in all three templates

**What goes wrong:** MANIFEST, HANDOUT, REHEARSAL each inline the full text of the QUAL gates they enforce. QUAL-07 (57-min budget) changes; three files need updating.

**Why it happens:** "Make it self-contained per template" feels safer than "require a cross-ref".

**How to avoid:** D-04 explicit: templates cite gate IDs (`see QUAL-GATES.md §QUAL-04`). QUAL-GATES.md is the single source. Templates carry workflow fields, not gate text.

**Warning signs:** Diff of MANIFEST.template.md vs QUAL-GATES.md shows ≥30% overlap.

### Pitfall 4: Series index drifts from session folders

**What goes wrong:** docs/presentations/README.md lists 9 sessions. Session 3 gets renamed. README still has the old slug.

**Why it happens:** Manual sync; deferred automation is an explicit tradeoff (per "Deferred Ideas").

**How to avoid:** Accept manual sync as the 9-session cost. The planner's acceptance criteria for SCAF-01 includes "grep that every session folder has a corresponding README row, and every README row has a session folder" — cheap sanity check.

**Warning signs:** `ls docs/presentations/*/` disagrees with the README table on slug names.

### Pitfall 5: "Gaia theme" assumption in THEME.md without variation awareness

**What goes wrong:** THEME.md names gaia as the theme, a session author with dark Mermaid diagrams uses the default light `backgroundColor: "#fff"`, diagram lines vanish.

**Why it happens:** Single snippet, no variation guidance.

**How to avoid:** THEME.md (per Claude's Discretion default) includes "Variations" subsection: dark background for dark-diagram decks, light background for default. The existing 2026-04-08 deck uses dark; 2026-04-10 uses light. Both variants are tested.

**Warning signs:** Rendered deck `.html` has invisible diagram lines (white-on-white or light-on-light).

### Pitfall 6: SETUP.md documents tools that fail on Windows/Linux

**What goes wrong:** `brew install asciinema vhs` is macOS-only. Workshop attendees on Linux/Windows hit a dead end at step 7.

**Why it happens:** Author uses macOS; platform blindness.

**How to avoid:** Each install step names all three platforms (`brew` / `apt` / `winget` or `choco`). VHS is charmbracelet/vhs and has Linux + Windows releases on GitHub. asciinema has Linux packages; Windows goes via pipx.

**Warning signs:** `grep -c 'apt\\|winget\\|choco' SETUP.md` returns 0.

### Pitfall 7: CONCERNS-MAPPING.md pre-claims everything to specific sessions

**What goes wrong:** Phase 0 claims XSS-01 to Session 8, XSS-02 to Session 8, ..., PVPGN-PREP to Session 5, TORNEO-N1 to Session 6 — lock, lock, lock. At Session 5 plan-phase, the planner discovers PVPGN-PREP doesn't fit; the demo task must be reselected.

**Why it happens:** Misreading D-06 as "lock every slice" instead of "use slice IDs to claim or defer".

**How to avoid:** Phase 0 CONCERNS-MAPPING locks ONLY the claims that are required by per-session REQs (S08-01 locks XSS-* to Session 8; S06-01 locks TORNEO-GODCLASS to Session 6). Everything else is either "candidate for Session X" or "deferred". Per-session plan-phase finalizes.

**Warning signs:** CONCERNS-MAPPING.md has every row locked to a session with no "candidate" or "deferred" rows.

### Pitfall 8: Forgetting Payara 5 is EOL when writing SETUP.md

**What goes wrong:** SETUP.md documents `payara/server-full:5.2022.5` as if it's a normal pin. A reader tries to upgrade. No Payara 5.x newer exists. They upgrade to Payara 6 and break everything.

**Why it happens:** The 5.2022.5 tag looks like a semver that could be updated.

**How to avoid:** Document explicitly: "Payara 5 is EOL. 5.2022.5 is the last release on the 5.x line. DO NOT upgrade to Payara 6 — it is a Jakarta EE 10 server and would break the `javax.*` imports across the codebase." This is also a Session 2 framing opportunity ("EOL stack is the teaching topic").

**Warning signs:** SETUP.md Docker pinning section lacks a comment explaining why 5.2022.5 is terminal.

### Pitfall 9: Glossary deck `paginate: true` makes it look like a linear presentation

**What goes wrong:** Someone opens GLOSSARY.html expecting a reference and sees "1/6", "2/6"... they click through looking for a conclusion.

**Why it happens:** Default Marp frontmatter has `paginate: true`.

**How to avoid:** GLOSSARY.md frontmatter sets `paginate: false`. The deck is a reference, not a linear arc.

**Warning signs:** GLOSSARY.html has page numbers.

## Code Examples

### Marp render command (from existing CLAUDE.md)

```bash
# Source: docs/presentations/CLAUDE.md — existing convention
npx @marp-team/marp-cli@latest --html \
  docs/presentations/<folder>/<file>.md \
  -o docs/presentations/<folder>/<file>.html
```

### Mermaid render command (from existing CLAUDE.md)

```bash
# Source: docs/presentations/CLAUDE.md — existing convention
npx @mermaid-js/mermaid-cli \
  -i docs/presentations/<folder>/<name>.mmd \
  -o docs/presentations/<folder>/<name>.svg \
  -b transparent
```

### Docker manifest inspect (for SCAF-05 planner to fetch digests)

```bash
# Source: Docker Docs 2026 — manifest inspect
docker manifest inspect postgres:15.17-bookworm \
  | jq -r '.manifests[] | select(.platform.architecture=="amd64") | .digest'
# Output: sha256:ea647d76ba6059d92926662900af0f5d4bcaa9adcb1de477a32f80db3f14b9fe
```

### Git annotated tag for pre/post sessions (from research/ARCHITECTURE.md)

```bash
# Source: .planning/research/ARCHITECTURE.md Pattern 2
git tag -a session-NN-pre -m "Session NN — Pre-demo state"
git push origin session-NN-pre

# After the live session:
git tag -a session-NN-post -m "Session NN — Post-demo state"
git push origin session-NN-post
```

### Speaker notes inline HTML comment (from existing deck)

```markdown
---

# Slide title

Slide body bullet 1

<!--
Presentador: línea para el presenter.
ALT-TAB AL TERMINAL cuando llegues aquí.
-->

---
```
`[Source: docs/presentations/2026-04-10-ai-driven-development.md line 22-27, 107-111]`

## State of the Art

| Old Approach | Current Approach | When Changed | Impact |
|--------------|------------------|--------------|--------|
| Runtime Mermaid JS in Marp | Pre-rendered SVG | HTMLPreview rewrite issue | Non-negotiable; encoded in docs/presentations/CLAUDE.md |
| `image: postgres:15` floating tag | `image: postgres:15.17-bookworm@sha256:...` digest pin | 2023+ industry shift toward supply-chain hardening | SCAF-05 requirement |
| Marp custom theme via `--theme-set` flag | Gaia built-in + copy-pasteable frontmatter (D-04) | Avoids flag-at-render complexity | Scope-specific choice |
| Speaker notes in separate files | Inline `<!-- -->` HTML comments | Avoids drift | Already established convention |

**Deprecated / outdated:**
- **Floating Docker tags in production compose files** — industry consensus post-2023 SolarWinds era. `[CITED: Docker Docs Image Digests 2026]`
- **Runtime Mermaid rendering in slide decks** — broken on HTMLPreview since ~2022; pre-rendered SVG is the working pattern. `[CITED: docs/presentations/CLAUDE.md]`

## Assumptions Log

| # | Claim | Section | Risk if Wrong |
|---|-------|---------|---------------|
| A1 | `docker compose config` parses digest-pinned images correctly into the resolved form | Docker Tag Pinning | LOW — Docker Compose has supported digest syntax since v1.6 (2016); pinned compose files are standard |
| A2 | Payara 5 will remain on Docker Hub indefinitely (no tag removal) | Docker Tag Pinning | MEDIUM — Docker Hub has pulled images before; digest-pin means even if the tag disappears, a pre-pulled image by digest still works locally. Mitigate with pre-pull in SETUP.md §8 |
| A3 | Ollama model tag `qwen2.5:7b` remains canonical through the 9-session arc | SETUP.md landmines | MEDIUM — research/STACK.md explicitly flags "model tags drift monthly". Phase 3 has a research gate to re-verify |
| A4 | The existing `docs/presentations/CLAUDE.md` headings are stable anchor points | CLAUDE.md Extension | LOW — file was authored 2026-04-19 by this project team; stability controlled by us |
| A5 | 9 QUAL gates is a manageable single-file reference | QUAL-GATES.md | LOW — 12 short paragraphs ≈ 100 lines; reasonable |

All claims tagged `[VERIFIED]` or `[CITED]` in sections above are not assumptions — they are either tool-verified this session or pulled from authoritative project docs.

## Open Questions

1. **Should QUAL-GATES.md be a separate artifact or embedded in docs/presentations/CLAUDE.md?**
   - What we know: D-04 says it's a separate reference doc (`docs/presentations/QUAL-GATES.md`). CONTEXT.md locks this.
   - What's unclear: Nothing — D-04 locks this.
   - Recommendation: Follow D-04. Separate file.

2. **How to handle re-teaches of the same session to a different cohort?**
   - What we know: `session-NN-pre` tag is immutable. research/ARCHITECTURE.md Pattern 2 says re-teaches use a branch off the pre-tag (e.g., `replay/session-03-cohort-N`).
   - What's unclear: Whether Phase 0 needs to document the re-teach convention now, or defer to v2.
   - Recommendation: Defer to v2. The CLAUDE.md extension (SCAF-04) names annotated tags as immutable; the re-teach branching pattern is not yet needed for the 9-session first run.

3. **What image digest should we cite for the arm64 (Apple Silicon) platform?**
   - What we know: This research captured amd64 digests only. `docker buildx imagetools inspect` returns a multi-arch manifest list.
   - What's unclear: Whether to pin to the manifest-list digest (cross-platform) or per-platform digests (tighter but platform-branched compose).
   - Recommendation: Pin to the manifest-list digest. Planner's task for SCAF-05: run `docker buildx imagetools inspect <tag> --format "{{json .Manifest}}" | jq -r '.digest'` and use that (not the amd64-specific digest cited here).

4. **Should the Phase 0 plan-set produce the two pre-series decks' MANIFEST.md retroactively?**
   - What we know: research/ARCHITECTURE.md calls them "raw material, not part of the arc". They are un-numbered.
   - What's unclear: Whether the CONCERNS-MAPPING + SCAF-04 NN-infix convention should retro-annotate them.
   - Recommendation: No. Leave as-is. Phase 0 scaffolding serves sessions 1-9 only; the pre-series decks are named out of scope explicitly.

## Environment Availability

| Dependency | Required By | Available | Version | Fallback |
|------------|------------|-----------|---------|----------|
| Docker Desktop | SCAF-05 verification (`docker manifest inspect`) | yes | 24.0.2 | — |
| Docker Compose v2 | SCAF-05 verification (`docker compose config`) | yes | 2.19.0 | — |
| Node.js 20+ | Marp/Mermaid CLI via npx | yes | 22.19.0 | — |
| npm | package version checks | yes | 10.9.3 | — |
| Marp CLI | SCAF-03 render test | ephemeral via npx | 4.3.1 | — |
| Mermaid CLI | SCAF-07 render test | not pre-installed | 11.12.0 available via npx | — |
| Python 3.11+ | email-rag corpus build | yes | 3.11.3 | — |
| jq | JSON parsing (digest extract) | yes | 1.6 | — |
| git | tag creation verification | yes | 2.39.2 | — |
| asciinema | fallback recording (SCAF-04 mention) | NOT installed | — | Document Install in SETUP.md §7; not needed for Phase 0 execution |
| VHS | fallback recording (SCAF-04 mention) | NOT installed | — | Same as asciinema |
| Ollama | SETUP.md mention only | NOT installed | — | Same as asciinema — Phase 0 only documents; first real use is Session 3 |
| Payara running | not needed for Phase 0 | — | — | Phase 0 only mutates docker-compose.yml; no runtime dependency |

**Missing dependencies with no fallback:** None — Phase 0 is documentation + static file authoring.

**Missing dependencies with fallback:** asciinema, VHS, Ollama — these are all documented in SETUP.md for future sessions; their absence at Phase 0 execution time does not block plan completion.

## Validation Architecture

### Test Framework

Phase 0 is documentation-only. There is no programmatic test framework for doc correctness. Instead, validation is **grep-based and shell-command-based**, and every acceptance criterion below maps to a runnable shell check.

| Property | Value |
|----------|-------|
| Framework | Shell (`grep`, `test`, `jq`, `docker compose config`) |
| Config file | None required |
| Quick run command | `bash .planning/phases/00-series-scaffolding/validate.sh` (planner authors this script as part of Wave 0) |
| Full suite command | Same |

**Wave 0 gap:** a `validate.sh` script should be authored as part of the first-wave plan that collects the grep patterns below into one runnable artifact. Alternative: keep them as a checklist in the plan's `## Verification` section, no script.

### Phase Requirements → Test Map

| Req ID | Behavior | Test Type | Automated Command | File Exists? |
|--------|----------|-----------|-------------------|-------------|
| SCAF-01 | README.md lists 9 sessions with required columns | grep | `test $(grep -c "^\| 0[1-9] " docs/presentations/README.md) -eq 9` | Wave 0 |
| SCAF-01 | README.md status column values valid | grep | `grep -E "^\| 0[1-9] \| .* \| .* \| (pending\|rehearsed\|delivered) \|" docs/presentations/README.md \| wc -l` returns ≥9 | Wave 0 |
| SCAF-01 | README.md in Spanish | grep | `grep -q "Próxima\|Sesión\|Fecha\|Estado" docs/presentations/README.md` | Wave 0 |
| SCAF-02 | SETUP.md exists and covers all 6 tool sections | grep | `test $(grep -c "^## " docs/presentations/SETUP.md) -ge 8` | Wave 0 |
| SCAF-02 | SETUP.md in Spanish | grep | `grep -q "instalación\|configuración\|Apéndice\|Requisitos" docs/presentations/SETUP.md` | Wave 0 |
| SCAF-02 | SETUP.md references all required tools | grep | `grep -c -E "docker\|claude\|marp\|mermaid\|ollama\|qwen2.5\|nomic-embed-text\|email-rag" docs/presentations/SETUP.md` returns ≥8 | Wave 0 |
| SCAF-03 | THEME.md exists and contains frontmatter block | grep | `grep -q "marp: true" docs/presentations/THEME.md && grep -q "theme: gaia" docs/presentations/THEME.md` | Wave 0 |
| SCAF-04 | CLAUDE.md contains new sections | grep | `grep -q "session-NN-pre" docs/presentations/CLAUDE.md && grep -q "MANIFEST.md" docs/presentations/CLAUDE.md && grep -q "REHEARSAL.md" docs/presentations/CLAUDE.md && grep -q "HANDOUT.md" docs/presentations/CLAUDE.md && grep -q "asciinema\|VHS" docs/presentations/CLAUDE.md` | Wave 0 |
| SCAF-05 | docker-compose.yml pinned | grep | `grep -E '^\s+image:' docker-compose.yml \| grep -v '@sha256:' \| wc -l` returns 0 | Wave 0 |
| SCAF-05 | Dockerfile digest-pinned | grep | `grep -E '^FROM' Dockerfile \| grep -v '@sha256:' \| wc -l` returns 0 | Wave 0 |
| SCAF-05 | `docker compose config` validates | cmd | `docker compose config > /dev/null` exit 0 | Wave 0 |
| SCAF-06 | CONCERNS-MAPPING.md exists and has claims | grep | `test -f docs/presentations/CONCERNS-MAPPING.md && grep -c "\| .* \| .* \| .* \| Session" docs/presentations/CONCERNS-MAPPING.md` returns ≥5 | Wave 0 |
| SCAF-06 | Every HIGH/MED CONCERNS.md item appears | grep | `for slice in XSS-01 XSS-02 XSS-03 XSS-04 XSS-05 PVPGN-PREP TORNEO-GODCLASS TORNEO-N1 DOCKER-PINS; do grep -q "$slice" docs/presentations/CONCERNS-MAPPING.md \|\| echo "MISSING: $slice"; done` returns empty | Wave 0 |
| SCAF-07 | GLOSSARY/ dir exists with 6 mmd + 6 svg + GLOSSARY.md + .html | shell | `test -d docs/presentations/GLOSSARY && test $(ls docs/presentations/GLOSSARY/*.mmd \| wc -l) -eq 6 && test $(ls docs/presentations/GLOSSARY/*.svg \| wc -l) -eq 6 && test -f docs/presentations/GLOSSARY/GLOSSARY.md && test -f docs/presentations/GLOSSARY/GLOSSARY.html` | Wave 0 |
| SCAF-07 | GLOSSARY.md defines all 6 primitives | grep | `for p in RAG MCP Skill Agent Hook "Slash Command"; do grep -q -E "^#\s*$p" docs/presentations/GLOSSARY/GLOSSARY.md; done` | Wave 0 |
| SCAF-08 | MANIFEST.template.md has required placeholder fields | grep | `grep -q "session-NN-pre" docs/presentations/MANIFEST.template.md && grep -q "session-NN-post" docs/presentations/MANIFEST.template.md && grep -q "Slide.*commit" docs/presentations/MANIFEST.template.md && grep -q "Recovery" docs/presentations/MANIFEST.template.md && grep -q "compare" docs/presentations/MANIFEST.template.md && grep -q "version" docs/presentations/MANIFEST.template.md` | Wave 0 |
| SCAF-09 | REHEARSAL.template.md has checklist + notes | grep | `grep -c "^- \[ \]" docs/presentations/REHEARSAL.template.md` returns ≥5 | Wave 0 |
| SCAF-09 | REHEARSAL.template.md references QUAL-02, QUAL-03, QUAL-09 | grep | `grep -q "QUAL-02" docs/presentations/REHEARSAL.template.md && grep -q "QUAL-03" docs/presentations/REHEARSAL.template.md && grep -q "QUAL-09" docs/presentations/REHEARSAL.template.md` | Wave 0 |
| SCAF-10 | HANDOUT.template.md has all 5 Spanish sections | grep | `grep -q "¿Qué vimos?" docs/presentations/HANDOUT.template.md && grep -q "Comandos para probar" docs/presentations/HANDOUT.template.md && grep -q "Link de comparación" docs/presentations/HANDOUT.template.md && grep -q "Próxima sesión" docs/presentations/HANDOUT.template.md && grep -q "Lecturas" docs/presentations/HANDOUT.template.md` | Wave 0 |
| QUAL-01..12 | QUAL-GATES.md defines all 12 gates | grep | `for n in 01 02 03 04 05 06 07 08 09 10 11 12; do grep -q "QUAL-$n" docs/presentations/QUAL-GATES.md \|\| echo "MISSING: QUAL-$n"; done` returns empty | Wave 0 |

### Sampling Rate

- **Per task commit:** grep-based check for the specific artifact touched (e.g., after editing docker-compose.yml, run the SCAF-05 grep trio)
- **Per wave merge:** Run the full row set above
- **Phase gate:** All 22 checks green; `docker compose config` exits 0; no floating tags

### Wave 0 Gaps

- [ ] `docs/presentations/README.md` — SCAF-01 target (does not exist yet)
- [ ] `docs/presentations/SETUP.md` — SCAF-02 target
- [ ] `docs/presentations/THEME.md` — SCAF-03 target
- [ ] `docs/presentations/QUAL-GATES.md` — D-04 derivative (referenced by SCAF-08/09/10 templates)
- [ ] `docs/presentations/CONCERNS-MAPPING.md` — SCAF-06 target
- [ ] `docs/presentations/GLOSSARY/GLOSSARY.md` + `GLOSSARY.html` + 6 `.mmd` + 6 `.svg` — SCAF-07 target
- [ ] `docs/presentations/MANIFEST.template.md` — SCAF-08 target
- [ ] `docs/presentations/REHEARSAL.template.md` — SCAF-09 target
- [ ] `docs/presentations/HANDOUT.template.md` — SCAF-10 target
- [ ] `docker-compose.yml` + `Dockerfile` edits — SCAF-05 target (mutation of existing files)
- [ ] `docs/presentations/CLAUDE.md` extension — SCAF-04 target (mutation of existing file)

No programmatic test framework to install — shell is sufficient.

## Plan-Splitting Recommendation

### Dependency graph (what depends on what)

```
QUAL-GATES.md ──┐
                ├──► MANIFEST.template.md
                ├──► REHEARSAL.template.md
                └──► HANDOUT.template.md

(independent, no deps on each other)
─────────────────────────────────────
THEME.md           (SCAF-03, standalone)
docker pins        (SCAF-05, mutates existing files)
CONCERNS-MAPPING   (SCAF-06, reads CONCERNS.md, no deps)
GLOSSARY/          (SCAF-07, 8 files: deck + .html + 6 mmd+svg pairs)
SETUP.md           (SCAF-02, reads STACK.md)
CLAUDE.md ext      (SCAF-04, appends existing file)

(depends on ~everything else)
─────────────────────────────────────
README.md          (SCAF-01, summarizes the others — authored last)
```

### Recommended 7-plan structure

| # | Plan | Scope | Deps | Parallelizable with |
|---|------|-------|------|---------------------|
| 1 | **docker-pinning** | SCAF-05 — mutates `docker-compose.yml`, `Dockerfile`; fetches digests; updates SETUP.md §8 cadence note | none | 2, 3, 4, 5, 6 |
| 2 | **concerns-mapping** | SCAF-06 — authors `CONCERNS-MAPPING.md` with 14-slice table + deferred section | reads `.planning/codebase/CONCERNS.md` | 1, 3, 4, 5, 6 |
| 3 | **primitives-glossary** | SCAF-07 + CURR-03 — authors `GLOSSARY/GLOSSARY.md` + `.html` + 6 `.mmd`/`.svg` pairs | none (D-11 says before Session 1) | 1, 2, 4, 5, 6 |
| 4 | **setup-doc** | SCAF-02 — authors `SETUP.md` (layered: quick-start + appendix) in Spanish | reads `.planning/codebase/STACK.md` + `tools/email-rag/README.md`; cross-links SCAF-05 pinning cadence (loose) | 1, 2, 3, 5, 6 |
| 5 | **sidecar-templates-and-qual-gates** | SCAF-08/09/10 + D-04-derived `QUAL-GATES.md` — 4 artifacts together because templates link to QUAL-GATES.md by ID | QUAL-GATES.md authored before or alongside the three templates | 1, 2, 3, 4, 6 |
| 6 | **theme-and-claude-md-ext** | SCAF-03 (`THEME.md`) + SCAF-04 (CLAUDE.md extension) — small, related, and both are docs-about-docs | none | 1, 2, 3, 4, 5 |
| 7 | **series-index** | SCAF-01 — authors `README.md` summarizing all 9 sessions using the other plans' outputs | **waits on 1-6** (reads their outputs) | none — last |

**Rationale for the 7-plan grouping:**

- Templates and QUAL-GATES are bundled because each template cites gate IDs (D-04), and splitting would create a race for who names the anchors first.
- THEME.md and CLAUDE.md extension are bundled because they're both tiny and document-level (small plan > two micro-plans that are slower to dispatch-and-aggregate).
- Docker pinning is its own plan because it mutates two existing infrastructure files and needs per-file attention to avoid breaking the build. (Does it break? `docker compose config` + a quick `docker compose up -d --build` smoke test answers that.)
- Series index is the follower (locked by ROADMAP.md Phase 0 Notes).

**Alternative 8-plan split:** if the planner decides SCAF-08/09/10 each deserve their own plan (three template artifacts), split plan #5 into three (SCAF-08, SCAF-09, SCAF-10) + one for QUAL-GATES.md — total 8 plans. This increases parallelism but adds coordination cost for the QUAL-GATES anchors. Recommend sticking with 7 unless a reason surfaces.

**Alternative 6-plan split:** merge plans #4 (setup-doc) and #1 (docker-pinning) since SETUP.md's pin-cadence note is the only cross-dep. Recommend against — SETUP.md has large Spanish content that benefits from dedicated attention.

### What parallelizes

Plans 1-6 are fully independent: each writes distinct files, each has no compile-time dependency on the others. The ROADMAP.md "Parallelization Notes" row for Phase 0 is consistent with this grouping:

> Templates (MANIFEST/HANDOUT/REHEARSAL), Docker pinning, CONCERNS-MAPPING, glossary deck, THEME.md — all independent (series index plan should follow as it summarizes them)

`[CITED: ROADMAP.md Parallelization Notes table, Phase 0 row]`

### What sequences

Only plan #7 (series-index) must follow. It reads the output of plans 1-6 to populate its Sessions table entries. Even so, the series-index does not need all plans fully complete to *start* — it needs the per-session slug names, dates, and abstracts, which are roadmap-known (ROADMAP.md has all 9 phase titles). The series-index plan could in principle begin earlier and stub placeholders, but it is cleaner to wait until plans 1-6 are merged.

## Security Domain

> `security_enforcement` is NOT explicitly set in `.planning/config.json`. Treating as enabled per convention.

### Applicable ASVS Categories

| ASVS Category | Applies | Standard Control |
|---------------|---------|-----------------|
| V1 Architecture | yes (Phase 0 embeds ecosystem choices that propagate to all sessions) | digest-pinned images; Spanish-language rule; Marp pipeline stable |
| V2 Authentication | no (Phase 0 changes no auth code) | n/a |
| V3 Session Management | no | n/a |
| V4 Access Control | no | n/a |
| V5 Input Validation | no | n/a |
| V6 Cryptography | no | n/a |
| V7 Error Handling | no | n/a |
| V8 Data Protection | no (email corpus PII redaction is upstream — `tools/email-rag/` enforces; Phase 0 does not touch) | n/a — but SETUP.md should note the redaction invariant |
| V9 Communications | partial | TLS termination reminder inherits from CONCERNS.md §FORM-AUTH-TLS |
| V10 Malicious Code | **yes** | Docker digest pinning (SCAF-05) — supply-chain; prevents `latest`-tag substitution attacks |
| V14 Configuration | **yes** | `.env` gitignore reminder; `.mcp.json` conventions (deferred to Session 4) |

### Known Threat Patterns for Workshop Series Scaffolding

| Pattern | STRIDE | Standard Mitigation |
|---------|--------|---------------------|
| Floating Docker tag pulls different image mid-series | Tampering | Digest pin (SCAF-05) |
| Glossary text drifts between sessions | Information disclosure (wrong mental model) | D-10 reference-only embed |
| Secret leak via committed `.env` | Information disclosure | CLAUDE.md / SETUP.md reminder; `.gitignore` audit (pre-existing — verify in plan's acceptance) |
| PII leak via email corpus | Information disclosure | Corpus is per-developer + gitignored + PII-redacted (already enforced by `tools/email-rag/`); SETUP.md must re-cite the redaction invariant |
| MANIFEST.md records SHA of wrong repo | Integrity | Compare URLs template uses placeholder; per-session plan-phase fills |

**Phase 0's security surface is small but real.** The primary threat is V10 Malicious Code (supply-chain) — SCAF-05 pinning directly mitigates. Secondary surface is SETUP.md accuracy: documentation that tells contributors to run `tools/email-rag/` without mentioning the PII-redaction invariant could lead to a contributor committing raw mbox data. SETUP.md §6 (email-rag) must explicitly re-state: "El corpus está PII-redactado upstream; el mbox crudo NO se commitea."

## Sources

### Primary (HIGH confidence — direct tool verification or project docs)

- `.planning/REQUIREMENTS.md` — SCAF-01..10, QUAL-01..12, CURR-03
- `.planning/ROADMAP.md` — Phase 0 goal, success criteria, parallelization notes
- `.planning/phases/00-series-scaffolding/00-CONTEXT.md` — locked decisions (D-01..D-12), Claude's Discretion defaults, deferred ideas
- `.planning/codebase/CONCERNS.md` — HIGH/MED debt inventory (feeds SCAF-06)
- `.planning/codebase/STACK.md` — current stack versions (feeds SETUP.md)
- `.planning/research/ARCHITECTURE.md` — three-tier content arch, Pattern 1 MANIFEST example, Pattern 2 pre/post tags, Anti-Patterns 1-6
- `.planning/research/STACK.md` — Marp 4.x, Mermaid 11.x, Ollama model tags, asciinema/VHS
- `.planning/research/SUMMARY.md` — Phase 0 rationale, Gap 6 (demo-task pre-claiming), confidence assessment
- `.planning/research/PITFALLS.md` — Pitfall 1 (AI flakes), Pitfall 6 (hour overrun), Pitfall 8 (Docker), Pitfall 18 (multi-primitive), Pitfall 21 (invisible deps), Pitfall 25 (bilingual), Pitfall 30 (refactor overclaims)
- `docs/presentations/CLAUDE.md` — existing convention file (target of SCAF-04 extension)
- `docs/presentations/2026-04-10-ai-driven-development/2026-04-10-ai-driven-development.md` — reference deck for Marp frontmatter, speaker-note HTML-comment convention
- `docker-compose.yml` — current state (target of SCAF-05)
- `Dockerfile` — current state (target of SCAF-05)
- `./CLAUDE.md` — project root — Spanish rule, stack, build commands, email corpus
- Context7 `/marp-team/marp-cli` — `--theme`, `--theme-set`, global markdown directives verified this session
- Docker Hub API 2026-04-20:
  - `postgres:15.17-bookworm` amd64 digest `sha256:ea647d76ba6059d92926662900af0f5d4bcaa9adcb1de477a32f80db3f14b9fe`
  - `payara/server-full:5.2022.5` amd64 digest `sha256:95f45ebc141eb68f1e572725b570aad03059a4e8ab34e590f8f7c7259011df75`
- npm registry 2026-04-20: `@marp-team/marp-cli@4.3.1`, `@mermaid-js/mermaid-cli@11.12.0`
- GitHub `charmbracelet/vhs` — VHS v0.11.0 released 2026-03-10

### Secondary (MEDIUM confidence — training-stable external knowledge)

- WebSearch 2026-04 — Marp + Mermaid pre-rendered SVG pitfall (font-size in `<foreignObject>`)
- Marp issue #266, discussions #115, #536 — custom theme loading (consensus on `--theme` and `--theme-set`)
- Docker Docs Image Digests page — `image:tag@sha256:<digest>` syntax
- ecosystem consensus — Ollama model tag drift monthly, documented in research/STACK.md + SUMMARY.md

### Tertiary (LOW confidence — flagged for re-verification per ROADMAP research gates)

- None — Phase 0 has `[STANDARD]` research flag per research/SUMMARY.md. No re-verification gate.
- Downstream research gates (Phases 3/4/7/9) are not this phase's concern.

## Metadata

**Confidence breakdown:**

- Standard stack: **HIGH** — Marp 4.3.1, Mermaid 11.12.0, Payara 5.2022.5 digest, Postgres 15.17 digest all verified this session
- Architecture: **HIGH** — research/ARCHITECTURE.md + CONTEXT.md D-01..D-12 lock the decision surface
- Pitfalls: **HIGH** — repo-local pitfalls grounded in existing file inspection; external pitfalls cross-referenced
- Plan-splitting: **HIGH** — dependency graph derived from file-system ownership, not opinion; matches ROADMAP.md parallelization guidance

**Research date:** 2026-04-20
**Valid until:** 2026-05-20 (monthly for Docker digests; longer for Marp/Marpit conventions which are ecosystem-stable)

---

*Research completed: 2026-04-20*
*Ready for planning: yes*
