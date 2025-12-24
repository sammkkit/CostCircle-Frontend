package com.samkit.costcircle.ui.screens.newGroupAddition

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.samkit.costcircle.ui.screens.newGroupAddition.components.GroupIconPreview
import com.samkit.costcircle.ui.screens.newGroupAddition.viewModels.NewGroupViewModel
import com.yourapp.costcircle.ui.theme.AccentTeal
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun NewGroupAdditionScreen(
    onBack: () -> Unit,
    viewModel: NewGroupViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    // Local state for the text field input
    var memberEmail by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        viewModel.onEvent(NewGroupContract.Event.Reset)
    }
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is NewGroupContract.Effect.NavigateBack -> {
                    onBack()
                }
                is NewGroupContract.Effect.GroupCreated -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Group created successfully!",
                            duration = SnackbarDuration.Short
                        )
                        // Optional: small delay to let them see success before closing
                        kotlinx.coroutines.delay(500)
                        onBack()
                    }
                }
                is NewGroupContract.Effect.ShowError -> {
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

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Create New Group", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(NewGroupContract.Event.BackClicked) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = state.isCreateEnabled,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                ExtendedFloatingActionButton(
                    onClick = {
                        if (!state.isLoading && state.isCreateEnabled) {
                            viewModel.onEvent(NewGroupContract.Event.CreateClicked)
                        }
                    },
                    containerColor = if (state.isLoading) AccentTeal.copy(alpha = 0.5f) else AccentTeal,
                    contentColor = Color.White
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(12.dp))
                        Text("Creating...")
                    } else {
                        Icon(Icons.Default.Done, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Create Group")
                    }
                }
            }
        },
        containerColor = if (state.isLoading) AccentTeal.copy(alpha = 0.5f) else AccentTeal,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            GroupIconPreview(state.groupName)

            // --- SECTION 1: GROUP NAME ---
            Text(
                text = "Group Name",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = state.groupName,
                onValueChange = { viewModel.onEvent(NewGroupContract.Event.NameChanged(it)) },
                placeholder = { Text("e.g. Goa Trip 2024") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                enabled = !state.isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentTeal,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- SECTION 2: INVITE MEMBERS ---
            Text(
                text = "Invite Members",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = memberEmail,
                onValueChange = { memberEmail = it },
                placeholder = { Text("Enter friend's email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                enabled = !state.isLoading,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (memberEmail.isNotBlank()) {
                                viewModel.onEvent(NewGroupContract.Event.MemberAdded(memberEmail))
                                memberEmail = "" // Clear input
                            }
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Member", tint = AccentTeal)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentTeal,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            // --- SECTION 3: MEMBER CHIPS ---
            // Displayed dynamically as users are added
            androidx.compose.foundation.layout.FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.members.forEach { email ->
                    androidx.compose.material3.InputChip(
                        selected = false,
                        onClick = { viewModel.onEvent(NewGroupContract.Event.MemberRemoved(email)) },
                        label = { Text(email, style = MaterialTheme.typography.bodySmall) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove",
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(100.dp)) // Extra space for FAB
        }
    }
}