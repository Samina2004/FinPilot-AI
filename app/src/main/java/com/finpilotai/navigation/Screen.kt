package com.finpilotai.navigation

sealed class Screen(val route: String) {
    object Splash         : Screen("splash")
    object Onboarding     : Screen("onboarding")
    object Login          : Screen("login")
    object Register       : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Dashboard      : Screen("dashboard")
    object ExpenseList    : Screen("expense_list")
    object ScanReceipt    : Screen("scan_receipt")
    object AddExpense     : Screen("add_expense?expenseId={expenseId}&merchant={merchant}&amount={amount}&category={category}&imagePath={imagePath}") {
        fun createRoute(
            expenseId: Long? = null,
            merchant: String? = null,
            amount: Double? = null,
            category: String? = null,
            imagePath: String? = null
        ): String {
            val params = mutableListOf<String>()
            expenseId?.let { params.add("expenseId=$it") }
            merchant?.let { params.add("merchant=${java.net.URLEncoder.encode(it, "UTF-8")}") }
            amount?.let { params.add("amount=$it") }
            category?.let { params.add("category=${java.net.URLEncoder.encode(it, "UTF-8")}") }
            imagePath?.let { params.add("imagePath=${java.net.URLEncoder.encode(it, "UTF-8")}") }
            return if (params.isEmpty()) "add_expense" else "add_expense?${params.joinToString("&")}"
        }
    }
}