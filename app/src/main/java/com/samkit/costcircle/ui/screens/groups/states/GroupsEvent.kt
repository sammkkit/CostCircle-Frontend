package com.samkit.costcircle.ui.screens.groups.states

sealed interface GroupsEvent {
    data object LoadGroups : GroupsEvent
    data object Retry : GroupsEvent
    data class GroupClicked(val groupId: Long, val name: String) : GroupsEvent
}
