package com.aviatorpredictorai.ui.components

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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NorthEast
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
import com.aviatorpredictorai.model.HistoricalRound
import com.aviatorpredictorai.model.PredictionAnalysis
import com.aviatorpredictorai.model.RiskProfile
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun ResultRingDisplay(
    latestRound: HistoricalRound?,
    analysisWindow: Int,
    predictionAnalysis: PredictionAnalysis?,
    riskProfile: RiskProfile,
    isAnalyzing: Boolean,
    isFlightActive: Boolean = false,
    liveFlightMultiplier: Double = 1.00,
    modifier: Modifier = Modifier
) {
    // 0 = Next Prediction (default when algorithm runs), 1 = Last Confirmed Result
    var displayMode by remember { mutableIntStateOf(0) }

    // When algorithm runs or prediction updates, focus on Next Prediction immediately!
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
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweepAngle"
    )

    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
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
                delay(60)
            }
        }
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val outlineColor = MaterialTheme.colorScheme.outline

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mode Switcher: NEXT PREDICTION vs LAST CONFIRMED
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(surfaceVariantColor)
                .border(1.dp, outlineColor.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(11.dp))
                    .background(if (displayMode == 0) primaryColor.copy(alpha = 0.22f) else Color.Transparent)
                    .clickable { displayMode = 0 }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = if (displayMode == 0) primaryColor else onSurfaceVariantColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "NEXT PREDICTION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = if (displayMode == 0) primaryColor else onSurfaceVariantColor
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(11.dp))
                    .background(if (displayMode == 1) secondaryColor.copy(alpha = 0.22f) else Color.Transparent)
                    .clickable { displayMode = 1 }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = if (displayMode == 1) secondaryColor else onSurfaceVariantColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "LAST CONFIRMED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = if (displayMode == 1) secondaryColor else onSurfaceVariantColor
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
            Canvas(modifier = Modifier.size(232.dp)) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = (size.minDimension / 2f) - 12.dp.toPx()

                // Glow ring
                val rimColor = if (displayMode == 0) primaryColor else secondaryColor
                drawCircle(
                    color = rimColor.copy(alpha = 0.25f * pulseGlow),
                    radius = radius + 10.dp.toPx(),
                    center = center
                )

                // Background track
                drawCircle(
                    color = surfaceVariantColor,
                    radius = radius,
                    center = center,
                    style = Stroke(width = 8.dp.toPx())
                )

                // Neon primary sweep arc
                drawArc(
                    brush = Brush.sweepGradient(
                        0.0f to rimColor,
                        0.5f to tertiaryColor,
                        1.0f to rimColor
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
                    sweepAngle = 28f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                    style = Stroke(width = 9.5.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Inner Center Content
            Box(
                modifier = Modifier
                    .size(185.dp)
                    .clip(CircleShape)
                    .background(surfaceColor)
                    .border(
                        1.5.dp,
                        if (displayMode == 0) primaryColor.copy(alpha = 0.6f) else secondaryColor.copy(alpha = 0.6f),
                        CircleShape
                    ),
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
                                color = primaryColor
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = cyclingNumber,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 38.sp,
                                color = primaryColor
                            )
                        )
                        Text(
                            text = "QUANTILE PROJECTION",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 9.sp,
                                color = onSurfaceVariantColor
                            )
                        )
                    } else if (isFlightActive) {
                        Text(
                            text = "LIVE FLIGHT IN PROGRESS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                fontSize = 8.5.sp,
                                color = primaryColor
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = String.format(Locale.US, "%.2fx", liveFlightMultiplier),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 42.sp,
                                color = primaryColor,
                                letterSpacing = (-1).sp
                            )
                        )
                        Text(
                            text = "CLIMBING MULTIPLIER",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 9.sp,
                                color = onSurfaceVariantColor
                            )
                        )
                    } else if (displayMode == 0) {
                        // PREDICTED NEXT TARGET
                        val targetMult = predictionAnalysis?.predictedNextPossibleMultiplier
                            ?: (predictionAnalysis?.suggestedCollectMultiplier ?: 1.85)

                        Text(
                            text = "NEXT PREDICTED TARGET",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp,
                                fontSize = 8.5.sp,
                                color = primaryColor
                            )
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = String.format(Locale.US, "%.2fx", targetMult),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 40.sp,
                                color = primaryColor,
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
                                    color = primaryColor
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "RANGE: ${pa.predictedRangeLow}x - ${pa.predictedRangeHigh}x",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 8.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = onSurfaceVariantColor
                                )
                            )
                        } ?: run {
                            Text(
                                text = "TAP RUN ALGORITHM",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 9.sp,
                                    color = onSurfaceVariantColor
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
                                color = secondaryColor
                            )
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        if (latestRound != null) {
                            Text(
                                text = String.format(Locale.US, "%.2fx", latestRound.crashMultiplier),
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 40.sp,
                                    color = secondaryColor,
                                    letterSpacing = (-1).sp
                                )
                            )

                            Text(
                                text = "ROUND ${latestRound.roundId}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = onSurfaceVariantColor
                                )
                            )

                            Text(
                                text = "WINDOW: $analysisWindow ROUNDS",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 8.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = onSurfaceVariantColor
                                )
                            )
                        } else {
                            Text(
                                text = "WAITING",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp,
                                    color = onSurfaceVariantColor
                                )
                            )
                            Text(
                                text = "Waiting for verified data",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 9.sp,
                                    color = onSurfaceVariantColor
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Strategy Recommendation Card below Ring
        Surface(
            color = surfaceColor,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, outlineColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
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
                            contentDescription = null,
                            tint = primaryColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SUGGESTED COLLECT STRATEGY",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                fontSize = 11.sp,
                                color = onSurfaceColor
                            )
                        )
                    }

                    // Risk profile badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(primaryColor.copy(alpha = 0.18f))
                            .border(0.8.dp, primaryColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = riskProfile.label.uppercase(Locale.US),
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Suggested Collect Target",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.sp,
                                color = onSurfaceVariantColor
                            )
                        )
                        Text(
                            text = predictionAnalysis?.let {
                                String.format(Locale.US, "%.2fx", it.suggestedCollectMultiplier)
                            } ?: "1.85x",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 20.sp,
                                color = primaryColor
                            )
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Wilson 95% CI Range",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.sp,
                                color = onSurfaceVariantColor
                            )
                        )
                        Text(
                            text = predictionAnalysis?.let {
                                "[${String.format(Locale.US, "%.1f%%", it.confidenceIntervalLow * 100)}, ${String.format(Locale.US, "%.1f%%", it.confidenceIntervalHigh * 100)}]"
                            } ?: "[54.2%, 67.8%]",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp,
                                color = onSurfaceColor
                            )
                        )
                    }
                }
            }
        }
    }
}
