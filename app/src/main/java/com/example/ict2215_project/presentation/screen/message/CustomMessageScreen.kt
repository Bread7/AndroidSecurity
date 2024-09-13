package com.example.ict2215_project.presentation.screen.message

import android.content.Context
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.LatLng
import io.getstream.chat.android.compose.ui.messages.MessagesScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.viewmodel.messages.MessagesViewModelFactory
import io.getstream.chat.android.models.User

@Composable
fun CustomMessageScreen(
    channelId: String, onBackPressed: () -> Unit, viewModel: ChannelUsersViewModel = hiltViewModel(),
) {
    var showChannelAvatarDialog by remember { mutableStateOf(false) }

    if (showChannelAvatarDialog) {
        UserListDialog(
            viewModel = viewModel,
            channelId = channelId,
            onDismiss = { showChannelAvatarDialog = false })
    }

    CustomMessagesScreen(
        location = viewModel.getLatestLocation(),
        channelId=channelId,
        viewModelFactory = MessagesViewModelFactory(
            context = LocalContext.current, channelId = channelId
        ),
        onBackPressed = onBackPressed,
        onChannelAvatarClick = {
            viewModel.getChannelUsers(channelId)
            showChannelAvatarDialog = true
        },
    )
}

@Composable
fun UserListDialog(viewModel: ChannelUsersViewModel, channelId: String, onDismiss: () -> Unit) {
    when (val state = viewModel.channelUsersState.collectAsState().value) {
        is ChannelUsersState.Loading -> {
            // Show loading indicator
            CircularProgressIndicator()
        }

        is ChannelUsersState.Success -> {
            AlertDialog(onDismissRequest = onDismiss, title = { Text("Channel Users") }, text = {
                LazyColumn {
                    items(state.users) { user ->
                        Text(text = user.user.name) // Adjust according to your data model
                    }
                }
            }, confirmButton = {
                Button(onClick = onDismiss) {
                    Text("Close")
                }
            })
        }

        is ChannelUsersState.Error -> {
            // Show error message
            Text("Error: ${state.message}")
        }

        else -> Unit // Idle state or any other state not expected to result in UI changes
    }
}
