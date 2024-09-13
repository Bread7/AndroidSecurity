package com.example.ict2215_project.presentation.screen.channelList.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.getstream.chat.android.compose.ui.channels.header.ChannelListHeader
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.models.ConnectionState
import io.getstream.chat.android.models.User

@Composable
fun CustomChannelListHeader(
    modifier: Modifier = Modifier,
    user: User?,
    connectionState: ConnectionState,
    onJoinChannelClick: () -> Unit = {},
    onCreateChannelClick: () -> Unit = {},
    onAvatarClick: (User?) -> Unit = {}
) {
    ChannelListHeader(modifier = modifier.fillMaxWidth(),
        title = "My Awesome App",
        currentUser = user,
        connectionState = connectionState,
        onAvatarClick = onAvatarClick,
        trailingContent = {
            if (user?.role == "admin") {
                IconButton(
                    onClick = onCreateChannelClick
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create a new channel",
                        tint = ChatTheme.colors.textHighEmphasis
                    )
                }
            } else {
                IconButton(
                    onClick = onJoinChannelClick,
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Join a new channel",
                        tint = ChatTheme.colors.textHighEmphasis
                    )
                }
            }
        })
}