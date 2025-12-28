package com.samkit.costcircle.data.group.dto

data class AddExpenseRequest(
    val amount: Double,
    val description: String?,
    val paidBy: Long, // The user ID who paid

    // --- NEW FIELDS ---
    val splitType: SplitType = SplitType.EQUAL, // Defaults to EQUAL
    val splits: List<SplitEntryDto> = emptyList() // Defaults to empty
    ,val category: String

)