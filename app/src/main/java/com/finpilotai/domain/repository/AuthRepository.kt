// ============================================================
// FILE: domain/repository/AuthRepository.kt
// ============================================================
package com.finpilotai.domain.repository

import com.finpilotai.domain.model.AuthResult
import com.finpilotai.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun signInWithEmail(email: String, password: String): AuthResult<User>
    suspend fun registerWithEmail(email: String, password: String, name: String): AuthResult<User>
    suspend fun signInWithGoogle(idToken: String): AuthResult<User>
    suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit>
    suspend fun signOut()
    fun isUserLoggedIn(): Boolean
}
