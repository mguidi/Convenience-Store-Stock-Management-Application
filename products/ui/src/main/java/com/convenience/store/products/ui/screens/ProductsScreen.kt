package com.convenience.store.products.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.window.core.layout.WindowSizeClass
import com.convenience.store.products.domain.entities.Product
import com.convenience.store.products.ui.viewmodels.ProductsViewModel
import com.convenience.store.core.ui.R
import kotlinx.coroutines.flow.flowOf
import java.math.BigDecimal
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    modifier: Modifier = Modifier,
    onMenuClick: () -> Unit,
    onAddClick: () -> Unit,
) {
    val viewModel: ProductsViewModel = hiltViewModel<ProductsViewModel>()
    val pagingItems = viewModel.products.collectAsLazyPagingItems()

    ProductsScreenInt(modifier, pagingItems, onMenuClick, onAddClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProductsScreenInt(
    modifier: Modifier = Modifier,
    pagingItems: LazyPagingItems<Product>,
    onMenuClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onProductClick: (Product) -> Unit = {}
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val showGrid =
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Products") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = stringResource(R.string.core_nav_menu)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = stringResource(R.string.core_action_search)
                        )
                    }
                    IconButton(onClick = onAddClick) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = stringResource(R.string.core_action_add)
                        )
                    }
                },
            )
        },

        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (showGrid) {
                ProductsGridScreen(
                    pagingItems = pagingItems,
                    onProductClick = onProductClick
                )
            } else {
                ProductsListScreen(
                    pagingItems = pagingItems,
                    onProductClick = onProductClick
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Phone", device = "spec:width=360dp,height=800dp,dpi=411")
@Composable
fun ProductsScreenCompactPreview() {
    val pagingItems = getSamplePagingItems()
    MaterialTheme {
        ProductsScreenInt(
            pagingItems = pagingItems,
        )
    }
}

@Preview(showBackground = true, name = "Tablet", device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun ProductsScreenExpandedPreview() {
    val pagingItems = getSamplePagingItems()
    MaterialTheme {
        ProductsScreenInt(
            pagingItems = pagingItems,
        )
    }
}

@Composable
private fun getSamplePagingItems(): LazyPagingItems<Product> {
    val products = listOf(
        Product(
            id = UUID.randomUUID(),
            name = "Organic Milk",
            description = "Fresh organic whole milk from local farms. 1 Gallon.",
            price = BigDecimal("4.99"),
            barcode = "1234567890123",
            categoryId = UUID.randomUUID(),
            supplierId = UUID.randomUUID(),
            availableQuantity = BigDecimal("25")
        ),
        Product(
            id = UUID.randomUUID(),
            name = "Wheat Bread",
            description = "Whole wheat sliced bread. No preservatives.",
            price = BigDecimal("3.49"),
            barcode = "9876543210987",
            categoryId = UUID.randomUUID(),
            supplierId = UUID.randomUUID(),
            availableQuantity = BigDecimal("15")
        )
    )

    val pagingData = PagingData.from(products)
    return flowOf(pagingData).collectAsLazyPagingItems()
}
