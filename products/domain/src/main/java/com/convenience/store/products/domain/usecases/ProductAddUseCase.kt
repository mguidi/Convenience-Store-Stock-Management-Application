package com.convenience.store.products.domain.usecases

import arrow.core.Either
import com.convenience.store.core.domain.services.UuidService
import com.convenience.store.products.domain.entities.Product
import com.convenience.store.products.domain.entities.ProductError
import com.convenience.store.products.domain.repositories.ProductRepository
import java.math.BigDecimal
import java.util.UUID

interface ProductAddUseCase {

    suspend operator fun invoke(
        name: String,
        description: String,
        price: BigDecimal,
        barcode: String,
        categoryId: UUID,
        supplierId: UUID,
    ): Either<List<ProductError>, Unit>

}

class ProductAddUseCaseImpl(
    private val uuidService: UuidService,
    private val productRepository: ProductRepository
) : ProductAddUseCase {

    override suspend operator fun invoke(
        name: String,
        description: String,
        price: BigDecimal,
        barcode: String,
        categoryId: UUID,
        supplierId: UUID,
    ): Either<List<ProductError>, Unit> {

        val errors = mutableListOf<ProductError>()

        if (name.isBlank()) errors += ProductError.ValidationError.InvalidName
        if (description.isBlank()) errors += ProductError.ValidationError.InvalidDescription
        if (price < BigDecimal.ZERO) errors += ProductError.ValidationError.InvalidPrice
        if (barcode.isBlank()) errors += ProductError.ValidationError.InvalidBarcode
        if (false /* TODO check if categoryId exits*/) errors += ProductError.ValidationError.InvalidCategory
        if (false /* TODO check if supplierId exits*/) errors += ProductError.ValidationError.InvalidSupplier

        if (errors.isNotEmpty()) return Either.Left(errors)

        val product = Product(
            id = uuidService.createSortableUuid(),
            name = name,
            description = description,
            price = price,
            barcode = barcode,
            categoryId = categoryId,
            supplierId = supplierId,
        )

        return productRepository.insert(product).mapLeft { listOf(it) }
    }
}