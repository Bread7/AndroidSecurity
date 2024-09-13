package com.example.ict2215_project.presentation.nav

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
class NavGraphViewModel @Inject constructor(
    private val client: ChatClient,
    private val repository: IStreamChatServerRepository,
    private val authRepository: IAuthRepository
) : ViewModel() {
    private val _navGraphState = MutableStateFlow<NavGraphState>(NavGraphState.Loading)
    val navGraphState: StateFlow<NavGraphState> = _navGraphState.asStateFlow()

    init {
        checkNavGraphState()
    }

    private fun checkNavGraphState() {
        viewModelScope.launch {
            if (authRepository.hasUser()) {
                try {
                    val userId = authRepository.getUserId()
                    val user = User(id = userId)
                    val token = repository.createToken(userId)
                    client.connectUser(user, token).enqueue { result ->
                        if (result.isSuccess) {
                            _navGraphState.value = NavGraphState.Success
                            Log.d("NavGraphViewModel", "Connected to the server")
                        } else {
                            _navGraphState.value = NavGraphState.Error
                            Log.e("NavGraphViewModel", "Failed to connect to the server")
                        }
                    }
                } catch (e: Exception) {
                    _navGraphState.value = NavGraphState.Error
                    Log.e("NavGraphViewModel", "Exception during login", e)
                }
            } else {
                _navGraphState.value = NavGraphState.Error // Consider using a more specific error
            }
        }
    }
}