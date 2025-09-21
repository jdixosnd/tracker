package com.babyfeed.tracker.ui.screens.childprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babyfeed.tracker.data.local.Child
import com.babyfeed.tracker.data.repository.ChildRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChildProfileViewModel @Inject constructor(
    private val childRepository: ChildRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val children: StateFlow<List<Child>> = childRepository.getAllChildren()
        .onEach { _isLoading.value = false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getChild(id: Int): Flow<Child?> {
        if (id == -1) {
            return flowOf(null)
        }
        return childRepository.getChild(id)
    }

    fun addChild(child: Child) {
        viewModelScope.launch {
            childRepository.insertChild(child)
        }
    }

    fun updateChild(child: Child) {
        viewModelScope.launch {
            childRepository.updateChild(child)
        }
    }

    fun deleteChild(child: Child) {
        viewModelScope.launch {
            childRepository.deleteChild(child)
        }
    }
}
