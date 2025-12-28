package com.samkit.costcircle.ui.screens.groupDetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.samkit.costcircle.data.auth.dto.UserDto
import com.samkit.costcircle.data.group.dto.ExpenseCategory
import com.samkit.costcircle.data.group.dto.TransactionDto
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun TransactionList(
    transactions: List<TransactionDto>,
    members: List<UserDto>,
    currentUserId: Long
) {
    if (transactions.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No transactions yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(transactions) { _, transaction ->
                TransactionItem(
                    transaction = transaction,
                    members = members,
                    currentUserId = currentUserId
                )
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: TransactionDto,
    members: List<UserDto>,
    currentUserId: Long
) {
    val isSettlement = transaction.type == "SETTLEMENT"

    // 1. Resolve Category for Icons & Colors
    val categoryEnum = remember(transaction.category) {
        ExpenseCategory.fromCode(transaction.category)
    }

    // Resolve Names
    val payerName = members.find { it.id == transaction.payerId }?.name?.split(" ")?.firstOrNull() ?: "Unknown"
    val receiverName = if (transaction.receiverId != null) {
        members.find { it.id == transaction.receiverId }?.name?.split(" ")?.firstOrNull() ?: "Unknown"
    } else null

    // 2. Updated Date Formatting (Includes Time)
    // 2. Updated Date Formatting (Robust Fix)
    val dateString = remember(transaction.createdAt) {
        try {
            // 1. Parse UTC string automatically (Handle 'Z' correctly)
            val instant = java.time.Instant.parse(transaction.createdAt)

            // 2. Convert to User's Phone Timezone (e.g., Asia/Kolkata)
            val zoneId = java.time.ZoneId.systemDefault()
            val localDateTime = instant.atZone(zoneId)

            // 3. Format nicely
            val formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd • h:mm a")
            formatter.format(localDateTime)
        } catch (e: Exception) {
            // Fallback: Just show the first 10 chars (Date) if parsing fails
            transaction.createdAt.take(10)
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = if (isSettlement) 2.dp else 0.dp,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 3. DYNAMIC ICON BOX
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        // If Settlement -> Green, Else -> Category Color (e.g., Orange for Food)
                        color = if (isSettlement) Color(0xFFE8F5E9) else categoryEnum.color,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    // If Settlement -> Check, Else -> Category Icon (e.g., Burger)
                    imageVector = if (isSettlement) Icons.Outlined.CheckCircle else categoryEnum.icon,
                    contentDescription = null,
                    // If Settlement -> Dark Green, Else -> Dark Grey/Black transparency
                    tint = if (isSettlement) Color(0xFF2E7D32) else Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 4. TEXT CONTENT
            Column(modifier = Modifier.weight(1f)) {
                if (isSettlement) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if(transaction.payerId == currentUserId) "You" else payerName,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = " paid ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if(transaction.receiverId == currentUserId) "You" else receiverName ?: "Someone",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                } else {
                    Text(
                        text = transaction.description ?: "Expense",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        // Show Category Name in small text (e.g., "Food • You paid")
                        text = "${categoryEnum.displayName} • ${if(transaction.payerId == currentUserId) "You" else payerName} paid",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 5. AMOUNT & TIME
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${transaction.amount}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isSettlement) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}