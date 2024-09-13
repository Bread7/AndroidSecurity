package com.example.ict2215_project.data.repository

import com.example.ict2215_project.domain.repository.IAuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(private val auth: FirebaseAuth) : IAuthRepository {
    override fun user(): FirebaseUser? = auth.currentUser

    override fun hasUser(): Boolean = auth.currentUser != null

    override fun getUserId(): String = auth.currentUser?.uid.orEmpty()

    override suspend fun createUser(
        email: String, password: String, onComplete: (Boolean) -> Unit
    ) {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            onComplete.invoke(result.user != null)
        } catch (e: Exception) {
            onComplete.invoke(false)
        }
    }

    override suspend fun loginUser(
        email: String, password: String, onComplete: (Boolean) -> Unit
    ) {
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            onComplete.invoke(result.user != null)
        } catch (e: Exception) {
            onComplete.invoke(false)
        }
    }

    override fun logoutUser() {
        auth.signOut()
    }
}