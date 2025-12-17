package com.samkit.costcircle.core.model

/**
 * balance > 0 → user should receive money
 * balance < 0 → user owes money
 */
data class UserBalance(
    val userId: Long,
    val name: String,
    val balance: Double
)
