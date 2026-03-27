package com.convenience.store.alerts.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.convenience.store.alerts.domain.usecases.StockAlertCheckUseCase
import com.convenience.store.core.data.datasources.EventLogDao
import com.convenience.store.core.data.datasources.EventLogOffsetDao
import com.convenience.store.core.data.models.EventLogOffsetDto
import com.convenience.store.core.domain.events.StockAddEvent
import com.convenience.store.core.domain.events.StockRemoveEvent
import com.convenience.store.stocks.data.models.events.StockAddEventDto
import com.convenience.store.stocks.data.models.events.StockRemoveEventDto
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

/**
 * Worker to check stock level and notify alert
 */
@HiltWorker
class StockAlertWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val database: RoomDatabase,
    private val eventLogDao: EventLogDao,
    private val eventLogOffsetDao: EventLogOffsetDao,
    private val stockAlertCheckUseCase: StockAlertCheckUseCase
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val CONSUMER = "StockAlertWorker"
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
                    //region execute stock alert checks
                    val data = _json.decodeFromString<StockAddEventDto>(event.payload)
                    val result = stockAlertCheckUseCase(data.productId)
                    //endregion

                    //region on success update of the last processed event id
                    result.onRight {
                        database.withTransaction {
                            eventLogOffsetDao.insertOrUpdate(EventLogOffsetDto(CONSUMER, event.id))
                        }
                    }
                    //endregion
                }

                StockRemoveEvent.NAME -> {
                    //region execute stock alert checks
                    val data = _json.decodeFromString<StockRemoveEventDto>(event.payload)
                    val result = stockAlertCheckUseCase(data.productId)
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