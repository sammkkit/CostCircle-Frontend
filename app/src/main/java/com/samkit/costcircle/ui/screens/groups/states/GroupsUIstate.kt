package com.samkit.costcircle.ui.screens.groups.states

import com.samkit.costcircle.ui.screens.groups.models.GroupUiModel

sealed interface GroupsUiState {
    data object Loading : GroupsUiState
    data object Empty : GroupsUiState
    data class Error(val message: String) : GroupsUiState
    data class Success(val groups: List<GroupUiModel>) : GroupsUiState
}
