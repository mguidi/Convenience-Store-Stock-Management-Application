package com.convenience.store.assessment.navigation.ui.screens

import java.util.UUID

sealed interface Screens {
    data object SplashScreen : Screens
    data object AuthenticationScreen : Screens
    data object ProductsScreen : Screens

    data object ProductCreateScreen : Screens

    data class StockManagementScreen(val productId: UUID, val productName: String) : Screens

}