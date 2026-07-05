// ============================================================
// FILE: presentation/onboarding/OnboardingScreen.kt
// ============================================================
package com.finpilotai.presentation.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.finpilotai.presentation.components.FinPilotButton
import com.finpilotai.ui.theme.*
import kotlinx.coroutines.launch

// ── Onboarding Data ────────────────────────────────────────────────────────────
data class OnboardingPage(
    val emoji: String,
    val title: String,
    val subtitle: String
)

val onboardingPages = listOf(
    OnboardingPage(
        emoji    = "🤖",
        title    = "AI-Powered Finance",
        subtitle = "Let FinPilot AI analyze your spending, detect anomalies, and give you personalized financial insights every day."
    ),
    OnboardingPage(
        emoji    = "📸",
        title    = "Scan & Attach Receipts",
        subtitle = "Take a quick photo of your physical receipts to digitally attach them to your expenses. Keep an organized, visual proof of all your spending without losing paperwork."
    ),
    OnboardingPage(
        emoji    = "📊", // Changed to graph for better budget tracking visual
        title    = "Track Your Budget",
        subtitle = "Set your monthly budget goals and track your expenses in real-time. Stay on top of your remaining balance and visual categories to grow your savings effortlessly."
    )
)

// ============================================================
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(initialPage = 0) { onboardingPages.size }
    val scope      = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == onboardingPages.lastIndex

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Skip button (top right)
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                if (!isLastPage) {
                    TextButton(
                        onClick = { viewModel.completeOnboarding(); onFinished() },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Text("Skip", color = OnSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            // Pager
            HorizontalPager(
                state    = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPage(page = onboardingPages[page])
            }

            // Dots indicator
            Row(
                modifier             = Modifier.padding(vertical = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment    = Alignment.CenterVertically
            ) {
                repeat(onboardingPages.size) { index ->
                    val isActive = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isActive) Primary else OutlineVariant)
                            .size(if (isActive) 10.dp else 6.dp)
                    )
                }
            }

            // CTA Button
            FinPilotButton(
                text      = if (isLastPage) "Get Started" else "Continue",
                onClick   = {
                    if (isLastPage) {
                        viewModel.completeOnboarding()
                        onFinished()
                    } else {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                },
                modifier  = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            )
        }
    }
}

// ── Single Onboarding Page ─────────────────────────────────────────────────────
@Composable
private fun OnboardingPage(page: OnboardingPage) {
    Column(
        modifier              = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment   = Alignment.CenterHorizontally,
        verticalArrangement   = Arrangement.Center
    ) {
        // Emoji inside glass card
        Box(
            modifier = Modifier
                .size(140.dp)
                .background(
                    color = SurfaceContainer,
                    shape = RoundedCornerShape(32.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = page.emoji, style = MaterialTheme.typography.displayLarge)
        }

        Spacer(Modifier.height(48.dp))

        Text(
            text      = page.title,
            style     = MaterialTheme.typography.headlineMedium,
            color     = OnBackground,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text      = page.subtitle,
            style     = MaterialTheme.typography.bodyLarge,
            color     = OnSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}