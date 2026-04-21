---
phase: 0
slug: series-scaffolding
status: draft
nyquist_compliant: false
wave_0_complete: false
created: 2026-04-20
---

# Phase 0 — Validation Strategy

> Per-phase validation contract for feedback sampling during execution.
> Phase 0 is documentation-only; there is no programmatic test framework. Validation is grep-based and shell-command-based. Every acceptance criterion maps to a runnable shell check.

---

## Test Infrastructure

| Property | Value |
|----------|-------|
| **Framework** | Shell (`grep`, `test`, `ls`, `jq`, `docker compose config`) |
| **Config file** | none — shell is sufficient |
| **Quick run command** | `bash .planning/phases/00-series-scaffolding/validate.sh` (Wave 0 artifact — optional; alternative is per-plan `## Verification` checklist) |
| **Full suite command** | same as quick run |
| **Estimated runtime** | < 10 seconds (all checks are filesystem reads + one `docker compose config`) |

---

## Sampling Rate

- **After every task commit:** Run the grep trio for the artifact touched (e.g., after editing `docker-compose.yml`, run the SCAF-05 checks).
- **After every plan wave:** Run the full row set in the Per-Task Verification Map.
- **Before `/gsd-verify-work`:** All 22 rows green; `docker compose config` exits 0; no floating tags.
- **Max feedback latency:** < 10 seconds.

---

## Per-Task Verification Map

| Req ID | Plan | Wave | Requirement | Threat Ref | Secure Behavior | Test Type | Automated Command | File Exists | Status |
|--------|------|------|-------------|------------|-----------------|-----------|-------------------|-------------|--------|
| SCAF-01 | 07 | 2 | README.md lists 9 sessions | — | N/A | grep | `test $(grep -c "^\| 0[1-9] " docs/presentations/README.md) -eq 9` | ❌ W0 | ⬜ pending |
| SCAF-01 | 07 | 2 | README.md status column valid | — | N/A | grep | `grep -E "^\| 0[1-9] \| .* \| .* \| (pending\|rehearsed\|delivered) \|" docs/presentations/README.md \| wc -l` ≥ 9 | ❌ W0 | ⬜ pending |
| SCAF-01 | 07 | 2 | README.md in Spanish | — | N/A | grep | `grep -q "Próxima\|Sesión\|Fecha\|Estado" docs/presentations/README.md` | ❌ W0 | ⬜ pending |
| SCAF-02 | 04 | 1 | SETUP.md covers all tool sections | — | N/A | grep | `test $(grep -c "^## " docs/presentations/SETUP.md) -ge 8` | ❌ W0 | ⬜ pending |
| SCAF-02 | 04 | 1 | SETUP.md in Spanish | — | N/A | grep | `grep -q "instalación\|configuración\|Apéndice\|Requisitos" docs/presentations/SETUP.md` | ❌ W0 | ⬜ pending |
| SCAF-02 | 04 | 1 | SETUP.md references all required tools | T-V8 (PII note) | SETUP re-cites redaction invariant | grep | `grep -c -E "docker\|claude\|marp\|mermaid\|ollama\|qwen2.5\|nomic-embed-text\|email-rag" docs/presentations/SETUP.md` ≥ 8 | ❌ W0 | ⬜ pending |
| SCAF-03 | 06 | 1 | THEME.md exists with Marp frontmatter block | — | N/A | grep | `grep -q "marp: true" docs/presentations/THEME.md && grep -q "theme: gaia" docs/presentations/THEME.md` | ❌ W0 | ⬜ pending |
| SCAF-04 | 06 | 1 | CLAUDE.md extended with series sections | — | N/A | grep | `grep -q "session-NN-pre" docs/presentations/CLAUDE.md && grep -q "MANIFEST.md" docs/presentations/CLAUDE.md && grep -q "REHEARSAL.md" docs/presentations/CLAUDE.md && grep -q "HANDOUT.md" docs/presentations/CLAUDE.md && grep -q "asciinema\|VHS" docs/presentations/CLAUDE.md` | ❌ W0 | ⬜ pending |
| SCAF-05 | 01 | 1 | docker-compose.yml digest-pinned | T-V10 (supply chain) | No floating tags | grep | `grep -E '^\s+image:' docker-compose.yml \| grep -v '@sha256:' \| wc -l` = 0 | ✅ existing | ⬜ pending |
| SCAF-05 | 01 | 1 | Dockerfile digest-pinned | T-V10 | No floating tags | grep | `grep -E '^FROM' Dockerfile \| grep -v '@sha256:' \| wc -l` = 0 | ✅ existing | ⬜ pending |
| SCAF-05 | 01 | 1 | `docker compose config` validates | T-V10 | Compose file parses with pinned digests | cmd | `docker compose config > /dev/null` exit 0 | ✅ existing | ⬜ pending |
| SCAF-06 | 02 | 1 | CONCERNS-MAPPING.md table has claims | — | N/A | shell | `test -f docs/presentations/CONCERNS-MAPPING.md && grep -c "\| .* \| .* \| .* \| Session" docs/presentations/CONCERNS-MAPPING.md` ≥ 5 | ❌ W0 | ⬜ pending |
| SCAF-06 | 02 | 1 | Every HIGH/MED CONCERNS item listed | — | N/A | shell | `for s in XSS-01 XSS-02 XSS-03 XSS-04 XSS-05 PVPGN-PREP TORNEO-GODCLASS TORNEO-N1 DOCKER-PINS; do grep -q "$s" docs/presentations/CONCERNS-MAPPING.md \|\| echo MISSING; done` returns empty | ❌ W0 | ⬜ pending |
| SCAF-07 | 03 | 1 | GLOSSARY/ has 6 mmd + 6 svg + md + html | — | N/A | shell | `test -d docs/presentations/GLOSSARY && test $(ls docs/presentations/GLOSSARY/*.mmd \| wc -l) -eq 6 && test $(ls docs/presentations/GLOSSARY/*.svg \| wc -l) -eq 6 && test -f docs/presentations/GLOSSARY/GLOSSARY.md && test -f docs/presentations/GLOSSARY/GLOSSARY.html` | ❌ W0 | ⬜ pending |
| SCAF-07 | 03 | 1 | GLOSSARY.md defines all 6 primitives | — | N/A | shell | `for p in RAG MCP Skill Agent Hook "Slash Command"; do grep -q -E "^#\s*$p" docs/presentations/GLOSSARY/GLOSSARY.md \|\| echo MISSING; done` returns empty | ❌ W0 | ⬜ pending |
| SCAF-08 | 05 | 1 | MANIFEST.template.md placeholder fields | — | N/A | grep | `grep -q "session-NN-pre" docs/presentations/MANIFEST.template.md && grep -q "session-NN-post" docs/presentations/MANIFEST.template.md && grep -q "Slide.*commit" docs/presentations/MANIFEST.template.md && grep -q "Recovery" docs/presentations/MANIFEST.template.md && grep -q "compare" docs/presentations/MANIFEST.template.md && grep -q "version" docs/presentations/MANIFEST.template.md` | ❌ W0 | ⬜ pending |
| SCAF-09 | 05 | 1 | REHEARSAL.template.md checklist | — | N/A | grep | `grep -c "^- \[ \]" docs/presentations/REHEARSAL.template.md` ≥ 5 | ❌ W0 | ⬜ pending |
| SCAF-09 | 05 | 1 | REHEARSAL.template.md references QUAL-02/03/09 | — | N/A | grep | `grep -q "QUAL-02" docs/presentations/REHEARSAL.template.md && grep -q "QUAL-03" docs/presentations/REHEARSAL.template.md && grep -q "QUAL-09" docs/presentations/REHEARSAL.template.md` | ❌ W0 | ⬜ pending |
| SCAF-10 | 05 | 1 | HANDOUT.template.md 5 Spanish sections | — | N/A | grep | `grep -q "¿Qué vimos?" docs/presentations/HANDOUT.template.md && grep -q "Comandos para probar" docs/presentations/HANDOUT.template.md && grep -q "Link de comparación" docs/presentations/HANDOUT.template.md && grep -q "Próxima sesión" docs/presentations/HANDOUT.template.md && grep -q "Lecturas" docs/presentations/HANDOUT.template.md` | ❌ W0 | ⬜ pending |
| QUAL-01..12 | 05 | 1 | QUAL-GATES.md defines all 12 gates | — | N/A | shell | `for n in 01 02 03 04 05 06 07 08 09 10 11 12; do grep -q "QUAL-$n" docs/presentations/QUAL-GATES.md \|\| echo MISSING; done` returns empty | ❌ W0 | ⬜ pending |

*Status: ⬜ pending · ✅ green · ❌ red · ⚠️ flaky*

*Plan numbering follows the research Plan-Splitting Recommendation:*
*01 docker-pinning · 02 concerns-mapping · 03 primitives-glossary · 04 setup-doc · 05 sidecar-templates-and-qual-gates · 06 theme-and-claude-md-ext · 07 series-index*

---

## Wave 0 Requirements

All artifacts below are created by Phase 0 itself — there is no external test framework to install.

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

Optional Wave 0 artifact: `.planning/phases/00-series-scaffolding/validate.sh` collecting the 22 commands above into one script.

---

## Manual-Only Verifications

| Behavior | Requirement | Why Manual | Test Instructions |
|----------|-------------|------------|-------------------|
| Primitives glossary renders cleanly as Marp deck | SCAF-07 | Requires visual inspection of rendered HTML | Run `npx @marp-team/marp-cli docs/presentations/GLOSSARY/GLOSSARY.md -o /tmp/glossary.html` then open in a browser; confirm 6 slides render, no broken Mermaid |
| SETUP.md end-to-end works on a fresh clone | SCAF-02 | Can only verify by executing every step on a clean machine | A presenter (or CI) clones the repo at `session-01-pre`, follows SETUP.md top-to-bottom, reaches Docker up + Payara responsive + Ollama models pulled + `tools/email-rag` corpus built. Capture the time-to-green. |
| CONCERNS-MAPPING.md claims are coherent | SCAF-06 | Human judgment on whether "claimed by Session NN" matches that session's actual scope | Reviewer walks CONCERNS-MAPPING.md row-by-row and confirms each Session NN claim aligns with that phase's goal/REQ-IDs in ROADMAP.md |
| THEME.md copy-paste snippet produces consistent decks | SCAF-03 | Requires rendering a sample deck against the snippet | Copy THEME.md frontmatter block into a test `.md`, run `npx @marp-team/marp-cli test.md -o /tmp/test.html`, confirm rendered theme matches intent |

---

## Validation Sign-Off

- [ ] All tasks have an automated grep/shell check in the Per-Task Verification Map above, or are listed in Manual-Only Verifications with explicit instructions
- [ ] Sampling continuity: no 3 consecutive tasks without a grep-based check
- [ ] Wave 0 covers all MISSING file-exists references (every new file above)
- [ ] No watch-mode flags (this is a doc-only phase; no processes to keep alive)
- [ ] Feedback latency < 10 seconds (all checks are filesystem + one docker config)
- [ ] `nyquist_compliant: true` set in frontmatter once plans are merged and all rows pass

**Approval:** pending
