package com.convenience.store.products.domain.usecases

import arrow.core.left
import arrow.core.right
import com.convenience.store.core.domain.services.UuidService
import com.convenience.store.products.domain.entities.Product
import com.convenience.store.products.domain.entities.ProductError
import com.convenience.store.products.domain.repositories.ProductRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID

class ProductAddUseCaseTest {

    private val productRepository: ProductRepository = mockk()
    private val uuidService: UuidService = mockk()

    private lateinit var useCase: ProductAddUseCase

    @BeforeEach
    fun setUp() {
        useCase = ProductAddUseCaseImpl(uuidService, productRepository)
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
        )
        coEvery { productRepository.insert(product) } returns Unit.right()
        coEvery { uuidService.createSortableUuid() } returns id


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

        val product = Product(
            id = id,
            name = name,
            description = description,
            price = price,
            barcode = barcode,
            categoryId = categoryId,
            supplierId = supplierId,
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

        // When
        val result = useCase(name, description, price, barcode, categoryId, supplierId)

        // Then
        assertTrue(result.isLeft())
        assertEquals(
            listOf(
                ProductError.ValidationError.InvalidName,
                ProductError.ValidationError.InvalidDescription,
                ProductError.ValidationError.InvalidPrice,
                ProductError.ValidationError.InvalidBarcode
            ), result.leftOrNull()
        )
    }


}