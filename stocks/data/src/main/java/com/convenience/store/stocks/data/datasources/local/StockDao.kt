package com.convenience.store.stocks.data.datasources.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.convenience.store.stocks.data.models.local.StockDto
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.util.UUID

/**
 * Data Access Object (DAO) for the stocks table.
 */
@Dao
interface StockDao {

    /**
     * Inserts or updates a stock entry.
     *
     * @param stock The stock entity to be saved.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStock(stock: StockDto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<StockDto>)

    /**
     * Retrieves the stock for a specific product.
     *
     * @param productId The unique identifier of the product.
     * @return The [StockDto] or null if not found.
     */
    @Query("SELECT * FROM stocks WHERE productId = :productId")
    fun getStockByProductId(productId: UUID): Flow<StockDto?>

    /**
     * Updates the stock quantity sync for a specific product.
     *
     * @param productId The unique identifier of the product.
     * @param newQuantity The new quantity
     * @return The number of rows updated (1 if successful, 0 if condition not met).
     */
    @Query("UPDATE stocks SET quantitySync = :newQuantity WHERE productId = :productId")
    suspend fun updateStockQuantitySync(productId: UUID, newQuantity: BigDecimal): Int

    /**
     * Updates the stock quantity sync for a specific product.
     *
     * @param productId The unique identifier of the product.
     * @param quantityChange The amount to add (positive) or subtract (negative).
     * @return The number of rows updated (1 if successful, 0 if condition not met).
     */
    @Query("UPDATE stocks SET quantitySync = quantitySync + :quantityChange WHERE productId = :productId")
    suspend fun addStockQuantitySync(productId: UUID, quantityChange: BigDecimal): Int

    /**
     * Updates the stock quantity not sync for a specific product.
     *
     * @param productId The unique identifier of the product.
     * @param quantityChange The amount to add (positive) or subtract (negative).
     * @return The number of rows updated (1 if successful, 0 if condition not met).
     */
    @Query("UPDATE stocks SET quantityNotSync = quantityNotSync + :quantityChange WHERE productId = :productId")
    suspend fun addStockQuantityNotSync(productId: UUID, quantityChange: BigDecimal): Int


}