package com.convenience.store.authentication.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.convenience.store.authentication.domain.entities.AuthenticationError
import com.convenience.store.authentication.ui.R
import com.convenience.store.authentication.ui.viewmodels.AuthenticationScreenState
import com.convenience.store.authentication.ui.viewmodels.AuthenticationViewModel
import com.convenience.store.core.ui.theme.ConvenienceStoreAssessmentTheme
import com.convenience.store.core.ui.widgets.PasswordTextField
import com.convenience.store.core.ui.R as coreR

@Composable
fun AuthenticationScreen() {
    val viewModel = hiltViewModel<AuthenticationViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalResources.current

    LaunchedEffect(Unit) {
        viewModel.init()
    }

    LaunchedEffect(uiState) {
        val state = uiState
        if (state is AuthenticationScreenState.Error) {
            val message = when (state.error) {
                is AuthenticationError.InvalidCredential -> context.getString(R.string.authentication_errors_invalid_credential)
                is AuthenticationError.ExpiredCredential -> context.getString(R.string.authentication_errors_expired_credential)
                is AuthenticationError.Unknown -> context.getString(coreR.string.core_unknown_error)
            }
            snackbarHostState.showSnackbar(message)
        }
    }

    AuthenticationScreenInt(
        usernameState = viewModel.usernameState,
        passwordState = viewModel.passwordState,
        snackbarHostState = snackbarHostState,
        isLoading = uiState is AuthenticationScreenState.Loading,
        onLoginClick = { viewModel.authenticate() })
}

@Composable
internal fun AuthenticationScreenInt(
    usernameState: TextFieldState,
    passwordState: TextFieldState,
    snackbarHostState: SnackbarHostState,
    isLoading: Boolean,
    onLoginClick: () -> Unit
) {

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    state = usernameState,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.authentication_username)) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                PasswordTextField(
                    state = passwordState,
                    label = stringResource(R.string.authentication_password),
                    contentDescription = stringResource(R.string.authentication_password_show)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isLoading

                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(stringResource(R.string.authentication_login))
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AuthenticationScreenPreview() {
    ConvenienceStoreAssessmentTheme {
        AuthenticationScreenInt(
            usernameState = rememberTextFieldState(),
            passwordState = rememberTextFieldState(),
            snackbarHostState = remember { SnackbarHostState() },
            isLoading = false,
            onLoginClick = {}
        )
    }
}