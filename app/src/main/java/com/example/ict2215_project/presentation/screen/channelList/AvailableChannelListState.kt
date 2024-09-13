package com.example.ict2215_project.presentation.screen.channelList

import com.example.ict2215_project.domain.model.ChannelDetail

sealed class AvailableChannelListState {
    data object Idle : AvailableChannelListState()
    data object Loading : AvailableChannelListState()
    data class Success(val channels: List<ChannelDetail>) : AvailableChannelListState()
    data class Error(val message: String) : AvailableChannelListState()
}