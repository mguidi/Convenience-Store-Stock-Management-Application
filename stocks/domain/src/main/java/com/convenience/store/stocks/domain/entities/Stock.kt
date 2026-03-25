package com.convenience.store.stocks.domain.entities

import java.math.BigDecimal
import java.util.UUID

data class Stock(
    val productId: UUID,
    val quantitySync: BigDecimal,
    val quantityNotSync: BigDecimal
) {
    val quantity = quantitySync + quantityNotSync
}