---
status: partial
phase: 00-series-scaffolding
source: [00-VERIFICATION.md]
started: 2026-04-21T12:00:00Z
updated: 2026-04-21T12:00:00Z
---

## Current Test

[awaiting human testing]

## Tests

### 1. Render GLOSSARY.html visual confirmation
expected: 6 primitive slides (RAG, MCP, Skill, Agent, Hook, Slash Command) render cleanly in a browser with embedded SVG diagrams; no broken image tags; no Marp error pages; deck is visually consistent with THEME.md light variant.
result: [pending]

### 2. SETUP.md end-to-end on fresh clone
expected: Following `docs/presentations/SETUP.md` on a fresh clone reaches working state — `docker compose up -d` brings Payara + Postgres healthy; Claude Code installed; Marp/Mermaid CLIs invokable; Ollama `qwen2.5:7b` + `nomic-embed-text` pulled; optionally `tools/email-rag/` corpus builds.
result: [pending]

### 3. CONCERNS-MAPPING semantic coherence review
expected: Walking `docs/presentations/CONCERNS-MAPPING.md` row-by-row, each `claimed` or `candidate` row is coherent with the per-session requirements in ROADMAP.md (e.g., XSS-01..04 → Session 8 matches S08-01; TORNEO-GODCLASS → Session 6 matches S06-01).
result: [pending]

### 4. THEME.md copy-paste consistency
expected: Copying the canonical light/dark YAML frontmatter blocks into a test `.md` and rendering via Marp CLI produces a properly themed deck (light default; dark when overridden).
result: [pending]

## Summary

total: 4
passed: 0
issues: 0
pending: 4
skipped: 0
blocked: 0

## Gaps
