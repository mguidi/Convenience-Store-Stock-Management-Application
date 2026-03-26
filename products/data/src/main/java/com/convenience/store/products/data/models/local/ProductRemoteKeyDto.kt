package com.convenience.store.products.data.models.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "product_remote_keys")
data class ProductRemoteKeyDto(
    @PrimaryKey val productId: UUID,
    val prevKey: Int?,
    val nextKey: Int?,
)