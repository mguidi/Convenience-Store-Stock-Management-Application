package com.convenience.store.assessment.navigation.ui.screens

sealed interface Screens {
    data object SplashScreen : Screens
    data object AuthenticationScreen : Screens
    data object ProductsScreen : Screens

    data object ProductAddScreen : Screens

}