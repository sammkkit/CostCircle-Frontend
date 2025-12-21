package com.samkit.costcircle.ui.screens.groupdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samkit.costcircle.data.auth.session.SessionManager
import com.samkit.costcircle.data.group.repository.GroupRepository
import com.samkit.costcircle.ui.screens.groups.states.GroupsContract
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

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
        onEvent(GroupDetailsContract.Event.Load)
    }

    fun onEvent(event: GroupDetailsContract.Event) {
        when (event) {
            GroupDetailsContract.Event.Load,
            GroupDetailsContract.Event.Retry -> loadDetails()

            GroupDetailsContract.Event.BackClicked -> sendEffect(GroupDetailsContract.Effect.NavigateBack)
        }
    }

    private fun loadDetails() {
        viewModelScope.launch {
            _state.value = GroupDetailsContract.State.Loading

            runCatching {
                repository.getGroupFinancialSummary(groupId)
            }.onSuccess { response ->
                _state.value =
                    if (response.settlements.isEmpty())
                        GroupDetailsContract.State.Empty
                    else
                        GroupDetailsContract.State.Success(response.settlements)
            }.onFailure {
                _state.value =
                    GroupDetailsContract.State.Error("Failed to load group details")
            }
        }
    }

    private fun sendEffect(effect: GroupDetailsContract.Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
