package com.convenience.store.core.domain.events

import java.util.UUID

data class ProductDeleteEvent(
    val id: UUID,
) {
    companion object {
        const val NAME = "ProductDeleteEvent"
    }
}
