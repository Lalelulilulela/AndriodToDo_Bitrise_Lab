package com.demo.todolist.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val Teal = Color(0xFF0F6E6E)
private val TealContainer = Color(0xFFB7E6E1)
private val Ink = Color(0xFF1C1B19)
private val Paper = Color(0xFFF7F5F1)
private val Muted = Color(0xFF5E5A54)

private val LightColors = lightColorScheme(
    primary = Teal,
    onPrimary = Color.White,
    primaryContainer = TealContainer,
    onPrimaryContainer = Color(0xFF00201F),
    secondary = Color(0xFF4A635F),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCE8E3),
    onSecondaryContainer = Color(0xFF06201D),
    background = Paper,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = Color(0xFFE7E2D9),
    onSurfaceVariant = Muted,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF1EEE8),
    surfaceContainer = Color(0xFFEBE7E0),
    surfaceContainerHigh = Color(0xFFE4E0D8),
    outline = Color(0xFFC9C3B8),
    outlineVariant = Color(0xFFDED8CE),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8ED4CE),
    onPrimary = Color(0xFF003735),
    primaryContainer = Color(0xFF0F5555),
    onPrimaryContainer = TealContainer,
    secondary = Color(0xFFB0CCC7),
    onSecondary = Color(0xFF1C3532),
    secondaryContainer = Color(0xFF334B48),
    onSecondaryContainer = Color(0xFFCCE8E3),
    background = Color(0xFF121413),
    onBackground = Color(0xFFE6E2DB),
    surface = Color(0xFF121413),
    onSurface = Color(0xFFE6E2DB),
    surfaceVariant = Color(0xFF3F3A34),
    onSurfaceVariant = Color(0xFFCAC4BA),
    surfaceContainerLowest = Color(0xFF0D0F0E),
    surfaceContainerLow = Color(0xFF1A1C1B),
    surfaceContainer = Color(0xFF1E2120),
    surfaceContainerHigh = Color(0xFF292C2A),
    outline = Color(0xFF8E8980),
    outlineVariant = Color(0xFF45423C),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
)

private val AppTypography = Typography().run {
    copy(
        headlineMedium = headlineMedium.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 32.sp,
            letterSpacing = (-0.5).sp,
        ),
        titleMedium = titleMedium.copy(fontWeight = FontWeight.Medium),
        bodyLarge = bodyLarge.copy(
            fontSize = 17.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal,
        ),
        bodyMedium = bodyMedium.copy(lineHeight = 20.sp),
        labelLarge = labelLarge.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            letterSpacing = 0.sp,
        ),
    )
}

@Composable
fun TodoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        typography = AppTypography,
        content = content,
    )
}
