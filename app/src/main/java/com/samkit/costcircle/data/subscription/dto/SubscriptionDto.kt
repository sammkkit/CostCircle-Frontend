package com.samkit.costcircle.data.subscription.dto

import com.google.gson.annotations.SerializedName

data class SubscriptionDto(
    @SerializedName("id") val id: String, // "sub_ABC123xyz"
    @SerializedName("status") val status: String,
    @SerializedName("short_url") val shortUrl: String?,
    @SerializedName("plan_id") val planId: String,
    @SerializedName("currentPeriodStart") val currentPeriodStart: String?,
    @SerializedName("currentPeriodEnd") val currentPeriodEnd: String?,
    @SerializedName("createdAt") val createdAt: String?
)