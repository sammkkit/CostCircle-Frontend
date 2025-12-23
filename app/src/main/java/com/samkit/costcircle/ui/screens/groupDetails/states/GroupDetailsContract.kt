package com.samkit.costcircle.ui.screens.groupdetails

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
            val isAddingMembers: Boolean = false // NEW: Specifically for the "Invite" button loader
        ) : State
    }

    sealed interface Event {
        data object Load : Event
        data object Retry : Event
        data object BackClicked : Event

        // NEW: Handle tab switching in the UI
        data class TabSelected(val index: Int) : Event

        // NEW: Handle adding members from the details screen
        data class AddMembers(val emails: List<String>) : Event
    }

    sealed interface Effect {
        data object NavigateBack : Effect

        // NEW: Provide feedback for member addition
        data class ShowToast(val message: String) : Effect
    }
}