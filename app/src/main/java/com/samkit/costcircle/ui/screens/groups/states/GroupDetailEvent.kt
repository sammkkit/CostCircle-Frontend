package com.samkit.costcircle.ui.screens.groups.states

sealed interface GroupDetailsEvent {
    data object Load : GroupDetailsEvent
    data object Retry : GroupDetailsEvent
}
