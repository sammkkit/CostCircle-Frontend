package com.samkit.costcircle.data.group.dto

import kotlinx.serialization.Serializable

@Serializable
data class TransactionDto(
    val id: Long,
    val description: String?, // "Payment" or "Dinner"
    val amount: Double,
    val payerId: Long,
    val receiverId: Long? = null, // Only for SETTLEMENT
    val createdAt: String,
    val type: String, // "EXPENSE" or "SETTLEMENT"
    val category: String? = null,
    val groupName: String? = null,
    val payerName: String? = null,
    val receiverName: String? = null
)