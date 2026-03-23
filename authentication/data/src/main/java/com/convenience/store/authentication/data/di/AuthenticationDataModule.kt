package com.convenience.store.authentication.data.di

import com.convenience.store.authentication.data.repositories.AuthenticationRepositoryImpl
import com.convenience.store.authentication.domain.repositories.AuthenticationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthenticationDataModule {
    @Binds
    @Singleton
    abstract fun bindAuthenticationRepository(
        impl: AuthenticationRepositoryImpl
    ): AuthenticationRepository

}