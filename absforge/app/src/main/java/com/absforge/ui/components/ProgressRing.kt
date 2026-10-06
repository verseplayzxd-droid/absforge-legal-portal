package com.absforge.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.absforge.ui.theme.AbsForgePrimary
import com.absforge.ui.theme.AbsForgePrimaryGlow

@Composable
fun ProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    strokeWidth: Dp = 8.dp,
    trackColor: Color = Color(0xFF222428),
    progressColor: Color = AbsForgePrimary,
    glowColor: Color = AbsForgePrimaryGlow,
    showGlow: Boolean = true,
    content: @Composable () -> Unit = {}
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 300),
        label = "progressAnimation"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val strokeWidthPx = strokeWidth.toPx()
            val canvasDiameter = this.size.minDimension - strokeWidthPx
            val topLeft = Offset(
                (this.size.width - canvasDiameter) / 2f,
                (this.size.height - canvasDiameter) / 2f
            )
            val arcSize = Size(canvasDiameter, canvasDiameter)

            // Draw background track
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidthPx)
            )

            if (showGlow && animatedProgress > 0.01f) {
                // Draw glow behind active arc
                drawArc(
                    color = glowColor,
                    startAngle = -90f,
                    sweepAngle = animatedProgress * 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidthPx * 1.8f, cap = StrokeCap.Round)
                )
            }

            if (animatedProgress > 0.005f) {
                // Draw active progress arc
                drawArc(
                    color = progressColor,
                    startAngle = -90f,
                    sweepAngle = animatedProgress * 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(
                        width = strokeWidthPx,
                        cap = StrokeCap.Round
                    )
                )
            }
        }

        Box(contentAlignment = Alignment.Center) {
            content()
        }
    }
}
