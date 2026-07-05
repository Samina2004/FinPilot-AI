// ============================================================
// FILE: domain/usecase/auth/ForgotPasswordUseCase.kt
// ============================================================
package com.finpilotai.domain.usecase.auth

import com.finpilotai.domain.model.AuthResult
import com.finpilotai.domain.repository.AuthRepository
import javax.inject.Inject

class ForgotPasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String): AuthResult<Unit> {
        if (email.isBlank()) return AuthResult.Error("Email cannot be empty")
        return repository.sendPasswordResetEmail(email.trim())
    }
}