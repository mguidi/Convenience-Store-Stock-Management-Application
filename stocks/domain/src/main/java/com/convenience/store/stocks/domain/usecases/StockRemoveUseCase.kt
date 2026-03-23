package com.convenience.store.stocks.domain.usecases

import arrow.core.Either
import com.convenience.store.stocks.domain.entities.StockError
import com.convenience.store.stocks.domain.repositories.StockRepository
import java.math.BigDecimal
import java.util.UUID

interface StockRemoveUseCase {
    suspend operator fun invoke(productId: UUID, quantity: BigDecimal): Either<StockError, Unit>
}

class StockRemoveUseCaseImpl(
    private val stockRepository: StockRepository
) : StockRemoveUseCase {


    override suspend fun invoke(
        productId: UUID,
        quantity: BigDecimal
    ): Either<StockError, Unit> {
        if (quantity < BigDecimal.ZERO) return Either.Left(StockError.InvalidQuantity)

        return stockRepository.removeStock(productId, quantity)
    }
}