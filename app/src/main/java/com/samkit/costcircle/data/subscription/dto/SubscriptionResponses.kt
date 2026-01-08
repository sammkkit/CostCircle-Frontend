package com.samkit.costcircle.data.subscription.dto


import com.google.gson.annotations.SerializedName

data class CreateSubscriptionResponse(
    @SerializedName("msg") val message: String,
    @SerializedName("subscription") val subscription: SubscriptionDto
)

data class SubscriptionStatusResponse(
    @SerializedName("hasSubscription") val hasSubscription: Boolean,
    @SerializedName("isPremium") val isPremium: Boolean,
    @SerializedName("subscription") val subscription: SubscriptionDto?
)

data class CancelSubscriptionResponse(
    @SerializedName("msg") val message: String,
    @SerializedName("currentPeriodEnd") val currentPeriodEnd: String?,
    @SerializedName("cancelledAt") val cancelledAt: String?
)