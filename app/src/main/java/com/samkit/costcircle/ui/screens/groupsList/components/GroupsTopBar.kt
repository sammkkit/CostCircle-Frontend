package com.samkit.costcircle.ui.screens.groupsList.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.samkit.costcircle.ui.screens.groupsList.states.GroupsContract
import com.yourapp.costcircle.ui.theme.GreenOwed
import com.yourapp.costcircle.ui.theme.OrangeOwe
import kotlin.math.pow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsTopBar(
    state: GroupsContract.State,
    scrollOffset: Float,
    onEvent: (GroupsContract.Event) -> Unit
) {
    val successState = state as? GroupsContract.State.Success
    val isSearchActive = successState?.isSearchActive ?: false

    val netBalance = (successState?.totalOwedToYou ?: 0.0) - (successState?.totalYouOwe ?: 0.0)
    val statusColor = if (netBalance >= 0) GreenOwed else OrangeOwe

    // Enhanced smooth color transition with exponential easing
    val backgroundAlpha by animateFloatAsState(
        targetValue = if (scrollOffset > 0.4f) {
            // Exponential curve for smoother appearance
            ((scrollOffset - 0.4f) / 0.6f).pow(1.5f).coerceIn(0f, 0.25f)
        } else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "bgAlpha"
    )


    // Gradient that intensifies on scroll
    val gradientColors = listOf(
        statusColor.copy(alpha = backgroundAlpha * 0.8f),
        statusColor.copy(alpha = backgroundAlpha * 0.4f),
        Color.Transparent
    )

    // Title scale animation on scroll
    val titleScale by animateFloatAsState(
        targetValue = if (scrollOffset > 0.3f) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy, // More "elastic" feel
            stiffness = Spring.StiffnessLow
        ),
        label = "titleScale"
    )

    // Shimmer effect when scrolling
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer{
                translationY = scrollOffset * -10f
            }
            .background(
                Brush.verticalGradient(
                    colors = gradientColors,
                    startY = 0f,
                    endY = 400f
                )
            )
    ) {
        // Subtle shimmer overlay on scroll
        if (scrollOffset > 0.5f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                statusColor.copy(alpha = 0.3f * shimmerAlpha),
                                Color.Transparent
                            ),
                            startX = -200f + shimmerAlpha * 600f,
                            endX = shimmerAlpha * 600f
                        )
                    )
            )
        }

        TopAppBar(
            title = {
                AnimatedContent(
                    targetState = isSearchActive,
                    transitionSpec = {
                        if (targetState) {
                            // Search activating
                            (slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                )
                            ) + fadeIn(tween(200))).togetherWith(
                                slideOutHorizontally(
                                    targetOffsetX = { -it / 2 },
                                    animationSpec = tween(200)
                                ) + fadeOut(tween(150))
                            )
                        } else {
                            // Search deactivating
                            (slideInHorizontally(
                                initialOffsetX = { -it / 2 },
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                )
                            ) + fadeIn(tween(250))).togetherWith(
                                slideOutHorizontally(
                                    targetOffsetX = { it },
                                    animationSpec = tween(200)
                                ) + fadeOut(tween(150))
                            )
                        }
                    },
                    label = "titleContent"
                ) { active ->
                    if (active) {
                        TextField(
                            value = successState?.searchQuery ?: "",
                            onValueChange = {
                                onEvent(GroupsContract.Event.SearchQueryChanged(it))
                            },
                            placeholder = {
                                Text(
                                    "Search groups...",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 8.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = statusColor
                            ),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        Text(
                            text = "CostCircle",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Black
                            ),
                            modifier = Modifier.graphicsLayer {
                                scaleX = titleScale
                                scaleY = titleScale
                            }
                        )
                    }
                }
            },
            actions = {
                // Animated icon buttons with press feedback
                IconButton(
                    onClick = { onEvent(GroupsContract.Event.ToggleSearch) },
                    modifier = Modifier.graphicsLayer {
                        // Subtle rotation on search toggle
                        rotationZ = if (isSearchActive) 90f else 0f
                    }
                ) {
                    Icon(
                        imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                        contentDescription = if (isSearchActive) "Close search" else "Search",
                        tint = if (scrollOffset > 0.5f) {
                            statusColor.copy(alpha = 0.9f)
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }

                IconButton(
                    onClick = { onEvent(GroupsContract.Event.CreateGroupClicked) }
                ) {
                    Icon(
                        imageVector = Icons.Default.GroupAdd,
                        contentDescription = "Add group",
                        tint = if (scrollOffset > 0.5f) {
                            statusColor.copy(alpha = 0.9f)
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent
            ),
            modifier = Modifier.graphicsLayer {
                // Subtle elevation effect on scroll
                shadowElevation = scrollOffset * 8f
            }
        )
    }
}