# CLAUDE.md

Guidance for Claude Code when working in this repository.

> 🔴 **This project is governed by the AG Kit system in `.agents/`.** Its rules, agents, skills and
> workflows are **binding**, not optional reference material. Read them and comply.

---

## 1. Session Bootstrap (do this first)

At the start of every session, in this order:

| # | Action | File |
|---|--------|------|
| 1 | Load the highest-priority protocol | `.agents/rules/core-protocol.md` |
| 2 | Load always-on universal rules | `.agents/rules/universal-rules.md` |
| 3 | Load always-on routing rules | `.agents/rules/request-routing.md` |
| 4 | Load persistent memory | `.agents/memory/MEMORY.md` (+ the files it points to) |

**On demand only** (do NOT preload every session):

- `.agents/rules/code-rules.md` — when writing/refactoring/fixing code
- `.agents/rules/design-rules.md` — when touching UI files
- `.agents/rules/quick-reference.md` — fast index of agents/skills/scripts
- `.agents/ARCHITECTURE.md` — full catalog (20 agents / 47 skills / 13 workflows)

---

## 2. Rule Priority

```
P0  .agents/rules/*          (workspace rules — highest)
P1  .agents/agent/*.md       (active specialist agent)
P2  .agents/skills/*/SKILL.md (loaded skills)
P3  this CLAUDE.md           (project context)
```

All levels are binding. On conflict, the lower number wins.

---

## 3. Mandatory Protocol Per Request

**Every request** — before any tool use or implementation:

1. **Classify** the request (QUESTION / SURVEY / SIMPLE CODE / COMPLEX CODE / NEW APP / DESIGN / SLASH CMD)
   per the table in `request-routing.md`.
2. **Route to an agent** — read `.agents/agent/{agent}.md`, then announce:
   `🤖 **Applying knowledge of @[agent-name]...**`
3. **Load skills** listed in that agent's frontmatter — read `SKILL.md` (index) first, then **only** the
   sections relevant to the request. Announce them:
   `📚 **Using skill: @[skill-name]...**`
4. **Pass the Socratic Gate** (`code-rules.md`) — new feature = min. 3 strategic questions; bug fix =
   confirm understanding + impact; even an explicit "proceed" requires 2 edge-case questions.
5. **Read → Understand WHY → Apply principles → Code.** Never read an agent file and jump straight to code.

❌ Writing code without identifying an agent = protocol violation.
❌ Applying a skill silently = the user cannot verify which knowledge was used.

---

## 4. Agent Routing For This Project

This is an **Android / Kotlin / Jetpack Compose** app → it is a **MOBILE** project.

| Domain | Agent | Skills |
|--------|-------|--------|
| App code + UI (default here) | `mobile-developer` | `mobile-design` |
| Gemini API / data layer / networking | `backend-specialist` | `api-patterns` |
| Bug investigation | `debugger` | `systematic-debugging` |
| Multi-domain / large feature | `orchestrator` or `project-planner` | per agent frontmatter |

> 🔴 **`frontend-specialist` is WRONG for this repo.** It is for web. Mobile UI → `mobile-developer` only.

---

## 5. UI Work Gate

Before writing or editing **any** Compose UI, a `DESIGN.md` must exist at the project root.

- It does **not** exist yet → create it first (tokens + rationale) following `.agents/skills/design-spec/SKILL.md`.
- Once it exists → read it and build strictly against its tokens; keep it in sync with visual changes.
- Only a genuinely trivial tweak to existing UI may skip this. Net-new UI never skips it.

Agent rules that apply to all UI here (in `.agents/agent/mobile-developer.md`): Purple Ban, Template Ban,
anti-cliché rules, Deep Design Thinking.

---

## 6. Workflows (slash commands)

Procedures live in `.agents/workflows/`. Read the matching file when the user invokes one:

| Command | Purpose |
|---------|---------|
| `/create` | New application — routes through `project-planner` + `app-builder` |
| `/plan` | Plan file only, no code |
| `/brainstorm` | Structured option exploration before implementation |
| `/orchestrate`, `/coordinate` | Multi-agent dispatch and synthesis |
| `/enhance` | Add/update a feature in the existing app |
| `/debug` | Systematic root-cause investigation |
| `/test` | Generate and run tests |
| `/verify` | Prove a change works by executing it |
| `/deploy` | Pre-flight checks + release |
| `/preview` | Local dev server start/stop/status |
| `/status` | Agent + project status board |
| `/remember` | Persist a convention/decision to `.agents/memory/` |

---

## 7. Validation Scripts

Run from the project root:

```bash
python .agents/scripts/checklist.py .        # priority-based audit
python .agents/scripts/verify_all.py         # verification suite
python .agents/skills/<skill>/scripts/<script>.py
```

Priority order: **Security → Lint → Schema → Tests → UX → SEO → Lighthouse/E2E.**
A task is not finished until `checklist.py` succeeds; fix Critical blockers (Security/Lint) first.

> Note: several scripts target web stacks. For this Android project the relevant ones are
> `mobile_audit.py`, `security_scan.py`, `lint_runner.py`, `test_runner.py` — skip the ones that
> do not apply and say so rather than reporting a false pass.

---

## 8. Project Context

- **App:** AI Insect Identifier Pro — Android, Kotlin, Jetpack Compose, Gradle KTS.
- **Package:** `com.kynv1.aiinsectidentifierpro` (`app/src/main/java/...`).
- **Stack notes:** Gemini API (`data/remote/GeminiConfig.kt`), Firebase (FCM, Crashlytics, Analytics).
- **Build:** `./gradlew assembleDebug` — never commit `local.properties` or API keys.
- **Root-level `*.md` files** (`home-screen.md`, `watch-ads.md`, `scan-bg-redesign.md`, `insect-id-clone.md`,
  the Firebase guides, …) are **feature specs / task plans**. Read the matching one before touching that feature.

### Git conventions (from `.agents/memory/project-conventions.md`)

- Always create a dedicated branch for major changes: `feature/[task-slug]` or `fix/[bug-slug]`.
- Commit/push only when the user asks.

### Communication

- The user writes in **Vietnamese** → **respond in Vietnamese**.
- Code, identifiers, and code comments stay in **English**.

---

## 9. Clean Code (global, non-negotiable)

All code follows `.agents/skills/clean-code/SKILL.md`: concise and direct, no over-engineering,
self-documenting, tests mandatory (Unit > Integration > E2E, AAA pattern), measure before optimizing,
verify secret handling.
