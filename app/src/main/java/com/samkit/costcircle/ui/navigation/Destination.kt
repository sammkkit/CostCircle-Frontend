package com.samkit.costcircle.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Destination {
    @Serializable
    data object Login : Destination, NavKey
    @Serializable
    data object Register : Destination, NavKey
    @Serializable
    data object Groups : Destination, NavKey
}
