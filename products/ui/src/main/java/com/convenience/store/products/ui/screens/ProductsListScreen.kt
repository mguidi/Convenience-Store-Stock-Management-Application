package com.convenience.store.products.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.convenience.store.products.domain.entities.Product
import com.convenience.store.products.ui.R
import kotlinx.coroutines.flow.flowOf
import java.math.BigDecimal
import java.util.UUID
import com.convenience.store.core.ui.R as coreR


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsListScreen(
    pagingItems: LazyPagingItems<Product>,
    onProductClick: (Product) -> Unit,
    onProductLongClick: (Product) -> Unit,
    itemContent: @Composable (Product) -> Unit = { product ->
        ProductItem(
            product = product,
            onClick = { onProductClick(product) },
            onLongClick = { onProductLongClick(product) }
        )
    }
) {
    val isRefreshing = pagingItems.loadState.refresh is LoadState.Loading

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { pagingItems.refresh() },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(
                count = pagingItems.itemCount,
                key = pagingItems.itemKey { it.id }
            ) { index ->
                val product = pagingItems[index]
                if (product != null) {
                    itemContent(product)
                }
            }

            when (val state = pagingItems.loadState.append) {
                is LoadState.Loading -> {
                    item {
                        LoadingIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }

                is LoadState.Error -> {
                    item {
                        ErrorItem(
                            message = state.error.localizedMessage
                                ?: stringResource(coreR.string.core_unknown_error),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                else -> {}
            }
        }

        // Gestione stato di caricamento iniziale (Refresh)
        when (val state = pagingItems.loadState.refresh) {
            is LoadState.Error -> {
                ErrorItem(
                    message = state.error.localizedMessage
                        ?: stringResource(coreR.string.core_unknown_error),
                    modifier = Modifier.fillMaxSize()
                )
            }

            else -> {
                if (pagingItems.itemCount == 0 && state is LoadState.NotLoading) {
                    EmptyState(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Composable
private fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(modifier = modifier.padding(16.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorItem(message: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.padding(16.dp), contentAlignment = Alignment.Center) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(text = stringResource(R.string.products_no_products_found))
    }
}

@Preview(showBackground = true)
@Composable
fun ProductsListScreenPreview() {
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
    val pagingItems = flowOf(pagingData).collectAsLazyPagingItems()

    MaterialTheme {
        ProductsListScreen(
            pagingItems = pagingItems,
            onProductClick = {},
            onProductLongClick = {},
            itemContent = { product ->
                ProductItemInt(
                    product = product,
                    stockQuantity = BigDecimal.TEN,
                    onClick = {},
                    onLongClick = {}
                )
            }
        )
    }
}
