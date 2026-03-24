package com.convenience.store.stocks.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.convenience.store.stocks.domain.entities.StockError
import com.convenience.store.stocks.ui.R
import com.convenience.store.stocks.ui.viewmodels.StockManagementScreenState
import com.convenience.store.stocks.ui.viewmodels.StockManagementViewModel
import java.util.UUID
import com.convenience.store.core.ui.R as coreR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockManagementScreen(
    productId: UUID,
    productName: String,
    onBackClick: () -> Unit
) {
    val viewModel = hiltViewModel<StockManagementViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentStock by viewModel.currentStock.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalResources.current

    LaunchedEffect(productId) {
        viewModel.loadStock(productId)
    }

    LaunchedEffect(uiState) {
        val state = uiState
        if (state is StockManagementScreenState.Success) {
            viewModel.reset()

        } else if (state is StockManagementScreenState.Error) {
            val message = state.errors.joinToString("\n") { error ->
                when (error) {
                    is StockError.ValidationError.InvalidQuantity -> context.getString(R.string.stocks_errors_invalid_quantity)
                    is StockError.RepositoryError.InsufficientStock -> context.getString(R.string.stocks_errors_insufficient_stock)
                    is StockError.RepositoryError.DatabaseError -> context.getString(coreR.string.core_database_error)
                    is StockError.RepositoryError.Unknown -> context.getString(coreR.string.core_unknown_error)
                }
            }
            snackbarHostState.showSnackbar(message)
        }
    }

    StockManagementScreenInt(
        productName = productName,
        currentStockQuantity = currentStock?.quantity?.toPlainString() ?: "0",
        adjustQuantityState = viewModel.quantityState,
        isLoading = uiState is StockManagementScreenState.Loading,
        snackbarHostState = snackbarHostState,
        onBackClick = onBackClick,
        onAddClick = viewModel::addStock,
        onRemoveClick = viewModel::removeStock
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StockManagementScreenInt(
    productName: String,
    currentStockQuantity: String,
    adjustQuantityState: TextFieldState,
    snackbarHostState: SnackbarHostState,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.stocks_manage_stock)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(coreR.string.core_action_back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = productName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.stocks_current_stock),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )

            Text(
                text = currentStockQuantity,
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                state = adjustQuantityState,
                label = { Text(stringResource(R.string.stocks_adjust_quantity)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onRemoveClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    enabled = !isLoading
                ) {
                    Icon(Icons.Default.Remove, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.stocks_remove))
                }

                Button(
                    onClick = onAddClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    enabled = !isLoading
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.stocks_add))
                }
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(24.dp))
                CircularProgressIndicator()
            }

            // TODO: Aggiungere qui la lista delle transazioni (Stock Movements)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StockManagementScreenPreview() {
    MaterialTheme {
        StockManagementScreenInt(
            productName = "Pane Fresco",
            currentStockQuantity = "10.5",
            adjustQuantityState = rememberTextFieldState("5"),
            snackbarHostState = remember { SnackbarHostState() },
            isLoading = false,
            onBackClick = {},
            onAddClick = {},
            onRemoveClick = {}
        )
    }
}
