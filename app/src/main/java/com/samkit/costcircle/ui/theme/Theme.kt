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
import com.yourapp.costcircle.ui.theme.DarkElevatedSurface
import com.yourapp.costcircle.ui.theme.DarkSurface
import com.yourapp.costcircle.ui.theme.DeepGrey
import com.yourapp.costcircle.ui.theme.DividerDark
import com.yourapp.costcircle.ui.theme.DividerLight
import com.yourapp.costcircle.ui.theme.GreenOwed
import com.yourapp.costcircle.ui.theme.LightBackground
import com.yourapp.costcircle.ui.theme.LightSurface
import com.yourapp.costcircle.ui.theme.OrangeOwe
import com.yourapp.costcircle.ui.theme.TextPrimaryDark
import com.yourapp.costcircle.ui.theme.TextPrimaryLight
import com.yourapp.costcircle.ui.theme.TextSecondaryDark
import com.yourapp.costcircle.ui.theme.TextSecondaryLight

private val DarkColorScheme = darkColorScheme(
    primary = AccentTeal,
    secondary = GreenOwed,      // Mapped for Owed status
    tertiary = OrangeOwe,       // Mapped for Owe status
    background = DeepGrey,
    surface = DarkSurface,
    surfaceVariant = DarkElevatedSurface, // Used for elevated cards

    onPrimary = Color.Black,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark,
    outlineVariant = DividerDark
)

private val LightColorScheme = lightColorScheme(
    primary = AccentTeal,
    secondary = GreenOwed,
    tertiary = OrangeOwe,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = Color.White,

    onPrimary = Color.White,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    onSurfaceVariant = TextSecondaryLight,
    outlineVariant = DividerLight
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