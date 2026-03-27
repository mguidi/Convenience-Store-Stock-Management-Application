package com.convenience.store.products.data.repositories

import android.content.Context
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
import com.convenience.store.core.domain.events.ProductCreateEvent
import com.convenience.store.core.domain.events.ProductDeleteEvent
import com.convenience.store.core.domain.events.ProductUpdateEvent
import com.convenience.store.core.domain.services.UuidService
import com.convenience.store.products.data.datasources.local.ProductDao
import com.convenience.store.products.data.datasources.remote.ProductApiService
import com.convenience.store.products.data.models.events.toDto
import com.convenience.store.products.data.models.local.toDomain
import com.convenience.store.products.data.models.local.toDto
import com.convenience.store.products.data.models.remote.toDto
import com.convenience.store.products.domain.entities.Product
import com.convenience.store.products.domain.entities.ProductError
import com.convenience.store.products.domain.repositories.ProductRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject

/**
 * This class is responsible for managing the products
 */
class ProductRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val database: RoomDatabase,
    private val eventLogDao: EventLogDao,
    private val productDao: ProductDao,
    private val productApiService: ProductApiService,
    private val uuidService: UuidService,
) : ProductRepository {

    override suspend fun insert(product: Product): Either<ProductError.RepositoryError, Unit> {
        return try {
            database.withTransaction {
                //region insert the product on the local database
                productDao.insert(product.toDto())
                val eventPayload = ProductCreateEvent(
                    product.id,
                    product.name,
                    product.description,
                    product.price,
                    product.barcode,
                    product.categoryId,
                    product.supplierId,
                )
                //endregion

                //region store the event in the event log
                val event = EventLogDto(
                    id = uuidService.createSortableUuid(),
                    type = ProductCreateEvent.NAME,
                    payload = Json.encodeToString(eventPayload.toDto())
                )
                eventLogDao.insert(event)
                //endregion
            }
            Unit.right()

        } catch (e: android.database.sqlite.SQLiteConstraintException) {
            Log.w("ProductRepository", "Product already exists", e)
            ProductError.RepositoryError.AlreadyExists.left()
        } catch (e: Exception) {
            Log.e("ProductRepository", "Error inserting product", e)
            ProductError.RepositoryError.DatabaseError.left()
        }
    }

    override suspend fun update(product: Product): Either<ProductError.RepositoryError, Unit> {
        return try {
            database.withTransaction {
                // update the product on the local database
                productDao.insertOrUpdate(product.toDto())
                val eventPayload = ProductUpdateEvent(
                    product.id,
                    product.name,
                    product.description,
                    product.price,
                    product.barcode,
                    product.categoryId,
                    product.supplierId,
                )

                // store the event in the event log
                val event = EventLogDto(
                    id = uuidService.createSortableUuid(),
                    type = ProductUpdateEvent.NAME,
                    payload = Json.encodeToString(eventPayload.toDto())
                )
                eventLogDao.insert(event)
            }
            Unit.right()

        } catch (e: Exception) {
            Log.e("ProductRepository", "Error updating product", e)
            ProductError.RepositoryError.DatabaseError.left()
        }
    }


    override suspend fun deleteById(productId: UUID): Either<ProductError.RepositoryError, Unit> {
        return try {
            database.withTransaction {
                // update the product on the local database
                productDao.deleteById(productId)
                val eventPayload = ProductDeleteEvent(productId)

                // store the event in the event log
                val event = EventLogDto(
                    id = uuidService.createSortableUuid(),
                    type = ProductUpdateEvent.NAME,
                    payload = Json.encodeToString(eventPayload.toDto())
                )
                eventLogDao.insert(event)
            }
            Unit.right()

        } catch (e: Exception) {
            Log.e("ProductRepository", "Error deleting product", e)
            ProductError.RepositoryError.DatabaseError.left()
        }
    }

    override fun getProductById(productId: UUID): Flow<Product?> = flow {
        //region get the product from the local database and emit the value
        val localProduct = productDao.getProductById(productId).firstOrNull()
        if (localProduct != null) {
            emit(localProduct.toDomain())
        }
        //endregion

        //region get the product from the remote service
        val remoteResult = try {
            productApiService.getProductById(productId)
        } catch (e: Exception) {
            null
        }
        //endregion

        //region on success update the product on the local database
        remoteResult?.onRight { remoteProduct ->
            productDao.insertOrUpdate(remoteProduct.toDto())
        }
        //endregion

        // get the product from the local database updated by the remote service
        emitAll(productDao.getProductById(productId).map { it?.toDomain() })
    }

    override fun getProductByBarcode(barcode: String): Flow<Product?> {
        return productDao.getProductByBarcode(barcode).map { it?.toDomain() }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getProducts(categoryId: UUID?, barcode: String?): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 2
            ),
            remoteMediator = ProductRemoteMediator(
                context = context,
                database = database,
                productDao = productDao,
                productApiService = productApiService,
                categoryId = categoryId,
                barcode = barcode
            ),
            pagingSourceFactory = { productDao.getProductsPaged(categoryId, barcode) }
        ).flow
            .map { pagingData ->
                pagingData.map { entity -> entity.toDomain() }
            }
    }
}
