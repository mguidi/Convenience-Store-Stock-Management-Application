package com.convenience.store.products.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.convenience.store.core.ui.widgets.GenericExposedDropdown
import com.convenience.store.products.domain.entities.Category
import com.convenience.store.products.domain.entities.ProductError
import com.convenience.store.products.ui.viewmodels.ProductCreateScreenState
import com.convenience.store.products.ui.viewmodels.ProductCreateViewModel
import com.convenience.store.suppliers.domain.entities.Supplier
import com.convenience.store.core.ui.R as coreR
import com.convenience.store.products.ui.R as productsR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCreateScreen(
    onBackClick: () -> Unit,
) {
    val viewModel = hiltViewModel<ProductCreateViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle(emptyList())
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val suppliers by viewModel.suppliers.collectAsStateWithLifecycle(emptyList())
    val selectedSupplier by viewModel.selectedSupplier.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalResources.current

    val onBackClickReset = {
        onBackClick()
        viewModel.reset()
    }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is ProductCreateScreenState.Success -> {
                onBackClickReset()
            }

            is ProductCreateScreenState.Error -> {
                val message = state.errors.joinToString("\n") { error ->
                    when (error) {
                        is ProductError.ValidationError.InvalidName -> context.getString(productsR.string.products_errors_invalid_name)
                        is ProductError.ValidationError.InvalidDescription -> context.getString(
                            productsR.string.products_errors_invalid_description
                        )

                        is ProductError.ValidationError.InvalidPrice -> context.getString(productsR.string.products_errors_invalid_price)
                        is ProductError.ValidationError.InvalidBarcode -> context.getString(
                            productsR.string.products_errors_invalid_barcode
                        )

                        is ProductError.ValidationError.InvalidCategory -> context.getString(
                            productsR.string.products_errors_invalid_category
                        )

                        is ProductError.ValidationError.InvalidSupplier -> context.getString(
                            productsR.string.products_errors_invalid_supplier
                        )

                        is ProductError.RepositoryError.AlreadyExists -> context.getString(productsR.string.products_errors_already_exists)
                        is ProductError.RepositoryError.NotExists -> context.getString(productsR.string.products_errors_not_exists)
                        is ProductError.RepositoryError.DatabaseError -> context.getString(coreR.string.core_database_error)
                        is ProductError.RepositoryError.UnknownError -> context.getString(coreR.string.core_unknown_error)
                    }
                }

                snackbarHostState.showSnackbar(message)
            }

            else -> {}
        }
    }

    ProductCreateScreenInt(
        nameState = viewModel.nameState,
        descriptionState = viewModel.descriptionState,
        priceState = viewModel.priceState,
        barcodeState = viewModel.barcodeState,
        snackbarHostState = snackbarHostState,
        isLoading = uiState is ProductCreateScreenState.Loading,
        categories = categories,
        selectedCategory = selectedCategory,
        suppliers = suppliers,
        selectedSupplier = selectedSupplier,
        onCategorySelected = viewModel::onCategorySelected,
        onSupplierSelected = viewModel::onSupplierSelected,
        onBackClick = onBackClickReset,
        onSaveClick = {
            viewModel.save()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProductCreateScreenInt(
    nameState: TextFieldState,
    descriptionState: TextFieldState,
    priceState: TextFieldState,
    barcodeState: TextFieldState,
    snackbarHostState: SnackbarHostState,
    isLoading: Boolean,
    categories: List<Category>,
    selectedCategory: Category?,
    suppliers: List<Supplier>,
    selectedSupplier: Supplier?,
    onCategorySelected: (Category) -> Unit,
    onSupplierSelected: (Supplier) -> Unit,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {

    val isSaveEnabled = !isLoading &&
            nameState.text.isNotBlank() &&
            descriptionState.text.isNotBlank() &&
            priceState.text.toString().toBigDecimalOrNull() != null &&
            barcodeState.text.isNotBlank() &&
            selectedCategory != null &&
            selectedSupplier != null

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(productsR.string.products_create_product)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick, enabled = !isLoading) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(coreR.string.core_action_back)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onSaveClick,
                        enabled = isSaveEnabled
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = stringResource(coreR.string.core_action_save)
                        )
                    }
                }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                state = nameState,
                label = { Text(stringResource(productsR.string.products_name)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                state = descriptionState,
                label = { Text(stringResource(productsR.string.products_description)) },
                modifier = Modifier.fillMaxWidth(),
                lineLimits = TextFieldLineLimits.MultiLine(minHeightInLines = 3),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                state = priceState,
                label = { Text(stringResource(productsR.string.products_price)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                state = barcodeState,
                label = { Text(stringResource(productsR.string.products_barcode)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            GenericExposedDropdown(
                label = stringResource(productsR.string.products_cateogry),
                items = categories,
                selectedItem = selectedCategory,
                itemLabel = { it.name },
                onItemSelected = onCategorySelected,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            GenericExposedDropdown(
                label = stringResource(productsR.string.products_supplier),
                items = suppliers,
                selectedItem = selectedSupplier,
                itemLabel = { it.name },
                onItemSelected = onSupplierSelected,
                enabled = !isLoading
            )


            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = isSaveEnabled
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(stringResource(coreR.string.core_action_save))
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ProductCreateScreenPreview() {
    MaterialTheme {
        ProductCreateScreenInt(
            nameState = rememberTextFieldState(),
            descriptionState = rememberTextFieldState(),
            priceState = rememberTextFieldState(),
            barcodeState = rememberTextFieldState(),
            snackbarHostState = remember { SnackbarHostState() },
            isLoading = false,
            categories = emptyList(),
            selectedCategory = null,
            suppliers = emptyList(),
            selectedSupplier = null,
            onCategorySelected = {},
            onSupplierSelected = {},
            onBackClick = {},
            onSaveClick = {}
        )
    }
}
