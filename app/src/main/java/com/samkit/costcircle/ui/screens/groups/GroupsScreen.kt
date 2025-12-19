package com.samkit.costcircle.ui.screens.groups

import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.samkit.costcircle.ui.screens.groups.components.GroupCard
import com.samkit.costcircle.ui.screens.groups.components.GroupsEmptyState
import com.samkit.costcircle.ui.screens.groups.components.GroupsErrorState
import com.samkit.costcircle.ui.screens.groups.components.GroupsLoadingState
import com.samkit.costcircle.ui.screens.groups.components.OverallSummaryCard
import com.samkit.costcircle.ui.screens.groups.models.GroupUiModel
import com.samkit.costcircle.ui.screens.groups.states.GroupsEffect
import com.samkit.costcircle.ui.screens.groups.states.GroupsEvent
import com.samkit.costcircle.ui.screens.groups.states.GroupsUiState
import com.samkit.costcircle.ui.screens.groups.viewModels.GroupsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun GroupsScreen(
    modifier: Modifier = Modifier,
    viewModel: GroupsViewModel = koinViewModel(),
    onGroupClick: (Long, String) -> Unit
) {
    val uiState by viewModel.state.collectAsState()
    val context = LocalContext.current
    // Handle one-time effects (navigation)
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is GroupsEffect.NavigateToGroup ->
                    onGroupClick(effect.groupId, effect.name)

                is GroupsEffect.CreateToastWhenGroupClicked -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    when (uiState) {

        GroupsUiState.Loading -> {
            GroupsLoadingState()
        }

        GroupsUiState.Empty -> {
            GroupsEmptyState {
                viewModel.onEvent(GroupsEvent.Retry)
            }
        }

        is GroupsUiState.Error -> {
            GroupsErrorState(
                message = "Something went wrong",
                onRetry = { viewModel.onEvent(GroupsEvent.Retry) }
            )
        }

        is GroupsUiState.Success -> {
            val groups = (uiState as GroupsUiState.Success).groups

            val youOwe = groups
                .filter { it.balance < 0 }
                .sumOf { -it.balance }

            val youAreOwed = groups
                .filter { it.balance > 0 }
                .sumOf { it.balance }

            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                item {
                    OverallSummaryCard(
                        youOwe = youOwe,
                        youAreOwed = youAreOwed
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                items(
                    items = groups,
                    key = { it.id }
                ) { group ->
                    GroupCard(
                        group = group,
                        onClick = {
                            viewModel.onEvent(
                                GroupsEvent.GroupClicked(
                                    groupId = group.id,
                                    name = group.name
                                )
                            )
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}




