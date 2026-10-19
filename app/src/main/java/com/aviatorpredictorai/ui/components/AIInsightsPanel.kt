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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aviatorpredictorai.model.PredictionAnalysis
import com.aviatorpredictorai.ui.theme.CyanAccent
import com.aviatorpredictorai.ui.theme.CyanBorder
import com.aviatorpredictorai.ui.theme.CyanPrimary
import com.aviatorpredictorai.ui.theme.ElectricBlue
import com.aviatorpredictorai.ui.theme.LossRed
import com.aviatorpredictorai.ui.theme.NeonGreenResult
import com.aviatorpredictorai.ui.theme.NeonYellowRing
import com.aviatorpredictorai.ui.theme.SurfaceBorder
import com.aviatorpredictorai.ui.theme.SurfaceContainer
import com.aviatorpredictorai.ui.theme.TextBrightYellow
import com.aviatorpredictorai.ui.theme.TextMuted
import com.aviatorpredictorai.ui.theme.TextNeonGreen
import com.aviatorpredictorai.ui.theme.TextPrimary
import com.aviatorpredictorai.ui.theme.TextSecondary
import com.aviatorpredictorai.ui.theme.WinGreen
import java.util.Locale

@Composable
fun AIInsightsPanel(
    analysis: PredictionAnalysis?,
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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Analysis",
                        tint = CyanPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AI ANALYSIS & INSIGHTS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                            color = TextSecondary
                        )
                    )
                }

                if (analysis != null) {
                    Text(
                        text = "${analysis.calculationTimeMs}ms execution",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = TextMuted
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (analysis == null || analysis.status != "success") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F1B2F))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Run algorithm to generate empirical probability insights and risk parameters.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Monitored target reach rates grid
                    Text(
                        text = "EMPIRICAL MULTIPLIER REACH RATES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.5.sp,
                            color = CyanAccent
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val keyTargets = analysis.reachRates.filter { it.targetMultiplier in listOf(1.20, 1.50, 1.85, 2.00, 3.00) }
                        keyTargets.forEach { reach ->
                            ReachRatePill(
                                target = reach.targetMultiplier,
                                rate = reach.rate,
                                ciLow = reach.confidenceIntervalLow,
                                ciHigh = reach.confidenceIntervalHigh,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Descriptive metrics summary
                    analysis.descriptiveStats?.let { stats ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0C1424))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MetricStatItem(label = "SAMPLE MEAN", value = String.format(Locale.US, "%.2fx", stats.meanMultiplier))
                            MetricStatItem(label = "MEDIAN (P50)", value = String.format(Locale.US, "%.2fx", stats.medianMultiplier))
                            MetricStatItem(label = "IQR (P75-P25)", value = String.format(Locale.US, "%.2fx", stats.p75 - stats.p25))
                            MetricStatItem(label = "MAX OBSERVED", value = String.format(Locale.US, "%.2fx", stats.maxMultiplier))
                        }
                    }

                    // Clustering Insight
                    if (analysis.consecutiveLowCrashes >= 2) {
                        InsightCard(
                            icon = Icons.Default.Warning,
                            iconTint = LossRed,
                            title = "Low Crash Clustering Alert",
                            message = "${analysis.consecutiveLowCrashes} consecutive recent rounds crashed below 1.50x. While rounds are independent, drawdowns in crash games can cluster in small samples."
                        )
                    } else {
                        InsightCard(
                            icon = Icons.Default.CheckCircle,
                            iconTint = WinGreen,
                            title = "Distribution Stability",
                            message = "Observed sample distribution adheres to inverse-CDF characteristics with empirical median at ${String.format(Locale.US, "%.2fx", analysis.descriptiveStats?.medianMultiplier ?: 1.8)}."
                        )
                    }

                    // Provably fair statistical disclaimer
                    InsightCard(
                        icon = Icons.Default.Info,
                        iconTint = TextBrightYellow,
                        title = "Mathematical Transparency Notice",
                        message = "Crash results are generated via independent cryptographic seeds. Historical reach rates show sample frequency, NOT deterministic future predictions."
                    )
                }
            }
        }
    }
}

@Composable
private fun ReachRatePill(
    target: Double,
    rate: Double,
    ciLow: Double,
    ciHigh: Double,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0B1424))
            .border(0.8.dp, Color(0xFF1E2D4A), RoundedCornerShape(8.dp))
            .padding(vertical = 6.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${target}x",
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp,
                color = CyanPrimary
            )
        )
        Text(
            text = String.format(Locale.US, "%.0f%%", rate * 100),
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (rate >= 0.6) TextNeonGreen else TextBrightYellow
            )
        )
        Text(
            text = "±${String.format(Locale.US, "%.0f%%", (ciHigh - ciLow) * 50)}",
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 8.sp,
                color = TextMuted
            )
        )
    }
}

@Composable
private fun MetricStatItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 8.5.sp,
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

@Composable
private fun InsightCard(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    message: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF091120))
            .border(0.6.dp, iconTint.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = iconTint
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 10.5.sp,
                    color = TextSecondary,
                    lineHeight = 14.sp
                )
            )
        }
    }
}
