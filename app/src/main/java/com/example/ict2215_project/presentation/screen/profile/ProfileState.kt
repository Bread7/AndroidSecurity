package com.example.ict2215_project.presentation.screen.profile

import io.getstream.chat.android.models.User

sealed class ProfileState {
    data object Idle : ProfileState()
    data object Loading : ProfileState()
    data class Success(val user: User) : ProfileState()
    data class Error(val error: String) : ProfileState()
}