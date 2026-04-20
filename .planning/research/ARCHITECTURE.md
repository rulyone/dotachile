# Architecture Research

**Domain:** AI-assisted software engineering workshop series (live 1-hour sessions, Spanish-language, demos landing on `master` of a legacy Java EE codebase)
**Researched:** 2026-04-19
**Confidence:** HIGH for repo-local conclusions (grounded in files read); MEDIUM for cross-industry workshop conventions (training-data only — WebSearch unavailable this session, flagged inline)

## Standard Architecture

A workshop series is not a product — it is a **content pipeline** whose "production" artifact is a live hour in front of humans, and whose durable artifact is a set of checked-in decks + a set of commits on `master`. The architecture therefore has three tiers, not the usual MVC ones:

```
┌──────────────────────────────────────────────────────────────────┐
│                      SERIES TIER (shared)                        │
│  ┌──────────┐  ┌─────────────┐  ┌──────────┐  ┌──────────────┐   │
│  │ Index /  │  │ Series-wide │  │ Shared   │  │ Build & pub  │   │
│  │ READMEs  │  │ setup guide │  │ theming  │  │ toolchain    │   │
│  │ (ES)     │  │ (ES)        │  │ (Marp)   │  │ (Marp+Mermaid)│  │
│  └────┬─────┘  └──────┬──────┘  └────┬─────┘  └──────┬───────┘   │
│       │               │              │                │           │
├───────┴───────────────┴──────────────┴────────────────┴──────────┤
│                    SESSION TIER (one per hour)                   │
│  ┌────────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌───────┐ │
│  │ Deck (.md  │ │ Speaker  │ │ Demo     │ │ Rehearsal│ │Handout│ │
│  │  + .html)  │ │  notes   │ │ manifest │ │ log      │ │(audi- │ │
│  │            │ │ (inline) │ │ (SHAs+tag│ │ (dry-run)│ │ ence) │ │
│  └─────┬──────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘ └───┬───┘ │
│        │             │            │             │           │     │
│        └─────────────┴── per-session folder ────┴───────────┘     │
├───────────────────────────────────────────────────────────────────┤
│                      CODE TIER (the lab)                         │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │  DotaChile codebase on `master` — mutated live              │ │
│  │  Git tags: session-NN-pre / session-NN-post                 │ │
│  │  Demo commits are real, conventional, reviewable            │ │
│  └─────────────────────────────────────────────────────────────┘ │
└───────────────────────────────────────────────────────────────────┘
```

### Component Responsibilities

| Component | Responsibility | Typical Implementation in this repo |
|-----------|----------------|-------------------------------------|
| **Series README** | Landing page, reading order, prereqs, audience framing (Spanish) | `docs/presentations/README.md` (new, series-level index) |
| **Series setup guide** | "Clone, `docker compose up`, install Claude Code + Plane + email-rag" — one doc every session can link to | `docs/presentations/SETUP.md` (new, Spanish) |
| **Session folder** | Owns everything a single 1-hour slot needs | `docs/presentations/YYYY-MM-DD-NN-<slug>/` |
| **Deck (`.md` / `.html`)** | Slides + inline speaker notes (HTML comments) — already the convention | `YYYY-MM-DD-NN-<slug>.md` + `.html` |
| **Mermaid sources** | Versioned diagrams, pre-rendered to `.svg` | `*.mmd` + `*.svg` (HTMLPreview needs pre-rendered SVG) |
| **Demo manifest** | The single source of truth tying slides ↔ commits ↔ tags | `MANIFEST.md` in the session folder |
| **Rehearsal log** | Dry-run record — what broke, timing, cut-list | `REHEARSAL.md` in the session folder |
| **Exit handout** | What the audience takes home (commands, repo URLs, tag names, links) | `HANDOUT.md` in the session folder (short, Spanish) |
| **Recovery plan** | Reset command, fallback demo, kill-switches | Section inside `MANIFEST.md` (not a separate file — reduces drift) |
| **Git tags** | Pre/post SHA markers on `master` for reset and diff | `session-NN-pre`, `session-NN-post` |
| **Codebase `.planning/`** | Durable state of the lab across sessions | Already exists — `PROJECT.md`, `codebase/*` |

## Recommended Project Structure

**Keep the existing convention. Extend it.** Current: `docs/presentations/YYYY-MM-DD-<slug>/`. The folder already groups `.md`, `.html`, `.mmd`, `.svg` — this is the right shape. Add a numeric session index and four per-session sidecars.

```
docs/
└── presentations/
    ├── CLAUDE.md                                   # EXISTS — extend with series conventions
    ├── README.md                                   # NEW — series index (ES): order, links, status
    ├── SETUP.md                                    # NEW — shared prereqs (ES)
    ├── THEME.md                                    # NEW (optional) — Marp frontmatter snippet shared across decks
    │
    ├── 2026-04-08-mas-alla-del-hype/               # EXISTS — pre-series "raw material"
    │   └── …                                       # leave as-is (prior art, not part of numbered arc)
    │
    ├── 2026-04-10-ai-driven-development/           # EXISTS — pre-series "raw material"
    │   └── …                                       # leave as-is
    │
    ├── 2026-MM-DD-01-<slug>/                       # NEW — first numbered session
    │   ├── 2026-MM-DD-01-<slug>.md                 #   deck source (speaker notes inline as <!-- -->)
    │   ├── 2026-MM-DD-01-<slug>.html               #   rendered, committed alongside .md
    │   ├── 2026-MM-DD-01-<slug>-<diagram>.mmd      #   Mermaid source
    │   ├── 2026-MM-DD-01-<slug>-<diagram>.svg      #   pre-rendered (HTMLPreview requirement)
    │   ├── MANIFEST.md                             #   slides ↔ commits ↔ tags + recovery plan
    │   ├── REHEARSAL.md                            #   dry-run log; gitignored-optional
    │   └── HANDOUT.md                              #   audience takeaway (short ES)
    │
    ├── 2026-MM-DD-02-<slug>/                       # NEW — next session, same shape
    │   └── …
    │
    └── …
```

### Structure Rationale

- **`docs/presentations/` stays flat + date-prefixed.** Do not move to `docs/workshops/`. The folder already exists, `CLAUDE.md` already documents it, two decks already live there, and the GSD codebase map references it. Cost of rename > benefit. (HIGH confidence — grounded in repo state.)
- **Add a `NN-` numeric infix after the date** (`2026-05-15-03-agents/`). Chronological sort still works; session order becomes explicit; scripts can grep `-NN-` to enumerate the arc. The two pre-existing decks (`2026-04-08-…`, `2026-04-10-…`) predate the series and stay un-numbered — a clean boundary between "raw material" and "the arc".
- **One folder = one session** is already how the repo thinks. Each folder is self-contained enough that you can point somebody at one directory and they have everything (deck, diagrams, manifest, handout, dry-run log). (MEDIUM confidence on industry prevalence — this matches standard conference-talk / workshop-repo structure in my training data, though no single authoritative source.)
- **Series-level docs (`README`, `SETUP`, `THEME`) at `docs/presentations/` root**, not inside any session folder. Keeps the "series tier" distinct from the "session tier" and avoids duplication.
- **Speaker notes stay inside the deck** (as `<!-- -->` HTML comments). That's already the existing pattern in `2026-04-10-…md` and it survives `marp-cli` rendering. Do **not** split speaker notes into a separate file — every duplication is a drift risk, and comments in the source are the natural place.
- **`MANIFEST.md` is the canonical slides↔code link.** Not inline in the deck, not in a separate index at the root — in the session folder, next to the deck that references it. One hop from the slides.
- **`REHEARSAL.md` is optional per session** but when it exists it lives next to the deck. It captures the dry-run that happened the night before: timing per section, what cut, what broke, what to watch for live. Git-tracked so future sessions inherit the lessons.
- **`HANDOUT.md` is short and Spanish.** Audience-facing takeaway with the 3-5 commands to try after the session, plus the post-session tag URL. Can be rendered to HTML the same way as the deck if print/QR-code distribution is wanted, but `.md` is enough.

## Architectural Patterns

### Pattern 1: Session Manifest as Single Source of Truth (RECOMMENDED)

**What:** Every session folder contains a `MANIFEST.md` that declares, for that session: the pre-session git tag, the expected demo commit SHAs (or tag ranges), the post-session tag, the fallback/recovery commands, and the slide→SHA mapping.

**When to use:** Always — this is the one mechanism that makes slides ↔ code traceable *and* survives three months later when someone clones the repo and wants to replay session 3.

**Trade-offs:**
- Pro: Single file per session answers "what does this deck demo?" in 30 seconds. Survives deck edits without invalidating commit links.
- Pro: Doubles as the recovery plan (keeps reset commands next to the SHAs they reset to).
- Con: One manual file to keep in sync. Mitigation: make it the *first* thing authored during session planning (before the deck), and the *last* thing updated post-session (with the real SHAs, which are known only after the live run).

**Example:**

```markdown
# Session 03 — Skills en el ciclo de desarrollo — Manifest

**Fecha:** 2026-05-15
**Slug:** 2026-05-15-03-skills-en-el-ciclo
**Deck:** [2026-05-15-03-skills-en-el-ciclo.md](./2026-05-15-03-skills-en-el-ciclo.md)

## Git tags

| Tag                | Purpose                            | SHA       |
| ------------------ | ---------------------------------- | --------- |
| `session-03-pre`   | Repo state before the live session | `abc1234` |
| `session-03-post`  | Repo state after the live session  | `def5678` |

## Slide → commit map

| Slide # | Deck section                  | Commit(s)            | Notes |
| ------- | ----------------------------- | -------------------- | ----- |
| 8       | "Demo 1: skill user-story"    | `a1b2c3d`            | Ticket created via MCP, no code change |
| 12      | "Demo 2: refactor PvpgnHash"  | `e4f5g6h..i7j8k9l`   | 3 commits, TDD cycle, lands on master  |
| 17      | "Verificar en vivo"           | `m0n1o2p`            | Touch-up commit during Q&A             |

## Recovery plan

- **If the demo goes sideways on master:** `git reset --hard session-03-pre`.
- **If Payara fails to hot-reload:** `docker compose restart payara`, then `./dev-sync.sh java`.
- **If MCP/Plane loses connection:** skip Demo 1, move straight to Demo 2; the arc survives.
- **Kill-switch:** `git tag -d session-03-post && git push --delete origin session-03-post` if a post-tag was applied to a broken state.

## Compare links

- Pre → Post diff: https://github.com/<org>/dotachile/compare/session-03-pre...session-03-post
```

### Pattern 2: Pre/Post Git Tags per Session

**What:** Before each live session, tag `master` with `session-NN-pre`. After the session, tag with `session-NN-post`. Both are annotated tags (`git tag -a`), pushed to origin, immutable.

**When to use:** Every numbered session. This is the **one concrete recommendation** for slides ↔ code traceability (the downstream roadmap needs a single answer, not a menu).

**Trade-offs:**
- Pro: Two tags per session is enough to recover, diff, clone at an exact state, and generate a GitHub compare URL for the handout. Scales to 20+ sessions without clutter.
- Pro: Immutable (annotated tags) — audience can trust the link six months later.
- Pro: Plays nicely with the "commits on master, no long-lived teaching branches" constraint from `PROJECT.md`.
- Con: If you re-run the same session topic in a different cohort, the tag is taken. Mitigation: `session-03-pre` / `session-03-post` refers to the *session number in the arc*, not the cohort; if you re-teach, create a branch off `session-03-pre` for the replay.
- Con: Tags don't move. If the live session runs off the rails and you fix things later, the post-tag either (a) stays on the live-broken state and a new tag `session-03-post-v2` is added, or (b) is force-moved — document the choice in `MANIFEST.md`. Prefer (a) — honesty over tidiness.

**Why not branches?** Long-lived branches conflict with the repo convention ("demo commits land on `master`, same repo", `PROJECT.md`). Branches also invite merge-conflict drama between sessions. Tags are read-only bookmarks — zero maintenance cost.

**Why not just a SHA list?** Bare SHAs in a manifest work, but (a) they require updating after every live session, (b) `git reset --hard <sha>` is less discoverable than `git reset --hard session-03-pre`, (c) GitHub compare URLs are cleaner with tag names, (d) `git tag -l 'session-*'` enumerates the arc in a single command — useful for any "give me all demo commits" tooling later.

### Pattern 3: Session Folders as Content Capsules

**What:** Each session folder is self-contained: deck, diagrams, manifest, handout, rehearsal log. No session file lives outside the session folder. Shared content lives one level up.

**When to use:** Always. This is already the repo's pattern — the recommendation is to not break it.

**Trade-offs:**
- Pro: `cp -r 2026-05-15-03-skills/ 2026-06-01-04-agents/` is a valid session-start workflow.
- Pro: Deleting a session = deleting a folder.
- Pro: Grep-friendly (`docs/presentations/*/MANIFEST.md`).
- Con: Some cross-session content gets duplicated (e.g., the Marp frontmatter). Mitigation: `docs/presentations/THEME.md` with the canonical frontmatter snippet that each session copies (Marp doesn't support includes — copy-paste is the honest answer).

### Pattern 4: Speaker Notes as Inline HTML Comments

**What:** Speaker notes live in the deck markdown as `<!-- … -->` HTML comments, per-slide, addressed to the presenter ("Presentador:", "ALT-TAB AL TERMINAL"). This is the existing convention (see `2026-04-10-ai-driven-development.md`).

**When to use:** Every session. Don't invent a new format.

**Trade-offs:**
- Pro: Survives `marp-cli` rendering (comments are stripped from HTML output — audience doesn't see them, presenter does by reading the `.md`).
- Pro: Zero drift between slide and its notes — they live side-by-side in the same file.
- Con: Notes aren't shown in Marp's "presenter mode" unless a theme supports it. Mitigation: treat the `.md` as the source the presenter reads during dry-run; during live session, the presenter already knows the notes by heart from rehearsal.

### Pattern 5: Rehearsal-First Authoring

**What:** Every session is dry-run end-to-end at least once before going live. The dry-run produces `REHEARSAL.md` with timing, cut-list, and observed failures. The deck is edited based on the rehearsal, not on what looks good in the slides.

**When to use:** Every session. For 1-hour budget + live demo + messy legacy code, the ratio of rehearsal time to session time is close to 2:1. Skipping this is the #1 cause of blown budgets. (MEDIUM confidence — training-data generality, consistent with the existing deck's explicit "ALT-TAB AL TERMINAL" stage directions which suggest the author has rehearsed.)

**Trade-offs:**
- Pro: Finds the 20-minute subsection that won't fit in the hour before the audience does.
- Pro: Produces speaker notes that actually match what happens live.
- Con: Doubles prep time. Accept it or don't run live demos.

## Data Flow

### Content Flow (slide authoring → audience)

```
[Author intent]
     ↓
[spec / outline in ES]
     ↓
[deck .md with inline speaker notes + Mermaid .mmd files]
     ↓  (marp-cli + mmdc)
[deck .html + .svg diagrams (committed alongside .md)]
     ↓  (commit + push)
[GitHub / HTMLPreview render]
     ↓
[Audience screen during live session]
```

### Demo Flow (session run-time)

```
T-0:  Presenter verifies HEAD == session-NN-pre
      Verifies docker compose, Claude Code, MCP servers up
T+0:  Live session begins; deck shown from pre-rendered HTML
T+n:  Demo segment — commits land on master
T+60: Session ends
T+60: `git tag -a session-NN-post -m "..."`
      `git push origin session-NN-post`
      Update MANIFEST.md with real SHAs committed during the session
      Regenerate HANDOUT.md compare URL
      Commit manifest/handout updates
```

### Slide ↔ Code Traceability Flow (audience replay later)

```
[Audience clones repo]
     ↓
[git fetch --tags]
     ↓
[opens docs/presentations/2026-MM-DD-NN-<slug>/ ]
     ↓
[reads MANIFEST.md]
     ↓
[git checkout session-NN-pre] → see starting point
[git log session-NN-pre..session-NN-post] → see every demo commit
[GitHub compare URL] → diff view in browser
```

### Key Data Flows

1. **Deck authoring:** `outline → .md (Marp) → .html (marp-cli) → .mmd diagrams → .svg (mmdc)`. Every commit updates `.md` *and* its `.html`; every `.mmd` edit updates its `.svg`. This is the current `docs/presentations/CLAUDE.md` rule — inherit it.
2. **Demo preparation:** `rehearsal dry-run → REHEARSAL.md → deck edits → pre-session tag`. The rehearsal is what makes the demo believable.
3. **Live demo:** `session-NN-pre → live commits → session-NN-post`. Nothing goes on `master` between these tags except demo work.
4. **Post-session wrap-up:** `update MANIFEST.md with real SHAs → update HANDOUT.md with compare URL → commit these together`. This is the point where the deck and the code become linkable for future audiences.

## Build / Publish Flow

**Existing (unchanged):**
- `npx @marp-team/marp-cli@latest --html <file>.md -o <file>.html` — deck render. Committed alongside `.md`.
- `npx @mermaid-js/mermaid-cli -i <name>.mmd -o <name>.svg -b transparent` — diagram render. Committed alongside `.mmd`.
- `HTMLPreview.github.io` consumes the rendered `.html` directly from GitHub (no build server needed).

**Recommended extension — keep it simple:**
- **Local-only rendering** is sufficient for this arc (4–8 sessions). A pre-commit hook or CI job is possible but not needed — the existing convention ("always regenerate and commit the `.html` alongside `.md` changes") is enforced by the author, not the tooling.
- **No GitHub Pages needed.** HTMLPreview works on the committed `.html`, and the audience screen during live sessions can be served the `.html` from `file://` or a local `python -m http.server` — whichever the presenter prefers.
- **Mermaid regeneration trigger:** manual. When the author edits `.mmd`, they run `mmdc` and commit both. This is the current rule; it scales fine to 8 sessions with 1-3 diagrams each.
- **Optional future enhancement (not for the MVP arc):** a Makefile or npm script `make slides` that walks `docs/presentations/*/` and rebuilds any `.html` / `.svg` whose source is newer. Defer this until the manual flow actually becomes painful.

**Publishing target during a live session:**
- Presenter opens the `.html` file locally in a browser, enters Marp's presenter view (or just full-screen).
- No internet dependency for the slides themselves. The demos need the network (Claude Code API, MCP servers, Plane) but the slides do not. This is a resilience win — flaky venue WiFi breaks the demo half of the session but not the slide half.

## Per-Session Anatomy → File Paths

Direct answer to the downstream consumer's requirement:

| Component                     | Lives at                                                                  | Required? |
| ----------------------------- | ------------------------------------------------------------------------- | --------- |
| Deck source                   | `docs/presentations/<date>-NN-<slug>/<date>-NN-<slug>.md`                 | Required  |
| Deck rendered                 | `docs/presentations/<date>-NN-<slug>/<date>-NN-<slug>.html`               | Required  |
| Speaker notes                 | Inline in the deck `.md` as `<!-- … -->` HTML comments                    | Required  |
| Mermaid sources               | `docs/presentations/<date>-NN-<slug>/<date>-NN-<slug>-*.mmd`              | Optional  |
| Mermaid SVGs                  | `docs/presentations/<date>-NN-<slug>/<date>-NN-<slug>-*.svg`              | If .mmd   |
| Setup checklist               | Link out to `docs/presentations/SETUP.md` (series-level)                  | Required  |
| Pre-session SHA               | `session-NN-pre` git tag + recorded in `MANIFEST.md`                      | Required  |
| Demo-commit manifest          | `docs/presentations/<date>-NN-<slug>/MANIFEST.md`                         | Required  |
| Exit handout                  | `docs/presentations/<date>-NN-<slug>/HANDOUT.md`                          | Required  |
| Audience takeaway artifact    | The demo commit(s) themselves on `master`, reachable via the two tags     | Required  |
| Recovery plan                 | Section inside `MANIFEST.md`                                              | Required  |
| Dry-run / rehearsal log       | `docs/presentations/<date>-NN-<slug>/REHEARSAL.md`                        | Strongly recommended |
| Post-session SHA              | `session-NN-post` git tag + recorded in `MANIFEST.md`                     | Required  |

## Cross-Session Structure → Linkage Model

| Mechanism               | Where                                                   | Purpose |
| ----------------------- | ------------------------------------------------------- | ------- |
| Numeric `NN-` infix     | Folder name                                             | Canonical order |
| Series README           | `docs/presentations/README.md`                          | Entry point — lists sessions in order with status + short abstract (ES) |
| Prerequisite graph      | Section in `docs/presentations/README.md` (ASCII or small Mermaid) | Which sessions assume which |
| Shared SETUP            | `docs/presentations/SETUP.md`                           | One place for tooling install + `docker compose up` + Claude Code + MCP config |
| State-of-repo convention | `session-NN-pre` git tag                               | "To follow session N from scratch, `git checkout session-NN-pre`" |
| Shared theme            | `docs/presentations/THEME.md` (optional)                | Marp frontmatter canonical snippet — copy-paste into each deck |

**Prerequisite model:** each session's `MANIFEST.md` declares a `Prerequisites:` line naming the prior sessions it assumes. The series README aggregates these into a DAG. For a 4–8-session arc this stays small enough to render as ASCII.

## Suggested Build Order (the 4–8-session arc)

The downstream consumer explicitly asked for teach-order with dependency rationale. Topic pool from `PROJECT.md`: intro/LLM-evolution, RAG, MCP, Skills, Agents, Hooks, Commands, plugin-building capstone. Call these **Sessions 1–8** below; if the arc lands at 6, drop sessions 3 or 4 (the two most similar-shaped).

```
       ┌────────────────────────────────────────────┐
       │         Session 1: Intro / LLM evolution    │  foundational context — everybody needs it
       └────────────────────────────────────────────┘
                          │
                          ▼
       ┌────────────────────────────────────────────┐
       │         Session 2: RAG                     │  "bring your own context" — self-contained
       │         (uses tools/email-rag, prebuilt)   │
       └────────────────────────────────────────────┘
                          │
                          ▼
       ┌────────────────────────────────────────────┐
       │         Session 3: MCP                     │  "bring your own tools" — independent of RAG
       │         (Plane MCP + codebase-level MCP)   │     but same mental model ("external context")
       └────────────────────────────────────────────┘
                          │
                          ▼
       ┌────────────────────────────────────────────┐
       │         Session 4: Skills                  │  reusable knowledge packets
       │         (user-story, PRD-generator demos)  │     depends on MCP mental model (Skills call tools)
       └────────────────────────────────────────────┘
                          │
                          ▼
       ┌────────────────────────────────────────────┐
       │         Session 5: Agents                  │  sub-agents with their own context/tools
       │                                            │     depends on Skills + MCP (agents use both)
       └────────────────────────────────────────────┘
                          │
                          ▼
       ┌────────────────────────────────────────────┐
       │         Session 6: Hooks                   │  deterministic gates around LLM output
       │                                            │     depends on Agents (hooks wrap sub-agent calls)
       └────────────────────────────────────────────┘
                          │
                          ▼
       ┌────────────────────────────────────────────┐
       │         Session 7: Commands                │  user-facing entry points to workflows
       │                                            │     depends on Skills+Agents+Hooks (commands
       │                                            │     compose all three)
       └────────────────────────────────────────────┘
                          │
                          ▼
       ┌────────────────────────────────────────────┐
       │         Session 8: Capstone — plugin/      │  stitch the whole arc: build a GSD/Superpowers/
       │         workflow system build              │  SpecKit-style plugin on top of DotaChile
       └────────────────────────────────────────────┘
```

**Dependency rationale (concrete, not hand-wavy):**

1. **Intro (Session 1) first, always.** The graduate-student half of the room needs the LLM-evolution framing (ChatGPT → transformers → chain-of-thought → today) before the specifics land. The staff engineer half won't be insulted by it if the session also includes a hands-on "write an agent + skill in 10 minutes" payoff, which the existing topic list already implies.
2. **RAG before MCP** — both are "external context" primitives and feel similar; teaching RAG first lets MCP be framed as "the same idea but for *actions*, not just retrieval". RAG is also the most self-contained demo (`tools/email-rag` is prebuilt; no live system needs to be up beyond the search script) — low blast radius for an early session where the audience is calibrating trust.
3. **MCP before Skills** — Skills frequently call MCP tools; teaching Skills first forces hand-waving about what they call out to. With MCP taught, Skills become "named, versioned recipes that orchestrate MCP calls".
4. **Skills before Agents** — sub-agents load skills; explaining an agent before the audience has the "skill" mental model requires either teaching both simultaneously (overloaded) or handwaving. Taught in order, each primitive adds one new concept.
5. **Agents before Hooks** — Hooks wrap things (tool calls, sub-agent spawns). Teaching Hooks without Agents means demonstrating them against bare tool calls, which is a strictly less interesting demo than demonstrating them around a sub-agent boundary. The *reason* Hooks matter (deterministic gates around stochastic LLM output) is most vivid when the stochastic thing is an Agent.
6. **Hooks before Commands** — Commands are the top-level user-facing entry point, and good Commands compose Skills, Agents, and Hooks. Teaching Commands last lets each live demo show a command that uses all three primitives — the payoff moment for the arc.
7. **Capstone (Session 8) last** — by construction. The capstone is "build a plugin that assembles everything you've seen". Without Sessions 2-7 it has no parts to assemble.

**Cuts if the arc lands at 6 sessions:**
- **Merge Skills + Agents into one session** (call it "Skills & Agents"). They're the most similar-shaped ("named reusable units with their own context") and 1-hour coverage of both is plausible if the skill half stays a 15-min demo and the agent half takes 35 min.
- **Or merge Hooks + Commands** — Hooks-as-a-standalone-topic is the thinnest of the primitives; folding them into the Commands session as "Commands wrapped in safety rails" is defensible.

**Cuts if the arc lands at 4 sessions:**
- Session 1: Intro
- Session 2: RAG + MCP (together — both are "external context" primitives)
- Session 3: Skills + Agents + Hooks + Commands (one session = "all four primitives" — brutal pace, only viable because Session 4 is the capstone)
- Session 4: Capstone

The 4-session shape is legitimate but sacrifices the "one primitive per session" pedagogical rhythm. Recommend 6-8 unless audience availability forces 4.

## Boundaries: Session-Owned vs. Series-Shared

| Concern                                    | Session-owned | Series-shared |
| ------------------------------------------ | ------------- | ------------- |
| Deck content                               | Yes           |               |
| Speaker notes                              | Yes           |               |
| Diagrams                                   | Yes           |               |
| Session-specific prereqs (e.g., new MCP server) | Yes      |               |
| Demo commits + git tags                    | Yes (tags)    | Yes (commits on `master`) |
| Manifest / recovery plan                   | Yes           |               |
| Handout                                    | Yes           |               |
| Rehearsal log                              | Yes           |               |
| General environment setup (Docker, Claude Code install, Java 8) |      | Yes           |
| Marp theme / frontmatter snippet           |               | Yes           |
| Series audience framing ("who this is for", "what you'll leave with") |  | Yes           |
| Series index / session order / prereq graph |              | Yes           |
| Codebase `.planning/` artifacts            |               | Yes (lives at repo root, orthogonal) |

## Scaling Considerations

A 4–8-session arc is not "scale" in the traditional product sense. "Scale" here means: what breaks as session count grows from 1 to 8 to 24?

| Scale        | Architecture adjustments |
| ------------ | ------------------------ |
| 1-3 sessions | Current convention suffices. Manifest + tags + handout, no automation. |
| 4-8 sessions (this arc) | Introduce series README + SETUP.md + numeric `NN-` infix + tag convention + manifest template. All manual. |
| 9-20 sessions | Introduce a `make slides` build script. Introduce a `docs/presentations/_templates/SESSION_TEMPLATE/` scaffold with MANIFEST.md / HANDOUT.md stubs and a `new-session.sh` that copies + date-stamps. Consider rendering the series README from the per-session manifests programmatically. |
| 20+ sessions | Split the arc into "seasons" — nested folders (`docs/presentations/season-1/…`) — and elevate session IDs from `NN` to `S1-NN`. Until you have 20+, don't bother. |

### Scaling Priorities

1. **First thing that breaks:** keeping the series README in sync with the session folders. Mitigation: once you hit 6+ sessions, write a small script that regenerates the README's "Sessions" table from the per-session `MANIFEST.md` front-matter.
2. **Second thing that breaks:** theme/frontmatter drift between decks. Mitigation: `docs/presentations/THEME.md` with the canonical snippet + a note at the top of each deck "copy from THEME.md when starting a new one".
3. **Third thing that breaks:** tag-pollution (`session-03-post-v2`, `session-03-post-v3` after re-teaches). Mitigation: for re-teaches, cut a branch `replay/session-03-cohort-N` off `session-03-pre` rather than retagging.

## Anti-Patterns

### Anti-Pattern 1: Long-lived teaching branches

**What people do:** Create `workshop/session-03` branch, demo onto it, never merge.
**Why it's wrong:** Contradicts the `PROJECT.md` "demos land on `master`" constraint; the codebase becomes a museum of half-finished branches; the audience can't just clone and replay. Breaks the core promise that the codebase is a lab, not a showcase.
**Do this instead:** Land on `master`. Use `session-NN-pre` / `session-NN-post` tags as read-only bookmarks. For re-teaches, branch off `session-NN-pre` (not trunk).

### Anti-Pattern 2: Embedding commit SHAs in the deck body

**What people do:** Write `git show abc1234` directly on a slide. Looks traceable.
**Why it's wrong:** Decks get re-run on different cohorts; SHAs change; slides become lies; author re-renders the HTML, forgets to update slides, and the audience copies a 404 command. Also: bare SHAs on a slide are unreadable from the back of the room.
**Do this instead:** Slides reference the session by *name* (e.g., "Demo 1: fix XSS in comentarios"). Exact SHA mapping lives once, in `MANIFEST.md`. Post-session, the handout carries the compare-URL. The deck never names a SHA.

### Anti-Pattern 3: Per-session `setup.md` that duplicates the series SETUP

**What people do:** Each session folder gets its own `SETUP.md` describing how to install Java, Docker, Claude Code, etc.
**Why it's wrong:** Eight copies of near-identical setup instructions; one drifts; audience following session 5 hits a broken step from session 2's era.
**Do this instead:** One `docs/presentations/SETUP.md` at the series level. Each session's `MANIFEST.md` names its *incremental* prereqs only ("this session additionally needs the Plane MCP server configured per SETUP.md §4").

### Anti-Pattern 4: Rendering the HTML only at session time

**What people do:** Edit `.md`, defer running `marp-cli` until 10 minutes before going live.
**Why it's wrong:** (a) `docs/presentations/CLAUDE.md` rule violated ("Always regenerate and commit the `.html` alongside `.md` changes"); (b) unrendered decks in git are invisible to `HTMLPreview.github.io` audience-replay; (c) last-minute render catches real bugs (broken Mermaid SVG references) at the worst possible time.
**Do this instead:** Render-on-commit. If you edit `.md`, regenerate `.html` in the same commit. Existing convention — inherit and enforce.

### Anti-Pattern 5: Making the capstone depend on a feature the arc skipped

**What people do:** Plan a 6-session arc that cuts Hooks, then design a capstone that uses Hooks.
**Why it's wrong:** Breaks the pedagogical arc. The capstone is the payoff — if it uses a primitive the audience hasn't seen, it stops being a capstone and becomes a cold-open.
**Do this instead:** Design the capstone *after* deciding which sessions make the arc. The capstone uses only primitives taught in the chosen sessions. (See the 4-session cuts above — the capstone there can't assume Hooks as a standalone concept.)

### Anti-Pattern 6: Skipping the rehearsal to save prep time

**What people do:** Write the deck, write the manifest, skip the dry-run, hope live Claude behaves.
**Why it's wrong:** Live demos on legacy code regularly take 1.5-2× the time the author estimated. Skipping rehearsal = regularly blowing the 1-hour budget. Audience remembers the over-run, not the content.
**Do this instead:** Rehearse once, end-to-end, in the same conditions as the live session (same laptop, same network posture, same `docker compose` state). Log it in `REHEARSAL.md`. Cut content until the rehearsal runs in 50 minutes. The 10-minute buffer is for live Q&A and demo wobble.

## Integration Points

### External Services (per session, declared in session MANIFEST)

| Service                   | Integration pattern                                 | Notes |
| ------------------------- | --------------------------------------------------- | ----- |
| Claude Code CLI           | Presenter's laptop, pre-installed per SETUP.md      | Foundational — every session needs this |
| Anthropic API             | Via Claude Code, network-dependent                  | Flaky venue WiFi = #1 live risk |
| MCP servers (Plane, etc.) | Local Docker / process per SETUP.md                 | Introduced in Session 3, reused onward |
| `tools/email-rag`         | Local Python venv, per-developer corpus            | Session 2 demo surface; prebuilt, low live-risk |
| PostgreSQL (DotaChile DB) | Docker compose, localhost:5432                      | Required for any demo that touches Usuario/Clan/Torneo |
| Payara 5                  | Docker compose, localhost:8080                      | Required for any demo that needs the running app |
| GitHub                    | Remote for `git push` of commits + tags             | Tags need `git push --tags` to be visible in compare URLs |

### Internal Boundaries

| Boundary                                   | Communication               | Notes |
| ------------------------------------------ | --------------------------- | ----- |
| Session folder ↔ codebase (`src/`)         | Read-only (slides reference code); write during demos via commits + tags | The lab is mutated, the deck is not |
| Session N ↔ Session N+1                    | Git state at `session-N-post` == starting state of Session N+1 prep | Sessions are chronologically ordered, not just numbered |
| Deck `.md` ↔ Deck `.html`                  | `marp-cli` render, committed together | Existing rule, inherit |
| Deck `.md` ↔ `MANIFEST.md`                 | Deck names demos; manifest maps demo names → SHAs | One-way link only — the manifest points out to commits; the deck does not reference SHAs |
| Session MANIFEST ↔ git tags                | MANIFEST records the tag names; tags are the durable pointer | Tags are truth; manifest is documentation of the truth |
| `docs/presentations/*` ↔ `.planning/*`     | Independent; planning artifacts don't ship to audience | GSD state is author-side, slides are audience-side |
| Superpowers archive ↔ new arc              | Read-only reference ("here's how we planned a prior presentation") | `docs/superpowers/plans/2026-04-10-ai-driven-development-presentation.md` is exemplar, not template |

## Quality-Gate Self-Check

- Per-session anatomy maps to concrete file paths in this repo — yes (table above lists every component with its exact repo path).
- Traceability mechanism is one clear recommendation, not a menu — yes: **`session-NN-pre` / `session-NN-post` annotated git tags + a `MANIFEST.md` per session folder**. Rejected alternatives (branches, inline SHAs in deck, bare SHA lists) are named with reasons.
- Build order proposed with dependency rationale — yes (Intro → RAG → MCP → Skills → Agents → Hooks → Commands → Capstone with per-edge rationale).
- Existing `docs/presentations/` convention is either preserved with extensions or explicitly recommended to change — **preserved with extensions**: keep date-prefix folder convention; add numeric `NN-` infix; add `MANIFEST.md` / `HANDOUT.md` / `REHEARSAL.md` per session; add `README.md` / `SETUP.md` / optional `THEME.md` at series level. No rename to `docs/workshops/`.
- Honors 1-hour session budget — yes (Rehearsal-First Authoring pattern; cut-list required; 50-min rehearsal target with 10-min buffer).
- Honors Spanish-language constraint — yes (all audience-facing artifacts in Spanish: deck, handout, series README, SETUP; planning artifacts in English are OK per `PROJECT.md`).

## Sources

- `/Users/pmartinez/Documents/git/quantumentangled/dotachile/.planning/PROJECT.md` (project constraints, topic pool, key decisions) — HIGH confidence, authoritative
- `/Users/pmartinez/Documents/git/quantumentangled/dotachile/docs/presentations/CLAUDE.md` (existing Marp / Mermaid / HTML convention) — HIGH confidence, authoritative
- `/Users/pmartinez/Documents/git/quantumentangled/dotachile/docs/presentations/2026-04-10-ai-driven-development/` (reference layout: .md + .html + .mmd + .svg in one folder) — HIGH confidence
- `/Users/pmartinez/Documents/git/quantumentangled/dotachile/docs/superpowers/plans/2026-04-10-ai-driven-development-presentation.md` (prior presentation planning style — frontmatter, speaker notes as `<!-- -->`, section dividers, stage directions) — HIGH confidence
- `/Users/pmartinez/Documents/git/quantumentangled/dotachile/.planning/codebase/ARCHITECTURE.md` (lab surface the demos mutate) — HIGH confidence
- Training-data knowledge of common workshop-repo conventions (flat date-prefixed folders, per-session self-contained directories, pre/post tags for live-coding demos) — MEDIUM confidence. **WebSearch was unavailable in this session (permission denied), so industry-wide convention claims are not externally verified.** The repo-local recommendations above do not depend on these general claims; they depend on the existing project state, which is verified.

---
*Architecture research for: AI-assisted workshop series on a legacy Java EE codebase, Spanish-language, 4-8 one-hour live sessions, demos landing on `master`*
*Researched: 2026-04-19*
