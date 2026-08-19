package com.absforge.ui.workout.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.absforge.ui.components.AbsForgeButton

@Composable
fun PauseOverlay(
    exerciseName: String,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onEndWorkout: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC090B0A))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "PAUSED",
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = exerciseName,
                color = Color(0xFFB7FF00),
                fontSize = 20.sp
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            AbsForgeButton(
                text = "RESUME",
                onClick = onResume,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(
                onClick = onRestart,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("RESTART EXERCISE", color = Color.White, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(
                onClick = onEndWorkout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("END WORKOUT", color = Color(0xFFFF5252), fontSize = 16.sp)
            }
        }
    }
}
