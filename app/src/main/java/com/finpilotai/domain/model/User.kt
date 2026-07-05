// ============================================================
// FILE: domain/model/User.kt
// ============================================================
package com.finpilotai.domain.model

data class User(
    val uid: String,
    val email: String,
    val displayName: String?,
    val photoUrl: String?,
    val isEmailVerified: Boolean
)
