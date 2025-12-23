package com.samkit.costcircle.ui.screens.addExpense.states

import com.samkit.costcircle.data.group.dto.GroupSummaryDto

object AddExpenseContract {
    data class State(
        val groups: List<GroupSummaryDto> = emptyList(),
        val selectedGroup: GroupSummaryDto? = null,
        val isLoadingGroups: Boolean = false,
        val amount: String = "",
        val description: String = "",
        val isSubmitting: Boolean = false,
        val errorMessage: String? = null
    )

    sealed interface Event {
        data object LoadGroups : Event
        data class GroupSelected(val group: GroupSummaryDto) : Event
        data class AmountChanged(val amount: String) : Event
        data class DescriptionChanged(val desc: String) : Event
        data object SaveExpense : Event
        data object Reset : Event
    }
    sealed interface Effect {
        data object ExpenseSaved : Effect
    }
}