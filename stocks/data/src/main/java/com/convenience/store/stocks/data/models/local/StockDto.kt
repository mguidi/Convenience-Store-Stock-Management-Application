package com.convenience.store.stocks.data.models.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.convenience.store.stocks.domain.entities.Stock
import java.math.BigDecimal
import java.util.UUID

@Entity(tableName = "stocks")
data class StockDto(
    @PrimaryKey val productId: UUID,
    val quantity: BigDecimal,
    val version: Long
)


fun StockDto.toDomain() = Stock(
    productId = productId,
    quantity = quantity,
    version = version
)

//fun Stock.toEntity() = StockEntity(
//    productId = this.productId,
//    quantity = this.quantity
//)
