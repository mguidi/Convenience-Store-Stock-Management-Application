package com.convenience.store.products.domain.usecases

import com.convenience.store.products.domain.entities.Product
import com.convenience.store.products.domain.repositories.ProductRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface ProductGetUseCase {

    operator fun invoke(productId: UUID): Flow<Product?>

}

class ProductGetUseCaseImpl(
    private val productRepository: ProductRepository
) : ProductGetUseCase {

    override fun invoke(productId: UUID): Flow<Product?> {
        return productRepository.getProductById(productId)
    }
}