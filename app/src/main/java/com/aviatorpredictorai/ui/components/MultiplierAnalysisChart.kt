package com.aviatorpredictorai.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aviatorpredictorai.core.StatisticsEngine
import com.aviatorpredictorai.model.HistoricalRound
import com.aviatorpredictorai.model.MultiplierBucket
import com.aviatorpredictorai.ui.theme.CyanAccent
import com.aviatorpredictorai.ui.theme.CyanBorder
import com.aviatorpredictorai.ui.theme.CyanPrimary
import com.aviatorpredictorai.ui.theme.ElectricBlue
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
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min

@Composable
fun MultiplierAnalysisChart(
    rounds: List<HistoricalRound>,
    targetMultiplier: Double,
    modifier: Modifier = Modifier
) {
    var selectedFilterCount by remember { mutableIntStateOf(50) }
    var useLogScale by remember { mutableStateOf(false) }
    var activeTab by remember { mutableIntStateOf(0) } // 0 = Trajectory, 1 = Histogram
    var scrubIndex by remember { mutableIntStateOf(-1) }

    Surface(
        color = SurfaceContainer,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header with Chart Mode Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (activeTab == 0) Icons.Default.ShowChart else Icons.Default.BarChart,
                        contentDescription = "Analysis Chart",
                        tint = CyanPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "MULTIPLIER ANALYSIS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                            color = TextSecondary
                        )
                    )
                }

                // Tab Switcher (Timeline vs Distribution)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F1B2F))
                        .border(0.8.dp, CyanBorder, RoundedCornerShape(8.dp))
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (activeTab == 0) CyanAccent.copy(alpha = 0.3f) else Color.Transparent)
                            .clickable { activeTab = 0 }
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "TIMELINE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeTab == 0) CyanPrimary else TextMuted
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (activeTab == 1) CyanAccent.copy(alpha = 0.3f) else Color.Transparent)
                            .clickable { activeTab = 1 }
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "DISTRIBUTION",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeTab == 1) CyanPrimary else TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Filters & Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Filter chips: 20, 50, 100, All
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(20, 50, 100, 0).forEach { count ->
                        val label = if (count == 0) "ALL" else "${count}R"
                        val isSelected = selectedFilterCount == count
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) Color(0xFF00557A) else Color(0xFF0E1A2D))
                                .border(0.8.dp, if (isSelected) CyanPrimary else SurfaceBorder, RoundedCornerShape(6.dp))
                                .clickable { selectedFilterCount = count }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else TextMuted
                            )
                        }
                    }
                }

                // Log scale toggle
                if (activeTab == 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "LOG SCALE",
                            fontSize = 9.5.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = if (useLogScale) TextBrightYellow else TextMuted
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Switch(
                            checked = useLogScale,
                            onCheckedChange = { useLogScale = it },
                            modifier = Modifier.size(32.dp),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = TextBrightYellow,
                                checkedTrackColor = Color(0xFF42370A),
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = Color(0xFF1E293B)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Chart area
            val filteredRounds = remember(rounds, selectedFilterCount) {
                if (selectedFilterCount == 0 || selectedFilterCount >= rounds.size) {
                    rounds
                } else {
                    rounds.take(selectedFilterCount)
                }
            }

            if (filteredRounds.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No rounds available for charting", color = TextMuted)
                }
            } else if (activeTab == 0) {
                // Time-series Trajectory Line Chart
                TrajectoryChartCanvas(
                    rounds = filteredRounds,
                    targetMultiplier = targetMultiplier,
                    useLogScale = useLogScale,
                    scrubIndex = scrubIndex,
                    onScrubIndexChange = { scrubIndex = it }
                )
            } else {
                // Multiplier Distribution Histogram
                HistogramCanvas(rounds = filteredRounds)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Chart Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    LegendItem(color = CyanPrimary, label = "Crash Point")
                    LegendItem(color = ElectricBlue, label = "Rolling Avg", isDashed = true)
                    LegendItem(color = NeonYellowRing, label = "Target (${targetMultiplier}x)", isDashed = true)
                }
                Text(
                    text = "Historical reference only",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 9.sp,
                        color = TextMuted
                    )
                )
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String, isDashed: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(width = 12.dp, height = if (isDashed) 2.dp else 4.dp)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 9.sp,
            color = TextSecondary,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun TrajectoryChartCanvas(
    rounds: List<HistoricalRound>,
    targetMultiplier: Double,
    useLogScale: Boolean,
    scrubIndex: Int,
    onScrubIndexChange: (Int) -> Unit
) {
    // Chronological order: oldest to newest (left to right)
    val chronoRounds = remember(rounds) { rounds.reversed() }
    val multipliers = remember(chronoRounds) { chronoRounds.map { it.crashMultiplier } }
    val rollingMeans = remember(multipliers) { StatisticsEngine.calculateRollingMean(multipliers, 8) }

    val rawMax = multipliers.maxOrNull() ?: 10.0
    val rawMin = 1.0

    // Transform functions for scaling
    fun scaleY(value: Double, height: Float): Float {
        val padding = 20f
        val usableHeight = height - padding * 2
        return if (useLogScale) {
            val logMin = ln(1.0)
            val logMax = ln(max(5.0, rawMax))
            val logVal = ln(max(1.0, value))
            val ratio = ((logVal - logMin) / (logMax - logMin)).toFloat().coerceIn(0f, 1f)
            height - padding - (ratio * usableHeight)
        } else {
            val effectiveMax = min(rawMax, 30.0) // compress extreme spikes if linear
            val ratio = ((value - rawMin) / (effectiveMax - rawMin)).toFloat().coerceIn(0f, 1f)
            height - padding - (ratio * usableHeight)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF070D18))
            .border(0.8.dp, Color(0xFF16233B), RoundedCornerShape(10.dp))
            .pointerInput(chronoRounds) {
                detectTapGestures { offset ->
                    val xRatio = (offset.x / size.width).coerceIn(0f, 1f)
                    val idx = (xRatio * (chronoRounds.size - 1)).toInt()
                    onScrubIndexChange(idx)
                }
            }
            .pointerInput(chronoRounds) {
                detectDragGestures { change, _ ->
                    val xRatio = (change.position.x / size.width).coerceIn(0f, 1f)
                    val idx = (xRatio * (chronoRounds.size - 1)).toInt()
                    onScrubIndexChange(idx)
                }
            }
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val width = size.width
            val height = size.height

            // Grid lines
            val gridLevels = if (useLogScale) listOf(1.0, 2.0, 5.0, 10.0, 50.0) else listOf(1.0, 2.0, 5.0, 10.0, 20.0)
            for (level in gridLevels) {
                if (level <= rawMax || level <= 10.0) {
                    val y = scaleY(level, height)
                    drawLine(
                        color = Color(0xFF142033),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1f
                    )
                }
            }

            // Target multiplier reference line (Neon Yellow dashed)
            val targetY = scaleY(targetMultiplier, height)
            drawLine(
                color = NeonYellowRing.copy(alpha = 0.8f),
                start = Offset(0f, targetY),
                end = Offset(width, targetY),
                strokeWidth = 1.8f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )

            if (chronoRounds.size >= 2) {
                val dx = width / (chronoRounds.size - 1).toFloat()

                // 1. Shaded area under multipliers curve
                val fillPath = Path()
                fillPath.moveTo(0f, height)
                fillPath.lineTo(0f, scaleY(multipliers[0], height))

                for (i in 1 until multipliers.size) {
                    val x = i * dx
                    val y = scaleY(multipliers[i], height)
                    fillPath.lineTo(x, y)
                }
                fillPath.lineTo(width, height)
                fillPath.close()

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            CyanPrimary.copy(alpha = 0.35f),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = height
                    )
                )

                // 2. Main crash multipliers line
                val linePath = Path()
                linePath.moveTo(0f, scaleY(multipliers[0], height))
                for (i in 1 until multipliers.size) {
                    val x = i * dx
                    val y = scaleY(multipliers[i], height)
                    linePath.lineTo(x, y)
                }
                drawPath(
                    path = linePath,
                    color = CyanPrimary,
                    style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round)
                )

                // 3. Rolling average line (Electric Blue dashed)
                if (rollingMeans.isNotEmpty()) {
                    val rollPath = Path()
                    rollPath.moveTo(0f, scaleY(rollingMeans[0], height))
                    for (i in 1 until rollingMeans.size) {
                        val x = i * dx
                        val y = scaleY(rollingMeans[i], height)
                        rollPath.lineTo(x, y)
                    }
                    drawPath(
                        path = rollPath,
                        color = ElectricBlue,
                        style = Stroke(
                            width = 1.8.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                        )
                    )
                }

                // 4. Scrubber cursor & point if selected
                if (scrubIndex in chronoRounds.indices) {
                    val scrubX = scrubIndex * dx
                    val scrubY = scaleY(multipliers[scrubIndex], height)

                    drawLine(
                        color = TextBrightYellow,
                        start = Offset(scrubX, 0f),
                        end = Offset(scrubX, height),
                        strokeWidth = 1.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                    )

                    drawCircle(
                        color = TextBrightYellow,
                        radius = 5.dp.toPx(),
                        center = Offset(scrubX, scrubY)
                    )
                }
            }
        }

        // Floating tooltip for scrubber
        if (scrubIndex in chronoRounds.indices) {
            val round = chronoRounds[scrubIndex]
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F1B2F))
                    .border(1.dp, CyanPrimary, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${round.roundId} · ${String.format(Locale.US, "%.2fx", round.crashMultiplier)}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = CyanPrimary
                    )
                )
            }
        }
    }
}

@Composable
private fun HistogramCanvas(rounds: List<HistoricalRound>) {
    val buckets = remember(rounds) { StatisticsEngine.calculateDistributionBuckets(rounds) }
    val maxPct = buckets.maxOfOrNull { it.percentage } ?: 100.0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF070D18))
            .border(0.8.dp, Color(0xFF16233B), RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        buckets.forEach { bucket ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = bucket.label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = TextSecondary
                    ),
                    modifier = Modifier.width(78.dp)
                )

                // Bar
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF10192A))
                ) {
                    val ratio = (bucket.percentage / max(1.0, maxPct)).toFloat().coerceIn(0f, 1f)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = max(0.02f, ratio))
                            .height(14.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF0077B6),
                                        CyanPrimary
                                    )
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "${String.format(Locale.US, "%.1f", bucket.percentage)}% (${bucket.count})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp,
                        color = TextPrimary
                    ),
                    modifier = Modifier.width(62.dp)
                )
            }
        }
    }
}
