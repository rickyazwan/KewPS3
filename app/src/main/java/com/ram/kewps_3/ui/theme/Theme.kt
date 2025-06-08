package com.ram.kewps_3.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = ModernBlue80,
    secondary = ModernIndigo80,
    tertiary = ModernGreen80,
    background = ModernGray900,
    surface = Color(0xFF1E293B),
    surfaceVariant = Color(0xFF334155),
    onPrimary = ModernGray900,
    onSecondary = ModernGray900,
    onTertiary = ModernGray900,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = ModernBlue,
    secondary = ModernIndigo,
    tertiary = ModernGreen,
    background = Color.White,
    surface = ModernGray50,
    surfaceVariant = ModernGray100,
    primaryContainer = ModernBlue80.copy(alpha = 0.2f),
    secondaryContainer = ModernIndigo80.copy(alpha = 0.2f),
    tertiaryContainer = ModernGreen80.copy(alpha = 0.2f),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = ModernGray900,
    onSurface = ModernGray900,
    onSurfaceVariant = ModernGray600
)

@Composable
fun KewPS3Theme(
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
        typography = Typography,
        content = content
    )
}