package com.absforge.ui.workout.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.absforge.ads.AbsForgeAdBanner
import com.absforge.ui.animation.ExerciseAnimationView
import com.absforge.ui.components.ProgressRing
import com.absforge.ui.theme.*

@Composable
fun ActiveExerciseScreen(
    exercise: WorkoutExercise,
    state: WorkoutPlayerState,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onComplete: () -> Unit,
    onToggleSound: () -> Unit,
    onExit: () -> Unit
) {
    val totalMins = state.totalElapsedSeconds / 60
    val totalSecs = state.totalElapsedSeconds % 60
    val elapsedStr = String.format("%02d:%02d", totalMins, totalSecs)

    val remainingSeconds = (state.exerciseTimeRemainingMs / 1000).toInt()
    val progress = if (state.exerciseTotalTimeMs > 0) state.exerciseTimeRemainingMs.toFloat() / state.exerciseTotalTimeMs else 1f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AbsForgeBackground)
            .padding(14.dp)
            .statusBarsPadding()
    ) {
        // Top Header: Exit X, EXERCISE X OF Y, WORKOUT TIME 00:05 (counts UP), Sound & Pause controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onExit) {
                Icon(Icons.Default.Close, contentDescription = "Exit Workout", tint = AbsForgeTextPrimary)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "EXERCISE ${state.currentExerciseIndex + 1} OF ${state.totalExercises}",
                    color = AbsForgePrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "WORKOUT TIME  $elapsedStr",
                    color = AbsForgeTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onToggleSound) {
                    Icon(
                        imageVector = if (state.soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "Sound",
                        tint = if (state.soundEnabled) AbsForgePrimary else AbsForgeTextSecondary
                    )
                }

                IconButton(
                    onClick = { if (state.isPaused) onResume() else onPause() },
                    modifier = Modifier
                        .size(40.dp)
                        .background(AbsForgeSurfaceElevated, CircleShape)
                ) {
                    Icon(
                        imageVector = if (state.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = if (state.isPaused) "Resume" else "Pause",
                        tint = AbsForgePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Workout Progress Bar
        LinearProgressIndicator(
            progress = (state.currentExerciseIndex + 1).toFloat() / state.totalExercises.coerceAtLeast(1),
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = AbsForgePrimary,
            trackColor = AbsForgeSurfaceElevated
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Vector Animation View
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(AbsForgeSurface, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            ExerciseAnimationView(
                animationId = exercise.animationId,
                isPlaying = !state.isPaused,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Exercise Name & Rep Target / Muscle Info Chip (Left) + Large Real Countdown Timer (Right)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exercise.name.uppercase(),
                    color = AbsForgeTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        color = AbsForgePrimary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = exercise.targetMuscle.uppercase(),
                            color = AbsForgePrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    exercise.reps?.let { repCount ->
                        Surface(
                            color = AbsForgePrimary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "TARGET: $repCount REPS",
                                color = AbsForgePrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Real 30-Second Countdown Timer (Counts DOWN 00:30 -> 00:00)
            ProgressRing(progress = progress, modifier = Modifier.size(68.dp)) {
                Text(
                    text = String.format("%02d:%02d", remainingSeconds / 60, remainingSeconds % 60),
                    color = AbsForgePrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Coaching Instructions
        Text(
            text = exercise.instructions.ifBlank { exercise.tips },
            color = AbsForgeTextSecondary,
            fontSize = 12.sp,
            maxLines = 2
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Start.io Active Exercise Banner at bottom
        // NO DONE BUTTON — Timer automatically reaches 00:00 and triggers Exercise Complete!
        AbsForgeAdBanner(
            modifier = Modifier.fillMaxWidth(),
            tag = "active_workout_banner"
        )
    }
}
