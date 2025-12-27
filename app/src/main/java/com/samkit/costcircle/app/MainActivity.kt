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
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.messaging.FirebaseMessaging
import com.samkit.costcircle.data.auth.session.SessionManager
import com.samkit.costcircle.data.group.repository.GroupRepository
import com.samkit.costcircle.ui.navigation.AppNavHost
import com.samkit.costcircle.ui.theme.CostCircleTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    // Inject dependencies
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

        // 2. Logic to ask permission
        askNotificationPermission()

        setContent {
            CostCircleTheme {
                AppNavHost()
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