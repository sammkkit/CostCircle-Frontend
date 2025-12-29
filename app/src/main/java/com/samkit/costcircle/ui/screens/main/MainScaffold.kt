package com.samkit.costcircle.ui.screens.main

import androidx.compose.runtime.Composable
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import com.samkit.costcircle.ui.components.BottomBar
import com.samkit.costcircle.ui.navigation.Destination
import com.samkit.costcircle.ui.screens.account.AccountScreen
import com.samkit.costcircle.ui.screens.activity.GlobalActivityScreen
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
        }
//        floatingActionButton = {
//            AnimatedVisibility(
//                visible = selectedTab == Destination.Groups,
//                // Slide up/down looks more premium for FABs than scaling
//                enter = slideInVertically(initialOffsetY = { it * 2 }) + fadeIn(),
//                exit = slideOutVertically(targetOffsetY = { it * 2 }) + fadeOut()
//            ) {
//                ExtendedFloatingActionButton(
//                    onClick = onAddExpenseClick,
//                    containerColor = MaterialTheme.colorScheme.primary,
//                    contentColor = MaterialTheme.colorScheme.onPrimary,
//                    elevation = FloatingActionButtonDefaults.elevation(
//                        defaultElevation = 6.dp,
//                        pressedElevation = 12.dp
//                    ),
//                    icon = {
//                        // "PostAdd" looks like a document with a + sign (perfect for "Log Bill")
//                        Icon(
//                            imageVector = Icons.Default.PostAdd,
//                            contentDescription = "Log Bill",
//                            modifier = Modifier.size(24.dp)
//                        )
//                    },
//                    text = {
//                        Text(
//                            text = "Add Expense",
//                            style = MaterialTheme.typography.titleSmall.copy(
//                                fontWeight = FontWeight.Bold,
//                                letterSpacing = 0.5.sp
//                            )
//                        )
//                    }
//                )
//            }
//        }

    ) {paddingValues ->
        when (selectedTab) {
            Destination.Groups -> GroupsScreen(
                paddingMain = paddingValues,
                onGroupClick = onGroupClick,
                onAddGroupClick = onAddGroupClick,
                onAddExpenseClick = onAddExpenseClick
            )
            Destination.Account -> AccountScreen(onLogout = onLogout)
            Destination.Activity -> GlobalActivityScreen(
                paddingMain =   paddingValues
            )
            else -> Unit
        }
    }
}

