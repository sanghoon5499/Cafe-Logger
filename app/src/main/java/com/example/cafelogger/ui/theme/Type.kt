package com.example.cafelogger.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.cafelogger.R

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )

    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

val WorkSans = FontFamily(
    // Normal styles
    Font(R.font.work_sans_thin, FontWeight.Thin, FontStyle.Normal),
    Font(R.font.work_sans_extralight, FontWeight.ExtraLight, FontStyle.Normal),
    Font(R.font.work_sans_light, FontWeight.Light, FontStyle.Normal),
    Font(R.font.work_sans_regular, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.work_sans_medium, FontWeight.Medium, FontStyle.Normal),
    Font(R.font.work_sans_semibold, FontWeight.SemiBold, FontStyle.Normal),
    Font(R.font.work_sans_bold, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.work_sans_extrabold, FontWeight.ExtraBold, FontStyle.Normal),
    Font(R.font.work_sans_black, FontWeight.Black, FontStyle.Normal),

    // Italic styles
    Font(R.font.work_sans_thinitalic, FontWeight.Thin, FontStyle.Italic),
    Font(R.font.work_sans_extralightitalic, FontWeight.ExtraLight, FontStyle.Italic),
    Font(R.font.work_sans_lightitalic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.work_sans_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.work_sans_mediumitalic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.work_sans_semibolditalic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.work_sans_bolditalic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.work_sans_extrabolditalic, FontWeight.ExtraBold, FontStyle.Italic),
    Font(R.font.work_sans_blackitalic, FontWeight.Black, FontStyle.Italic)
)

// Get the default Material 3 typography definitions
private val workSansTypography = Typography()

// Create a new Typography object by copying the default one and overriding the fontFamily
val WorkSansTypography = Typography(
    displayLarge = workSansTypography.displayLarge.copy(fontFamily = WorkSans),
    displayMedium = workSansTypography.displayMedium.copy(fontFamily = WorkSans),
    displaySmall = workSansTypography.displaySmall.copy(fontFamily = WorkSans),
    headlineLarge = workSansTypography.headlineLarge.copy(fontFamily = WorkSans),
    headlineMedium = workSansTypography.headlineMedium.copy(fontFamily = WorkSans),
    headlineSmall = workSansTypography.headlineSmall.copy(fontFamily = WorkSans),
    titleLarge = workSansTypography.titleLarge.copy(fontFamily = WorkSans),
    titleMedium = workSansTypography.titleMedium.copy(fontFamily = WorkSans),
    titleSmall = workSansTypography.titleSmall.copy(fontFamily = WorkSans),
    bodyLarge = workSansTypography.bodyLarge.copy(fontFamily = WorkSans),
    bodyMedium = workSansTypography.bodyMedium.copy(fontFamily = WorkSans),
    bodySmall = workSansTypography.bodySmall.copy(fontFamily = WorkSans),
    labelLarge = workSansTypography.labelLarge.copy(fontFamily = WorkSans),
    labelMedium = workSansTypography.labelMedium.copy(fontFamily = WorkSans),
    labelSmall = workSansTypography.labelSmall.copy(fontFamily = WorkSans)
)