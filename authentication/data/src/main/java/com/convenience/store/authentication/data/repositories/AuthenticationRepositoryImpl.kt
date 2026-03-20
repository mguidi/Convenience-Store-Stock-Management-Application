package com.convenience.store.authentication.data.repositories

import arrow.core.Either
import com.convenience.store.authentication.domain.entities.AuthenticationError
import com.convenience.store.authentication.domain.entities.AuthenticationState
import com.convenience.store.authentication.domain.repositories.AuthenticationRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor() : AuthenticationRepository {

    private val _authenticationState: MutableStateFlow<AuthenticationState> =
        MutableStateFlow(AuthenticationState.NotAuthenticated)
    override val authenticationState = _authenticationState.asStateFlow()

    override suspend fun login(
        username: String,
        password: String
    ): Either<AuthenticationError, Unit> {
        delay(2000)
        if (username == "admin" && password == "password") {
            _authenticationState.value = AuthenticationState.Authenticated(id = "1", username)
            return Either.Right(Unit)

        } else {
            return Either.Left(AuthenticationError.InvalidCredential)
        }
    }

    override suspend fun logout(): Either<Unit, Unit> {
        delay(2000)
        return Either.Right(Unit)
    }
}