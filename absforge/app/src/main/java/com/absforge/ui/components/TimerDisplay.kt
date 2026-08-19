package com.absforge.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.absforge.ui.theme.timerDisplay

@Composable
fun TimerDisplay(
    timeText: String,
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 240.dp
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        ProgressRing(
            progress = progress,
            size = size,
            strokeWidth = 12.dp
        )
        Text(
            text = timeText,
            style = timerDisplay
        )
    }
}
