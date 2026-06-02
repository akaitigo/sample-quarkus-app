#!/bin/bash
# PostToolUse hook: auto-format Kotlin files after edit.
# Phase 4 example (see docs/course/adoption-checklist.md).
#
# Behavior:
# - If ktlint CLI is installed → format the touched file
# - If not installed → silently exit (soft-fail; participants can add it later)

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

# Only handle Kotlin files
if [[ "$FILE_PATH" != *.kt ]]; then
    exit 0
fi

# Use ktlint CLI if installed; otherwise soft-fail.
# Install via: brew install ktlint (macOS) or see https://ktlint.github.io/
if command -v ktlint >/dev/null 2>&1; then
    if ktlint --format "$FILE_PATH" >/dev/null 2>&1; then
        echo "[Harness] ktlint formatted: $FILE_PATH"
    else
        echo "[Harness] ktlint reported issues in $FILE_PATH (auto-fix where possible)."
    fi
else
    # ktlint not installed — Phase 4 territory.
    # See docs/course/adoption-checklist.md to enable formatting.
    echo "[Harness] ktlint not installed — skipping format (install via 'brew install ktlint' to enable Phase 4 auto-format)"
    exit 0
fi
