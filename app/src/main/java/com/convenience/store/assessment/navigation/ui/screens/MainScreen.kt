package com.convenience.store.assessment.navigation.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.convenience.store.assessment.R


@Composable
fun MainScreen(
    backStack: SnapshotStateList<Screens>,
    content: @Composable (onMenuClick: () -> Unit) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val features = remember { listOf(Feature.Products) }

    val selectedFeature = when (backStack.firstOrNull()) {
        is Screens.ProductsScreen -> Feature.Products
        else -> null
    }


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    stringResource(R.string.app_name),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                features.forEach { feature ->
                    val label = stringResource(feature.title)
                    NavigationDrawerItem(
                        label = { Text(label) },
                        selected = selectedFeature == feature,
                        onClick = {
                            scope.launch { drawerState.close() }
                            if (selectedFeature != feature) {
                                backStack.clear()
                                backStack.add(feature.route)
                            }
                        },
                        icon = { Icon(feature.icon, contentDescription = label) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {

        NotificationPermissionWrapper {
            content {
                scope.launch { drawerState.open() }
            }
        }

    }
}
