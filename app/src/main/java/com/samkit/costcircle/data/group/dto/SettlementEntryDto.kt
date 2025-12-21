package com.samkit.costcircle.data.group.dto

data class SettlementEntryDto(
    val payerUserId: Long,
    val payerName: String,
    val receiverUserId: Long,
    val receiverName: String,
    val amount: Double
)
