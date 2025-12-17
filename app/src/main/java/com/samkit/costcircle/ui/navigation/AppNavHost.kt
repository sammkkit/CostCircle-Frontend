package com.samkit.costcircle.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.samkit.costcircle.data.auth.session.SessionManager
import com.samkit.costcircle.ui.screens.auth.AuthViewModel
import com.samkit.costcircle.ui.screens.auth.LoginScreen
import com.samkit.costcircle.ui.screens.auth.RegisterScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    sessionManager: SessionManager = org.koin.androidx.compose.get()

) {
    val startDestination = remember {
        if (sessionManager.isLoggedIn()) {
            Destination.Groups
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
                        backstack.add(Destination.Groups)
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
            entry<Destination.Groups>{key->
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Group Screen",
                        style = MaterialTheme.typography.titleLarge)

                }
            }
        }
    )
}