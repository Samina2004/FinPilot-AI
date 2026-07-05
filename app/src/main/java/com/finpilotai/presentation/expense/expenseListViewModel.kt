package com.finpilotai.presentation.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finpilotai.domain.model.Expense
import com.finpilotai.domain.usecase.expense.GetAllExpensesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class ExpenseListUiState(
    val expenses: List<Expense> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String? = null,   // null = sab categories
    val isLoading: Boolean = true
) {
    val filteredExpenses: List<Expense>
        get() = expenses.filter { expense ->
            val matchesSearch = searchQuery.isBlank() ||
                    expense.merchant.contains(searchQuery, ignoreCase = true) ||
                    expense.description.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategory == null || expense.category == selectedCategory
            matchesSearch && matchesCategory
        }
}

@HiltViewModel
class ExpenseListViewModel @Inject constructor(
    private val getAllExpenses: GetAllExpensesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpenseListUiState())
    val uiState: StateFlow<ExpenseListUiState> = _uiState

    init {
        getAllExpenses()
            .onEach { expenses ->
                _uiState.update { it.copy(expenses = expenses, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onCategoryFilterChanged(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }
}