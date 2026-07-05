package com.finpilotai.domain.model

data class Expense(
    val id: Long = 0,
    val amount: Double,
    val category: String,
    val merchant: String,
    val description: String,
    val date: Long,
    val paymentMethod: String,
    val imagePath: String? = null  // Receipt image path (optional)
)