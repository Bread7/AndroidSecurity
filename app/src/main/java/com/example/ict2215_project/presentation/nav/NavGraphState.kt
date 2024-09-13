package com.example.ict2215_project.presentation.nav

sealed class NavGraphState {
    data object Loading : NavGraphState()
    data object Success : NavGraphState()
    data object Error : NavGraphState()
}