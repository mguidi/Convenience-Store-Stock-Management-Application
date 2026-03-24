package com.convenience.store.stocks.data.datasources.remote

import android.util.Log
import arrow.core.Either
import arrow.core.right
import com.convenience.store.stocks.data.models.remote.StockAddApiDto
import com.convenience.store.stocks.data.models.remote.StockApiDto
import com.convenience.store.stocks.data.models.remote.StockApiError
import com.convenience.store.stocks.data.models.remote.StockRemoveApiDto
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

class StockApiServiceMockImpl @Inject constructor() : StockApiService {

    override suspend fun addStock(stockAddApiDto: StockAddApiDto): Either<StockApiError, Unit> {
        Log.d(
            "StockApiService",
            "Add stock ${Json.encodeToString(stockAddApiDto)}"
        )
        delay(2000)
        return Unit.right()
    }

    override suspend fun removeStock(stockRemoveApiDto: StockRemoveApiDto): Either<StockApiError, Unit> {
        Log.d(
            "StockApiService",
            "Remove stock ${Json.encodeToString(stockRemoveApiDto)}"
        )
        delay(2000)
        return Unit.right()
    }

    override suspend fun getStockByProductId(productId: UUID): Either<StockApiError, StockApiDto> {
        delay(2000)
        return StockApiDto(productId, BigDecimal.TEN, 0).right()
    }
}