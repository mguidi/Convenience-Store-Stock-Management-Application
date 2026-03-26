package com.convenience.store.core.domain.events

import java.math.BigDecimal
import java.util.UUID

data class ProductUpdateEvent(
    val id: UUID,
    val name: String,
    val description: String,
    val price: BigDecimal,
    val barcode: String,
    val categoryId: UUID,
    val supplierId: UUID,
) {
    companion object {
        const val NAME = "ProductUpdateEvent"
    }
}
