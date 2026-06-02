# ハンズオン手順 — 商品価格更新 API を追加する

第 2 回ワークショップの演習手順。

> 📌 **位置づけ**: この API 実装は、当日 §6 で **講師がライブデモ** として見せます。**各自が自分の手で実装するのは課後の自習** です（下記 Path B）。当日 90 分で全員がフル実装する時間枠は取っていません — 当日のハンズオンは §4（AGENTS.md 1 行）・§5（Skill 改造）・§6 末（ミスを潰す 1 行）の小さな 3 つ。本書は「家に帰ってからハーネスの効果を自分の手で確かめる」ためのものです。

---

## 演習テーマ

```
商品価格を更新する API を追加してください。
```

---

## 要件 (DoD = Definition of Done)

完了とみなすには以下をすべて満たす:

1. **商品 ID を指定して価格を更新できる** (`PATCH /products/{id}/price` 等)
2. **価格は 0 円未満にできない** (バリデーション)
3. **存在しない商品 ID の場合は 404 を返す**
4. **正常時は更新後の商品情報を返す**
5. **テストを追加** (正常系 / 0 円未満 / 404 をカバー)
6. `./gradlew test` が緑
7. アーキテクチャ規約に従う (Resource にロジック禁止、Service に業務ロジック)

> ⚠️ **既知の落とし穴**: `PriceUpdateRequest(val price: Int)` のような **単一プロパティの data class** は `jackson-module-kotlin` が委譲コンストラクタと誤解し、`{"price":150}` が 400 になります（正常系・404 テストが落ちる）。`@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)` + `@JsonProperty("price")` で回避。詳細は `docs/ai/architecture.md`「既知の落とし穴」。`quarkus-kotlin-feature` Skill の DTO 例はこの対処込みです。

---

## 2 つのパスでやる

### Path A: ハーネスなし (比較用・参考)

> ⚠️ 注意: これは比較のためのもので、**実用は推奨しません**。

#### 準備

リポジトリのコピーを作って、Harness 要素を一時削除:

```bash
cp -r sample-quarkus-app sample-quarkus-app-no-harness
cd sample-quarkus-app-no-harness
rm AGENTS.md CLAUDE.md
rm -rf .claude/ .agents/ docs/ai/
```

#### 依頼

新規 Claude Code セッションで:

```
商品価格更新APIを追加して。
```

#### 観察ポイント

- 既存設計を **読んだか / 推測か**
- Resource / Service / Repository の **責務を守ったか**
- テストを **書いたか / 雑か**
- 例外処理が **既存流儀に合っているか**
- `./gradlew test` を **実行したか**
- 完了報告に **テスト結果・リスクが含まれるか**

→ 多くの場合、Resource にロジック流入、テスト雑、報告は「できました」。

---

### Path B: ハーネスあり (本筋)

#### 準備

オリジナルの `sample-quarkus-app/` でそのまま作業:

```bash
cd sample-quarkus-app
```

`AGENTS.md`, `CLAUDE.md`, `.claude/skills/`, `.agents/skills/`, `docs/ai/*` が揃っていることを確認。

#### 依頼

新規 Claude Code セッションで:

```
quarkus-kotlin-feature skill を使って、商品価格更新 API を追加してください。完了前にテスト結果とリスクを報告してください。
```

#### Skill 経由の振る舞い (期待)

`quarkus-kotlin-feature` Skill が以下を強制:

1. **Step 1: Understand existing design**
   - Resource / Service / Repository / DTO / テストを読む
2. **Step 2: Propose plan**
   - 対象動作・変更ファイル・テスト戦略・リスクをまとめる
3. **Step 3: Implement**
   - Resource は HTTP 境界のみ
   - Service に 0 円未満チェック
   - Repository は永続化のみ
4. **Step 4: Test**
   - `./gradlew test` を実行
   - 通れば次へ、失敗したら修正
5. **Step 5: Report**
   - 変更ファイル / 実装した振る舞い / 実行したテスト / 残るリスク

#### 観察ポイント

- AGENTS.md / `docs/ai/architecture.md` を **読みに行ったか**
- plan mode で **書面計画を出したか**
- Service に **0 円未満チェックが入ったか**
- Resource が **HTTP 境界に留まったか**
- テストが **3 ケース (正常 / 0 円未満 / 404) 揃ったか**
- 完了報告に **テスト結果・リスクが含まれたか**

---

## 比較観点表

ハンズオン後に振り返るための観点:

| 観点 | ハーネスなし | ハーネスあり |
|---|---|---|
| 既存設計の調査 | 推測 | AGENTS.md / docs/ai 経由 |
| 設計判断 | Resource にロジック | Service に業務ロジック |
| バリデーション場所 | Resource か Service か曖昧 | Service (DoD 通り) |
| テスト | 抜けやすい / 雑 | 3 ケース揃う |
| 完了報告 | 「できました」 | 変更点・テスト結果・リスク |
| 再現性 (他の人に頼む) | バラつく | 揃う |

---

## 期待される最終状態 (Path B の場合)

### 変更ファイル

```
src/main/kotlin/com/example/products/ProductResource.kt          (新エンドポイント追加)
src/main/kotlin/com/example/products/ProductService.kt           (updatePrice メソッド + 0 円未満チェック)
src/main/kotlin/com/example/products/dto/PriceUpdateRequest.kt   (新規)
src/test/kotlin/com/example/products/ProductResourceTest.kt     (テスト追加)
src/test/kotlin/com/example/products/ProductServiceTest.kt      (テスト追加)
```

### 完了報告の例

```
## 変更ファイル
- ProductResource.kt: PATCH /products/{id}/price を追加
- ProductService.kt: updatePrice(id, newPrice) — 0 円未満で IllegalArgumentException、id 不存在で null
- dto/PriceUpdateRequest.kt: 新規 (price: Int)
- ProductResourceTest.kt: 正常系 (200) / 0 円未満 (400) / 404 を追加
- ProductServiceTest.kt: updatePrice 単体 (正常 / 0 円未満 / id 不存在) を追加

## 実装した振る舞い
- 商品 ID を指定して価格を更新できる: ✅ (PATCH /products/{id}/price → 200 + 更新後の商品)
- 価格は 0 円未満にできない: ✅ (Service で require、Resource で 400 にマップ)
- 存在しない商品 ID は 404: ✅ (Service が null を返し、Resource で 404 にマップ)
- 正常時は更新後の商品情報を返す: ✅
- テストを追加: ✅ (3 ケース)

## 実行したテスト
./gradlew test
→ BUILD SUCCESSFUL — 全 N テスト緑

## 残るリスク
- 価格 0 円「ちょうど」を許容するかは未確認 (現状は許容)
- 同時更新時の競合制御は未実装 (Repository インメモリのため割愛)
```

---

## ハンズオン Tips

### 詰まったとき

1. `AGENTS.md` を読み直す
2. `docs/ai/architecture.md` で層構造を確認
3. `docs/ai/testing.md` でテスト書き方を確認
4. `docs/ai/code-review.md` でレビュー観点を確認
5. それでも詰まったら Meet チャットへ

### Claude Code が Skill を呼ばない場合

明示的に指示:

```
.claude/skills/quarkus-kotlin-feature/SKILL.md を読んで、その手順に従ってください。
```

### Hook が邪魔になる場合

```bash
# 一時的に無効化
mv .claude/hooks/post-tool-use-format.sh .claude/hooks/post-tool-use-format.sh.bak
# あとで戻す
mv .claude/hooks/post-tool-use-format.sh.bak .claude/hooks/post-tool-use-format.sh
```

---

## 振り返り

ハンズオン終了後、以下を 1 行ずつ Meet チャットに貼ってください:

```
✅ うまくいったこと:
❌ うまくいかなかったこと:
💡 自社リポジトリに持ち帰れる発見:
```

これが第 3 回のインプットになります。

---

## 関連ファイル

- 比較デモの講師手順: `no-harness-vs-harness-demo.md`
- 自社導入: `adoption-checklist.md`
- 講師ガイド: `instructor-guide-90min.md` の §6
