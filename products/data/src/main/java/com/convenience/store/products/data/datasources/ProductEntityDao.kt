package com.convenience.store.products.data.datasources

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.convenience.store.products.data.models.ProductEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface ProductEntityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProduct(productEntity: ProductEntity)

    @Query("SELECT * FROM products WHERE id = :productId")
    fun getProductById(productId: UUID): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE barcode = :barcode")
    fun getProductByBarcode(barcode: String): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE categoryId = :categoryId")
    fun getProductsByCategoryPaged(categoryId: UUID): PagingSource<Int, ProductEntity>

    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getProductsPaged(): PagingSource<Int, ProductEntity>
}
