package com.convenience.store.suppliers.data.di

import com.convenience.store.suppliers.data.repositories.SupplierRepositoryMock
import com.convenience.store.suppliers.domain.repositories.SupplierRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SuppliersDataModule {
    @Binds
    @Singleton
    abstract fun bindSupplierRepository(impl: SupplierRepositoryMock): SupplierRepository

}