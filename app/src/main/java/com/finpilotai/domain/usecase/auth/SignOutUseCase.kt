// ============================================================
// FILE: domain/usecase/auth/SignOutUseCase.kt
// ============================================================
package com.finpilotai.domain.usecase.auth

import com.finpilotai.domain.repository.AuthRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() = repository.signOut()
}
