package com.samkit.costcircle.ui.screens.Statistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.samkit.costcircle.ui.screens.statistics.DonutChart
import com.samkit.costcircle.ui.screens.stats.components.MemberBarChart
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupStatsScreen(
    groupId: Long,
    onBack: () -> Unit,
) {
    val viewModel: GroupStatsViewModel = koinViewModel(
        key = "GroupStats-$groupId"
    ) { parametersOf(groupId) }

    LaunchedEffect(groupId) {
        viewModel.loadStats()
    }

    val stats by viewModel.stats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // 1. Loading State
    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return // Stop here
    }

    // 2. Data State
    val data = stats

    // Check if data is null OR if total spending is 0 (No expenses yet)
    if (data == null || data.totalSpending == 0.0) {
        StatsEmptyState()
    } else {
        // 3. Content State (Charts)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Spending by Category (Donut)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.5f))
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Spending by Category", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    DonutChart(
                        stats = data.byCategory,
                        totalSpending = data.totalSpending,
                        modifier = Modifier.fillMaxWidth() // Let height be determined by content or fixed inside chart
                    )
                }
            }

            // Spending by Member (Bar)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.5f))
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Top Spenders", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    MemberBarChart(
                        stats = data.byMember,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(80.dp)) // Extra space for FAB
        }
    }
}

// --- NEW EMPTY STATE UI ---
@Composable
fun StatsEmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.BarChart,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.surfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Statistics Yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Add expenses to see charts here.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}