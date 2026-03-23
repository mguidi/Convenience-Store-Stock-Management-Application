package com.convenience.store.assessment.di

import android.content.Context
import androidx.room.Room
import com.convenience.store.assessment.database.AppDatabase
import com.convenience.store.authentication.data.repositories.AuthenticationRepositoryImpl
import com.convenience.store.authentication.domain.repositories.AuthenticationRepository
import com.convenience.store.core.data.datasources.EventLogEntityDao
import com.convenience.store.core.data.services.UuidCreatorService
import com.convenience.store.core.domain.services.UuidService
import com.convenience.store.products.data.datasources.ProductEntityDao
import com.convenience.store.products.data.repositories.ProductRepositoryImpl
import com.convenience.store.products.domain.repositories.ProductRepository
import com.convenience.store.products.domain.usecases.ProductAddUseCase
import com.convenience.store.products.domain.usecases.ProductAddUseCaseImpl
import com.convenience.store.stocks.data.datasources.StockEntityDao
import com.convenience.store.stocks.data.repositories.StockRepositoryImpl
import com.convenience.store.stocks.domain.repositories.StockRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Singleton
    @Binds
    abstract fun bindAuthenticationRepository(
        authenticationRepositoryImpl: AuthenticationRepositoryImpl
    ): AuthenticationRepository

    @Singleton
    @Binds
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository

    @Singleton
    @Binds
    abstract fun bindStockRepository(
        stockRepositoryImpl: StockRepositoryImpl
    ): StockRepository


    @Singleton
    @Binds
    abstract fun bindUuidService(
        uuidCreatorService: UuidCreatorService
    ): UuidService

    @Singleton
    @Binds
    abstract fun bindProductAddUseCase(
        productAddUseCaseImpl: ProductAddUseCaseImpl
    ): ProductAddUseCase

    companion object {
        @Provides
        fun provideEventLogEntityDao(db: AppDatabase): EventLogEntityDao {
            return db.eventLogEntityDao()
        }

        @Provides
        fun provideStockEntityDao(db: AppDatabase): StockEntityDao {
            return db.stockEntityDao()
        }

        @Provides
        fun provideProductEntityDao(db: AppDatabase): ProductEntityDao {
            return db.productEntityDao()
        }

        @Singleton
        @Provides
        fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "app_database"
            )
//                    .fallbackToDestructiveMigration() // Utile in fase di sviluppo
                .build()
        }
    }

}