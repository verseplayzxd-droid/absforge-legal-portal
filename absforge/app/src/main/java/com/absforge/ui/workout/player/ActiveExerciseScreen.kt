package com.absforge.ui.workout.player

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
    onPrevious: () -> Unit,
    onSkip: () -> Unit,
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
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        // --- 1. TOP HEADER: Exit, Progress Counter, Workout Elapsed, Sound, Pause ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onExit,
                modifier = Modifier
                    .size(38.dp)
                    .background(AbsForgeSurfaceElevated, CircleShape)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Exit Workout",
                    tint = AbsForgeTextPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "EXERCISE ${state.currentExerciseIndex + 1} OF ${state.totalExercises}",
                    color = AbsForgePrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "ELAPSED  $elapsedStr",
                    color = AbsForgeTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(
                    onClick = onToggleSound,
                    modifier = Modifier
                        .size(38.dp)
                        .background(AbsForgeSurfaceElevated, CircleShape)
                ) {
                    Icon(
                        imageVector = if (state.soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "Sound",
                        tint = if (state.soundEnabled) AbsForgePrimary else AbsForgeTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = { if (state.isPaused) onResume() else onPause() },
                    modifier = Modifier
                        .size(38.dp)
                        .background(AbsForgeSurfaceElevated, CircleShape)
                ) {
                    Icon(
                        imageVector = if (state.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = if (state.isPaused) "Resume" else "Pause",
                        tint = AbsForgePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Progress line across total workout
        LinearProgressIndicator(
            progress = { (state.currentExerciseIndex + 1).toFloat() / state.totalExercises.coerceAtLeast(1) },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = AbsForgePrimary,
            trackColor = AbsForgeSurfaceElevated
        )

        Spacer(modifier = Modifier.height(10.dp))

        // --- 2. 3D ANATOMICAL MODEL VIEW (Seamless Dark Background & Zero Watermark) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFF0A0B0E))
                .border(1.dp, AbsForgeGhostBorder, RoundedCornerShape(22.dp)),
            contentAlignment = Alignment.Center
        ) {
            ExerciseAnimationView(
                animationId = exercise.animationId,
                isPlaying = !state.isPaused,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- 3. EXERCISE TITLE, MUSCLE BADGE & COUNTDOWN RING ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exercise.name.uppercase(),
                    color = AbsForgeTextPrimary,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    exercise.reps?.let { repCount ->
                        Surface(
                            color = AbsForgeSurfaceElevated,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, AbsForgeGhostBorder)
                        ) {
                            Text(
                                text = "TARGET: $repCount REPS",
                                color = AbsForgeTextPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }

            // Real Circular Countdown Timer (Counts DOWN 00:30 -> 00:00)
            ProgressRing(
                progress = progress,
                size = 68.dp,
                strokeWidth = 6.dp,
                trackColor = Color(0xFF1E2024),
                progressColor = AbsForgePrimary
            ) {
                Text(
                    text = String.format("%02d:%02d", remainingSeconds / 60, remainingSeconds % 60),
                    color = AbsForgePrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- 4. TECHNIQUE & FORM TIPS CARD (Eliminates Blank Space with Pro Training Value) ---
        Surface(
            color = AbsForgeSurface,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, AbsForgeGhostBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = AbsForgePrimary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "COACH FORM CUES",
                        color = AbsForgePrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                val tipText = exercise.tips.ifBlank { exercise.instructions }
                    .ifBlank { "Keep your core contracted throughout the full range of motion. Breathe steadily." }

                Text(
                    text = tipText,
                    color = AbsForgeTextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- 5. WORKOUT CONTROL BAR (100M+ Fitness App Standard: Previous | Big Done | Skip) ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous Exercise Button
            IconButton(
                onClick = onPrevious,
                enabled = state.currentExerciseIndex > 0,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (state.currentExerciseIndex > 0) AbsForgeSurfaceElevated else AbsForgeSurfaceElevated.copy(alpha = 0.4f),
                        CircleShape
                    )
                    .border(1.dp, AbsForgeGhostBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous Exercise",
                    tint = if (state.currentExerciseIndex > 0) AbsForgeTextPrimary else AbsForgeTextSecondary.copy(alpha = 0.4f),
                    modifier = Modifier.size(24.dp)
                )
            }

            // GIANT PRIMARY ACTION BUTTON: "✓ I'M DONE (14 REPS)"
            val primaryLabel = if (exercise.reps != null) {
                "✓ I'M DONE (${exercise.reps} REPS)"
            } else {
                "✓ FINISH SET EARLY"
            }

            Button(
                onClick = onComplete,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AbsForgePrimary)
            ) {
                Text(
                    text = primaryLabel,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    letterSpacing = 0.8.sp
                )
            }

            // Skip Exercise Button
            IconButton(
                onClick = onSkip,
                modifier = Modifier
                    .size(48.dp)
                    .background(AbsForgeSurfaceElevated, CircleShape)
                    .border(1.dp, AbsForgeGhostBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Skip Exercise",
                    tint = AbsForgeTextPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // --- 6. AD BANNER (Padded cleanly at bottom) ---
        AbsForgeAdBanner(
            modifier = Modifier.fillMaxWidth(),
            tag = "active_workout_banner"
        )
    }
}
