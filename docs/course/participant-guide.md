# 受講者ガイド — 第 2 回 Harness Engineering 入門

ようこそ。このドキュメントは **講座当日までに目を通しておくもの** です。

---

## 講座の概要

- **テーマ**: Quarkus + Kotlin で学ぶ AI Harness Engineering 入門
- **時間**: 90 分
- **形式**: 解説 + ライブデモ + ハンズオン
- **持ち物**: ノート PC (Mac / Linux / Windows)、Claude Code or Codex CLI

詳細は `syllabus.md` を読んでください。

---

## 事前準備 (開始 30 分前までに完了)

### 1. リポジトリ取得

```bash
git clone <配布される URL>
cd sample-quarkus-app
```

または zip 配布の場合は展開して `cd` で入ってください。

### 2. JDK 確認

```bash
java -version
# JDK 17 または 21 (LTS) を推奨。Quarkus 3.15 は 17/21 で検証済み。
# 非常に新しい JDK (24+) は未検証 — 当日は 17 か 21 を使うこと。
```

入っていなければ [Adoptium](https://adoptium.net/) などからインストール。

### 3. 依存解決とビルド

```bash
./gradlew build
```

初回は依存ダウンロードに 2〜3 分かかります。

### 4. テスト緑確認

```bash
./gradlew test
```

**全テストが緑になることを必ず確認してください**。エラーが出る場合は当日 30 分前までに Slack で連絡を。

### 5. Claude Code または Codex を準備

- Claude Code: `claude` コマンドが動くこと
- Codex: `codex` CLI が動くこと

どちらか片方で OK。両方使う方は両方確認。

### 6. (推奨) plan mode の動作確認

```bash
claude
# 起動後 Shift+Tab で plan mode に切り替わるか確認
```

または `.claude/settings.json` の `defaultMode: "plan"` で自動起動を確認。

---

## 講座中のルール

### 質問の出し方

Meet チャットを使います。テンプレ:

- **質問**: `Q: 質問内容`
- **感想・気づき**: `→ 気づいた内容`
- **困りごと**: `🆘 困っている内容` (講師が優先対応)

### 画面・マイク

- 自由 (顔出し任意)
- マイクはミュート推奨 (講師指名時のみ ON)

### ハンズオンの進め方

- 自分のペースで OK
- 詰まったらチャットへ
- 周りに合わせず、**自分の手で動かす** ことを優先

---

## 講座中に書くもの

各セクションで「あなたが書く」要素があります:

| セクション | 書くもの |
|---|---|
| §4 | AGENTS.md に 1 行追加 (アーキテクチャ規約。**手元エディタで直接編集** — AGENTS.md は Hook 保護対象) |
| §5 | `quarkus-kotlin-feature` Skill の Step 1 改造 |
| §6 末 | Round 1 で見たミスを潰す AGENTS.md の 1 行（チャット投稿） |
| §7 | 自社リポジトリの最初の Harness 要素 (持ち帰り宿題) |

**手元のメモ帳を用意** しておいてください。Meet チャットに貼る場面もあります。

> 📌 **当日の実装範囲**: 価格更新 API（`PATCH /products/{id}/price`）そのものの実装は、§6 で **講師のライブデモを観察** します。**各自が手を動かして実装するのは課後の自習**（`docs/course/hands-on.md` の Path B）です。90 分の当日に手で書くのは §4・§5・§6末 の小さな 3 つだけ。焦らず「ハーネスで AI の挙動がどう変わるか」を見ることを優先してください。

---

## 持ち帰り宿題

講座終了時に各自が持ち帰るもの:

### 1. 自社リポジトリの「最初に置く Harness 要素」を 1 つ決める

候補:
- AGENTS.md (30〜50 行のポインタ型)
- CLAUDE.md (@AGENTS.md import + 数行)
- feature Skill 1 つ
- 危険操作ブロックの Hook 1 つ

### 2. 第 3 回までに 1 PR で導入し、1 行レポートを書く

レポートテンプレ:

```
- サブドメイン: <名前>
- 導入した要素: <AGENTS.md / Skill / etc.>
- 効果: <1 行で>
- 副作用: <なければ「なし」>
```

第 3 回 (社内メインリポジトリへの適用) で発表してもらいます。

---

## 講座後にやってほしいこと

- アンケート回答 (講座最後に URL を共有)
- `adoption-checklist.md` を一読
- 自社リポジトリで実際に書いてみる

---

## トラブル対応

### ビルド失敗

```bash
./gradlew clean build --refresh-dependencies
```

これでも直らなければチャットへ。

### Claude Code が AGENTS.md / 規約を読まない・plan mode で始まらない

- 起動時の cwd が `sample-quarkus-app/` **直下**になっているか確認（Claude Code は CLAUDE.md を読み、その `@AGENTS.md` import で AGENTS.md が入る。cwd がズレると両方読まれない）
- **初回起動の trust(信頼)確認を承認**したか。承認後に project の `.claude/settings.json`（`defaultMode: "plan"`）が効き、plan mode で始まる
- plan mode で始まらないときは **`claude --permission-mode plan`** で起動（最も確実）。または起動後 `Shift+Tab`
- `/memory` で読み込み済みファイル一覧を確認 (Claude Code)
- Codex は AGENTS.md を直接読む（cwd 配下に AGENTS.md があること）

### Hook が遅すぎる

- 一時的に `.claude/hooks/post-tool-use-format.sh` を `.bak` にリネーム
- 講師に申告

---

## 参考資料 (講座前に読んでおくと深まる)

- [Mitchell Hashimoto: My AI adoption journey](https://mitchellh.com/writing/my-ai-adoption-journey) — Harness Engineering の原典
- [Claude Code Docs](https://code.claude.com/docs)
- [Codex Docs — AGENTS.md](https://developers.openai.com/codex/guides/agents-md)
- [Quarkus Kotlin guide](https://quarkus.io/guides/kotlin)

---

## 関連ファイル (sample-quarkus-app 内)

- `docs/course/syllabus.md` — 講座全体
- `docs/course/hands-on.md` — 演習手順 (当日もう一度開く)
- `docs/course/no-harness-vs-harness-demo.md` — 比較デモ手順
- `docs/course/adoption-checklist.md` — 自社導入チェックリスト
- `AGENTS.md` / `CLAUDE.md` — 講座で実物を見るファイル
- `.claude/skills/` / `.agents/skills/` — 講座で実物を見る Skill 群

---

楽しんで参加してください。何かあれば事前に Slack へ。
