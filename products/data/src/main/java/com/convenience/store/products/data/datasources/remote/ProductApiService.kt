package com.convenience.store.products.data.datasources.remote

import arrow.core.Either
import com.convenience.store.products.data.models.remote.ProductCreateApiDto
import com.convenience.store.products.data.models.remote.ProductApiDto
import com.convenience.store.products.data.models.remote.ProductApiError
import com.convenience.store.products.domain.entities.Product
import java.util.UUID

interface ProductApiService {

    suspend fun createProduct(commandId: UUID, product: Product): Either<ProductApiError, Unit>

    suspend fun updateProduct(commandId: UUID, product: Product): Either<ProductApiError, Unit>

    suspend fun deleteProduct(commandId: UUID, productId: UUID): Either<ProductApiError, Unit>

    suspend fun getProductById(id: UUID): Either<ProductApiError, ProductApiDto>

    suspend fun getProducts(page: Int, pageSize: Int): Either<ProductApiError, List<ProductApiDto>>

    suspend fun getProductsByCategoryId(categoryId: UUID, page: Int, pageSize: Int): Either<ProductApiError, List<ProductApiDto>>
}