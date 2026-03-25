package com.convenience.store.stocks.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.convenience.store.core.data.datasources.EventLogDao
import com.convenience.store.core.data.datasources.EventLogOffsetDao
import com.convenience.store.core.data.models.EventLogOffsetDto
import com.convenience.store.core.domain.events.StockAddEvent
import com.convenience.store.core.domain.events.StockRemoveEvent
import com.convenience.store.stocks.data.datasources.local.StockDao
import com.convenience.store.stocks.data.datasources.remote.StockApiService
import com.convenience.store.stocks.data.models.remote.StockAddApiDto
import com.convenience.store.stocks.data.models.events.StockAddEventDto
import com.convenience.store.stocks.data.models.remote.StockRemoveApiDto
import com.convenience.store.stocks.data.models.events.StockRemoveEventDto
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

@HiltWorker
class StockSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val database: RoomDatabase,
    private val eventLogDao: EventLogDao,
    private val eventLogOffsetDao: EventLogOffsetDao,
    private val stockDao: StockDao,
    private val stockApiService: StockApiService
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val CONSUMER = "StockSyncWorker"
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
                StockAddEvent.NAME -> {
                    val data = _json.decodeFromString<StockAddEventDto>(event.payload)
                    stockApiService.addStock(
                        StockAddApiDto(
                            requestId = event.id,
                            productId = data.productId,
                            quantity = data.quantityChange
                        )
                    ).onRight {
                        database.withTransaction {
                            stockDao.addStockQuantitySync(
                                productId = data.productId,
                                quantityChange = data.quantityChange
                            )
                            stockDao.addStockQuantityNotSync(
                                productId = data.productId,
                                quantityChange = data.quantityChange.negate()
                            )
                            eventLogOffsetDao.insertOrUpdate(EventLogOffsetDto(CONSUMER, event.id))
                        }
                    }
                }

                StockRemoveEvent.NAME -> {
                    val data = _json.decodeFromString<StockRemoveEventDto>(event.payload)
                    stockApiService.removeStock(
                        StockRemoveApiDto(
                            requestId = event.id,
                            productId = data.productId,
                            quantity = data.quantityChange.negate()
                        )
                    ).onRight {
                        database.withTransaction {
                            stockDao.addStockQuantitySync(
                                productId = data.productId,
                                quantityChange = data.quantityChange.negate()
                            )
                            stockDao.addStockQuantityNotSync(
                                productId = data.productId,
                                quantityChange = data.quantityChange
                            )
                            eventLogOffsetDao.insertOrUpdate(EventLogOffsetDto(CONSUMER, event.id))
                        }
                    }
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