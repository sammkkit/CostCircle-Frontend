package com.samkit.costcircle.ui.screens.auth

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.samkit.costcircle.R // Ensure you have a Google logo drawable
import com.samkit.costcircle.data.auth.remote.GoogleAuthClient
import com.samkit.costcircle.ui.screens.auth.components.AuthHeader
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = koinViewModel() // Use the new MVI ViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Google Auth Helper
    val googleAuthClient = remember { GoogleAuthClient(context) }

    // Handle MVI Effects (Navigation/Error)
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LoginContract.Effect.NavigateToHome -> onLoginSuccess()
                is LoginContract.Effect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Button Animation State
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.95f else 1f, label = "scale")

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 1. Beautiful Header
                AuthHeader(
                    title = "Welcome to CostCircle",
                    subtitle = "Split bills, not friendships."
                )

                Spacer(modifier = Modifier.height(48.dp))

                // 2. The One and Only Google Button
                Surface(
                    onClick = {
                        if (!state.isLoading) {
                            scope.launch {
                                val token = googleAuthClient.signIn()
                                if (token != null) {
                                    viewModel.onEvent(LoginContract.Event.GoogleLogin(token))
                                } else {
                                    Toast.makeText(context, "Google Sign-In Cancelled", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        },
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh, // Light grey/surface
                    tonalElevation = 4.dp,
                    interactionSource = interactionSource,
                    enabled = !state.isLoading
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Signing in...")
                        } else {
                            // Requires a Google Logo drawable (R.drawable.ic_google_logo)
                            // If you don't have one, just use text for now.
                            // Icon(painter = painterResource(id = R.drawable.ic_google_logo), contentDescription = null, tint = Color.Unspecified)

                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Continue with Google",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "No password to remember. Secure and fast.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}