package com.convenience.store.assessment.navigation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.convenience.store.assessment.navigation.ui.viewmodels.NavigationScreenState
import com.convenience.store.assessment.navigation.ui.viewmodels.NavigationViewModel
import com.convenience.store.authentication.domain.entities.AuthenticationState
import com.convenience.store.authentication.ui.screens.AuthenticationScreen
import com.convenience.store.products.ui.screens.ProductCreateScreen
import com.convenience.store.products.ui.screens.ProductsScreen
import com.convenience.store.stocks.ui.screens.StockManagementScreen

@Composable
fun NavigationScreen() {
    val backStack = remember { mutableStateListOf<Screens>(Screens.SplashScreen) }
    val viewModel = hiltViewModel<NavigationViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // handle navigation base on the uiState
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is NavigationScreenState.Success -> {
                val targetScreen = when (state.authenticationState) {
                    is AuthenticationState.Authenticated -> Screens.ProductsScreen
                    is AuthenticationState.NotAuthenticated -> Screens.AuthenticationScreen
                }

                // replace splashScreen with the target screen
                if (backStack.lastOrNull() != targetScreen) {
                    backStack.clear()
                    backStack.add(targetScreen)
                }
            }

            is NavigationScreenState.Error -> {
                // TODO: handle error
            }

            is NavigationScreenState.Loading -> {
                if (backStack.isEmpty()) backStack.add(Screens.SplashScreen)
            }
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                is Screens.SplashScreen -> NavEntry(key) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }

                is Screens.AuthenticationScreen -> NavEntry(key) {
                    AuthenticationScreen()
                }

                is Screens.ProductsScreen -> NavEntry(key) {
                    MainScreen(backStack) { onMenuClick ->
                        ProductsScreen(
                            onMenuClick = onMenuClick,
                            onAddClick = { backStack.add(Screens.ProductCreateScreen) },
                            onProductClick = {
                                backStack.add(
                                    Screens.StockManagementScreen(
                                        it.id,
                                        it.name
                                    )
                                )
                            }
                        )
                    }
                }

                is Screens.ProductCreateScreen -> NavEntry(key) {
                    MainScreen(backStack) { _ ->
                        ProductCreateScreen(
                            onBackClick = { backStack.removeAt(backStack.size - 1) },
                        )
                    }
                }

                is Screens.StockManagementScreen -> NavEntry(key) {
                    MainScreen(backStack) { _ ->
                        StockManagementScreen(
                            key.productId,
                            key.productName,
                            onBackClick = { backStack.removeAt(backStack.size - 1) },
                        )
                    }
                }
            }
        }
    )

}