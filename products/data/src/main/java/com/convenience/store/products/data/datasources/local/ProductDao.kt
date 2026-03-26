package com.convenience.store.products.data.datasources.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.convenience.store.products.data.models.local.ProductDto
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface ProductDao {

    @Insert
    suspend fun insert(product: ProductDto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(product: ProductDto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAll(products: List<ProductDto>)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteById(id: UUID)

    @Query("SELECT * FROM products WHERE id = :productId")
    fun getProductById(productId: UUID): Flow<ProductDto?>

    @Query("SELECT * FROM products WHERE barcode = :barcode")
    fun getProductByBarcode(barcode: String): Flow<ProductDto?>

    @Query("SELECT * FROM products WHERE categoryId = :categoryId ORDER by id ASC")
    fun getProductsByCategoryPaged(categoryId: UUID): PagingSource<Int, ProductDto>

    @Query("SELECT * FROM products ORDER by id ASC")
    fun getProductsPaged(): PagingSource<Int, ProductDto>
}