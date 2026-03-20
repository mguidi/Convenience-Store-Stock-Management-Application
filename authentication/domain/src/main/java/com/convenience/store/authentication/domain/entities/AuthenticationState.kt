package com.convenience.store.authentication.domain.entities

sealed interface AuthenticationState {
    data class Authenticated(val id: String, val username: String) : AuthenticationState
    data object NotAuthenticated : AuthenticationState

}

