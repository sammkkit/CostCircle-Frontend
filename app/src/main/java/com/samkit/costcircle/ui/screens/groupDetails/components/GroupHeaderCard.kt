package com.samkit.costcircle.ui.screens.groupdetails.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.samkit.costcircle.data.group.dto.SettlementEntryDto
import com.yourapp.costcircle.ui.theme.GreenOwed
import com.yourapp.costcircle.ui.theme.OrangeOwe

@Composable
fun GroupHeaderCard(
    groupName: String,
    settlements: List<SettlementEntryDto>,
    currentUserId: Long
) {
    // 1. Calculate user's net position in this specific group
    val userNetPosition = remember(settlements, currentUserId) {
        val amountOwedToUser = settlements
            .filter { it.receiverUserId == currentUserId }
            .sumOf { it.amount }
        val amountUserOwes = settlements
            .filter { it.payerUserId == currentUserId }
            .sumOf { it.amount }

        amountOwedToUser - amountUserOwes
    }

    // 2. Select theme colors based on the position
    val statusColor = when {
        userNetPosition > 0 -> GreenOwed
        userNetPosition < 0 -> OrangeOwe
        else -> MaterialTheme.colorScheme.primary
    }

    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            statusColor.copy(alpha = 0.15f), // Dynamic color
                            Color.Transparent
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Text(
                text = groupName,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                color = MaterialTheme.colorScheme.onSurface
            )

            // 3. Dynamic status subtitle
            val statusText = when {
                userNetPosition > 0 -> "You are owed ₹${String.format("%.2f", userNetPosition)} in total"
                userNetPosition < 0 -> "You owe ₹${String.format("%.2f", Math.abs(userNetPosition))} in total"
                else -> "Group is fully settled"
            }

            Text(
                text = statusText,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = statusColor
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "CostCircle has calculated the most efficient way to settle all debts within this group.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
