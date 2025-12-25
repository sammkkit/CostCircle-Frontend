package com.samkit.costcircle.ui.screens.addExpense

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.outlined.Notes
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samkit.costcircle.data.group.dto.GroupSummaryDto
import com.samkit.costcircle.ui.screens.addExpense.components.ExpenseSplitSheet
import com.samkit.costcircle.ui.screens.addExpense.components.GroupSelectionSheet
import com.samkit.costcircle.ui.screens.addExpense.components.SimplifiedGroupBottomBar
import com.samkit.costcircle.ui.screens.addExpense.states.AddExpenseContract
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    onClose: () -> Unit,
    viewModel: AddExpenseViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    // Sheet States
    var showGroupSheet by remember { mutableStateOf(false) }
    var showSplitSheet by remember { mutableStateOf(false) } // <--- NEW

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                AddExpenseContract.Effect.ExpenseSaved -> {
                    Toast.makeText(context, "Expense saved!", Toast.LENGTH_SHORT).show()
                    viewModel.onEvent(AddExpenseContract.Event.Reset)
                    onClose()
                }
                is AddExpenseContract.Effect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // --- SHEETS ---
    if (showGroupSheet) {
        GroupSelectionSheet(
            groups = state.groups,
            selectedGroupId = state.selectedGroup?.groupId,
            onGroupSelected = { group ->
                viewModel.onEvent(AddExpenseContract.Event.GroupSelected(group))
                showGroupSheet = false
            },
            onDismiss = { showGroupSheet = false }
        )
    }

    // <--- NEW SHEET ---
    if (showSplitSheet) {
        ExpenseSplitSheet(
            state = state,
            onEvent = viewModel::onEvent,
            onDismiss = { showSplitSheet = false }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                title = { Text("Add expense", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.onEvent(AddExpenseContract.Event.SaveExpense) },
                        enabled = !state.isSubmitting
                    ) {
                        if (state.isSubmitting) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Save", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            )
        },
        bottomBar = {
            SimplifiedGroupBottomBar(
                selectedGroupName = state.selectedGroup?.groupName ?: "Choose group",
                onClick = { showGroupSheet = true }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Group Selection Row (Kept same)
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("With you and:", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.width(12.dp))
                Surface(
                    onClick = { showGroupSheet = true },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Groups, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = state.selectedGroup?.groupName ?: "Choose group",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Description Input (Kept same)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconContainer { Icon(Icons.Outlined.Notes, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                Spacer(modifier = Modifier.width(16.dp))
                UnderlinedInput(
                    value = state.description,
                    onValueChange = { viewModel.onEvent(AddExpenseContract.Event.DescriptionChanged(it)) },
                    placeholder = "Enter a description",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Amount Input (Kept same)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconContainer { Text("₹", style = MaterialTheme.typography.titleLarge) }
                Spacer(modifier = Modifier.width(16.dp))
                UnderlinedInput(
                    value = state.amount,
                    onValueChange = { viewModel.onEvent(AddExpenseContract.Event.AmountChanged(it)) },
                    placeholder = "0.00",
                    modifier = Modifier.weight(1f),
                    isAmount = true
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // --- UPDATED SPLIT SUMMARY ROW ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Paid by ", color = MaterialTheme.colorScheme.onSurface)
                BetterActionChip(label = "you") { haptic.performHapticFeedback(HapticFeedbackType.LongPress) }
                Text(" and split ", color = MaterialTheme.colorScheme.onSurface)

                // DYNAMIC LABEL based on Split Type
                val splitLabel = when(state.splitType) {
                    com.samkit.costcircle.data.group.dto.SplitType.EQUAL -> "equally"
                    com.samkit.costcircle.data.group.dto.SplitType.PERCENTAGE -> "by %"
                    com.samkit.costcircle.data.group.dto.SplitType.EXACT -> "unequally"
                }

                BetterActionChip(label = splitLabel) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    // OPEN THE SPLIT SHEET
                    if (state.selectedGroup != null) {
                        showSplitSheet = true
                    } else {
                        Toast.makeText(context, "Select a group first", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}



@Composable
fun UnderlinedInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isAmount: Boolean = false
) {
    Column(modifier = modifier) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.headlineMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = if (isAmount) 32.sp else 18.sp
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isAmount) KeyboardType.Decimal else KeyboardType.Text,
                imeAction = if (isAmount) ImeAction.Done else ImeAction.Next
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = if (isAmount) 32.sp else 18.sp
                        )
                    )
                }
                innerTextField()
            }
        )
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
fun BetterActionChip(
    label: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = MaterialTheme.colorScheme.primary, // Using your AccentTeal
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun IconContainer(
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp) // Standard size for a touch-friendly/visual anchor
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant, // Your DividerDark color
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}