package com.finpilotai.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import com.finpilotai.domain.repository.UserPrefsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPrefsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPrefsRepository {

    private object Keys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val REMEMBER_LOGIN       = booleanPreferencesKey("remember_login")
        val MONTHLY_BUDGET       = doublePreferencesKey("monthly_budget")
    }

    override val isOnboardingCompleted: Flow<Boolean> =
        dataStore.data.map { it[Keys.ONBOARDING_COMPLETED] ?: false }

    override val rememberLogin: Flow<Boolean> =
        dataStore.data.map { it[Keys.REMEMBER_LOGIN] ?: false }

    override val monthlyBudget: Flow<Double> =
        dataStore.data.map { it[Keys.MONTHLY_BUDGET] ?: 0.0 }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { it[Keys.ONBOARDING_COMPLETED] = completed }
    }

    override suspend fun setRememberLogin(remember: Boolean) {
        dataStore.edit { it[Keys.REMEMBER_LOGIN] = remember }
    }

    override suspend fun setMonthlyBudget(budget: Double) {
        dataStore.edit { it[Keys.MONTHLY_BUDGET] = budget }
    }
}