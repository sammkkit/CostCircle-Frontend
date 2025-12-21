package com.samkit.costcircle.ui.screens.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samkit.costcircle.data.group.repository.GroupRepository
import com.samkit.costcircle.ui.screens.groups.states.GroupsContract
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

class GroupsViewModel(
    private val repository: GroupRepository
) : ViewModel() {

    private val _state = MutableStateFlow<GroupsContract.State>(GroupsContract.State.Empty)
    val state: StateFlow<GroupsContract.State> = _state.asStateFlow()

    private val _effect = Channel<GroupsContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

//    init {
//        onEvent(GroupsContract.Event.Load)
//    }

    fun onEvent(event: GroupsContract.Event) {
        when (event) {
            GroupsContract.Event.Load,
            GroupsContract.Event.Retry -> loadGroups()

            is GroupsContract.Event.GroupClicked -> {
                sendEffect(
                    GroupsContract.Effect.NavigateToGroup(
                        event.groupId,
                        event.groupName
                    )
                )
            }
        }
    }

    // GroupsViewModel.kt
    private fun loadGroups() {
        viewModelScope.launch {
            if (_state.value is GroupsContract.State.Loading) return@launch
            _state.value = GroupsContract.State.Loading

            runCatching {
                repository.getGroupsSummary()
            }.onSuccess { groups ->
                if (groups.isEmpty()) {
                    _state.value = GroupsContract.State.Empty
                } else {
                    // Proper Production Calculation:
                    val amounts = groups.map { it.netAmount ?: 0.0 }

                    val owed = amounts.filter { it > 0 }.sum()
                    val owe = amounts.filter { it < 0 }.sum().absoluteValue

                    _state.value = GroupsContract.State.Success(
                        groups = groups,
                        totalOwedToYou = owed,
                        totalYouOwe = owe
                    )
                }
            }.onFailure {
                _state.value = GroupsContract.State.Error("Failed to load groups")
            }
        }
    }

    private fun sendEffect(effect: GroupsContract.Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
