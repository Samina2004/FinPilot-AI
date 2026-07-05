// ============================================================
// FILE: domain/usecase/auth/RegisterWithEmailUseCase.kt
// ============================================================
package com.finpilotai.domain.usecase.auth

import com.finpilotai.domain.model.AuthResult
import com.finpilotai.domain.model.User
import com.finpilotai.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterWithEmailUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String
    ): AuthResult<User> {
        if (name.isBlank()) return AuthResult.Error("Name cannot be empty")
        if (email.isBlank()) return AuthResult.Error("Email cannot be empty")
        if (password.length < 6) return AuthResult.Error("Password must be at least 6 characters")
        return repository.registerWithEmail(email.trim(), password, name.trim())
    }
}
