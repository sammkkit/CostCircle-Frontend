object NewGroupContract {

    data class State(
        val groupName: String = "",
        val members: List<String> = emptyList(), // NEW: List of emails to invite
        val isLoading: Boolean = false,
        val isCreateEnabled: Boolean = false,
        val error: String? = null
    )

    sealed interface Event {
        data class NameChanged(val name: String) : Event
        data class MemberAdded(val email: String) : Event // NEW: Add to list
        data class MemberRemoved(val email: String) : Event // NEW: Remove from list
        object CreateClicked : Event
        object BackClicked : Event
        data object Reset : Event
    }

    sealed interface Effect {
        object NavigateBack : Effect
        data class GroupCreated(val groupId: Long) : Effect // NEW: Pass ID to navigate to details
        data class ShowError(val message: String) : Effect
    }
}
