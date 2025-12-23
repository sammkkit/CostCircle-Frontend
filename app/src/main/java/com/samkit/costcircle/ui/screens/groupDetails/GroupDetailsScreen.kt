package com.samkit.costcircle.ui.screens.groupdetails

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.samkit.costcircle.data.auth.session.SessionManager
import com.samkit.costcircle.data.group.dto.SettlementEntryDto
import com.samkit.costcircle.ui.screens.groupDetails.components.InviteMemberDialog
import com.samkit.costcircle.ui.screens.groupDetails.components.TransactionList
import com.samkit.costcircle.ui.screens.groupdetails.components.GroupDetailsEmpty
import com.samkit.costcircle.ui.screens.groupdetails.components.GroupDetailsError
import com.samkit.costcircle.ui.screens.groupdetails.components.GroupDetailsLoading
import com.samkit.costcircle.ui.screens.groupdetails.components.GroupHeaderCard
import com.samkit.costcircle.ui.screens.groupdetails.components.SettlementItem
import kotlinx.coroutines.launch
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
    val viewModel: GroupDetailsViewModel = koinViewModel(
        key = "GroupDetails-$groupId"
    ) { parametersOf(groupId) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val state by viewModel.state.collectAsState()
    val currentUserId = viewModel.currentUserId
    val context = LocalContext.current
    var showAddMemberDialog by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                GroupDetailsContract.Effect.NavigateBack -> onBack()
                is GroupDetailsContract.Effect.ShowToast -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = effect.message,
                            duration = SnackbarDuration.Long,
                            withDismissAction = true
                        )
                    }
                }
            }
        }
    }

    // --- ADD MEMBER DIALOG ---
    if (showAddMemberDialog) {
        InviteMemberDialog(
            onDismiss = { showAddMemberDialog = false },
            onConfirm = { emails ->
                viewModel.onEvent(GroupDetailsContract.Event.AddMembers(emails))
                showAddMemberDialog = false
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(groupName, style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(GroupDetailsContract.Event.BackClicked) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        showAddMemberDialog = true
                    }) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Add Member")
                    }
                }
            )
        },
        floatingActionButton = {
            // Modern FAB for adding expense
            FloatingActionButton(
                onClick = { /* Navigate to Add Expense */ },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val currentState = state) {
                is GroupDetailsContract.State.Loading -> GroupDetailsLoading()
                is GroupDetailsContract.State.Error -> GroupDetailsError(
                    message = currentState.message,
                    onRetry = { viewModel.onEvent(GroupDetailsContract.Event.Retry) }
                )
                is GroupDetailsContract.State.Empty -> GroupDetailsEmpty(
                    onInviteFriends = { showAddMemberDialog = true }
                )
                is GroupDetailsContract.State.Success -> {
                    Column {
                        // --- TABS SECTION ---
                        TabRow(
                            selectedTabIndex = currentState.selectedTab,
                            containerColor = MaterialTheme.colorScheme.background,
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[currentState.selectedTab]),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        ) {
                            Tab(
                                selected = currentState.selectedTab == 0,
                                onClick = { viewModel.onEvent(GroupDetailsContract.Event.TabSelected(0)) },
                                text = { Text("Balances") }
                            )
                            Tab(
                                selected = currentState.selectedTab == 1,
                                onClick = { viewModel.onEvent(GroupDetailsContract.Event.TabSelected(1)) },
                                text = { Text("Transactions") }
                            )
                        }

                        // --- CONTENT SECTION ---
                        if (currentState.selectedTab == 0) {
                            BalancesList(
                                groupName = groupName,
                                settlements = currentState.settlements,
                                currentUserId = currentUserId
                            )

                        } else {
                            TransactionList(currentState.transactions)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun BalancesList(
    groupName: String,
    settlements: List<SettlementEntryDto>,
    currentUserId: Long?
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GroupHeaderCard(
                groupName = groupName,
                settlements = settlements,
                currentUserId = currentUserId
            )
        }

        items(settlements) { entry ->
            SettlementItem(entry)
        }
    }
}
