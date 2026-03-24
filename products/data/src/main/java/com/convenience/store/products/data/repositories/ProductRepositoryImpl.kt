package com.convenience.store.products.data.repositories

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import arrow.core.Either
import com.convenience.store.core.data.datasources.EventLogDao
import com.convenience.store.core.data.models.EventLogDto
import com.convenience.store.core.domain.events.ProductAddEvent
import com.convenience.store.products.data.datasources.ProductDao
import com.convenience.store.products.data.models.toDomain
import com.convenience.store.products.data.models.toDto
import com.convenience.store.products.domain.entities.Product
import com.convenience.store.products.domain.entities.ProductError
import com.convenience.store.products.domain.repositories.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val database: RoomDatabase,
    private val eventLogEntityDao: EventLogDao,
    private val productEntityDao: ProductDao
) : ProductRepository {

    override suspend fun insert(product: Product): Either<ProductError.RepositoryError, Unit> {
        return try {
            database.withTransaction {
                productEntityDao.insert(product.toDto())
                val eventPayload = ProductAddEvent(
                    product.id,
                    product.name,
                    product.description,
                    product.price,
                    product.barcode,
                    product.categoryId,
                    product.supplierId,
                )
                val event = EventLogDto(
                    type = ProductAddEvent.NAME,
                    dataId = product.id,
                    payload = Json.encodeToString(eventPayload.toDto())
                )
                eventLogEntityDao.insert(event)
            }
            Either.Right(Unit)

        } catch (e: android.database.sqlite.SQLiteConstraintException) {
            Log.w("ProductRepository", "Product already exists", e)
            Either.Left(ProductError.RepositoryError.AlreadyExists)
        } catch (e: Exception) {
            Log.e("ProductRepository", "Generic error", e)
            Either.Left(ProductError.RepositoryError.DatabaseError)
        }
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
