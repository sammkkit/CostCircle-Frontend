package com.samkit.costcircle.ui.screens.groups.viewModels

import android.util.Log
import androidx.compose.foundation.rememberPlatformOverscrollFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samkit.costcircle.data.auth.session.SessionManager
import com.samkit.costcircle.data.group.Repository.GroupRepository
import com.samkit.costcircle.ui.screens.groups.components.dummyGroups
import com.samkit.costcircle.ui.screens.groups.mappers.toUiModel
import com.samkit.costcircle.ui.screens.groups.models.GroupUiModel
import com.samkit.costcircle.ui.screens.groups.states.GroupDetailsEvent
import com.samkit.costcircle.ui.screens.groups.states.GroupDetailsUiState
import com.samkit.costcircle.ui.screens.groups.states.GroupsEffect
import com.samkit.costcircle.ui.screens.groups.states.GroupsEvent
import com.samkit.costcircle.ui.screens.groups.states.GroupsUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


class GroupDetailsViewModel(
    private val groupId: Long,
    private val repository: GroupRepository,
    sessionManager: SessionManager
) : ViewModel() {

    private val _state =
        MutableStateFlow<GroupDetailsUiState>(GroupDetailsUiState.Loading)
    val state = _state.asStateFlow()
    val currentUserId: Long = sessionManager.getUserId()
    init {
        load()
    }

    fun onEvent(event: GroupDetailsEvent) {
        when (event) {
            GroupDetailsEvent.Load,
            GroupDetailsEvent.Retry -> load()
        }
    }

    // In GroupDetailsViewModel.kt
    private fun load() {
        viewModelScope.launch {
            _state.value = GroupDetailsUiState.Loading

            Log.d("DEBUG_COST", "1. Loading for GroupID: $groupId")
            Log.d("DEBUG_COST", "2. Current UserID from Session: $currentUserId")

            try {
                val summaries = repository.getGroupSettlementSummary(groupId)
                Log.d("DEBUG_COST", "3. Received ${summaries.size} summaries from repository")

                // Log details of each summary to check IDs
                summaries.forEach { summary ->
                    Log.d("DEBUG_COST", "Summary for UserID: ${summary.userId}, netAmount: ${summary.netAmount}")
                }

                _state.value = if (summaries.isEmpty()) {
                    Log.d("DEBUG_COST", "4. Result: EMPTY")
                    GroupDetailsUiState.Empty
                } else {
                    Log.d("DEBUG_COST", "4. Result: SUCCESS")
                    GroupDetailsUiState.Success(summaries)
                }

            } catch (e: Exception) {
                Log.e("DEBUG_COST", "4. Result: ERROR - ${e.message}")
                _state.value = GroupDetailsUiState.Error("Failed to load group details")
            }
        }
    }
}
