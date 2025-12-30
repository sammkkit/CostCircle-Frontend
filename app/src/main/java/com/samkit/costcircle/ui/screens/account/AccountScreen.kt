package com.samkit.costcircle.ui.screens.account

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.samkit.costcircle.core.utils.BiometricPromptManager
import com.samkit.costcircle.data.auth.session.SessionManager
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
fun AccountScreen(
    onLogout: () -> Unit,
    sessionManager: SessionManager = koinInject()
) {
    val name = sessionManager.getUserName() ?: "CostCircle User"
    val picture = sessionManager.getUserPicture()
    val email = sessionManager.getUserEmail() ?: ""

    val scrollState = rememberScrollState()
    val visibleState = remember { MutableTransitionState(false) }.apply { targetState = true }

    // Floating animation
    val infiniteTransition = rememberInfiniteTransition(label = "bg")
    val floatY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface) // Clean base
    ) {
        // Animated background orbs
        AnimatedBackgroundOrbs(floatY)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
                .navigationBarsPadding() // Handled system bars
        ) {
            Spacer(modifier = Modifier.height(60.dp)) // More top space

            AnimatedVisibility(
                visibleState = visibleState,
                enter = fadeIn(tween(800)) + scaleIn(
                    tween(800, easing = FastOutSlowInEasing),
                    initialScale = 0.9f
                )
            ) {
                ProfileHeroRevamped(name, email, picture)
            }

            Spacer(modifier = Modifier.height(40.dp))

//            AnimatedVisibility(
//                visibleState = visibleState,
//                enter = fadeIn(tween(600, delayMillis = 200)) +
//                        slideInVertically(tween(600, delayMillis = 200)) { it / 4 }
//            ) {
//                StatsCardsRow()
//            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visibleState = visibleState,
                enter = fadeIn(tween(600, delayMillis = 300)) +
                        slideInVertically(tween(600, delayMillis = 300)) { it / 4 }
            ) {
                SettingsCardRevamped()
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visibleState = visibleState,
                enter = fadeIn(tween(700, delayMillis = 400)) +
                        slideInVertically(tween(700, delayMillis = 400)) { it / 4 }
            ) {
                LogoutButtonRevamped(onLogout = {
                    sessionManager.clear()
                    onLogout()
                })
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun AnimatedBackgroundOrbs(floatY: Float) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Top-right orb (Subtler)
        Box(
            modifier = Modifier
                .size(300.dp) // Larger for smoother gradient
                .offset(x = 150.dp, y = (-100).dp)
                .graphicsLayer(translationY = floatY, alpha = 0.6f)
                .blur(80.dp) // Softer blur
                .background(
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    CircleShape
                )
        )

        // Bottom-left orb (Subtler)
        Box(
            modifier = Modifier
                .size(250.dp)
                .offset(x = (-80).dp, y = 100.dp)
                .graphicsLayer(translationY = -floatY / 1.5f, alpha = 0.5f)
                .blur(70.dp)
                .background(
                    MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
                    CircleShape
                )
                .align(Alignment.CenterStart)
        )
    }
}

@Composable
fun ProfileHeroRevamped(name: String, email: String, pictureUrl: String?) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(130.dp)
        ) {
            // Main avatar
            Surface(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                shadowElevation = 10.dp,
                tonalElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (pictureUrl != null) {
                        AsyncImage(
                            model = pictureUrl,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Fallback text avatar
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primaryContainer,
                                            MaterialTheme.colorScheme.surface
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = name.take(1).uppercase(),
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }
            }

            // Online indicator (Better Position)
            Surface(
                modifier = Modifier
                    .size(26.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = (-6).dp, y = (-6).dp),
                shape = CircleShape,
                color = Color(0xFF4CAF50),
                border = BorderStroke(3.dp, MaterialTheme.colorScheme.surface)
            ) {}
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = name,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            ),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = email,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.1.sp
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Premium Badge - Cleaner Pill Shape
        Surface(
            shape = CircleShape, // Fully rounded pill
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
            modifier = Modifier.height(36.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Verified, // Verified looks more premium than CheckCircle
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Premium Member",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

@Composable
fun StatsCardsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Groups,
            value = "12",
            label = "Active Groups",
            color = MaterialTheme.colorScheme.primary
        )
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Savings,
            value = "₹45K",
            label = "Total Saved",
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    label: String,
    color: Color
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        label = "statScale"
    )

    Surface(
        modifier = modifier
            .height(140.dp) // Slightly taller
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clickable(interactionSource = interactionSource, indication = null) {},
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface, // Clean background
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
        shadowElevation = 4.dp,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon in a subtle circle
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.1f),
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                )
            }
        }
    }
}

@Composable
fun SettingsCardRevamped() {
    // State for the dialogs
    // 1. Get the Activity from Compose Context
    val context = LocalContext.current
    // We need to cast it safely to AppCompatActivity
    val activity = context as? AppCompatActivity
        ?: error("Context is not AppCompatActivity. Ensure MainActivity extends AppCompatActivity")
    val biometricManager: BiometricPromptManager = koinInject { parametersOf(activity) }
    val sessionManager: SessionManager = koinInject()
    var showPersonalInfoDialog by remember { mutableStateOf(false) }
    var showSecurityDialog by remember { mutableStateOf(false) }
    var showAppearanceDialog by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Preferences",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 8.dp)
        )

        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp)) {
                // Option 1: Personal Info
                SettingsItemRevamped(
                    icon = Icons.Default.PersonOutline,
                    title = "Profile Details",
                    subtitle = "Display name & avatar",
                    color = MaterialTheme.colorScheme.primary,
                    onClick = { showPersonalInfoDialog = true }
                )

                // Option 2: Security (Biometrics instead of Password)
                SettingsItemRevamped(
                    icon = Icons.Default.Fingerprint, // 👈 Changed Icon
                    title = "App Lock & Security",    // 👈 Changed Title
                    subtitle = "Biometrics & data",
                    color = MaterialTheme.colorScheme.tertiary,
                    onClick = { showSecurityDialog = true }
                )

                // Option 3: Appearance
                SettingsItemRevamped(
                    icon = Icons.Default.Palette,
                    title = "Appearance",
                    subtitle = "Theme & display",
                    color = Color(0xFFFF6B9D),
                    isLast = true,
                    onClick = { showAppearanceDialog = true }
                )
            }
        }
    }

    // --- DIALOGS (The actual "Sensible Options") ---

    if (showPersonalInfoDialog) {
        PersonalInfoDialog(onDismiss = { showPersonalInfoDialog = false })
    }

    if (showSecurityDialog) {
        SecurityDialog(
            onDismiss = { showSecurityDialog = false },
            biometricPromptManager = biometricManager,
            sessionManager=sessionManager
        )
    }
}
@Composable
fun SecurityDialog(
    onDismiss: () -> Unit,
    biometricPromptManager: BiometricPromptManager,
    sessionManager: SessionManager
) {
    // In a real app, load this boolean from DataStore/SharedPreferences
    var isBiometricEnabled by remember {
        mutableStateOf(sessionManager.isBiometricEnabled())
    }
    val scope = rememberCoroutineScope()

    // Listen for results
    LaunchedEffect(biometricPromptManager) {
        biometricPromptManager.promptResults.collect { result ->
            when (result) {
                is BiometricPromptManager.BiometricResult.AuthenticationSuccess -> {
                    // User successfully proved identity -> Enable the feature
                    isBiometricEnabled = true
                    sessionManager.setBiometricEnabled(isBiometricEnabled)
                    // TODO: Save "true" to DataStore here
                }
                is BiometricPromptManager.BiometricResult.AuthenticationError -> {
                    // Handle error (show toast)
                }
                else -> Unit
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Fingerprint, contentDescription = null) },
        title = { Text("Security") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("App Lock", style = MaterialTheme.typography.titleSmall)
                        Text(
                            "Require biometric to open",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isBiometricEnabled,
                        onCheckedChange = { shouldEnable ->
                            if (shouldEnable) {
                                // Trigger Prompt to confirm identity BEFORE enabling
                                biometricPromptManager.showBiometricPrompt(
                                    title = "Enable App Lock",
                                    description = "Verify your identity to enable security."
                                )
                            } else {
                                isBiometricEnabled = false
                                sessionManager.setBiometricEnabled(isBiometricEnabled)
                                // TODO: Save "false" to DataStore
                            }
                        }
                    )
                }
                HorizontalDivider()
                // ... Delete Account button ...
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Done") } }
    )
}
@Composable
fun PersonalInfoDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Person, contentDescription = null) },
        title = { Text("Edit Profile") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Editable Display Name
                OutlinedTextField(
                    value = "Samkit Jain", // Replace with real state
                    onValueChange = { /* Update State */ },
                    label = { Text("Display Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Read-Only Email
                OutlinedTextField(
                    value = "samkit@gmail.com",
                    onValueChange = {},
                    label = { Text("Email (Google)") },
                    readOnly = true,
                    enabled = false,
                    leadingIcon = {
                        // Google G Icon (or generic mail)
                        Icon(Icons.Default.Email, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                )
                Text(
                    "To change your email or password, please manage your Google Account settings.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = { Button(onClick = onDismiss) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
@Composable
private fun SettingsItemRevamped(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    isLast: Boolean = false,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // 1. Tactile Feedback: Slight shrink when pressed
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "itemScale"
    )

    // 2. Visual Feedback: Subtle background tint
    val backgroundColor = if (isPressed) {
        color.copy(alpha = 0.05f) // Very subtle tint of the icon's color
    } else {
        Color.Transparent
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer(scaleX = scale, scaleY = scale) // Apply scale
                .clip(RoundedCornerShape(16.dp))
                .background(backgroundColor)
                .clickable(
                    interactionSource = interactionSource,
                    // 3. The Fix: Enable Ripple, color-matched to the icon
                    indication = ripple(color = color),
                    onClick = onClick
                )
                .padding(horizontal = 16.dp, vertical = 14.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {
            // Squircle Icon Background
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(14.dp),
                color = color.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(18.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Arrow icon with subtle alpha
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                modifier = Modifier.size(20.dp)
            )
        }

        if (!isLast) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 82.dp, end = 16.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
fun LogoutButtonRevamped(onLogout: () -> Unit) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "buttonScale"
    )

    Surface(
        onClick = { showLogoutDialog = true },
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale),
        shape = RoundedCornerShape(16.dp),
        // Changed to surface with border for a cleaner look than "red box"
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f)),
        shadowElevation = 2.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Sign Out",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            )
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text("Sign Out?", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Are you sure you want to sign out? You will need to login again.",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Sign Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    }
}