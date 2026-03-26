package com.convenience.store.stocks.data.datasources.remote

import arrow.core.Either
import com.convenience.store.stocks.data.models.remote.StockApiDto
import com.convenience.store.stocks.data.models.remote.StockApiError
import java.math.BigDecimal
import java.util.UUID


interface StockApiService {
    suspend fun addStock(
        requestId: UUID,
        productId: UUID,
        quantity: BigDecimal
    ): Either<StockApiError, Unit>

    suspend fun removeStock(
        requestId: UUID,
        productId: UUID,
        quantity: BigDecimal
    ): Either<StockApiError, Unit>

    suspend fun getStockByProductId(productId: UUID): Either<StockApiError, StockApiDto>
}
