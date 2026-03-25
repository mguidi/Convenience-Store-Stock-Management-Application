package com.convenience.store.products.data.services

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.convenience.store.products.data.workers.ProductSyncWorker
import com.convenience.store.products.domain.services.ProductSyncService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * This class is responsible for scheduling the product sync worker.
 */
class ProductSyncServiceImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : ProductSyncService {

    override fun scheduleSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<ProductSyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "product_sync",
            ExistingWorkPolicy.APPEND,
            syncRequest
        )
    }
}