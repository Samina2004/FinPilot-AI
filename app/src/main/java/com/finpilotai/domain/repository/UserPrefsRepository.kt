package com.finpilotai.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPrefsRepository {
    val isOnboardingCompleted: Flow<Boolean>
    val rememberLogin: Flow<Boolean>
    val monthlyBudget: Flow<Double>
    suspend fun setOnboardingCompleted(completed: Boolean)
    suspend fun setRememberLogin(remember: Boolean)
    suspend fun setMonthlyBudget(budget: Double)
}