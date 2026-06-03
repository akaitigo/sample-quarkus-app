#!/bin/bash
# PreToolUse hook: block edits to protected files.
# See docs/course/adoption-checklist.md (Phase 4) for the rationale.

set -uo pipefail

INPUT=$(cat)

# Soft-fail if jq is not installed (don't break the workflow)
if ! command -v jq >/dev/null 2>&1; then
    exit 0
fi

FILE_PATH=$(echo "$INPUT" | jq -r '.tool_input.file_path // empty')

if [[ -z "$FILE_PATH" ]]; then
    exit 0
fi

# Protected files: build config, hook scripts, agent briefs.
# These define the harness itself — humans should modify them, not the agent.
PROTECTED=(
    "build.gradle.kts"
    "settings.gradle.kts"
    "gradle.properties"
    ".claude/hooks/"
    ".claude/settings.json"
    "AGENTS.md"
    "CLAUDE.md"
)

# 注: 教材用の簡易判定（部分一致）。派生名(例: NOTES_AGENTS.md)も巻き込み得る。
# 本番では basename 一致や末尾一致 (*/AGENTS.md) に厳密化するとよい。
for p in "${PROTECTED[@]}"; do
    if [[ "$FILE_PATH" == *"$p"* ]]; then
        echo "[Harness] BLOCKED: $FILE_PATH is a protected harness file." >&2
        echo "Ask the human to make this change. See AGENTS.md 'What NOT to do'." >&2
        exit 2  # exit 2 = block the operation (Claude Code hook protocol)
    fi
done

exit 0
