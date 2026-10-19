package com.aviatorpredictorai.ui.components

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

@Composable
fun OffsetControls(
    currentOffsetSeconds: Long,
    onAdjustOffset: (Long) -> Unit,
    onResetOffset: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showExplanation by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
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
                            color = MaterialTheme.colorScheme.onSurface,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Offset Details",
                        tint = MaterialTheme.colorScheme.primary,
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
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .clickable { onResetOffset() }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                            .testTag("reset_offset_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Restore,
                                contentDescription = "Reset offset",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(end = 2.dp)
                            )
                            Text(
                                text = "RESET",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            if (showExplanation) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Calibrates timestamp drift between device clock and server round confirmation. Does not alter actual crash multipliers or falsify historical outcomes.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Offset Adjustment Buttons: −1m, −1s, 0s, +1s, +60s, +1m
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OffsetButton(label = "−1m", onClick = { onAdjustOffset(-60) }, modifier = Modifier.weight(1f))
                OffsetButton(label = "−1s", onClick = { onAdjustOffset(-1) }, modifier = Modifier.weight(1f))
                OffsetButton(label = "+1s", onClick = { onAdjustOffset(1) }, modifier = Modifier.weight(1f))
                OffsetButton(label = "+60s", onClick = { onAdjustOffset(60) }, modifier = Modifier.weight(1f))
                OffsetButton(label = "+1m", onClick = { onAdjustOffset(60) }, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun OffsetButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .testTag("offset_btn_${label.replace("+", "plus_").replace("−", "minus_").replace("-", "minus_")}"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}
