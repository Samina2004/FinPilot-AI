// ============================================================
// FILE: domain/usecase/auth/SignInWithEmailUseCase.kt
// ============================================================
package com.finpilotai.domain.usecase.auth

import com.finpilotai.domain.model.AuthResult
import com.finpilotai.domain.model.User
import com.finpilotai.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithEmailUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): AuthResult<User> {
        if (email.isBlank() || password.isBlank())
            return AuthResult.Error("Email and password cannot be empty")
        return repository.signInWithEmail(email.trim(), password)
    }
}
