package com.samkit.costcircle.ui.screens.groupdetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samkit.costcircle.data.auth.session.SessionManager
import com.samkit.costcircle.data.group.dto.SettleUpRequest
import com.samkit.costcircle.data.group.dto.SettlementEntryDto
import com.samkit.costcircle.data.group.repository.GroupRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GroupDetailsViewModel(
    private val groupId: Long,
    private val repository: GroupRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    val currentUserId = sessionManager.getUserId()
    private val _state = MutableStateFlow<GroupDetailsContract.State>(GroupDetailsContract.State.Loading)
    val state: StateFlow<GroupDetailsContract.State> = _state.asStateFlow()

    private val _effect = Channel<GroupDetailsContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            repository.groupsRefreshTrigger.collect {
                // Silently refresh details without showing full-screen loading
                loadDetails(showLoading = false)
            }
        }
        onEvent(GroupDetailsContract.Event.Load)
    }

    fun onEvent(event: GroupDetailsContract.Event) {
        when (event) {
            GroupDetailsContract.Event.Load,
            GroupDetailsContract.Event.Retry -> loadDetails()

            is GroupDetailsContract.Event.TabSelected -> {
                // Update the state with the new tab index if in Success state
                val currentState = _state.value
                if (currentState is GroupDetailsContract.State.Success) {
                    _state.value = currentState.copy(selectedTab = event.index)
                }
            }

            is GroupDetailsContract.Event.AddMembers -> addMembersBulk(event.emails)

            GroupDetailsContract.Event.BackClicked -> sendEffect(GroupDetailsContract.Effect.NavigateBack)
            GroupDetailsContract.Event.MemberListClicked -> {
                sendEffect(GroupDetailsContract.Effect.OpenMembersSheet)
            }
            is GroupDetailsContract.Event.SettleUpClicked -> performSettleUp(event.settlement)
        }
    }
    private fun performSettleUp(settlement: SettlementEntryDto) {
        viewModelScope.launch {
            runCatching {
                // 1. Record the payment in the new table
                repository.settleUp(
                    groupId = groupId,
                    request = SettleUpRequest(
                        receiverId = settlement.receiverUserId,
                        amount = settlement.amount
                    )
                )
            }.onSuccess {
                sendEffect(GroupDetailsContract.Effect.ShowToast("Payment recorded! Balance updated."))
                // 2. Trigger a data refresh
                loadDetails(showLoading = false)
            }.onFailure { e ->
                sendEffect(GroupDetailsContract.Effect.ShowToast("Failed to settle: ${e.message}"))
            }
        }
    }
    private fun loadDetails(showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) _state.value = GroupDetailsContract.State.Loading

            runCatching {
                // Parallel fetching to optimize performance
                val settlementsDeferred = async { repository.getGroupFinancialSummary(groupId) }
                val transactionsDeferred = async { repository.getGroupTransactions(groupId) }
                val membersDef = async { repository.getGroupMembers(groupId) }

                val settlementsResponse = settlementsDeferred.await()
                val transactionsList = transactionsDeferred.await()
                val members = membersDef.await()
                Log.d("GroupRepository", "Fetching group transactions for groupId: $transactionsList")

                // If both are empty, show Empty state; otherwise Success
                if (settlementsResponse.settlements.isEmpty() && transactionsList.isEmpty()) {
                    GroupDetailsContract.State.Empty
                } else {
                    GroupDetailsContract.State.Success(
                        settlements = settlementsResponse.settlements,
                        transactions = transactionsList,
                        members = members,
                        // Retain current tab if we are just refreshing
                        selectedTab = (_state.value as? GroupDetailsContract.State.Success)?.selectedTab ?: 0
                    )
                }
            }.onSuccess { newState ->
                _state.value = newState
            }.onFailure { e ->
                _state.value = GroupDetailsContract.State.Error(
                    e.message ?: "Failed to load group details"
                )
            }
        }
    }

    private fun addMembersBulk(emails: List<String>) {
        viewModelScope.launch {
            runCatching {
                repository.addMembersBulk(groupId, emails)
            }.onSuccess { response ->
                // Case A: Success (Some or all added)
                val message = if (response.missingEmails.isEmpty()) {
                    "All members added successfully!"
                } else {
                    "${response.addedCount} added. ${response.missingEmails.joinToString(", ")} not found."
                }
                sendEffect(GroupDetailsContract.Effect.ShowToast(message))
            }.onFailure { e ->
                // Case B: Failure (e.g., 404 Not Found or Server Error)
                // You can parse the error body here if needed, but for now:
                sendEffect(GroupDetailsContract.Effect.ShowToast("No registered users found for those emails."))
            }
        }
    }

    private fun sendEffect(effect: GroupDetailsContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}