package com.example.ict2215_project.data.network

import com.example.ict2215_project.domain.model.CreateTokenRequest
import com.example.ict2215_project.domain.model.CreateTokenResponse
import com.example.ict2215_project.domain.model.QueryChannelsResponse
import com.example.ict2215_project.domain.model.UpsertUserRequest
import com.example.ict2215_project.domain.model.UpsertUserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface StreamChatServerAPI {
    @POST("createToken")
    suspend fun createToken(@Body request: CreateTokenRequest): CreateTokenResponse

    @POST("upsertUser")
    suspend fun upsertUser(@Body request: UpsertUserRequest): UpsertUserResponse

    @GET("channels")
    suspend fun queryChannels(@Query("user_id") userId: String): QueryChannelsResponse
}