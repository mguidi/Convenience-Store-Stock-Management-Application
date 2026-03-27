package com.convenience.store.alerts.domain.repositories

import androidx.paging.PagingData
import com.convenience.store.alerts.domain.entities.StockAlert
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface StockAlertRepository {

    fun getStockAlertByProductId(productId: UUID): Flow<StockAlert?>

    fun getStockAlerts(): Flow<PagingData<StockAlert>>

}