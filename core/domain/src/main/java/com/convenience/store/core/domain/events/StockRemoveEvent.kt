package com.convenience.store.core.domain.events

import java.math.BigDecimal
import java.util.UUID

data class StockRemoveEvent(
    val productId: UUID,
    val quantity: BigDecimal,
) {
    companion object {
        val NAME= "StockRemoveEvent"
    }
}
