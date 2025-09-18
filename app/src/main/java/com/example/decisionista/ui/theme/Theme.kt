package com.example.decisionista.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = Indigo500,
    onPrimary = White,
    secondary = Emerald500,
    onSecondary = White,
    tertiary = Purple500,
    background = Gray10,
    onBackground = Gray900,
    surface = White,
    onSurface = Gray900,
    error = Red500,
    onError = White
)

private val DarkColors = darkColorScheme(
    primary = Indigo500,
    onPrimary = White,
    secondary = Green600,
    onSecondary = White,
    tertiary = Purple600,
    background = Gray900,
    onBackground = Gray100,
    surface = Gray800,
    onSurface = Gray100,
    error = Red100,
    onError = Black
)

@Composable
fun DecisionistaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as Activity).window
        window.statusBarColor = colorScheme.primary.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Typography.kt (puoi definire titoli, body ecc.)
        content = content
    )
}
