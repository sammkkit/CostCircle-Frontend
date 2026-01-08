package com.samkit.costcircle.data.subscription.repository

import com.samkit.costcircle.data.auth.session.SessionManager
import com.samkit.costcircle.data.subscription.dto.CancelSubscriptionResponse
import com.samkit.costcircle.data.subscription.dto.CreateSubscriptionResponse
import com.samkit.costcircle.data.subscription.dto.SubscriptionStatusResponse
import com.samkit.costcircle.data.subscription.remote.SubscriptionApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class SubscriptionRepository(
    private val api: SubscriptionApi,
    private val sessionManager: SessionManager
) {

    // Signal to refresh subscription status across the app (Account Screen, etc.)
    private val _subscriptionRefreshTrigger = MutableSharedFlow<Unit>(replay = 0)
    val subscriptionRefreshTrigger = _subscriptionRefreshTrigger.asSharedFlow()

    /**
     * Creates a Razorpay Subscription Order
     */
    suspend fun createSubscription(): CreateSubscriptionResponse {
        return api.createSubscription()
    }

    /**
     * Checks if the user is Premium and updates local Session
     */
    suspend fun getStatus(): SubscriptionStatusResponse {
        val response = api.getSubscriptionStatus()

        // Sync local session with server truth (Important!)
        // If server says premium is false, we must update local storage
        // Assuming sessionManager has a method like setPremium(boolean)
        // sessionManager.setPremium(response.isPremium)

        return response
    }

    /**
     * Cancels the active subscription
     */
    suspend fun cancelSubscription(): CancelSubscriptionResponse {
        val response = api.cancelSubscription()

        // Signal refresh so UI updates to show "Pending Cancellation" or similar
        _subscriptionRefreshTrigger.emit(Unit)

        return response
    }

    /**
     * Helper to force a refresh signal manually (e.g., after successful Payment)
     */
    suspend fun triggerRefresh() {
        _subscriptionRefreshTrigger.emit(Unit)
    }
}