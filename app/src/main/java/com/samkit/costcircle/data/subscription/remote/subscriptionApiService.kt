package com.samkit.costcircle.data.subscription.remote

import com.samkit.costcircle.data.subscription.dto.CancelSubscriptionResponse
import com.samkit.costcircle.data.subscription.dto.CreateSubscriptionResponse
import com.samkit.costcircle.data.subscription.dto.SubscriptionStatusResponse
import retrofit2.http.GET
import retrofit2.http.POST

interface SubscriptionApi {

    @POST("subscription/create")
    suspend fun createSubscription(): CreateSubscriptionResponse

    @GET("subscription/status")
    suspend fun getSubscriptionStatus(): SubscriptionStatusResponse

    @POST("subscription/cancel")
    suspend fun cancelSubscription(): CancelSubscriptionResponse
}
