package com.convenience.store.assessment

import com.convenience.store.authentication.data.repositories.AuthenticationRepositoryImpl
import com.convenience.store.authentication.domain.repositories.AuthenticationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class Module {

    @Singleton
    @Binds
    abstract fun bindAuthenticationRepository(
        authenticationRepositoryImpl: AuthenticationRepositoryImpl
    ): AuthenticationRepository

}