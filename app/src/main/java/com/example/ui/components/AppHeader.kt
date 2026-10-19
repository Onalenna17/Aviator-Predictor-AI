package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Palette
import com.example.model.AppThemeMode
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.DataMode
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanBorder
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DeepNavySurface
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.LossRed
import com.example.ui.theme.NeonGreenResult
import com.example.ui.theme.NeonYellowRing
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.TextBrightYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextNeonGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WinGreen

@Composable
fun AppHeader(
    dataMode: DataMode,
    isStreaming: Boolean,
    isLiveWsConnected: Boolean = false,
    latencyMs: Long = 0L,
    currentTheme: AppThemeMode = AppThemeMode.LIGHT,
    onCycleTheme: () -> Unit = {},
    onToggleStreaming: () -> Unit,
    onOpenImportExport: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenLiveFeed: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ),
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
            ),
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo & Title block
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Stylized Red Airplane Logo
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF161F36))
                            .border(1.dp, CyanBorder, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.aviator_icon_1792358724469),
                            contentDescription = "Aviator Logo",
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "AVIATOR PREDICTOR",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.8.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF0F3B4A))
                                    .border(0.8.dp, CyanAccent, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "AI",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = CyanPrimary
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ANALYST",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp,
                                    color = NeonYellowRing
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Memory,
                                contentDescription = "AI Engine",
                                tint = CyanAccent,
                                modifier = Modifier
                                    .size(13.dp)
                                    .scale(pulseScale)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "v2.4 Statistical Core",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            )
                        }
                    }
                }

                // Header Action Buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Demo stream toggle
                    IconButton(
                        onClick = onToggleStreaming,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("toggle_stream_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isStreaming) Color(0x3310B981) else Color(0x2238BDF8))
                                .border(
                                    1.dp,
                                    if (isStreaming) WinGreen else CyanBorder,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isStreaming) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isStreaming) "Pause Demo Stream" else "Start Demo Stream",
                                tint = if (isStreaming) WinGreen else CyanPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Import / Export
                    IconButton(
                        onClick = onOpenImportExport,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("import_export_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0x2238BDF8))
                                .border(1.dp, CyanBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileUpload,
                                contentDescription = "Data Ingestion",
                                tint = CyanPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Theme Toggle
                    IconButton(
                        onClick = onCycleTheme,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("theme_toggle_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0x2238BDF8))
                                .border(1.dp, CyanBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Switch Theme: ${currentTheme.label}",
                                tint = when (currentTheme) {
                                    AppThemeMode.LIGHT -> TextBrightYellow
                                    AppThemeMode.CYBER_GREEN -> Color(0xFF00E676)
                                    AppThemeMode.DARK_NAVY -> CyanPrimary
                                },
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Settings
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("settings_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0x2238BDF8))
                                .border(1.dp, CyanBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Quick Settings",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sub-bar with Tagline & Status Connection Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "“Smarter Analysis · Better Decisions”",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                )

                // Connection pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F1A2E))
                        .border(
                            0.8.dp,
                            if (isLiveWsConnected) WinGreen else if (isStreaming) CyanPrimary else CyanBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onOpenLiveFeed() }
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isLiveWsConnected) WinGreen
                                    else if (isStreaming) CyanPrimary
                                    else NeonYellowRing
                                )
                                .scale(if (isLiveWsConnected || isStreaming) pulseScale else 1f)
                        )
                        Text(
                            text = when {
                                isLiveWsConnected -> "LIVE WS (${latencyMs}ms)"
                                isStreaming -> "DEMO STREAM"
                                else -> dataMode.label.uppercase()
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                color = if (isLiveWsConnected) TextNeonGreen else if (isStreaming) CyanPrimary else TextBrightYellow
                            )
                        )
                    }
                }
            }
        }
    }
}
