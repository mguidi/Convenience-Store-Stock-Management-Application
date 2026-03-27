package com.convenience.store.products.domain.usecases

import androidx.paging.PagingData
import com.convenience.store.products.domain.entities.Product
import com.convenience.store.products.domain.repositories.ProductRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface ProductsGetUseCase {

    operator fun invoke(categoryId: UUID?, barcode: String?): Flow<PagingData<Product>>

}

class ProductsGetUseCaseImpl(
    private val productRepository: ProductRepository
) : ProductsGetUseCase {

    override fun invoke(categoryId: UUID?, barcode: String?): Flow<PagingData<Product>> {
        return productRepository.getProducts(categoryId, barcode)
    }
}