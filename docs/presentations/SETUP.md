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

Cada sección asume una máquina limpia y nombra los 3 sistemas operativos soportados.
Si una herramienta falla, los comandos de troubleshooting están al final.

## 1. Docker + Docker Compose v2

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

## 2. Levantar el stack DotaChile (Payara 5 + Postgres 15)

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

Las imágenes de `docker-compose.yml` y `Dockerfile` están **pinneadas por digest
`@sha256:`** para garantizar que todos los participantes bajan los mismos bytes.
El procedimiento de re-pin mensual está documentado más abajo en la sección
"Cadencia de actualización de pins de Docker" (ver también plan 00-01 en
`.planning/phases/00-series-scaffolding/00-01-docker-pinning-PLAN.md`).

## 3. Node.js 20+ (para Marp CLI + Mermaid CLI)

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

## 4. Claude Code CLI

Instalá el CLI oficial siguiendo las instrucciones de
[docs.claude.com](https://docs.claude.com) (scope usuario, no sistema).

Este repo asume:
- El directorio hermano `../dotachile-emails/corpus` se auto-monta vía
  `.claude/settings.json` (`additionalDirectories`).
- `.claude/skills/`, `.claude/commands/`, `.claude/hooks/` se cargan desde el
  repo automáticamente cuando corrés `claude` en la raíz.

## 5. Marp CLI + Mermaid CLI (pineados)

Ambos corren vía `npx` — no se instalan globalmente.

```bash
npx @marp-team/marp-cli@4.3.1 --version      # verifica instalación
npx @mermaid-js/mermaid-cli@11.12.0 --version
```

Convenciones de render (ver `docs/presentations/CLAUDE.md`):
- `.md` del deck se renderiza a `.html` con `--html`.
- `.mmd` de Mermaid se renderiza a `.svg` con `-b transparent`.
- Commitear siempre **fuente + salida** juntos (render-on-commit rule).

## 6. Ollama + modelos

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

## 7. `tools/email-rag/` — corpus de emails (opcional)

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

## 8. asciinema + VHS (opcional — para fallback recordings de QUAL-02)

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

## 9. Pre-pull de imágenes Docker (antes de cada sesión)

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
