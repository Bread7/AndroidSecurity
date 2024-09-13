package com.example.ict2215_project.presentation.screen.message

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ict2215_project.utils.LocationManager
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.models.Filters
import io.getstream.chat.android.models.Member
import io.getstream.chat.android.models.querysort.QuerySortByField
import io.getstream.chat.android.models.querysort.QuerySortByField.Companion.descByName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChannelUsersViewModel @Inject constructor(private val client: ChatClient, private val locationManager: LocationManager) : ViewModel() {
    private val _channelUsersState = MutableStateFlow<ChannelUsersState>(ChannelUsersState.Idle)
    val channelUsersState: StateFlow<ChannelUsersState> = _channelUsersState.asStateFlow()

    fun getChannelUsers(channelId: String) {
        viewModelScope.launch {
            _channelUsersState.value = ChannelUsersState.Loading
            try {
                val result = client.channel(channelId).queryMembers(
                    offset = 0,
                    limit = 100, // Consider setting a reasonable limit
                    filter = Filters.neutral(),
                    sort = QuerySortByField<Member>().descByName("id")
                ).enqueue{ result ->
                    if (result.isSuccess) {
                        val members = result.getOrThrow()
                        _channelUsersState.value = ChannelUsersState.Success(members)
                    } else {
                        _channelUsersState.value = ChannelUsersState.Error(result.errorOrNull()?.message ?: "Unknown error")
                    }
                }
            } catch (e: Exception) {
                _channelUsersState.value =
                    ChannelUsersState.Error(e.message ?: "Error fetching channel members")
            }
        }
    }

    fun getLatestLocation(): LatLng {
        val location = locationManager.getLatestLocation()
        return if (location != null) {
            LatLng(location.latitude, location.longitude)
        } else {
            Log.d("ChannelUsersViewModel", "Location not available")
            LatLng(0.0, 0.0)
        }
    }

}