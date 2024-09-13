package com.example.ict2215_project.presentation.screen.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class LoginViewModel @Inject constructor(
    private val client: ChatClient,
    private val repository: IStreamChatServerRepository,
    private val authRepository: IAuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun login(email: String, password: String) {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            var loginSuccess = false
            authRepository.loginUser(email, password) { success ->
                loginSuccess = success
            }

            if (loginSuccess) {
                try {
                    val userId = authRepository.getUserId()
                    val user = User(id = userId)
                    val token = repository.createToken(userId)
                    client.connectUser(user, token).enqueue { result ->
                        if (result.isSuccess) {
                            _loginState.value = LoginState.Success(user)
                            Log.d("LoginViewModel", "Connected to the server")
                            Log.d("LoginViewModel", "User: ${client.getCurrentUser()}")
                        } else {
                            _loginState.value =
                                LoginState.Error(Exception("Failed to connect to the server"))
                            Log.e("LoginViewModel", "Failed to connect to the server")
                        }
                    }
                } catch (e: Exception) {
                    _loginState.value = LoginState.Error(e)
                }
            } else {
                _loginState.value = LoginState.Error(Exception("Failed to login"))
            }
        }
    }

    fun testLogin() {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            var loginSuccess = false
            authRepository.loginUser("123@gmail.com", "123456") { success ->
                loginSuccess = success
            }

            if (loginSuccess) {
                try {
                    val userId = authRepository.getUserId()
                    val user = User(id = userId)
                    val token =
                        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiOFh3SDZHR3NERU5YbTJtVG1GVm5YTHRHYXUxMiJ9.PDLTKe0RnQ5P4elwKwODFFrXNsamZ0Oe1tIeNEoWqBk"
                    client.connectUser(user, token).enqueue { result ->
                        if (result.isSuccess) {
                            _loginState.value = LoginState.Success(user)
                            Log.d("LoginViewModel", "Connected to the server")
                            Log.d("LoginViewModel", "User: ${client.getCurrentUser()}")
                        } else {
                            _loginState.value =
                                LoginState.Error(Exception("Failed to connect to the server"))
                            Log.e("LoginViewModel", "Failed to connect to the server")
                        }
                    }
                } catch (e: Exception) {
                    _loginState.value = LoginState.Error(e)
                }
            } else {
                _loginState.value = LoginState.Error(Exception("Failed to login"))
            }
        }
    }
}
