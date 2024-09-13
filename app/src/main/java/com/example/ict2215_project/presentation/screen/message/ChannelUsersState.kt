package com.example.ict2215_project.presentation.screen.message

import io.getstream.chat.android.models.Member

sealed class ChannelUsersState {
    data object Idle : ChannelUsersState()
    data object Loading : ChannelUsersState()
    data class Success(val users: List<Member>) : ChannelUsersState()
    data class Error(val message: String) : ChannelUsersState()
}
