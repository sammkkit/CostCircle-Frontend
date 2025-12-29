package com.samkit.costcircle.ui.screens.statistics

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.samkit.costcircle.data.group.dto.CategoryStatDto
import com.samkit.costcircle.data.group.dto.ExpenseCategory

@Composable
fun DonutChart(
    stats: List<CategoryStatDto>,
    totalSpending: Double,
    modifier: Modifier = Modifier
) {
    val animatedProgress = remember { Animatable(0f) }

    // 1. Define Distinct Colors (Vibrant Palette for Dark/Light Mode)
    val distinctColors = remember {
        listOf(
            Color(0xFF42A5F5), // Blue
            Color(0xFFFFA726), // Orange
            Color(0xFF66BB6A), // Green
            Color(0xFFEF5350), // Red
            Color(0xFFAB47BC), // Purple
            Color(0xFF26C6DA), // Cyan
            Color(0xFFFFEE58), // Yellow
            Color(0xFFEC407A)  // Pink
        )
    }

    // 2. Sort stats once so Chart and Legend match exactly
    val sortedStats = remember(stats) {
        stats.sortedByDescending { it.total }
    }

    LaunchedEffect(stats) {
        animatedProgress.animateTo(1f, animationSpec = tween(1000))
    }

    Column(modifier = modifier) {
        // --- THE CHART ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(180.dp)) {
                val strokeWidth = 35.dp.toPx()
                var startAngle = -90f

                sortedStats.forEachIndexed { index, stat ->
                    val category = ExpenseCategory.fromCode(stat.category)
                    val sliceFraction = (stat.total / totalSpending.toFloat())
                    val sweepAngle = (sliceFraction * 360f) * animatedProgress.value

                    // Pick color from our distinct list (looping if we have more categories than colors)
                    val distinctColor = distinctColors[index % distinctColors.size]

                    drawArc(
                        color = distinctColor,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle.toFloat(),
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                    )
                    startAngle += sweepAngle.toFloat()
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "₹${totalSpending.toInt()}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- THE LEGEND ---
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Use the same 'sortedStats' so the order and colors match the chart
            sortedStats.forEachIndexed { index, stat ->
                val category = ExpenseCategory.fromCode(stat.category)
                val percentage = ((stat.total / totalSpending) * 100).toInt()
                val distinctColor = distinctColors[index % distinctColors.size]

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(distinctColor) // Use the same distinct color
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = category.displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "$percentage%",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "₹${stat.total.toInt()}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
            }
        }
    }
}