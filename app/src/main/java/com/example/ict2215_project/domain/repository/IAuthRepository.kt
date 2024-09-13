package com.example.ict2215_project.domain.repository

import com.google.firebase.auth.FirebaseUser

interface IAuthRepository {
    fun user(): FirebaseUser?
    fun hasUser(): Boolean
    fun getUserId(): String
    suspend fun createUser(email: String, password: String, onComplete: (Boolean) -> Unit)
    suspend fun loginUser(email: String, password: String, onComplete: (Boolean) -> Unit)
    fun logoutUser()
}