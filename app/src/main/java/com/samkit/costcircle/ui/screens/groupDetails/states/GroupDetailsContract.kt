package com.samkit.costcircle.ui.screens.groupdetails

import com.samkit.costcircle.data.auth.dto.UserDto
import com.samkit.costcircle.data.group.dto.SettlementEntryDto
import com.samkit.costcircle.data.group.dto.TransactionDto // You will need to create this DTO

object GroupDetailsContract {

    sealed interface State {
        data object Loading : State
        data object Empty : State
        data class Error(val message: String) : State

        // Updated Success state to hold both datasets and the current tab
        data class Success(
            val settlements: List<SettlementEntryDto> = emptyList(),
            val transactions: List<TransactionDto> = emptyList(),
            val selectedTab: Int = 0,
            val members: List<UserDto> = emptyList(),
            val isAddingMembers: Boolean = false ,// NEW: Specifically for the "Invite" button loader
            val isSettling: Boolean = false
        ) : State
    }

    sealed interface Event {
        data object Load : Event
        data object Retry : Event
        data object BackClicked : Event
        data object MemberListClicked : Event
        data object AddExpenseClicked: Event
        // NEW: Handle tab switching in the UI
        data class TabSelected(val index: Int) : Event

        // NEW: Handle adding members from the details screen
        data class AddMembers(val emails: List<String>) : Event
        data class SettleUpClicked(val settlement: SettlementEntryDto) : Event
        data object OnResume : Event
    }

    sealed interface Effect {
        data object NavigateBack : Effect
        data object OpenMembersSheet : Effect
        data object AddExpenseNavigate : Effect
        // NEW: Provide feedback for member addition
        data class ShowToast(val message: String) : Effect
    }
}