// ============================================================
// FILE: presentation/auth/register/RegisterScreen.kt
// ============================================================
package com.finpilotai.presentation.auth.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.finpilotai.presentation.components.*
import com.finpilotai.ui.theme.*

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState       by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarState  = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isSuccess) { if (uiState.isSuccess) onRegisterSuccess() }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarState.showSnackbar(it)
            viewModel.onEvent(RegisterEvent.ClearError)
        }
    }

    Scaffold(
        snackbarHost    = { FinPilotSnackbarHost(snackbarState) },
        containerColor  = Background,
        topBar          = {
            IconButton(
                onClick  = onNavigateToLogin,
                modifier = Modifier.padding(8.dp).systemBarsPadding()
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = OnBackground)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(BrandPrimary.copy(alpha = 0.08f), Background)
                    )
                )
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            Text(
                text  = "Create account",
                style = MaterialTheme.typography.headlineLarge,
                color = OnBackground
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text  = "Start your AI-powered finance journey",
                style = MaterialTheme.typography.bodyLarge,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                // Full Name
                OutlinedTextField(
                    value         = uiState.name,
                    onValueChange = { viewModel.onEvent(RegisterEvent.NameChanged(it)) },
                    label         = { Text("Full Name") },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(12.dp),
                    colors        = finPilotTextFieldColors(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(Modifier.height(16.dp))

                EmailField(
                    value         = uiState.email,
                    onValueChange = { viewModel.onEvent(RegisterEvent.EmailChanged(it)) },
                    imeAction     = ImeAction.Next
                )

                Spacer(Modifier.height(16.dp))

                PasswordField(
                    value         = uiState.password,
                    onValueChange = { viewModel.onEvent(RegisterEvent.PasswordChanged(it)) },
                    label         = "Password",
                    imeAction     = ImeAction.Next
                )

                Spacer(Modifier.height(16.dp))

                PasswordField(
                    value         = uiState.confirmPassword,
                    onValueChange = { viewModel.onEvent(RegisterEvent.ConfirmPasswordChanged(it)) },
                    label         = "Confirm Password",
                    imeAction     = ImeAction.Done,
                    onImeAction   = { viewModel.onEvent(RegisterEvent.RegisterClicked) }
                )

                Spacer(Modifier.height(24.dp))

                FinPilotButton(
                    text      = "Create Account",
                    onClick   = { viewModel.onEvent(RegisterEvent.RegisterClicked) },
                    isLoading = uiState.isLoading
                )
            }

            Spacer(Modifier.height(24.dp))

            Row(
                verticalAlignment      = Alignment.CenterVertically,
                horizontalArrangement  = Arrangement.Center
            ) {
                Text("Already have an account? ", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                TextButton(onClick = onNavigateToLogin, contentPadding = PaddingValues(0.dp)) {
                    Text("Sign in", color = Primary, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}
