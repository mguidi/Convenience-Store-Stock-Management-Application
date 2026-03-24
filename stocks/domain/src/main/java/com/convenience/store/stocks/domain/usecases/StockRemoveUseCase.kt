package com.convenience.store.stocks.domain.usecases

import arrow.core.Either
import arrow.core.left
import com.convenience.store.stocks.domain.entities.StockError
import com.convenience.store.stocks.domain.repositories.StockRepository
import com.convenience.store.stocks.domain.services.StockSyncService
import java.math.BigDecimal
import java.util.UUID

interface StockRemoveUseCase {
    suspend operator fun invoke(productId: UUID, quantity: BigDecimal): Either<StockError, Unit>
}

class StockRemoveUseCaseImpl(
    private val stockRepository: StockRepository,
    private val stockSyncService: StockSyncService
) : StockRemoveUseCase {


    override suspend fun invoke(
        productId: UUID,
        quantity: BigDecimal
    ): Either<StockError, Unit> {
        if (quantity < BigDecimal.ZERO) return StockError.ValidationError.InvalidQuantity.left()

        return stockRepository.removeStock(productId, quantity).onRight {
            stockSyncService.scheduleSync()
        }
    }
}