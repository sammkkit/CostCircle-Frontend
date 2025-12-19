package com.samkit.costcircle.ui.screens.addExpense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.samkit.costcircle.ui.screens.addExpense.models.AddExpenseUiState
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    onClose: () -> Unit
) {
    var uiState by remember {
        mutableStateOf(AddExpenseUiState())
    }
    LaunchedEffect(uiState.isSubmitting) {
        if (uiState.isSubmitting) {
            delay(1200)
            uiState = uiState.copy(isSubmitting = false)
            onClose()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add expense") },
                navigationIcon = {
                    IconButton(
                        onClick = { if (!uiState.isSubmitting) onClose() }
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Group info (static for now)
            Text(
                text = "With group",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Goa Trip",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Amount
            OutlinedTextField(
                value = uiState.amount,
                onValueChange = {
                    uiState = uiState.copy(
                        amount = it,
                        errorMessage = null
                    )
                },
                label = { Text("Amount") },
                leadingIcon = { Text("₹") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.errorMessage != null
            )

            // Description
            OutlinedTextField(
                value = uiState.description,
                onValueChange = {
                    uiState = uiState.copy(description = it)
                },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            SettingRow(label = "Paid by", value = "You")
            SettingRow(label = "Split", value = "Equally")

            // Error message
            uiState.errorMessage?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Save button
            Button(
                onClick = {
                    if (!uiState.isValid) {
                        uiState = uiState.copy(
                            errorMessage = "Please enter a valid amount"
                        )
                        return@Button
                    }

                    // 🔴 Fake submit (simulate network)
                    uiState = uiState.copy(
                        isSubmitting = true,
                        errorMessage = null
                    )


                },
                enabled = uiState.isValid && !uiState.isSubmitting,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Save expense")
                }
            }
        }
    }
}


@Composable
fun SettingRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null
            )
        }
    }
}

