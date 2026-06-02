# Quarkus + Kotlin で学ぶ AI Harness Engineering 入門 — シラバス

**副題**: AI に安全・再現可能に実装させるためのリポジトリ設計

**回数**: Claude Code 教育 第 2 回

**時間**: 90 分

---

## 中心メッセージ

> AI 活用 = チャットで依頼することではない。
>
> AI 活用 = AI が作業できる **文脈・制約・検証・再利用手順** をリポジトリに埋め込むこと。

AI の性能差だけで成果は決まらない。成果を決めるのは、AI に渡す **文脈**、AI が使える **道具**、AI が従う **制約**、AI の出力を **検証する仕組み**である。本講座は、それらをリポジトリに埋め込む技術である **Harness Engineering** を、Quarkus + Kotlin の実例を通して学ぶ。

---

## 対象者

- Quarkus + Kotlin を使うバックエンド開発者
- Claude Code / Codex を業務で使い始めたい、または使い始めたばかりの方
- AGENTS.md / CLAUDE.md / Skills / Hooks / Subagents の **体系的な使い方** をまだ十分理解していない方
- 自社リポジトリへの導入計画を立てたい方 (PMO / リード含む)

## 前提

- Kotlin で簡単な API を読み書きできる
- Gradle ビルドの基本概念がわかる
- 「プロンプトの書き方」だけでは AI 出力が安定しない経験がある (歓迎)

---

## 到達目標

講座後に、以下ができる状態を目指す。

1. **AGENTS.md** を自分のリポジトリに書ける (Coding Agent 共通の文脈ブリーフ)
2. **CLAUDE.md** を Claude Code 用に書き分けられる (ポインタ型・薄く保つ)
3. **Skill** を3つの軸 (feature / test / review) で設計できる
4. AI に守らせる **設計制約**・**完了条件** を明文化できる
5. テスト・レビュー・リスク報告を AI 作業の **標準フロー** に組み込める
6. 自社リポジトリへ **段階的に導入する計画** (Phase 1〜5) を立てられる

---

## 90 分タイムテーブル

| 時間 | セクション | 目的 |
|---|---|---|
| 10 分 | **§1. Harness Engineering の概念** | プロンプト術ではなく「AI が働ける環境設計」と理解する |
| 10 分 | **§2. Claude / Codex 公式機能の位置づけ** | AGENTS.md / CLAUDE.md / Skills / Hooks / Subagents の全体像 |
| 15 分 | **§3. 題材リポジトリ説明** | 商品マスタ API (Quarkus + Kotlin) の構造を理解 |
| 15 分 | **§4. AGENTS.md / CLAUDE.md 作成** | リポジトリ固有の文脈を AI に渡す方法 |
| 15 分 | **§5. Skill 作成** | 繰り返し作業を再利用可能な手順にする |
| 15 分 | **§6. ハーネスなし / あり 比較デモ** | 効果を実感する (同一リポジトリで段階整備) |
| 10 分 | **§7. 自社リポジトリへの導入手順** | Phase 1〜5 ロードマップで現場導入のイメージ |

---

## 事前準備

開催の **30 分前まで** に以下を完了:

1. リポジトリ取得: `git clone <URL>` または zip 展開
2. 依存解決: `./gradlew build`
3. テスト実行確認: `./gradlew test` (緑になること)
4. Claude Code または Codex CLI が動くことを確認

詳細は `participant-guide.md` を参照。

---

## 講座フォーマット

- 各セクションは **解説 → デモ → 受講者ハンズオン** の流れ
- ハンズオン中は **チャット (Meet / Slack)** で質問可
- §6 比較デモは講師が画面共有で実演 (受講者は手元のリポジトリで再現可能)
- 最後の 10 分は **持ち帰りワーク** の設計時間

---

## 持ち帰り宿題

講座終了時に各自が持ち帰るもの:

1. **自社リポジトリの「最初に置く Harness 要素」を 1 つ決める**
   - 候補: AGENTS.md / CLAUDE.md / feature Skill / Hook の最小例
2. 次回 (第 3 回) までに 1 PR で導入し、効果を 1 行レポートにまとめる

第 3 回 (社内メインリポジトリへの適用) で発表する想定。

---

## 参考資料 (公式・一次情報)

### Anthropic / Claude Code
- [Anthropic Academy: Claude Code 101](https://anthropic.skilljar.com/claude-code-101)
- [Claude Code Docs](https://code.claude.com/docs)
- [Memory (CLAUDE.md / AGENTS.md import)](https://code.claude.com/docs/en/memory)
- [Permission modes (plan mode 既定化)](https://code.claude.com/docs/en/permission-modes)
- [Skills](https://code.claude.com/docs/en/skills)
- [Hooks](https://code.claude.com/docs/en/hooks)
- [Subagents](https://code.claude.com/docs/en/sub-agents)
- [Settings](https://code.claude.com/docs/en/settings)

### OpenAI / Codex
- [Codex Docs — AGENTS.md](https://developers.openai.com/codex/guides/agents-md)
- [Codex Docs — Agent Skills](https://developers.openai.com/codex/skills)
- [Codex 開発者ハブ](https://developers.openai.com/codex/)
- [AGENTS.md オープン標準](https://agents.md/)

### Quarkus / Kotlin
- [Quarkus Kotlin guide](https://quarkus.io/guides/kotlin)
- [Quarkus testing guide](https://quarkus.io/guides/getting-started-testing)
- [Quarkus continuous testing](https://quarkus.io/guides/continuous-testing)

### Harness Engineering (概念)
- [Mitchell Hashimoto: My AI adoption journey (2026-02-05)](https://mitchellh.com/writing/my-ai-adoption-journey) — *"engineer a solution such that the agent never makes that mistake again"*
- [nyosegawa: Harness Engineering ベストプラクティス 7 章](https://nyosegawa.com/posts/harness-engineering-best-practices-2026.md) — 2 次情報 (Hashimoto + Anthropic + OpenAI の整理)

---

## 用語集

| 用語 | 意味 |
|---|---|
| **Harness Engineering** | AI エージェントが安全・一貫・検証可能に開発できる環境を、リポジトリに埋め込む技術 |
| **AGENTS.md** | Coding Agent 共通の文脈ブリーフ (Claude / Codex / Cursor / Aider 等が読む) |
| **CLAUDE.md** | Claude Code 専用の追加ブリーフ。AGENTS.md を import し、Claude 固有ルールだけ薄く書く |
| **Skill** | 繰り返し手順をパッケージ化した再利用可能な作業手順 (`SKILL.md`) |
| **Hook** | エディタ操作の前後に自動実行されるスクリプト (PreToolUse / PostToolUse) |
| **Subagent** | 役割別に分業する子エージェント (調査専門 / レビュー専門など) |
| **DoD (Definition of Done)** | 完了条件。テスト緑・レビュー観点クリア・リスク報告までを含む |
| **Phase 1〜5** | 自社リポジトリへの段階的導入ロードマップ |

---

## 講師ガイド・受講者ガイド

- 講師向け: `instructor-guide-90min.md`
- 受講者向け: `participant-guide.md`
- 演習手順: `hands-on.md`
- 比較デモ: `no-harness-vs-harness-demo.md`
- 自社導入: `adoption-checklist.md`
