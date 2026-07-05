package com.finpilotai.data.repository

import com.finpilotai.data.local.dao.ExpenseDao
import com.finpilotai.data.local.entity.toDomain
import com.finpilotai.data.local.entity.toEntity
import com.finpilotai.domain.model.CategorySpend
import com.finpilotai.domain.model.Expense
import com.finpilotai.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepositoryImpl @Inject constructor(
    private val dao: ExpenseDao
) : ExpenseRepository {

    override fun getAllExpenses(): Flow<List<Expense>> =
        dao.getAllExpenses().map { list -> list.map { it.toDomain() } }

    override suspend fun getExpenseById(id: Long): Expense? =
        dao.getExpenseById(id)?.toDomain()

    override fun getExpensesBetween(startMillis: Long, endMillis: Long): Flow<List<Expense>> =
        dao.getExpensesBetween(startMillis, endMillis).map { list -> list.map { it.toDomain() } }

    override fun getTotalExpenseBetween(startMillis: Long, endMillis: Long): Flow<Double> =
        dao.getTotalExpenseBetween(startMillis, endMillis)

    override fun getCategoryBreakdownBetween(startMillis: Long, endMillis: Long): Flow<List<CategorySpend>> =
        dao.getCategoryBreakdownBetween(startMillis, endMillis).map { rows ->
            rows.map { CategorySpend(category = it.category, amount = it.amount) }
        }

    override suspend fun addExpense(expense: Expense): Long =
        dao.insertExpense(expense.toEntity())

    override suspend fun updateExpense(expense: Expense) =
        dao.updateExpense(expense.toEntity())

    override suspend fun deleteExpense(expense: Expense) =
        dao.deleteExpense(expense.toEntity())

    override suspend fun deleteExpenseById(id: Long) =
        dao.deleteExpenseById(id)
}