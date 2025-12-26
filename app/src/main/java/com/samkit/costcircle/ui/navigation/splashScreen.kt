package com.samkit.costcircle.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.samkit.costcircle.R
import com.samkit.costcircle.data.auth.session.SessionManager
@Composable
fun SplashScreen(
    sessionManager: SessionManager,
    onFinished: (NavKey) -> Unit
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.splash_lotitie)
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1
    )

    // Only trigger navigation when progress is truly finished
    LaunchedEffect(progress) {
        if (progress == 1f) {
            onFinished(
                if (sessionManager.isLoggedIn()) Destination.Main else Destination.Login
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background), // Ensure this matches system splash color
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Only show the animation once it's actually loaded to prevent a flicker
        if (composition != null) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.fillMaxSize() // Or a specific size that matches your logo
            )
        }
    }
}