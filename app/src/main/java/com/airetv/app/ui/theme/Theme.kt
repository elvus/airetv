package com.airetv.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

@OptIn(ExperimentalTvMaterial3Api::class)
private val AireTvColorScheme = darkColorScheme(
    primary = RedPrimary,
    onPrimary = TextPrimary,
    secondary = BluePrimary,
    onSecondary = TextPrimary,
    tertiary = GoldAccent,
    onTertiary = BackgroundDark,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = RedPrimary,
    onError = TextPrimary
)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AireTvTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AireTvColorScheme,
        content = content
    )
}
