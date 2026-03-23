package com.convenience.store.core.data.di

import com.convenience.store.core.data.services.UuidCreatorService
import com.convenience.store.core.domain.services.UuidService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreDataModule {
    @Binds
    @Singleton
    abstract fun bindUuidService(
        uuidCreatorService: UuidCreatorService
    ): UuidService
}