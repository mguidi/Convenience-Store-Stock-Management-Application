package com.convenience.store.stocks.data.models

import com.convenience.store.core.data.serializers.BigDecimalSerializer
import com.convenience.store.core.data.serializers.UuidSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.util.UUID

@Serializable
data class StockApiDto(
    @Serializable(with = UuidSerializer::class) val productId: UUID,
    @Serializable(with = BigDecimalSerializer::class) val quantity: BigDecimal,
)
