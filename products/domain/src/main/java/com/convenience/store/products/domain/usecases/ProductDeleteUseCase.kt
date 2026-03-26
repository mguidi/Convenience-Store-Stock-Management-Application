package com.convenience.store.products.domain.usecases

import arrow.core.Either
import com.convenience.store.products.domain.entities.ProductError
import com.convenience.store.products.domain.repositories.ProductRepository
import com.convenience.store.products.domain.services.ProductSyncService
import java.util.UUID

interface ProductDeleteUseCase {

    suspend operator fun invoke(
        id: UUID,
    ): Either<List<ProductError>, Unit>

}

class ProductDeleteUseCaseImpl(
    private val productSyncService: ProductSyncService,
    private val productRepository: ProductRepository,
) : ProductDeleteUseCase {

    override suspend operator fun invoke(
        id: UUID,
    ): Either<List<ProductError>, Unit> {
        return productRepository.deleteById(id)
            .mapLeft { listOf(it) }
            .onRight {
                productSyncService.scheduleSync()
            }
    }
}
