package com.absforge.ui.workout.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.absforge.ui.components.AbsForgeButton
import com.absforge.ui.theme.*

@Composable
fun ExerciseCompleteOverlay(
    exerciseName: String,
    targetMuscle: String = "CORE",
    initialReps: Int? = null,
    durationSeconds: Int? = null,
    nextExerciseName: String? = null,
    onContinue: (completedReps: Int?) -> Unit,
    onRetry: () -> Unit,
    onAddExtraRest: (() -> Unit)? = null
) {
    var loggedReps by remember(initialReps) { mutableIntStateOf(initialReps ?: 12) }
    var selectedFeedback by remember { mutableStateOf("Perfect") }

    val feedbackOptions = listOf(
        "Easy" to "😌",
        "Perfect" to "💪",
        "Tough" to "🔥"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.88f))
            .padding(horizontal = 20.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + scaleIn(initialScale = 0.92f)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = AbsForgeSurface),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.5.dp, Brush.verticalGradient(
                    listOf(AbsForgePrimary, AbsForgePrimary.copy(alpha = 0.2f))
                )),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 460.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Celebration glowing check badge
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                Brush.radialGradient(
                                    listOf(AbsForgePrimary, AbsForgePrimaryDim)
                                ),
                                shape = CircleShape
                            )
                            .border(2.dp, Color(0xFFFF6B6B), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "SET CRUSHED! 🔥",
                        color = AbsForgePrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = exerciseName.uppercase(),
                        color = AbsForgeTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Muscle & Calorie Pills
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = AbsForgeSurfaceElevated,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, AbsForgeGhostBorder)
                        ) {
                            Text(
                                text = targetMuscle.uppercase(),
                                color = AbsForgeTextSecondary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            color = AbsForgePrimary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = AbsForgePrimary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "~12 KCAL",
                                    color = AbsForgePrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Interactive Reps Adjuster Card (100M+ app standard: allows user to adjust logged reps)
                    Surface(
                        color = AbsForgeBackground,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, AbsForgeGhostBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "LOGGED VOLUME",
                                    color = AbsForgeTextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = if (initialReps != null) "$loggedReps REPS" else "${durationSeconds ?: 30} SEC",
                                    color = AbsForgeTextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            if (initialReps != null) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { if (loggedReps > 1) loggedReps-- },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(AbsForgeSurfaceElevated, CircleShape)
                                    ) {
                                        Icon(
                                            Icons.Default.Remove,
                                            contentDescription = "Decrease",
                                            tint = AbsForgeTextPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { loggedReps++ },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(AbsForgePrimary, CircleShape)
                                    ) {
                                        Icon(
                                            Icons.Default.Add,
                                            contentDescription = "Increase",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Workout Difficulty Feedback Chips
                    Text(
                        text = "HOW DID THIS SET FEEL?",
                        color = AbsForgeTextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        feedbackOptions.forEach { (label, emoji) ->
                            val isSelected = selectedFeedback == label
                            Surface(
                                color = if (isSelected) AbsForgePrimary.copy(alpha = 0.2f) else AbsForgeBackground,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) AbsForgePrimary else AbsForgeGhostBorder
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedFeedback = label }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = emoji, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = label,
                                        color = if (isSelected) AbsForgePrimary else AbsForgeTextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // Primary Button: Proceed to Next / Rest
                    val nextLabel = if (!nextExerciseName.isNullOrBlank()) {
                        "NEXT: ${nextExerciseName.uppercase()} →"
                    } else {
                        "TAKE A REST (20s) →"
                    }

                    AbsForgeButton(
                        text = nextLabel,
                        onClick = { onContinue(if (initialReps != null) loggedReps else null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Secondary Options: Repeat Set & +20s Extra Rest
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onRetry,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, AbsForgeTextSecondary.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                Icons.Default.Replay,
                                contentDescription = null,
                                tint = AbsForgeTextPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "REPEAT SET",
                                color = AbsForgeTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        if (onAddExtraRest != null) {
                            OutlinedButton(
                                onClick = onAddExtraRest,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, AbsForgePrimary.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "+20s REST",
                                    color = AbsForgePrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
