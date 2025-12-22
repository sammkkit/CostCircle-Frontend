package com.samkit.costcircle.ui.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
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
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                FloatingActionButton(
                    onClick = onAddExpenseClick,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add expense")
                }
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

