package com.convenience.store.products.domain.repositories

import androidx.paging.PagingData
import arrow.core.Either
import com.convenience.store.products.domain.entities.Product
import com.convenience.store.products.domain.entities.ProductError
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface ProductRepository {

    suspend fun insert(product: Product): Either<ProductError.RepositoryError, Unit>

    suspend fun update(product: Product): Either<ProductError.RepositoryError, Unit>

    fun getProductById(productId: UUID): Flow<Product?>

    fun getProductByBarcode(barcode: String): Flow<Product?>

    fun getProducts(): Flow<PagingData<Product>>

    fun getProductsByCategory(categoryId: UUID): Flow<PagingData<Product>>
}
