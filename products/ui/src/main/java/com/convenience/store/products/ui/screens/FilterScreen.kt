package com.convenience.store.products.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.convenience.store.products.domain.entities.Category
import com.convenience.store.products.ui.R
import java.util.UUID

@Composable
fun FilterScreen(
    barcodeState: TextFieldState,
    categories: List<Category>,
    selectedCategoryId: UUID?,
    onCategorySelected: (UUID?) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.products_filters),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.products_filter_by_barcode),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        TextField(
            state = barcodeState,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.products_filter_by_barcode)) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.products_filter_by_category),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            item {
                CategoryFilterItem(
                    name = stringResource(R.string.products_all_categories),
                    isSelected = selectedCategoryId == null,
                    onClick = { onCategorySelected(null) }
                )
            }
            items(categories) { category ->
                CategoryFilterItem(
                    name = category.name,
                    isSelected = category.id == selectedCategoryId,
                    onClick = { onCategorySelected(category.id) }
                )
            }
        }

    }
}

@Composable
private fun CategoryFilterItem(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick
        )
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
        if (isSelected) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun FilterScreenPreview() {

    val categories = listOf(
        Category(
            UUID.fromString("019d2616-21ee-78b4-a43c-fef07b5ff7ab"),
            "Beverages",
        ),
        Category(
            UUID.fromString("019d2616-21ee-78f9-a43d-976eddb2f099"),
            "Snacks",
        ),
        Category(
            UUID.fromString("019d2616-21ee-7943-a43e-b38a2961adb6"),
            "Dairy",
        )
    )

    FilterScreen(
        barcodeState = TextFieldState(initialText = "123456789012"),
        categories = categories,
        selectedCategoryId = null,
        onCategorySelected = {},
        onClose = {}
    )
}