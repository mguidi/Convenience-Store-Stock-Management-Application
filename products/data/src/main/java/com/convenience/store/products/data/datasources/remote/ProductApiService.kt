package com.convenience.store.products.data.datasources.remote

import arrow.core.Either
import com.convenience.store.products.data.models.remote.ProductApiDto
import com.convenience.store.products.data.models.remote.ProductApiError
import java.math.BigDecimal
import java.util.UUID

interface ProductApiService {

    suspend fun createProduct(
        commandId: UUID,
        productId: UUID,
        name: String,
        description: String,
        price: BigDecimal,
        barcode: String,
        categoryId: UUID,
        supplierId: UUID,
    ): Either<ProductApiError, Unit>

    suspend fun updateProduct(
        commandId: UUID,
        productId: UUID,
        name: String,
        description: String,
        price: BigDecimal,
        barcode: String,
        categoryId: UUID,
        supplierId: UUID,
    ): Either<ProductApiError, Unit>

    suspend fun deleteProduct(commandId: UUID, productId: UUID): Either<ProductApiError, Unit>

    suspend fun getProductById(id: UUID): Either<ProductApiError, ProductApiDto>

    suspend fun getProducts(
        categoryId: UUID?,
        barcode: String?,
        page: Int,
        pageSize: Int
    ): Either<ProductApiError, List<ProductApiDto>>
}