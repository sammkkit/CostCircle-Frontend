package com.samkit.costcircle.ui.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samkit.costcircle.data.auth.session.SessionManager
import com.samkit.costcircle.data.subscription.repository.SubscriptionRepository
import com.samkit.costcircle.ui.subscription.states.SubscriptionContract
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SubscriptionViewModel(
    private val repository: SubscriptionRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow<SubscriptionContract.State>(SubscriptionContract.State.Loading)
    val state: StateFlow<SubscriptionContract.State> = _state.asStateFlow()

    private val _effect = Channel<SubscriptionContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        onEvent(SubscriptionContract.Event.Load)
    }

    fun onEvent(event: SubscriptionContract.Event) {
        when (event) {
            SubscriptionContract.Event.Load -> loadStatus()
            SubscriptionContract.Event.UpgradeClicked -> initiatePurchase()
            SubscriptionContract.Event.CancelSubscriptionClicked -> cancelSubscription()

            // Callbacks from UI/PaymentManager
            SubscriptionContract.Event.PaymentSuccess -> verifyPayment()
            is SubscriptionContract.Event.PaymentFailed -> {
                updateContentState { it.copy(isProcessingPayment = false) }
                sendEffect(SubscriptionContract.Effect.ShowToast("Payment Failed: ${event.error}"))
            }
        }
    }

    private fun loadStatus() {
        viewModelScope.launch {
            _state.value = SubscriptionContract.State.Loading
            runCatching {
                repository.getStatus()
            }.onSuccess { response ->
                _state.value = SubscriptionContract.State.Content(
                    isPremium = response.isPremium,
                    currentSubscription = response.subscription
                )
            }.onFailure { e ->
                _state.value = SubscriptionContract.State.Error(e.message ?: "Failed to load status")
            }
        }
    }

    private fun initiatePurchase() {
        viewModelScope.launch {
            // 1. Show loader on button
            updateContentState { it.copy(isProcessingPayment = true) }

            runCatching {
                // 2. Call API to get sub_ID
                repository.createSubscription()
            }.onSuccess { response ->
                // 3. Launch Razorpay SDK via Effect
                val userEmail = sessionManager.getUserEmail() ?: "user@costcircle.com"

                sendEffect(SubscriptionContract.Effect.LaunchRazorpay(
                    subscriptionId = response.subscription.id,
                    email = userEmail,
                    phone = "9999999999" // Optional: You could fetch from profile if available
                ))
            }.onFailure { e ->
                updateContentState { it.copy(isProcessingPayment = false) }
                sendEffect(SubscriptionContract.Effect.ShowToast(e.message ?: "Failed to start payment"))
            }
        }
    }

    private fun verifyPayment() {
        viewModelScope.launch {
            // Keep the loader showing while we verify
            updateContentState { it.copy(isProcessingPayment = true) }

            sendEffect(SubscriptionContract.Effect.ShowToast("Verifying payment..."))

            // Poll the server twice to ensure Webhook has processed
            var isVerified = false
            repeat(3) {
                if (isVerified) return@repeat

                runCatching { repository.getStatus() }
                    .onSuccess {
                        if (it.isPremium) {
                            isVerified = true
                            _state.value = SubscriptionContract.State.Content(
                                isPremium = true,
                                currentSubscription = it.subscription,
                                isProcessingPayment = false
                            )
                            sendEffect(SubscriptionContract.Effect.ShowToast("Welcome to Pro! 🌟"))
                            // Optional: Close sheet automatically
                             sendEffect(SubscriptionContract.Effect.CloseSheet)
                        }
                    }
                if (!isVerified) delay(2000) // Wait 2s before retry
            }

            if (!isVerified) {
                updateContentState { it.copy(isProcessingPayment = false) }
                sendEffect(SubscriptionContract.Effect.ShowToast("Payment received. Activating shortly..."))
            }
        }
    }

    private fun cancelSubscription() {
        viewModelScope.launch {
            runCatching {
                repository.cancelSubscription()
            }.onSuccess {
                sendEffect(SubscriptionContract.Effect.ShowToast("Subscription cancelled."))
                loadStatus() // Refresh UI to show cancellation status
            }.onFailure {
                sendEffect(SubscriptionContract.Effect.ShowToast("Failed to cancel: ${it.message}"))
            }
        }
    }

    // Helper to safely update Content state without losing data
    private fun updateContentState(update: (SubscriptionContract.State.Content) -> SubscriptionContract.State.Content) {
        val current = _state.value
        if (current is SubscriptionContract.State.Content) {
            _state.value = update(current)
        }
    }

    private fun sendEffect(effect: SubscriptionContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}