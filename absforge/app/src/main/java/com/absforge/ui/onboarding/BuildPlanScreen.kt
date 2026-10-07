package com.absforge.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.absforge.ui.animation.ExerciseThumbnail
import com.absforge.ui.components.AbsForgeButton
import com.absforge.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun BuildPlanScreen(
    onStartJourney: () -> Unit,
    viewModel: OnboardingViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var phase by remember { mutableIntStateOf(1) }
    var step1 by remember { mutableStateOf(false) }
    var step2 by remember { mutableStateOf(false) }
    var step3 by remember { mutableStateOf(false) }
    var step4 by remember { mutableStateOf(false) }
    var saveStarted by remember { mutableStateOf(false) }

    var targetPercent by remember { mutableFloatStateOf(0f) }
    val animatedPercent by animateFloatAsState(
        targetValue = targetPercent,
        animationSpec = tween(3200),
        label = "percent"
    )

    // Observe save success from ViewModel
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    LaunchedEffect(Unit) {
        if (!saveStarted) {
            saveStarted = true
            viewModel.startSaveProfile()
        }

        targetPercent = 100f
        delay(700)
        step1 = true
        delay(750)
        step2 = true
        delay(750)
        step3 = true
        delay(750)
        step4 = true
        delay(600)
        phase = 2
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AbsForgeBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (phase == 1) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Circular Glowing Progress Counter
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(AbsForgePrimary.copy(alpha = 0.25f), Color.Transparent)
                            ),
                            shape = CircleShape
                        )
                        .border(2.dp, Brush.sweepGradient(listOf(AbsForgePrimary, AbsForgePrimaryDim, AbsForgePrimary)), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${animatedPercent.toInt()}%",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "CALIBRATING",
                            color = AbsForgePrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))

                Text(
                    text = "BUILDING YOUR 30-DAY PLAN",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.8.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Personalizing core overload volume and rest schedule...",
                    color = AbsForgeTextSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Detailed Calibration Steps
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    BuildPlanStep(
                        title = "Anatomy & Body Metrics Analyzed",
                        subtitle = "Targeting upper, lower abs and lateral obliques",
                        isComplete = step1
                    )
                    BuildPlanStep(
                        title = "Progressive Overload Calibrated",
                        subtitle = "Adaptive volume scaled across 30 days",
                        isComplete = step2
                    )
                    BuildPlanStep(
                        title = "Rest & Recovery Intervals Structured",
                        subtitle = "Prevents fatigue and maximizes muscular definition",
                        isComplete = step3
                    )
                    BuildPlanStep(
                        title = "33 3D HD Animations & Voice Guidance Mapped",
                        subtitle = "Real-time anatomical form cues synchronized",
                        isComplete = step4
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                LinearProgressIndicator(
                    progress = { animatedPercent / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = AbsForgePrimary,
                    trackColor = AbsForgeSurfaceElevated
                )
            }
        } else {
            AnimatedVisibility(visible = true, enter = fadeIn(tween(800))) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = AbsForgePrimary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ElectricBolt,
                                contentDescription = null,
                                tint = AbsForgePrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "AI PROGRAM GENERATED",
                                color = AbsForgePrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "YOUR 30-DAY FORGE IS READY!",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val selectedGoal by viewModel.selectedGoal.collectAsState()
                    val fitnessLevel by viewModel.fitnessLevel.collectAsState()

                    val displayGoal = selectedGoal.ifBlank { "strong_abs" }.replace("_", " ").uppercase()
                    val displayLevel = fitnessLevel.ifBlank { "beginner" }.uppercase()

                    // Premium Blueprint Card with Real 3D Anatomical Hero Image
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = AbsForgeSurface),
                        border = BorderStroke(1.dp, AbsForgeGhostBorder),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // 3D Visual Hero Image
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
                                    animationId = "v_up",
                                    modifier = Modifier.fillMaxSize().padding(4.dp),
                                    cornerRadius = 14,
                                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            PlanSummaryRow("TARGET FOCUS", displayGoal)
                            Spacer(modifier = Modifier.height(10.dp))
                            PlanSummaryRow("STARTING LEVEL", displayLevel)
                            Spacer(modifier = Modifier.height(10.dp))
                            PlanSummaryRow("DURATION", "30 DAYS")
                            Spacer(modifier = Modifier.height(10.dp))
                            PlanSummaryRow("SESSION TIME", "~10 – 15 MIN / DAY")
                            Spacer(modifier = Modifier.height(10.dp))
                            PlanSummaryRow("ANIMATION FORMAT", "33 3D HD EXERCISES + AUDIO")
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    AbsForgeButton(
                        text = "START DAY 1 WORKOUT →",
                        onClick = onStartJourney,
                        enabled = saveSuccess,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun BuildPlanStep(
    title: String,
    subtitle: String,
    isComplete: Boolean
) {
    Surface(
        color = if (isComplete) AbsForgeSurfaceElevated else AbsForgeSurface,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, if (isComplete) AbsForgePrimary.copy(alpha = 0.4f) else AbsForgeGhostBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        if (isComplete) AbsForgePrimary else AbsForgeSurfaceElevated,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isComplete) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = AbsForgeTextSecondary.copy(alpha = 0.4f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = if (isComplete) Color.White else AbsForgeTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = AbsForgeTextSecondary.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun PlanSummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = AbsForgeTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(value, color = AbsForgePrimary, fontWeight = FontWeight.Black, fontSize = 12.sp)
    }
}
