package com.convenience.store.products.data.datasources.remote

import arrow.core.Either
import com.convenience.store.products.data.models.remote.ProductAddApiDto
import com.convenience.store.products.data.models.remote.ProductApiError

interface ProductApiService {

    suspend fun addProduct(productAddApiDto: ProductAddApiDto): Either<ProductApiError, Unit>


}