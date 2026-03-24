package com.convenience.store.stocks.data.datasources

import android.util.Log
import arrow.core.Either
import arrow.core.right
import com.convenience.store.stocks.data.models.StockApiDto
import com.convenience.store.stocks.data.models.StockApiError
import kotlinx.coroutines.delay
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

class StockApiServiceMockImpl @Inject constructor(
) : StockApiService {

    override suspend fun addStock(
        productId: UUID,
        quantity: BigDecimal
    ): Either<StockApiError, Unit> {
        Log.d("StockApiService", "Add stock for product $productId by $quantity")
        delay(2000)
        return Unit.right()
    }

    override suspend fun removeStock(
        productId: UUID,
        quantity: BigDecimal
    ): Either<StockApiError, Unit> {
        Log.d("StockApiService", "Remove stock for product $productId by $quantity")
        delay(2000)
        return Unit.right()
    }

    override suspend fun getStockByProductId(productId: UUID): Either<StockApiError, StockApiDto> {
        return StockApiDto(productId, BigDecimal.TEN).right()
    }
}