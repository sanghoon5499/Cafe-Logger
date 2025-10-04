package com.example.cafelogger.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val DarkBrown = Color(0xFF4A2B20)
val MediumBrown = Color(0xFF6A4B40)
val Cream = Color(0xFFF0EAE2)
val LightGray = Color(0xFFD3CFCB)
val TextColor = Color(0xFF1C1B1F)

private val DarkColorScheme = darkColorScheme(
    primary = MediumBrown,
    secondary = LightGray,
    background = Color(0xFF1C1B1F), // A dark background
    surface = Color(0xFF2C2A29),
    onPrimary = Color.White,
    onBackground = Cream,
    onSurface = Cream
)

private val LightColorScheme = lightColorScheme(
    primary = DarkBrown,
    secondary = MediumBrown,
    background = Cream,
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = TextColor,
    onSurface = TextColor

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun CafeLoggerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = WorkSansTypography,
        content = content
    )
}