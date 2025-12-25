package com.samkit.costcircle.ui.screens.addExpense.states

import com.samkit.costcircle.data.auth.dto.UserDto

data class UserSplitUiModel(
    val user: UserDto,
    val isSelected: Boolean = true, // For "Subset" selection (Radio/Checkbox)
    val assignedValue: String = "", // What the user typed (Amount or Percentage)
    val calculatedAmount: Double = 0.0 // The result (e.g., 50% -> ₹100)
)