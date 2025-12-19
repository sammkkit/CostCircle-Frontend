package com.samkit.costcircle.ui.screens.addExpense.models

data class AddExpenseUiState(
    val amount: String = "",
    val description: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
) {
    val isValid: Boolean
        get() = amount.isNotBlank() && amount.toDoubleOrNull() != null
}
