package com.convenience.store.stocks.data.workers

import android.content.Context
import androidx.core.content.edit
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.convenience.store.core.data.datasources.EventLogDao
import com.convenience.store.core.domain.events.StockAddEvent
import com.convenience.store.core.domain.events.StockRemoveEvent
import com.convenience.store.stocks.data.datasources.remote.StockApiService
import com.convenience.store.stocks.data.models.remote.StockAddApiDto
import com.convenience.store.stocks.data.models.local.StockAddEventDto
import com.convenience.store.stocks.data.models.remote.StockRemoveApiDto
import com.convenience.store.stocks.data.models.local.StockRemoveEventDto
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import java.util.UUID

@HiltWorker
class StockSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val eventLogDao: EventLogDao,
    private val stockApiService: StockApiService
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val SHARED_PREF_NAME = "StockSyncWorker"
        const val LAST_PROCESSED_ID_KEY = "lastProcessedId"
    }

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun doWork(): Result {
        val sharedPref =
            applicationContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

        val events =
            if (sharedPref.contains(LAST_PROCESSED_ID_KEY)) {
                val lastProcessedId = UUID.fromString(
                    sharedPref
                        .getString(LAST_PROCESSED_ID_KEY, "")
                )
                eventLogDao.getEventsAfter(lastProcessedId).first()
            } else {
                eventLogDao.getEvents().first()
            }


        var allSuccess = true
        for (event in events) {
            val syncResult = when (event.type) {
                StockAddEvent.NAME -> {
                    val data = json.decodeFromString<StockAddEventDto>(event.payload)
                    stockApiService.addStock(
                        StockAddApiDto(
                            requestId = event.id,
                            productId = data.productId,
                            quantity = data.quantity
                        )
                    )
                }

                StockRemoveEvent.NAME -> {
                    val data = json.decodeFromString<StockRemoveEventDto>(event.payload)
                    stockApiService.removeStock(
                        StockRemoveApiDto(
                            requestId = event.id,
                            productId = data.productId,
                            quantity = data.quantity.negate()
                        )
                    )
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

        if (events.isNotEmpty()) {
            sharedPref.edit(commit = true) {
                putString(LAST_PROCESSED_ID_KEY, events.last().id.toString())
            }
        }

        return if (allSuccess) Result.success() else Result.retry()
    }
}