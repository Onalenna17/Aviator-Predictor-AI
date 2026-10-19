package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HistoricalRound
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanBorder
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.LossRed
import com.example.ui.theme.NeonGreenResult
import com.example.ui.theme.NeonYellowRing
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WinGreen
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecentMultiplierGrid(
    rounds: List<HistoricalRound>,
    onSelectRound: (HistoricalRound) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SurfaceContainer,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History",
                        tint = CyanAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "RECENT CRASH MULTIPLIERS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                            color = TextSecondary
                        )
                    )
                }

                Text(
                    text = "${rounds.size} TOTAL",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (rounds.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No historical crash rounds available in active dataset.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
            } else {
                // Show latest 18 rounds in a dense, beautiful badge grid
                val displayList = rounds.take(18)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    displayList.forEach { round ->
                        MultiplierBadge(
                            round = round,
                            onClick = { onSelectRound(round) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MultiplierBadge(
    round: HistoricalRound,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mult = round.crashMultiplier
    val (bgColor, textColor, borderColor) = when {
        mult >= 10.0 -> Triple(Color(0xFF381A4E), Color(0xFFF472B6), Color(0xFFC084FC)) // Super high neon purple/pink
        mult >= 3.0 -> Triple(Color(0xFF2C2405), NeonYellowRing, Color(0xFFFACC15))     // High gold
        mult >= 2.0 -> Triple(Color(0xFF063022), NeonGreenResult, Color(0xFF10B981))    // Medium-high green
        mult >= 1.5 -> Triple(Color(0xFF0F2642), CyanPrimary, Color(0xFF38BDF8))        // Medium cyan/blue
        else -> Triple(Color(0xFF201317), Color(0xFFF87171), Color(0xFF7F1D1D))         // Low bust red/slate
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(0.8.dp, borderColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 9.dp, vertical = 6.dp)
            .testTag("multiplier_badge_${round.roundId}"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = String.format(Locale.US, "%.2fx", mult),
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp,
                color = textColor
            )
        )
    }
}
