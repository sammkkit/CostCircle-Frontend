package com.samkit.costcircle.ui.screens.groupsList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samkit.costcircle.data.group.dto.GroupSummaryDto
import com.samkit.costcircle.data.group.repository.GroupRepository
import com.samkit.costcircle.ui.screens.groupsList.states.GroupsContract
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

    // Keep a master list to filter against
    private var allGroups: List<GroupSummaryDto> = emptyList()

    init {
        // Observe the refresh trigger from the repository
        viewModelScope.launch {
            repository.groupsRefreshTrigger.collect {
                loadGroups() // Re-fetch data whenever the repository signals a change
            }
        }
        loadGroups() // Initial load
    }
    fun onEvent(event: GroupsContract.Event) {
        when (event) {
            GroupsContract.Event.Load,
            GroupsContract.Event.Retry -> loadGroups()

            is GroupsContract.Event.GroupClicked -> {
                sendEffect(GroupsContract.Effect.NavigateToGroup(event.groupId, event.groupName))
            }

            is GroupsContract.Event.CreateGroupClicked -> {
                sendEffect(GroupsContract.Effect.NavigateToCreateGroup)
            }

            is GroupsContract.Event.SearchQueryChanged -> filterGroups(event.query)

            GroupsContract.Event.ToggleSearch -> toggleSearch()
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
                    allGroups = emptyList()
                    _state.value = GroupsContract.State.Empty
                } else {
                    allGroups = groups

                    // Calculate Totals for the Header Card
                    // The backend sends positive absolute numbers in netAmount.
                    // We sum them based on the 'direction' flag.

                    val totalOwedToMe = groups
                        .filter { it.direction == "YOU_ARE_OWED" }
                        .sumOf { it.netAmount }

                    val totalIOwe = groups
                        .filter { it.direction == "YOU_OWE" }
                        .sumOf { it.netAmount }

                    _state.value = GroupsContract.State.Success(
                        groups = groups,
                        filteredGroups = groups,
                        totalOwedToYou = totalOwedToMe,
                        totalYouOwe = totalIOwe
                    )
                }
            }.onFailure {
                _state.value = GroupsContract.State.Error("Failed to load groups")
            }
        }
    }

    private fun filterGroups(query: String) {
        val currentState = _state.value as? GroupsContract.State.Success ?: return

        val filtered = if (query.isBlank()) {
            allGroups
        } else {
            allGroups.filter { it.groupName.contains(query, ignoreCase = true) }
        }

        _state.value = currentState.copy(
            searchQuery = query,
            filteredGroups = filtered
        )
    }

    private fun toggleSearch() {
        val currentState = _state.value as? GroupsContract.State.Success ?: return
        val newSearchActive = !currentState.isSearchActive

        // If closing search, reset the query and the list
        _state.value = currentState.copy(
            isSearchActive = newSearchActive,
            searchQuery = if (!newSearchActive) "" else currentState.searchQuery,
            filteredGroups = if (!newSearchActive) allGroups else currentState.filteredGroups
        )
    }

    private fun sendEffect(effect: GroupsContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}