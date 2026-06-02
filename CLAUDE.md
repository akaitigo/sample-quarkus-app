# CLAUDE.md

Claude Code specific addendum. Universal rules live in `AGENTS.md`, imported below.

Keep this file thin (under 30 lines). Don't duplicate AGENTS.md — extend it.

## Import universal brief

@AGENTS.md

## Claude Code specific rules

- Use **plan mode** before any non-trivial change (Shift+Tab, or `defaultMode: "plan"` in `.claude/settings.json` enforces this).
- Ask before modifying `build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`, `.claude/settings.json`, anything under `.claude/hooks/`, or `AGENTS.md` / `CLAUDE.md` themselves.
- Prefer reading existing tests before implementing new behavior.
- When test output is large, summarize only failing tests and likely causes — do not paste the entire log.

## Subagents (use to keep main context clean)

- `@agent-codebase-investigator` — read-only exploration (Read/Grep/Glob, Haiku)
- `@agent-test-failure-analyzer` — diagnose `./gradlew test` failures without dumping the full log
- `@agent-reviewer` — pre-PR diff review against `docs/ai/code-review.md`

## Model defaults

- Read-only exploration: Haiku (`@agent-codebase-investigator`)
- Implementation: Sonnet
- Heavy design decisions: Opus (rare)

## Hooks behavior

PostToolUse hooks (`.claude/hooks/post-tool-use-format.sh`) run after file edits. Their output appears in your context — read it and self-correct without being asked.

PreToolUse hook (`.claude/hooks/pre-tool-use-protect.sh`) blocks edits to protected files: `build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`, `.claude/hooks/*`, `.claude/settings.json`, `AGENTS.md`, `CLAUDE.md`. If you need to change one, ask the human.

## What NOT to do

- Don't add tutorial content to this file. Pointer-type only.
- Don't skip `./gradlew test`.
- Don't guess API signatures — grep for usage first.
