package com.finpilotai.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.finpilotai.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpenseById(id: Long): ExpenseEntity?

    @Query("""
        SELECT * FROM expenses 
        WHERE date BETWEEN :startMillis AND :endMillis 
        ORDER BY date DESC
    """)
    fun getExpensesBetween(startMillis: Long, endMillis: Long): Flow<List<ExpenseEntity>>

    @Query("""
        SELECT COALESCE(SUM(amount), 0.0) FROM expenses 
        WHERE date BETWEEN :startMillis AND :endMillis
    """)
    fun getTotalExpenseBetween(startMillis: Long, endMillis: Long): Flow<Double>

    @Query("""
        SELECT category, SUM(amount) as amount FROM expenses 
        WHERE date BETWEEN :startMillis AND :endMillis 
        GROUP BY category 
        ORDER BY amount DESC
    """)
    fun getCategoryBreakdownBetween(startMillis: Long, endMillis: Long): Flow<List<CategorySpendRow>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpenseById(id: Long)
}

data class CategorySpendRow(
    val category: String,
    val amount: Double
)