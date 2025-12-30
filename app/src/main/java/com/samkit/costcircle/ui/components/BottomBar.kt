package com.samkit.costcircle.ui.components

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.samkit.costcircle.ui.navigation.Destination

@Composable
fun BottomBar(
    selected: Destination,
    onSelect: (Destination) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem(Destination.Groups, Icons.Outlined.Home, Icons.Filled.Home, "Groups"),
        NavItem(Destination.Activity, Icons.Outlined.Notifications, Icons.Filled.Notifications, "Activity"),
        NavItem(Destination.Account, Icons.Outlined.Person, Icons.Filled.Person, "Account")
    )

    // Floating Container
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 32.dp, end = 32.dp, bottom = 32.dp, top = 16.dp)
    ) {
        // The Glass Capsule
        GlassmorphicCapsule {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    GlassNavItem(
                        item = item,
                        isSelected = selected == item.destination,
                        onClick = { onSelect(item.destination) }
                    )
                }
            }
        }
    }
}

/**
 * 🎨 FIXED: iOS-Style Glassmorphism Container
 * Key Fix: Blur is REMOVED from the container itself
 * The blur effect should be on the background behind the bar, not on the bar
 */
@Composable
fun GlassmorphicCapsule(
    content: @Composable () -> Unit
) {
    // 1. The Glass Gradient (More opaque for better visibility)
    val glassGradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f), // Higher opacity
            MaterialTheme.colorScheme.surface.copy(alpha = 0.85f), // Higher opacity
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    // 2. The Border Gradient (Simulates light catching the top edge)
    val borderGradient = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.6f), // Bright top edge
            Color.White.copy(alpha = 0.1f)  // Subtle bottom edge
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            // ❌ REMOVED: The blur was here causing the problem!
            // Don't blur the container - blur should be on background layer
            .background(glassGradient, RoundedCornerShape(percent = 50))
            .border(
                BorderStroke(1.5.dp, borderGradient),
                RoundedCornerShape(percent = 50)
            )
            .clip(RoundedCornerShape(percent = 50))
    ) {
        content()
    }
}

@Composable
private fun RowScope.GlassNavItem(
    item: NavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    // Spring Animation for Scale
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.25f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    // Color Animation
    val iconColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        label = "color"
    )

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Disable ripple for cleaner look
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Optional: A subtle "glow" behind the selected item
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .blur(50.dp) // Soft glow
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        RoundedCornerShape(50)
                    )
            )
        }

        Icon(
            imageVector = if (isSelected) item.selectedIcon else item.icon,
            contentDescription = item.label,
            tint = iconColor,
            modifier = Modifier
                .size(28.dp)
                .scale(scale)
        )
    }
}

private data class NavItem(
    val destination: Destination,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val label: String
)