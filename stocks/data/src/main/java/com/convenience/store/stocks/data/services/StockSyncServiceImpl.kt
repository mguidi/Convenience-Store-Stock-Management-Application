package com.convenience.store.stocks.data.services

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.convenience.store.stocks.data.workers.StockSyncWorker
import com.convenience.store.stocks.domain.services.StockSyncService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * This class is responsible for scheduling the stock sync worker.
 */
class StockSyncServiceImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : StockSyncService {

    override fun scheduleSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<StockSyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "stock_sync",
            ExistingWorkPolicy.APPEND,
            syncRequest
        )
    }
}