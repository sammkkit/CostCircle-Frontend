package com.samkit.costcircle.core.utils


import android.app.Activity
import android.util.Log
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.samkit.costcircle.app.MainActivity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import org.json.JSONObject
import com.samkit.costcircle.BuildConfig
/**
 * Wraps the Razorpay SDK logic to keep it out of the UI/ViewModel.
 * Needs to implement PaymentResultListener to get callbacks from Razorpay Activity.
 */
class PaymentManager(
    private val activity: Activity
) : PaymentResultListener {

    // Channel to send results back to the UI/ViewModel
    private val _paymentResult = Channel<PaymentResult>(Channel.BUFFERED)
    val paymentResult = _paymentResult.receiveAsFlow()

    // Update init in PaymentManager.kt
    init {
        Checkout.preload(activity.applicationContext)
        if (activity is MainActivity) {
            activity.paymentListener = this
        }
    }

    fun startSubscriptionPayment(
        subscriptionId: String,
        email: String,
        phone: String? = null // Dummy or real phone
    ) {
        val checkout = Checkout()
        checkout.setKeyID(BuildConfig.RAZORPAY_KEY_ID)

        try {
            val options = JSONObject()
            options.put("name", "CostCircle Pro")
            options.put("description", "Premium Subscription")
            options.put("subscription_id", subscriptionId) // IMPORTANT: Use subscription_id, not order_id
            options.put("currency", "INR")

            // UI Color (Matches your app theme)
            options.put("theme.color", "#667eea")

            // Retry options
            val retryObj = JSONObject()
            retryObj.put("enabled", true)
            retryObj.put("max_count", 4)
            options.put("retry", retryObj)

            // User details for pre-fill
            val prefill = JSONObject()
            prefill.put("email", email)
            // 👈 ONLY add phone if we actually have one
            if (!phone.isNullOrEmpty()) {
                prefill.put("contact", phone)
            }
            options.put("prefill", prefill)

            // Launch the Payment Activity
            checkout.open(activity, options)

        } catch (e: Exception) {
            Log.e("PaymentManager", "Error starting payment", e)
            _paymentResult.trySend(PaymentResult.Error("Failed to open payment: ${e.message}"))
        }
    }

    // --- Razorpay Callbacks ---

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        Log.d("PaymentManager", "Success: $razorpayPaymentId")
        _paymentResult.trySend(PaymentResult.Success(razorpayPaymentId ?: ""))
    }

    override fun onPaymentError(code: Int, response: String?) {
        Log.e("PaymentManager", "Error: $code - $response")
        _paymentResult.trySend(PaymentResult.Error(response ?: "Payment failed"))
    }

    // Simple Result Wrapper
    sealed interface PaymentResult {
        data class Success(val paymentId: String) : PaymentResult
        data class Error(val message: String) : PaymentResult
    }
}