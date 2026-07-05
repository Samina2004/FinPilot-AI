package com.finpilotai.data.repository

import com.finpilotai.BuildConfig
import com.finpilotai.domain.model.CategorySpend
import com.finpilotai.domain.repository.InsightsRepository
import com.google.ai.client.generativeai.GenerativeModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InsightsRepositoryImpl @Inject constructor() : InsightsRepository {

    private val model = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    override suspend fun generateInsights(
        categoryBreakdown: List<CategorySpend>,
        totalExpense: Double,
        budget: Double
    ): Result<String> {
        val breakdownText = categoryBreakdown.joinToString("\n") {
            "${it.category}: Rs.${it.amount.toInt()}"
        }
        val percentUsed = if (budget > 0) ((totalExpense / budget) * 100).toInt() else 0

        return try {
            val prompt = """
                You are a friendly personal finance assistant. Analyze this user's monthly spending and give them a short, encouraging, actionable insight.
                
                Monthly Budget: Rs.${budget.toInt()}
                Total Spent: Rs.${totalExpense.toInt()} ($percentUsed% of budget)
                
                Category breakdown:
                $breakdownText
                
                Write 2-3 short sentences (max 50 words total). Be specific, mention the highest spending category, and give one practical tip. Friendly and conversational tone, no markdown formatting, plain text only.
            """.trimIndent()

            val response = model.generateContent(prompt)
            val text = response.text?.trim()

            if (text.isNullOrBlank()) {
                Result.success(fallbackInsight(categoryBreakdown, totalExpense, budget, percentUsed))
            } else {
                Result.success(text)
            }
        } catch (e: Exception) {
            // Gemini fail ho gaya (quota/network) — local fallback insight use karo
            Result.success(fallbackInsight(categoryBreakdown, totalExpense, budget, percentUsed))
        }
    }

    // ── Fallback: rule-based insight (no AI needed) ────────────────────────────
    private fun fallbackInsight(
        breakdown: List<CategorySpend>,
        totalExpense: Double,
        budget: Double,
        percentUsed: Int
    ): String {
        if (breakdown.isEmpty()) {
            return "Start adding expenses to track your spending patterns!"
        }

        val topCategory = breakdown.maxByOrNull { it.amount }
        val topCategoryText = if (topCategory != null) {
            val sharePercent = if (totalExpense > 0) ((topCategory.amount / totalExpense) * 100).toInt() else 0
            "Your highest spending is in ${topCategory.category} (${sharePercent}% of total)."
        } else ""

        val budgetText = when {
            budget <= 0 -> "Set a monthly budget to track your progress better."
            percentUsed >= 100 -> "You've exceeded your monthly budget — consider reviewing your expenses."
            percentUsed >= 80 -> "You've used $percentUsed% of your budget. Keep an eye on spending for the rest of the month."
            else -> "You're at $percentUsed% of your budget — looking good!"
        }

        return "$topCategoryText $budgetText".trim()
    }
}