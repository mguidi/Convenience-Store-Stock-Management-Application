package com.convenience.store.products.data.repositories

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.convenience.store.core.data.datasources.EventLogDao
import com.convenience.store.core.data.models.EventLogDto
import com.convenience.store.core.domain.events.ProductAddEvent
import com.convenience.store.core.domain.services.UuidService
import com.convenience.store.products.data.datasources.local.ProductDao
import com.convenience.store.products.data.datasources.remote.ProductApiService
import com.convenience.store.products.data.models.local.toDomain
import com.convenience.store.products.data.models.local.toDto
import com.convenience.store.products.data.models.remote.toDto
import com.convenience.store.products.domain.entities.Product
import com.convenience.store.products.domain.entities.ProductError
import com.convenience.store.products.domain.repositories.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val database: RoomDatabase,
    private val eventLogDao: EventLogDao,
    private val productDao: ProductDao,
    private val productApiService: ProductApiService,
    private val uuidService: UuidService,
) : ProductRepository {

    override suspend fun insert(product: Product): Either<ProductError.RepositoryError, Unit> {
        return try {
            database.withTransaction {
                productDao.insert(product.toDto())
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
                    id = uuidService.createSortableUuid(),
                    type = ProductAddEvent.NAME,
                    payload = Json.encodeToString(eventPayload.toDto())
                )
                eventLogDao.insert(event)
            }
            Unit.right()

        } catch (e: android.database.sqlite.SQLiteConstraintException) {
            Log.w("ProductRepository", "Product already exists", e)
            ProductError.RepositoryError.AlreadyExists.left()
        } catch (e: Exception) {
            Log.e("ProductRepository", "Generic error", e)
            ProductError.RepositoryError.DatabaseError.left()
        }
    }

    override fun getProductById(productId: UUID): Flow<Product?> = flow {
        val localProduct = productDao.getProductById(productId).firstOrNull()
        if (localProduct != null) {
            emit(localProduct.toDomain())
        }

        val remoteResult = try {
            productApiService.getProductById(productId)
        } catch (e: Exception) {
            null
        }

        remoteResult?.onRight { apiDto ->
            productDao.insertOrUpdateProduct(apiDto.toDto())
        }

        emitAll(productDao.getProductById(productId).map { it?.toDomain() })
    }

    override fun getProductByBarcode(barcode: String): Flow<Product?> {
        return productDao.getProductByBarcode(barcode).map { it?.toDomain() }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getProductsStream(): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 5
            ),
            remoteMediator = ProductRemoteMediator(
                database = database,
                productDao = productDao,
                productApiService = productApiService
            ),
            pagingSourceFactory = { productDao.getProductsPaged() }
        ).flow
            .map { pagingData ->
                pagingData.map { entity -> entity.toDomain() }
            }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getProductsByCategoryStream(categoryId: UUID): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 5
            ),
            remoteMediator = ProductRemoteMediator(
                database = database,
                productDao = productDao,
                productApiService = productApiService,
                categoryId = categoryId
            ),
            pagingSourceFactory = { productDao.getProductsByCategoryPaged(categoryId) }
        ).flow
            .map { pagingData ->
                pagingData.map { entity -> entity.toDomain() }
            }
    }
}
