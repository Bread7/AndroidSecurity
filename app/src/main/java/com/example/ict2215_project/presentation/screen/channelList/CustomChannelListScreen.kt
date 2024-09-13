package com.example.ict2215_project.presentation.screen.channelList

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ict2215_project.presentation.screen.channelList.components.CreateChannelDialog
import com.example.ict2215_project.presentation.screen.channelList.components.CustomChannelListHeader
import com.example.ict2215_project.presentation.screen.channelList.components.CustomChannelListItem
import com.example.ict2215_project.presentation.screen.channelList.components.EmptyContent
import com.example.ict2215_project.presentation.screen.channelList.components.JoinChannelDialog
import io.getstream.chat.android.compose.ui.channels.list.ChannelList
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.viewmodel.channels.ChannelListViewModel
import io.getstream.chat.android.compose.viewmodel.channels.ChannelViewModelFactory
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.Filters
import io.getstream.chat.android.models.User
import java.util.UUID

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.*

@Composable
fun CustomChannelListScreen(
    modifier: Modifier = Modifier,
    viewModel: ChannelListViewModel = viewModel(
        factory = ChannelViewModelFactory(),
    ),
    availableChannelListViewModel: AvailableChannelListViewModel = hiltViewModel(),
    onAvatarClick: (User?) -> Unit = {},
    onChannelClick: (Channel) -> Unit = {}
) {
    val user by viewModel.user.collectAsState()
    val connectionState by viewModel.connectionState.collectAsState()
    val availableChannelListState by availableChannelListViewModel.availableChannelListState.collectAsState()
    var showCreateChannelDialog by remember { mutableStateOf(false) }
    var showJoinChannelDialog by remember { mutableStateOf(false) }
    viewModel.setFilters(
        Filters.and(
            Filters.eq("type", "team"), Filters.`in`("members", listOf(user!!.id))
        )
    )

    if (showCreateChannelDialog) {
        CreateChannelDialog(onDismiss = { showCreateChannelDialog = false }) { channelName ->
            val channelClient = viewModel.chatClient.channel("team", UUID.randomUUID().toString())
            channelClient.create(
                memberIds = listOf(user!!.id), extraData = mapOf("name" to channelName)
            ).enqueue {
                if (it.isSuccess) {
                    Log.d("CustomChannelListScreen", "Channel created successfully")
                } else {
                    Log.e(
                        "CustomChannelListScreen",
                        it.errorOrNull()?.message ?: "Error creating channel"
                    )
                }
            }
            showCreateChannelDialog = false
        }
    }


    if (showJoinChannelDialog) {
        JoinChannelDialog(state = availableChannelListState, onJoinChannel = { channelId ->
            availableChannelListViewModel.joinChannel(channelId)
            showJoinChannelDialog = false
        }, onDismiss = { showJoinChannelDialog = false })
    }

    Scaffold(topBar = {
        CustomChannelListHeader(
            user = user,
            connectionState = connectionState,
            onCreateChannelClick = { showCreateChannelDialog = true },
            onJoinChannelClick = {
                availableChannelListViewModel.getAvailableChannelList()
                showJoinChannelDialog = true
            },
            onAvatarClick = onAvatarClick
        )
    }) { innerPadding ->
        ChannelList(modifier = modifier
            .padding(innerPadding)
            .background(ChatTheme.colors.appBackground),
            emptyContent = { EmptyContent() },
            viewModel = viewModel,
            itemContent = { channelItem -> // Customize the channel items
                CustomChannelListItem(
                    channelItem = channelItem, user = user, onChannelClick = onChannelClick
                )
            })
    }

    // unable to wipe data for some reason
    val context = LocalContext.current
    // Button(onClick = { availableChannelListViewModel.wipeData(context) }) {  
    //     Text("Transaction completed")  
    // }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom, // Aligns children to the bottom
            modifier = Modifier
                .align(Alignment.BottomCenter) // Aligns the Column itself to the bottom center of the Box
                .fillMaxWidth()
        ) {
            Button(
                onClick = { availableChannelListViewModel.wipeData(context) },
            ) {
                Text("Surprise Button!")
            }
        }
    }
}







