// ============================================================
// FILE: domain/usecase/auth/SignInWithGoogleUseCase.kt
// ============================================================
package com.finpilotai.domain.usecase.auth

import com.finpilotai.domain.model.AuthResult
import com.finpilotai.domain.model.User
import com.finpilotai.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): AuthResult<User> =
        repository.signInWithGoogle(idToken)
}
