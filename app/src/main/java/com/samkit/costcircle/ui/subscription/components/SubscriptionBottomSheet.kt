package com.samkit.costcircle.ui.subscription.components


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.samkit.costcircle.core.utils.PaymentManager
import com.samkit.costcircle.ui.subscription.SubscriptionViewModel
import com.samkit.costcircle.ui.subscription.states.SubscriptionContract
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionBottomSheet(
    onDismiss: () -> Unit,
    // We pass PaymentManager from the Activity/Screen level
    paymentManager: PaymentManager?,
    viewModel: SubscriptionViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // 1. Listen for ViewModel Effects (Launch Razorpay, Toasts)
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SubscriptionContract.Effect.LaunchRazorpay -> {
                    paymentManager?.startSubscriptionPayment(
                        subscriptionId = effect.subscriptionId,
                        email = effect.email,
                        phone = effect.phone
                    )
                }
                is SubscriptionContract.Effect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                SubscriptionContract.Effect.CloseSheet -> onDismiss()
            }
        }
    }

    // 2. Listen for Razorpay SDK Results (Success/Failure)
    LaunchedEffect(paymentManager) {
        paymentManager?.paymentResult?.collect { result ->
            when (result) {
                is PaymentManager.PaymentResult.Success -> {
                    viewModel.onEvent(SubscriptionContract.Event.PaymentSuccess)
                }
                is PaymentManager.PaymentResult.Error -> {
                    viewModel.onEvent(SubscriptionContract.Event.PaymentFailed(result.message))
                }
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp) // Bottom padding for navigation bar
        ) {
            when (val s = state) {
                is SubscriptionContract.State.Loading -> {
                    Box(Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is SubscriptionContract.State.Error -> {
                    ErrorContent(message = s.message, onRetry = { viewModel.onEvent(SubscriptionContract.Event.Load) })
                }
                is SubscriptionContract.State.Content -> {
                    if (s.isPremium) {
                        // User is ALREADY a Subscriber
                        ActiveSubscriptionContent(
                            planName = "CostCircle Pro",
                            // Parse the date nicely if needed, or pass raw string for now
                            expiryDate = s.currentSubscription?.currentPeriodEnd ?: "Unknown",
                            // Pass the status here 👇
                            status = s.currentSubscription?.status ?: "active",
                            onCancel = { viewModel.onEvent(SubscriptionContract.Event.CancelSubscriptionClicked) }
                        )
                    } else {
                        // User is Free -> Show Paywall
                        PaywallContent(
                            isProcessing = s.isProcessingPayment,
                            onUpgrade = { viewModel.onEvent(SubscriptionContract.Event.UpgradeClicked) }
                        )
                    }
                }
            }
        }
    }
}

// --- UI COMPONENTS ---

@Composable
private fun PaywallContent(
    isProcessing: Boolean,
    onUpgrade: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Gold Badge
        Surface(
            shape = CircleShape,
            color = Color(0xFFFFD700).copy(alpha = 0.15f),
            modifier = Modifier.size(72.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Diamond,
                    contentDescription = null,
                    tint = Color(0xFFFFD700), // Gold
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Upgrade to Pro",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Unlock powerful tools to manage group finances better.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Benefits
        BenefitRow("Export expenses to Excel/CSV")
        BenefitRow("Detailed Spending Analytics")
        BenefitRow("Ad-Free Experience")
        BenefitRow("Priority Support")

        Spacer(modifier = Modifier.height(40.dp))

        // Upgrade Button
        Button(
            onClick = onUpgrade,
            enabled = !isProcessing,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            if (isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Processing...")
            } else {
                Text(
                    "Subscribe for ₹2 / month",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Cancel anytime. Secure payment via Razorpay.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}
@Composable
private fun ActiveSubscriptionContent(
    planName: String,
    expiryDate: String,
    status: String, // 👈 Add this parameter
    onCancel: () -> Unit
) {
    val isCancelling = status == "pending_cancellation"

    Column(
        modifier = Modifier.padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (isCancelling) Icons.Default.AccessTime else Icons.Default.Check,
            contentDescription = null,
            tint = if (isCancelling) Color(0xFFFF9800) else Color(0xFF4CAF50), // Orange if pending, Green if active
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isCancelling) "Cancellation Pending" else "You are a Pro Member!",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        // Status Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Current Plan", style = MaterialTheme.typography.labelMedium)
                Text(planName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                // Change label based on status
                Text(
                    text = if (isCancelling) "Access Ends On" else "Renews On",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(expiryDate, style = MaterialTheme.typography.bodyMedium)
            }
        }

        // Only show Cancel button if NOT already cancelling
        if (!isCancelling) {
            OutlinedButton(
                onClick = onCancel,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Cancel Subscription")
            }
        } else {
            // Optional: Show a message that they are good to go until the date
            Text(
                text = "You retain premium access until the end of the billing period.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun BenefitRow(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Close, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(message, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}