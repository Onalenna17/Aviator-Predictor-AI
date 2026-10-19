package com.aviatorpredictorai.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.aviatorpredictorai.model.AppThemeMode

// 1. DEFAULT: Dark Navy with Radiant Green Rims
private val DarkNavyGreenColorScheme = darkColorScheme(
    primary = Color(0xFF00E676),
    onPrimary = Color(0xFF012411),
    primaryContainer = Color(0xFF063A1F),
    onPrimaryContainer = Color(0xFFB9FCD2),
    secondary = Color(0xFF38BDF8),
    onSecondary = Color(0xFF002235),
    secondaryContainer = Color(0xFF0E2E44),
    onSecondaryContainer = Color(0xFFBAE6FD),
    tertiary = Color(0xFF00FF88),
    onTertiary = Color(0xFF002812),
    tertiaryContainer = Color(0xFF054522),
    onTertiaryContainer = Color(0xFFB5FED3),
    background = Color(0xFF080C16),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF0D1627),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF13233C),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF1E3250),
    outlineVariant = Color(0x6600E676),
    error = Color(0xFFEF4444),
    onError = Color.White
)

// 2. Pristine Light & Emerald
private val PristineLightColorScheme = lightColorScheme(
    primary = Color(0xFF059669),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = Color(0xFF064E3B),
    secondary = Color(0xFF0284C7),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF0369A1),
    tertiary = Color(0xFF0D9488),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFCCFBF1),
    onTertiaryContainer = Color(0xFF115E59),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF334155),
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0x66059669),
    error = Color(0xFFDC2626),
    onError = Color.White
)

// 3. Matrix Emerald Green
private val CyberGreenColorScheme = darkColorScheme(
    primary = Color(0xFF00E676),
    onPrimary = Color(0xFF012111),
    primaryContainer = Color(0xFF093E20),
    onPrimaryContainer = Color(0xFFB4FAD4),
    secondary = Color(0xFFA3E635),
    onSecondary = Color(0xFF132800),
    secondaryContainer = Color(0xFF1F4308),
    onSecondaryContainer = Color(0xFFE0FFB3),
    tertiary = Color(0xFF10B981),
    onTertiary = Color(0xFF002914),
    background = Color(0xFF040E08),
    onBackground = Color(0xFFF0FDF4),
    surface = Color(0xFF081C10),
    onSurface = Color(0xFFF0FDF4),
    surfaceVariant = Color(0xFF0E2818),
    onSurfaceVariant = Color(0xFF86EFAC),
    outline = Color(0xFF163E25),
    outlineVariant = Color(0x6600E676),
    error = Color(0xFFEF4444),
    onError = Color.White
)

// 4. Cyber Navy & Cyan
private val DarkNavyCyanColorScheme = darkColorScheme(
    primary = Color(0xFF00F2FE),
    onPrimary = Color(0xFF03101E),
    primaryContainer = Color(0xFF063A4D),
    onPrimaryContainer = Color(0xFFC7F8FE),
    secondary = Color(0xFF3B82F6),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFF162D50),
    onSecondaryContainer = Color(0xFFD6E4FF),
    tertiary = Color(0xFFFFD600),
    onTertiary = Color(0xFF231B00),
    tertiaryContainer = Color(0xFF3E3100),
    onTertiaryContainer = Color(0xFFFFEFA7),
    background = Color(0xFF080C16),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF0D1424),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF131D31),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF1E2D4A),
    outlineVariant = Color(0x4D00F2FE),
    error = Color(0xFFEF4444),
    onError = Color.White
)

// 5. Obsidian & Gold
private val ObsidianGoldColorScheme = darkColorScheme(
    primary = Color(0xFFF59E0B),
    onPrimary = Color(0xFF261500),
    primaryContainer = Color(0xFF452600),
    onPrimaryContainer = Color(0xFFFDE68A),
    secondary = Color(0xFFFBBF24),
    onSecondary = Color(0xFF251500),
    secondaryContainer = Color(0xFF3A2300),
    onSecondaryContainer = Color(0xFFFEF3C7),
    tertiary = Color(0xFFFFD700),
    onTertiary = Color(0xFF281C00),
    background = Color(0xFF090A0E),
    onBackground = Color(0xFFFFFBEB),
    surface = Color(0xFF11131A),
    onSurface = Color(0xFFFFFBEB),
    surfaceVariant = Color(0xFF1A1C26),
    onSurfaceVariant = Color(0xFFFDE68A),
    outline = Color(0xFF302B1D),
    outlineVariant = Color(0x66F59E0B),
    error = Color(0xFFEF4444),
    onError = Color.White
)

// 6. Crimson Terminal
private val CrimsonTerminalColorScheme = darkColorScheme(
    primary = Color(0xFFFF2E63),
    onPrimary = Color(0xFF26000B),
    primaryContainer = Color(0xFF4D0017),
    onPrimaryContainer = Color(0xFFFFD1DC),
    secondary = Color(0xFFFB7185),
    onSecondary = Color(0xFF2B000E),
    secondaryContainer = Color(0xFF48061B),
    onSecondaryContainer = Color(0xFFFFE4E6),
    tertiary = Color(0xFFF43F5E),
    onTertiary = Color(0xFF26000B),
    background = Color(0xFF0F060A),
    onBackground = Color(0xFFFFF1F2),
    surface = Color(0xFF180A11),
    onSurface = Color(0xFFFFF1F2),
    surfaceVariant = Color(0xFF24101A),
    onSurfaceVariant = Color(0xFFFDA4AF),
    outline = Color(0xFF3D1625),
    outlineVariant = Color(0x66FF2E63),
    error = Color(0xFFEF4444),
    onError = Color.White
)

// 7. Royal Amethyst & Violet
private val RoyalAmethystColorScheme = darkColorScheme(
    primary = Color(0xFFA855F7),
    onPrimary = Color(0xFF1B0330),
    primaryContainer = Color(0xFF3B1261),
    onPrimaryContainer = Color(0xFFF3E8FF),
    secondary = Color(0xFFC084FC),
    onSecondary = Color(0xFF1F0438),
    secondaryContainer = Color(0xFF4C1D95),
    onSecondaryContainer = Color(0xFFEDE9FE),
    tertiary = Color(0xFFE879F9),
    onTertiary = Color(0xFF2E023D),
    background = Color(0xFF0B0716),
    onBackground = Color(0xFFFAF5FF),
    surface = Color(0xFF130D24),
    onSurface = Color(0xFFFAF5FF),
    surfaceVariant = Color(0xFF1D1438),
    onSurfaceVariant = Color(0xFFD8B4FE),
    outline = Color(0xFF362058),
    outlineVariant = Color(0x66A855F7),
    error = Color(0xFFEF4444),
    onError = Color.White
)

// 8. Slate Crisp Light
private val SlateLightColorScheme = lightColorScheme(
    primary = Color(0xFF2563EB),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E40AF),
    secondary = Color(0xFF0D9488),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCFBF1),
    onSecondaryContainer = Color(0xFF0F766E),
    tertiary = Color(0xFF4F46E5),
    onTertiary = Color.White,
    background = Color(0xFFF1F5F9),
    onBackground = Color(0xFF020617),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF020617),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF334155),
    outline = Color(0xFF94A3B8),
    outlineVariant = Color(0x662563EB),
    error = Color(0xFFDC2626),
    onError = Color.White
)

@Composable
fun AviatorPredictorTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK_NAVY_GREEN,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        AppThemeMode.DARK_NAVY_GREEN -> DarkNavyGreenColorScheme
        AppThemeMode.PRISTINE_LIGHT -> PristineLightColorScheme
        AppThemeMode.CYBER_GREEN -> CyberGreenColorScheme
        AppThemeMode.DARK_NAVY_CYAN -> DarkNavyCyanColorScheme
        AppThemeMode.OBSIDIAN_GOLD -> ObsidianGoldColorScheme
        AppThemeMode.CRIMSON_TERMINAL -> CrimsonTerminalColorScheme
        AppThemeMode.ROYAL_AMETHYST -> RoyalAmethystColorScheme
        AppThemeMode.SLATE_LIGHT -> SlateLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
