package com.aviatorpredictorai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.aviatorpredictorai.model.SimulationSummary
import com.aviatorpredictorai.ui.theme.CyanAccent
import com.aviatorpredictorai.ui.theme.CyanBorder
import com.aviatorpredictorai.ui.theme.CyanPrimary
import com.aviatorpredictorai.ui.theme.LossRed
import com.aviatorpredictorai.ui.theme.NeonGreenResult
import com.aviatorpredictorai.ui.theme.NeonYellowRing
import com.aviatorpredictorai.ui.theme.SurfaceBorder
import com.aviatorpredictorai.ui.theme.SurfaceContainer
import com.aviatorpredictorai.ui.theme.TextBrightYellow
import com.aviatorpredictorai.ui.theme.TextMuted
import com.aviatorpredictorai.ui.theme.TextPrimary
import com.aviatorpredictorai.ui.theme.TextSecondary
import com.aviatorpredictorai.ui.theme.WinGreen
import java.util.Locale

@Composable
fun SimulationPanel(
    summary: SimulationSummary?,
    stake: Double,
    targetMultiplier: Double,
    onRerunSimulation: () -> Unit,
    onExportCsv: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SurfaceContainer,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header & Prominent Simulation Disclaimer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Simulator",
                        tint = CyanPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "STRATEGY BACKTEST SIMULATOR",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                            color = TextSecondary
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF231E05))
                        .border(0.8.dp, NeonYellowRing.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "VIRTUAL ONLY",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextBrightYellow
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Mandatory Disclaimer Label
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF0C1424))
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "HISTORICAL SIMULATION — NOT ACTUAL WAGERING",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp,
                        color = CyanAccent
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (summary == null || summary.totalRounds == 0) {
                Text(
                    text = "No historical rounds available to run simulation.",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                )
            } else {
                // Key Simulation Metric Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SimStatCard(
                        label = "NET PROFIT",
                        value = "${if (summary.netProfit >= 0) "+" else ""}${String.format(Locale.US, "$%.2f", summary.netProfit)}",
                        valueColor = if (summary.netProfit >= 0) WinGreen else LossRed,
                        modifier = Modifier.weight(1f)
                    )
                    SimStatCard(
                        label = "WIN RATE",
                        value = String.format(Locale.US, "%.1f%%", summary.winRate),
                        valueColor = if (summary.winRate >= 50.0) NeonGreenResult else TextBrightYellow,
                        modifier = Modifier.weight(1f)
                    )
                    SimStatCard(
                        label = "RETURN ON STAKE",
                        value = "${if (summary.returnOnStakePercentage >= 0) "+" else ""}${String.format(Locale.US, "%.1f%%", summary.returnOnStakePercentage)}",
                        valueColor = if (summary.returnOnStakePercentage >= 0) WinGreen else LossRed,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Breakdown Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0B1424))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    BreakdownColumn(
                        label = "WINS / LOSSES",
                        value = "${summary.wins}W / ${summary.losses}L"
                    )
                    BreakdownColumn(
                        label = "TOTAL STAKED",
                        value = String.format(Locale.US, "$%.2f", summary.totalStaked)
                    )
                    BreakdownColumn(
                        label = "MAX WIN STREAK",
                        value = "${summary.maxWinStreak} in a row"
                    )
                    BreakdownColumn(
                        label = "MAX LOSS STREAK",
                        value = "${summary.maxLossStreak} in a row"
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action buttons: Re-run & Export CSV
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onRerunSimulation,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("rerun_simulation_button"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = CyanPrimary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyanBorder)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Rerun",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "RERUN BACKTEST", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onExportCsv,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("export_simulation_button"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextBrightYellow
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonYellowRing.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = "Export CSV",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "EXPORT CSV", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SimStatCard(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF091120))
            .border(0.8.dp, SurfaceBorder, RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 8.5.sp,
                color = TextMuted
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                color = valueColor
            )
        )
    }
}

@Composable
private fun BreakdownColumn(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 8.sp,
                color = TextMuted
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = TextPrimary
            )
        )
    }
}
