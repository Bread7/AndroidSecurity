package com.example.ict2215_project.presentation.screen.signUp

import io.getstream.chat.android.models.User

sealed class SignUpState {
    data object Idle : SignUpState()
    data object Loading : SignUpState()
    data object Success : SignUpState()
    data class Error(val error: Throwable) : SignUpState()
}