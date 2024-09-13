package com.example.ict2215_project.presentation.screen.channelList.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ict2215_project.presentation.screen.channelList.AvailableChannelListState
import com.example.ict2215_project.presentation.screen.loading.LoadingScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme

@Composable
fun JoinChannelDialog(
    state: AvailableChannelListState, onJoinChannel: (String) -> Unit, onDismiss: () -> Unit
) {
    var selectedChannelId by remember { mutableStateOf<String?>(null) }

    when (state) {
        is AvailableChannelListState.Loading -> {
            LoadingScreen()
        }

        is AvailableChannelListState.Success -> {
            val channels = state.channels
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Join a Channel") },
                text = {
                    LazyColumn {
                        items(channels) { channel ->
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedChannelId = channel.id
                                }
                                .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedChannelId == channel.id,
                                    onClick = { selectedChannelId = channel.id }
                                )
                                Text(text = channel.name, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { selectedChannelId?.let { onJoinChannel(it) } },
                        enabled = selectedChannelId != null
                    ) {
                        Text("Join")
                    }
                },
                dismissButton = {
                    Button(onClick = onDismiss) {
                        Text("Cancel")
                    }
                }
            )
        }

        is AvailableChannelListState.Error -> {
            val message = state.message
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = message,
                    color = ChatTheme.colors.errorAccent,
                    style = ChatTheme.typography.body
                )
            }
        }

        else -> Unit
    }
}
