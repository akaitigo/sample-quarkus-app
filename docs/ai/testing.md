# Testing Strategy

テスト方針と書き方の規約。`quarkus-kotlin-test` Skill から参照される。

---

## テストレベル

| レベル | 対象 | ツール | 場所 |
|---|---|---|---|
| **Service 単体** | 業務ロジック | JUnit 5 + MockK | `ProductServiceTest.kt` |
| **Repository 単体** | 永続化アクセス | JUnit 5 | `ProductRepositoryTest.kt` |
| **Resource 統合** | HTTP 境界 | QuarkusTest + REST Assured | `ProductResourceTest.kt` |

本講座では E2E は実装しない (Quarkus continuous testing で代替)。

---

## テスト命名規約

```kotlin
// 形式: <対象> <条件> <期待結果>
@Test
fun `updatePrice with negative value throws IllegalArgumentException`() { ... }

@Test
fun `findById returns null when product does not exist`() { ... }
```

バッククォート関数名で日本語も可。チームで統一すれば英語/日本語どちらでも。

---

## カバレッジ要件 (DoD)

振る舞いを変えたら以下を **必ず** カバー:

1. **正常系**: 期待通りの入力で期待通りの出力
2. **異常系**: 不正入力 / 存在しないリソース
3. **境界値**: 0 / 最大値 / null など

例: 価格更新 API なら:
- 正常系: 商品 ID 存在 + 正の価格 → 200 + 更新後のレスポンス
- 異常系 1: 価格負数 → 400
- 異常系 2: 商品 ID 不存在 → 404

---

## Service 層テスト (MockK 例)

```kotlin
@Test
fun `updatePrice with negative value throws IllegalArgumentException`() {
    val repository = mockk<ProductRepository>()
    val service = ProductService(repository)

    assertThrows<IllegalArgumentException> {
        service.updatePrice(1L, -100)
    }

    // Repository が呼ばれていないことも確認
    verify(exactly = 0) { repository.save(any()) }
}
```

---

## Resource 層テスト (REST Assured 例)

既存テスト (`ProductResourceTest.kt`) は **POST をインライン** で書いている（共有ヘルパは無い）。まずそれに倣い、重複が増えたらテストクラス内の `private` 関数（下記 `createProductReturnsId` 等）に括り出してよい。

```kotlin
@QuarkusTest
class ProductResourceTest {

    private fun createProductReturnsId(name: String, price: Int): Long =
        given()
            .contentType(ContentType.JSON)
            .body("""{"name": "$name", "price": $price}""")
            .`when`()
            .post("/products")
            .then()
            .statusCode(201)
            .extract()
            .response()
            .jsonPath()
            .getLong("id")

    @Test
    fun `PATCH price returns 200 with updated product`() {
        val productId = createProductReturnsId("Apple", 100)

        given()
            .contentType(ContentType.JSON)
            .body("""{"price": 150}""")
            .`when`()
            .patch("/products/$productId/price")
            .then()
            .statusCode(200)
            .body("price", equalTo(150))
    }

    @Test
    fun `PATCH price with negative value returns 400`() {
        val productId = createProductReturnsId("Apple", 100)

        given()
            .contentType(ContentType.JSON)
            .body("""{"price": -100}""")
            .`when`()
            .patch("/products/$productId/price")
            .then()
            .statusCode(400)
    }

    @Test
    fun `PATCH price on missing product returns 404`() {
        given()
            .contentType(ContentType.JSON)
            .body("""{"price": 150}""")
            .`when`()
            .patch("/products/99999/price")
            .then()
            .statusCode(404)
    }
}
```

ローカルプライベート関数として `createProductReturnsId` のような共有セットアップを置くのは OK (テストクラス内に閉じる)。`@TestMethodOrder` などで状態を共有しないこと。

---

## アサーション規約

- REST Assured: `body("field", equalTo(value))` 形式
- JUnit: `assertEquals(expected, actual)` 形式
- MockK: `verify { ... }` でモック呼び出し検証

`assertThat` (AssertJ) は本プロジェクトでは使わない (依存削減)。

---

## やってはいけないこと

- ❌ テストを削除して `./gradlew test` を緑にする
- ❌ `@Disabled` でスキップして放置 (理由を必ずコメントで明示し、復活時期を書く)
- ❌ Resource 層テストで Service をモックする (統合テストの意味が失われる)
- ❌ Service 層テストで Quarkus を起動する (重い)
- ❌ テスト名が `test1`, `test2` のように内容を表していない

---

## テスト追加時の手順 (Skill 連動)

1. 既存テストを Grep して命名・構造・assertion 形式を確認
2. 同じスタイルで追加
3. `./gradlew test` で確認
4. 失敗したら失敗ログを要約 (`quarkus-kotlin-test` Skill の Step 5 を参照)

---

## 関連

- アーキテクチャ規約: `architecture.md`
- レビュー観点: `code-review.md`
- 完了条件: `definition-of-done.md`
- テスト Skill: `.claude/skills/quarkus-kotlin-test/SKILL.md`
