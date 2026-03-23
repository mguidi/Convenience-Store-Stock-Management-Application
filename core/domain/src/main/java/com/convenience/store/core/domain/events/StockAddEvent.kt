package com.convenience.store.core.domain.events

import java.math.BigDecimal
import java.util.UUID

data class StockAddEvent(
    val productId: UUID,
    val quantity: BigDecimal,
) {
    companion object {
        const val NAME = "StockAddEvent"
    }
}
