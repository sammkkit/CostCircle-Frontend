package com.samkit.costcircle.ui.screens.groups

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.samkit.costcircle.core.model.Settlement
import com.samkit.costcircle.core.model.SettlementSummary
import com.samkit.costcircle.ui.screens.groups.components.GroupDetailsEmptyState
import com.samkit.costcircle.ui.screens.groups.components.GroupDetailsErrorState
import com.samkit.costcircle.ui.screens.groups.components.GroupDetailsLoadingState
import com.samkit.costcircle.ui.screens.groups.components.GroupHeaderCard
import com.samkit.costcircle.ui.screens.groups.states.GroupDetailsEvent
import com.samkit.costcircle.ui.screens.groups.states.GroupDetailsUiState
import com.samkit.costcircle.ui.screens.groups.viewModels.GroupDetailsViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailsScreen(
    groupId: Long,
    groupName: String,
    onBack: () -> Unit,
    onAddExpense: () -> Unit = {}
) {
    val viewModel: GroupDetailsViewModel =
        koinViewModel(key = groupId.toString(),
            parameters = { parametersOf(groupId) })

    val uiState by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(groupName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddExpense) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add expense"
                )
            }
        }
    ) { padding ->

        when (uiState) {

            GroupDetailsUiState.Loading -> {
                GroupDetailsLoadingState()
            }

            GroupDetailsUiState.Empty -> {
                GroupDetailsEmptyState(
                    onAddExpenseClick = onAddExpense
                )
            }

            is GroupDetailsUiState.Error -> {
                GroupDetailsErrorState(
                    message = (uiState as GroupDetailsUiState.Error).message,
                    onRetry = {
                        viewModel.onEvent(GroupDetailsEvent.Retry)
                    }
                )
            }

            is GroupDetailsUiState.Success -> {
                val summaries =
                    (uiState as GroupDetailsUiState.Success).summaries

                GroupDetailsSuccessContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    summaries = summaries,
                    currentUserId = viewModel.currentUserId
                )
            }
        }
    }
}
@Composable
fun GroupDetailsSuccessContent(
    modifier: Modifier = Modifier,
    summaries: List<SettlementSummary>,
    currentUserId: Long
) {
    // Top card stays specific to YOU (User 10)
    val mySummary = summaries.find { it.userId == currentUserId }
    val netBalance = mySummary?.netAmount ?: 0.0
    val youOwe = if (netBalance < 0) -netBalance else 0.0
    val youAreOwed = if (netBalance > 0) netBalance else 0.0

    // Get EVERY unique settlement in the group
    val allSettlements = summaries
        .flatMap { it.settlements }
        .distinctBy { "${it.fromUserId}-${it.toUserId}-${it.amount}" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            GroupHeaderCard(youOwe = youOwe, youAreOwed = youAreOwed)
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(
            items = allSettlements,
            key = { "${it.fromUserId}-${it.toUserId}-${it.amount}" }
        ) { settlement ->
            SettlementItem(
                settlement = settlement,
                currentUserId = currentUserId
            )
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}


@Composable
fun SettlementItem(
    settlement: Settlement,
    currentUserId: Long
) {
    val isYouPaying = settlement.fromUserId == currentUserId
    val isYouReceiving = settlement.toUserId == currentUserId

    // Determine the text to display
    val title = when {
        isYouPaying -> "You pay ${settlement.toName}"
        isYouReceiving -> "${settlement.fromName} pays you"
        else -> "${settlement.fromName} pays ${settlement.toName}"
    }

    // Determine color: Red if you owe, Primary if you're owed, Neutral if not involved
    val amountColor = when {
        isYouPaying -> MaterialTheme.colorScheme.error
        isYouReceiving -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = "₹${settlement.amount}",
            style = MaterialTheme.typography.titleMedium,
            color = amountColor
        )
    }
}

