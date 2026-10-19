package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.service.LiveFeedState
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanBorder
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.LossRed
import com.example.ui.theme.NeonGreenResult
import com.example.ui.theme.NeonYellowRing
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.TextBrightYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextNeonGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WinGreen

@Composable
fun LiveFeedConsolePanel(
    feedState: LiveFeedState,
    onConnect: (String) -> Unit,
    onDisconnect: () -> Unit,
    onTriggerTestRound: () -> Unit,
    modifier: Modifier = Modifier
) {
    var endpointInput by remember(feedState.endpointUrl) { mutableStateOf(feedState.endpointUrl) }
    var showLogs by remember { mutableStateOf(false) }

    Surface(
        color = SurfaceContainer,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, if (feedState.isConnected) WinGreen.copy(alpha = 0.5f) else CyanBorder, RoundedCornerShape(16.dp))
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
                        imageVector = Icons.Default.Sensors,
                        contentDescription = "Live Feed",
                        tint = if (feedState.isConnected) WinGreen else CyanPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LIVE DATA SOURCE & WEBSOCKET",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                            color = TextSecondary
                        )
                    )
                }

                // Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (feedState.isConnected) Color(0xFF063A29) else Color(0xFF131F33))
                        .border(
                            0.8.dp,
                            if (feedState.isConnected) WinGreen else CyanBorder,
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (feedState.isConnected) WinGreen else if (feedState.isConnecting) NeonYellowRing else LossRed)
                        )
                        Text(
                            text = if (feedState.isConnected) "LIVE FEED ACTIVE" else if (feedState.isConnecting) "CONNECTING" else "OFFLINE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (feedState.isConnected) TextNeonGreen else if (feedState.isConnecting) TextBrightYellow else TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Endpoint input field
            OutlinedTextField(
                value = endpointInput,
                onValueChange = { endpointInput = it },
                label = { Text("WebSocket URL (wss:// or ws://)", fontSize = 11.sp, color = TextMuted) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("websocket_url_input"),
                textStyle = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.5.sp,
                    color = TextPrimary
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanPrimary,
                    unfocusedBorderColor = SurfaceBorder,
                    focusedContainerColor = Color(0xFF091120),
                    unfocusedContainerColor = Color(0xFF091120)
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Presets row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Presets:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        color = TextMuted
                    ),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF0F1B2F))
                        .border(0.6.dp, CyanBorder, RoundedCornerShape(4.dp))
                        .clickable { endpointInput = "wss://echo.websocket.events" }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("Echo Event WS", fontSize = 9.5.sp, color = CyanPrimary)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF0F1B2F))
                        .border(0.6.dp, CyanBorder, RoundedCornerShape(4.dp))
                        .clickable { endpointInput = "ws://10.0.2.2:8080/aviator" }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("Localhost Bridge", fontSize = 9.5.sp, color = CyanPrimary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Connect / Disconnect Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!feedState.isConnected) {
                    Button(
                        onClick = { onConnect(endpointInput) },
                        modifier = Modifier
                            .weight(1.4f)
                            .height(44.dp)
                            .testTag("connect_live_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyanPrimary,
                            contentColor = Color(0xFF03101E)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = "Connect",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (feedState.isConnecting) "CONNECTING..." else "CONNECT LIVE WS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = onDisconnect,
                        modifier = Modifier
                            .weight(1.4f)
                            .height(44.dp)
                            .testTag("disconnect_live_button"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = LossRed),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LossRed.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LinkOff,
                            contentDescription = "Disconnect",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "DISCONNECT FEED",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp
                        )
                    }
                }

                OutlinedButton(
                    onClick = onTriggerTestRound,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("send_packet_button"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextBrightYellow),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonYellowRing.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = "Send Test Packet",
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "TEST PACKET",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metrics telemetry bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0A1222))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LATENCY: ${if (feedState.isConnected) "${feedState.latencyMs}ms" else "--"}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.5.sp,
                        color = if (feedState.isConnected) TextNeonGreen else TextMuted
                    )
                )
                Text(
                    text = "PACKETS: ${feedState.messagesReceived}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.5.sp,
                        color = CyanPrimary
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showLogs = !showLogs }
                ) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = "Logs",
                        tint = CyanAccent,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (showLogs) "HIDE LOGS" else "SHOW LOGS",
                        fontSize = 9.5.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = CyanAccent
                    )
                }
            }

            // Real-time Event Log Console
            AnimatedVisibility(visible = showLogs) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF050912))
                        .border(0.8.dp, Color(0xFF142033), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "REAL-TIME WEBSOCKET TELEMETRY LOGS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.5.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (feedState.eventLogs.isEmpty()) {
                        Text(
                            text = "No events logged yet.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 9.5.sp,
                                color = TextMuted
                            )
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                        ) {
                            items(feedState.eventLogs.reversed()) { log ->
                                Text(
                                    text = log,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp,
                                        color = if (log.contains("✓") || log.contains("Inbound")) TextNeonGreen else if (log.contains("⚠️")) LossRed else TextSecondary
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
