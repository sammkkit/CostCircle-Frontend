package com.samkit.costcircle.ui.screens.Statistics


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samkit.costcircle.data.group.dto.GroupStatsDto
import com.samkit.costcircle.data.group.repository.GroupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GroupStatsViewModel(
    private val repository: GroupRepository,
    private val groupId: Long
) : ViewModel() {

    private val _stats = MutableStateFlow<GroupStatsDto?>(null)
    val stats = _stats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _stats.value = repository.getGroupStats(groupId)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}