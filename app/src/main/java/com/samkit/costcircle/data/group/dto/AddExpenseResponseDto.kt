package com.samkit.costcircle.data.group.dto

data class AddExpenseResponse(
    val msg: String,
    val expenseId: Long,
    val category: String?
)