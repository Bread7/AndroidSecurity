package com.example.ict2215_project.domain.repository

import com.example.ict2215_project.domain.model.ChannelDetail

interface IStreamChatServerRepository {
    suspend fun createToken(userId: String): String
    suspend fun upsertUser(userId: String, name: String, email: String, role: String): Boolean
    suspend fun queryChannels(userId: String): List<ChannelDetail>
}