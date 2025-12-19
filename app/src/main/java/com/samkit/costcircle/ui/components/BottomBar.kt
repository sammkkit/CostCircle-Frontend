package com.samkit.costcircle.ui.components


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.samkit.costcircle.ui.navigation.Destination

@Composable
fun BottomBar(
    selected: Destination,
    onSelect: (Destination) -> Unit
) {
    NavigationBar {

        NavigationBarItem(
            selected = selected == Destination.Groups,
            onClick = { onSelect(Destination.Groups) },
            icon = {
                Icon(Icons.Outlined.Home, contentDescription = "Groups")
            },
            label = { Text("Groups") }
        )

        NavigationBarItem(
            selected = selected == Destination.Account,
            onClick = { onSelect(Destination.Account) },
            icon = {
                Icon(Icons.Outlined.Face, contentDescription = "Account")
            },
            label = { Text("Account") }
        )
    }
}
