package com.samkit.costcircle.ui.screens.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AccountScreen(modifier: Modifier = Modifier, onLogout: () -> Unit) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Account Screen")
        Button(
            onClick = {
                onLogout()
            },
            modifier = modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Logout")
        }
    }
}