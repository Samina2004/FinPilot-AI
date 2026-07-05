// ============================================================
// FILE: data/repository/AuthRepositoryImpl.kt
// ============================================================
package com.finpilotai.data.repository

import com.finpilotai.domain.model.AuthResult
import com.finpilotai.domain.model.User
import com.finpilotai.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override val currentUser: Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.toDomain())
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun signInWithEmail(
        email: String,
        password: String
    ): AuthResult<User> = runCatching {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        AuthResult.Success(result.user!!.toDomain())
    }.getOrElse { AuthResult.Error(it.message ?: "Sign-in failed", it as? Exception) }

    override suspend fun registerWithEmail(
        email: String,
        password: String,
        name: String
    ): AuthResult<User> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user!!
        // Update display name
        user.updateProfile(userProfileChangeRequest { displayName = name }).await()
        // Create Firestore user document
        firestore.collection("users").document(user.uid)
            .set(mapOf("name" to name, "email" to email, "createdAt" to System.currentTimeMillis()))
            .await()
        AuthResult.Success(user.toDomain())
    }.getOrElse { AuthResult.Error(it.message ?: "Registration failed", it as? Exception) }

    override suspend fun signInWithGoogle(idToken: String): AuthResult<User> = runCatching {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        val user = result.user!!
        // Create Firestore document if new user
        if (result.additionalUserInfo?.isNewUser == true) {
            firestore.collection("users").document(user.uid)
                .set(mapOf(
                    "name" to (user.displayName ?: ""),
                    "email" to (user.email ?: ""),
                    "createdAt" to System.currentTimeMillis()
                )).await()
        }
        AuthResult.Success(user.toDomain())
    }.getOrElse { AuthResult.Error(it.message ?: "Google sign-in failed", it as? Exception) }

    override suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit> = runCatching {
        auth.sendPasswordResetEmail(email).await()
        AuthResult.Success(Unit)
    }.getOrElse { AuthResult.Error(it.message ?: "Failed to send reset email", it as? Exception) }

    override suspend fun signOut() {
        auth.signOut()
    }

    override fun isUserLoggedIn(): Boolean = auth.currentUser != null

    private fun FirebaseUser.toDomain() = User(
        uid             = uid,
        email           = email ?: "",
        displayName     = displayName,
        photoUrl        = photoUrl?.toString(),
        isEmailVerified = isEmailVerified
    )
}
