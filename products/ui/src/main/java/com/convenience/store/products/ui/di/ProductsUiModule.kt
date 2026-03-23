package com.convenience.store.products.ui.di

import com.convenience.store.core.domain.services.UuidService
import com.convenience.store.products.domain.repositories.ProductRepository
import com.convenience.store.products.domain.usecases.ProductAddUseCase
import com.convenience.store.products.domain.usecases.ProductAddUseCaseImpl
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
    fun bindProductAddUseCase(
        uuidService: UuidService,
        productRepository: ProductRepository
    ): ProductAddUseCase {
        return ProductAddUseCaseImpl(uuidService, productRepository)
    }
}