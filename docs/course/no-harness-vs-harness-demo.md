# ハーネスなし / あり 比較デモ手順書

第 2 回 §6 (15 分) で講師が画面共有してライブ実演するための手順書。

---

## 目的

同一タスク **「商品価格更新 API を追加して」** を、Harness を **意図的に外した状態** と **整備済みの状態** で実行し、出力の差を受講者に体感させる。

---

## デモの設計原則

1. **まったく同じプロンプトで比較する** — Round 1 / Round 2 とも一字一句同じ雑なプロンプト「商品価格更新APIを追加して。」を使う。**変数はリポジトリの中身（ハーネス）だけ**。「skill を使って」「テスト結果を報告して」等をプロンプトに足すと *プロンプト差* が交絡し「環境差」のデモにならないので、**足さない**。
2. **Harness なしを先にやる** — 「困った状態」を見せた後で「Harness が解決する」流れにする
3. **AI モデルは同じ** — モデル差を変数から除外
4. **新規セッションでやる** — 履歴の汚染を避ける

> このデモの核心は「**頼んでいないのに、テスト実行とリスク報告が出る**」を見せること。報告はプロンプトではなく `AGENTS.md` の Definition of Done が強制している。だから Round 2 でプロンプトに報告指示を足してはいけない（足すと「プロンプトで頼んだから出た」になり台無し）。

---

## 事前準備 (講座開始 30 分前まで)

### 準備 1: ハーネスなしリポジトリのコピーを作っておく

```bash
cd /path/to/working-dir
cp -r sample-quarkus-app sample-quarkus-app-no-harness
cd sample-quarkus-app-no-harness
rm AGENTS.md CLAUDE.md
rm -rf .claude/ .agents/ docs/ai/
# docs/course/ は残しても良いが Round 1 では読まれないようにしたい
rm -rf docs/course/
./gradlew test  # 緑であることを確認 (テスト本体は手付かず)
```

### 準備 2: ハーネスありリポジトリ (本物) を確認

```bash
cd /path/to/working-dir/sample-quarkus-app
ls AGENTS.md CLAUDE.md .claude/skills/ .agents/skills/ docs/ai/  # 全部存在を確認
./gradlew test  # 緑であることを確認
```

### 準備 3: 講師機に ktlint を入れておく（Hook 演出のため）

PostToolUse Hook (`post-tool-use-format.sh`) は **ktlint が入っていれば** `[Harness] ktlint formatted: <path>` を出し、入っていなければ `[Harness] ktlint not installed — skipping format ...` になる。Round 2 で「編集したら自動で format が走る」を見せたいので、**講師機だけは事前に導入**する:

```bash
brew install ktlint   # macOS。未導入だと formatted 演出は出ない（skip ログになる）
```

> 受講者の大半は ktlint 未導入で skip ログになる。これは想定どおりで「format Hook の有効化は Phase 4」と口頭補足する。Hook 自体が走っていること（[Harness] 行が出ること）は ktlint 有無に関わらず見せられる。

### 準備 4: gradle daemon ウォームアップ + 録画フォールバック

- 開始前に本物リポジトリで `./gradlew test` を **1 回流して daemon を温める**（コールド初回コンパイルの数十秒遅延を消す）
- Round 2 が時間内に終わらないリスクに備え、**正常に通ったときの録画** を用意しておく

### 準備 5: Claude Code (または Codex) の準備

- 起動直後の cwd を切り替えられる準備をする（cwd が `sample-quarkus-app/` 直下でないと AGENTS.md が読まれない）
- 録画 ON
- 画面共有テスト

---

## Round 1: ハーネスなし (5 分)

### 操作

#### 1. ディレクトリ移動

```bash
cd /path/to/working-dir/sample-quarkus-app-no-harness
ls
# AGENTS.md / CLAUDE.md / .claude/ / .agents/ / docs/ai/ が無いことを受講者に見せる
```

#### 2. Claude Code 起動

```bash
claude
```

#### 3. プロンプト

```
商品価格更新APIを追加して。
```

(↑ 雑に依頼するのがポイント。受講者の「いつものやり方」を再現)

#### 4. AI の振る舞いを観察 (3 分)

予想される挙動:

- 既存ファイルを **ざっくり読む** (Resource だけ見て Service は読まない、など)
- plan mode に **入らない可能性が高い** (defaultMode 設定が無いため)
- Resource にバリデーションを書く可能性
- DTO を作らずプリミティブで受ける可能性
- テストを書かない / 雑に書く
- 完了報告が **「できました」「テストはこちらで実行してください」** などになりがち

#### 5. テスト実行 (講師)

```bash
./gradlew test
```

→ 通る / 通らない / そもそも書かれていない、を受講者に見せる。

### 観察ポイント (講師が口頭で解説)

- AI が **どのファイルを読んだか** (履歴を遡って見せる)
- 設計判断が **どこで暗黙的に決まったか**
- テストが **本当にカバーしているか** (3 ケース揃ったか?)
- 完了報告の **検証可能性** (「できました」では誰も検証できない)

---

## Round 2: ハーネスあり (7 分)

### 操作

#### 1. ディレクトリ移動

```bash
cd /path/to/working-dir/sample-quarkus-app
ls
# AGENTS.md / CLAUDE.md / .claude/ / .agents/ / docs/ai/ が存在することを受講者に見せる
```

#### 2. 主要ファイルを 30 秒で紹介

開いて見せる (中身は説明済みなので素早く):
- `AGENTS.md` (DoD 含む)
- `docs/ai/architecture.md` (層構造)
- `.claude/skills/quarkus-kotlin-feature/SKILL.md` (5 ステップ)

#### 3. 新規 Claude Code セッション

```bash
claude
```

#### 4. プロンプト (Round 1 と一字一句同じ)

```
商品価格更新APIを追加して。
```

> ⚠️ Round 1 と **完全に同じ** プロンプト。「skill を使って」「報告して」を足さないこと（理由は冒頭の設計原則）。

#### 5. AI の振る舞いを観察 (4 分)

期待される挙動（プロンプトは雑なまま、**環境だけで** こう変わる）:

- **`defaultMode: "plan"` で自動的に plan mode に入る**（頼んでいない）
- **AGENTS.md を読む** (画面で確認)
- **docs/ai/architecture.md を読む** (画面で確認)
- plan（書面計画）に 変更ファイル一覧 / テスト戦略 / リスク が出る
- 計画承認後に実装
- Resource は HTTP 境界に留まる
- Service に 0 円未満チェック
- DTO (PriceUpdateRequest) を新規作成
- テスト 3 ケース追加
- `./gradlew test` を自動実行
- **頼んでいないのに** 完了報告が構造化される (変更ファイル / 実装した振る舞い / テスト結果 / リスク)

> ★ ここを強調: 「『テスト結果とリスクを報告して』とは言っていない。なのに出る。`AGENTS.md` の Definition of Done に書いてあるからです。**依頼文ではなくリポジトリが完了基準を決めている**」

> フォールバック: quarkus-kotlin-feature skill が自動発火しない場合でも、plan mode 突入・AGENTS.md/docs 読み込み・DoD 準拠の報告は `defaultMode`/自動 import で起きるので効果は示せる。どうしても skill 動作を見せたい時だけ「quarkus-kotlin-feature skill を使って」を足す（が、足さずに効くのが今日の主眼）。

#### 6. PostToolUse Hook の効果を見せる

ファイル編集の直後に format Hook が走る様子を見せる。

```
[Harness] ktlint formatted: src/main/kotlin/com/example/products/ProductService.kt
```

#### 7. テスト実行 (AI が自動でやるはず)

`./gradlew test` の出力を見せる。緑であることを確認。

### 観察ポイント (講師が口頭で解説)

- AGENTS.md / `docs/ai/architecture.md` が **本当に読まれた** こと
- 計画 (plan) が **書面で残る** こと → 後でレビュー可能
- 設計判断が **AGENTS.md の規約に沿った** こと
- テストが **DoD 通り 3 ケース** 揃った
- 完了報告が **DoD 形式** で構造化された
- Hook が **自動で format** を走らせた

---

## 比較サマリ (3 分)

スライドに表示しつつ、講師が口頭でまとめる:

| 観点 | Round 1 (ハーネスなし) | Round 2 (ハーネスあり) |
|---|---|---|
| 既存設計の調査 | 推測ベース | AGENTS.md / docs/ai 経由 |
| 計画 | 出ない / 暗黙 | plan mode で書面化 |
| 設計判断 | Resource にロジック流入の可能性 | Service に集約（層分離を遵守） |
| バリデーション | 場所が曖昧 | Service (DoD 通り) |
| DTO | プリミティブで済ます可能性 | PriceUpdateRequest 作成 |
| テスト | 抜けやすい / 雑 | 3 ケース揃う |
| Format | 手動 / 抜ける | Hook が自動 |
| 完了報告 | 「できました」 | 変更点・テスト結果・リスク |
| 再現性 (別の人に頼む) | バラつく | 揃う |

### 中心メッセージの再確認

> 「**性能差ではなく環境差**」
>
> 「AI モデルは同じ。**プロンプトは一字一句同じ**。違うのはリポジトリの中身だけ」
>
> 「これが Harness Engineering です」

---

## トラブル対応

### Round 1 で AI が意外と良い結果を出してしまう

- 「これでも良いが、**チームで毎回これを期待できるか?**」と問いかける
- 別の受講者に同じプロンプトを打ってもらい、結果のバラつきを見せる (時間があれば)

### Round 2 で正常系・404 が 400 になる（単一プロパティ DTO の Jackson 罠）

`PriceUpdateRequest(val price: Int)` は単一プロパティ data class のため `jackson-module-kotlin` が `{"price":150}` を受け付けず 400 になる既知の罠。**むしろ絶好の教材**: テスト（Resource 200/404）がこのミスを機械的に検出する＝検証軸が効いている実例として見せられる。
- 講師の選択肢A（安全）: `quarkus-kotlin-feature` Skill の DTO 例は `@JsonCreator(PROPERTIES)` 込みなので、skill に従えば一発で通る。
- 講師の選択肢B（教材化）: あえて素の単一プロパティ DTO で 400 を出し、テストが落ちる→ Claude が `@JsonCreator` で自己修正する流れを見せる（時間に余裕がある時のみ。デモ手順書冒頭のフォールバック方針に従う）。
- 詳細: `docs/ai/architecture.md`「既知の落とし穴」。

### Round 2 で Hook が遅くてデモが止まる

- 事前に `post-tool-use-format.sh` が高速 (1 秒以内) であることを確認
- 遅い場合は Hook を一時無効化し、「本番ではここで format が走ります」と口頭で補足

### AGENTS.md を読まない / plan mode に入らない（＝デモの効果が出ない）

これが起きると Round 2 が Round 1 と区別つかなくなるので最優先で確認:
- Claude Code 起動時の **cwd が `sample-quarkus-app/` 直下** か（ここがズレると AGENTS.md/CLAUDE.md/settings.json が読まれない）
- `.claude/settings.json` の `defaultMode: "plan"` を確認（無いと plan mode に入らない）
- `/memory` で AGENTS.md/CLAUDE.md が読み込まれているか確認
- Shift+Tab で手動 plan mode 切替も可

### skill の動きも見せたい場合（任意）

- 本デモは skill 明示なしでも AGENTS.md+plan mode で効果が出るが、skill 発火を見せたい時は `「quarkus-kotlin-feature skill を使って」` を足す
- ただし「報告して」等の **DoD を肩代わりする指示は足さない**（プロンプト差の交絡になる）

---

## デモ後の質疑応答 (オプション・時間が余れば)

予想される質問:

- **Q**: 「ハーネスを書くコストは?」
- **A**: 「初回 30〜60 分。育てるコストは PR ごとに 5 分以内」

- **Q**: 「Hook は本当に必要?」
- **A**: 「Phase 4 から。Phase 1〜3 (AGENTS.md / docs/ai / Skill) だけでも効果は出る」

- **Q**: 「他の AI (Cursor / Aider) でも効く?」
- **A**: 「AGENTS.md は **ベンダー中立のオープン標準**（Codex / Cursor / Amp / Google Jules 等が採用、仕様は `openai/agents.md`、Linux Foundation 配下で運営）。Claude Code も読む。Cursor は `.cursorrules` も別途読む。各エージェントの読込ファイルに同じポインタを置けば移植できる」

- **Q**: 「ハーネスなしリポジトリは本当に削除した?」
- **A**: 「コピーで作ったので、本物のリポジトリは無傷。**本物に手を入れるときは必ずコピーかブランチで**」

---

## デモ後の片付け

```bash
# ハーネスなしコピーは削除
rm -rf /path/to/working-dir/sample-quarkus-app-no-harness

# Round 2 で本物リポジトリに生まれた変更（価格更新 API 実装）を破棄
cd /path/to/working-dir/sample-quarkus-app
git restore .          # 配布リポジトリは git 管理済みなので clean state に戻る
git clean -fd          # 新規追加された PriceUpdateRequest.kt 等も除去
```

> 配布リポジトリは `git init` + 初期コミット済みで配っている前提（→ `20_URL置換手順.md` / README）。`git restore` が `fatal: not a git repository` になる場合は、リポジトリが git 化されていない。その時は配布元から再展開する。

---

## 関連ファイル

- 講師ガイド全体: `instructor-guide-90min.md` の §6
- ハンズオン詳細: `hands-on.md`
- DoD: `docs/ai/definition-of-done.md`
- アーキテクチャ規約: `docs/ai/architecture.md`
