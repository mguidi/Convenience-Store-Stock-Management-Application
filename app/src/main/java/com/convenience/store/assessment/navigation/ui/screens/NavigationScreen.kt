package com.convenience.store.assessment.navigation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.convenience.store.assessment.navigation.ui.viewmodels.NavigationScreenState
import com.convenience.store.assessment.navigation.ui.viewmodels.NavigationViewModel
import com.convenience.store.authentication.domain.entities.AuthenticationState
import com.convenience.store.authentication.ui.screens.AuthenticationScreen

@Composable
fun NavigationScreen() {
    val viewModel = hiltViewModel<NavigationViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is NavigationScreenState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is NavigationScreenState.Error -> {
                Text(
                    text = state.throwable.message ?: "Unknow error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }

            is NavigationScreenState.Success -> {
                when (state.authenticationState) {
                    is AuthenticationState.Authenticated ->
                        // TODO
                        Text(
                            text = "Authenticated",
                            modifier = Modifier.align(Alignment.Center)
                        )


                    is AuthenticationState.NotAuthenticated ->
                        AuthenticationScreen()
                }

            }
        }
    }

}