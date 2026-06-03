---
name: quarkus-kotlin-test
description: Use this when adding, updating, or diagnosing tests in a Quarkus + Kotlin backend service. Covers identifying behavior, matching existing style, adding cases, running tests, and diagnosing failures.
---

# Quarkus + Kotlin Test Assistance

Reusable workflow for test-related tasks. Use this skill when:

- Adding tests for a new behavior
- Updating tests after a refactor
- Diagnosing why an existing test fails

## Step 1: Identify the behavior under test

Before writing or fixing tests, clarify:

- **Normal path**: happy case input → expected output
- **Validation error**: invalid input → expected error response (400 / exception)
- **Not found error**: missing resource → expected 404 / exception
- **Boundary conditions**: edge values (0, max, null, empty string)

If behavior is unclear, read `docs/ai/architecture.md` and the related Service / Resource code.

## Step 2: Locate existing test style

**Do not invent a test style**. Read nearby tests first:

```bash
# Find similar tests for the layer
ls src/test/kotlin/com/example/products/
```

Note:
- Naming convention (e.g., backtick descriptions: `` `updatePrice with negative value throws IllegalArgumentException` ``)
- Setup / teardown approach
- Assertion style (REST Assured `body("field", equalTo(value))` for Resource tests; `assertEquals` / `assertThrows` for Service)
- Mocking approach (MockK in Service tests, real Quarkus startup in Resource tests via `@QuarkusTest`)

Match this style. Do not introduce AssertJ, Kotest, or other libraries not already in use.

## Step 3: Add or update tests

### Service-layer tests (MockK)

```kotlin
class ProductServiceTest {

    private val repository = mockk<ProductRepository>()
    private val service = ProductService(repository)

    @Test
    fun `updatePrice with negative value throws IllegalArgumentException`() {
        assertThrows<IllegalArgumentException> {
            service.updatePrice(1L, -100)
        }
        verify(exactly = 0) { repository.save(any()) }
    }
}
```

### Resource-layer tests (QuarkusTest + REST Assured)

Match the existing inline POST style in `ProductResourceTest.kt` (the existing tests inline the POST and extract the id; no shared helper yet). Inline it, or extract a class-local `private` helper if it reduces duplication.

```kotlin
@QuarkusTest
class ProductResourceTest {

    @Test
    fun `PATCH price returns 200 with updated product`() {
        val createResponse = given()
            .contentType(ContentType.JSON)
            .body("""{"name": "Apple", "price": 100}""")
            .`when`()
            .post("/products")
            .then()
            .statusCode(201)
            .extract()
            .response()
        val productId = createResponse.jsonPath().getLong("id")

        given()
            .contentType(ContentType.JSON)
            .body("""{"price": 150}""")
            .`when`()
            .patch("/products/$productId/price")
            .then()
            .statusCode(200)
            .body("price", equalTo(150))
    }
}
```

Prefer tests that verify **behavior** rather than implementation details. Don't assert on internal method call counts unless the count itself is the behavior (e.g., "save called exactly once").

## Step 4: Run tests

Run narrowly first if possible:

```bash
./gradlew test --tests "com.example.products.ProductServiceTest"
./gradlew test --tests "com.example.products.ProductResourceTest.PATCH price*"
```

Then run the full suite:

```bash
./gradlew test
```

## Step 5: Diagnose failures

When tests fail, report (do **not** paste the full log):

```
## Failing test
<test class>::<test method>

## Expected
<expected behavior or value>

## Actual
<observed behavior or value>

## Likely cause
<your hypothesis — 1〜2 sentences>

## Suggested fix
<one paragraph or code snippet>
```

If you cannot determine the cause:
1. Re-read the failure stack trace
2. Read the implementation under test
3. If still unclear, ask the human — do not guess

---

## Anti-patterns (don't do this)

- ❌ Disabling a failing test with `@Disabled` to "fix it later" without a written reason and revival date
- ❌ Deleting tests to make the build green
- ❌ Mocking a Service in a Resource-layer test (defeats the integration test purpose)
- ❌ Starting Quarkus in a Service-layer test (too slow)
- ❌ Tests named `test1`, `test2` (must describe behavior)
- ❌ Asserting on log output instead of return values

---

## References

- Testing strategy: `docs/ai/testing.md`
- Architecture: `docs/ai/architecture.md`
- DoD: `docs/ai/definition-of-done.md`
