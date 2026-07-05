package com.finpilotai.domain.usecase.expense

import com.finpilotai.domain.model.Expense
import com.finpilotai.domain.repository.ExpenseRepository
import javax.inject.Inject

class UpdateExpenseUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(expense: Expense): Result<Unit> {
        if (expense.amount <= 0) {
            return Result.failure(IllegalArgumentException("Amount must be greater than zero"))
        }
        return try {
            repository.updateExpense(expense)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}