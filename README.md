# sample-quarkus-app

Claude Code 教育 第 2 回「**Quarkus + Kotlin で学ぶ AI Harness Engineering 入門**」の教材リポジトリ。

商品マスタ管理 API (Quarkus + Kotlin) を題材に、AI エージェントが安全・一貫・検証可能に開発できるリポジトリ設計 = **Harness Engineering** を学ぶ。

---

## 中心メッセージ

> AI 活用 = AI が作業できる **文脈・制約・検証・再利用手順** をリポジトリに埋め込むこと。

---

## 受講者向け

まずこの順で読んでください:

1. [`docs/course/participant-guide.md`](docs/course/participant-guide.md) — 事前準備と当日の進め方
2. [`docs/course/syllabus.md`](docs/course/syllabus.md) — 講座全体像
3. [`docs/course/hands-on.md`](docs/course/hands-on.md) — 演習手順 (当日参照)

### クイックスタート

```bash
# JDK 17 または 21 (LTS) 推奨。build.gradle.kts は Java 17 ターゲット
java -version

# ビルド
./gradlew build

# テスト (全 N テスト緑になること)
./gradlew test

# 開発モード
./gradlew quarkusDev
```

### 起動後の動作確認

```bash
curl http://localhost:8080/products
# → []  (初期は空)

curl -X POST http://localhost:8080/products \
  -H 'Content-Type: application/json' \
  -d '{"name": "Apple", "price": 100}'
# → {"id":1,"name":"Apple","price":100}

curl http://localhost:8080/products
# → [{"id":1,"name":"Apple","price":100}]
```

---

## 講師向け

- [`docs/course/instructor-guide-90min.md`](docs/course/instructor-guide-90min.md) — 90 分版講師ガイド (台本付き)
- [`docs/course/no-harness-vs-harness-demo.md`](docs/course/no-harness-vs-harness-demo.md) — 比較デモ手順

---

## リポジトリ構成

```
.
├── AGENTS.md                          全エージェント共通の文脈ブリーフ
├── CLAUDE.md                          Claude Code 専用の追加 (@AGENTS.md import)
├── docs/
│   ├── course/                        ← 講座資料 (受講者・講師が読む)
│   │   ├── syllabus.md
│   │   ├── instructor-guide-90min.md
│   │   ├── participant-guide.md
│   │   ├── hands-on.md
│   │   ├── no-harness-vs-harness-demo.md
│   │   └── adoption-checklist.md
│   └── ai/                            ← AI 向け詳細ドキュメント (AGENTS.md からポインタ)
│       ├── architecture.md            層構造と責務
│       ├── code-review.md             レビュー観点
│       ├── testing.md                 テスト戦略
│       └── definition-of-done.md      完了条件
├── .claude/
│   ├── skills/                        ← Claude Code 用 Skills (再利用可能な手順)
│   │   ├── quarkus-kotlin-feature/SKILL.md
│   │   ├── quarkus-kotlin-test/SKILL.md
│   │   └── quarkus-kotlin-review/SKILL.md
│   ├── agents/                        ← Subagents (役割別エージェント)
│   ├── hooks/                         ← Hooks (危険操作ブロック、format 自動実行)
│   └── settings.json                  ← defaultMode: "plan" 等
├── .agents/                           ← Codex 用 Skills (.claude/skills と同内容)
└── src/
    ├── main/kotlin/com/example/products/
    │   ├── Product.kt                 ドメインモデル
    │   ├── ProductResource.kt         HTTP 境界
    │   ├── ProductService.kt          業務ロジック
    │   ├── ProductRepository.kt       インメモリ永続化
    │   └── dto/
    │       ├── ProductDto.kt          レスポンス DTO
    │       └── ProductCreateRequest.kt
    └── test/kotlin/com/example/products/
        ├── ProductResourceTest.kt
        ├── ProductServiceTest.kt
        └── ProductRepositoryTest.kt
```

---

## 講座のハンズオン課題 (予告)

`docs/course/hands-on.md` を参照。

要件サマリ:
- `PATCH /products/{id}/price` を追加
- 価格 0 円未満は 400
- 存在しない商品 ID は 404
- 正常時は更新後の商品情報を返す
- テスト追加 (正常 / 0 円未満 / 404)

実装は **未着手** の状態で配布されています (`ProductService.updatePrice` は意図的にコメントのみ)。

---

## このリポジトリで体験する 5 つの Harness 要素

| Harness 要素 | 場所 | 効果 |
|---|---|---|
| **文脈** (Context) | `AGENTS.md`, `CLAUDE.md`, `docs/ai/*` | AI に規約・DoD・層構造を伝える |
| **再利用** (Reuse) | `.claude/skills/`, `.agents/skills/` | 繰り返し手順を SKILL.md に固定 |
| **制約** (Constraints) | `.claude/hooks/pre-tool-use-protect.sh` | 危険ファイルの編集をブロック |
| **検証** (Verification) | `.claude/hooks/post-tool-use-format.sh` | 編集後に format 自動実行 |
| **分業** (Delegation) | `.claude/agents/` | 調査・テスト解析・レビューを分離 |

詳細は `docs/course/instructor-guide-90min.md` §2 を参照。

---

## 自社リポジトリへの導入

`docs/course/adoption-checklist.md` の **Phase 1〜5 ロードマップ** を参照。

---

## ライセンス

MIT (see LICENSE)
