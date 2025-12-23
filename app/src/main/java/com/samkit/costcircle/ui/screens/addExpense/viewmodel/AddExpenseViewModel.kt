package com.samkit.costcircle.ui.screens.addExpense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samkit.costcircle.data.group.repository.GroupRepository
import com.samkit.costcircle.ui.screens.addExpense.states.AddExpenseContract
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddExpenseViewModel(
    private val repository: GroupRepository
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
            }
            is AddExpenseContract.Event.AmountChanged -> {
                _state.value = _state.value.copy(amount = event.amount)
            }
            is AddExpenseContract.Event.DescriptionChanged -> {
                _state.value = _state.value.copy(description = event.desc)
            }
            is AddExpenseContract.Event.Reset -> {
                _state.value = AddExpenseContract.State(
                    groups = _state.value.groups
                )
            }

            AddExpenseContract.Event.SaveExpense -> saveExpense()
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

    private fun saveExpense() {
        val currentState = _state.value
        val amountValue = currentState.amount.toDoubleOrNull()
        val groupId = currentState.selectedGroup?.groupId

        // 1. Validation
        if (groupId == null) {
            _state.value = currentState.copy(errorMessage = "Please select a group")
            return
        }
        if (amountValue == null || amountValue <= 0) {
            _state.value = currentState.copy(errorMessage = "Please enter a valid amount")
            return
        }

        viewModelScope.launch {
            // 2. Set Loading State
            _state.value = _state.value.copy(isSubmitting = true, errorMessage = null)

            runCatching {
                // 3. API Call
                repository.addExpense(
                    groupId = groupId,
                    amount = amountValue,
                    description = currentState.description,
                )
            }.onSuccess {
                // 4. Success handling (UI will react to isSubmitting = false and navigate via Screen)
                _state.value = _state.value.copy(isSubmitting = false)
                _effect.send(AddExpenseContract.Effect.ExpenseSaved)
            }.onFailure { e ->
                // 5. Error handling
                _state.value = _state.value.copy(
                    isSubmitting = false,
                    errorMessage = e.message ?: "Failed to save expense"
                )
            }
        }
    }
}