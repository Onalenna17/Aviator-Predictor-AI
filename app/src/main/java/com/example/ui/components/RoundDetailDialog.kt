package com.example.ui.components

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.HistoricalRound
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanBorder
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DeepNavySurface
import com.example.ui.theme.LossRed
import com.example.ui.theme.NeonGreenResult
import com.example.ui.theme.NeonYellowRing
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.TextBrightYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WinGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RoundDetailDialog(
    round: HistoricalRound,
    betAmount: Double,
    collectMultiplier: Double,
    onDismiss: () -> Unit
) {
    val isWin = round.crashMultiplier >= collectMultiplier
    val net = if (isWin) betAmount * (collectMultiplier - 1.0) else -betAmount
    val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(round.timestamp))

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = DeepNavySurface,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyanBorder, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ROUND INSPECTOR",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = CyanAccent
                            )
                        )
                        Text(
                            text = round.roundId,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Crash Multiplier Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF070E1A))
                        .border(1.dp, if (round.crashMultiplier >= 2.0) WinGreen.copy(alpha = 0.5f) else CyanBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "FINAL CRASH MULTIPLIER",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                color = TextMuted
                            )
                        )
                        Text(
                            text = String.format(Locale.US, "%.2fx", round.crashMultiplier),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 34.sp,
                                color = if (round.crashMultiplier >= 2.0) NeonGreenResult else Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Detail Items Grid
                DetailRow(label = "Data Source", value = round.source.name)
                DetailRow(label = "Round Status", value = round.status.name)
                DetailRow(label = "Timestamp (UTC)", value = dateStr)
                DetailRow(label = "Simulated Stake", value = String.format(Locale.US, "$%.2f", betAmount))
                DetailRow(label = "Simulated Collect Point", value = String.format(Locale.US, "%.2fx", collectMultiplier))

                // Outcome & Net
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Simulated Outcome",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isWin) Color(0xFF063A29) else Color(0xFF3B151A))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isWin) "WIN (${String.format(Locale.US, "+$%.2f", net)})" else "BUST (-$${String.format(Locale.US, "%.2f", betAmount)})",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isWin) WinGreen else LossRed
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color(0xFF03101E)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("DISMISS", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )
    }
}
