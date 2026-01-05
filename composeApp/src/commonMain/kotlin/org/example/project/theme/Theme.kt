package org.example.project.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CoralOrange,
    secondary = NavyLight,
    background = DarkBackground,
    surface = NavyBlue,
    onPrimary = White,
    onSecondary = White,
    onBackground = White,
    onSurface = White
)

private val LightColorScheme = lightColorScheme(
    primary = NavyBlue,
    secondary = CoralOrange,
    background = LightBackground,
    surface = White,
    onPrimary = White,
    onSecondary = White,
    onBackground = NavyBlue,
    onSurface = NavyBlue
)

@Composable
fun GuardianXTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
