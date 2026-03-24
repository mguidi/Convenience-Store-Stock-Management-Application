package com.convenience.store.stocks.domain.repositories

import arrow.core.Either
import com.convenience.store.stocks.domain.entities.Stock
import com.convenience.store.stocks.domain.entities.StockError
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.util.UUID

/**
 * Repository interface for managing inventory and stock operations.
 * This interface abstracts the data source for adding or removing product quantities.
 * It uses Arrow's Either type to provide functional error handling.
 */
interface StockRepository {

    fun getStockById(productId: UUID) : Flow<Stock?>

    /**
     * Increases the available stock for a specific product.
     *
     * @param productId The unique identifier (UUID) of the product.
     * @param quantity The amount to be added to the current stock.
     * @return An [Either] containing a [StockError] on failure or [Unit] on success.
     */
    suspend fun addStock(productId: UUID, quantity: BigDecimal): Either<StockError, Unit>

    /**
     * Decreases the available stock for a specific product.
     *
     * @param productId The unique identifier (UUID) of the product.
     * @param quantity The amount to be removed from the current stock.
     * @return An [Either] containing a [StockError] on failure or [Unit] on success.
     */
    suspend fun removeStock(productId: UUID, quantity: BigDecimal): Either<StockError, Unit>
}