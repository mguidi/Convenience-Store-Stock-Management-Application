package com.convenience.store.assessment.navigation.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convenience.store.authentication.domain.entities.AuthenticationState
import com.convenience.store.authentication.domain.repositories.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    authenticationRepository: AuthenticationRepository,
) : ViewModel() {

    val uiState: StateFlow<NavigationScreenState> =
        authenticationRepository.authenticationState
            .map<AuthenticationState, NavigationScreenState> { authenticated ->
                NavigationScreenState.Success(authenticated)
            }
            .onStart { emit(NavigationScreenState.Loading) }
            .catch { emit(NavigationScreenState.Error(it)) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = NavigationScreenState.Loading
            )
}

sealed interface NavigationScreenState {
    data object Loading : NavigationScreenState

    data class Error(val throwable: Throwable) : NavigationScreenState

    data class Success(val authenticationState: AuthenticationState) : NavigationScreenState

}