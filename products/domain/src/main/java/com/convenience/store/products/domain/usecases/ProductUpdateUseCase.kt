package com.convenience.store.products.domain.usecases

import arrow.core.Either
import arrow.core.left
import com.convenience.store.products.domain.entities.Product
import com.convenience.store.products.domain.entities.ProductError
import com.convenience.store.products.domain.repositories.CategoryRepository
import com.convenience.store.products.domain.repositories.ProductRepository
import com.convenience.store.products.domain.services.ProductSyncService
import com.convenience.store.suppliers.domain.repositories.SupplierRepository
import kotlinx.coroutines.flow.firstOrNull
import java.math.BigDecimal
import java.util.UUID

interface ProductUpdateUseCase {

    suspend operator fun invoke(
        id: UUID,
        name: String,
        description: String,
        price: BigDecimal,
        barcode: String,
        categoryId: UUID,
        supplierId: UUID,
        version: Long
    ): Either<List<ProductError>, Unit>

}

class ProductUpdateUseCaseImpl(
    private val productSyncService: ProductSyncService,
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val supplierRepository: SupplierRepository
) : ProductUpdateUseCase {

    override suspend operator fun invoke(
        id: UUID,
        name: String,
        description: String,
        price: BigDecimal,
        barcode: String,
        categoryId: UUID,
        supplierId: UUID,
        version: Long
    ): Either<List<ProductError>, Unit> {

        val errors = mutableListOf<ProductError>()

        if (name.isBlank()) errors += ProductError.ValidationError.InvalidName
        if (description.isBlank()) errors += ProductError.ValidationError.InvalidDescription
        if (price < BigDecimal.ZERO) errors += ProductError.ValidationError.InvalidPrice
        if (barcode.isBlank()) errors += ProductError.ValidationError.InvalidBarcode
        if (categoryRepository.getCategoryById(categoryId).firstOrNull() == null) errors += ProductError.ValidationError.InvalidCategory
        if (supplierRepository.getSupplierById(supplierId).firstOrNull() == null) errors += ProductError.ValidationError.InvalidSupplier

        if (errors.isNotEmpty()) return errors.left()

        val product = Product(
            id = id,
            name = name,
            description = description,
            price = price,
            barcode = barcode,
            categoryId = categoryId,
            supplierId = supplierId,
            synced = false
        )

        return productRepository.update(product)
            .mapLeft { listOf(it) }
            .onRight {
                productSyncService.scheduleSync()
            }
    }
}
