---
name: quarkus-kotlin-review
description: Use this before creating a pull request for a Quarkus + Kotlin backend change. Reviews architecture compliance, test coverage, safety, and code style, then produces a structured review report.
---

# Quarkus + Kotlin Pre-PR Review

Reusable workflow for self-reviewing or peer-reviewing a diff before opening a PR.

## Step 1: Review architecture

Open the diff and check, using `docs/ai/architecture.md`:

- [ ] Resource classes contain only HTTP boundary code (no business logic)
- [ ] Service classes contain business rules
- [ ] Repository classes contain only persistence access
- [ ] DTOs are separate from domain models
- [ ] No layer-skipping (Resource → Repository directly is forbidden)

Common violations:
- Validation in Resource layer (should be in Service)
- Domain model returned directly from Resource (should be DTO)
- HTTP exceptions thrown from Service (should be domain exceptions, mapped to HTTP in Resource)

## Step 2: Review API contracts

- [ ] HTTP method matches intent (PATCH for partial update, PUT for full replace, POST for create)
- [ ] Status codes are appropriate (200 / 201 / 400 / 404 / 500)
- [ ] Response shape uses DTO, not domain model
- [ ] No accidental breaking changes to existing endpoints

## Step 3: Review tests

Per `docs/ai/testing.md`:

- [ ] Every behavior change has a corresponding test
- [ ] Normal, error, and boundary cases are covered
- [ ] Test names describe the behavior under test
- [ ] No `@Disabled` tests added without a reason and revival plan
- [ ] No tests deleted to make the build green
- [ ] `./gradlew test` is green

## Step 4: Review safety

- [ ] `build.gradle.kts` / `settings.gradle.kts` not modified (unless explicitly needed and explained)
- [ ] Persistence schema / migrations not modified (this repo is in-memory, so this should always be clean)
- [ ] No new dependencies added casually
- [ ] No public API contracts broken without documentation update
- [ ] No tests removed

## Step 5: Review code style

- [ ] ktlint was run (PostToolUse Hook handles this automatically when editing `.kt` files; manual: `ktlint --format <file>`)
- [ ] Imports are clean (no unused, no wildcard unless project convention)
- [ ] `val` used by default; `var` only when mutation is required
- [ ] Null safety considered (`?.`, `?:`, no unnecessary `!!`)
- [ ] Function / class names follow Kotlin conventions (camelCase / PascalCase)

## Step 6: Produce review report

Output in this exact format:

```
## サマリ
<1-3 行で全体評価>

## 良かった点
- <observation 1>
- <observation 2>

## 修正必須 (Must fix)
- [ ] <issue 1>: <location and suggestion>
- [ ] <issue 2>: ...

## 推奨 (Should consider)
- <suggestion 1>
- <suggestion 2>

## 不足しているテスト (Missing tests)
- <case 1, if any>
- <case 2, if any>

## 残るリスク (Risks)
- <risk 1, if any>
- 「なし」と書ける場合のみそう書く
```

---

## Anti-patterns (don't do this)

- ❌ Saying "LGTM" without checking each item
- ❌ Approving when tests are not green
- ❌ Approving when `build.gradle.kts` changes are unexplained
- ❌ Skipping the structured report and just writing prose
- ❌ Pasting the full diff into the report (refer to it, don't duplicate it)

---

## References

- Architecture: `docs/ai/architecture.md`
- Code review checklist: `docs/ai/code-review.md`
- Testing: `docs/ai/testing.md`
- DoD: `docs/ai/definition-of-done.md`
