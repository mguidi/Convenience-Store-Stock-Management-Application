package com.convenience.store.products.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.convenience.store.products.domain.entities.Product
import com.convenience.store.products.ui.R
import com.convenience.store.products.ui.viewmodels.ProductsViewModel
import com.convenience.store.stocks.domain.entities.Stock
import java.math.BigDecimal
import java.util.UUID

@Composable
fun ProductItem(
    product: Product,
    onClick: () -> Unit
) {

    val viewModel = hiltViewModel<ProductsViewModel>()
    val stock by viewModel.getStockById(product.id)
        .collectAsState(initial = Stock(product.id, BigDecimal.ZERO))

    ProductItemInt(
        product,
        stockQuantity = stock?.quantity ?: BigDecimal.ZERO,
        onClick
    )
}

@Composable
internal fun ProductItemInt(
    product: Product,
    stockQuantity: BigDecimal,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = stringResource(R.string.products_currency_dollar, product.price),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.products_barcode_colon, product.barcode),
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = stringResource(R.string.products_quantity_colon, stockQuantity),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (stockQuantity <= BigDecimal.ZERO) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.outline
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductItemIntPreview() {
    val sampleProduct = Product(
        id = UUID.randomUUID(),
        name = "Organic Milk",
        description = "Fresh organic whole milk from local farms. 1 Gallon.",
        price = BigDecimal("4.99"),
        barcode = "1234567890123",
        categoryId = UUID.randomUUID(),
        supplierId = UUID.randomUUID(),
    )
    MaterialTheme {
        ProductItemInt(product = sampleProduct, stockQuantity = BigDecimal.ZERO, onClick = {})
    }
}
