package com.convenience.store.stocks.ui.di

import com.convenience.store.stocks.domain.repositories.StockRepository
import com.convenience.store.stocks.domain.services.StockSyncService
import com.convenience.store.stocks.domain.usecases.StockAddUseCase
import com.convenience.store.stocks.domain.usecases.StockAddUseCaseImpl
import com.convenience.store.stocks.domain.usecases.StockGetByIdUseCase
import com.convenience.store.stocks.domain.usecases.StockGetByIdUseCaseImpl
import com.convenience.store.stocks.domain.usecases.StockRemoveUseCase
import com.convenience.store.stocks.domain.usecases.StockRemoveUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StocksUiModule {

    @Provides
    @Singleton
    fun provideStockAddUseCase(
        stockRepository: StockRepository,
        stockSyncService: StockSyncService
    ): StockAddUseCase {
        return StockAddUseCaseImpl(stockRepository, stockSyncService)
    }

    @Provides
    @Singleton
    fun provideStockRemoveUseCase(
        stockRepository: StockRepository,
        stockSyncService: StockSyncService
    ): StockRemoveUseCase {
        return StockRemoveUseCaseImpl(stockRepository, stockSyncService)
    }

    @Provides
    @Singleton
    fun provideStockGetByIdUseCase(
        stockRepository: StockRepository
    ): StockGetByIdUseCase {
        return StockGetByIdUseCaseImpl(stockRepository)
    }

}