package com.example.ui.theme

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
    primary = AtivaCyanLight,
    onPrimary = Color(0xFF003554),
    primaryContainer = AtivaCyanContainer,
    onPrimaryContainer = Color(0xFFCBE6FF),
    secondary = Color(0xFF94A3B8),
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF334155),
    onSecondaryContainer = Color(0xFFE2E8F0),
    tertiary = AtivaSuccessEmerald,
    onTertiary = Color(0xFF003822),
    tertiaryContainer = AtivaSuccessContainer,
    onTertiaryContainer = Color(0xFFA7F3D0),
    background = AtivaDarkBackground,
    onBackground = Color(0xFFF1F5F9),
    surface = AtivaDarkSurface,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = AtivaDarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = AtivaDarkBorder,
    error = AtivaCoralRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = AtivaCyanPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = Color(0xFF475569),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF1F5F9),
    onSecondaryContainer = Color(0xFF1E293B),
    tertiary = AtivaSuccessEmeraldDark,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD1FAE5),
    onTertiaryContainer = Color(0xFF065F46),
    background = AtivaLightBackground,
    onBackground = Color(0xFF0F172A),
    surface = AtivaLightSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = AtivaLightSurfaceVariant,
    onSurfaceVariant = Color(0xFF334155),
    outline = AtivaLightBorder,
    error = AtivaCoralRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our sleek custom automotive theme
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
