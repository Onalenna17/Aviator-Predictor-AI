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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aviatorpredictorai.model.AppThemeMode
import com.aviatorpredictorai.model.DataMode
import com.aviatorpredictorai.model.RiskProfile
import com.aviatorpredictorai.model.UserSettings
import com.aviatorpredictorai.ui.theme.CyanAccent
import com.aviatorpredictorai.ui.theme.CyanBorder
import com.aviatorpredictorai.ui.theme.CyanPrimary
import com.aviatorpredictorai.ui.theme.NeonYellowRing
import com.aviatorpredictorai.ui.theme.SurfaceBorder
import com.aviatorpredictorai.ui.theme.SurfaceContainer
import com.aviatorpredictorai.ui.theme.TextBrightYellow
import com.aviatorpredictorai.ui.theme.TextMuted
import com.aviatorpredictorai.ui.theme.TextPrimary
import com.aviatorpredictorai.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun QuickSettingsPanel(
    settings: UserSettings,
    onSaveSettings: (UserSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    var betAmount by remember(settings.betAmount) { mutableDoubleStateOf(settings.betAmount) }
    var collectMultiplier by remember(settings.collectMultiplier) { mutableDoubleStateOf(settings.collectMultiplier) }
    var analysisWindow by remember(settings.analysisWindow) { mutableIntStateOf(settings.analysisWindow) }
    var riskProfile by remember(settings.riskProfile) { mutableStateOf(settings.riskProfile) }
    var dataMode by remember(settings.dataMode) { mutableStateOf(settings.dataMode) }
    var themeMode by remember(settings.themeMode) { mutableStateOf(settings.themeMode) }
    var showSavedToast by remember { mutableStateOf(false) }

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
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Quick Settings",
                        tint = CyanPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "QUICK SETTINGS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                            color = TextSecondary
                        )
                    )
                }

                if (showSavedToast) {
                    Text(
                        text = "✓ Settings Saved",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4ADE80)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Bet Amount Stepper
            SettingStepper(
                label = "SIMULATED BET STAKE",
                valueDisplay = String.format(Locale.US, "$%.2f", betAmount),
                onDecrement = {
                    betAmount = (betAmount - 0.50).coerceIn(0.10, settings.spendingLimit)
                },
                onIncrement = {
                    betAmount = (betAmount + 0.50).coerceIn(0.10, settings.spendingLimit)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Collect Multiplier Stepper
            SettingStepper(
                label = "COLLECTION TARGET MULTIPLIER",
                valueDisplay = String.format(Locale.US, "%.2fx", collectMultiplier),
                onDecrement = {
                    collectMultiplier = (collectMultiplier - 0.05).coerceIn(1.05, 50.0)
                },
                onIncrement = {
                    collectMultiplier = (collectMultiplier + 0.05).coerceIn(1.05, 50.0)
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Analysis Window Selector (20, 50, 100, 200, 500)
            Text(
                text = "ANALYSIS WINDOW (ROUNDS)",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = TextMuted
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(20, 50, 100, 200).forEach { window ->
                    val isSelected = analysisWindow == window
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0xFF00557A) else Color(0xFF0D172A))
                            .border(0.8.dp, if (isSelected) CyanPrimary else SurfaceBorder, RoundedCornerShape(8.dp))
                            .clickable { analysisWindow = window }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${window}R",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Risk Profile Selector
            Text(
                text = "STRATEGY RISK PROFILE",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = TextMuted
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                RiskProfile.values().forEach { profile ->
                    val isSelected = riskProfile == profile
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0xFF152A4A) else Color(0xFF0D172A))
                            .border(0.8.dp, if (isSelected) CyanPrimary else SurfaceBorder, RoundedCornerShape(8.dp))
                            .clickable { riskProfile = profile }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) CyanPrimary else TextSecondary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = riskProfile.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 10.sp,
                    color = TextMuted,
                    lineHeight = 13.sp
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 5. Data Mode Selector
            Text(
                text = "DATA INGESTION MODE",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = TextMuted
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                DataMode.values().forEach { mode ->
                    val isSelected = dataMode == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0xFF242005) else Color(0xFF0D172A))
                            .border(0.8.dp, if (isSelected) NeonYellowRing else SurfaceBorder, RoundedCornerShape(8.dp))
                            .clickable { dataMode = mode }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mode.label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) TextBrightYellow else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 6. App Theme Selector (Default Light, Cyber Green, Dark Navy)
            Text(
                text = "APPLICATION THEME",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = TextMuted
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AppThemeMode.values().forEach { mode ->
                    val isSelected = themeMode == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) {
                                    when (mode) {
                                        AppThemeMode.LIGHT -> Color(0xFF0284C7)
                                        AppThemeMode.CYBER_GREEN -> Color(0xFF093E20)
                                        AppThemeMode.DARK_NAVY -> Color(0xFF152A4A)
                                    }
                                } else Color(0xFF0D172A)
                            )
                            .border(
                                0.8.dp,
                                if (isSelected) {
                                    when (mode) {
                                        AppThemeMode.LIGHT -> Color(0xFF38BDF8)
                                        AppThemeMode.CYBER_GREEN -> Color(0xFF00E676)
                                        AppThemeMode.DARK_NAVY -> CyanPrimary
                                    }
                                } else SurfaceBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { themeMode = mode }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mode.label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // APPLY SETTINGS BUTTON
            Button(
                onClick = {
                    val updated = settings.copy(
                        betAmount = betAmount,
                        collectMultiplier = collectMultiplier,
                        analysisWindow = analysisWindow,
                        riskProfile = riskProfile,
                        dataMode = dataMode,
                        themeMode = themeMode
                    )
                    onSaveSettings(updated)
                    showSavedToast = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("apply_settings_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanPrimary,
                    contentColor = Color(0xFF03101E)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "APPLY SETTINGS",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun SettingStepper(
    label: String,
    valueDisplay: String,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0A1222))
            .border(0.8.dp, SurfaceBorder, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    color = TextMuted
                )
            )
            Text(
                text = valueDisplay,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    color = CyanPrimary,
                    fontSize = 17.sp
                )
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF13233C))
                    .border(0.8.dp, CyanBorder, RoundedCornerShape(8.dp))
                    .clickable { onDecrement() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease",
                    tint = TextPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF13233C))
                    .border(0.8.dp, CyanBorder, RoundedCornerShape(8.dp))
                    .clickable { onIncrement() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase",
                    tint = TextPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
