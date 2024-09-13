package com.example.ict2215_project.presentation.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ict2215_project.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val client: ChatClient, private val authRepository: AuthRepository
) : ViewModel() {
    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    init {
        getProfile()
    }

    private fun getProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            val user = client.getCurrentUser()
            if (user != null) {
                _profileState.value = ProfileState.Success(user)
            } else {
                _profileState.value = ProfileState.Error("No user available")
            }
        }
    }

    fun updateProfile(name: String) {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            val user = client.getCurrentUser()
            if (user != null) {
                val updatedUser = user.copy(name = name)
                client.updateUsers(listOf(updatedUser)).enqueue { result ->
                    if (result.isSuccess) {
                        _profileState.value = ProfileState.Success(updatedUser)
                    } else {
                        val errorMessage = result.errorOrNull()?.message ?: "Failed to update user"
                        _profileState.value = ProfileState.Error(errorMessage)
                    }
                }
            } else {
                _profileState.value = ProfileState.Error("No user available")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            authRepository.logoutUser()
            client.disconnect(true).enqueue { result ->
                if (result.isSuccess) {
                    _profileState.value = ProfileState.Idle
                } else {
                    val errorMessage = result.errorOrNull()?.message ?: "Failed to logout"
                    _profileState.value = ProfileState.Error(errorMessage)
                }
            }
            _profileState.value = ProfileState.Idle
        }
    }
}