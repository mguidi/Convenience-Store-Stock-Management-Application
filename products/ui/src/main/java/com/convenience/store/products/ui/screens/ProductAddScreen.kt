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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.convenience.store.products.ui.viewmodels.ProductAddScreenState
import com.convenience.store.products.ui.viewmodels.ProductAddViewModel
import com.convenience.store.core.ui.R as coreR
import com.convenience.store.products.ui.R as productsR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductAddScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
) {
    val viewModel = hiltViewModel<ProductAddViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.init()
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is ProductAddScreenState.Success -> {
                onBackClick()
            }
            is ProductAddScreenState.Error -> {
                // TODO convert error to string resources
                snackbarHostState.showSnackbar(
                    message = (uiState as ProductAddScreenState.Error).error.toString()
                )
            }
            else -> {}
        }
    }

    ProductAddScreenInt(
        modifier = modifier,
        nameState = viewModel.nameState,
        descriptionState = viewModel.descriptionState,
        priceState = viewModel.priceState,
        barcodeState = viewModel.barcodeState,
        availableQuantityState = viewModel.availableQuantityState,
        snackbarHostState = snackbarHostState,
        isLoading = uiState is ProductAddScreenState.Loading,
        onBackClick = onBackClick,
        onSaveClick = {
            viewModel.save()
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProductAddScreenInt(
    modifier: Modifier = Modifier,
    nameState: TextFieldState = rememberTextFieldState(),
    descriptionState: TextFieldState = rememberTextFieldState(),
    priceState: TextFieldState = rememberTextFieldState(),
    barcodeState: TextFieldState = rememberTextFieldState(),
    availableQuantityState: TextFieldState = rememberTextFieldState(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    isLoading: Boolean = false,
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(productsR.string.products_add_product)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(coreR.string.core_action_back)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onSaveClick,
                        enabled = !isLoading && nameState.text.isNotBlank() && priceState.text.toString()
                            .toBigDecimalOrNull() != null
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = stringResource(coreR.string.core_action_save)
                        )
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
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

            OutlinedTextField(
                state = availableQuantityState,
                label = { Text(stringResource(productsR.string.products_initial_quantity)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && nameState.text.isNotBlank() && priceState.text.toString()
                    .toBigDecimalOrNull() != null
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
fun ProductAddScreenPreview() {
    MaterialTheme {
        ProductAddScreenInt()
    }
}
