package com.convenience.store.products.domain.usecases

import arrow.core.left
import arrow.core.right
import com.convenience.store.core.domain.services.UuidService
import com.convenience.store.products.domain.entities.Category
import com.convenience.store.products.domain.entities.Product
import com.convenience.store.products.domain.entities.ProductError
import com.convenience.store.products.domain.repositories.CategoryRepository
import com.convenience.store.products.domain.repositories.ProductRepository
import com.convenience.store.products.domain.services.ProductSyncService
import com.convenience.store.suppliers.domain.entities.Supplier
import com.convenience.store.suppliers.domain.repositories.SupplierRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID

class ProductAddUseCaseTest {

    private val productRepository: ProductRepository = mockk()

    private val categoryRepository: CategoryRepository = mockk()

    private val supplierRepository: SupplierRepository = mockk()

    private val productSyncService: ProductSyncService = mockk()
    private val uuidService: UuidService = mockk()

    private lateinit var useCase: ProductCreateUseCase

    @BeforeEach
    fun setUp() {
        useCase = ProductAddUseCaseImpl(
            uuidService,
            productSyncService,
            productRepository,
            categoryRepository,
            supplierRepository
        )
    }

    @Test
    fun `should return success when repository insert product successfully`() = runBlocking {
        // Given
        val id = UUID.randomUUID()
        val name = "Test Name"
        val description = "Test Description"
        val price = BigDecimal(10)
        val barcode = "Test Barcode"
        val categoryId = UUID.randomUUID()
        val supplierId = UUID.randomUUID()

        val product = Product(
            id = id,
            name = name,
            description = description,
            price = price,
            barcode = barcode,
            categoryId = categoryId,
            supplierId = supplierId,
            version = 0
        )
        coEvery { productRepository.insert(product) } returns Unit.right()
        coEvery { uuidService.createSortableUuid() } returns id
        coEvery { productSyncService.scheduleSync() } returns Unit
        coEvery { categoryRepository.getCategoryById(categoryId) } returns flowOf(
            Category(
                categoryId,
                "Category"
            )
        )
        coEvery { supplierRepository.getSupplierById(supplierId) } returns flowOf(
            Supplier(
                supplierId,
                "Supplier"
            )
        )

        // When
        val result = useCase(name, description, price, barcode, categoryId, supplierId)

        // Then
        assertTrue(result.isRight())
        coVerify(exactly = 1) { productRepository.insert(product) }
    }


    @Test
    fun `should return failure when repository fails to insert product`() = runBlocking {
        // Given
        val id = UUID.randomUUID()
        val name = "Test Name"
        val description = "Test Description"
        val price = BigDecimal(10)
        val barcode = "Test Barcode"
        val categoryId = UUID.randomUUID()
        val supplierId = UUID.randomUUID()
        coEvery { categoryRepository.getCategoryById(categoryId) } returns flowOf(
            Category(
                categoryId,
                "Category"
            )
        )
        coEvery { supplierRepository.getSupplierById(supplierId) } returns flowOf(
            Supplier(
                supplierId,
                "Supplier"
            )
        )

        val product = Product(
            id = id,
            name = name,
            description = description,
            price = price,
            barcode = barcode,
            categoryId = categoryId,
            supplierId = supplierId,
            version = 0
        )
        coEvery { productRepository.insert(product) } returns ProductError.RepositoryError.DatabaseError.left()
        coEvery { uuidService.createSortableUuid() } returns id


        // When
        val result = useCase(name, description, price, barcode, categoryId, supplierId)

        // Then
        assertTrue(result.isLeft())
        assertEquals(listOf(ProductError.RepositoryError.DatabaseError), result.leftOrNull())
        coVerify(exactly = 1) { productRepository.insert(product) }
    }

    @Test
    fun `should return failure when product fields are invalid`() = runBlocking {
        // Given
        val id = UUID.randomUUID()
        val name = " "
        val description = " "
        val price = BigDecimal(-10)
        val barcode = " "
        val categoryId = UUID.randomUUID()
        val supplierId = UUID.randomUUID()

        coEvery { uuidService.createSortableUuid() } returns id
        coEvery { categoryRepository.getCategoryById(categoryId) } returns flowOf(null)
        coEvery { supplierRepository.getSupplierById(supplierId) } returns flowOf(null)

        // When
        val result = useCase(name, description, price, barcode, categoryId, supplierId)

        // Then
        assertTrue(result.isLeft())
        assertEquals(
            listOf(
                ProductError.ValidationError.InvalidName,
                ProductError.ValidationError.InvalidDescription,
                ProductError.ValidationError.InvalidPrice,
                ProductError.ValidationError.InvalidBarcode,
                ProductError.ValidationError.InvalidCategory,
                ProductError.ValidationError.InvalidSupplier
            ), result.leftOrNull()
        )
    }


}