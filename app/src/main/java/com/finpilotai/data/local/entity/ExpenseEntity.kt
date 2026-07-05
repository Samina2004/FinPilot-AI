package com.finpilotai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.finpilotai.domain.model.Expense

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val category: String,
    val merchant: String,
    val description: String,
    val date: Long,
    val paymentMethod: String,
    val imagePath: String? = null  // Receipt image path
)

fun ExpenseEntity.toDomain() = Expense(
    id            = id,
    amount        = amount,
    category      = category,
    merchant      = merchant,
    description   = description,
    date          = date,
    paymentMethod = paymentMethod,
    imagePath     = imagePath
)

fun Expense.toEntity() = ExpenseEntity(
    id            = id,
    amount        = amount,
    category      = category,
    merchant      = merchant,
    description   = description,
    date          = date,
    paymentMethod = paymentMethod,
    imagePath     = imagePath
)