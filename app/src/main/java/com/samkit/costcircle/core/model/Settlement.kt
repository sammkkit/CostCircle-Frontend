package com.samkit.costcircle.core.model

data class Settlement(
    val fromUserId: Long,
    val toUserId: Long,
    val amount: Double
)
