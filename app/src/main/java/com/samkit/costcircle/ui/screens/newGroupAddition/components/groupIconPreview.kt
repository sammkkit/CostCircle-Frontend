package com.samkit.costcircle.ui.screens.newGroupAddition.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yourapp.costcircle.ui.theme.AccentTeal
import com.yourapp.costcircle.ui.theme.AvatarBlue
import com.yourapp.costcircle.ui.theme.AvatarGold
import com.yourapp.costcircle.ui.theme.AvatarPink
import com.yourapp.costcircle.ui.theme.AvatarPurple

@Composable
fun GroupIconPreview(groupName: String) {
    // Reuse your dynamic color logic from GroupSummaryCard
    val avatarColors = listOf(AccentTeal, AvatarPurple, AvatarBlue, AvatarGold, AvatarPink)
    val colorIndex = if (groupName.isEmpty()) 0 else groupName.length % avatarColors.size
    val dynamicColor = avatarColors[colorIndex]

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp)
    ) {
        Surface(
            modifier = Modifier.size(100.dp),
            shape = RoundedCornerShape(28.dp), // Large Squircle
            color = dynamicColor.copy(alpha = 0.15f),
            border = BorderStroke(2.dp, dynamicColor.copy(alpha = 0.5f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = if (groupName.isEmpty()) "?" else groupName.take(1).uppercase(),
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black),
                    color = dynamicColor
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Group Preview",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}