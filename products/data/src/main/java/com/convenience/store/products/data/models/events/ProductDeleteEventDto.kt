package com.convenience.store.products.data.models.events

import com.convenience.store.core.data.serializers.UuidSerializer
import com.convenience.store.core.domain.events.ProductDeleteEvent
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
internal data class ProductDeleteEventDto(
    @Serializable(with = UuidSerializer::class) val id: UUID,
)

internal fun ProductDeleteEvent.toDto() = ProductDeleteEventDto(
    id = id,
)
