package com.samkit.costcircle.ui.screens.groups.states

sealed interface GroupsEffect {
    data class NavigateToGroup(
        val groupId: Int,
        val name: String
    ) : GroupsEffect

    data class CreateToastWhenGroupClicked(val message : String) : GroupsEffect
}
