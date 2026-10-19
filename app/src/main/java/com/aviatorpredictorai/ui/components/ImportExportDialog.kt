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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.aviatorpredictorai.core.CsvExporter
import com.aviatorpredictorai.model.HistoricalRound
import com.aviatorpredictorai.ui.theme.CyanAccent
import com.aviatorpredictorai.ui.theme.CyanBorder
import com.aviatorpredictorai.ui.theme.CyanPrimary
import com.aviatorpredictorai.ui.theme.DeepNavySurface
import com.aviatorpredictorai.ui.theme.LossRed
import com.aviatorpredictorai.ui.theme.NeonYellowRing
import com.aviatorpredictorai.ui.theme.SurfaceBorder
import com.aviatorpredictorai.ui.theme.SurfaceContainer
import com.aviatorpredictorai.ui.theme.TextBrightYellow
import com.aviatorpredictorai.ui.theme.TextMuted
import com.aviatorpredictorai.ui.theme.TextPrimary
import com.aviatorpredictorai.ui.theme.TextSecondary
import com.aviatorpredictorai.ui.theme.WinGreen

@Composable
fun ImportExportDialog(
    rounds: List<HistoricalRound>,
    onDismiss: () -> Unit,
    onImportCsv: (String) -> Pair<Int, Int>,
    onImportJson: (String) -> Pair<Int, Int>,
    onResetToDemo: () -> Unit,
    onClearAll: () -> Unit
) {
    var activeTab by remember { mutableIntStateOf(0) } // 0 = Import, 1 = Export, 2 = Manage
    var inputText by remember { mutableStateOf("") }
    var resultMessage by remember { mutableStateOf<String?>(null) }
    val clipboardManager = LocalClipboardManager.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = DeepNavySurface,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyanBorder, RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DATA INGESTION & EXPORT",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp,
                            color = CyanPrimary
                        )
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tabs: IMPORT, EXPORT, MANAGE
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0B1424))
                        .padding(2.dp)
                ) {
                    listOf("IMPORT", "EXPORT", "MANAGE").forEachIndexed { index, title ->
                        val isSelected = activeTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) CyanAccent.copy(alpha = 0.35f) else Color.Transparent)
                                .clickable { activeTab = index; resultMessage = null }
                                .padding(vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) CyanPrimary else TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                when (activeTab) {
                    0 -> {
                        // Import tab
                        Text(
                            text = "Paste CSV or JSON records. Example CSV format:\nRoundId,CrashMultiplier\n#RD-99001,2.45\n#RD-99002,1.20",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.5.sp,
                                color = TextSecondary,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 14.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("Paste CSV or JSON records here...", fontSize = 11.sp, color = TextMuted) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp),
                            textStyle = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = TextPrimary
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanPrimary,
                                unfocusedBorderColor = SurfaceBorder,
                                focusedContainerColor = Color(0xFF080D18),
                                unfocusedContainerColor = Color(0xFF080D18)
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (inputText.isNotBlank()) {
                                        val (total, added) = if (inputText.trim().startsWith("{") || inputText.trim().startsWith("[")) {
                                            onImportJson(inputText)
                                        } else {
                                            onImportCsv(inputText)
                                        }
                                        resultMessage = "Successfully imported $added new rounds (parsed $total records)."
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("submit_import_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color(0xFF03101E))
                            ) {
                                Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("PARSE & IMPORT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    1 -> {
                        // Export tab
                        val csv = remember(rounds) { CsvExporter.exportRoundsToCsv(rounds) }
                        Text(
                            text = "CSV Export (${rounds.size} rounds ready)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF080D18))
                                .border(0.8.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = csv.take(1200) + if (csv.length > 1200) "\n... [${csv.length - 1200} characters truncated]" else "",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = TextPrimary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(csv))
                                resultMessage = "CSV copied to clipboard (${rounds.size} rounds)!"
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("copy_csv_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color(0xFF03101E))
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("COPY CSV TO CLIPBOARD", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    2 -> {
                        // Manage Dataset
                        Text(
                            text = "Active Dataset: ${rounds.size} historical crash rounds loaded.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = {
                                onResetToDemo()
                                resultMessage = "Dataset reset to verified reference sample."
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextBrightYellow),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonYellowRing.copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("RESTORE DEFAULT DEMO DATASET", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = {
                                onClearAll()
                                resultMessage = "All historical round records cleared."
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = LossRed),
                            border = androidx.compose.foundation.BorderStroke(1.dp, LossRed.copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("CLEAR ALL LOCAL RECORDS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (resultMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0D251D))
                            .border(0.8.dp, WinGreen, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = resultMessage ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF6EE7B7),
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
