package com.samkit.costcircle.ui.subscription.states


import com.samkit.costcircle.data.subscription.dto.SubscriptionDto

object SubscriptionContract {

    sealed interface State {
        // Initial Loading when opening the sheet
        data object Loading : State

        // Generic Error state
        data class Error(val message: String) : State

        // The main UI state
        data class Content(
            val isPremium: Boolean = false,
            val currentSubscription: SubscriptionDto? = null,
            // Used to show spinner on the "Pay" button specifically
            val isProcessingPayment: Boolean = false
        ) : State
    }

    sealed interface Event {
        data object Load : Event
        data object UpgradeClicked : Event
        data object CancelSubscriptionClicked : Event

        // Triggered by PaymentManager when SDK finishes
        data object PaymentSuccess : Event
        data class PaymentFailed(val error: String) : Event
    }

    sealed interface Effect {

        data class LaunchRazorpay(
            val subscriptionId: String,
            val email: String,
            val phone: String?
        ) : Effect

        data class ShowToast(val message: String) : Effect
        data object CloseSheet : Effect
    }
}