package com.finpilotai.presentation.expense

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finpilotai.domain.model.Expense
import com.finpilotai.domain.model.ExpenseCategory
import com.finpilotai.domain.model.PaymentMethod
import com.finpilotai.domain.usecase.expense.AddExpenseUseCase
import com.finpilotai.domain.usecase.expense.DeleteExpenseUseCase
import com.finpilotai.domain.usecase.expense.GetExpenseByIdUseCase
import com.finpilotai.domain.usecase.expense.UpdateExpenseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddExpenseUiState(
    val expenseId: Long? = null,
    val amount: String = "",
    val category: String = ExpenseCategory.FOOD,
    val merchant: String = "",
    val description: String = "",
    val date: Long = System.currentTimeMillis(),
    val paymentMethod: String = PaymentMethod.CASH,
    val imagePath: String? = null,      // Receipt image path
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val isDeleted: Boolean = false,
    val errorMessage: String? = null,
    val amountError: String? = null
) {
    val isEditMode: Boolean get() = expenseId != null
}

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val addExpense: AddExpenseUseCase,
    private val updateExpense: UpdateExpenseUseCase,
    private val deleteExpense: DeleteExpenseUseCase,
    private val getExpenseById: GetExpenseByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddExpenseUiState())
    val uiState: StateFlow<AddExpenseUiState> = _uiState

    init {
        val expenseId: Long? = savedStateHandle.get<String>("expenseId")?.toLongOrNull()
        if (expenseId != null) {
            loadExpense(expenseId)
        } else {
            // Scan se aaye pre-fill data check karo
            val scannedMerchant  = savedStateHandle.get<String>("merchant")
            val scannedAmount    = savedStateHandle.get<String>("amount")?.toDoubleOrNull()
            val scannedCategory  = savedStateHandle.get<String>("category")
            val scannedImagePath = savedStateHandle.get<String>("imagePath")

            _uiState.update {
                it.copy(
                    merchant  = scannedMerchant ?: "",
                    amount    = if (scannedAmount != null && scannedAmount > 0) scannedAmount.toString() else "",
                    category  = scannedCategory ?: it.category,
                    imagePath = scannedImagePath
                )
            }
        }
    }

    private fun loadExpense(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val expense = getExpenseById(id)
            if (expense != null) {
                _uiState.update {
                    it.copy(
                        expenseId     = expense.id,
                        amount        = expense.amount.toString(),
                        category      = expense.category,
                        merchant      = expense.merchant,
                        description   = expense.description,
                        date          = expense.date,
                        paymentMethod = expense.paymentMethod,
                        imagePath     = expense.imagePath,
                        isLoading     = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Expense not found") }
            }
        }
    }

    fun onAmountChanged(value: String) {
        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
            _uiState.update { it.copy(amount = value, amountError = null) }
        }
    }

    fun onCategoryChanged(value: String) = _uiState.update { it.copy(category = value) }
    fun onMerchantChanged(value: String) = _uiState.update { it.copy(merchant = value) }
    fun onDescriptionChanged(value: String) = _uiState.update { it.copy(description = value) }
    fun onDateChanged(value: Long) = _uiState.update { it.copy(date = value) }
    fun onPaymentMethodChanged(value: String) = _uiState.update { it.copy(paymentMethod = value) }

    fun saveExpense() {
        val state = _uiState.value
        val amountValue = state.amount.toDoubleOrNull()

        if (amountValue == null || amountValue <= 0) {
            _uiState.update { it.copy(amountError = "Enter a valid amount") }
            return
        }

        val expense = Expense(
            id            = state.expenseId ?: 0,
            amount        = amountValue,
            category      = state.category,
            merchant      = state.merchant.ifBlank { "Unknown" },
            description   = state.description,
            date          = state.date,
            paymentMethod = state.paymentMethod,
            imagePath     = state.imagePath
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = if (state.isEditMode) {
                updateExpense(expense)
            } else {
                addExpense(expense).map { Unit }
            }

            result.fold(
                onSuccess = { _uiState.update { it.copy(isLoading = false, isSaved = true) } },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Failed to save expense") }
                }
            )
        }
    }

    fun deleteCurrentExpense() {
        val state = _uiState.value
        if (!state.isEditMode || state.expenseId == null) return

        val expense = Expense(
            id            = state.expenseId,
            amount        = state.amount.toDoubleOrNull() ?: 0.0,
            category      = state.category,
            merchant      = state.merchant,
            description   = state.description,
            date          = state.date,
            paymentMethod = state.paymentMethod,
            imagePath     = state.imagePath
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            deleteExpense(expense).fold(
                onSuccess = { _uiState.update { it.copy(isLoading = false, isDeleted = true) } },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Failed to delete") }
                }
            )
        }
    }

    fun clearError() = _uiState.update { it.copy(errorMessage = null) }
}