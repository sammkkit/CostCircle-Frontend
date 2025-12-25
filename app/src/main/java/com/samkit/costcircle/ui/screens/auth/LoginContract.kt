package com.samkit.costcircle.ui.screens.auth


sealed interface LoginContract {

    // 1. STATE: Describes the UI at any moment
    data class State(
        val isLoading: Boolean = false
    )

    // 2. EVENTS: Actions the user performs
    sealed interface Event {
        data class GoogleLogin(val token: String) : Event
    }

    // 3. EFFECTS: One-time actions (Navigation, Toasts)
    sealed interface Effect {
        data object NavigateToHome : Effect
        data class ShowError(val message: String) : Effect
    }
}