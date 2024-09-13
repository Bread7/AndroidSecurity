package com.example.ict2215_project.domain.model

import com.google.gson.annotations.SerializedName

data class UpsertUserRequest(
    @SerializedName("user_id") val userId: String,
    val name: String,
    val email: String,
    val role: String
)
