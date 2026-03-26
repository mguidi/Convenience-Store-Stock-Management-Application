package com.convenience.store.products.domain.entities

import java.math.BigDecimal
import java.util.UUID

data class Product(
    val id: UUID,
    val name: String,
    val description: String,
    val price: BigDecimal,
    val barcode: String,
    val categoryId: UUID,
    val supplierId: UUID,
    val synced: Boolean
)