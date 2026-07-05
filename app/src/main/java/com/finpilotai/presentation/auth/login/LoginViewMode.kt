// ============================================================
// FILE: presentation/auth/login/LoginViewModel.kt
// ============================================================
package com.finpilotai.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finpilotai.domain.model.AuthResult
import com.finpilotai.domain.usecase.auth.SignInWithEmailUseCase
import com.finpilotai.domain.usecase.auth.SignInWithGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String        = "",
    val password: String     = "",
    val isLoading: Boolean   = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean   = false,
    val emailError: String?  = null,
    val passwordError: String? = null
)

sealed class LoginEvent {
    data class EmailChanged(val email: String)    : LoginEvent()
    data class PasswordChanged(val pass: String)  : LoginEvent()
    object LoginClicked                           : LoginEvent()
    data class GoogleSignIn(val idToken: String)  : LoginEvent()
    object ClearError                             : LoginEvent()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInWithEmail: SignInWithEmailUseCase,
    private val signInWithGoogle: SignInWithGoogleUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged    -> _uiState.update { it.copy(email = event.email, emailError = null) }
            is LoginEvent.PasswordChanged -> _uiState.update { it.copy(password = event.pass, passwordError = null) }
            is LoginEvent.LoginClicked    -> emailLogin()
            is LoginEvent.GoogleSignIn    -> googleLogin(event.idToken)
            is LoginEvent.ClearError      -> _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun emailLogin() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = signInWithEmail(_uiState.value.email, _uiState.value.password)) {
                is AuthResult.Success -> _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                is AuthResult.Error   -> _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                else -> Unit
            }
        }
    }

    private fun googleLogin(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = signInWithGoogle(idToken)) {
                is AuthResult.Success -> _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                is AuthResult.Error   -> _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                else -> Unit
            }
        }
    }
}
