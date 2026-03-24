package com.convenience.store.products.domain.usecases

import androidx.paging.PagingData
import com.convenience.store.products.domain.entities.Product
import com.convenience.store.products.domain.repositories.ProductRepository
import kotlinx.coroutines.flow.Flow

interface ProductsGetUseCase {

    fun invoke(): Flow<PagingData<Product>>

}

class ProductsGetUseCaseImpl(
    private val productRepository: ProductRepository
) : ProductsGetUseCase {

    override fun invoke(): Flow<PagingData<Product>> {
        return productRepository.getProductsStream()
    }
}