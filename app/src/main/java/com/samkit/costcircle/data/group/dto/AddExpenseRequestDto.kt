package com.samkit.costcircle.data.group.dto

data class AddExpenseRequest(
    val amount: Double,
    val description: String?,
    val paidBy: Long // The user ID who paid
)