// ============================================================
// FILE: presentation/splash/SplashScreen.kt
// ============================================================
package com.finpilotai.presentation.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.finpilotai.R
import com.finpilotai.ui.theme.*

@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val destination by viewModel.destination.collectAsStateWithLifecycle()

    LaunchedEffect(destination) {
        when (destination) {
            is SplashDestination.Onboarding -> onNavigateToOnboarding()
            is SplashDestination.Dashboard  -> onNavigateToDashboard()
            else -> Unit
        }
    }

    // Animate entrance
    val scale by animateFloatAsState(
        targetValue   = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label         = "logo_scale"
    )
    val alpha by animateFloatAsState(
        targetValue   = 1f,
        animationSpec = tween(durationMillis = 800),
        label         = "logo_alpha"
    )

    // Float animation
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_offset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors  = listOf(BrandPrimary.copy(alpha = 0.9f), BrandPrimary),
                    radius  = 1200f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Ambient glow behind logo
        Box(
            modifier = Modifier
                .size(200.dp)
                .blur(80.dp)
                .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(100.dp))
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .scale(scale)
                .alpha(alpha)
                .offset(y = floatOffset.dp)
        ) {
            // Logo box
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(Color.White, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Use a Material Icon as fallback
                // When you have a custom logo: Image(painter = painterResource(R.drawable.ic_logo), ...)
                Text(
                    text  = "FP",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Primary
                )
            }

            Spacer(Modifier.height(16.dp))

            // App name
            Text(
                text  = "FinPilot AI",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )

            // Tagline
            Text(
                text  = "Your Smart Finance Copilot",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.75f)
            )
        }
    }
}