package com.example.products.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 価格更新リクエスト（ハンズオン用に scaffold として提供済み）。
 *
 * 注: 単一プロパティの data class は Quarkus の Jackson 統合下で `{"price":150}` を
 * 正しくプロパティバインドできず（単一引数コンストラクタが裸の値を期待する挙動になり）、
 * 正しいリクエストが 400 になる既知の罠がある（実機検証済）。
 * `@JsonCreator(mode = PROPERTIES)` + `@JsonProperty` でプロパティバインドを明示し回避している。
 * 詳細は docs/ai/architecture.md「既知の落とし穴」を参照。
 */
data class PriceUpdateRequest @JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
    @JsonProperty("price") val price: Int,
)
