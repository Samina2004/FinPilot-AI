package com.finpilotai.domain.model

object ExpenseCategory {
    const val FOOD          = "Food"
    const val TRANSPORT     = "Transport"
    const val SHOPPING      = "Shopping"
    const val BILLS         = "Bills"
    const val ENTERTAINMENT = "Entertainment"
    const val HEALTH        = "Health"
    const val EDUCATION     = "Education"
    const val OTHER         = "Other"

    val all = listOf(FOOD, TRANSPORT, SHOPPING, BILLS, ENTERTAINMENT, HEALTH, EDUCATION, OTHER)

    fun emojiFor(category: String): String = when (category) {
        FOOD          -> "🍔"
        TRANSPORT     -> "🚗"
        SHOPPING      -> "🛍️"
        BILLS         -> "🧾"
        ENTERTAINMENT -> "🎬"
        HEALTH        -> "💊"
        EDUCATION     -> "📚"
        else          -> "💰"
    }
}

object PaymentMethod {
    const val CASH          = "Cash"
    const val CARD          = "Card"
    const val BANK_TRANSFER = "Bank Transfer"

    val all = listOf(CASH, CARD, BANK_TRANSFER)
}