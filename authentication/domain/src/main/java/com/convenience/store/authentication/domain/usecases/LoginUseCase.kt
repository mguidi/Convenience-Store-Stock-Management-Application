package com.convenience.store.authentication.domain.usecases

import arrow.core.Either
import com.convenience.store.authentication.domain.entities.AuthenticationError
import com.convenience.store.authentication.domain.repositories.AuthenticationRepository

interface LoginUseCase {

    suspend operator fun invoke(
        username: String,
        password: String
    ): Either<AuthenticationError, Unit>
}

class LoginUseCaseImpl(
    private val authenticationRepository: AuthenticationRepository
) : LoginUseCase {
    override suspend fun invoke(
        username: String,
        password: String
    ): Either<AuthenticationError, Unit> {
        return authenticationRepository.login(username, password)
    }
}