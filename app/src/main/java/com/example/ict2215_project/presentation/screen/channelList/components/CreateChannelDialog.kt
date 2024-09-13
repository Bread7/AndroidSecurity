package com.example.ict2215_project.presentation.screen.channelList.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun CreateChannelDialog(onDismiss: () -> Unit, onCreate: (String) -> Unit) {
    var channelName by remember { mutableStateOf("") }

    AlertDialog(onDismissRequest = onDismiss, title = { Text("Create New Channel") }, text = {
        Column {
            Text("Enter the name for the new channel:")
            TextField(value = channelName,
                onValueChange = { channelName = it },
                placeholder = { Text("Channel Name") })
        }
    }, confirmButton = {
        Button(onClick = { onCreate(channelName) }) {
            Text("Create")
        }
    }, dismissButton = {
        Button(onClick = onDismiss) {
            Text("Cancel")
        }
    })
}