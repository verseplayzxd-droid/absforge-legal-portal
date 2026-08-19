package com.absforge.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.absforge.ui.components.AbsForgeButton
import com.absforge.ui.theme.*

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        visible = true
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AbsForgeBackground)
            .padding(24.dp)
            .padding(top = 40.dp, bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(800)) + slideInVertically(tween(800)) { it / 2 },
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.8f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(AbsForgeSurfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder for silhouette
                Canvas(modifier = Modifier.size(200.dp)) {
                    drawCircle(color = AbsForgeSurfaceBright, radius = size.minDimension / 3)
                    drawCircle(color = AbsForgePrimary.copy(alpha = 0.2f), radius = size.minDimension / 4)
                    drawCircle(color = AbsForgePrimary, radius = size.minDimension / 8)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(800, delayMillis = 200)) + slideInVertically(tween(800, delayMillis = 200)) { it / 4 }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "BUILD YOUR ABS\nIN 30 DAYS",
                    color = AbsForgeTextPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 38.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Short daily workouts.\nNo equipment required.",
                    color = AbsForgeTextSecondary,
                    fontSize = 16.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(40.dp))
                AbsForgeButton(
                    onClick = onGetStarted,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "GET STARTED", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
