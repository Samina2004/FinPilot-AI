// ============================================================
// FILE: presentation/auth/forgot/ForgotPasswordViewModel.kt
// ============================================================
package com.finpilotai.presentation.auth.forgot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finpilotai.domain.model.AuthResult
import com.finpilotai.domain.usecase.auth.ForgotPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ForgotPasswordUiState(
    val email: String         = "",
    val isLoading: Boolean    = false,
    val isEmailSent: Boolean  = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val forgotPassword: ForgotPasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState

    fun onEmailChanged(email: String) = _uiState.update { it.copy(email = email) }

    fun sendReset() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = forgotPassword(_uiState.value.email)) {
                is AuthResult.Success -> _uiState.update { it.copy(isLoading = false, isEmailSent = true) }
                is AuthResult.Error   -> _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                else -> Unit
            }
        }
    }

    fun clearError() = _uiState.update { it.copy(errorMessage = null) }
}