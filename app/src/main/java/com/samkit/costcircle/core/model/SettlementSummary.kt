package com.samkit.costcircle.core.model


data class SettlementSummary(
    val userId: Long,
    val netAmount: Double,     // +ve receive, -ve owe
    val settlements: List<Settlement>
)
