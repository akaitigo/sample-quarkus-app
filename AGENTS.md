# AGENTS.md

Universal agent brief for this repository. Read by Claude Code, Codex, Cursor, Aider, Copilot Agent, and any other Coding Agent that reads AGENTS.md.

Keep this file as a **pointer-type briefing (under 50 lines)**. Put details in `docs/ai/*.md` and reference them by name.

## Project Overview

This repository is a **Quarkus + Kotlin backend service for product master management**. Used as the teaching artifact for the Claude Code Education series, Session 2: "Quarkus + Kotlin で学ぶ AI Harness Engineering 入門".

## Tech Stack

- Kotlin 2.0+ / Quarkus 3.x / Gradle Kotlin DSL
- JUnit 5 + MockK + REST Assured
- JDK 17+

## Architecture Rules

- `ProductResource` handles **HTTP request/response only**. No business logic.
- `ProductService` contains **business logic**. Price validation lives here.
- `ProductRepository` handles **persistence access only**. In-memory in this repo.
- DTOs (`dto/*`) are separate from the domain model (`Product`).
- Do **not** put business logic in Resource classes.
- Do **not** access persistence directly from Resource classes.

## Commands

Use the Gradle wrapper.

```bash
./gradlew test           # run tests
./gradlew build          # full build
./gradlew quarkusDev     # dev mode (continuous testing)
```

## Implementation Rules

- Prefer constructor injection.
- Keep DTOs separate from domain models.
- Add or update tests for every behavior change.
- Do not change public API contracts without updating documentation.
- Negative-amount validation for prices belongs in `ProductService`, not `ProductResource`.

## Definition of Done (DoD)

See `docs/ai/definition-of-done.md`. Before finishing a task:

1. Explain files changed.
2. Run `./gradlew test` and report the result.
3. Review the diff for regressions.
4. Identify remaining risks.

## Required Reading (pointers)

- `CLAUDE.md` — Claude Code specific addendum.
- `docs/ai/architecture.md` — Layer structure and responsibilities.
- `docs/ai/code-review.md` — Review checklist.
- `docs/ai/testing.md` — Testing strategy and conventions.
- `docs/ai/definition-of-done.md` — Completion criteria.
- `.claude/skills/` (Claude) or `.agents/skills/` (Codex) — Reusable workflows: feature / test / review.

## Agent Behavior

- Read `docs/ai/architecture.md` first when touching code.
- For non-trivial changes, enter **plan mode** and write a plan before editing.
- Prefer reading existing tests before implementing new behavior.
- Never invent APIs you have not seen. Grep for examples first.

## What NOT to do

- Do not tutorialize this file. Keep it as pointers.
- Do not bypass `./gradlew test` with shortcuts.
- Do not delete tests to make builds pass.
- Do not modify protected files without asking the human (the PreToolUse hook will block these anyway):
  - `build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`
  - `.claude/hooks/*`, `.claude/settings.json`
  - `AGENTS.md`, `CLAUDE.md` themselves
