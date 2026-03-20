package com.convenience.store.authentication.domain.entities

sealed interface AuthenticationError {

    data object InvalidCredential : AuthenticationError
    data object ExpiredCredential : AuthenticationError
    data object Unknown : AuthenticationError

}