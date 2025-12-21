package com.samkit.costcircle.ui.screens.groups

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.samkit.costcircle.ui.screens.groups.components.*
import com.samkit.costcircle.ui.screens.groups.states.GroupsContract
import org.koin.androidx.compose.koinViewModel

@Composable
fun GroupsScreen(
    modifier: Modifier = Modifier,
    onGroupClick: (Long, String) -> Unit,
    viewModel: GroupsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is GroupsContract.Effect.NavigateToGroup ->
                    onGroupClick(effect.groupId, effect.groupName)
            }
        }
    }
    SideEffect {
        if (state is GroupsContract.State.Empty) {
            viewModel.onEvent(GroupsContract.Event.Load)
        }
    }
    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        when (state) {
            GroupsContract.State.Loading -> GroupsLoading()

            GroupsContract.State.Empty -> GroupsEmpty {
                viewModel.onEvent(GroupsContract.Event.Retry)
            }

            is GroupsContract.State.Error -> GroupsError(
                message = (state as GroupsContract.State.Error).message,
                onRetry = { viewModel.onEvent(GroupsContract.Event.Retry) }
            )

            is GroupsContract.State.Success -> {
                val successState = state as GroupsContract.State.Success

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "Summary",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        TotalBalanceCard(
                            owed = successState.totalOwedToYou,
                            owe = successState.totalYouOwe
                        )
                    }

                    item {
                        Text(
                            text = "Groups",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    itemsIndexed(successState.groups, key = { _, g -> g.groupId }) { index, group ->
                        // Staggered slide-in animation
                        val visibleState = remember { MutableTransitionState(false) }.apply {
                            targetState = true
                        }

                        AnimatedVisibility(
                            visibleState = visibleState,
                            enter = slideInVertically(initialOffsetY = { it * (index + 1) }) + fadeIn()
                        ) {
                            GroupSummaryCard(
                                group = group,
                                onClick = { onGroupClick(group.groupId, group.groupName) }
                            )
                        }
                    }
                }
            }
        }
    }
}
