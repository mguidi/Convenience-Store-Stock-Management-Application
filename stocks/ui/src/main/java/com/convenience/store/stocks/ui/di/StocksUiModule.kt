package com.convenience.store.stocks.ui.di

import com.convenience.store.stocks.domain.repositories.StockRepository
import com.convenience.store.stocks.domain.usecases.StockAddUseCase
import com.convenience.store.stocks.domain.usecases.StockAddUseCaseImpl
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
        stockRepository: StockRepository
    ): StockAddUseCase {
        return StockAddUseCaseImpl(stockRepository)
    }

    @Provides
    @Singleton
    fun provideStockRemoveUseCase(
        stockRepository: StockRepository
    ): StockRemoveUseCase {
        return StockRemoveUseCaseImpl(stockRepository)
    }

}