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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aviatorpredictorai.model.HistoricalRound
import com.aviatorpredictorai.model.RoundStatus
import com.aviatorpredictorai.ui.theme.CyanAccent
import com.aviatorpredictorai.ui.theme.CyanBorder
import com.aviatorpredictorai.ui.theme.CyanPrimary
import com.aviatorpredictorai.ui.theme.DeepNavySurface
import com.aviatorpredictorai.ui.theme.LossRed
import com.aviatorpredictorai.ui.theme.LossRedBackground
import com.aviatorpredictorai.ui.theme.NeonGreenResult
import com.aviatorpredictorai.ui.theme.SurfaceBorder
import com.aviatorpredictorai.ui.theme.SurfaceContainer
import com.aviatorpredictorai.ui.theme.TextBrightYellow
import com.aviatorpredictorai.ui.theme.TextMuted
import com.aviatorpredictorai.ui.theme.TextPrimary
import com.aviatorpredictorai.ui.theme.TextSecondary
import com.aviatorpredictorai.ui.theme.WinGreen
import com.aviatorpredictorai.ui.theme.WinGreenBackground
import java.util.Locale

@Composable
fun LiveRoundsTable(
    rounds: List<HistoricalRound>,
    betAmount: Double,
    collectMultiplier: Double,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    filterOutcome: String,
    onFilterOutcomeChange: (String) -> Unit,
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
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TableChart,
                        contentDescription = "Live Rounds Table",
                        tint = CyanPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LIVE ROUNDS TABLE",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                            color = TextSecondary
                        )
                    )
                }

                // Filter tabs: ALL, WINS, LOSSES
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0C1626))
                        .border(0.8.dp, CyanBorder, RoundedCornerShape(8.dp))
                        .padding(2.dp)
                ) {
                    listOf("ALL", "WIN", "LOSS").forEach { filter ->
                        val isSelected = filterOutcome == filter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) CyanAccent.copy(alpha = 0.35f) else Color.Transparent)
                                .clickable { onFilterOutcomeChange(filter) }
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = filter,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) CyanPrimary else TextMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF080D18))
                    .border(0.8.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Search by Round ID (e.g. 98421)...",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            )
                        }
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 12.sp,
                                color = TextPrimary,
                                fontFamily = FontFamily.Monospace
                            ),
                            cursorBrush = SolidColor(CyanPrimary),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchQueryChange("") },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = TextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Table Column Headers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF0F1B2F))
                    .padding(horizontal = 8.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ROUND ID",
                    style = tableHeaderStyle(),
                    modifier = Modifier.weight(1.3f)
                )
                Text(
                    text = "CRASH",
                    style = tableHeaderStyle(),
                    modifier = Modifier.weight(1.0f)
                )
                Text(
                    text = "TARGET",
                    style = tableHeaderStyle(),
                    modifier = Modifier.weight(1.0f)
                )
                Text(
                    text = "OUTCOME",
                    style = tableHeaderStyle(),
                    modifier = Modifier.weight(1.1f)
                )
                Text(
                    text = "NET",
                    style = tableHeaderStyle(),
                    modifier = Modifier.weight(1.0f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Filtered Rounds Rows
            val filtered = remember(rounds, searchQuery, filterOutcome, collectMultiplier) {
                rounds.filter { round ->
                    val matchesSearch = searchQuery.isBlank() ||
                            round.roundId.contains(searchQuery, ignoreCase = true)
                    val isWin = round.crashMultiplier >= collectMultiplier
                    val matchesOutcome = when (filterOutcome) {
                        "WIN" -> isWin
                        "LOSS" -> !isWin
                        else -> true
                    }
                    matchesSearch && matchesOutcome
                }
            }

            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No matching rounds found.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filtered, key = { it.roundId }) { round ->
                        val isWin = round.crashMultiplier >= collectMultiplier
                        val net = if (isWin) betAmount * (collectMultiplier - 1.0) else -betAmount

                        RoundTableRow(
                            round = round,
                            collectMultiplier = collectMultiplier,
                            isWin = isWin,
                            netResult = net,
                            onClick = { onSelectRound(round) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoundTableRow(
    round: HistoricalRound,
    collectMultiplier: Double,
    isWin: Boolean,
    netResult: Double,
    onClick: () -> Unit
) {
    val rowBg = if (isWin) WinGreenBackground else LossRedBackground
    val borderCol = if (isWin) WinGreen.copy(alpha = 0.3f) else LossRed.copy(alpha = 0.3f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(rowBg)
            .border(0.6.dp, borderCol, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Round ID
        Text(
            text = round.roundId,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = TextPrimary
            ),
            modifier = Modifier.weight(1.3f)
        )

        // Crash Multiplier
        Text(
            text = String.format(Locale.US, "%.2fx", round.crashMultiplier),
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 11.5.sp,
                color = if (round.crashMultiplier >= 2.0) NeonGreenResult else Color.White
            ),
            modifier = Modifier.weight(1.0f)
        )

        // Target Collect Multiplier
        Text(
            text = String.format(Locale.US, "%.2fx", collectMultiplier),
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = TextSecondary
            ),
            modifier = Modifier.weight(1.0f)
        )

        // Outcome Badge
        Box(
            modifier = Modifier.weight(1.1f),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isWin) Color(0xFF063A29) else Color(0xFF3B151A))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (isWin) "WIN" else "BUST",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isWin) WinGreen else LossRed
                )
            }
        }

        // Net Result
        val sign = if (netResult > 0) "+" else ""
        Text(
            text = "$sign${String.format(Locale.US, "$%.2f", netResult)}",
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = if (isWin) WinGreen else LossRed
            ),
            modifier = Modifier.weight(1.0f)
        )
    }
}

@Composable
private fun tableHeaderStyle() = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontWeight = FontWeight.Bold,
    fontSize = 9.5.sp,
    color = TextMuted,
    letterSpacing = 0.5.sp
)
