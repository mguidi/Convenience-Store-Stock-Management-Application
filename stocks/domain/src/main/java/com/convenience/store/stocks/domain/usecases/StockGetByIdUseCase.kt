package com.convenience.store.stocks.domain.usecases

import com.convenience.store.stocks.domain.entities.Stock
import com.convenience.store.stocks.domain.repositories.StockRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface StockGetByIdUseCase {

    operator fun invoke(productId: UUID): Flow<Stock?>

}

class StockGetByIdUseCaseImpl(
    private val stockRepository: StockRepository
) : StockGetByIdUseCase {

    override fun invoke(productId: UUID): Flow<Stock?> {
        return stockRepository.getStockById(productId)
    }
}
