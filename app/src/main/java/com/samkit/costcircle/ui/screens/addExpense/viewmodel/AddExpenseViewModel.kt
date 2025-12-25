package com.samkit.costcircle.ui.screens.addExpense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samkit.costcircle.data.auth.session.SessionManager
import com.samkit.costcircle.data.group.dto.AddExpenseRequest
import com.samkit.costcircle.data.group.dto.SplitEntryDto
import com.samkit.costcircle.data.group.dto.SplitType
import com.samkit.costcircle.data.group.repository.GroupRepository
import com.samkit.costcircle.ui.screens.addExpense.states.AddExpenseContract
import com.samkit.costcircle.ui.screens.addExpense.states.UserSplitUiModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddExpenseViewModel(
    private val repository: GroupRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(AddExpenseContract.State())
    val state: StateFlow<AddExpenseContract.State> = _state.asStateFlow()

    private val _effect = Channel<AddExpenseContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        onEvent(AddExpenseContract.Event.LoadGroups)
    }

    fun onEvent(event: AddExpenseContract.Event) {
        when (event) {
            AddExpenseContract.Event.LoadGroups -> loadGroups()

            is AddExpenseContract.Event.GroupSelected -> {
                _state.value = _state.value.copy(selectedGroup = event.group)
                // Fetch members immediately when group changes
                fetchGroupMembers(event.group.groupId)
            }

            is AddExpenseContract.Event.AmountChanged -> {
                _state.value = _state.value.copy(amount = event.amount)
                recalculateSplits() // Recalculate if total changes
            }

            is AddExpenseContract.Event.DescriptionChanged -> {
                _state.value = _state.value.copy(description = event.desc)
            }

            is AddExpenseContract.Event.SplitTypeChanged -> {
                // RESET all entered values when switching tabs
                // This prevents "50" in Exact mode from becoming "50%" in Percentage mode
                val resetMembers = _state.value.splitMembers.map {
                    it.copy(assignedValue = "")
                }

                _state.value = _state.value.copy(
                    splitType = event.type,
                    splitMembers = resetMembers
                )
                recalculateSplits()
            }

            is AddExpenseContract.Event.SplitMemberToggled -> {
                // Only for EQUAL mode (Selecting who is involved)
                val updatedList = _state.value.splitMembers.map {
                    if (it.user.id == event.userId) it.copy(isSelected = !it.isSelected) else it
                }
                _state.value = _state.value.copy(splitMembers = updatedList)
            }

            is AddExpenseContract.Event.SplitValueChanged -> {
                // For PERCENT/EXACT modes
                val updatedList = _state.value.splitMembers.map {
                    if (it.user.id == event.userId) it.copy(assignedValue = event.value) else it
                }
                _state.value = _state.value.copy(splitMembers = updatedList)
                recalculateSplits()
            }

            AddExpenseContract.Event.SaveExpense -> saveExpense()

            AddExpenseContract.Event.Reset -> {
                _state.value = AddExpenseContract.State(groups = _state.value.groups)
            }
        }
    }

    private fun fetchGroupMembers(groupId: Long) {
        viewModelScope.launch {
            runCatching {
                repository.getGroupMembers(groupId)
            }.onSuccess { members ->
                // Convert DTOs to UI Models (Selected by default)
                val uiModels = members.map { UserSplitUiModel(user = it, isSelected = true) }
                _state.value = _state.value.copy(splitMembers = uiModels)
            }
        }
    }

    private fun recalculateSplits() {
        val currentState = _state.value
        val totalAmount = currentState.amount.toDoubleOrNull() ?: 0.0

        if (currentState.splitType == SplitType.EXACT) {
            val sumEntered = currentState.splitMembers.sumOf { it.assignedValue.toDoubleOrNull() ?: 0.0 }
            _state.value = currentState.copy(remainingAmount = totalAmount - sumEntered)

        } else if (currentState.splitType == SplitType.PERCENTAGE) {
            val sumPercent = currentState.splitMembers.sumOf { it.assignedValue.toDoubleOrNull() ?: 0.0 }
            _state.value = currentState.copy(remainingPercent = 100.0 - sumPercent)
        }
    }

    // 2. UPDATE THIS FUNCTION
    private fun saveExpense() {
        val currentState = _state.value
        val amountValue = currentState.amount.toDoubleOrNull()
        val groupId = currentState.selectedGroup?.groupId
        val currentUserId = sessionManager.getUserId()

        // --- BASIC VALIDATION ---
        if (groupId == null) {
            _state.value = currentState.copy(errorMessage = "Please select a group")
            return
        }
        if (amountValue == null || amountValue <= 0) {
            _state.value = currentState.copy(errorMessage = "Please enter a valid amount")
            return
        }

        // --- NEW: FRONTEND MATH VALIDATION ---
        when (currentState.splitType) {
            SplitType.EQUAL -> {
                if (currentState.splitMembers.none { it.isSelected }) {
                    _state.value = currentState.copy(errorMessage = "Select at least one person to split with")
                    return
                }
            }
            SplitType.EXACT -> {
                // Allow tiny floating point difference (e.g., 0.01)
                if (Math.abs(currentState.remainingAmount) > 0.02) {
                    _state.value = currentState.copy(errorMessage = "Allocation must equal the total amount")
                    return
                }
            }
            SplitType.PERCENTAGE -> {
                if (Math.abs(currentState.remainingPercent) > 0.1) {
                    _state.value = currentState.copy(errorMessage = "Percentages must equal 100%")
                    return
                }
            }
        }

        // --- CONSTRUCT SPLITS FOR API (Existing code...) ---
        val splitsToSend = mutableListOf<SplitEntryDto>()
        // ... (rest of your existing save logic) ...
        // (Ensure you copy the rest of the function I gave you previously here)

        when (currentState.splitType) {
            SplitType.EQUAL -> {
                currentState.splitMembers.filter { it.isSelected }.forEach {
                    splitsToSend.add(SplitEntryDto(userId = it.user.id))
                }
            }
            SplitType.PERCENTAGE, SplitType.EXACT -> {
                currentState.splitMembers.forEach { uiModel ->
                    val value = uiModel.assignedValue.toDoubleOrNull() ?: 0.0
                    if (value > 0) {
                        splitsToSend.add(SplitEntryDto(userId = uiModel.user.id, value = value))
                    }
                }
            }
        }

        viewModelScope.launch {
            _state.value = currentState.copy(isSubmitting = true, errorMessage = null)
            runCatching {
                repository.addExpense(
                    groupId = groupId,
                    request = AddExpenseRequest(
                        amount = amountValue,
                        description = currentState.description,
                        paidBy = currentUserId ?: 0,
                        splitType = currentState.splitType,
                        splits = splitsToSend
                    )
                )
            }.onSuccess {
                _state.value = _state.value.copy(isSubmitting = false)
                _effect.send(AddExpenseContract.Effect.ExpenseSaved)
            }.onFailure { e ->
                _state.value = _state.value.copy(isSubmitting = false, errorMessage = e.message)
            }
        }
    }

    private fun loadGroups() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingGroups = true)
            runCatching {
                repository.getGroupsSummary()
            }.onSuccess { groups ->
                _state.value = _state.value.copy(
                    groups = groups,
                    isLoadingGroups = false
                )
            }.onFailure {
                _state.value = _state.value.copy(isLoadingGroups = false)
            }
        }
    }

    

}