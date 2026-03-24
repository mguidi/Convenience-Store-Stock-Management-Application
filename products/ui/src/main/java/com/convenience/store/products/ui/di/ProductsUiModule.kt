package com.convenience.store.products.ui.di

import com.convenience.store.core.domain.services.UuidService
import com.convenience.store.products.domain.repositories.ProductRepository
import com.convenience.store.products.domain.services.ProductSyncService
import com.convenience.store.products.domain.usecases.ProductCreateUseCase
import com.convenience.store.products.domain.usecases.ProductAddUseCaseImpl
import com.convenience.store.products.domain.usecases.ProductsGetUseCase
import com.convenience.store.products.domain.usecases.ProductsGetUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProductsUiModule {
    @Provides
    @Singleton
    fun provideProductAddUseCase(
        uuidService: UuidService,
        productSyncService: ProductSyncService,
        productRepository: ProductRepository
    ): ProductCreateUseCase {
        return ProductAddUseCaseImpl(uuidService, productSyncService, productRepository)
    }

    @Provides
    @Singleton
    fun provideProductsGetUseCase(
        productRepository: ProductRepository
    ): ProductsGetUseCase {
        return ProductsGetUseCaseImpl(productRepository)
    }
}