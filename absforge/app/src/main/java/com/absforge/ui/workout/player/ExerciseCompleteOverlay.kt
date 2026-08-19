package com.absforge.ui.workout.player

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.absforge.ui.components.AbsForgeButton
import com.absforge.ui.theme.*

@Composable
fun ExerciseCompleteOverlay(
    exerciseName: String,
    repsOrDuration: String,
    onContinue: () -> Unit,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AbsForgeBackground.copy(alpha = 0.92f))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = AbsForgeSurface),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, AbsForgePrimary),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 500.dp)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "EXERCISE COMPLETE! 🔥",
                    color = AbsForgePrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = exerciseName.uppercase(),
                    color = AbsForgeTextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = repsOrDuration,
                    color = AbsForgeTextSecondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(28.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onRetry,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        border = BorderStroke(1.dp, AbsForgeTextSecondary)
                    ) {
                        Text("RETRY", color = AbsForgeTextPrimary, fontWeight = FontWeight.Bold)
                    }

                    AbsForgeButton(
                        text = "CONTINUE",
                        onClick = onContinue,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                    )
                }
            }
        }
    }
}
