package com.aviatorpredictorai.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aviatorpredictorai.model.AppThemeMode
import com.aviatorpredictorai.model.DataMode
import com.aviatorpredictorai.ui.theme.CyanAccent
import com.aviatorpredictorai.ui.theme.CyanBorder
import com.aviatorpredictorai.ui.theme.CyanPrimary
import com.aviatorpredictorai.ui.theme.LossRed
import com.aviatorpredictorai.ui.theme.NeonGreenResult
import com.aviatorpredictorai.ui.theme.WinGreen

@Composable
fun AppHeader(
    dataMode: DataMode,
    isStreaming: Boolean,
    isLiveWsConnected: Boolean = false,
    latencyMs: Long = 0L,
    currentTheme: AppThemeMode = AppThemeMode.DARK_NAVY_GREEN,
    onCycleTheme: () -> Unit = {},
    onToggleStreaming: () -> Unit,
    onToggleLive: () -> Unit = {},
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
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                ),
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
            ),
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // Main Top Bar: Logo + Brand + Action Icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Brand: Stylized Red Airplane Logo + Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onOpenLiveFeed() }
                ) {
                    // Stylized Red Supersonic Airplane
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(LossRed, Color(0xFFB91C1C))
                                )
                            )
                            .border(1.2.dp, Color(0xFFFF8A80), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(24.dp)) {
                            val path = Path().apply {
                                moveTo(size.width * 0.92f, size.height * 0.08f) // Nose (pointing up-right)
                                lineTo(size.width * 0.55f, size.height * 0.45f)
                                lineTo(size.width * 0.12f, size.height * 0.40f) // Left wingtip
                                lineTo(size.width * 0.35f, size.height * 0.62f)
                                lineTo(size.width * 0.28f, size.height * 0.92f) // Tail bottom
                                lineTo(size.width * 0.48f, size.height * 0.78f)
                                lineTo(size.width * 0.72f, size.height * 0.88f) // Tail right
                                lineTo(size.width * 0.62f, size.height * 0.58f)
                                lineTo(size.width * 0.92f, size.height * 0.08f)
                                close()
                            }
                            drawPath(path, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "AVIATOR PREDICTOR",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.8.sp,
                                    fontSize = 14.5.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                    .border(0.8.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "AI ANALYST",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // AI Chip + Tagline
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 1.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Memory,
                                contentDescription = "AI Powered",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Smarter Analysis · Better Decisions",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 9.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }

                // Right Actions: Live Toggle + Theme Cycle + Settings
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // LIVE TOGGLE BUTTON
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isLiveWsConnected) WinGreen.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .border(
                                1.dp,
                                if (isLiveWsConnected) WinGreen
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { onToggleLive() }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("header_live_toggle"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isLiveWsConnected) WinGreen else MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isLiveWsConnected) "LIVE ON" else "LIVE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isLiveWsConnected) WinGreen else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Theme Cycler
                    IconButton(
                        onClick = onCycleTheme,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("theme_toggle_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Switch Theme: ${currentTheme.label}",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Settings
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("settings_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Open Settings",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Sub Header: Data Source Connection Indicator & Offset Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Connection Indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onOpenLiveFeed() }
                        .padding(vertical = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(
                                if (isLiveWsConnected) WinGreen
                                else if (isStreaming) CyanPrimary
                                else MaterialTheme.colorScheme.outline
                            )
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = when {
                            isLiveWsConnected -> "VERIFIED LIVE FEED (${latencyMs}ms)"
                            isStreaming -> "DEMO GENERATOR (ACTIVE)"
                            else -> "STANDBY (TAP TO CONNECT)"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = when {
                                isLiveWsConnected -> WinGreen
                                isStreaming -> CyanPrimary
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    )
                }

                // Theme badge
                Text(
                    text = currentTheme.label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}
