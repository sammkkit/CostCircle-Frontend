package com.samkit.costcircle.ui.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.samkit.costcircle.data.auth.session.SessionManager
import com.samkit.costcircle.ui.screens.addExpense.AddExpenseScreen
import com.samkit.costcircle.ui.screens.auth.AuthViewModel
import com.samkit.costcircle.ui.screens.auth.LoginScreen
import com.samkit.costcircle.ui.screens.auth.RegisterScreen
import com.samkit.costcircle.ui.screens.groupdetails.GroupDetailsScreen
import com.samkit.costcircle.ui.screens.main.MainScaffold
import com.samkit.costcircle.ui.screens.newGroupAddition.NewGroupAdditionScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    sessionManager: SessionManager = org.koin.androidx.compose.get()

) {
    val startDestination = remember {
        if (sessionManager.isLoggedIn()) {
            Destination.Main
        } else {
            Destination.Login
        }
    }

    val backstack = rememberNavBackStack(startDestination)

    val authViewModel : AuthViewModel = koinViewModel()
    NavDisplay(
        backStack = backstack,
        onBack = {backstack.removeLastOrNull()},
        entryProvider = entryProvider {
            entry<Destination.Login>{key->
                LoginScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = {
                        backstack.clear()
                        backstack.add(Destination.Main)
//                        backstack.replaceAll(Destination.Main) NOT WORKING THIS WAY
                    },
                    onRegisterClick ={
                        backstack.add(Destination.Register)
                    }
                )
            }
            entry<Destination.Register>{key->
                RegisterScreen(
                    viewModel = authViewModel,
                    onRegisterSuccess = {
                        backstack.add(Destination.Login)
                    },
                    onBackToLogin = {
                        backstack.removeLastOrNull()
                    }
                )
            }
            entry<Destination.Main>{key->
                MainScaffold(
                    onLogout = {
                        sessionManager.clear()
                        backstack.clear()
                        backstack.add(Destination.Login)
                    },
                    onAddExpenseClick = {
                        backstack.add(Destination.AddExpense)
                    },
                    onGroupClick = { groupId, groupName ->
                        backstack.add(
                            Destination.GroupDetails(
                                groupId = groupId,
                                groupName = groupName
                            )
                        )
                    },
                    onAddGroupClick = {
                        backstack.add(Destination.AddNewGroup)
                    }
                )
            }
            entry<Destination.GroupDetails> { key ->
                GroupDetailsScreen(
                    groupName = key.groupName,
                    groupId = key.groupId,
                    onBack = {
                        backstack.removeLastOrNull()
                    }
                )
            }

            entry<Destination.AddExpense> {
                AddExpenseScreen(
                    onClose = {
                        backstack.removeLastOrNull()
                    }
                )
            }
            entry<Destination.AddNewGroup>{
                NewGroupAdditionScreen(
                    onBack = {
                        backstack.removeLastOrNull()
                    }
                )
            }
        },
        transitionSpec = {
            slideInHorizontally(
                initialOffsetX = { it }
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { -it }
            )
        },
        popTransitionSpec = {
            slideInHorizontally(
                initialOffsetX = { it }
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { -it }
            )
        },
        predictivePopTransitionSpec = {
            slideInHorizontally(
                initialOffsetX = { it }
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { -it }
            )
        }
    )
}