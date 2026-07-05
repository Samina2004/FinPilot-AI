// ============================================================
// FILE: presentation/splash/SplashViewModel.kt
// ============================================================
package com.finpilotai.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finpilotai.domain.repository.AuthRepository
import com.finpilotai.domain.repository.UserPrefsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashDestination {
    object Idle        : SplashDestination()
    object Onboarding  : SplashDestination()
    object Dashboard   : SplashDestination()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val prefsRepository: UserPrefsRepository
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination>(SplashDestination.Idle)
    val destination: StateFlow<SplashDestination> = _destination

    init {
        viewModelScope.launch {
            delay(2000L) // Splash display duration
            val onboardingDone = prefsRepository.isOnboardingCompleted.first()
            val isLoggedIn     = authRepository.isUserLoggedIn()
            _destination.value = when {
                isLoggedIn    -> SplashDestination.Dashboard
                onboardingDone-> SplashDestination.Onboarding  // Skip onboarding, go login? Actually send to login
                else          -> SplashDestination.Onboarding
            }
        }
    }
}