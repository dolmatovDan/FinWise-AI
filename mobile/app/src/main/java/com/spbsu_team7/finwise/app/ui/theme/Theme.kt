package com.spbsu_team7.finwise.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    secondary = md_theme_light_secondary,
    background = md_theme_light_background,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    outline = md_theme_light_outline,
    error = md_theme_light_error,
    primaryContainer = md_theme_light_primary_container,
    secondaryContainer = md_theme_light_secondary_container,
    surfaceVariant = md_theme_light_onSurface,
    onSurfaceVariant = md_theme_light_onSurface,
    tertiary = md_theme_light_primary,
    onTertiary = md_theme_light_primary,
    tertiaryContainer = md_theme_light_primary,
)

private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    secondary = md_theme_dark_secondary,
    background = md_theme_dark_background,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    outline = md_theme_dark_outline,
    error = md_theme_dark_error,
    primaryContainer = md_theme_dark_primary_container,
    secondaryContainer = md_theme_dark_secondary_container,
    surfaceVariant = md_theme_dark_onSurface,
    onSurfaceVariant = md_theme_dark_onSurface,
    tertiary = md_theme_dark_primary,
    onTertiary = md_theme_dark_primary,
    tertiaryContainer = md_theme_dark_primary,
)

@Composable
fun FinanceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors: ColorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = FinanceTypography,
        shapes = FinanceShapes,
        content = content,
    )
}
