package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.model.AppThemeMode

private val DarkNavyColorScheme = darkColorScheme(
    primary = CyanPrimary,
    onPrimary = Color(0xFF03101E),
    primaryContainer = Color(0xFF063A4D),
    onPrimaryContainer = Color(0xFFC7F8FE),
    secondary = ElectricBlue,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFF162D50),
    onSecondaryContainer = Color(0xFFD6E4FF),
    tertiary = NeonYellowRing,
    onTertiary = Color(0xFF231B00),
    tertiaryContainer = Color(0xFF3E3100),
    onTertiaryContainer = Color(0xFFFFEFA7),
    background = DarkNavyBackground,
    onBackground = TextPrimary,
    surface = DeepNavySurface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceContainerNavy,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceBorderNavy,
    outlineVariant = CyanBorder,
    error = LossRed,
    onError = Color.White
)

private val CyberGreenColorScheme = darkColorScheme(
    primary = CyberGreenPrimary,
    onPrimary = Color(0xFF012111),
    primaryContainer = Color(0xFF093E20),
    onPrimaryContainer = Color(0xFFB4FAD4),
    secondary = CyberGreenLime,
    onSecondary = Color(0xFF132800),
    secondaryContainer = Color(0xFF1F4308),
    onSecondaryContainer = Color(0xFFE0FFB3),
    tertiary = NeonGreenResult,
    onTertiary = Color(0xFF002914),
    background = CyberGreenBackground,
    onBackground = Color(0xFFF0FDF4),
    surface = CyberGreenSurface,
    onSurface = Color(0xFFF0FDF4),
    surfaceVariant = SurfaceContainerGreen,
    onSurfaceVariant = Color(0xFF86EFAC),
    outline = SurfaceBorderGreen,
    outlineVariant = Color(0x6600E676),
    error = LossRed,
    onError = Color.White
)

private val DefaultLightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = Color.White,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = Color(0xFF0C4A6E),
    secondary = LightAccentGreen,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD1FAE5),
    onSecondaryContainer = Color(0xFF065F46),
    tertiary = Color(0xFF0D9488),
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = TextDarkPrimary,
    surface = LightSurface,
    onSurface = TextDarkPrimary,
    surfaceVariant = SurfaceContainerLight,
    onSurfaceVariant = TextDarkSecondary,
    outline = SurfaceBorderLight,
    outlineVariant = Color(0xFF94A3B8),
    error = LossRed,
    onError = Color.White
)

@Composable
fun AviatorPredictorTheme(
    themeMode: AppThemeMode = AppThemeMode.LIGHT,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        AppThemeMode.LIGHT -> DefaultLightColorScheme
        AppThemeMode.CYBER_GREEN -> CyberGreenColorScheme
        AppThemeMode.DARK_NAVY -> DarkNavyColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
