package com.finpilotai.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.finpilotai.presentation.auth.forgot.ForgotPasswordScreen
import com.finpilotai.presentation.auth.login.LoginScreen
import com.finpilotai.presentation.auth.register.RegisterScreen
import com.finpilotai.presentation.dashboard.DashboardScreen
import com.finpilotai.presentation.expense.AddExpenseScreen
import com.finpilotai.presentation.expense.ExpenseListScreen
import com.finpilotai.presentation.onboarding.OnboardingScreen
import com.finpilotai.presentation.receipt.ScanReceiptScreen
import com.finpilotai.presentation.splash.SplashScreen

@Composable
fun FinPilotNavGraph(navController: NavHostController) {
    NavHost(
        navController    = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinished = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister       = { navController.navigate(Screen.Register.route) },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onAddExpense      = { navController.navigate(Screen.AddExpense.createRoute()) },
                onViewAllExpenses = { navController.navigate(Screen.ExpenseList.route) },
                onScanReceipt     = { navController.navigate(Screen.ScanReceipt.route) }
            )
        }

        composable(Screen.ExpenseList.route) {
            ExpenseListScreen(
                onBack        = { navController.popBackStack() },
                onAddExpense  = { navController.navigate(Screen.AddExpense.createRoute()) },
                onEditExpense = { expenseId ->
                    navController.navigate(Screen.AddExpense.createRoute(expenseId))
                }
            )
        }

        // ── Scan Receipt — seedha image path pass karo AddExpense pe ─────────
        composable(Screen.ScanReceipt.route) {
            ScanReceiptScreen(
                onBack = { navController.popBackStack() },
                onImageCaptured = { imagePath ->
                    navController.navigate(
                        Screen.AddExpense.createRoute(imagePath = imagePath)
                    ) {
                        popUpTo(Screen.ScanReceipt.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Add / Edit Expense — imagePath argument bhi ab included hai ──────
        composable(
            route = Screen.AddExpense.route,
            arguments = listOf(
                navArgument("expenseId") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("merchant")  { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("amount")    { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("category")  { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("imagePath") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) {
            AddExpenseScreen(
                onBack  = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
    }
}