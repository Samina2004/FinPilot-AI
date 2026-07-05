// ============================================================
// FILE: app/src/main/java/com/finpilotai/ui/theme/Type.kt
// ============================================================
package com.finpilotai.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.finpilotai.R

// Inter font family
// NOTE: Add inter_regular.ttf, inter_medium.ttf, inter_semibold.ttf,
//       inter_bold.ttf, inter_extrabold.ttf to res/font/ folder
val InterFontFamily = FontFamily(
    Font(R.font.inter_regular,   FontWeight.Normal),
    Font(R.font.inter_medium,    FontWeight.Medium),
    Font(R.font.inter_semibold,  FontWeight.SemiBold),
    Font(R.font.inter_bold,      FontWeight.Bold),
    Font(R.font.inter_extrabold, FontWeight.ExtraBold),
)

// Design tokens → Material3 type scale mapping
// display-lg  : 57sp / 64 / W700 / -0.02em  → displayLarge
// headline-lg : 32sp / 40 / W600 / -0.01em  → headlineLarge
// title-lg    : 22sp / 28 / W500             → titleLarge
// body-lg     : 16sp / 24 / W400             → bodyLarge
// body-md     : 14sp / 20 / W400             → bodyMedium
// label-md    : 12sp / 16 / W600 / +0.5px   → labelMedium

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily   = InterFontFamily,
        fontWeight   = FontWeight.Bold,
        fontSize     = 57.sp,
        lineHeight   = 64.sp,
        letterSpacing = (-0.02).sp
    ),
    headlineLarge = TextStyle(
        fontFamily   = InterFontFamily,
        fontWeight   = FontWeight.SemiBold,
        fontSize     = 32.sp,
        lineHeight   = 40.sp,
        letterSpacing = (-0.01).sp
    ),
    headlineMedium = TextStyle(
        fontFamily   = InterFontFamily,
        fontWeight   = FontWeight.SemiBold,
        fontSize     = 28.sp,
        lineHeight   = 36.sp,
    ),
    titleLarge = TextStyle(
        fontFamily   = InterFontFamily,
        fontWeight   = FontWeight.Medium,
        fontSize     = 22.sp,
        lineHeight   = 28.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily   = InterFontFamily,
        fontWeight   = FontWeight.Normal,
        fontSize     = 16.sp,
        lineHeight   = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily   = InterFontFamily,
        fontWeight   = FontWeight.Normal,
        fontSize     = 14.sp,
        lineHeight   = 20.sp,
    ),
    labelMedium = TextStyle(
        fontFamily   = InterFontFamily,
        fontWeight   = FontWeight.SemiBold,
        fontSize     = 12.sp,
        lineHeight   = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily   = InterFontFamily,
        fontWeight   = FontWeight.Medium,
        fontSize     = 10.sp,
        lineHeight   = 14.sp,
        letterSpacing = 0.5.sp
    ),
)
