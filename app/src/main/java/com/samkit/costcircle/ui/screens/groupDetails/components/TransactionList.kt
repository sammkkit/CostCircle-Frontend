package com.samkit.costcircle.ui.screens.groupDetails.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samkit.costcircle.core.utils.parseDateString
import com.samkit.costcircle.data.group.dto.TransactionDto
import java.time.ZonedDateTime
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun TransactionList(transactions: List<TransactionDto>) {
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
            itemsIndexed(transactions) { index, transaction ->
                TransactionItem(transaction, index)
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: TransactionDto, index: Int) {
    val animState = remember { MutableTransitionState(false) }.apply { targetState = true }

    // Parse the date
    val (day, month) = parseDateString(transaction.createdAt)

    AnimatedVisibility(
        visibleState = animState,
        enter = slideInVertically(initialOffsetY = { 50 * (index + 1) }) + fadeIn()
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow, // Slightly lighter than variant
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .height(IntrinsicSize.Min), // Ensures separation line stretches
                verticalAlignment = Alignment.CenterVertically
            ) {
                // --- 1. THE CALENDAR LEAF (Date) ---
                Column(
                    modifier = Modifier
                        .width(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = day,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = month,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // --- 2. MIDDLE CONTENT (Description + Payer) ---
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = transaction.description ?: "Expense",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Small Inline Payer Row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val initial = transaction.paidByName?.take(1)?.uppercase() ?: "?"
                        val payerName = if (transaction.wasPaidByMe) "You" else (transaction.paidByName?.split(" ")?.firstOrNull() ?: "Unknown")

                        // Tiny Avatar
                        Surface(
                            modifier = Modifier.size(18.dp),
                            shape = CircleShape,
                            color = if (transaction.wasPaidByMe)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            else
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = initial,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = "Paid by $payerName",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // --- 3. AMOUNT ---
                Text(
                    text = "₹${formatAmount(transaction.amount)}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// Simple formatter to remove .00 if not needed
fun formatAmount(amount: Double): String {
    return if (amount % 1.0 == 0.0) {
        String.format("%.0f", amount)
    } else {
        String.format("%.2f", amount)
    }
}