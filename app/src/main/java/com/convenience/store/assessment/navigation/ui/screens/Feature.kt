package com.convenience.store.assessment.navigation.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shop
import androidx.compose.ui.graphics.vector.ImageVector
import com.convenience.store.assessment.R


sealed class Feature(val title: Int, val icon: ImageVector, val route: Screens) {
    data object Products : Feature(
        R.string.nav_products,
        Icons.Default.Shop,
        Screens.ProductsScreen
    )

}