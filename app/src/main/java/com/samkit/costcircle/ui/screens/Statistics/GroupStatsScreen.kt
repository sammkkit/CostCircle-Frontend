package com.samkit.costcircle.ui.screens.Statistics


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
    // In GroupStatsScreen.kt

    val viewModel: GroupStatsViewModel = koinViewModel(
        key = "GroupStats-$groupId" // 👈 Add this back. Ensures unique VM per group ID.
    ) { parametersOf(groupId) }

// This ensures data loads fresh when entering, but survives configuration changes (rotation)
    LaunchedEffect(groupId) { // 👈 Use groupId as key instead of Unit
        viewModel.loadStats()
    }
    val stats by viewModel.stats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            stats?.let { data ->
                Column(
                    modifier = Modifier
                        .padding(bottom = padding.calculateBottomPadding())
                        .padding(top = 16.dp,start = 16.dp,end = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // 1. Spending by Category (Donut)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.5f))
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Spending by Category", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(16.dp))
                            DonutChart(
                                stats = data.byCategory,
                                totalSpending = data.totalSpending,
                                modifier = Modifier.fillMaxWidth().height(300.dp)
                            )
                        }
                    }

                    // 2. Spending by Member (Bar)
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

                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}