package com.absforge.ui.workout.player

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.absforge.ads.AbsForgeAdBanner
import com.absforge.ui.animation.ExerciseThumbnail
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
    val remainingSeconds = (state.restTimeRemainingMs / 1000).toInt().coerceAtLeast(0)
    val progress = if (state.restTotalTimeMs > 0) state.restTimeRemainingMs.toFloat() / state.restTotalTimeMs else 1f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AbsForgeBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Header Tag
            Surface(
                color = AbsForgePrimary.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "RECOVER & BREATHE",
                    color = AbsForgePrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Perfectly fitted Circular Progress Ring (Guaranteed NO text overflow)
            ProgressRing(
                progress = progress,
                size = 175.dp,
                strokeWidth = 10.dp,
                trackColor = Color(0xFF1E2024),
                progressColor = AbsForgePrimary
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%02d:%02d", remainingSeconds / 60, remainingSeconds % 60),
                        color = AbsForgePrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "REST",
                        color = AbsForgeTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Up Next Exercise Preview Card (Fills screen beautifully)
            Surface(
                color = AbsForgeSurface,
                shape = RoundedCornerShape(22.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FF3030)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = AbsForgePrimary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "UP NEXT",
                                color = AbsForgePrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Text(
                            text = nextEx?.targetMuscle?.uppercase() ?: "CORE",
                            color = AbsForgeTextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = state.nextExerciseName.uppercase(),
                            color = AbsForgeTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = state.nextExerciseReps,
                            color = AbsForgePrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Exercise Preview Animation Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0A0B0E))
                            .border(1.dp, AbsForgeGhostBorder, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        ExerciseThumbnail(
                            animationId = nextAnimId,
                            modifier = Modifier.fillMaxSize(),
                            cornerRadius = 14
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Form Cue Tip
                    val formTip = nextEx?.tips?.ifBlank { nextEx.instructions }?.takeIf { it.isNotBlank() }
                        ?: "Focus on controlled form and continuous muscle engagement."

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.TipsAndUpdates,
                            contentDescription = null,
                            tint = AbsForgePrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = formTip,
                            color = AbsForgeTextSecondary,
                            fontSize = 11.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // Bottom Action Controls & Ad Banner
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedButton(
                    onClick = onAddRestTime,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AbsForgePrimary)
                ) {
                    Text("+20 SEC", color = AbsForgePrimary, fontWeight = FontWeight.Black, fontSize = 14.sp)
                }

                AbsForgeButton(
                    text = "SKIP REST →",
                    onClick = onSkipRest,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Start.io Banner at bottom
            AbsForgeAdBanner(
                modifier = Modifier.fillMaxWidth(),
                tag = "workout_rest_banner"
            )
        }
    }
}
