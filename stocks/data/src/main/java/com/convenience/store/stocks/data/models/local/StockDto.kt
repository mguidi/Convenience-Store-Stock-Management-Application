package com.convenience.store.stocks.data.models.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.convenience.store.stocks.domain.entities.Stock
import java.math.BigDecimal
import java.util.UUID

@Entity(tableName = "stocks")
data class StockDto(
    @PrimaryKey val productId: UUID,
    val quantitySync: BigDecimal,
    val quantityNotSync: BigDecimal,
)


fun StockDto.toDomain() = Stock(
    productId = productId,
    quantitySync = quantitySync,
    quantityNotSync = quantityNotSync
)
