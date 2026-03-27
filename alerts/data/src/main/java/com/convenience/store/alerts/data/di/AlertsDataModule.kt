package com.convenience.store.alerts.data.di

import com.convenience.store.alerts.data.repositories.StockAlertRepositoryImpl
import com.convenience.store.alerts.data.services.AlertServiceImpl
import com.convenience.store.alerts.domain.repositories.StockAlertRepository
import com.convenience.store.alerts.domain.services.AlertService
import com.convenience.store.alerts.domain.usecases.StockAlertCheckUseCase
import com.convenience.store.alerts.domain.usecases.StockAlertCheckUseCasesImpl
import com.convenience.store.products.domain.repositories.ProductRepository
import com.convenience.store.stocks.domain.repositories.StockRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AlertsDataModule {
    @Binds
    @Singleton
    abstract fun bindStockAlertRepository(impl: StockAlertRepositoryImpl): StockAlertRepository

    @Binds
    @Singleton
    abstract fun bindAlertService(impl: AlertServiceImpl): AlertService

    companion object {
        @Provides
        @Singleton
        fun provideStockAlertCheckUseCase(
            stockAlertRepository: StockAlertRepository,
            stockRepository: StockRepository,
            productRepository: ProductRepository,
            alertService: AlertService
        ): StockAlertCheckUseCase {
            return StockAlertCheckUseCasesImpl(
                stockAlertRepository,
                stockRepository,
                productRepository,
                alertService
            )
        }

    }

}