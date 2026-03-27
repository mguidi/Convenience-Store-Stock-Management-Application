package com.convenience.store.alerts.data.services

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.convenience.store.alerts.data.workers.StockAlertWorker
import com.convenience.store.core.data.datasources.EventLogDao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockAlertScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val eventLogDao: EventLogDao
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        scope.launch {
            // monitoring events tables and schedule worker if a new events is added to the table
            eventLogDao.getEvents()
                .map { it.size }
                .distinctUntilChanged()
                .collectLatest {
                    scheduleWorker()
                }
        }
    }

    private fun scheduleWorker() {
        val request = OneTimeWorkRequestBuilder<StockAlertWorker>()
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            StockAlertWorker.CONSUMER,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}
