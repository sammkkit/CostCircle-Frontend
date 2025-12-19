package com.samkit.costcircle.ui.screens.groups.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun GroupDetailsLoadingState() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Header skeleton
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(MaterialTheme.shapes.large)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    )
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Expense skeletons
        items(5) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(vertical = 8.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    )
            )
        }
    }
}
