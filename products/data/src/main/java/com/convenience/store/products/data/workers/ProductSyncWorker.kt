package com.convenience.store.products.data.workers

import android.content.Context
import androidx.core.content.edit
import androidx.hilt.work.HiltWorker
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.convenience.store.core.data.datasources.EventLogDao
import com.convenience.store.core.data.datasources.EventLogOffsetDao
import com.convenience.store.core.data.models.EventLogOffsetDto
import com.convenience.store.core.domain.events.ProductCreateEvent
import com.convenience.store.products.data.datasources.remote.ProductApiService
import com.convenience.store.products.data.models.remote.ProductCreateApiDto
import com.convenience.store.products.data.models.events.ProductCreateEventDto
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import java.util.UUID

/**
 * Worker to sync the products with the server.
 */
@HiltWorker
class ProductSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val database: RoomDatabase,
    private val eventLogDao: EventLogDao,
    private val eventLogOffsetDao: EventLogOffsetDao,
    private val productApiService: ProductApiService
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val CONSUMER = "ProductSyncWorker"
    }

    private val _json = Json { ignoreUnknownKeys = true }

    override suspend fun doWork(): Result {
        val offset = eventLogOffsetDao.getEventLogOffsetForConsumer(CONSUMER)

        val events = offset?.let {
            eventLogDao.getEventsAfter(it.lastProcessedId).first()
        } ?: eventLogDao.getEvents().first()


        var allSuccess = true
        for (event in events) {
            val syncResult = when (event.type) {
                ProductCreateEvent.NAME -> {
                    //region call to the service to create the product
                    val data = _json.decodeFromString<ProductCreateEventDto>(event.payload)
                    val result = productApiService.createProduct(
                        ProductCreateApiDto(
                            requestId = event.id,
                            id = data.id,
                            name = data.name,
                            description = data.description,
                            price = data.price,
                            barcode = data.barcode,
                            categoryId = data.categoryId,
                            supplierId = data.supplierId
                        )
                    )
                    //endregion

                    //region on success update of the last processed event id
                    result.onRight {
                        database.withTransaction {
                            eventLogOffsetDao.insertOrUpdate(EventLogOffsetDto(CONSUMER, event.id))
                        }
                    }
                    //endregion
                }

                else -> null
            }

            if (syncResult != null) {
                if (syncResult.isLeft()) {
                    allSuccess = false
                    break
                }
            }
        }

        return if (allSuccess) Result.success() else Result.retry()
    }
}