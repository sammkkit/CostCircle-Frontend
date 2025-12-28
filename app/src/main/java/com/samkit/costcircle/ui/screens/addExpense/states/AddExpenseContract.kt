package com.samkit.costcircle.ui.screens.addExpense.states

import com.samkit.costcircle.data.group.dto.ExpenseCategory
import com.samkit.costcircle.data.group.dto.GroupSummaryDto
import com.samkit.costcircle.data.group.dto.SplitType

object AddExpenseContract {
    data class State(
        val groups: List<GroupSummaryDto> = emptyList(),
        val selectedGroup: GroupSummaryDto? = null,
        val isLoadingGroups: Boolean = false,
        val amount: String = "",
        val description: String = "",
        val selectedCategory: ExpenseCategory = ExpenseCategory.GENERAL,
        // --- NEW SPLIT LOGIC ---
        val splitType: SplitType = SplitType.EQUAL,
        val splitMembers: List<UserSplitUiModel> = emptyList(), // The list of people to split with
        val remainingAmount: Double = 0.0, // For "Exact" mode (e.g., "₹50 left to assign")
        val remainingPercent: Double = 100.0, // For "Percent" mode

        val isSubmitting: Boolean = false,
        val errorMessage: String? = null
    )

    sealed interface Event {
        data object LoadGroups : Event
        data class GroupSelected(val group: GroupSummaryDto) : Event
        data class AmountChanged(val amount: String) : Event
        data class DescriptionChanged(val desc: String) : Event
        data class CategorySelected(val category: ExpenseCategory) : Event
        // --- NEW EVENTS ---
        data class SplitTypeChanged(val type: SplitType) : Event
        data class SplitMemberToggled(val userId: Long) : Event // For EQUAL selection
        data class SplitValueChanged(val userId: Long, val value: String) : Event // For EXACT/PERCENT inputs

        data object SaveExpense : Event
        data object Reset : Event
    }
    sealed interface Effect {
        data object ExpenseSaved : Effect
        data class ShowToast(val message: String) : Effect // Generic toast helper
    }
}