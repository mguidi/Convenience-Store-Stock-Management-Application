package com.convenience.store.stocks.data.di

import com.convenience.store.stocks.data.datasources.remote.StockApiService
import com.convenience.store.stocks.data.datasources.remote.StockApiServiceMockImpl
import com.convenience.store.stocks.data.repositories.StockRepositoryImpl
import com.convenience.store.stocks.data.services.StockSyncServiceImpl
import com.convenience.store.stocks.domain.repositories.StockRepository
import com.convenience.store.stocks.domain.services.StockSyncService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StocksDataModule {

    @Binds
    @Singleton
    abstract fun bindStockRepository(
        stockRepositoryImpl: StockRepositoryImpl
    ): StockRepository

    @Binds
    @Singleton
    abstract fun bindStockApiService(
        stockApiServiceKtorImpl: StockApiServiceMockImpl
    ): StockApiService

    @Binds
    @Singleton
    abstract fun bindStockSyncService(
        stockSyncServiceImpl: StockSyncServiceImpl
    ): StockSyncService

    companion object {
        @Provides
        @Singleton
        fun provideHttpClient(): HttpClient {
            return HttpClient(Android) {
                install(Logging) {
                    level = LogLevel.INFO
                }
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        prettyPrint = true
                        isLenient = true
                    })
                }
            }
        }
    }
}