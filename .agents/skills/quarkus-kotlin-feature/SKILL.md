---
name: quarkus-kotlin-feature
description: Use this when implementing or modifying a feature in a Quarkus + Kotlin backend service. Walks through understanding existing design, proposing a plan, implementing safely, testing, and reporting.
---

# Quarkus + Kotlin Feature Implementation

A reusable workflow for adding or modifying behavior in this codebase. Follow each step in order — do not skip ahead.

## Step 1: Understand existing design

Before writing any code:

1. **Identify the target layer** (Resource / Service / Repository / DTO) using `docs/ai/architecture.md`
2. **Locate relevant files** with Grep — do not assume paths:
   ```
   grep -r "class ProductResource" src/
   grep -r "class ProductService" src/
   grep -r "class ProductRepository" src/
   ```
3. **Read existing tests** for the touched layer to learn the testing style:
   - Resource tests: `src/test/kotlin/com/example/products/ProductResourceTest.kt`
   - Service tests: `ProductServiceTest.kt`
4. **Do not assume architecture** — infer from existing code. If something is unclear, ask the human.

## Step 2: Propose a plan

Before editing, output a written plan covering:

- **Target behavior**: what the new endpoint / method does (in 1〜2 sentences)
- **Acceptance criteria**: bullet list (e.g., "negative price → 400", "missing id → 404")
- **Files to change**: explicit list
- **Test strategy**: which test files, which cases (normal / error / boundary)
- **Risks**: anything that could break or that you are unsure about

If `defaultMode: "plan"` is enabled in `.claude/settings.json`, you will be in plan mode automatically. Wait for human approval before implementing.

## Step 3: Implement

Follow the architecture rules from `docs/ai/architecture.md`:

- **Resource**: HTTP boundary only. No business logic. Catch exceptions and map to HTTP status codes (or use `@ServerExceptionMapper`).
- **Service**: Business rules. Validate domain invariants. Throw typed exceptions (`IllegalArgumentException`, `NotFoundException`).
- **Repository**: Persistence access only. No business rules.
- **DTO**: Separate from domain model. Use `dto/` subpackage.

Example for "update product price" — match the existing `create()` flow (see `ProductResource.kt:31-40` and `ProductService.kt:14-19`):

```kotlin
// dto/PriceUpdateRequest.kt
data class PriceUpdateRequest(val price: Int)

// ProductService.kt — business logic
//   Note: Service must not throw HTTP-level exceptions. Return null for "not found".
fun updatePrice(id: Long, newPrice: Int): Product? {
    require(newPrice >= 0) { "Price must be non-negative" }
    val product = repository.findById(id) ?: return null
    return repository.save(product.copy(price = newPrice))
}

// ProductResource.kt — HTTP boundary
//   Mirror create(): try/catch IllegalArgumentException → 400, null → 404.
@PATCH
@Path("/{id}/price")
fun updatePrice(@PathParam("id") id: Long, request: PriceUpdateRequest): Response {
    return try {
        val updated = service.updatePrice(id, request.price)
            ?: return Response.status(Response.Status.NOT_FOUND).build()
        Response.ok(updated.toDto()).build()
    } catch (e: IllegalArgumentException) {
        Response.status(Response.Status.BAD_REQUEST)
            .entity(mapOf("error" to (e.message ?: "Invalid request")))
            .build()
    }
}
```

Do **not** put `require(newPrice >= 0)` in `ProductResource`. That is a business rule and belongs in the Service.
Do **not** throw HTTP exceptions (`NotFoundException`, etc.) from `ProductService` — that violates `docs/ai/architecture.md` "Service must not depend on HTTP concepts". Return `null` and let the Resource map to 404.

## Step 4: Test

Add tests covering normal / error / boundary cases. Follow `docs/ai/testing.md` for style.

Run tests narrowly first if possible:

```bash
./gradlew test --tests "com.example.products.ProductServiceTest"
```

Then run the full suite:

```bash
./gradlew test
```

If tests fail:
- Read the failure message
- Locate the failing assertion
- Fix the code (not the test) unless the test was wrong
- Re-run

Never delete a failing test to make the build green.

## Step 5: Report

Use the `docs/ai/definition-of-done.md` report format:

```
## 変更ファイル
- <path>: <one-line description>

## 実装した振る舞い
- <acceptance criterion 1>: ✅
- <acceptance criterion 2>: ✅

## 実行したテスト
./gradlew test
→ BUILD SUCCESSFUL — N tests passed

## 残るリスク
- <list anything you didn't address, or "なし">

## 関連ドキュメント更新
- <files updated, or omit this section if none>
```

---

## Anti-patterns (don't do this)

- ❌ Putting validation in Resource layer
- ❌ Returning domain model directly (must go through DTO)
- ❌ Inventing API signatures you haven't seen — grep for examples
- ❌ Modifying `build.gradle.kts` to add convenience dependencies
- ❌ Skipping the plan step for "small" changes

---

## References

- Architecture: `docs/ai/architecture.md`
- Code review checklist: `docs/ai/code-review.md`
- Testing strategy: `docs/ai/testing.md`
- DoD: `docs/ai/definition-of-done.md`
