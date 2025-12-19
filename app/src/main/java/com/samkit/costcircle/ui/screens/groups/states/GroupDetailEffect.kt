package com.samkit.costcircle.ui.screens.groups.states

sealed interface GroupDetailsEffect {
    data object ShowError : GroupDetailsEffect
}
