package com.finpilotai.domain.usecase.expense

import com.finpilotai.domain.model.Expense
import com.finpilotai.domain.repository.ExpenseRepository
import javax.inject.Inject

class GetExpenseByIdUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(id: Long): Expense? = repository.getExpenseById(id)
}