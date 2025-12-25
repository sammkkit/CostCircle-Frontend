package com.samkit.costcircle.ui.screens.addExpense.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.samkit.costcircle.data.group.dto.SplitType
import com.samkit.costcircle.ui.screens.addExpense.states.AddExpenseContract
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseSplitSheet(
    state: AddExpenseContract.State,
    onEvent: (AddExpenseContract.Event) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // --- NEW: VALIDATION LOGIC ---
    val isError: Boolean = when(state.splitType) {
        SplitType.EQUAL -> state.splitMembers.none { it.isSelected } // Error if 0 selected
        SplitType.EXACT -> abs(state.remainingAmount) > 0.02 // Error if not 0 (approx)
        SplitType.PERCENTAGE -> abs(state.remainingPercent) > 0.1 // Error if not 0
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            // Header
            Text(
                text = "Split options",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 1. The Tabs
            SplitTypeTabs(
                selectedType = state.splitType,
                onTypeSelected = { onEvent(AddExpenseContract.Event.SplitTypeChanged(it)) }
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // 2. The User List
            LazyColumn(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .heightIn(max = 400.dp)
            ) {
                items(state.splitMembers) { member ->
                    SplitUserRow(
                        item = member,
                        splitType = state.splitType,
                        currencySymbol = "₹",
                        onToggle = { onEvent(AddExpenseContract.Event.SplitMemberToggled(member.user.id)) },
                        onValueChange = {
                            onEvent(AddExpenseContract.Event.SplitValueChanged(member.user.id, it))
                        }
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // 3. The Footer (Summary & Done)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    val label = when(state.splitType) {
                        SplitType.EQUAL -> "${state.splitMembers.count { it.isSelected }} selected"
                        SplitType.EXACT -> {
                            val sign = if (state.remainingAmount > 0) "left" else "over"
                            "₹${String.format("%.2f", abs(state.remainingAmount))} $sign"
                        }
                        SplitType.PERCENTAGE -> {
                            val sign = if (state.remainingPercent > 0) "left" else "over"
                            "${String.format("%.1f", abs(state.remainingPercent))}% $sign"
                        }
                    }

                    Text(
                        text = "Summary",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                // DISABLE BUTTON IF ERROR
                Button(
                    onClick = onDismiss,
                    enabled = !isError // <--- The Validation Fix
                ) {
                    Text("Done")
                }
            }
        }
    }
}