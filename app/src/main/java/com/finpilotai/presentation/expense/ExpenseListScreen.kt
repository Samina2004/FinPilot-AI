package com.finpilotai.presentation.expense

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.finpilotai.domain.model.Expense
import com.finpilotai.domain.model.ExpenseCategory
import com.finpilotai.presentation.components.finPilotTextFieldColors
import com.finpilotai.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExpenseListScreen(
    onBack: () -> Unit,
    onAddExpense: () -> Unit,
    onEditExpense: (Long) -> Unit,
    viewModel: ExpenseListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddExpense,
                containerColor = Primary,
                contentColor = OnPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(padding)
                .systemBarsPadding()
        ) {
            // ── Top Bar ───────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = OnBackground)
                }
                Text(
                    text  = "All Expenses",
                    style = MaterialTheme.typography.titleLarge,
                    color = OnBackground
                )
            }

            // ── Search Bar ────────────────────────────────────────────────
            OutlinedTextField(
                value         = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                modifier      = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                placeholder   = { Text("Search merchant or description") },
                leadingIcon   = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine    = true,
                shape  = RoundedCornerShape(12.dp),
                colors = finPilotTextFieldColors()
            )

            Spacer(Modifier.height(12.dp))

            // ── Category Filter Chips ────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.selectedCategory == null,
                    onClick  = { viewModel.onCategoryFilterChanged(null) },
                    label    = { Text("All") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Primary,
                        selectedLabelColor     = OnPrimary
                    )
                )
                ExpenseCategory.all.forEach { category ->
                    FilterChip(
                        selected = uiState.selectedCategory == category,
                        onClick  = { viewModel.onCategoryFilterChanged(category) },
                        label    = { Text("${ExpenseCategory.emojiFor(category)} $category") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            selectedLabelColor     = OnPrimary
                        )
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Expense List ──────────────────────────────────────────────
            val filtered = uiState.filteredExpenses

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if (filtered.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📭", style = MaterialTheme.typography.displayLarge)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text  = "No expenses found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = OnSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    items(filtered, key = { it.id }) { expense ->
                        ExpenseRow(
                            expense = expense,
                            onClick = { onEditExpense(expense.id) }
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

// ── Single Expense Row ──────────────────────────────────────────────────────
@Composable
private fun ExpenseRow(expense: Expense, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceContainerLowest)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            )
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(SurfaceContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(text = ExpenseCategory.emojiFor(expense.category), style = MaterialTheme.typography.titleLarge)
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text  = expense.merchant,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = OnBackground
            )
            Text(
                text  = "${expense.category} • ${formatExpenseDate(expense.date)}",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant
            )
        }

        Text(
            text  = formatExpenseCurrency(expense.amount),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = OnBackground
        )
    }
}

// ── Helpers ────────────────────────────────────────────────────────────────────
private fun formatExpenseDate(millis: Long): String {
    val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
    return sdf.format(Date(millis))
}

private fun formatExpenseCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "PK"))
    format.maximumFractionDigits = 0
    return format.format(amount).replace("PKR", "Rs.")
}