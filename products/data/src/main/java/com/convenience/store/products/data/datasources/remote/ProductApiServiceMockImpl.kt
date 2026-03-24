package com.convenience.store.products.data.datasources.remote

import android.util.Log
import arrow.core.Either
import com.convenience.store.products.data.models.remote.ProductAddApiDto
import com.convenience.store.products.data.models.remote.ProductApiError
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ProductApiServiceMockImpl @Inject constructor() : ProductApiService {

    override suspend fun addProduct(productAddApiDto: ProductAddApiDto): Either<ProductApiError, Unit> {
        Log.d(
            "ProductApiService",
            "Add product: ${Json.encodeToString(productAddApiDto)}"
        )
        delay(2000)
        return Either.Right(Unit)
    }
}