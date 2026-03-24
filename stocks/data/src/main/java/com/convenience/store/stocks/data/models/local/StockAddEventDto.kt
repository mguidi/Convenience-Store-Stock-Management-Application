package com.convenience.store.stocks.data.models.local

import com.convenience.store.core.data.serializers.BigDecimalSerializer
import com.convenience.store.core.data.serializers.UuidSerializer
import com.convenience.store.core.domain.events.StockAddEvent
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.util.UUID

@Serializable
internal data class StockAddEventDto(
    @Serializable(with = UuidSerializer::class) val productId: UUID,
    @Serializable(with = BigDecimalSerializer::class) val quantity: BigDecimal,
)

internal fun StockAddEvent.toDto() = StockAddEventDto(
    productId = productId,
    quantity = quantity
)