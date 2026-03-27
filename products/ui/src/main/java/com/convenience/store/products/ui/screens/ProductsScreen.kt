package com.convenience.store.products.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.placeCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.window.core.layout.WindowSizeClass
import com.convenience.store.products.domain.entities.Category
import com.convenience.store.products.domain.entities.Product
import com.convenience.store.products.ui.R
import com.convenience.store.products.ui.viewmodels.ProductsViewModel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.UUID
import com.convenience.store.core.ui.R as coreR


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    barcode: String?,
    onMenuClick: () -> Unit,
    onAddClick: () -> Unit,
    onScanClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    onProductLongClick: (Product) -> Unit
) {
    val viewModel: ProductsViewModel = hiltViewModel<ProductsViewModel>()
    val pagingItems = viewModel.products.collectAsLazyPagingItems()
    val categories by viewModel.categories.collectAsStateWithLifecycle(emptyList())
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsStateWithLifecycle()

    LaunchedEffect(barcode) {
        if (barcode != null) {
            viewModel.barcodeState.edit {
                replace(0, length, barcode)
                placeCursorAtEnd()
            }
        }
    }
    var showFilters by remember { mutableStateOf(false) }

    ProductsScreenInt(
        barcodeState = viewModel.barcodeState,
        pagingItems = pagingItems,
        categories = categories,
        selectedCategoryId = selectedCategoryId,
        showFilters = showFilters,
        onShowFiltersChange = { showFilters = it },
        onCategorySelected = viewModel::onCategorySelected,
        onMenuClick = onMenuClick,
        onAddClick = onAddClick,
        onScanClick = onScanClick,
        onProductClick = onProductClick,
        onProductLongClick = onProductLongClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProductsScreenInt(
    barcodeState: TextFieldState,
    pagingItems: LazyPagingItems<Product>,
    categories: List<Category>,
    selectedCategoryId: UUID?,
    showFilters: Boolean,
    onShowFiltersChange: (Boolean) -> Unit,
    onCategorySelected: (UUID?) -> Unit,
    onMenuClick: () -> Unit,
    onAddClick: () -> Unit,
    onScanClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    onProductLongClick: (Product) -> Unit
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val showGrid =
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.products_products)) },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = stringResource(coreR.string.core_nav_menu)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onScanClick) {
                        Icon(
                            Icons.Default.QrCodeScanner,
                            contentDescription = stringResource(coreR.string.core_action_scan)
                        )
                    }
                    IconButton(onClick = { onShowFiltersChange(true) }) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = stringResource(coreR.string.core_action_search)
                        )
                    }
                    IconButton(onClick = onAddClick) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = stringResource(coreR.string.core_action_add)
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (showGrid) {
                ProductsGridScreen(
                    pagingItems = pagingItems,
                    onProductClick = onProductClick,
                    onProductLongClick = onProductLongClick
                )
            } else {
                ProductsListScreen(
                    pagingItems = pagingItems,
                    onProductClick = onProductClick,
                    onProductLongClick = onProductLongClick
                )
            }

            if (showFilters) {
                ModalBottomSheet(
                    onDismissRequest = { onShowFiltersChange(false) },
                    sheetState = sheetState
                ) {
                    FilterScreen(
                        barcodeState = barcodeState,
                        categories = categories,
                        selectedCategoryId = selectedCategoryId,
                        onCategorySelected = {
                            onCategorySelected(it)
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    onShowFiltersChange(false)
                                }
                            }
                        },
                        onClose = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    onShowFiltersChange(false)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}


@Preview(
    showBackground = true,
    name = "Phone",
    device = "spec:width=360dp,height=800dp,dpi=411"
)
@Composable
fun ProductsScreenCompactPreview() {
    val pagingItems = getSamplePagingItems()
    MaterialTheme {
        ProductsScreenInt(
            barcodeState = TextFieldState(),
            pagingItems = pagingItems,
            categories = emptyList(),
            selectedCategoryId = null,
            showFilters = false,
            onShowFiltersChange = {},
            onCategorySelected = {},
            onMenuClick = {},
            onAddClick = {},
            onScanClick = {},
            onProductClick = {},
            onProductLongClick = {}
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
            synced = false
        ),
        Product(
            id = UUID.randomUUID(),
            name = "Wheat Bread",
            description = "Whole wheat sliced bread. No preservatives.",
            price = BigDecimal("3.49"),
            barcode = "9876543210987",
            categoryId = UUID.randomUUID(),
            supplierId = UUID.randomUUID(),
            synced = true
        )
    )

    val pagingData = PagingData.from(products)
    return flowOf(pagingData).collectAsLazyPagingItems()
}
