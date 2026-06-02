# 自社導入チェックリスト — Phase 1〜5 ロードマップ

第 2 回ワークショップ後、自社リポジトリに Harness Engineering を **段階的に** 導入するためのチェックリスト。

最初から Skills / Hooks / Subagents / MCP を全部入れないこと。Phase 1 から順に積み上げる。

---

## 中心原則

```
Phase 1: AI に作法を教える       (AGENTS.md / CLAUDE.md)
Phase 2: 完了条件を固定する       (code-review.md / testing.md / DoD)
Phase 3: 繰り返し作業を手順化     (Skills)
Phase 4: 危険操作と検証を自動化   (Hooks)
Phase 5: 分業と外部連携          (Subagents / MCP)
```

各 Phase は **前 Phase が定着してから** 次に進む。目安は **2〜4 週間** で 1 Phase。

---

## Phase 1: AGENTS.md / CLAUDE.md

**目的**: AI にリポジトリの作法を教える。これだけでも出力の一貫性が大きく上がる。

### チェック項目

- [ ] `AGENTS.md` を作成した (30〜50 行のポインタ型)
- [ ] プロジェクト概要 / 技術スタック / 必須コマンドを書いた
- [ ] アーキテクチャ規約を書いた (例: Resource にビジネスロジック禁止)
- [ ] DoD (Definition of Done) を書いた
- [ ] `CLAUDE.md` を作成した (`@AGENTS.md` で import、Claude Code 固有のみ追加)
- [ ] 重要な docs (architecture / testing / code-review) へのポインタを書いた
- [ ] **チームメンバーで読み合わせ** をした

### 落とし穴

- AGENTS.md にチュートリアル的説明を書きすぎる → ポインタ型を維持し、詳細は別ファイル
- CLAUDE.md と AGENTS.md の重複 → CLAUDE.md は薄く、共通ルールは AGENTS.md に寄せる
- 説明が腐敗する → 「実行可能なコマンド」と「検証可能な規約」中心で書く

---

## Phase 2: code-review.md / testing.md / definition-of-done.md

**目的**: 完了条件とレビュー観点を AI と人間の両方で共有する。

### チェック項目

- [ ] `docs/ai/architecture.md` — 層構造・責務・禁止事項
- [ ] `docs/ai/code-review.md` — レビュー時にチェックする項目
- [ ] `docs/ai/testing.md` — テスト方針 (どこを単体・どこを統合・どこを E2E)
- [ ] `docs/ai/definition-of-done.md` — 完了条件 (差分説明・テスト結果・リスク報告)
- [ ] AGENTS.md からこれらへのポインタを追加した
- [ ] AI に「完了報告」させた時、これらの観点が必ず含まれることを確認した

### 落とし穴

- AGENTS.md に全部書こうとする → 必ず分離する。AGENTS.md は地図、これらは目的地
- 「テストを書いて」と曖昧に書く → 「JUnit5 + MockK でユニットテスト、assertion は X 形式」のように検証可能に
- レビュー観点が一般論 → 自社の過去レビューで実際に指摘されたものを書く

---

## Phase 3: Skills

**目的**: 繰り返し作業 (新機能追加 / テスト追加 / レビュー) を再利用可能な手順にする。

### チェック項目

- [ ] `.claude/skills/<name>/SKILL.md` のフォーマットを理解した
- [ ] `.agents/skills/<name>/SKILL.md` を Codex 用にも置いた (組織で両方使うなら)
- [ ] 最初の 3 つの Skill: **feature / test / review** を作成
  - [ ] `feature` Skill — 既存設計の理解 → 計画 → 実装 → テスト → 報告
  - [ ] `test` Skill — 既存テストスタイル把握 → 追加 → 失敗診断
  - [ ] `review` Skill — アーキテクチャ / テスト / 安全性のレビュー
- [ ] Skill をプロジェクトドメインに合わせて調整した
  - 例: `quarkus-kotlin-feature` のように技術スタックを名前に入れる
- [ ] Skill を使ったときの出力品質を、使わない時と比較した

### 落とし穴

- Skill を増やしすぎる → 最初は 3 つで十分。10 個作っても誰も使わない
- Skill の中に AGENTS.md と重複する内容を書く → Skill は「手順」、AGENTS.md は「文脈」
- Skill のステップが抽象的 → 「Step 1: Resource ファイルを Grep で特定する」のように具体的に

---

## Phase 4: Hooks

**目的**: 危険操作のブロック、フォーマット自動実行、テスト結果の要約を自動化する。

### チェック項目

- [ ] `.claude/hooks/pre-tool-use-protect.sh` で保護対象 (`build.gradle.kts`, migrations 等) を編集ブロック
- [ ] `.claude/hooks/post-tool-use-format.sh` で Kotlin ファイル編集後に `ktlint --format` を自動実行（教材リポジトリは ktlint を採用。spotless 等を使うなら適宜読み替え）
- [ ] (オプション) 関連テストの自動実行 Hook
- [ ] `.claude/settings.json` で `defaultMode: "plan"` を設定 (大きい変更は強制 plan mode)
- [ ] Hook の失敗・成功メッセージが受講者に見える状態か確認
- [ ] Hook が遅すぎないか確認 (1 編集あたり数秒以内が目安)

### 落とし穴

- 最初から重い Hook を入れる → ktlint と format だけから始める。detekt や test は段階的に
- Hook が失敗したときに沈黙する → エラーは Claude のコンテキストに必ず注入する
- 保護対象が広すぎる → 本当に危険なファイルだけブロック (build.gradle.kts, migrations, .env)

---

## Phase 5: Subagents / MCP

**目的**: 役割分業 (調査・レビュー) と外部システム連携 (Jira / GitHub / Confluence) を導入する。

### チェック項目

- [ ] `.claude/agents/codebase-investigator.md` — 読み取り専用で関連コードを調査
- [ ] `.claude/agents/test-failure-analyzer.md` — テスト失敗ログの要約と原因推定
- [ ] `.claude/agents/reviewer.md` — 差分レビュー、設計逸脱、テスト不足の指摘
- [ ] (応用) MCP サーバー設定 — Jira / GitHub / Confluence 等
- [ ] 大量の探索結果でメイン会話が汚れる問題が解消したか確認

### 落とし穴

- 最初から MCP を入れる → Phase 5 でいい。それまでは AGENTS.md と Skill で十分
- Subagent を増やしすぎる → 3 つで十分。読み取り専用エージェントが特に有効
- Subagent に強い権限を渡す → 調査エージェントは Read/Grep/Glob 限定

---

## 各 Phase 完了の判定基準

| Phase | 完了の目安 |
|---|---|
| 1 | チームの新メンバーが Claude Code で初日に PR を出せる |
| 2 | AI の完了報告に必ずテスト結果・リスクが含まれる |
| 3 | 同じタスクを 2 人に依頼しても、出力が大きく散らからない |
| 4 | `build.gradle.kts` のような重要ファイルが事故で書き換わらない |
| 5 | 1 つのタスクで探索ログがメイン会話に流れ込まなくなる |

---

## 反パターン (やってはいけないこと)

```
× いきなり Skill を 10 個作る
× CLAUDE.md に何百行も書く
× Hook で本番デプロイを自動化する (危険)
× Subagent に書き込み権限を広く渡す
× Phase 1 をスキップして Phase 3 から始める
```

---

## 導入記録テンプレート (社内共有用)

各サブドメインで導入したら、以下を社内 wiki / Confluence に残す:

```
## サブドメイン名
- 導入日: YYYY-MM-DD
- 導入した Phase: 1 / 2 / 3 / 4 / 5
- 導入要素: AGENTS.md / CLAUDE.md / Skills (どれ) / Hooks / Subagents
- 効果 (1 行): AI の完了報告にテスト結果が必ず入るようになった
- 副作用: なし / Hook が時々遅い
- 次の Phase の予定: YYYY-MM-DD
```

---

## 関連

- 講座本体: `syllabus.md`
- 講師ガイド: `instructor-guide-90min.md`
- ハンズオン: `hands-on.md`
- 比較デモ: `no-harness-vs-harness-demo.md`
