package com.convenience.store.products.data.models.remote

import com.convenience.store.core.data.serializers.BigDecimalSerializer
import com.convenience.store.core.data.serializers.UuidSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.util.UUID

@Serializable
data class ProductCreateApiDto(
    @Serializable(with = UuidSerializer::class) val requestId: UUID,
    @Serializable(with = UuidSerializer::class) val id: UUID,
    val name: String,
    val description: String,
    @Serializable(with = BigDecimalSerializer::class) val price: BigDecimal,
    val barcode: String,
    @Serializable(with = UuidSerializer::class) val categoryId: UUID,
    @Serializable(with = UuidSerializer::class) val supplierId: UUID
)