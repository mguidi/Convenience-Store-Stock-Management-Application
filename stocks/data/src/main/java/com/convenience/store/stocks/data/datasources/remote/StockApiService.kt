package com.convenience.store.stocks.data.datasources.remote

import arrow.core.Either
import com.convenience.store.stocks.data.models.remote.StockAddApiDto
import com.convenience.store.stocks.data.models.remote.StockApiDto
import com.convenience.store.stocks.data.models.remote.StockApiError
import com.convenience.store.stocks.data.models.remote.StockRemoveApiDto
import java.util.UUID


interface StockApiService {
    suspend fun addStock(stockAddApiDto: StockAddApiDto): Either<StockApiError, Unit>

    suspend fun removeStock(stockRemoveApiDto: StockRemoveApiDto): Either<StockApiError, Unit>

    suspend fun getStockByProductId(productId: UUID): Either<StockApiError, StockApiDto>
}
