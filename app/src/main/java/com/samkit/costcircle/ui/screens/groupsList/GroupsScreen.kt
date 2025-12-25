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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samkit.costcircle.ui.screens.groupsList.components.*
import com.samkit.costcircle.ui.screens.groupsList.states.GroupsContract
import org.koin.androidx.compose.koinViewModel
import kotlin.math.abs
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    onGroupClick: (Long, String) -> Unit,
    onAddGroupClick: () -> Unit = {},
    onAddExpenseClick:()->Unit ={},
    viewModel: GroupsViewModel = koinViewModel(),
    paddingMain: PaddingValues
) {

    val state by viewModel.state.collectAsState()
    val scrollState = rememberLazyListState()
    val haptic = LocalHapticFeedback.current

    // --- PULL TO REFRESH STATE ---
    val pullRefreshState = rememberPullToRefreshState()
    // We consider it "refreshing" if the state is currently Loading.
    // This connects the UI spinner directly to your ViewModel's status.
    val isRefreshing = false
    // Logic to ensure vibration only triggers once per crossing
    var hasVibrated by remember { mutableStateOf(false) }

    // Enhanced scroll calculations with smooth easing
    val scrollOffset by remember {
        derivedStateOf {
            if (scrollState.firstVisibleItemIndex > 0) 1f
            else {
                val rawOffset = scrollState.firstVisibleItemScrollOffset.toFloat() / 400f
                (rawOffset * rawOffset).coerceIn(0f, 1f)
            }
        }
    }

    LaunchedEffect(scrollOffset) {
        val threshold = 0.95f
        if (scrollOffset >= threshold && !hasVibrated) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            hasVibrated = true
        } else if (scrollOffset < threshold) {
            hasVibrated = false
        }
    }

    // Parallax effect for summary card
    val summaryCardOffset by remember {
        derivedStateOf { min(scrollState.firstVisibleItemScrollOffset.toFloat() * 0.5f, 200f) }
    }

    val summaryAlpha by remember {
        derivedStateOf { (1f - scrollOffset * 1.2f).coerceIn(0f, 1f) }
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



    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                GroupsTopBar(
                    state = state,
                    scrollOffset = scrollOffset,
                    onEvent = viewModel::onEvent
                )
            }
        },
        floatingActionButton = {
            // Only show FAB if we have groups (Success State)
            if (state is GroupsContract.State.Success) {
                ExtendedFloatingActionButton(
                    onClick = onAddExpenseClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    icon = { Icon(Icons.Default.Edit, contentDescription = null) }, // Using Edit icon to distinguish from "Create Group"
                    text = { Text("Add expense") },
                    expanded = scrollState.firstVisibleItemIndex == 0,
                    modifier = Modifier.padding(bottom = paddingMain.calculateBottomPadding())
                )
            }
        }
    ) { padding ->
        // --- WRAP CONTENT IN PULL REFRESH BOX ---
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.onEvent(GroupsContract.Event.Load) },
            state = pullRefreshState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            indicator = {
                PullToRefreshDefaults.Indicator(
                    state = pullRefreshState,
                    isRefreshing = isRefreshing,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                when (state) {
                    GroupsContract.State.Loading -> {
                        // Keep the list visible momentarily or show skeleton?
                        // Since your VM clears state on load, we show the nice skeleton.
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

                    GroupsContract.State.Empty -> {
                        GroupsEmpty(
                            onCreateGroupClick = {
                                viewModel.onEvent(GroupsContract.Event.CreateGroupClicked)
                            }
                        )
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
                                top = 16.dp, // Adjusted since scaffold padding is handled by parent Box
                                start = 16.dp,
                                end = 16.dp,
                                bottom = paddingMain.calculateBottomPadding()
                            ),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // 1. SUMMARY CARD
                            item {
                                if (!successState.isSearchActive) {
                                    AnimatedVisibility(
                                        visible = scrollOffset < 0.95f,
                                        enter = expandVertically(animationSpec = spring(stiffness = Spring.StiffnessLow)) + fadeIn(tween(300)),
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
                            // 2. GROUPS HEADER
                            item {
                                var visible by remember { mutableStateOf(false) }
                                LaunchedEffect(Unit) { visible = true }
                                AnimatedVisibility(
                                    visible = visible,
                                    enter = slideInVertically { -40 } + fadeIn(tween(300))
                                ) {
                                    Text(
                                        text = "Groups",
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }

                            // 3. GROUP LIST
                            itemsIndexed(successState.filteredGroups, key = { _, g -> g.groupId }) { index, group ->
                                val delayMillis = (index * 50).coerceAtMost(300)
                                val visibleState = remember { MutableTransitionState(false).apply { targetState = true } }

                                AnimatedVisibility(
                                    visibleState = visibleState,
                                    enter = slideInVertically { it / 2 } + fadeIn(tween(400, delayMillis)) + scaleIn(initialScale = 0.92f),
                                    exit = fadeOut()
                                ) {
                                    // Parallax Logic
                                    val itemInfo = scrollState.layoutInfo.visibleItemsInfo.find { it.key == group.groupId }
                                    val parallaxOffset = itemInfo?.let {
                                        val distanceFromCenter = abs((it.offset + it.size / 2f) - (scrollState.layoutInfo.viewportEndOffset / 2f))
                                        (distanceFromCenter / (scrollState.layoutInfo.viewportEndOffset / 2f) * 15f).coerceIn(0f, 15f)
                                    } ?: 0f

                                    Box(modifier = Modifier.graphicsLayer { translationY = parallaxOffset }) {
                                        GroupSummaryCard(
                                            group = group,
                                            onClick = { onGroupClick(group.groupId, group.groupName) }
                                        )
                                    }
                                }
                            }

                            // 4. ADD BUTTON
                            item {
                                OutlinedButton(
                                    onClick = { viewModel.onEvent(GroupsContract.Event.CreateGroupClicked) },
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Start a new group")
                                }
                                Spacer(modifier = Modifier.height(32.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}