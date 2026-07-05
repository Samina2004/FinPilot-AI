package com.finpilotai.domain.model

data class CategorySpend(
    val category: String,
    val amount: Double
)

data class DashboardSummary(
    val monthlyBudget: Double,
    val monthlyExpense: Double,
    val remainingBudget: Double,
    val totalSavings: Double,
    val categoryBreakdown: List<CategorySpend>
)