package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanBorder
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.WinGreen

data class NavItemSpec(
    val label: String,
    val icon: ImageVector,
    val index: Int,
    val hasLiveBadge: Boolean = false
)

@Composable
fun FloatingBottomNav(
    selectedIndex: Int,
    onSelectIndex: (Int) -> Unit,
    isLiveWsActive: Boolean = false,
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        NavItemSpec("Terminal", Icons.Default.Dashboard, 0),
        NavItemSpec("Live WS", Icons.Default.Sensors, 1, hasLiveBadge = isLiveWsActive),
        NavItemSpec("Rounds", Icons.Default.TableChart, 2),
        NavItemSpec("Analytics", Icons.Default.Analytics, 3),
        NavItemSpec("Backtest", Icons.Default.Shield, 4),
        NavItemSpec("Settings", Icons.Default.Tune, 5)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        // Floating Pill Container
        Row(
            modifier = Modifier
                .widthIn(max = 500.dp)
                .fillMaxWidth()
                .height(64.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(32.dp),
                    ambientColor = MaterialTheme.colorScheme.primary,
                    spotColor = MaterialTheme.colorScheme.primary
                )
                .clip(RoundedCornerShape(32.dp))
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.94f)
                )
                .border(
                    width = 1.2.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    ),
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { item ->
                val isSelected = selectedIndex == item.index

                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.08f else 1.0f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "scale"
                )

                val itemBgColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.22f) else Color.Transparent,
                    label = "bgColor"
                )

                val iconColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    label = "iconColor"
                )

                val textColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    label = "textColor"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .background(itemBgColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onSelectIndex(item.index) }
                        .scale(scale)
                        .testTag("nav_item_${item.label.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(contentAlignment = Alignment.TopEnd) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = iconColor,
                                modifier = Modifier.size(20.dp)
                            )
                            if (item.hasLiveBadge) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(WinGreen)
                                        .border(0.8.dp, Color.Black, CircleShape)
                                )
                            }
                        }

                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                color = textColor
                            )
                        )
                    }
                }
            }
        }
    }
}
