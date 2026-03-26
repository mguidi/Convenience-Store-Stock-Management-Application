package com.convenience.store.products.data.models.remote

import com.convenience.store.core.data.serializers.UuidSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ProductDeleteApiDto(
    @Serializable(with = UuidSerializer::class) val commandId: UUID,
    @Serializable(with = UuidSerializer::class) val id: UUID,
)