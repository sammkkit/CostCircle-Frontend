package com.samkit.costcircle.ui.screens.groupsList.components

import com.yourapp.costcircle.ui.theme.GreenOwed
import com.yourapp.costcircle.ui.theme.OrangeOwe
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TotalBalanceCard(owed: Double, owe: Double) {
    var hasAnimated by rememberSaveable { mutableStateOf(false) }
    var targetOwed by remember { mutableStateOf(if (hasAnimated) owed.toFloat() else 0f) }
    var targetOwe by remember { mutableStateOf(if (hasAnimated) owe.toFloat() else 0f) }

    LaunchedEffect(owed, owe) {
        targetOwed = owed.toFloat()
        targetOwe = owe.toFloat()
        hasAnimated = true
    }

    val netBalance = targetOwed - targetOwe
    val netColor = if (netBalance >= 0) GreenOwed else OrangeOwe

    val animatedNet by animateFloatAsState(
        targetValue = netBalance,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
        label = "Net"
    )

    // Using surfaceContainerHigh and higher shadow for light mode visibility
    Surface(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 0.dp)
            ,
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shadowElevation = 8.dp,
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            netColor.copy(alpha = 0.12f),
                            Color.Transparent
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Text(
                text = "Total Net Balance",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = "₹${String.format("%.2f", animatedNet)}",
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black),
                color = netColor
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                BalanceDetailItem("You are owed", owed, GreenOwed)
                BalanceDetailItem("You owe", owe, OrangeOwe)
            }
        }
    }
}

@Composable
private fun BalanceDetailItem(label: String, amount: Double, color: Color) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            "₹${String.format("%.2f", amount)}",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = color
        )
    }
}