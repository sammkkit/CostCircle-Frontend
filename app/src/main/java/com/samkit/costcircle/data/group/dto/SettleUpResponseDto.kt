package com.samkit.costcircle.data.group.dto

import com.google.gson.annotations.SerializedName

data class SettleUpResponse(
    val msg: String,
    @SerializedName("payment_id")
    val paymentId: Long
)