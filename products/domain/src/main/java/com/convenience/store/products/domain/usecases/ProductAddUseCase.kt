package com.convenience.store.products.domain.usecases

import arrow.core.Either
import com.convenience.store.core.domain.services.UuidService
import com.convenience.store.products.domain.entities.Product
import com.convenience.store.products.domain.entities.ProductError
import com.convenience.store.products.domain.repositories.ProductRepository
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

interface ProductAddUseCase {

    suspend fun newProduct(
        name: String,
        description: String,
        price: BigDecimal,
        barcode: String,
        categoryId: UUID,
        supplierId: UUID,
        availableQuantity: BigDecimal
    ): Either<ProductError, Unit>

}

class ProductAddUseCaseImpl @Inject constructor(
    private val uuidService: UuidService,
    private val productRepository: ProductRepository
) : ProductAddUseCase {

    override suspend fun newProduct(
        name: String,
        description: String,
        price: BigDecimal,
        barcode: String,
        categoryId: UUID,
        supplierId: UUID,
        availableQuantity: BigDecimal
    ): Either<ProductError, Unit> {

        // TODO check if fields are valid

        val product = Product(
            id = uuidService.createSortableUuid(),
            name = name,
            description = description,
            price = price,
            barcode = barcode,
            categoryId = categoryId,
            supplierId = supplierId,
            availableQuantity = availableQuantity,
        )

        return productRepository.insert(product)
    }
}