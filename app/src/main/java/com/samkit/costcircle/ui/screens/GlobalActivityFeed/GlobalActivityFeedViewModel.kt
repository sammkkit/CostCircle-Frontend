package com.samkit.costcircle.ui.screens.GlobalActivityFeed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samkit.costcircle.data.auth.session.SessionManager
import com.samkit.costcircle.data.group.dto.TransactionDto
import com.samkit.costcircle.data.group.repository.GroupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GlobalActivityViewModel(
    private val repository: GroupRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _transactions = MutableStateFlow<List<TransactionDto>>(emptyList())
    val transactions = _transactions.asStateFlow()

    val currentUserId = sessionManager.getUserId() ?: 0L

    init {
        loadActivity()
    }

    fun loadActivity() {
        viewModelScope.launch {
            _isLoading.value = true
            runCatching {
                repository.getUserActivity()
            }.onSuccess { list ->
                _transactions.value = list
                _isLoading.value = false
            }.onFailure {
                _isLoading.value = false
                // Handle error (optional toast)
            }
        }
    }
}