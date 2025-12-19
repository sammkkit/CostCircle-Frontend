package com.samkit.costcircle.ui.screens.groups.states

import com.samkit.costcircle.ui.screens.groups.models.ExpenseUiModel

sealed interface GroupDetailsUiState {
    data object Loading : GroupDetailsUiState
    data object Empty : GroupDetailsUiState
    data class Error(val message: String) : GroupDetailsUiState
    data class Success(
        val expenses: List<ExpenseUiModel>
    ) : GroupDetailsUiState
}