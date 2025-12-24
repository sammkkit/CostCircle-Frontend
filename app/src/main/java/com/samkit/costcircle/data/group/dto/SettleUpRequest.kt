package com.samkit.costcircle.data.group.dto

import com.google.gson.annotations.SerializedName

data class SettleUpRequest(
    val receiverId: Long,
    val amount: Double
)