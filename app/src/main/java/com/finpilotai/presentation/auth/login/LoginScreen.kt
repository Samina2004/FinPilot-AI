// ============================================================
// FILE: presentation/auth/login/LoginScreen.kt
// ============================================================
package com.finpilotai.presentation.auth.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.finpilotai.presentation.components.*
import com.finpilotai.ui.theme.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState       by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarState  = remember { SnackbarHostState() }
    val context        = LocalContext.current

    // Navigate on success
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onLoginSuccess()
    }

    // Show errors in snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarState.showSnackbar(it)
            viewModel.onEvent(LoginEvent.ClearError)
        }
    }

    // Google Sign-In launcher
    val googleSignInLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { viewModel.onEvent(LoginEvent.GoogleSignIn(it)) }
        } catch (e: ApiException) {
            // handled by snackbar
        }
    }

    Scaffold(
        snackbarHost = { FinPilotSnackbarHost(snackbarState) },
        containerColor = Background
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
                .systemBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.height(48.dp))

                // Header
                Text(
                    text  = "Welcome back",
                    style = MaterialTheme.typography.headlineLarge,
                    color = OnBackground
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text  = "Sign in to your FinPilot account",
                    style = MaterialTheme.typography.bodyLarge,
                    color = OnSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(40.dp))

                // Glass card form
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    EmailField(
                        value         = uiState.email,
                        onValueChange = { viewModel.onEvent(LoginEvent.EmailChanged(it)) },
                        imeAction     = ImeAction.Next
                    )

                    Spacer(Modifier.height(16.dp))

                    PasswordField(
                        value         = uiState.password,
                        onValueChange = { viewModel.onEvent(LoginEvent.PasswordChanged(it)) },
                        imeAction     = ImeAction.Done,
                        onImeAction   = { viewModel.onEvent(LoginEvent.LoginClicked) }
                    )

                    Spacer(Modifier.height(8.dp))

                    // Forgot password
                    Box(modifier = Modifier.fillMaxWidth()) {
                        TextButton(
                            onClick  = onNavigateToForgotPassword,
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Text(
                                "Forgot password?",
                                color = Primary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    FinPilotButton(
                        text      = "Sign In",
                        onClick   = { viewModel.onEvent(LoginEvent.LoginClicked) },
                        isLoading = uiState.isLoading
                    )
// === GOOGLE SIGN-IN HIDDEN FOR NOW ===
                    /*
                    Spacer(Modifier.height(20.dp))
                    OrDivider()
                    Spacer(Modifier.height(20.dp))

                    // Google Sign In
                    FinPilotOutlinedButton(
                        text         = "Continue with Google",
                        onClick      = {
                            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken("270774883244-j2m1ju6tcc0t6f14niip9skgosclf72j.apps.googleusercontent.com") // Replace with your Web Client ID from google-services.json
                                .requestEmail()
                                .build()
                            val client = GoogleSignIn.getClient(context, gso)
                            googleSignInLauncher.launch(client.signInIntent)
                        }
                    )

                     */
                    // ======================================
                }

                Spacer(Modifier.height(32.dp))

                // Register link
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Don't have an account? ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )
                    TextButton(onClick = onNavigateToRegister, contentPadding = PaddingValues(0.dp)) {
                        Text(
                            "Sign up",
                            color = Primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}