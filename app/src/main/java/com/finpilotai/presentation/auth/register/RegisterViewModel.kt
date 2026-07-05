// ============================================================
// FILE: presentation/auth/register/RegisterViewModel.kt
// ============================================================
package com.finpilotai.presentation.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finpilotai.domain.model.AuthResult
import com.finpilotai.domain.usecase.auth.RegisterWithEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val name: String          = "",
    val email: String         = "",
    val password: String      = "",
    val confirmPassword: String = "",
    val isLoading: Boolean    = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean    = false
)

sealed class RegisterEvent {
    data class NameChanged(val name: String)            : RegisterEvent()
    data class EmailChanged(val email: String)          : RegisterEvent()
    data class PasswordChanged(val pass: String)        : RegisterEvent()
    data class ConfirmPasswordChanged(val pass: String) : RegisterEvent()
    object RegisterClicked                              : RegisterEvent()
    object ClearError                                   : RegisterEvent()
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerWithEmail: RegisterWithEmailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.NameChanged            -> _uiState.update { it.copy(name = event.name) }
            is RegisterEvent.EmailChanged           -> _uiState.update { it.copy(email = event.email) }
            is RegisterEvent.PasswordChanged        -> _uiState.update { it.copy(password = event.pass) }
            is RegisterEvent.ConfirmPasswordChanged -> _uiState.update { it.copy(confirmPassword = event.pass) }
            is RegisterEvent.RegisterClicked        -> register()
            is RegisterEvent.ClearError             -> _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun register() {
        val state = _uiState.value
        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Passwords do not match") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = registerWithEmail(state.name, state.email, state.password)) {
                is AuthResult.Success -> _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                is AuthResult.Error   -> _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                else -> Unit
            }
        }
    }
}
