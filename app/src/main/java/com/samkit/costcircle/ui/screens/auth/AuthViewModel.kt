package com.samkit.costcircle.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samkit.costcircle.data.auth.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(email: String, password: String) {
        _uiState.value = AuthUiState(isLoading = true)

        viewModelScope.launch {
            try {
                authRepository.login(email, password)

                _uiState.value = AuthUiState(
                    isSuccess = true
                )

            } catch (e: Exception) {
                _uiState.value = AuthUiState(
                    errorMessage = e.message ?: "Something went wrong"
                )
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        if (_uiState.value.isLoading) return

        _uiState.value = AuthUiState(isLoading = true)

        viewModelScope.launch {
            try {
                authRepository.register(name, email, password)
                _uiState.value = AuthUiState(isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = AuthUiState(
                    errorMessage = e.message ?: "Something went wrong"
                )
            }
        }
    }


    fun resetState() {
        _uiState.value = AuthUiState()
    }
}
