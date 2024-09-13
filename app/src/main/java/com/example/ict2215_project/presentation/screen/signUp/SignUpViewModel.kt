package com.example.ict2215_project.presentation.screen.signUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ict2215_project.data.repository.AuthRepository
import com.example.ict2215_project.data.repository.StreamChatServerRepository
import com.example.ict2215_project.domain.repository.IAuthRepository
import com.example.ict2215_project.domain.repository.IStreamChatServerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val streamChatServerRepository: IStreamChatServerRepository
) : ViewModel() {
    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val signUpState: StateFlow<SignUpState> = _signUpState.asStateFlow()

    fun signUp(username: String, email: String, password: String, role: String) {
        _signUpState.value = SignUpState.Loading
        viewModelScope.launch {
            var signUpSuccess = false
            authRepository.createUser(email, password) { success ->
                signUpSuccess = success
            }
            if (signUpSuccess) {
                try {
                    val userId = authRepository.getUserId()
                    val success =
                        streamChatServerRepository.upsertUser(userId, username, email, role)
                    if (success) {
                        _signUpState.value = SignUpState.Success
                    } else {
                        _signUpState.value = SignUpState.Error(Exception("Error creating user"))
                    }
                } catch (e: Exception) {
                    _signUpState.value = SignUpState.Error(e)
                }
            } else {
                _signUpState.value = SignUpState.Error(Exception("Error creating user"))
            }
        }
    }
}