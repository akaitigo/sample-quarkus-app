package com.example.products.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 価格更新リクエスト（ハンズオン用に scaffold として提供済み）。
 *
 * 注: 単一プロパティの data class は jackson-module-kotlin が「委譲コンストラクタ」と
 * 誤解し、`{"price":150}` ではなく裸の値 `150` を期待してしまう既知の罠がある
 * （結果として正しいリクエストが 400 になる）。`@JsonCreator(mode = PROPERTIES)` +
 * `@JsonProperty` でプロパティバインドを明示してこれを回避している。
 * 詳細は docs/ai/architecture.md「既知の落とし穴」を参照。
 */
data class PriceUpdateRequest @JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
    @JsonProperty("price") val price: Int,
)
