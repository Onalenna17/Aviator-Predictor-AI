package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanBorder
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.LossRed

@Composable
fun ActionButtons(
    isAnalyzing: Boolean,
    onRunAlgorithm: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "btn_glow")
    val glowElevation by infiniteTransition.animateFloat(
        initialValue = 4f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowElevation"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // RUN ALGORITHM Button: Prominent glowing turquoise/cyan button
        Box(
            modifier = Modifier
                .weight(2.2f)
                .height(54.dp)
                .shadow(
                    elevation = glowElevation.dp,
                    shape = RoundedCornerShape(14.dp),
                    ambientColor = CyanPrimary,
                    spotColor = CyanAccent
                )
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF00E5FF),
                            Color(0xFF00B4D8),
                            Color(0xFF0077B6)
                        )
                    )
                )
                .border(1.2.dp, Color(0xFFC7F8FE), RoundedCornerShape(14.dp))
                .clickable(enabled = !isAnalyzing) { onRunAlgorithm() }
                .testTag("run_algorithm_button"),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (isAnalyzing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color(0xFF03101E),
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "COMPUTING...",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp,
                            fontSize = 15.sp,
                            color = Color(0xFF03101E)
                        )
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Run",
                        tint = Color(0xFF03101E),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "RUN ALGORITHM",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp,
                            fontSize = 15.sp,
                            color = Color(0xFF03101E)
                        )
                    )
                }
            }
        }

        // STOP Button: Cyan-outlined button
        Box(
            modifier = Modifier
                .weight(1f)
                .height(54.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF0E1A2C))
                .border(1.5.dp, CyanAccent, RoundedCornerShape(14.dp))
                .clickable { onStop() }
                .testTag("stop_button"),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Stop",
                    tint = CyanPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "STOP",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = CyanPrimary
                    )
                )
            }
        }
    }
}
