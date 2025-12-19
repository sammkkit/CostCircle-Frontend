package com.samkit.costcircle.ui.screens.groups.models

data class ExpenseUiModel(
    val id: Int,
    val title: String,
    val subtitle: String,
    val amount: Double,
    val isOwed: Boolean
)