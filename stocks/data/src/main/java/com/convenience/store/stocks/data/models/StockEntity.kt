package com.convenience.store.stocks.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.util.UUID

@Entity(tableName = "stocks")
data class StockEntity(
    @PrimaryKey val productId: UUID,
    val quantity: BigDecimal,
)
