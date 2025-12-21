package com.samkit.costcircle.ui.screens.groupdetails

import com.samkit.costcircle.data.group.dto.SettlementEntryDto

object GroupDetailsContract {

    sealed interface State {
        data object Loading : State
        data object Empty : State
        data class Error(val message: String) : State
        data class Success(
            val settlements: List<SettlementEntryDto>
        ) : State
    }

    sealed interface Event {
        data object Load : Event
        data object Retry : Event
        data object BackClicked : Event
    }

    sealed interface Effect {
        data object NavigateBack : Effect
    }
}
