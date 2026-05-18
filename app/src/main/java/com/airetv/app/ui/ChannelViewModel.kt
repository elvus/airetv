package com.airetv.app.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airetv.app.data.model.ChannelList
import com.airetv.app.data.repository.ChannelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChannelViewModel : ViewModel() {

    private val _channels = MutableStateFlow(ChannelList(emptyList()))
    val channels: StateFlow<ChannelList> = _channels.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadChannels()
    }

    fun loadChannels() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _channels.value = ChannelRepository.getChannels()
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading channels"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshChannels() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _channels.value = ChannelRepository.refreshChannels()
            } catch (e: Exception) {
                _error.value = e.message ?: "Error refreshing channels"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
