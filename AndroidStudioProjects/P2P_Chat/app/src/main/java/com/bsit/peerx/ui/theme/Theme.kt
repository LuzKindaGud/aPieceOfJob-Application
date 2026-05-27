package com.bsit.peerx.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CyberColorScheme = darkColorScheme(
    primary = NeonPrimary,
    onPrimary = TextPrimary,
    secondary = NeonSecondary,
    onSecondary = TextPrimary,
    tertiary = NeonCyan,
    onTertiary = BgPrimary,
    background = BgPrimary,
    onBackground = TextPrimary,
    surface = BgSecondary,
    onSurface = TextPrimary,
    surfaceVariant = PanelBg,
    onSurfaceVariant = TextSecondary,
    outline = BorderNeon,
    outlineVariant = BorderNeonHover,
    error = NeonDanger,
    onError = TextPrimary
)

@Composable
fun PeerXTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CyberColorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}


