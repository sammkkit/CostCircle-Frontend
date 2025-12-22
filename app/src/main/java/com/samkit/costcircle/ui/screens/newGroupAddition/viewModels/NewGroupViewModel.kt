package com.samkit.costcircle.ui.screens.newGroupAddition.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samkit.costcircle.data.group.repository.GroupRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class NewGroupViewModel(
    private val repository: GroupRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NewGroupContract.State())
    val state = _state.asStateFlow()

    private val _effect = Channel<NewGroupContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private val TAG = "GroupCreationFlow"

    // CRITICAL FIX: Thread-safe atomic flag to prevent race conditions
    private val isCreating = AtomicBoolean(false)

    fun onEvent(event: NewGroupContract.Event) {
        when (event) {
            is NewGroupContract.Event.NameChanged -> {
                // Don't allow name changes while creating
                if (isCreating.get()) return

                _state.value = _state.value.copy(
                    groupName = event.name,
                    isCreateEnabled = event.name.trim().isNotEmpty(),
                    error = null
                )
            }

            NewGroupContract.Event.CreateClicked -> {
                Log.d(TAG, "0. Event: CreateClicked received")

                // TRIPLE GUARD:
                // 1. Atomic check-and-set (prevents race condition)
                // 2. State validation
                // 3. Name validation
                if (!isCreating.compareAndSet(false, true)) {
                    Log.d(TAG, "!! BLOCKED: Already creating (atomic flag)")
                    return
                }

                if (_state.value.isLoading) {
                    Log.d(TAG, "!! BLOCKED: isLoading already true")
                    isCreating.set(false) // Reset flag
                    return
                }

                if (_state.value.groupName.trim().isEmpty()) {
                    Log.d(TAG, "!! BLOCKED: Empty name")
                    isCreating.set(false) // Reset flag
                    return
                }

                createGroup()
            }

            NewGroupContract.Event.BackClicked -> {
                // Allow back navigation even while creating (user choice)
                sendEffect(NewGroupContract.Effect.NavigateBack)
            }
        }
    }

    private fun createGroup() {
        val name = _state.value.groupName.trim()
        Log.d(TAG, "1. ViewModel: createGroup() called for name: $name")

        viewModelScope.launch {
            try {
                Log.d(TAG, "2. ViewModel: Setting isLoading = true")
                _state.value = _state.value.copy(
                    isLoading = true,
                    isCreateEnabled = false // Disable button
                )

                Log.d(TAG, "3. ViewModel: Calling repository.createGroup($name)")
                val response = repository.createGroup(name)

                Log.d(TAG, "6. ViewModel: SUCCESS! Received groupId: ${response.groupId}")
                _state.value = _state.value.copy(isLoading = false)
                sendEffect(NewGroupContract.Effect.GroupCreated)

            } catch (e: Exception) {
                Log.d(TAG, "6. ViewModel: FAILURE! Error: ${e.localizedMessage}")
                _state.value = _state.value.copy(
                    isLoading = false,
                    isCreateEnabled = name.isNotEmpty() // Re-enable if name is valid
                )
                sendEffect(NewGroupContract.Effect.ShowError(e.message ?: "Error creating group"))

            } finally {
                // CRITICAL: Always reset the atomic flag
                isCreating.set(false)
                Log.d(TAG, "7. ViewModel: Reset isCreating flag")
            }
        }
    }

    private fun sendEffect(effect: NewGroupContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}