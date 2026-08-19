package com.absforge.ui.workout.player

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PreCountdownScreen(
    countdownValue: Int,
    exerciseName: String
) {
    var scale by remember { mutableStateOf(0.6f) }
    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = "countdown_scale"
    )
    
    LaunchedEffect(countdownValue) {
        scale = 1.15f
        kotlinx.coroutines.delay(100)
        scale = 1.0f
    }

    val displayText = if (countdownValue > 0) countdownValue.toString() else "GO!"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090B0A)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = displayText,
            color = Color(0xFFB7FF00),
            fontSize = 120.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.scale(animatedScale)
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "GET READY",
            color = Color(0xFF9A9F9B),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = exerciseName.uppercase(),
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
