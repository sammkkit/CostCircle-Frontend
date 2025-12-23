package com.samkit.costcircle.data.group.dto

import com.google.gson.annotations.SerializedName

data class TransactionDto(
    val id: Long,

    val description: String?,

    val amount: Double,

    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("paid_by_name")
    val paidByName: String?,

    @SerializedName("was_paid_by_me")
    val wasPaidByMe: Boolean
)