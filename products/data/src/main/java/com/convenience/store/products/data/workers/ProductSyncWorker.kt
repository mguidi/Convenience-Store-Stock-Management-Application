package com.convenience.store.products.data.workers

import android.content.Context
import androidx.core.content.edit
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.convenience.store.core.data.datasources.EventLogDao
import com.convenience.store.core.domain.events.ProductCreateEvent
import com.convenience.store.products.data.datasources.remote.ProductApiService
import com.convenience.store.products.data.models.remote.ProductCreateApiDto
import com.convenience.store.products.data.models.events.ProductCreateEventDto
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import java.util.UUID

@HiltWorker
class ProductSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val eventLogDao: EventLogDao,
    private val productApiService: ProductApiService
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val SHARED_PREF_NAME = "ProductSyncWorker"
        const val LAST_PROCESSED_ID_KEY = "lastProcessedId"
    }

    private val _json = Json { ignoreUnknownKeys = true }

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
                ProductCreateEvent.NAME -> {
                    val data = _json.decodeFromString<ProductCreateEventDto>(event.payload)
                    productApiService.createProduct(
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

        sharedPref.edit(commit = true) {
            putString(LAST_PROCESSED_ID_KEY, events.last().id.toString())
        }

        return if (allSuccess) Result.success() else Result.retry()
    }
}