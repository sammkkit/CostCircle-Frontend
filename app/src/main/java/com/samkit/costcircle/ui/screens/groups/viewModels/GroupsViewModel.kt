package com.samkit.costcircle.ui.screens.groups.viewModels

import androidx.compose.foundation.rememberPlatformOverscrollFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samkit.costcircle.data.group.Repository.GroupRepository
import com.samkit.costcircle.ui.screens.groups.components.dummyGroups
import com.samkit.costcircle.ui.screens.groups.mappers.toUiModel
import com.samkit.costcircle.ui.screens.groups.models.GroupUiModel
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


class GroupsViewModel(
    private val repository: GroupRepository
) : ViewModel() {

    private val _state = MutableStateFlow<GroupsUiState>(GroupsUiState.Loading)
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<GroupsEffect>()
    val effect = _effect.asSharedFlow()

    init {
        loadGroups()
    }

    fun onEvent(event: GroupsEvent) {
        when (event) {
            GroupsEvent.Retry -> loadGroups()
            is GroupsEvent.GroupClicked -> {
                viewModelScope.launch {
                    _effect.emit(
                        GroupsEffect.NavigateToGroup(
                            groupId = event.groupId,
                            name = event.name
                        )
                    )
                    _effect.emit(
                        GroupsEffect.CreateToastWhenGroupClicked(
                            message = "Clicked on ${event.name}"
                        )
                    )
                }
            }
            else -> {}
        }
    }

    private fun loadGroups() {
        viewModelScope.launch {
            _state.value = GroupsUiState.Loading
            // repo call (mock or real)
            repository.getGroups()
                .catch {
                    _state.value =
                        GroupsUiState.Error("Failed to load groups")
                }
                .collect { groups ->
                    val uiGroups = groups.map { it.toUiModel() }
                    _state.value =
                        if (uiGroups.isEmpty()) {
                            GroupsUiState.Empty
                        } else {
                            GroupsUiState.Success(uiGroups)
                        }
                }
        }
    }
}

