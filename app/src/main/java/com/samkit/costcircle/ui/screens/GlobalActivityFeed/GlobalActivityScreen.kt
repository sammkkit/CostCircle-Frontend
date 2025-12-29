package com.samkit.costcircle.ui.screens.activity // Update package if needed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.samkit.costcircle.ui.screens.GlobalActivityFeed.GlobalActivityViewModel
import com.samkit.costcircle.ui.screens.groupDetails.components.TransactionList
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalActivityScreen(
    viewModel: GlobalActivityViewModel = koinViewModel(),
    paddingMain: PaddingValues
) {
    val transactions by viewModel.transactions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // 1. Auto-Refresh on Entry
    LaunchedEffect(Unit) {
        viewModel.loadActivity()
    }

    // 2. Material 3 Refresh State
    val refreshState = rememberPullToRefreshState()

    Scaffold(
        modifier = Modifier.padding(bottom = paddingMain.calculateBottomPadding()),
        topBar = {
            TopAppBar(
                title = { Text("Recent Activity") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->

        // 3. The Material 3 PullToRefreshBox handles everything (Indicator + Swipe Logic)
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = { viewModel.loadActivity() },
            state = refreshState,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (transactions.isEmpty() && !isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No activity yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                TransactionList(
                    transactions = transactions,
                    members = emptyList(),
                    currentUserId = viewModel.currentUserId
                )
            }
        }
    }
}