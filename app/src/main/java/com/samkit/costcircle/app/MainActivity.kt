package com.samkit.costcircle.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.messaging.FirebaseMessaging
import com.razorpay.PaymentResultListener
import com.samkit.costcircle.core.utils.BiometricPromptManager
import com.samkit.costcircle.data.auth.session.SessionManager
import com.samkit.costcircle.data.group.repository.GroupRepository
import com.samkit.costcircle.ui.navigation.AppNavHost
import com.samkit.costcircle.ui.theme.CostCircleTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(), PaymentResultListener {

    // Inject dependencies
    var paymentListener: PaymentResultListener? = null

    override fun onPaymentSuccess(p0: String?) {
        paymentListener?.onPaymentSuccess(p0)
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        paymentListener?.onPaymentError(p0, p1)
    }
    private val sessionManager: SessionManager by inject()
    private val repository: GroupRepository by inject()

    // 1. Create the Permission Launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("FCM", "Notification permission granted")
            // Permission granted? Great, now sync the token
            syncFcmToken()
        } else {
            Log.w("FCM", "Notification permission denied")
            // Optionally tell user why they need notifications
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val biometricManager = BiometricPromptManager(this)
        // 2. Logic to ask permission
        askNotificationPermission()

        setContent {
            CostCircleTheme {
                val isLockEnabled = remember { sessionManager.isBiometricEnabled() }

                // If lock is enabled, start as "Locked" (false). Otherwise, "Unlocked" (true).
                var isUnlocked by remember { mutableStateOf(!isLockEnabled) }

                // 4. Trigger Authentication if locked
                LaunchedEffect(Unit) {
                    if (isLockEnabled) {
                        biometricManager.showBiometricPrompt(
                            title = "Unlock CostCircle",
                            description = "Verify your identity to access your expenses"
                        )

                        biometricManager.promptResults.collect { result ->
                            if (result is BiometricPromptManager.BiometricResult.AuthenticationSuccess) {
                                isUnlocked = true
                            }
                            // Optional: If AuthenticationError or Failed, you might want to close the app
                            // or show a "Retry" button.
                        }
                    }
                }

                // 5. Conditional Rendering
                if (isUnlocked) {
                    AppNavHost(
                        activity = this
                    )
                } else {
                    // Show a blank/logo screen while waiting for Fingerprint
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    )
                }
            }
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // Already granted: Sync Token
                syncFcmToken()
            } else {
                // Not granted: Ask the user
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            // Android 12 or lower: Permission is granted automatically
            syncFcmToken()
        }
    }

    // 3. Your existing Sync Logic (Moved to a function)
    private fun syncFcmToken() {
        if (sessionManager.getToken() != null) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }

                val token = task.result
                Log.d("FCM", "Token retrieved: $token")

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        repository.updateFcmToken(token)
                        Log.d("FCM", "Token synced with backend")
                    } catch (e: Exception) {
                        Log.e("FCM", "Failed to sync token", e)
                    }
                }
            }
        }
    }
}