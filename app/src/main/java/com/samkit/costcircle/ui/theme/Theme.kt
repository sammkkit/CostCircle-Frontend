package com.samkit.costcircle.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.yourapp.costcircle.ui.theme.AccentTeal
import com.yourapp.costcircle.ui.theme.DarkBackground
import com.yourapp.costcircle.ui.theme.DarkSurface
import com.yourapp.costcircle.ui.theme.LightBackground
import com.yourapp.costcircle.ui.theme.LightSurface
import com.yourapp.costcircle.ui.theme.TextPrimaryDark
import com.yourapp.costcircle.ui.theme.TextPrimaryLight

private val DarkColorScheme = darkColorScheme(
    primary = AccentTeal,
    background = DarkBackground,
    surface = DarkSurface,

    onPrimary = Color.Black,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = AccentTeal,
    background = LightBackground,
    surface = LightSurface,

    onPrimary = Color.White,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight
)


@Composable
fun CostCircleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}