package com.samkit.costcircle.ui.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samkit.costcircle.ui.components.BottomBar
import com.samkit.costcircle.ui.navigation.Destination
import com.samkit.costcircle.ui.screens.account.AccountScreen
import com.samkit.costcircle.ui.screens.groupsList.GroupsScreen

@Composable
fun MainScaffold(
    onLogout: () -> Unit,
    onAddExpenseClick: () -> Unit,
    onGroupClick: (Long, String) -> Unit,
    onAddGroupClick: () -> Unit

) {
    var selectedTab by remember {
        mutableStateOf<Destination>(Destination.Groups)
    }

    Scaffold(
        bottomBar = {
            BottomBar(
                selected = selectedTab,
                onSelect = { selectedTab = it }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = selectedTab == Destination.Groups,
                // Slide up/down looks more premium for FABs than scaling
                enter = slideInVertically(initialOffsetY = { it * 2 }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it * 2 }) + fadeOut()
            ) {
                ExtendedFloatingActionButton(
                    onClick = onAddExpenseClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 12.dp
                    ),
                    icon = {
                        // "PostAdd" looks like a document with a + sign (perfect for "Log Bill")
                        Icon(
                            imageVector = Icons.Default.PostAdd,
                            contentDescription = "Log Bill",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    text = {
                        Text(
                            text = "Add Expense",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                )
            }
        }

    ) {paddingValues ->
        when (selectedTab) {
            Destination.Groups -> GroupsScreen(
                paddingMain = paddingValues,
                onGroupClick = onGroupClick,
                onAddGroupClick = onAddGroupClick
            )
            Destination.Account -> AccountScreen(onLogout = onLogout)
            else -> Unit
        }
    }
}

