package com.absforge.ui.animation

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.min

@Composable
fun ExerciseAnimationView(
    animationId: String,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val animDef = ExerciseAnimations.getAnimation(animationId)
    
    var frozenProgress by remember { mutableFloatStateOf(0f) }
    
    val infiniteTransition = rememberInfiniteTransition(label = "ExerciseTransition")
    val animatedProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(animDef.durationMs, easing = FastOutSlowInEasing),
            repeatMode = animDef.repeatMode
        ),
        label = "PoseProgress"
    )

    val currentProgress = if (isPlaying) {
        frozenProgress = animatedProgress
        animatedProgress
    } else {
        frozenProgress
    }

    val currentPose = interpolatePose(animDef.startPose, animDef.endPose, currentProgress)

    Canvas(modifier = modifier.fillMaxSize().background(Color(0xFF090B0A))) {
        val w = size.width
        val h = size.height
        val center = Offset(w / 2, h / 2)
        val scale = min(w, h)
        
        drawHumanFigure(
            pose = currentPose,
            canvasCenter = center,
            scale = scale
        )
    }
}
