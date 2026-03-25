package com.convenience.store.stocks.data.datasources.remote

import android.util.Log
import arrow.core.Either
import arrow.core.right
import com.convenience.store.stocks.data.models.remote.StockAddApiDto
import com.convenience.store.stocks.data.models.remote.StockApiDto
import com.convenience.store.stocks.data.models.remote.StockApiError
import com.convenience.store.stocks.data.models.remote.StockRemoveApiDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject
import kotlin.concurrent.withLock

class StockApiServiceMockImpl @Inject constructor() : StockApiService {

    private val _stockMap = mutableMapOf<UUID, BigDecimal>()
    private val _mutex = Mutex()


    override suspend fun addStock(stockAddApiDto: StockAddApiDto): Either<StockApiError, Unit> {
        Log.d(
            "StockApiService",
            "Add stock ${Json.encodeToString(stockAddApiDto)}"
        )
        delay(2000)

        _mutex.withLock {
            val quantity = _stockMap[stockAddApiDto.productId] ?: BigDecimal.ZERO
            _stockMap[stockAddApiDto.productId] = quantity + stockAddApiDto.quantity
        }

        return Unit.right()
    }

    override suspend fun removeStock(stockRemoveApiDto: StockRemoveApiDto): Either<StockApiError, Unit> {
        Log.d(
            "StockApiService",
            "Remove stock ${Json.encodeToString(stockRemoveApiDto)}"
        )
        delay(2000)

        _mutex.withLock {
            val quantity = _stockMap[stockRemoveApiDto.productId] ?: BigDecimal.ZERO
            _stockMap[stockRemoveApiDto.productId] = quantity - stockRemoveApiDto.quantity
        }

        return Unit.right()
    }

    override suspend fun getStockByProductId(productId: UUID): Either<StockApiError, StockApiDto> {
        delay(2000)
        val quantity = _stockMap[productId] ?: BigDecimal.ZERO
        return StockApiDto(productId, quantity).right()
    }
}