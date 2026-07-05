// ============================================================
// FILE: presentation/onboarding/OnboardingViewModel.kt
// ============================================================
package com.finpilotai.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finpilotai.domain.repository.UserPrefsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val prefsRepository: UserPrefsRepository
) : ViewModel() {

    fun completeOnboarding() {
        viewModelScope.launch {
            prefsRepository.setOnboardingCompleted(true)
        }
    }
}
