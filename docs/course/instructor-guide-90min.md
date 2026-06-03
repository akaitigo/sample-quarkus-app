# 講師ガイド (90 分版)

第 2 回ワークショップ「Quarkus + Kotlin で学ぶ AI Harness Engineering 入門」の講師用台本。

各セクションは **学習目標 / スライド / 台本 / デモ / Q&A 想定 / 次セクションへの繋ぎ** で構成。

---

## §0. 開会 (オフレコ・2 分)

開始 5 分前まで:
- 受講者の `./gradlew test` が緑になっているか Meet チャットで確認
- 画面共有テスト
- 録画 ON
- アンケート URL を Meet チャットに貼る

開始:
- 「第 2 回 Harness Engineering 入門を始めます」
- 「90 分で、AI が安全・再現可能に動くリポジトリ設計を学びます」
- 「**まず大事なこと: これは Claude / Codex の操作講座ではありません**」

---

## 時間管理の方針 (講師向け・重要)

タイムテーブルの 90 分は **§1〜§7 の合計**（10+10+15+15+15+15+10）。§0 開会・§8 クロージングは各 2 分で、**全体は約 90 分の枠に収める**前提。単独講師・28 名規模で押しやすいので、以下を守る:

- **講評はタイムボックス**: §4/§5 のハンズオン講評は **2〜3 件・各 90 秒**。超過しそうならチャット投稿を拾うだけで先へ。
- **遷移バッファ**: 各 § の「時間メモ」に Q&A・画面切替の余白を含めてある。Q&A が伸びたら「続きはチャット/課後」へ流す。
- **押したら削る順**: ①§3 の構造ツアーを短縮 → ②§7 ワークを 8 分に圧縮 → ③Q&A をチャット回し。**§6 デモと §6 末の engineer-out 体験は死守**（学習の核）。
- **§6 はライブ依存**: Round 2 は AI 応答・テスト実行に時間が左右される。開始前に `./gradlew test` を 1 回流して **gradle daemon をウォームアップ**し、コールド遅延を消す。延びそうなら **録画フォールバック**（→ `no-harness-vs-harness-demo.md`）。

---

## §1. Harness Engineering の概念 (10 分)

### 学習目標
受講者が「Harness Engineering ≠ プロンプトエンジニアリング」を言語化できる。

### スライド
- スライド 2「中心メッセージ」
- スライド 3「Harness Engineering とは」
- スライド 4「プロンプトエンジニアリングとの違い」

### 台本

> 「AI を業務で使い始めた皆さん、こんな経験ないですか?」
>
> - 同じプロンプトを別の人が打つと、別の結果が出る
> - 昨日うまくいった依頼が、今日は通らない
> - 完了報告が来たが、テストが書いてない / ビルドが壊れている
>
> 「これらは **AI の性能** の問題ではなく、**AI に渡している環境** の問題です」

中心メッセージを読み上げ:

> **AI 活用 = AI が作業できる文脈・制約・検証・再利用手順をリポジトリに埋め込むこと**

> 「これを総称して **Harness Engineering** と呼びます。Mitchell Hashimoto (元 HashiCorp / Vagrant 作者) が 2026 年 2 月に提唱した概念で、原文は短くてシンプルです」

> *"anytime you find an agent makes a mistake, you take the time to engineer a solution such that the agent never makes that mistake again"*
>
> — Mitchell Hashimoto, 2026-02-05

> 「**ミスを engineer-out する** これがコアです。プロンプトを改善するのではなく、**プロンプトを書かなくてもミスしない設計** を作る」

### プロンプトエンジニアリングとの比較表 (口頭で対比)

| プロンプトエンジニアリング | Harness Engineering |
|---|---|
| 「どう依頼するか」 | 「どこに何を置くか」 |
| 一時的・個人ごと | 永続的・チームで共有 |
| AI ごとに書き直す | AI を変えても効く |
| チャット欄に書く | リポジトリにコミットする |

### Q&A 想定

- **Q**: 「プロンプトエンジニアリングと両方やるの?」
- **A**: 「両方やります。ただし Harness が下、プロンプトが上です。土台が無いと、いいプロンプトも安定しません」

### 繋ぎ
> 「では、Harness を **どこに何として** 埋め込むか。公式機能を見ていきます」

---

## §2. Claude / Codex 公式機能の位置づけ (10 分)

### 学習目標
AGENTS.md / CLAUDE.md / Skills / Hooks / Subagents の **役割分担** を理解する。

### スライド
- スライド 5「公式機能マトリクス」
- スライド 6「ファイル配置例」

### 台本

> 「Harness は 4 つの軸で構成されます」

```
文脈 (Context)     : AGENTS.md, CLAUDE.md, docs/ai/*
制約 (Constraints) : Hooks (PreToolUse で危険操作ブロック)
検証 (Verification): Hooks (PostToolUse で format/lint/test 自動実行)
再利用 (Reuse)     : Skills (繰り返し作業を SKILL.md に固定)
```

> 「役割分担を明確にしてください」

| 公式機能 | 軸 | 提供元 | 役割 |
|---|---|---|---|
| `AGENTS.md` | 文脈 | ベンダー中立のオープン標準（Codex/Cursor/Amp 等が採用・Claude も読む） | **全エージェント共通** のブリーフ。プロジェクト概要・規約・DoD |
| `CLAUDE.md` | 文脈 | Anthropic | **Claude Code 専用** の追加。`@AGENTS.md` import 推奨。薄く |
| `docs/ai/*.md` | 文脈 | 自前 | **詳細リファレンス**。AGENTS.md からポインタで参照 |
| Skills | 再利用 | 両者あり (`.claude/skills/`, `.agents/skills/`) | 繰り返し手順を **SKILL.md** にパッケージ化 |
| PreToolUse Hook | 制約 | Claude Code | 危険操作を **編集前にブロック** |
| PostToolUse Hook | 検証 | Claude Code | 編集後に **format / lint / test を自動実行** |
| Subagents | 分業 | 両者あり | 役割別 (調査・レビュー) に **コンテキストを分離** |
| MCP | 連携 | 両者あり | Jira / GitHub / Confluence などの **外部システム連携** |

> 「初回はこの中で **AGENTS.md / CLAUDE.md / Skills** の 3 つに集中します。Hooks / Subagents / MCP は今日のあとで補足程度に触れます」

> 「正直に言うと、4 軸のうち **今日 自分の手で触るのは『文脈 (AGENTS.md/CLAUDE.md)』と『再利用 (Skills)』の 2 つ** です。**検証 (Hooks)** は §6 デモで動きを見ます。**制約 (PreToolUse Hook)・分業 (Subagents)・MCP** は Phase 4〜5 として持ち帰り、自社で段階導入します。今日 全部を体験するのではなく、まず効果の大きい 2 軸を手で覚えてもらうのが狙いです」

### Q&A 想定

- **Q**: 「`.claude/skills/` と `.agents/skills/` 両方書くの?」
- **A**: 「組織で Claude と Codex 両方使うなら両方。内容はほぼコピペで OK。違いは Claude が `.claude/`、Codex が `.agents/` を見るというパスだけ」

### 繋ぎ
> 「では実際の題材リポジトリを見ましょう」

---

## §3. 題材リポジトリ説明 (15 分)

### 学習目標
商品マスタ API (Quarkus + Kotlin) の **層構造と責務** を把握する。

### スライド
- スライド 7「題材: 商品マスタ API」
- スライド 8「アーキテクチャ層」
- スライド 9「ディレクトリ構成」

### 時間メモ (15 分の内訳)
- 題材・要件の説明: 約 7 分
- IDE 画面共有デモ (構造ツアー): 5 分
- Q&A・遷移バッファ: 約 3 分

### 当日 受講者が手を動かす範囲 (重要)

> 「**当日 自分の手で書くのは §4 (AGENTS.md 1 行) と §5 (Skill 1 ステップ改造) です**。価格更新 API そのものの実装は、§6 で講師のライブデモを観察し、**課後に各自 `docs/course/hands-on.md` を見ながら実装** します。90 分でフルに実装まではやりません — 体験の核は『ハーネスがあると AI の挙動がどう変わるか』を見ることです」

### 台本 + デモ

> 「題材は **商品マスタ管理 API** です。小売・業務システムでよく出る形を意図的に選びました」

実装済み機能 (今日のスタート地点):
- 商品一覧取得 `GET /products`
- 商品詳細取得 `GET /products/{id}`
- 商品登録 `POST /products`

ハンズオンで追加する機能:
- **商品価格更新 `PATCH /products/{id}/price`**

> 「**講座中のゴール: 商品価格更新 API を追加する**」

要件 (スライドで読み上げ):
- 商品 ID を指定して価格を更新する
- 価格は 0 円未満にできない
- 存在しない商品 ID の場合は 404 を返す
- 正常時は更新後の商品情報を返す
- テストを追加する

#### IDE 画面共有デモ (5 分)

`docs/ai/architecture.md` をスライド表示しつつ、リポジトリを実際に開く:

```
sample-quarkus-app/
├── AGENTS.md                ← 全エージェント共通ブリーフ
├── CLAUDE.md                ← Claude Code 追加
├── docs/
│   ├── ai/                  ← AI 向け詳細ドキュメント
│   │   ├── architecture.md     層構造・責務
│   │   ├── code-review.md      レビュー観点
│   │   ├── testing.md          テスト戦略
│   │   └── definition-of-done.md  完了条件
│   └── course/              ← 講座資料 (このファイル含む)
├── .claude/
│   ├── skills/              ← Claude Code 用 Skills
│   ├── agents/              ← Subagents
│   ├── hooks/               ← Hooks
│   └── settings.json
├── .agents/                 ← Codex 用 Skills
└── src/
    ├── main/kotlin/com/example/products/
    │   ├── ProductResource.kt   HTTP 境界
    │   ├── ProductService.kt    業務ロジック
    │   ├── ProductRepository.kt インメモリ永続化
    │   ├── Product.kt           ドメインモデル
    │   └── dto/                 ProductDto, ProductCreateRequest, PriceUpdateRequest(提供済)
    └── test/kotlin/...
```

> 「`dto/PriceUpdateRequest` は **scaffold として提供済み**（リクエスト形状の DTO）。`ProductService.updatePrice` と `ProductResource` の PATCH エンドポイント・テストが **未実装** の状態で配布しています。§6 ではこの未実装部分を埋めます」

> 講師メモ: DTO を提供済みにしているのは、単一プロパティ DTO の Jackson 罠（後述）で実演が脱線するのを防ぐため。実機検証で、雑プロンプトだと AI がこの罠を踏んで 10 分超ハマることを確認済み。DTO を渡しておけば Round 2 は素直に通る。

`src/main/kotlin/com/example/products/ProductResource.kt` を開いて、以下を強調:

> 「Resource 層は HTTP 境界。バリデーションの入口だけ。**ビジネスロジックは入れない**」

`ProductService.kt` を開いて:

> 「Service 層に業務ロジック。**0 円未満チェックはここに置く**」

### Q&A 想定

- **Q**: 「Repository がインメモリなのは?」
- **A**: 「講座を 90 分で完結させるため。本番は JPA / Panache / R2DBC など。AGENTS.md には『永続化は Repository だけ』とだけ書けば、AI は技術選定を勝手に変えない」

### 繋ぎ
> 「では、この構造を **AI に伝える** AGENTS.md と CLAUDE.md を見ましょう」

---

## §4. AGENTS.md / CLAUDE.md 作成 (15 分)

### 学習目標
AGENTS.md / CLAUDE.md の **役割分担と書き方** を体得する。

### スライド
- スライド 10「AGENTS.md のサンプル」
- スライド 11「CLAUDE.md のサンプル (薄い)」
- スライド 12「検証可能な具体性」

### 台本 + デモ

> 「AGENTS.md は **全エージェント共通** のブリーフです。ただし読まれ方が2系統あります: **Codex は AGENTS.md を直接読む**。一方 **Claude Code が起動時に読むのは CLAUDE.md** で、その中の `@AGENTS.md` というインポートで AGENTS.md を取り込みます。**だから両方のファイルがある**——共通の中身は AGENTS.md に1つ持ち、CLAUDE.md は薄く保って import するわけです。Cursor 等も各自の読込ファイル(`.cursorrules` 等)から参照させれば共通化できます」

リポジトリの `AGENTS.md` を画面で開く。要素を順に解説:

1. **Project Overview** — 何のリポジトリか
2. **Tech Stack** — 使っている技術
3. **Architecture Rules** — 層構造の規約 (例: Resource にロジック禁止)
4. **Commands** — 必須コマンド (`./gradlew test` 等)
5. **Implementation Rules** — 実装規約 (DI / DTO 分離 / テスト同時更新)
6. **Definition of Done** — 完了条件

> 「**検証可能な具体性で書く**。これがコツです」

| NG (曖昧) | OK (具体的) |
|-----------|------------|
| コードを適切にフォーマットして | `ktlint --format <file>`（PostToolUse Hook が編集後に自動実行） |
| テストを書いて | JUnit5 + MockK でユニットテスト。`src/test/kotlin/` 配下、assertion は `assertEquals` 形式 |
| 適切なパッケージに配置 | Resource/Service/Repository は `com.example.products` 直下にクラス分離、DTO は `dto/` サブパッケージ |

> 「曖昧な指示は AI に勝手に解釈されます。必ず **実行可能なコマンド** か **検証可能な規約** で書いてください」

#### CLAUDE.md は薄く

実ファイル `CLAUDE.md` を画面で開く（以下は骨子の抜粋。実物は Subagents / Model defaults / Hooks behavior 節も持つ）:

```
@AGENTS.md

## Claude Code specific rules
- Use plan mode before any non-trivial change.
- Ask before modifying build.gradle.kts / settings.json / .claude/hooks/.
- When test output is large, summarize only failing tests and likely causes.

## Subagents (use to keep main context clean)
- @agent-codebase-investigator — read-only exploration
- @agent-test-failure-analyzer — diagnose test failures
- @agent-reviewer — pre-PR diff review
```

> 「`@AGENTS.md` で import するから、共通ルールは全部 AGENTS.md にある。CLAUDE.md には **Claude Code 固有の操作ルール** と **サブエージェントの呼び分け** だけ書く。実物は薄いまま（30 行台）です」

#### ハンズオン (5 分)

> 「では受講者の皆さん、5 分でやってみてください」
>
> 1. `AGENTS.md` の `## Architecture Rules` セクションに、1 行追加（**手元のエディタで直接編集**してください）
>    - 例: 「価格更新時の負数チェックは Service 層で行う」
> 2. Meet チャットに「追加した 1 行」を貼る

> ⚠️ 講師補足: 「`AGENTS.md` は PreToolUse Hook の **保護対象** です。Claude Code に『AGENTS.md に追記して』と頼むと **わざとブロックされます**（`[Harness] BLOCKED`）。これはバグではなく『ハーネスそのものは人間が編集する』という意図した安全設計。なので今日は **手元エディタで直接** 書いてください。保護の動きは §6 でも見せます」

→ 講師は **2〜3 件をタイムボックス 90 秒** で講評（超過しそうならチャット投稿だけ拾って先へ）。

### Q&A 想定

- **Q**: 「AGENTS.md は何行までが目安?」
- **A**: 「30〜50 行。腐敗を防ぐため、詳細は `docs/ai/*.md` にポインタで参照する」
- **Q**: 「@AGENTS.md import は Codex でも効く?」
- **A**: 「Codex は AGENTS.md を直接読むので import 不要。Claude Code は `@AGENTS.md` で明示」

### 繋ぎ
> 「文脈は伝わった。次は **繰り返し作業の手順化** = Skills です」

---

## §5. Skill 作成 (15 分)

### 学習目標
Skill の **3 軸 (feature / test / review)** と **書き方の型** を理解する。

### スライド
- スライド 13「Skill とは」
- スライド 14「3 つの Skill サンプル」

### 台本 + デモ

> 「Skill は **繰り返し作業の手順化** です。AGENTS.md は『地図』、Skill は『手順書』だと思ってください」

> 「初回は **3 つだけ** 作ります」

| Skill | いつ使う | 何をするか |
|---|---|---|
| `quarkus-kotlin-feature` | 機能追加・変更時 | 既存設計の理解 → 計画 → 実装 → テスト → 報告 |
| `quarkus-kotlin-test` | テスト追加・診断時 | 既存テストスタイル把握 → 追加 → 失敗診断 |
| `quarkus-kotlin-review` | PR 作成前 | アーキテクチャ / テスト / 安全性のレビュー |

#### Skill ファイル構造 (画面で開く)

`.claude/skills/quarkus-kotlin-feature/SKILL.md` を表示:

```yaml
---
name: quarkus-kotlin-feature
description: Use this when implementing or modifying a feature in a Quarkus + Kotlin backend service.
---
```

> 「`description` がポイント。Claude / Codex は **description を見て『この Skill を使うべきか』を自己判断します**」

中身を見せる:

```
Step 1: Understand existing design
Step 2: Propose plan
Step 3: Implement
Step 4: Test
Step 5: Report
```

> 「具体的なコマンド・チェック項目が含まれているのがわかりますね」

#### ハンズオン (5 分)

> 「`SKILL.md` の Step 1 を改造してみてください」
>
> 例: 「Resource を読む前に、対応する Service と Repository を必ず Grep する」
>
> （`.claude/skills/` は保護対象外なので、ここはエージェント経由でも手編集でも可）

→ 受講者がチャットに貼る → 講師は **2〜3 件をタイムボックス 90 秒** で講評（超過しそうならチャット投稿だけ拾って先へ）。

### Q&A 想定

- **Q**: 「`.claude/skills/` と `.agents/skills/` どちらに書く?」
- **A**: 「両方。コピペで OK。Claude は `.claude/`、Codex は `.agents/` を見るので、二重管理は避けられないが、内容は同じ」
- **Q**: 「Skill が呼ばれているか確認方法は?」
- **A**: 「Claude Code なら `/help` でスキル一覧、対話中なら『この Skill を使ってください』と明示してもいい。description 経由で自動呼び出しもある」

### 繋ぎ
> 「文脈 (AGENTS) と再利用 (Skill) ができた。**本当に効くのか** 比較デモで確認します」

---

## §6. ハーネスなし / あり 比較デモ (15 分)

### 学習目標
ハーネスの **効果** を、同一タスクで体感する。

### スライド
- スライド 15「比較デモの設計」
- スライド 16「観察ポイント」

### 時間メモ (15 分の内訳)
- Round 1 (ハーネスなし): 5 分
- Round 2 (ハーネスあり): 6 分
- 比較サマリ: 2 分
- 観察 → engineer-out 体験: 2 分

詳細手順は **`no-harness-vs-harness-demo.md` が正本**。本節はその要約。事前準備（コピー作成・gradle daemon ウォームアップ・録画フォールバック）は必ずデモ手順書を参照。

### 台本 + ライブデモ (講師が画面共有で実演)

> 「**まったく同じ雑なプロンプト『商品価格更新APIを追加して。』を 2 通りでやります**。変えるのはプロンプトではなく、リポジトリの中身（ハーネス）だけ」

#### Round 1: ハーネスなし (5 分)

> 「まず Harness を **意図的に外した** コピーでやります」

操作（`no-harness-vs-harness-demo.md` の cp -r コピー方式に従う。**本物リポジトリには触らない**）:
1. 事前に作った `sample-quarkus-app-no-harness/`（AGENTS.md/CLAUDE.md/.claude/.agents/docs/ai を削除済み）に `cd`
2. Claude Code を起動
3. プロンプト: **「商品価格更新APIを追加して。」**

観察ポイント (講師が解説):
- 既存設計を読まずに推測で書く可能性
- Resource にロジックが入る可能性
- テストを書かない / 雑なテストを書く可能性
- 完了報告が「できました」だけになりがち

#### Round 2: ハーネスあり (6 分)

> 「次に **無傷の本物リポジトリ** で、**まったく同じプロンプト** を打ちます」

操作（cp -r 方式なので本物は無傷。復元作業は不要、`cd` で戻るだけ）:
1. `sample-quarkus-app/`（ハーネス整備済み）に `cd`
2. 新規 Claude Code セッション
3. プロンプト: **「商品価格更新APIを追加して。」**（Round 1 と一字一句同じ）

観察ポイント (講師が解説):
- `defaultMode: "plan"` で **自動的に plan mode** に入る（頼んでいないのに）
- **AGENTS.md の内容に沿って動く**（Claude Code は起動時に CLAUDE.md を自動読込 → その `@AGENTS.md` import で AGENTS.md がコンテキストに入る）。その指示に従い **`docs/ai/architecture.md` を自分から読みに行く**（証拠を画面で示す）
- Service 層に 0 円未満チェック / Resource は HTTP 境界に留まる
- テストを同時に追加
- **頼んでいないのに完了報告にテスト結果・リスクが付く** ← これが核心

> 「ここが今日いちばん大事です。**『テスト結果とリスクを報告して』とプロンプトで頼んでいないのに、報告が出てくる**。なぜか? `AGENTS.md` の Definition of Done に『テストを実行して結果を報告／リスクを明示』と **書いてあるから** です。依頼文ではなく **リポジトリに埋め込んだ DoD** が AI の完了基準を決めている。これが Harness Engineering です」

> 講師フォールバック: もし skill が自動発火せず RPI フローが浅い場合でも、plan mode 突入と AGENTS.md 読み込みは `defaultMode`/自動 import で **確実に起きる**ので効果は示せる。明示的に呼びたければ「quarkus-kotlin-feature skill を使って」と足してもよい（ただし *足さなくても効く* のが今日の主眼）。所要が延びそうなら録画フォールバックに切替（→ デモ手順書）。

> ⚠️ Jackson の罠（DTO 提供で回避済み）: 単一プロパティ DTO は `jackson-module-kotlin` の委譲コンストラクタ誤認で `{"price":150}` が 400 になる罠がある。**実機検証で、雑プロンプトだと AI が自分で素の DTO を書いてこの罠を踏み、10 分超ハマる**ことを確認したため、`dto/PriceUpdateRequest` は `@JsonCreator(PROPERTIES)` 込みで提供済みにしてある。よって Round 2 は脱線せず通る。罠の解説は `docs/ai/architecture.md`「既知の落とし穴」。

#### 比較サマリ (2 分)

| 観点 | ハーネスなし | ハーネスあり |
|---|---|---|
| 既存設計の調査 | その場の推測 | AGENTS.md / docs/ai 経由で確認 |
| 計画 | 出ない / 暗黙 | plan mode で自動書面化 |
| 設計判断 | Resource にロジック流入の可能性 | Service に集約（層分離を遵守） |
| テスト | 抜けやすい / 雑 | DoD に含まれ自動で付く |
| 完了報告 | 「できました」 | 頼まずとも 変更点・テスト結果・リスク |
| 再現性 | 人によってバラつく | チーム標準に寄る |

> 「**性能差ではなく環境差**。プロンプトも AI モデルも同じ。違うのはリポジトリの中身だけ」

> 講師注: ハーネスありで出力が **揃う** のは、SKILL が手順と参照実装を固定し AGENTS.md が DoD を強制しているから ＝ **チーム標準の共有効果**。「AI が急に賢くなった」のではなく「環境が答えの形を決めている」と言語化すること。

#### 観察 → engineer-out 体験 (2 分) ★Hashimoto の核

> 「今 Round 1 で **実際に見たミス** を 1 つ、二度と起きないように潰します。これが Hashimoto の言う *engineer a solution such that the agent never makes that mistake again* の最小サイクルです」

1. Round 1 で観察されたミスを 1 つ板書（例: 「Resource に 0 円チェックを書いた」「テストを書かず完了宣言した」）
2. 受講者は **そのミスを防ぐ AGENTS.md の 1 行** を考え、Meet チャットに投稿
   - 例: 「価格の負数チェックは Service 層で行い、Resource には書かない」
   - 例: 「振る舞いを変えたら必ず対応するテストを追加し、`./gradlew test` 緑を完了条件とする」
3. 講師が 1〜2 件を読み上げ「この 1 行を AGENTS.md に足せば、次回から AI はこのミスをしない」と締める

> 「**想像のミスではなく、たった今 目の前で起きたミスを潰す**。これを自社リポジトリで回し続けるのが Harness Engineering の本体です。§7 ではこれを自分のリポジトリへ持ち帰ります」

### Q&A 想定

- **Q**: 「プロンプトが同じなら、なぜ結果が変わる?」
- **A**: 「Claude Code は起動時に CLAUDE.md を自動で読み、その `@AGENTS.md` import で AGENTS.md も入る。`defaultMode: plan` で plan mode に入り、AGENTS.md の DoD が完了基準を強制するから。依頼文に書かなくても環境が効いている」
- **Q**: 「ハーネスを書くコストは?」
- **A**: 「初回 30〜60 分。育てるコストは PR ごとに 5 分以内。投資対効果はチーム規模 × 開発期間で見て十分回収できる」

### 繋ぎ
> 「いま潰した 1 行を、**自社リポジトリ** でやってみましょう」

---

## §7. 自社リポジトリへの導入手順 (10 分)

### 学習目標
Phase 1〜5 ロードマップを **自分のリポジトリに適用するイメージ** を持つ。

### スライド
- スライド 17「Phase 1〜5 ロードマップ」
- スライド 18「最初の 1 つを選ぶ」

### 台本

> 「**最初から全部入れない**。Phase 1 から順に積み上げます」

```
Phase 1: AGENTS.md / CLAUDE.md                      ← 今日でほぼ理解できた
Phase 2: docs/ai/ (code-review / testing / DoD)     ← 同上
Phase 3: Skills (feature / test / review)           ← 同上
Phase 4: Hooks (危険操作ブロック + format 自動実行)   ← 後で補足
Phase 5: Subagents / MCP                            ← 後で補足
```

> 「**自社リポジトリで最初に置く Harness 要素を 1 つ決めてください**。10 分でやります」

ワークシート (Meet チャットに貼る or 別紙):

```
1. 自分の担当サブドメインで、Claude にやらせたいタスクを 1 つ
2. そのタスクで起きそうな「ミス」を 1 つ
3. それを engineer-out する Harness 要素を 1 つ選ぶ
   (AGENTS.md の 1 行 / CLAUDE.md / feature Skill / Hook)
4. 来週中に 1 PR で導入する具体的なアクション
```

> 「第 3 回 (社内メインリポジトリへの適用) で、皆さんの導入結果を共有します」

### 詳細は別ドキュメント参照

> 「Phase ごとの導入チェックリストは `adoption-checklist.md` に全部書いてあります。あとで読んでください」

### Q&A 想定

- **Q**: 「Phase 4 と 5 はいつ学ぶ?」
- **A**: 「Hooks は第 3 回後半 or 知見共有会で。MCP は応用編。今日は Phase 1〜3 だけで十分」

---

## §8. クロージング (2 分)

> 「今日のメッセージを 1 行に圧縮すると:」
>
> **AI 活用 = リポジトリに文脈・制約・検証・再利用を埋め込むこと**
>
> 「次回までの宿題:」
> 1. 自社リポジトリに **最初の Harness 要素を 1 つ** 導入する PR を出す
> 2. 効果を 1 行レポートにまとめる
>
> 「アンケート URL: （当日 Meet チャットで共有します）」
>
> 「お疲れさまでした」

---

## トラブル対応

### 受講者の `./gradlew build` が失敗する場合
- JDK 17 以上が入っているか確認 (`java -version`)
- ネットワーク制限でリポジトリが落ちてくる依存をブロックしていないか確認

### Hook が遅すぎる / 失敗する
- `.claude/hooks/post-tool-use-format.sh` を一時的に無効化 (rename)
- 受講者個別に解除指示

### Claude Code / Codex が AGENTS.md を読まない
- `/memory` (Claude Code) で読み込み状況を確認
- 受講者の cwd が sample-quarkus-app 配下になっているか確認

### Meet チャットが流れすぎる
- 質問は「Q: 〜」、感想は「→ 〜」とテンプレ化を促す
- 講師は質問だけ拾う

---

## 講座後の作業

- アンケート回収
- 質問で答えられなかったものを翌日中にチャットで補足
- 次回 (第 3 回) スライドの調整 (Phase 1〜5 と整合させる)
