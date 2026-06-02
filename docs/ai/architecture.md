# Architecture

商品マスタ管理 API のアーキテクチャ規約。AI エージェントが層構造と責務を守るために読む。

---

## レイヤ構造

```
┌─────────────────────┐
│ ProductResource     │  HTTP 境界
│  - Endpoint 定義     │  バリデーション入口のみ
│  - Service 呼び出し  │  業務ロジック禁止
└──────────┬──────────┘
           │
┌──────────▼──────────┐
│ ProductService      │  業務ロジック
│  - 価格負数チェック  │  例外スロー
│  - その他業務規則    │  Repository を組み立てる
└──────────┬──────────┘
           │
┌──────────▼──────────┐
│ ProductRepository   │  永続化アクセス
│  - 取得/保存/削除    │  業務ロジック禁止
│  - インメモリ実装    │  (本講座では)
└─────────────────────┘
```

---

## 各層の責務

### ProductResource (HTTP 境界)

**やること**:
- JAX-RS / Quarkus REST のエンドポイント定義 (`@Path`, `@GET`, `@POST`, `@PATCH`)
- リクエスト DTO のデシリアライズ
- レスポンス DTO のシリアライズ
- HTTP ステータスコード決定
- Service 呼び出し

**やってはいけないこと**:
- ❌ ビジネスルールの判定 (例: 0 円未満チェック)
- ❌ Repository への直接アクセス
- ❌ ドメインモデルをそのままレスポンスに返す (DTO 経由)

### ProductService (業務ロジック)

**やること**:
- 業務ルールの実装 (例: 価格は 0 円未満不可)
- 例外スロー (`IllegalArgumentException`, `NotFoundException` 等)
- Repository の組み立て (取得 → 加工 → 保存)
- DTO とドメインモデルの変換

**やってはいけないこと**:
- ❌ HTTP の概念を扱う (`Response`, `@Path` など)
- ❌ 永続化技術に依存 (SQL、ORM の詳細)

### ProductRepository (永続化アクセス)

**やること**:
- ドメインモデルの取得・保存・削除
- ID 採番 (本講座ではインメモリで `AtomicLong` 等)

**やってはいけないこと**:
- ❌ 業務ロジック (バリデーション、加工)
- ❌ HTTP / DTO の概念

### dto/ (リクエスト/レスポンス DTO)

**やること**:
- HTTP 入出力の形状を定義
- Resource 層でのバリデーションアノテーション (`@NotNull` 等)

**やってはいけないこと**:
- ❌ 業務ロジック
- ❌ 永続化マッピング

### Product (ドメインモデル)

**やること**:
- 業務上の概念を表現
- 不変量 (例: `id`, `name`, `price` がそろう)

**やってはいけないこと**:
- ❌ DTO として直接公開しない (互換性を担保するため DTO 経由)

---

## エラーハンドリング規約

- バリデーション失敗 (例: 0 円未満) → `IllegalArgumentException` を Service でスロー
- 存在しないリソース → `NotFoundException` (or `WebApplicationException(404)`)
- Resource 層で HTTP ステータスにマップ:
  - `IllegalArgumentException` → 400 Bad Request
  - `NotFoundException` → 404 Not Found
  - その他 → 500

または `@ServerExceptionMapper` で一元化。

---

## ディレクトリ規約

現状 (リポジトリ初期状態):

```
src/main/kotlin/com/example/products/
├── Product.kt                  ドメインモデル
├── ProductResource.kt          Resource 層
├── ProductService.kt           Service 層
├── ProductRepository.kt        Repository 層
└── dto/
    ├── ProductDto.kt           レスポンス DTO
    └── ProductCreateRequest.kt 商品登録リクエスト

src/test/kotlin/com/example/products/
├── ProductResourceTest.kt      Resource 層テスト (REST Assured)
├── ProductServiceTest.kt       Service 層単体テスト (MockK)
└── ProductRepositoryTest.kt    Repository 層テスト
```

ハンズオン完了後に追加されるもの (受講者が作成):

```
└── dto/
    └── PriceUpdateRequest.kt   価格更新リクエスト (新規)
```

---

## 新機能を追加する手順

例: 「商品価格更新 API」を追加する場合

1. `docs/ai/testing.md` を読んでテスト方針を確認
2. `dto/PriceUpdateRequest.kt` を新規作成 (リクエスト形状)
3. `ProductService.updatePrice(id, newPrice)` を追加 (業務ロジック)
4. `ProductRepository` に必要なら更新メソッド追加
5. `ProductResource.updatePrice()` エンドポイント追加 (HTTP 境界)
6. テスト追加 (正常系 / 0 円未満 / 404)
7. `./gradlew test` で確認

---

## 関連

- レビュー観点: `code-review.md`
- テスト方針: `testing.md`
- 完了条件: `definition-of-done.md`
