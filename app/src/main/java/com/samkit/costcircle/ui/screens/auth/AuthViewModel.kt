package com.samkit.costcircle.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samkit.costcircle.data.auth.repository.AuthRepository
import com.samkit.costcircle.data.auth.session.SessionManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager // ✅ Add this dependency
) : ViewModel() {

    // 1. STATE (UI Data)
    private val _state = MutableStateFlow(LoginContract.State())
    val state = _state.asStateFlow()

    // 2. EFFECT (Navigation / One-time events)
    private val _effect = Channel<LoginContract.Effect>()
    val effect = _effect.receiveAsFlow()

    // 3. EVENT HANDLER
    fun onEvent(event: LoginContract.Event) {
        when (event) {
            is LoginContract.Event.GoogleLogin -> handleGoogleLogin(event.token)
        }
    }

    private fun handleGoogleLogin(token: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = authRepository.googleLogin(token)

            result.onSuccess { response ->
                // ✅ Save the full profile to SharedPreferences
                sessionManager.saveUserSession(
                    token = response.token,
                    userId = response.user.id.toLong(), // Ensure type match (Int -> Long)
                    name = response.user.name,
                    email = response.user.email,
                    picture = response.user.picture
                )

                _state.update { it.copy(isLoading = false) }
                _effect.send(LoginContract.Effect.NavigateToHome)

            }.onFailure { error ->
                _state.update { it.copy(isLoading = false) }
                _effect.send(LoginContract.Effect.ShowError(error.message ?: "Google Login failed"))
            }
        }
    }
}