package com.finpilotai.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.finpilotai.domain.model.CategorySpend
import com.finpilotai.presentation.components.GlassCard
import com.finpilotai.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    onAddExpense: () -> Unit = {},
    onViewAllExpenses: () -> Unit = {},
    onScanReceipt: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showBudgetDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BrandPrimary.copy(alpha = 0.08f), Background)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(24.dp))

            Text(
                text  = "Dashboard",
                style = MaterialTheme.typography.headlineLarge,
                color = OnBackground
            )
            Text(
                text  = "Your monthly overview",
                style = MaterialTheme.typography.bodyLarge,
                color = OnSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            } else {
                // ── Summary Cards Grid ─────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { showBudgetDialog = true },
                        label    = "Monthly Budget",
                        amount   = uiState.monthlyBudget,
                        emoji    = "💰"
                    )
                    SummaryCard(
                        modifier = Modifier.weight(1f),
                        label    = "Monthly Expense",
                        amount   = uiState.monthlyExpense,
                        emoji    = "💸"
                    )
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        modifier = Modifier.weight(1f),
                        label    = "Remaining",
                        amount   = uiState.remainingBudget,
                        emoji    = "📊"
                    )
                    SummaryCard(
                        modifier = Modifier.weight(1f),
                        label    = "Savings",
                        amount   = uiState.totalSavings,
                        emoji    = "🎯"
                    )
                }

                if (uiState.monthlyBudget == 0.0) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text  = "Tap Monthly Budget to set your budget",
                        style = MaterialTheme.typography.labelMedium,
                        color = Primary
                    )
                }

                Spacer(Modifier.height(24.dp))

                // ── AI Spending Insight Card ─────────────────────────────────
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("✨", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text  = "AI Insight",
                            style = MaterialTheme.typography.titleLarge,
                            color = OnBackground
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    if (uiState.isInsightLoading) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Primary)
                            Spacer(Modifier.width(8.dp))
                            Text("Analyzing your spending...", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                        }
                    } else {
                        Text(
                            text  = uiState.insightText ?: "Add some expenses to get personalized AI insights.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // ── Category Breakdown Chart ───────────────────────────────
                Text(
                    text  = "Spending by Category",
                    style = MaterialTheme.typography.titleLarge,
                    color = OnBackground
                )

                Spacer(Modifier.height(12.dp))

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    if (uiState.categoryBreakdown.isEmpty()) {
                        Text(
                            text  = "No expenses yet this month. Start adding your spending!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceVariant
                        )
                    } else {
                        CategoryBarChart(data = uiState.categoryBreakdown)
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick  = onViewAllExpenses,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape    = RoundedCornerShape(12.dp)
                    ) {
                        Text("View All Expenses")
                    }
                    Button(
                        onClick  = onAddExpense,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = OnPrimary)
                    ) {
                        Text("+ Add Expense")
                    }
                }

                Spacer(Modifier.height(12.dp))

                OutlinedButton(
                    onClick  = onScanReceipt,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = Secondary)
                ) {
                    Text("📷  Scan Receipt with AI")
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }

    // ── Budget Set Dialog ─────────────────────────────────────────────────
    if (showBudgetDialog) {
        var budgetInput by remember { mutableStateOf(if (uiState.monthlyBudget > 0) uiState.monthlyBudget.toInt().toString() else "") }

        AlertDialog(
            onDismissRequest = { showBudgetDialog = false },
            title   = { Text("Set Monthly Budget") },
            text    = {
                OutlinedTextField(
                    value = budgetInput,
                    onValueChange = { value ->
                        if (value.isEmpty() || value.matches(Regex("^\\d*$"))) {
                            budgetInput = value
                        }
                    },
                    label = { Text("Amount") },
                    leadingIcon = { Text("Rs.") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val value = budgetInput.toDoubleOrNull() ?: 0.0
                    viewModel.updateBudget(value)
                    showBudgetDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showBudgetDialog = false }) { Text("Cancel") }
            }
        )
    }
}

// ── Summary Card ──────────────────────────────────────────────────────────────
@Composable
private fun SummaryCard(
    modifier: Modifier = Modifier,
    label: String,
    amount: Double,
    emoji: String
) {
    GlassCard(modifier = modifier) {
        Text(text = emoji, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            text  = label,
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text  = formatCurrency(amount),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = OnBackground
        )
    }
}

// ── Simple Bar Chart (native Compose Canvas) ───────────────────────────────────
@Composable
private fun CategoryBarChart(data: List<CategorySpend>) {
    val maxAmount = data.maxOfOrNull { it.amount } ?: 1.0
    val colors = listOf(Primary, Secondary, Tertiary, PrimaryContainer, TertiaryContainer)

    Column(modifier = Modifier.fillMaxWidth()) {
        data.take(5).forEachIndexed { index, item ->
            val barFraction = (item.amount / maxAmount).toFloat().coerceIn(0.05f, 1f)
            val barColor = colors[index % colors.size]

            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text  = item.category,
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnBackground
                    )
                    Text(
                        text  = formatCurrency(item.amount),
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )
                }
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .background(SurfaceContainerHigh, RoundedCornerShape(6.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(barFraction)
                            .height(10.dp)
                            .background(barColor, RoundedCornerShape(6.dp))
                    )
                }
            }
        }
    }
}

// ── Currency Formatter ──────────────────────────────────────────────────────────
private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "PK"))
    format.maximumFractionDigits = 0
    return format.format(amount).replace("PKR", "Rs.")
}