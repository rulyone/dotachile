---
phase: 00-series-scaffolding
plan: 04
type: execute
wave: 1
depends_on: []
files_modified:
  - docs/presentations/SETUP.md
autonomous: true
requirements:
  - SCAF-02
tags:
  - documentation
  - setup
  - toolchain
user_setup: []

must_haves:
  truths:
    - "Un developer que clone el repo en session-01-pre puede seguir SETUP.md end-to-end y llegar a: Docker up + Payara responsive + Claude Code instalado + Marp/Mermaid CLIs disponibles + Ollama con qwen2.5:7b + nomic-embed-text + tools/email-rag/ corpus opcional construido (si van a ver Session 3)"
    - "SETUP.md está en español (prose), código inline en bash/English"
    - "Layer estructurado: quick-start (3 líneas para seniors) + Apéndice (paso-a-paso para máquina limpia)"
    - "Cadencia de actualización de digests Docker documentada (monthly)"
    - "La invariante PII-redaction del corpus email se re-cita explícitamente (SETUP no debe inducir a committear mbox crudo)"
    - "Comandos multi-plataforma — no solo macOS (brew/apt/winget per Pitfall 6)"
  artifacts:
    - path: "docs/presentations/SETUP.md"
      provides: "Guía de setup de toolchain end-to-end en español (layered: quick-start + apéndice)"
      contains: "## Quick-start"
  key_links:
    - from: "docs/presentations/SETUP.md §Cadencia de actualización de pins"
      to: "docker-compose.yml digest pinning work (SCAF-05 plan 00-01)"
      via: "monthly re-pin procedure documentation"
      pattern: "docker manifest inspect"
    - from: "docs/presentations/SETUP.md §tools/email-rag"
      to: "tools/email-rag/README.md"
      via: "relative link for per-developer corpus build"
      pattern: "tools/email-rag"
---

<objective>
Author `docs/presentations/SETUP.md` — the single Spanish-language, end-to-end toolchain install guide that every session (Phases 1-9) assumes. Layered structure: a **quick-start** 3-line block for experienced developers who already have the toolchain, followed by an **Apéndice** with full platform-specific install steps for clean machines.

**Purpose (SCAF-02):** Without SETUP.md, every session re-documents "cómo instalar Marp" etc. Phase 0's deliverable is the single canonical guide that CURR-07 ("audience tooling assumptions consistent across the arc") depends on.

**Output:** One new file (`docs/presentations/SETUP.md`) — Spanish prose with English bash code, covering:
- Docker Compose + Payara 5 + Postgres 15 (references docker-compose.yml — pre-pin ritual)
- Claude Code CLI + Node.js 20+
- Marp CLI `@4.3.1` + Mermaid CLI `@11.12.0` (pinned per RESEARCH tool inventory)
- Ollama + `qwen2.5:7b` + `nomic-embed-text` (model tags drift monthly — flagged)
- `tools/email-rag/` corpus (per-developer, PII-redacted, NOT in repo)
- asciinema + VHS (optional — for QUAL-02 fallback recordings)
- Cadencia de actualización de digests Docker (monthly)

Security-relevant: SETUP.md §6 (email-rag) re-cites the PII-redaction invariant per `<security_context>` (V14 Configuration / T-V8).
</objective>

<execution_context>
@/Users/pmartinez/Documents/git/quantumentangled/dotachile/.claude/get-shit-done/workflows/execute-plan.md
@/Users/pmartinez/Documents/git/quantumentangled/dotachile/.claude/get-shit-done/templates/summary.md
</execution_context>

<context>
@.planning/PROJECT.md
@.planning/ROADMAP.md
@.planning/STATE.md
@.planning/phases/00-series-scaffolding/00-CONTEXT.md
@.planning/phases/00-series-scaffolding/00-RESEARCH.md
@.planning/phases/00-series-scaffolding/00-PATTERNS.md
@.planning/phases/00-series-scaffolding/00-VALIDATION.md
@.planning/codebase/STACK.md
@./CLAUDE.md
@tools/email-rag/README.md
</context>

<tasks>

<task type="auto" tdd="false">
  <name>Task 1: Author docs/presentations/SETUP.md with layered quick-start + Apéndice structure in Spanish</name>
  <files>docs/presentations/SETUP.md</files>
  <read_first>
    - .planning/codebase/STACK.md (tool versions + Payara/Postgres specifics)
    - ./CLAUDE.md (root — stack, build commands `mvn -o package` / `docker compose up -d --build` / `./dev-sync.sh java|views|all`, email-rag convention)
    - tools/email-rag/README.md (per-developer corpus build procedure)
    - .planning/phases/00-series-scaffolding/00-CONTEXT.md §"Claude's Discretion → SETUP.md depth" (layered default)
    - .planning/phases/00-series-scaffolding/00-RESEARCH.md §"SETUP.md End-to-End (SCAF-02)" — verified tool versions + landmines + cadence
    - .planning/phases/00-series-scaffolding/00-RESEARCH.md §"Security Domain" — PII redaction invariant for email corpus (T-V8)
    - .planning/phases/00-series-scaffolding/00-PATTERNS.md §"docs/presentations/SETUP.md (install guide)" — root CLAUDE.md as structural analog, mixed Spanish-prose-English-code convention
    - .planning/phases/00-series-scaffolding/00-VALIDATION.md (SCAF-02 grep rows — ≥8 H2 sections, Spanish keywords, all required tools grepped)
  </read_first>
  <action>
    Create a new file at `docs/presentations/SETUP.md` with the content below. Spanish prose, English bash code (matches root CLAUDE.md mixed convention per PATTERNS.md). Every step must name **all three platforms** (brew / apt / winget or choco) per Pitfall 6 — no macOS-only dead-ends.

    **Write this file verbatim:**

    ```markdown
    # SETUP — Serie AI-SWE 2026

    Guía end-to-end para dejar el entorno listo antes de cualquier sesión del arco.
    Para la lista de sesiones ver [`README.md`](README.md).

    Esta doc cubre dos perfiles:

    - **Quick-start** — ya tenés Docker, Node 20+, Claude Code y Ollama instalados;
      solo necesitás levantar el repo y opcionalmente el corpus RAG.
    - **Apéndice — setup desde cero** — máquina limpia; cada paso nombra los 3 sistemas
      (macOS / Linux / Windows).

    > **Regla Spanish-first:** esta guía y la narración de cada sesión están en español.
    > La salida de Claude Code y los comandos de CLI se quedan en inglés — es el estado
    > actual del ecosistema.

    ---

    ## Quick-start (si ya tenés el toolchain)

    ```bash
    # 1. levanta los servicios (Payara + Postgres) — idempotente
    docker compose up -d

    # 2. hot-push de código (Java + views) al contenedor
    ./dev-sync.sh all

    # 3. smoke-test del corpus RAG (opcional; solo si vas a ver Session 3)
    tools/email-rag/.venv/bin/python tools/email-rag/search.py "torneo"
    ```

    Si cualquiera de esos tres falla, leé el **Apéndice** abajo.

    ---

    ## Requisitos de hardware

    - ≥ 16 GB RAM recomendado (el modelo `qwen2.5:7b` usa ~6 GB; Payara otros ~2 GB).
    - Apple Silicon: sin problemas. Intel Mac: ídem.
    - Linux x86_64: sin problemas. Windows: WSL2 obligatorio para `./dev-sync.sh`.

    ---

    ## Apéndice: setup desde cero

    ### 1. Docker + Docker Compose v2

    **macOS:**
    ```bash
    brew install --cask docker
    ```

    **Linux (Ubuntu/Debian):**
    ```bash
    sudo apt-get install docker.io docker-compose-plugin
    ```

    **Windows:**
    ```powershell
    winget install Docker.DockerDesktop
    ```

    Verificá:
    ```bash
    docker --version          # ≥ 24.0.2
    docker compose version    # ≥ 2.19.0 — atención: `docker compose` (v2), no `docker-compose` (v1)
    ```

    El repo usa sintaxis v2 (`condition: service_healthy`) — v1 no funciona.

    ### 2. Levantar el stack DotaChile (Payara 5 + Postgres 15)

    ```bash
    docker compose up -d
    docker compose ps   # ambos servicios deben mostrar "healthy"
    ```

    **Pre-warm obligatorio antes de cualquier sesión en vivo:** arrancá
    `docker compose up -d` **al menos 10 minutos antes** — Payara 5 tarda 60-90 segundos
    en el boot frío y QUAL-09 prohíbe hacer `docker compose up` como paso en vivo.

    **Payara 5 EOL:** la versión 5.2022.5 es la última de la línea 5.x. NO actualizar a
    Payara 6 — es Jakarta EE 10 y rompería todos los imports `javax.*` de la codebase.
    El `Dockerfile` documenta esto inline.

    ### 3. Node.js 20+ (para Marp CLI + Mermaid CLI)

    **macOS:**
    ```bash
    brew install node@20
    ```

    **Linux:**
    ```bash
    curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
    sudo apt-get install -y nodejs
    ```

    **Windows:**
    ```powershell
    winget install OpenJS.NodeJS.LTS
    ```

    Verificá:
    ```bash
    node --version    # ≥ v20.x
    npm --version     # ≥ 10.x
    ```

    ### 4. Claude Code CLI

    Instalá el CLI oficial siguiendo las instrucciones de
    [docs.claude.com](https://docs.claude.com) (scope usuario, no sistema).

    Este repo asume:
    - El directorio hermano `../dotachile-emails/corpus` se auto-monta vía
      `.claude/settings.json` (`additionalDirectories`).
    - `.claude/skills/`, `.claude/commands/`, `.claude/hooks/` se cargan desde el
      repo automáticamente cuando corrés `claude` en la raíz.

    ### 5. Marp CLI + Mermaid CLI (pineados)

    Ambos corren vía `npx` — no se instalan globalmente.

    ```bash
    npx @marp-team/marp-cli@4.3.1 --version      # verifica instalación
    npx @mermaid-js/mermaid-cli@11.12.0 --version
    ```

    Convenciones de render (ver `docs/presentations/CLAUDE.md`):
    - `.md` del deck se renderiza a `.html` con `--html`.
    - `.mmd` de Mermaid se renderiza a `.svg` con `-b transparent`.
    - Commitear siempre **fuente + salida** juntos (render-on-commit rule).

    ### 6. Ollama + modelos

    **macOS / Linux:**
    ```bash
    curl -fsSL https://ollama.com/install.sh | sh
    ```

    **Windows:**
    ```powershell
    winget install Ollama.Ollama
    ```

    Pull de los modelos:
    ```bash
    ollama pull qwen2.5:7b         # ~4.7 GB — modelo generativo, multilingüe
    ollama pull nomic-embed-text   # ~275 MB — embeddings para RAG
    ```

    **Advertencia:** los tags de Ollama driftean mes a mes. Verificá en
    [ollama.com/library](https://ollama.com/library) el día del workshop que
    `qwen2.5:7b` y `nomic-embed-text` siguen siendo las etiquetas actuales.
    Phase 3 (Session RAG) corre un research-gate explícito para re-verificar.

    ### 7. `tools/email-rag/` — corpus de emails (opcional)

    **El corpus NO vive en el repo.** Cada developer lo construye desde su propio
    Gmail Takeout siguiendo [`tools/email-rag/README.md`](../../tools/email-rag/README.md).

    > **Invariante de seguridad (T-V8 / V14 Configuration):** el corpus está
    > **PII-redactado upstream** por el pipeline en `tools/email-rag/`
    > (`[REDACTED_PERSON]`, `[REDACTED_EMAIL_ADDRESS]`, `USER_NNNN`, etc).
    > **El mbox crudo NO se commitea** — vive en un sparse bundle encriptado
    > separado que no se monta en sesiones normales. Si tocás el redactor,
    > seguí las reglas de `tools/email-rag/CLAUDE.md`.

    Si `search.py` falla con `error: corpus dir not found`, todavía no construiste
    el corpus. Solo Session 3 (RAG) lo requiere; las demás sesiones funcionan sin él.

    ### 8. asciinema + VHS (opcional — para fallback recordings de QUAL-02)

    **macOS:**
    ```bash
    brew install asciinema vhs
    ```

    **Linux:**
    ```bash
    sudo apt-get install asciinema
    # VHS: descargá el binario desde github.com/charmbracelet/vhs/releases
    ```

    **Windows:**
    ```powershell
    pipx install asciinema
    # VHS: github.com/charmbracelet/vhs/releases (binario Windows disponible)
    ```

    Cada sesión commitea su fallback artefacto (`.cast` o `.tape`) junto al deck
    — es requisito de QUAL-02 (ver `QUAL-GATES.md`).

    ### 9. Pre-pull de imágenes Docker (antes de cada sesión)

    Para evitar pull en vivo durante el workshop:

    ```bash
    docker compose pull
    ```

    Las imágenes están pinneadas por digest `@sha256:` en `docker-compose.yml` y
    `Dockerfile` — pull siempre baja los mismos bytes.

    ---

    ## Cadencia de actualización de pins de Docker

    Los digests `@sha256:` en `docker-compose.yml` y `Dockerfile` se re-pinean
    **mensualmente** para no quedar atrás en parches de seguridad upstream.

    **Comando de re-pin (corré antes de cada ciclo mensual):**

    ```bash
    # Postgres (manifest-list digest, cross-platform)
    docker buildx imagetools inspect postgres:15.17-bookworm \
      --format "{{json .Manifest}}" | jq -r .digest

    # Maven (build stage del Dockerfile)
    docker buildx imagetools inspect maven:3.8.8-openjdk-11 \
      --format "{{json .Manifest}}" | jq -r .digest

    # Payara (runtime stage — 5.2022.5 está EOL-congelado, re-verificar igual)
    docker buildx imagetools inspect payara/server-full:5.2022.5 \
      --format "{{json .Manifest}}" | jq -r .digest
    ```

    Si `buildx imagetools` no está disponible, fallback a:

    ```bash
    docker manifest inspect <image>:<tag> | \
      jq -r '.manifests[] | select(.platform.architecture=="amd64") | .digest'
    ```

    Substituí el digest devuelto en la línea correspondiente de `docker-compose.yml`
    o `Dockerfile`, actualizá el comentario `# Pinned YYYY-MM-DD`, y commiteá.

    ---

    ## Troubleshooting común

    - **`dev-sync.sh` no empuja cambios:** probablemente el nombre del contenedor cambió.
      Fallback: `docker compose build && docker compose up -d` (rebuild completo, ~3 minutos).
    - **Payara no responde en `localhost:8080`:** esperá 60-90 segundos después del
      `docker compose up -d`. Si sigue sin responder, `docker compose logs app` da la pista.
    - **Postgres en red corporativa con DNS bloqueado:** el compose tiene `dns: [1.1.1.1, 8.8.8.8]`
      hardcoded; si eso no basta, chequeá la política de la red.
    - **Ollama lento:** el primer query contra `qwen2.5:7b` puede tardar 5-15 segundos en
      warm-up. Session 3 incluye una "smoke-test slide" para hacer ese warm-up on-stage
      y evitar dead air en la demo real (S03-04, Pitfall 13).

    ---

    ## Referencias

    - `README.md` — índice de las 9 sesiones
    - `CLAUDE.md` (root) — stack + comandos de build + regla Spanish-language
    - `docs/presentations/CLAUDE.md` — convenciones Marp/Mermaid + naming NN-infix
    - `docs/presentations/QUAL-GATES.md` — las 12 reglas QUAL (QUAL-01..QUAL-12)
    - `tools/email-rag/README.md` — cómo construir tu propio corpus
    ```

    **Constraints (per CONTEXT + RESEARCH + PATTERNS + VALIDATION):**
    - **Spanish prose, English code.** Every section heading Spanish; `bash` code blocks English. Matches root CLAUDE.md mixed convention per PATTERNS.md.
    - **≥ 8 H2 sections** (`^## `) required by VALIDATION.md SCAF-02 row. The template above has: Quick-start, Requisitos de hardware, Apéndice: setup desde cero (+ 9 H3 subsections under it), Cadencia de actualización de pins de Docker, Troubleshooting común, Referencias — 6 H2. **Must also include** (to reach ≥8 H2): promote two of the H3 subsections (Docker + Docker Compose v2, Ollama + modelos) to H2 by changing the `### ` prefix. Rewriting `### 1. Docker + Docker Compose v2` → `## 1. Docker + Docker Compose v2` (and the other major sections) during authoring gets you ≥8 top-level sections. Recommended: promote EVERY `### <N>. <topic>` section to `## <N>. <topic>` since each is a distinct tool install. That yields: Quick-start, Requisitos, 1. Docker, 2. Stack levantar, 3. Node.js, 4. Claude Code, 5. Marp/Mermaid, 6. Ollama, 7. email-rag, 8. asciinema, 9. pre-pull, Cadencia, Troubleshooting, Referencias = **14 H2 sections**. Use the H2-per-numbered-section approach.
    - **Three-platform install commands** everywhere (brew / apt / winget or choco) per Pitfall 6.
    - **Payara 5 EOL note** inline per Pitfall 8.
    - **PII-redaction invariant re-cited** in §tools/email-rag per `<security_context>` T-V8. Exact phrase from RESEARCH.md Security Domain: *"El corpus está PII-redactado upstream; el mbox crudo NO se commitea."* Adapt into the §7 prose above — already included.
    - **Ollama model tag drift** flagged (RESEARCH §SETUP.md landmine 1).
    - **Tool versions pinned** to RESEARCH values: `@marp-team/marp-cli@4.3.1`, `@mermaid-js/mermaid-cli@11.12.0`, Node ≥20, Docker ≥24, Compose ≥2.19.
    - **dev-sync.sh fallback** documented (`docker compose build && docker compose up -d`) per Landmine 5.
    - **Do NOT link to non-existent files.** `README.md` and `QUAL-GATES.md` are planned (plans 07, 05) — they're referenced in the "Referencias" section. This is a forward reference that becomes valid when plans 05 and 07 land (Waves 1 and 2 respectively).
  </action>
  <verify>
    <automated>test -f docs/presentations/SETUP.md && test $(grep -c "^## " docs/presentations/SETUP.md) -ge 8 && grep -q "instalación\|configuración\|Apéndice\|Requisitos" docs/presentations/SETUP.md</automated>
  </verify>
  <acceptance_criteria>
    - `test -f docs/presentations/SETUP.md` succeeds
    - VALIDATION.md SCAF-02 row 1: `test $(grep -c "^## " docs/presentations/SETUP.md) -ge 8` succeeds (≥ 8 top-level H2 sections)
    - VALIDATION.md SCAF-02 row 2: `grep -q "instalación\|configuración\|Apéndice\|Requisitos" docs/presentations/SETUP.md` succeeds (Spanish)
    - VALIDATION.md SCAF-02 row 3: `grep -c -E "docker|claude|marp|mermaid|ollama|qwen2.5|nomic-embed-text|email-rag" docs/presentations/SETUP.md` returns `≥ 8` (all required tools referenced)
    - Pitfall 6 multi-platform: `grep -c -E "brew|apt|winget|choco|pipx" docs/presentations/SETUP.md` returns `≥ 6`
    - Pitfall 8 EOL note: `grep -q "Payara 5 EOL\|Payara 5.*EOL\|EOL.*Payara" docs/presentations/SETUP.md` succeeds
    - PII invariant re-cited (T-V8): `grep -q "PII-redactado\|PII-redaction\|mbox crudo NO se commitea\|redactado upstream" docs/presentations/SETUP.md` succeeds
    - Ollama drift flagged: `grep -q "driftean\|drift\|mes a mes\|research-gate" docs/presentations/SETUP.md` succeeds
    - Cadencia de Docker: `grep -q "Cadencia de actualización" docs/presentations/SETUP.md` succeeds
    - Version pins: `grep -q "@4.3.1" docs/presentations/SETUP.md && grep -q "@11.12.0" docs/presentations/SETUP.md` succeeds
    - Pre-warm mention (QUAL-09 cross-link): `grep -q "pre-warm\|Pre-warm" docs/presentations/SETUP.md` succeeds
  </acceptance_criteria>
  <done>
    `docs/presentations/SETUP.md` exists with ≥8 H2 sections, Spanish prose + English code, covers all required tools (docker, claude, marp, mermaid, ollama, qwen2.5, nomic-embed-text, email-rag), multi-platform install commands, Payara 5 EOL warning, PII invariant re-cited, Ollama drift flagged, monthly Docker re-pin cadence documented.
  </done>
</task>

</tasks>

<threat_model>
## Trust Boundaries

| Boundary | Description |
|----------|-------------|
| External install sources → developer machine | `brew`, `apt`, `winget`, `pipx`, `npm`, `ollama.com/install.sh` all execute on the developer's machine during SETUP |
| Developer email archive → repo | If SETUP doesn't re-cite the PII invariant, a contributor might commit raw mbox data |

## STRIDE Threat Register

| Threat ID | Category | Component | Disposition | Mitigation Plan |
|-----------|----------|-----------|-------------|-----------------|
| T-00-V8-01 | Information Disclosure | `tools/email-rag/` — raw mbox commit risk if invariant not documented | mitigate | SETUP.md §7 re-cites the PII-redaction invariant verbatim: "El corpus está PII-redactado upstream; el mbox crudo NO se commitea." ASVS V8 Data Protection / V14 Configuration. Verified by `grep -q "PII-redactado\|mbox crudo NO se commitea" docs/presentations/SETUP.md`. |
| T-00-V14-01 | Information Disclosure | `.env` file accidentally committed | accept | SETUP.md does not create `.env` files; root `.gitignore` already handles this (pre-existing). SETUP.md reader is not instructed to create an `.env`. Low risk — workshop demos use the defaults hardcoded in `docker-compose.yml` (`${POSTGRES_PASSWORD:-dotachile}`). ASVS V14. |
| T-00-V1-01 | Tampering (supply chain of install scripts) | `curl -fsSL https://ollama.com/install.sh \| sh` | accept | Ecosystem-standard install pattern. Ollama publishes the install script on their own domain with HTTPS; risk is the same as installing any third-party tool. Not specific to this workshop; would only be problematic if Ollama themselves were compromised. ASVS V1. |

**Expected secure behavior:** SETUP.md §7 re-cites the PII invariant for the email corpus; a grep for `PII-redactado` or `mbox crudo NO se commitea` returns a match.
</threat_model>

<verification>
Run all VALIDATION.md SCAF-02 rows:

```bash
test -f docs/presentations/SETUP.md
test $(grep -c "^## " docs/presentations/SETUP.md) -ge 8
grep -q "instalación\|configuración\|Apéndice\|Requisitos" docs/presentations/SETUP.md
grep -c -E "docker|claude|marp|mermaid|ollama|qwen2.5|nomic-embed-text|email-rag" docs/presentations/SETUP.md   # ≥ 8
```

Plus the security check:

```bash
grep -q "PII-redactado\|mbox crudo NO se commitea" docs/presentations/SETUP.md   # T-V8 mitigation
```

All must pass.
</verification>

<success_criteria>
A new developer cloning the repo at `session-01-pre` can follow `docs/presentations/SETUP.md` end-to-end and reach a working state (Docker Compose up, Payara + Postgres responsive, Claude Code installed, Marp/Mermaid CLIs available, Ollama with both models pulled, `tools/email-rag/` corpus built if they need RAG). Satisfies ROADMAP.md Phase 0 Success Criterion #2 and unblocks CURR-07.
</success_criteria>

<output>
After completion, create `.planning/phases/00-series-scaffolding/00-04-SUMMARY.md` recording:
- Final H2 section count
- Tool versions pinned vs flexible
- Any tool/version that deviates from RESEARCH.md tool inventory and why
</output>
