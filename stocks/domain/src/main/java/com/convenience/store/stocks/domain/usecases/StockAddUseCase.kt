package com.convenience.store.stocks.domain.usecases

import arrow.core.Either
import com.convenience.store.stocks.domain.entities.StockError
import com.convenience.store.stocks.domain.repositories.StockRepository
import java.math.BigDecimal
import java.util.UUID

interface StockAddUseCase {
    suspend operator fun invoke(productId: UUID, quantity: BigDecimal): Either<StockError, Unit>
}

class StockAddUseCaseImpl(
    private val stockRepository: StockRepository
) : StockAddUseCase {


    override suspend fun invoke(
        productId: UUID,
        quantity: BigDecimal
    ): Either<StockError, Unit> {
        if (quantity < BigDecimal.ZERO) return Either.Left(StockError.ValidationError.InvalidQuantity)

        return stockRepository.addStock(productId, quantity)
    }
}