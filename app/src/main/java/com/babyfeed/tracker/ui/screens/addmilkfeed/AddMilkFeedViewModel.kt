package com.babyfeed.tracker.ui.screens.addmilkfeed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babyfeed.tracker.data.local.MilkFeed
import com.babyfeed.tracker.data.repository.MilkFeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMilkFeedViewModel @Inject constructor(
    private val milkFeedRepository: MilkFeedRepository
) : ViewModel() {

    fun addMilkFeed(milkFeed: MilkFeed) {
        viewModelScope.launch {
            milkFeedRepository.insertFeed(milkFeed)
        }
    }
}
