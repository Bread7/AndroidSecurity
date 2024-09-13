package com.example.ict2215_project.data.repository

import android.util.Log
import com.example.ict2215_project.data.network.StreamChatServerAPI
import com.example.ict2215_project.domain.model.ChannelDetail
import com.example.ict2215_project.domain.model.CreateTokenRequest
import com.example.ict2215_project.domain.model.UpsertUserRequest
import com.example.ict2215_project.domain.repository.IStreamChatServerRepository
import javax.inject.Inject

class StreamChatServerRepository @Inject constructor(
    private val api: StreamChatServerAPI
) : IStreamChatServerRepository {
    override suspend fun createToken(userId: String): String {
        try {
            // Construct the request object
            val request = CreateTokenRequest(user_id = userId)
            // Make the API call and await the result
            val response = api.createToken(request)
            // Log and return the token from the response
            Log.d("StreamChatTokenRepository", "createToken: ${response.token}")
            return response.token
        } catch (e: Exception) {
            Log.e(
                "StreamChatTokenRepository",
                "Error creating token for user $userId: ${e.message}",
                e
            )
            // Handle the exception appropriately - maybe rethrow, return a default/fallback value, or handle differently
            throw e // or return "" or a specific error token
        }
    }


    override suspend fun upsertUser(
        userId: String, name: String, email: String, role: String
    ): Boolean {
        try {
            // Construct the request object for upserting the user
            val request = UpsertUserRequest(
                userId = userId, name = name, email = email, role = role
            )
            // Make the API call and await the result
            val response = api.upsertUser(request)
            // Here you might want to log the response or perform additional checks
            Log.d("StreamChatUserRepository", "upsertUser: success for user $userId")
            // Return true to indicate success
            return true
        } catch (e: Exception) {
            Log.e("StreamChatUserRepository", "Error upserting user: ${e.message}")
            // Return false or handle the exception as appropriate
            return false
        }
    }

    override suspend fun queryChannels(userId: String): List<ChannelDetail> {
        try {
            // Make the API call and await the result
            val response = api.queryChannels(userId)
            // Log and return the list of channels from the response
            Log.d("StreamChatChannelRepository", "queryChannels: ${response.channels}")
            return response.channels
        } catch (e: Exception) {
            Log.e("StreamChatChannelRepository", "Error querying channels: ${e.message}")
            // Handle the exception appropriately - maybe rethrow, return an empty list, or handle differently
            throw e // or return emptyList() or a specific error list
        }
    }
}