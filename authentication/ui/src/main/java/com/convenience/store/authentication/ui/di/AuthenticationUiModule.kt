package com.convenience.store.authentication.ui.di

import com.convenience.store.authentication.domain.repositories.AuthenticationRepository
import com.convenience.store.authentication.domain.usecases.LoginUseCase
import com.convenience.store.authentication.domain.usecases.LoginUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthenticationUiModule {
    @Provides
    @Singleton
    fun provideLoginUseCase(
        authenticationRepository: AuthenticationRepository
    ): LoginUseCase {
        return LoginUseCaseImpl(authenticationRepository)
    }
}