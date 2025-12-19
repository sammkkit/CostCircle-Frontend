package com.samkit.costcircle.ui.screens.groups

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.samkit.costcircle.ui.screens.groups.components.EmptyExpensesState
import com.samkit.costcircle.ui.screens.groups.components.ExpenseItem
import com.samkit.costcircle.ui.screens.groups.components.GroupDetailsEmptyState
import com.samkit.costcircle.ui.screens.groups.components.GroupDetailsErrorState
import com.samkit.costcircle.ui.screens.groups.components.GroupDetailsLoadingState
import com.samkit.costcircle.ui.screens.groups.components.GroupHeaderCard
import com.samkit.costcircle.ui.screens.groups.models.ExpenseUiModel
import com.samkit.costcircle.ui.screens.groups.states.GroupDetailsUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailsScreen(
    groupName: String,
    onBack: () -> Unit,
    onAddExpenseClick: () -> Unit
) {
    // 🔴 TEMP fake state (replace later with ViewModel)
    var uiState by remember {
        mutableStateOf<GroupDetailsUiState>(
            GroupDetailsUiState.Success(
                listOf(
                    ExpenseUiModel(
                        id = 1,
                        title = "Dinner",
                        subtitle = "You paid",
                        amount = 250.0,
                        isOwed = false
                    ),
                    ExpenseUiModel(
                        id = 2,
                        title = "Taxi",
                        subtitle = "Rahul paid",
                        amount = 600.0,
                        isOwed = true
                    )
                )
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(groupName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddExpenseClick) {
                Icon(Icons.Default.Add, contentDescription = "Add expense")
            }
        }
    ) { padding ->

        when (uiState) {

            GroupDetailsUiState.Loading -> {
                GroupDetailsLoadingState()
            }

            GroupDetailsUiState.Empty -> {
                GroupDetailsEmptyState(
                    onAddExpenseClick = onAddExpenseClick
                )
            }

            is GroupDetailsUiState.Error -> {
                GroupDetailsErrorState(
                    message = (uiState as GroupDetailsUiState.Error).message,
                    onRetry = {
                        uiState = GroupDetailsUiState.Loading
                    }
                )
            }

            is GroupDetailsUiState.Success -> {
                val expenses =
                    (uiState as GroupDetailsUiState.Success).expenses

                val youOwe =
                    expenses.filter { !it.isOwed }.sumOf { it.amount }

                val youAreOwed =
                    expenses.filter { it.isOwed }.sumOf { it.amount }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                ) {

                    item {
                        GroupHeaderCard(
                            youOwe = youOwe,
                            youAreOwed = youAreOwed
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    items(expenses, key = { it.id }) { expense ->
                        ExpenseItem(expense)
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

