package com.convenience.store.products.domain.usecases

import androidx.paging.PagingData
import com.convenience.store.products.domain.entities.Product
import com.convenience.store.products.domain.repositories.ProductRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface ProductsGetsByCategoryUseCase {

    operator fun invoke(categoryId: UUID): Flow<PagingData<Product>>

}

class ProductsGetsByCategoryUseCaseImpl(
    private val productRepository: ProductRepository
) : ProductsGetsByCategoryUseCase {

    override fun invoke(categoryId: UUID): Flow<PagingData<Product>> {
        return productRepository.getProductsByCategoryStream(categoryId)
    }
}