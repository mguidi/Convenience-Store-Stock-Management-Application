package com.convenience.store.products.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import arrow.core.Either
import com.convenience.store.products.data.datasources.ProductEntityDao
import com.convenience.store.products.data.models.toDomain
import com.convenience.store.products.data.models.toEntity
import com.convenience.store.products.domain.entities.Product
import com.convenience.store.products.domain.entities.ProductError
import com.convenience.store.products.domain.repositories.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productEntityDao: ProductEntityDao
) : ProductRepository {

    override suspend fun insert(product: Product): Either<ProductError, Unit> {
        productEntityDao.insertOrUpdateProduct(product.toEntity())
        return Either.Right(Unit) // TODO handle errors
    }

    override fun getProductById(productId: UUID): Flow<Product?> {
        return productEntityDao.getProductById(productId).map { it?.toDomain() }
    }

    override fun getProductByBarcode(barcode: String): Flow<Product?> {
        return productEntityDao.getProductByBarcode(barcode).map { it?.toDomain() }
    }

    override fun getProductsStream(): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 5
            ),
            pagingSourceFactory = { productEntityDao.getProductsPaged() }
        ).flow
            .map { pagingData ->
                pagingData.map { entity -> entity.toDomain() }
            }
    }

    override fun getProductsByCategoryStream(categoryId: UUID): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 5
            ),
            pagingSourceFactory = { productEntityDao.getProductsByCategoryPaged(categoryId) }
        ).flow
            .map { pagingData ->
                pagingData.map { entity -> entity.toDomain() }
            }
    }
}