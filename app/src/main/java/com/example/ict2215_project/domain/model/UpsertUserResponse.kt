package com.example.ict2215_project.domain.model

import com.google.gson.annotations.SerializedName

data class UpsertUserResponse(
    val success: Boolean, val userId: String, val message: String, val errorCode: String
)
