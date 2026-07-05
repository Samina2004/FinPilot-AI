package com.finpilotai.domain.repository

import com.finpilotai.domain.model.CategorySpend
import com.finpilotai.domain.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getAllExpenses(): Flow<List<Expense>>
    suspend fun getExpenseById(id: Long): Expense?
    fun getExpensesBetween(startMillis: Long, endMillis: Long): Flow<List<Expense>>
    fun getTotalExpenseBetween(startMillis: Long, endMillis: Long): Flow<Double>
    fun getCategoryBreakdownBetween(startMillis: Long, endMillis: Long): Flow<List<CategorySpend>>
    suspend fun addExpense(expense: Expense): Long
    suspend fun updateExpense(expense: Expense)
    suspend fun deleteExpense(expense: Expense)
    suspend fun deleteExpenseById(id: Long)
}