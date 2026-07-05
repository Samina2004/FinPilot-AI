package com.finpilotai.presentation.expense

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.finpilotai.domain.model.ExpenseCategory
import com.finpilotai.domain.model.PaymentMethod
import com.finpilotai.presentation.components.FinPilotButton
import com.finpilotai.presentation.components.FinPilotSnackbarHost
import com.finpilotai.presentation.components.GlassCard
import com.finpilotai.presentation.components.finPilotTextFieldColors
import com.finpilotai.ui.theme.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: AddExpenseViewModel = hiltViewModel()
) {
    val uiState         by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarState     = remember { SnackbarHostState() }
    var showDatePicker    by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSaved) { if (uiState.isSaved) onSaved() }
    LaunchedEffect(uiState.isDeleted) { if (uiState.isDeleted) onSaved() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost   = { FinPilotSnackbarHost(snackbarState) },
        containerColor = Background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .systemBarsPadding()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = OnBackground)
                }
                if (uiState.isEditMode) {
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Error)
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(BrandPrimary.copy(alpha = 0.08f), Background)
                    )
                )
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column {
                Spacer(Modifier.height(8.dp))

                Text(
                    text  = if (uiState.isEditMode) "Edit Expense" else "Add Expense",
                    style = MaterialTheme.typography.headlineLarge,
                    color = OnBackground
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = "Track your spending easily",
                    style = MaterialTheme.typography.bodyLarge,
                    color = OnSurfaceVariant
                )

                Spacer(Modifier.height(24.dp))

                GlassCard(modifier = Modifier.fillMaxWidth()) {

                    // ── Amount ──────────────────────────────────────────────
                    Text("Amount", style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value         = uiState.amount,
                        onValueChange = viewModel::onAmountChanged,
                        modifier      = Modifier.fillMaxWidth(),
                        placeholder   = { Text("0.00") },
                        leadingIcon   = { Text("Rs.", color = OnSurfaceVariant) },
                        isError       = uiState.amountError != null,
                        singleLine    = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape  = RoundedCornerShape(12.dp),
                        colors = finPilotTextFieldColors()
                    )
                    if (uiState.amountError != null) {
                        Text(
                            text  = uiState.amountError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    // ── Category ────────────────────────────────────────────
                    Text("Category", style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    CategoryDropdown(
                        selected = uiState.category,
                        onSelect = viewModel::onCategoryChanged
                    )

                    Spacer(Modifier.height(20.dp))

                    // ── Merchant ────────────────────────────────────────────
                    Text("Merchant", style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value         = uiState.merchant,
                        onValueChange = viewModel::onMerchantChanged,
                        modifier      = Modifier.fillMaxWidth(),
                        placeholder   = { Text("e.g. McDonald's") },
                        singleLine    = true,
                        shape  = RoundedCornerShape(12.dp),
                        colors = finPilotTextFieldColors()
                    )

                    Spacer(Modifier.height(20.dp))

                    // ── Description ─────────────────────────────────────────
                    Text("Description", style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value         = uiState.description,
                        onValueChange = viewModel::onDescriptionChanged,
                        modifier      = Modifier.fillMaxWidth(),
                        placeholder   = { Text("Optional notes") },
                        minLines      = 2,
                        shape  = RoundedCornerShape(12.dp),
                        colors = finPilotTextFieldColors()
                    )

                    Spacer(Modifier.height(20.dp))

                    // ── Date ────────────────────────────────────────────────
                    Text("Date", style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainerLowest)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { showDatePicker = true }
                            .border(
                                width = 1.dp,
                                color = OutlineVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Text(
                            text  = formatDate(uiState.date),
                            color = OnBackground,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    // ── Payment Method ──────────────────────────────────────
                    Text("Payment Method", style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PaymentMethod.all.forEach { method ->
                            val selected = uiState.paymentMethod == method
                            FilterChip(
                                selected = selected,
                                onClick  = { viewModel.onPaymentMethodChanged(method) },
                                label    = { Text(method) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Primary,
                                    selectedLabelColor     = OnPrimary
                                )
                            )
                        }
                    }

                    // ── Receipt Image (scan se aaya ho to dikhao) ──────────
                    if (uiState.imagePath != null) {
                        Spacer(Modifier.height(20.dp))
                        Text(
                            "Receipt Proof",
                            style = MaterialTheme.typography.labelMedium,
                            color = OnSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))
                        AsyncImage(
                            model              = File(uiState.imagePath!!),
                            contentDescription = "Receipt proof",
                            modifier           = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = 1.dp,
                                    color = OutlineVariant,
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text  = "📎 Receipt attached as proof",
                            style = MaterialTheme.typography.labelMedium,
                            color = Secondary
                        )
                    }

                    Spacer(Modifier.height(28.dp))

                    FinPilotButton(
                        text      = if (uiState.isEditMode) "Update Expense" else "Save Expense",
                        onClick   = viewModel::saveExpense,
                        isLoading = uiState.isLoading
                    )
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }

    // ── Date Picker Dialog ─────────────────────────────────────────────────
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.date)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { viewModel.onDateChanged(it) }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // ── Delete Confirmation Dialog ─────────────────────────────────────────
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title   = { Text("Delete Expense?") },
            text    = { Text("This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    viewModel.deleteCurrentExpense()
                }) { Text("Delete", color = Error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

// ── Category Dropdown ─────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(selected: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = "${ExpenseCategory.emojiFor(selected)}  $selected",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape  = RoundedCornerShape(12.dp),
            colors = finPilotTextFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ExpenseCategory.all.forEach { category ->
                DropdownMenuItem(
                    text = { Text("${ExpenseCategory.emojiFor(category)}  $category") },
                    onClick = {
                        onSelect(category)
                        expanded = false
                    }
                )
            }
        }
    }
}

// ── Helpers ────────────────────────────────────────────────────────────────────
private fun formatDate(millis: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(millis))
}