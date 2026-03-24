package com.convenience.store.products.data.datasources.remote

import arrow.core.Either
import com.convenience.store.products.data.models.remote.ProductAddApiDto
import com.convenience.store.products.data.models.remote.ProductApiDto
import com.convenience.store.products.data.models.remote.ProductApiError
import java.util.UUID

interface ProductApiService {

    suspend fun addProduct(productAddApiDto: ProductAddApiDto): Either<ProductApiError, Unit>

    suspend fun getProductById(id: UUID): Either<ProductApiError, ProductApiDto>

    suspend fun getProducts(): Either<ProductApiError, List<ProductApiDto>>
}