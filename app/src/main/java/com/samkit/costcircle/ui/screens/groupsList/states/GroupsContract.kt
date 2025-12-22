package com.samkit.costcircle.ui.screens.groupsList.states

import com.samkit.costcircle.data.group.dto.GroupSummaryDto

object GroupsContract {

    sealed interface State {
        data object Loading : State
        data object Empty : State
        data class Error(val message: String) : State
        data class Success(
            val groups: List<GroupSummaryDto>,
            val filteredGroups: List<GroupSummaryDto>,
            val totalOwedToYou: Double,
            val totalYouOwe: Double,
            val searchQuery: String = "",
            val isSearchActive: Boolean = false
        ) : State
    }

    sealed interface Event {
        data object Load : Event
        data object Retry : Event
        data class GroupClicked(
            val groupId: Long,
            val groupName: String
        ) : Event

        object CreateGroupClicked : Event
        data class SearchQueryChanged(val query: String) : Event
        data object ToggleSearch : Event
    }

    sealed interface Effect {
        data class NavigateToGroup(
            val groupId: Long,
            val groupName: String
        ) : Effect

        object NavigateToCreateGroup : Effect
    }
}
