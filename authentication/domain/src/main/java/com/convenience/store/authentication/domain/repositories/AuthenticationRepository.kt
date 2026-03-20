package com.convenience.store.authentication.domain.repositories

import arrow.core.Either
import com.convenience.store.authentication.domain.entities.AuthenticationError
import com.convenience.store.authentication.domain.entities.AuthenticationState
import kotlinx.coroutines.flow.StateFlow

interface AuthenticationRepository {

    suspend fun login(username: String, password: String): Either<AuthenticationError, Unit>

    suspend fun logout(): Either<Unit, Unit>

    val authenticationState: StateFlow<AuthenticationState>

}
