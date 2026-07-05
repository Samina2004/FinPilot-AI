package com.finpilotai.domain.usecase.expense

import com.finpilotai.domain.model.Expense
import com.finpilotai.domain.repository.ExpenseRepository
import javax.inject.Inject

class AddExpenseUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(expense: Expense): Result<Long> {
        if (expense.amount <= 0) {
            return Result.failure(IllegalArgumentException("Amount must be greater than zero"))
        }
        if (expense.category.isBlank()) {
            return Result.failure(IllegalArgumentException("Category cannot be empty"))
        }
        return try {
            val id = repository.addExpense(expense)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}