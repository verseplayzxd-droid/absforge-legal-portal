package com.absforge.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var saveStarted by remember { mutableStateOf(false) }

    var progress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(3000),
        label = "progress"
    )

    // Observe save success from ViewModel (set by viewModelScope, survives recomposition)
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    // Trigger saveProfile via viewModelScope (NOT LaunchedEffect coroutine)
    // This ensures save work survives even if this composable leaves composition
    LaunchedEffect(Unit) {
        if (!saveStarted) {
            saveStarted = true
            viewModel.startSaveProfile()
        }

        progress = 1f
        delay(800)
        step1 = true
        delay(800)
        step2 = true
        delay(800)
        step3 = true
        delay(600)
        phase = 2
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AbsForgeBackground)
            .padding(24.dp)
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (phase == 1) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProgressStep("Analyzing your goal...", step1)
                Spacer(modifier = Modifier.height(24.dp))
                ProgressStep("Setting difficulty...", step2)
                Spacer(modifier = Modifier.height(24.dp))
                ProgressStep("Building your 30-day plan...", step3)

                Spacer(modifier = Modifier.height(64.dp))

                LinearProgressIndicator(
                    progress = animatedProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(AbsForgeSurface, RoundedCornerShape(4.dp)),
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
                    Spacer(modifier = Modifier.height(40.dp))
                    Text(
                        text = "YOUR PLAN IS READY",
                        color = AbsForgePrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(40.dp))

                    val selectedGoal by viewModel.selectedGoal.collectAsState()
                    val fitnessLevel by viewModel.fitnessLevel.collectAsState()

                    val displayGoal = selectedGoal.ifBlank { "strong_abs" }.replace("_", " ").uppercase()
                    val displayLevel = fitnessLevel.ifBlank { "beginner" }.uppercase()

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = AbsForgeSurfaceElevated),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            PlanSummaryRow("Goal", displayGoal)
                            Spacer(modifier = Modifier.height(16.dp))
                            PlanSummaryRow("Level", displayLevel)
                            Spacer(modifier = Modifier.height(16.dp))
                            PlanSummaryRow("Duration", "30 Days")
                            Spacer(modifier = Modifier.height(16.dp))
                            PlanSummaryRow("Daily Commitment", "~10-15 min")
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    AbsForgeButton(
                        text = "START MY JOURNEY",
                        onClick = onStartJourney,
                        enabled = saveSuccess,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun ProgressStep(text: String, isComplete: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = text,
            color = if (isComplete) AbsForgeTextPrimary else AbsForgeTextSecondary,
            fontSize = 18.sp,
            modifier = Modifier.weight(1f)
        )
        if (isComplete) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = AbsForgePrimary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun PlanSummaryRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = AbsForgeTextSecondary, fontSize = 16.sp)
        Text(value, color = AbsForgeTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}
