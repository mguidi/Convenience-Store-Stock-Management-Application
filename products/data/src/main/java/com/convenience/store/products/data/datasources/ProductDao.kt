package com.convenience.store.products.data.datasources

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.convenience.store.products.data.models.ProductDto
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface ProductDao {

    @Insert
    suspend fun insert(product: ProductDto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProduct(product: ProductDto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductDto>)

    @Query("SELECT * FROM products WHERE id = :productId")
    fun getProductById(productId: UUID): Flow<ProductDto?>

    @Query("SELECT * FROM products WHERE barcode = :barcode")
    fun getProductByBarcode(barcode: String): Flow<ProductDto?>

    @Query("SELECT * FROM products WHERE categoryId = :categoryId")
    fun getProductsByCategoryPaged(categoryId: UUID): PagingSource<Int, ProductDto>

    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getProductsPaged(): PagingSource<Int, ProductDto>
}
