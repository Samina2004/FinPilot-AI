package com.finpilotai.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finpilotai.domain.model.CategorySpend
import com.finpilotai.domain.repository.ExpenseRepository
import com.finpilotai.domain.repository.UserPrefsRepository
import com.finpilotai.domain.usecase.insights.GetSpendingInsightsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class DashboardUiState(
    val monthlyBudget: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val remainingBudget: Double = 0.0,
    val totalSavings: Double = 0.0,
    val categoryBreakdown: List<CategorySpend> = emptyList(),
    val isLoading: Boolean = true,
    val insightText: String? = null,
    val isInsightLoading: Boolean = false
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val userPrefsRepository: UserPrefsRepository,
    private val getSpendingInsights: GetSpendingInsightsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        val (startMillis, endMillis) = getCurrentMonthRange()

        combine(
            expenseRepository.getTotalExpenseBetween(startMillis, endMillis),
            expenseRepository.getCategoryBreakdownBetween(startMillis, endMillis),
            userPrefsRepository.monthlyBudget
        ) { totalExpense, breakdown, budget ->
            DashboardUiState(
                monthlyBudget    = budget,
                monthlyExpense   = totalExpense,
                remainingBudget  = (budget - totalExpense).coerceAtLeast(0.0),
                totalSavings     = (budget - totalExpense).coerceAtLeast(0.0),
                categoryBreakdown = breakdown,
                isLoading        = false,
                insightText      = _uiState.value.insightText,
                isInsightLoading = _uiState.value.isInsightLoading
            )
        }.onEach { newState ->
            val previousBreakdown = _uiState.value.categoryBreakdown
            _uiState.update { newState }
            if (newState.categoryBreakdown.isNotEmpty() && newState.categoryBreakdown != previousBreakdown) {
                loadInsights()
            }
        }.launchIn(viewModelScope)
    }

    fun updateBudget(newBudget: Double) {
        viewModelScope.launch {
            userPrefsRepository.setMonthlyBudget(newBudget)
        }
    }

    fun loadInsights() {
        viewModelScope.launch {
            _uiState.update { it.copy(isInsightLoading = true) }
            val state = _uiState.value
            getSpendingInsights(state.categoryBreakdown, state.monthlyExpense, state.monthlyBudget)
                .fold(
                    onSuccess = { text ->
                        _uiState.update { it.copy(insightText = text, isInsightLoading = false) }
                    },
                    onFailure = {
                        _uiState.update { it.copy(isInsightLoading = false) }
                    }
                )
        }
    }

    private fun getCurrentMonthRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.MILLISECOND, -1)
        val end = calendar.timeInMillis

        return start to end
    }
}