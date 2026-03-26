package com.convenience.store.products.data.models.events

import com.convenience.store.core.data.serializers.BigDecimalSerializer
import com.convenience.store.core.data.serializers.UuidSerializer
import com.convenience.store.core.domain.events.ProductUpdateEvent
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.util.UUID

@Serializable
internal data class ProductUpdateEventDto(
    @Serializable(with = UuidSerializer::class) val id: UUID,
    val name: String,
    val description: String,
    @Serializable(with = BigDecimalSerializer::class) val price: BigDecimal,
    val barcode: String,
    @Serializable(with = UuidSerializer::class) val categoryId: UUID,
    @Serializable(with = UuidSerializer::class) val supplierId: UUID
)

internal fun ProductUpdateEvent.toDto() = ProductUpdateEventDto(
    id = id,
    name = name,
    description = description,
    price = price,
    barcode = barcode,
    categoryId = categoryId,
    supplierId = supplierId
)
