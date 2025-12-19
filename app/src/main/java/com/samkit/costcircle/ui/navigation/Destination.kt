package com.samkit.costcircle.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Destination {

    //Auth
    @Serializable
    data object Login : Destination, NavKey
    @Serializable
    data object Register : Destination, NavKey

    // One-off screens
    @Serializable data object AddExpense : Destination, NavKey
    @Serializable
    data class GroupDetails(val groupId: Int, val groupName: String) : Destination,NavKey

    // Main
    @Serializable data object Main : Destination,NavKey

    // Bottom bar tabs
    @Serializable data object Groups : Destination,NavKey
    @Serializable data object Activity : Destination,NavKey // future
    @Serializable data object Account : Destination,NavKey
}
