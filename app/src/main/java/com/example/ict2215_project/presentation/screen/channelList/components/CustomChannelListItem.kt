package com.example.ict2215_project.presentation.screen.channelList.components

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.getstream.chat.android.client.extensions.currentUserUnreadCount
import io.getstream.chat.android.compose.state.channels.list.ChannelItemState
import io.getstream.chat.android.compose.ui.channels.list.ChannelItem
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.User

@Composable
fun CustomChannelListItem(
    channelItem: ChannelItemState, user: User?, onChannelClick: (Channel) -> Unit
) {
    ChannelItem(channelItem = channelItem,
        currentUser = user,
        onChannelLongClick = {},
        onChannelClick = onChannelClick,
        trailingContent = {
            if (channelItem.channel.currentUserUnreadCount > 0) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxHeight() // This makes the Surface fill the maximum size available
                        .padding(5.dp) // Optional: if you want to ensure some spacing from the parent's edges
                ) {
                    Text(
                        text = it.channel.currentUserUnreadCount.toString(),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(10.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        })
}