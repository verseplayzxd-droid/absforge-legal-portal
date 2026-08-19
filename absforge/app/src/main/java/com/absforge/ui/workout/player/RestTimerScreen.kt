package com.absforge.ui.workout.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.absforge.ads.AbsForgeAdBanner
import com.absforge.ui.animation.ExerciseAnimationView
import com.absforge.ui.components.AbsForgeButton
import com.absforge.ui.components.ProgressRing
import com.absforge.ui.theme.*

@Composable
fun RestTimerScreen(
    state: WorkoutPlayerState,
    onAddRestTime: () -> Unit,
    onSkipRest: () -> Unit,
    onPause: () -> Unit
) {
    val nextEx = state.exercises.getOrNull(state.currentExerciseIndex + 1)
    val nextAnimId = nextEx?.animationId ?: "crunch"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AbsForgeBackground)
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "REST",
            color = AbsForgeTextSecondary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        val remainingSeconds = (state.restTimeRemainingMs / 1000).toInt()
        val progress = if (state.restTotalTimeMs > 0) state.restTimeRemainingMs.toFloat() / state.restTotalTimeMs else 1f

        ProgressRing(
            progress = progress,
            modifier = Modifier.size(180.dp),
            strokeWidth = 12.dp
        ) {
            Text(
                text = String.format("%02d:%02d", remainingSeconds / 60, remainingSeconds % 60),
                color = AbsForgePrimary,
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Next exercise preview card with live animation preview
        Card(
            colors = CardDefaults.cardColors(containerColor = AbsForgeSurface),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(AbsForgeSurfaceElevated, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    ExerciseAnimationView(
                        animationId = nextAnimId,
                        isPlaying = true,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text("NEXT EXERCISE", color = AbsForgeTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(state.nextExerciseName, color = AbsForgeTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(state.nextExerciseReps, color = AbsForgePrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onAddRestTime,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AbsForgePrimary)
            ) {
                Text("+20 SEC", color = AbsForgePrimary, fontWeight = FontWeight.Bold)
            }

            AbsForgeButton(
                text = "SKIP REST",
                onClick = onSkipRest,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Start.io Banner at very bottom of Rest Screen (collapses if load fails / no fill)
        AbsForgeAdBanner(
            modifier = Modifier.fillMaxWidth(),
            tag = "workout_rest_banner"
        )
    }
}
