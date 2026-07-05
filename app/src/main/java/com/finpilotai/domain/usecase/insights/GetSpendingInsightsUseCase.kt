package com.finpilotai.domain.usecase.insights

import com.finpilotai.domain.model.CategorySpend
import com.finpilotai.domain.repository.InsightsRepository
import javax.inject.Inject

class GetSpendingInsightsUseCase @Inject constructor(
    private val repository: InsightsRepository
) {
    suspend operator fun invoke(
        categoryBreakdown: List<CategorySpend>,
        totalExpense: Double,
        budget: Double
    ): Result<String> {
        if (categoryBreakdown.isEmpty()) {
            return Result.success("Start adding expenses to get AI-powered insights about your spending!")
        }
        return repository.generateInsights(categoryBreakdown, totalExpense, budget)
    }
}