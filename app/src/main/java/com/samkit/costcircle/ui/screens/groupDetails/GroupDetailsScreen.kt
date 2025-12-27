package com.samkit.costcircle.ui.screens.groupdetails

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.samkit.costcircle.data.group.dto.SettlementEntryDto
import com.samkit.costcircle.ui.screens.groupDetails.components.GroupMembersSheet
import com.samkit.costcircle.ui.screens.groupDetails.components.InviteMemberDialog
import com.samkit.costcircle.ui.screens.groupDetails.components.TransactionList
import com.samkit.costcircle.ui.screens.groupdetails.components.GroupDetailsEmpty
import com.samkit.costcircle.ui.screens.groupdetails.components.GroupDetailsError
import com.samkit.costcircle.ui.screens.groupdetails.components.GroupDetailsLoading
import com.samkit.costcircle.ui.screens.groupdetails.components.GroupHeaderCard
import com.samkit.costcircle.ui.screens.groupdetails.components.SettlementItem
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailsScreen(
    groupId: Long,
    groupName: String,
    onBack: () -> Unit,
    NavigateToAddExpense: () -> Unit = {}
) {
    val viewModel: GroupDetailsViewModel = koinViewModel(
        key = "GroupDetails-$groupId"
    ) { parametersOf(groupId) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val state by viewModel.state.collectAsState()
    val currentUserId = viewModel.currentUserId

    // UI State for Dialogs and Menus
    var showAddMemberDialog by remember { mutableStateOf(false) }
    var showMembersSheet by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current

    // Lifecycle and Effects
    LaunchedEffect(Unit) {
        viewModel.onEvent(GroupDetailsContract.Event.Load)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onEvent(GroupDetailsContract.Event.OnResume)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

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
                GroupDetailsContract.Effect.OpenMembersSheet -> showMembersSheet = true
                GroupDetailsContract.Effect.AddExpenseNavigate -> NavigateToAddExpense()
            }
        }
    }

    // --- SHEET & DIALOGS ---

    if (showMembersSheet && state is GroupDetailsContract.State.Success) {
        GroupMembersSheet(
            members = (state as GroupDetailsContract.State.Success).members,
            onDismiss = { showMembersSheet = false }
        )
    }

    if (showAddMemberDialog) {
        InviteMemberDialog(
            onDismiss = { showAddMemberDialog = false },
            onConfirm = { emails ->
                viewModel.onEvent(GroupDetailsContract.Event.AddMembers(emails))
                showAddMemberDialog = false
            }
        )
    }

    // NEW: Delete Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Delete Group?") },
            text = {
                Text("This will permanently delete the group '$groupName' and remove all expenses and history. This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onEvent(GroupDetailsContract.Event.DeleteGroupClicked)
                        showDeleteConfirmDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            val titleInteractionSource = remember { MutableInteractionSource() }
            val isTitlePressed by titleInteractionSource.collectIsPressedAsState()
            val titleScale by animateFloatAsState(
                targetValue = if (isTitlePressed) 0.97f else 1f,
                label = "titleScale"
            )

            CenterAlignedTopAppBar(
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .graphicsLayer(scaleX = titleScale, scaleY = titleScale)
                            .clickable(
                                interactionSource = titleInteractionSource,
                                indication = null
                            ) {
                                viewModel.onEvent(GroupDetailsContract.Event.MemberListClicked)
                            }
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = groupName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-0.5).sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (state is GroupDetailsContract.State.Success) {
                            val count = (state as GroupDetailsContract.State.Success).members.size
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                ),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Group,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$count ${if (count == 1) "Member" else "Members"}",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.onEvent(GroupDetailsContract.Event.BackClicked) },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 2.dp,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                },
                actions = {
                    // NEW: 3-Dot Menu Logic
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surface, // Clean look for menu trigger
                                tonalElevation = 0.dp,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.MoreVert,
                                        contentDescription = "Menu",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            // Option 1: Add Member
                            DropdownMenuItem(
                                text = { Text("Add Members") },
                                onClick = {
                                    showMenu = false
                                    showAddMemberDialog = true
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.PersonAdd, contentDescription = null)
                                }
                            )

                            HorizontalDivider()

                            // Option 2: Delete Group (Red Warning Color)
                            DropdownMenuItem(
                                text = { Text("Delete Group", color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    showMenu = false
                                    showDeleteConfirmDialog = true
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.onEvent(GroupDetailsContract.Event.AddExpenseClicked)
                },
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

                        if (currentState.selectedTab == 0) {
                            BalancesList(
                                groupName = groupName,
                                settlements = currentState.settlements,
                                currentUserId = currentUserId,
                                onSettleClick = { settlement ->
                                    viewModel.onEvent(GroupDetailsContract.Event.SettleUpClicked(settlement))
                                }
                            )

                        } else {
                            TransactionList(
                                transactions = currentState.transactions,
                                members = currentState.members,
                                currentUserId = currentUserId ?: 0L
                            )
                        }
                    }
                }
            }
        }
    }
}

// BalancesList Composable remains unchanged...
@Composable
fun BalancesList(
    groupName: String,
    settlements: List<SettlementEntryDto>,
    currentUserId: Long?,
    onSettleClick: (SettlementEntryDto) -> Unit
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
            SettlementItem(
                entry,
                onSettleUp = onSettleClick,
                currentUserId = currentUserId
            )
        }
    }
}