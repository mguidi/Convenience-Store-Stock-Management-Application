package com.convenience.store.stocks.data.di

import com.convenience.store.stocks.data.repositories.StockRepositoryImpl
import com.convenience.store.stocks.domain.repositories.StockRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StocksDataModule {

    @Binds
    @Singleton
    abstract fun bindStockRepository(
        stockRepositoryImpl: StockRepositoryImpl
    ): StockRepository

}