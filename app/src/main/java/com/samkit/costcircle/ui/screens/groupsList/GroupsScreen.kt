package com.samkit.costcircle.ui.screens.groupsList

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.samkit.costcircle.ui.screens.groupsList.components.*
import com.samkit.costcircle.ui.screens.groupsList.states.GroupsContract
import org.koin.androidx.compose.koinViewModel
import kotlin.math.abs
import kotlin.math.min

@Composable
fun GroupsScreen(
    onGroupClick: (Long, String) -> Unit,
    onAddGroupClick: () -> Unit = {},
    viewModel: GroupsViewModel = koinViewModel(),
    paddingMain: PaddingValues
) {

    val state by viewModel.state.collectAsState()
    val scrollState = rememberLazyListState()
    val haptic = LocalHapticFeedback.current

    // Logic to ensure vibration only triggers once per crossing
    var hasVibrated by remember { mutableStateOf(false) }
    // Enhanced scroll calculations with smooth easing
    val scrollOffset by remember {
        derivedStateOf {
            if (scrollState.firstVisibleItemIndex > 0) 1f
            else {
                val rawOffset = scrollState.firstVisibleItemScrollOffset.toFloat() / 400f
                // Smooth easing curve
                (rawOffset * rawOffset).coerceIn(0f, 1f)
            }
        }
    }
    LaunchedEffect(scrollOffset) {
        val threshold = 0.95f // The point where your TopBar usually becomes fully solid

        if (scrollOffset >= threshold && !hasVibrated) {
            // Trigger a light haptic "tick"
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            hasVibrated = true
        } else if (scrollOffset < threshold) {
            // Reset so it can vibrate again when scrolling back up and then down
            hasVibrated = false
        }
    }
    // Parallax effect for summary card
    val summaryCardOffset by remember {
        derivedStateOf {
            min(scrollState.firstVisibleItemScrollOffset.toFloat() * 0.5f, 200f)
        }
    }

    // Dynamic blur/fade effect
    val summaryAlpha by remember {
        derivedStateOf {
            (1f - scrollOffset * 1.2f).coerceIn(0f, 1f)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is GroupsContract.Effect.NavigateToGroup ->
                    onGroupClick(effect.groupId, effect.groupName)
                is GroupsContract.Effect.NavigateToCreateGroup ->
                    onAddGroupClick()
            }
        }
    }

    SideEffect {
        if (state is GroupsContract.State.Empty) {
            viewModel.onEvent(GroupsContract.Event.Load)
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                GroupsTopBar(
                    state = state,
                    scrollOffset = scrollOffset,
                    onEvent = viewModel::onEvent
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (state) {
                GroupsContract.State.Loading -> {
                    // Animated loading state
                    val infiniteTransition = rememberInfiniteTransition(label = "loading")
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(800, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "loadingAlpha"
                    )
                    Box(modifier = Modifier.alpha(alpha)) {
                        GroupsLoading()
                    }
                }

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
                        state = scrollState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            top = padding.calculateTopPadding(),
                            start = 16.dp,
                            end = 16.dp,
                            bottom = paddingMain.calculateBottomPadding()
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Enhanced summary card with parallax and fade (inside LazyColumn)
                        item {
                            if (!successState.isSearchActive) {
                                AnimatedVisibility(
                                    visible = scrollOffset < 0.95f,
                                    enter = expandVertically(
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessLow
                                        )
                                    ) + fadeIn(tween(300)),
                                    exit = shrinkVertically(tween(200)) + fadeOut(tween(150))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .padding(vertical = 8.dp)
                                            .graphicsLayer {
                                                translationY = -summaryCardOffset
                                                alpha = summaryAlpha
                                                scaleX = 1f - (scrollOffset * 0.05f)
                                                scaleY = 1f - (scrollOffset * 0.05f)
                                                rotationX = scrollOffset * -3f
                                            }
                                    ) {
                                        TotalBalanceCard(
                                            owed = successState.totalOwedToYou,
                                            owe = successState.totalYouOwe,
                                        )
                                    }
                                }
                            }
                        }

                        // Groups header with slide-in animation
                        item {
                            var visible by remember { mutableStateOf(false) }
                            LaunchedEffect(Unit) { visible = true }

                            AnimatedVisibility(
                                visible = visible,
                                enter = slideInVertically(
                                    initialOffsetY = { -40 },
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    )
                                ) + fadeIn(tween(300))
                            ) {
                                Text(
                                    text = "Groups",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }

                        // Enhanced staggered group cards
                        itemsIndexed(
                            successState.filteredGroups,
                            key = { _, g -> g.groupId }
                        ) { index, group ->
                            val visibleState = remember {
                                MutableTransitionState(false).apply { targetState = true }
                            }

                            // Calculate dynamic stagger delay
                            val delayMillis = (index * 50).coerceAtMost(300)

                            AnimatedVisibility(
                                visibleState = visibleState,
                                enter = slideInVertically(
                                    initialOffsetY = { it / 2 },
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMediumLow
                                    )
                                ) + fadeIn(
                                    animationSpec = tween(
                                        durationMillis = 400,
                                        delayMillis = delayMillis,
                                        easing = FastOutSlowInEasing
                                    )
                                ) + scaleIn(
                                    initialScale = 0.92f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    )
                                ),
                                exit = slideOutVertically(
                                    targetOffsetY = { -it / 3 }
                                ) + fadeOut(tween(200)) + scaleOut(
                                    targetScale = 0.95f,
                                    animationSpec = tween(200)
                                )
                            ) {
                                // Add parallax effect based on scroll position
                                val itemInfo = scrollState.layoutInfo.visibleItemsInfo
                                    .find { it.key == group.groupId }

                                val parallaxOffset = itemInfo?.let {
                                    val itemCenter = it.offset + it.size / 2f
                                    val viewportCenter = scrollState.layoutInfo.viewportEndOffset / 2f
                                    val distanceFromCenter = abs(itemCenter - viewportCenter)
                                    val maxDistance = scrollState.layoutInfo.viewportEndOffset / 2f
                                    (distanceFromCenter / maxDistance * 15f).coerceIn(0f, 15f)
                                } ?: 0f

                                Box(
                                    modifier = Modifier.graphicsLayer {
                                        translationY = parallaxOffset
                                    }
                                ) {
                                    GroupSummaryCard(
                                        group = group,
                                        onClick = { onGroupClick(group.groupId, group.groupName) }
                                    )
                                }
                            }
                        }

                        // Enhanced add group button
                        item {
                            var buttonVisible by remember { mutableStateOf(false) }
                            LaunchedEffect(Unit) {
                                kotlinx.coroutines.delay(
                                    (successState.filteredGroups.size * 50 + 200).toLong()
                                )
                                buttonVisible = true
                            }

                            AnimatedVisibility(
                                visible = buttonVisible,
                                enter = slideInVertically(
                                    initialOffsetY = { it / 3 },
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioLowBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    )
                                ) + fadeIn(tween(300)) + scaleIn(
                                    initialScale = 0.9f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy
                                    )
                                )
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.onEvent(GroupsContract.Event.CreateGroupClicked)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Add group button"
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text("Start a new group")
                                }
                            }
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}