package com.example.ict2215_project.presentation.screen.login

import io.getstream.chat.android.models.User

sealed class LoginState {
    data object Idle : LoginState()
    data object Loading : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val error: Throwable) : LoginState()
}
