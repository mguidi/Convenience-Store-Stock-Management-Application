package com.convenience.store.stocks.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.convenience.store.core.data.datasources.EventLogDao
import com.convenience.store.core.domain.events.StockAddEvent
import com.convenience.store.core.domain.events.StockRemoveEvent
import com.convenience.store.stocks.data.datasources.StockApiService
import com.convenience.store.stocks.data.models.StockAddEventDto
import com.convenience.store.stocks.data.models.StockRemoveEventDto
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

@HiltWorker
class StockSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val eventLogDao: EventLogDao,
    private val stockApiService: StockApiService
) : CoroutineWorker(appContext, workerParams) {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun doWork(): Result {
        // TODO read lastProcessedId from db
        val events = eventLogDao.getEventsAfter(-1).first()

        var allSuccess = true
//        var lastProcessedId = -1L

        for (event in events) {
            val syncResult = when (event.type) {
                StockAddEvent.NAME -> {
                    val data = json.decodeFromString<StockAddEventDto>(event.payload)
                    stockApiService.addStock(data.productId, data.quantity)
                }

                StockRemoveEvent.NAME -> {
                    val data = json.decodeFromString<StockRemoveEventDto>(event.payload)
                    stockApiService.removeStock(data.productId, data.quantity.negate())
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

//        if (lastProcessedId != -1L) {
//            eventLogDao.deleteProcessedEvents(lastProcessedId)
//        }

        // TODO store lastProcessedId to db

        return if (allSuccess) Result.success() else Result.retry()
    }
}