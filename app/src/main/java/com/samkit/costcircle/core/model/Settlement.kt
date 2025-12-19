package com.samkit.costcircle.core.model

data class Settlement(
    val fromUserId: Long,
    val fromName: String?,
    val toUserId: Long,
    val toName: String?,
    val amount: Double
)
