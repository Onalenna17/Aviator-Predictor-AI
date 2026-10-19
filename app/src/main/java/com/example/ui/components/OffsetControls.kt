package com.example.ui.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanBorder
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.TextBrightYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun OffsetControls(
    currentOffsetSeconds: Long,
    onAdjustOffset: (Long) -> Unit,
    onResetOffset: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showExplanation by remember { mutableStateOf(false) }

    Surface(
        color = SurfaceContainer,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showExplanation = !showExplanation }
                ) {
                    Text(
                        text = "Data Offset / Time Alignment",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Offset Details",
                        tint = CyanAccent,
                        modifier = Modifier.padding(2.dp)
                    )
                }

                // Current offset badge + reset button
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val sign = if (currentOffsetSeconds > 0) "+" else if (currentOffsetSeconds < 0) "" else "±"
                    Text(
                        text = "$sign${currentOffsetSeconds}s",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = if (currentOffsetSeconds != 0L) TextBrightYellow else CyanPrimary
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0F1A2D))
                            .border(0.8.dp, CyanBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    )

                    if (currentOffsetSeconds != 0L) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x33EF4444))
                                .clickable { onResetOffset() }
                                .padding(horizontal = 6.dp, vertical = 3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "RESET",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFCA5A5)
                            )
                        }
                    }
                }
            }

            if (showExplanation) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Calibrates network latency and timestamps between local telemetry and server feeds. Data offset never alters historical crash multipliers or fabricates upcoming results.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = TextMuted,
                        lineHeight = 14.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Buttons: -1m, -1s, Reset, +1s, +60s, +1m
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OffsetButton(
                    label = "−1m",
                    modifier = Modifier.weight(1f).testTag("offset_minus_1m")
                ) { onAdjustOffset(-60) }

                OffsetButton(
                    label = "−1s",
                    modifier = Modifier.weight(1f).testTag("offset_minus_1s")
                ) { onAdjustOffset(-1) }

                OffsetButton(
                    label = "0s",
                    isReset = true,
                    modifier = Modifier.weight(1f).testTag("offset_reset")
                ) { onResetOffset() }

                OffsetButton(
                    label = "+1s",
                    modifier = Modifier.weight(1f).testTag("offset_plus_1s")
                ) { onAdjustOffset(1) }

                OffsetButton(
                    label = "+60s",
                    modifier = Modifier.weight(1f).testTag("offset_plus_60s")
                ) { onAdjustOffset(60) }

                OffsetButton(
                    label = "+1m",
                    modifier = Modifier.weight(1f).testTag("offset_plus_1m")
                ) { onAdjustOffset(60) }
            }
        }
    }
}

@Composable
private fun OffsetButton(
    label: String,
    modifier: Modifier = Modifier,
    isReset: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isReset) Color(0xFF16233B) else Color(0xFF0F1B30))
            .border(0.8.dp, if (isReset) CyanAccent else SurfaceBorder, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = if (isReset) CyanPrimary else TextPrimary
            )
        )
    }
}
