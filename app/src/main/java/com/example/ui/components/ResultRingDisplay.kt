package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HistoricalRound
import com.example.model.PredictionAnalysis
import com.example.model.RiskProfile
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanBorder
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonGreenResult
import com.example.ui.theme.NeonYellowGlow
import com.example.ui.theme.NeonYellowRing
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.TextBrightYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextNeonGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WinGreen
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun ResultRingDisplay(
    latestRound: HistoricalRound?,
    analysisWindow: Int,
    predictionAnalysis: PredictionAnalysis?,
    riskProfile: RiskProfile,
    isAnalyzing: Boolean,
    modifier: Modifier = Modifier
) {
    // 0 = Next Prediction (default when algorithm runs), 1 = Last Confirmed Result
    var displayMode by remember { mutableIntStateOf(0) }

    // When algorithm finishes running or analysis updates, immediately focus on the Next Prediction!
    LaunchedEffect(predictionAnalysis?.generatedAt) {
        if (predictionAnalysis != null) {
            displayMode = 0
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "ring_glow")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweepAngle"
    )

    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    // Animated cycling number when analyzing
    var cyclingNumber by remember { mutableStateOf("1.85x") }
    LaunchedEffect(isAnalyzing) {
        if (isAnalyzing) {
            val randomList = listOf("1.42x", "2.10x", "1.75x", "3.05x", "1.92x", "2.40x")
            var idx = 0
            while (isAnalyzing) {
                cyclingNumber = randomList[idx % randomList.size]
                idx++
                delay(70)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mode Switcher: NEXT PREDICTION vs LAST CONFIRMED
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF0C1628))
                .border(1.dp, CyanBorder, RoundedCornerShape(12.dp))
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(9.dp))
                    .background(if (displayMode == 0) CyanAccent.copy(alpha = 0.35f) else Color.Transparent)
                    .clickable { displayMode = 0 }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = if (displayMode == 0) CyanPrimary else TextMuted,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "NEXT PREDICTION",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Black,
                        color = if (displayMode == 0) CyanPrimary else TextMuted
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(9.dp))
                    .background(if (displayMode == 1) Color(0xFF2A2306) else Color.Transparent)
                    .clickable { displayMode = 1 }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = if (displayMode == 1) TextBrightYellow else TextMuted,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "LAST CONFIRMED",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Black,
                        color = if (displayMode == 1) TextBrightYellow else TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Main Circular Ring Gauge
        Box(
            modifier = Modifier.size(240.dp),
            contentAlignment = Alignment.Center
        ) {
            // Background Canvas for glowing neon ring and animated trajectory sweep
            Canvas(modifier = Modifier.size(230.dp)) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = (size.minDimension / 2f) - 12.dp.toPx()

                // Glow ring
                val ringGlowColor = if (displayMode == 0) CyanPrimary else NeonYellowRing
                drawCircle(
                    color = ringGlowColor.copy(alpha = 0.22f * pulseGlow),
                    radius = radius + 10.dp.toPx(),
                    center = center
                )

                // Background track
                drawCircle(
                    color = Color(0xFF132034),
                    radius = radius,
                    center = center,
                    style = Stroke(width = 8.dp.toPx())
                )

                // Neon primary sweep arc
                drawArc(
                    brush = Brush.sweepGradient(
                        0.0f to if (displayMode == 0) CyanPrimary else NeonYellowRing,
                        0.7f to if (displayMode == 0) ElectricBlue else Color(0xFFFFEA75),
                        1.0f to if (displayMode == 0) CyanPrimary else NeonYellowRing
                    ),
                    startAngle = sweepAngle,
                    sweepAngle = 280f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                    style = Stroke(width = 8.5.dp.toPx(), cap = StrokeCap.Round)
                )

                // Highlight pulse head
                drawArc(
                    color = Color.White,
                    startAngle = (sweepAngle + 280f) % 360f,
                    sweepAngle = 30f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                    style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Inner Center Content
            Box(
                modifier = Modifier
                    .size(185.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF070C16))
                    .border(1.2.dp, if (displayMode == 0) CyanBorder else Color(0x44FFD600), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(8.dp)
                ) {
                    if (isAnalyzing) {
                        Text(
                            text = "CALCULATING...",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp,
                                fontSize = 9.sp,
                                color = CyanPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = cyclingNumber,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 38.sp,
                                color = CyanAccent
                            )
                        )
                        Text(
                            text = "QUANTILE PROJECTION",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 9.sp,
                                color = TextMuted
                            )
                        )
                    } else if (displayMode == 0) {
                        // PREDICTED NEXT TARGET
                        val targetMult = predictionAnalysis?.predictedNextPossibleMultiplier ?: (predictionAnalysis?.suggestedCollectMultiplier ?: 1.85)

                        Text(
                            text = "NEXT PREDICTED TARGET",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp,
                                fontSize = 8.5.sp,
                                color = CyanPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = String.format(Locale.US, "%.2fx", targetMult),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 40.sp,
                                color = NeonGreenResult,
                                letterSpacing = (-1).sp
                            )
                        )

                        predictionAnalysis?.let { pa ->
                            Text(
                                text = "PROBABILITY · ${String.format(Locale.US, "%.1f%%", pa.estimatedReachRate * 100)}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.8.sp,
                                    color = TextNeonGreen
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "RANGE: ${pa.predictedRangeLow}x - ${pa.predictedRangeHigh}x",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 8.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextMuted
                                )
                            )
                        } ?: run {
                            Text(
                                text = "CLICK RUN ALGORITHM",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 9.sp,
                                    color = TextMuted
                                )
                            )
                        }
                    } else {
                        // LAST CONFIRMED RESULT
                        Text(
                            text = "LAST CONFIRMED RESULT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                fontSize = 8.5.sp,
                                color = TextBrightYellow
                            )
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        if (latestRound != null) {
                            Text(
                                text = String.format(Locale.US, "%.2fx", latestRound.crashMultiplier),
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 38.sp,
                                    color = if (latestRound.crashMultiplier >= 2.0) NeonGreenResult else Color.White,
                                    letterSpacing = (-1).sp
                                )
                            )

                            Text(
                                text = "WINDOW · ${analysisWindow}R",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    letterSpacing = 1.sp,
                                    color = CyanPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "${latestRound.roundId} · ${latestRound.source.name}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextMuted
                                )
                            )
                        } else {
                            Text(
                                text = "Waiting...",
                                style = MaterialTheme.typography.titleMedium.copy(color = TextMuted)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Distinct Suggested Strategy & Reach Rate Card
        Surface(
            color = SurfaceContainer,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyanBorder, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = "Strategy",
                            tint = CyanPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "EMPIRICAL STRATEGY TARGET",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.8.sp,
                                color = TextPrimary
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0F2C3D))
                            .border(0.8.dp, CyanAccent, RoundedCornerShape(6.dp))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = riskProfile.label.uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (predictionAnalysis != null && predictionAnalysis.status == "success") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "CALIBRATED TARGET",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    color = TextMuted
                                )
                            )
                            Text(
                                text = String.format(Locale.US, "%.2fx", predictionAnalysis.suggestedCollectMultiplier),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    color = CyanPrimary,
                                    fontSize = 28.sp
                                )
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "EMPIRICAL REACH RATE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    color = TextMuted
                                )
                            )
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = String.format(Locale.US, "%.1f%%", predictionAnalysis.estimatedReachRate * 100),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = TextNeonGreen
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "(N=${predictionAnalysis.sampleSize})",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = TextSecondary
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Wilson CI & EV breakdown bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0A1220))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "95% Wilson CI: [${String.format(Locale.US, "%.1f%%", predictionAnalysis.confidenceIntervalLow * 100)}, ${String.format(Locale.US, "%.1f%%", predictionAnalysis.confidenceIntervalHigh * 100)}]",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        )

                        val ev = predictionAnalysis.expectedValuePerUnitStake
                        val evSign = if (ev > 0) "+" else ""
                        Text(
                            text = "EV: ${evSign}${String.format(Locale.US, "%.2f", ev)}/u",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = if (ev >= 0) WinGreen else TextMuted
                            )
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F1B2F))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Click RUN ALGORITHM to calculate next predicted target and empirical probability.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
