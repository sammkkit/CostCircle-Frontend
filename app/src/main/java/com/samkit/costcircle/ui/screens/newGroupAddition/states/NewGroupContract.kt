object NewGroupContract {

    data class State(
        val groupName: String = "",
        val isLoading: Boolean = false,
        val isCreateEnabled: Boolean = false,
        val error: String? = null
    )

    sealed interface Event {
        data class NameChanged(val name: String) : Event
        object CreateClicked : Event
        object BackClicked : Event
    }

    sealed interface Effect {
        object NavigateBack : Effect
        object GroupCreated : Effect
        data class ShowError(val message: String) : Effect
    }
}
