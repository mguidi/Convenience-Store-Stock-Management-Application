package com.convenience.store.products.data.datasources.remote

import android.util.Log
import arrow.core.Either
import arrow.core.right
import com.convenience.store.products.data.models.remote.ProductCreateApiDto
import com.convenience.store.products.data.models.remote.ProductApiDto
import com.convenience.store.products.data.models.remote.ProductApiError
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

class ProductApiServiceMockImpl @Inject constructor() : ProductApiService {

    override suspend fun createProduct(productCreateApiDto: ProductCreateApiDto): Either<ProductApiError, Unit> {
        Log.d(
            "ProductApiService",
            "Create product: ${Json.encodeToString(productCreateApiDto)}"
        )
        delay(2000)
        return Either.Right(Unit)
    }

    override suspend fun getProductById(id: UUID): Either<ProductApiError, ProductApiDto> {
        delay(2000)
        return ProductApiDto(
            id = UUID.fromString("4f7edd9a-236a-4921-b720-dbe451646a0f"),
            name = "Wheat Bread",
            description = "Whole wheat sliced bread. No preservatives.",
            price = BigDecimal("3.49"),
            barcode = "9876543210987",
            categoryId = UUID.randomUUID(),
            supplierId = UUID.randomUUID(),
            version = 0
        ).right()
    }

    override suspend fun getProducts(): Either<ProductApiError, List<ProductApiDto>> {
        delay(2000)
        val initialProducts = listOf(
            ProductApiDto(
                id = UUID.fromString("019d1d3d-ed99-7be7-9e8c-d74fd0d83d97"),
                name = "Organic Milk",
                description = "Fresh organic whole milk from local farms. 1 Gallon.",
                price = BigDecimal("4.99"),
                barcode = "1234567890123",
                categoryId = UUID.randomUUID(),
                supplierId = UUID.randomUUID(),
                version = 0
            ),
            ProductApiDto(
                id = UUID.fromString("4f7edd9a-236a-4921-b720-dbe451646a0f"),
                name = "Wheat Bread",
                description = "Whole wheat sliced bread. No preservatives.",
                price = BigDecimal("3.49"),
                barcode = "9876543210987",
                categoryId = UUID.randomUUID(),
                supplierId = UUID.randomUUID(),
                version = 0
            )
        )
        return initialProducts.right()
    }
}