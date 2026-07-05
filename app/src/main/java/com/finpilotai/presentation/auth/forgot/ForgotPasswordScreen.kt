// ============================================================
// FILE: presentation/auth/forgot/ForgotPasswordScreen.kt
// ============================================================
package com.finpilotai.presentation.auth.forgot

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val uiState       by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarState  = remember { SnackbarHostState() }

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
            IconButton(
                onClick  = onBack,
                modifier = Modifier.padding(8.dp).systemBarsPadding()
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = OnBackground)
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
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(targetState = uiState.isEmailSent, label = "forgot_state") { emailSent ->
                if (emailSent) {
                    // Success state
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("📧", style = MaterialTheme.typography.displayLarge)
                        Text(
                            text      = "Email sent!",
                            style     = MaterialTheme.typography.headlineLarge,
                            color     = OnBackground
                        )
                        Text(
                            text      = "Check your inbox at ${uiState.email}. Follow the link to reset your password.",
                            style     = MaterialTheme.typography.bodyLarge,
                            color     = OnSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(8.dp))
                        FinPilotButton(text = "Back to Login", onClick = onBack)
                    }
                } else {
                    // Input state
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text  = "Reset password",
                            style = MaterialTheme.typography.headlineLarge,
                            color = OnBackground
                        )
                        Text(
                            text      = "Enter your email and we'll send you a reset link",
                            style     = MaterialTheme.typography.bodyLarge,
                            color     = OnSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(24.dp))

                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            EmailField(
                                value         = uiState.email,
                                onValueChange = viewModel::onEmailChanged,
                                imeAction     = ImeAction.Done,
                                onImeAction   = viewModel::sendReset
                            )

                            Spacer(Modifier.height(24.dp))

                            FinPilotButton(
                                text      = "Send Reset Link",
                                onClick   = viewModel::sendReset,
                                isLoading = uiState.isLoading
                            )
                        }
                    }
                }
            }
        }
    }
}
