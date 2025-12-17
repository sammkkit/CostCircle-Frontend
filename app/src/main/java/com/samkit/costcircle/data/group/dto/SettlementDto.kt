package com.samkit.costcircle.data.group.dto

data class SettlementDto(
    val fromUserId: Long,
    val fromName: String?,
    val toUserId: Long,
    val toName: String?,
    val amount: Double
)
