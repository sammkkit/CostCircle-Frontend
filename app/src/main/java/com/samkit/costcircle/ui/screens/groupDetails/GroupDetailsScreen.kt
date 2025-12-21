package com.samkit.costcircle.ui.screens.groupdetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.samkit.costcircle.data.auth.session.SessionManager
import com.samkit.costcircle.ui.screens.groupdetails.components.GroupDetailsEmpty
import com.samkit.costcircle.ui.screens.groupdetails.components.GroupDetailsError
import com.samkit.costcircle.ui.screens.groupdetails.components.GroupDetailsLoading
import com.samkit.costcircle.ui.screens.groupdetails.components.GroupHeaderCard
import com.samkit.costcircle.ui.screens.groupdetails.components.SettlementItem
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailsScreen(
    groupId: Long,
    groupName: String,
    onBack: () -> Unit,

) {

    val viewModel: GroupDetailsViewModel =
        koinViewModel(
            key = "GroupDetails-$groupId"
        ) { parametersOf(groupId) }

    val currentUserId = viewModel.currentUserId
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                GroupDetailsContract.Effect.NavigateBack -> onBack()
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar( // More modern alignment
                title = { Text(groupName, style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(GroupDetailsContract.Event.BackClicked) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            when (state) {
                is GroupDetailsContract.State.Loading ->
                    GroupDetailsLoading()

                is GroupDetailsContract.State.Empty ->
                    GroupDetailsEmpty()

                is GroupDetailsContract.State.Error ->
                    GroupDetailsError(
                        message = (state as GroupDetailsContract.State.Error).message,
                        onRetry = {
                            viewModel.onEvent(GroupDetailsContract.Event.Retry)
                        }
                    )

                is GroupDetailsContract.State.Success -> {
                    val settlements = (state as GroupDetailsContract.State.Success).settlements

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            GroupHeaderCard(
                                groupName = groupName,
                                settlements = settlements,
                                currentUserId = currentUserId // From your Auth state/ViewModel
                            )
                        }

                        item {
                            Text(
                                text = "Required Actions",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        itemsIndexed(settlements) { index, entry ->
                            // STAGGERED ENTRANCE ANIMATION
                            val animVisible = remember { MutableTransitionState(false) }.apply {
                                targetState = true
                            }

                            AnimatedVisibility(
                                visibleState = animVisible,
                                enter = slideInVertically(
                                    initialOffsetY = { it * (index + 1) },
                                    animationSpec = tween(500, easing = FastOutSlowInEasing)
                                ) + fadeIn()
                            ) {
                                SettlementItem(entry)
                            }
                        }
                    }
                }
            }
        }
    }
}



