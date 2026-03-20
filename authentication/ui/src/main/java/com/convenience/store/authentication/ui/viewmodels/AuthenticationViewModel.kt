package com.convenience.store.authentication.ui.viewmodels

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convenience.store.authentication.domain.entities.AuthenticationError
import com.convenience.store.authentication.domain.repositories.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
) : ViewModel() {

    val usernameState = TextFieldState()
    val passwordState = TextFieldState()

    private val _uiState: MutableStateFlow<AuthenticationScreenState> =
        MutableStateFlow(AuthenticationScreenState.Init)
    val uiState = _uiState.asStateFlow()

    fun authenticate() {
        viewModelScope.launch {
            _uiState.value = AuthenticationScreenState.Loading
            val username = usernameState.text.toString()
            val password = passwordState.text.toString()
            val loginResult = authenticationRepository.login(username, password)
            loginResult.fold(
                ifLeft = {
                    _uiState.value = AuthenticationScreenState.Error(it)
                },
                ifRight = {
                    _uiState.value = AuthenticationScreenState.Success
                }
            )
        }
    }

}


sealed interface AuthenticationScreenState {
    data object Init : AuthenticationScreenState

    data object Loading : AuthenticationScreenState

    data class Error(val error: AuthenticationError) : AuthenticationScreenState

    data object Success : AuthenticationScreenState

}