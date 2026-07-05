package com.finpilotai.domain.repository

import com.finpilotai.domain.model.CategorySpend

interface InsightsRepository {
    suspend fun generateInsights(
        categoryBreakdown: List<CategorySpend>,
        totalExpense: Double,
        budget: Double
    ): Result<String>
}