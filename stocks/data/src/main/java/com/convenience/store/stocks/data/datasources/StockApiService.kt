package com.convenience.store.stocks.data.datasources

import arrow.core.Either
import com.convenience.store.stocks.data.models.StockApiDto
import com.convenience.store.stocks.data.models.StockApiError
import java.math.BigDecimal
import java.util.UUID


interface StockApiService {
    suspend fun addStock(
        productId: UUID,
        quantity: BigDecimal
    ): Either<StockApiError, Unit>

    suspend fun removeStock(
        productId: UUID,
        quantity: BigDecimal
    ): Either<StockApiError, Unit>

    suspend fun getStockByProductId(productId: UUID): Either<StockApiError, StockApiDto>
}
