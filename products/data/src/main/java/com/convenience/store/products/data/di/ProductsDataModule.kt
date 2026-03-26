package com.convenience.store.products.data.di

import com.convenience.store.products.data.datasources.remote.ProductApiService
import com.convenience.store.products.data.datasources.remote.ProductApiServiceMockImpl
import com.convenience.store.products.data.repositories.CategoryRepositoryMock
import com.convenience.store.products.data.repositories.ProductRepositoryImpl
import com.convenience.store.products.data.services.ProductSyncServiceImpl
import com.convenience.store.products.domain.repositories.CategoryRepository
import com.convenience.store.products.domain.repositories.ProductRepository
import com.convenience.store.products.domain.services.ProductSyncService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductsDataModule {
    @Binds
    @Singleton
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository

    @Binds
    @Singleton
    abstract fun bindProductApiService(impl: ProductApiServiceMockImpl): ProductApiService

    @Binds
    @Singleton
    abstract fun bindProductSyncService(impl: ProductSyncServiceImpl): ProductSyncService

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(impl: CategoryRepositoryMock): CategoryRepository

}