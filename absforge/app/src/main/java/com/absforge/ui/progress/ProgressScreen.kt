package com.absforge.ui.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.absforge.ui.components.AbsForgeButton
import com.absforge.ui.components.AbsForgeCard
import com.absforge.ui.components.StatsCard
import com.absforge.ui.theme.*

@Composable
fun ProgressScreen(
    onNavigateToBodyTracker: () -> Unit = {},
    viewModel: ProgressViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showAddWeightDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AbsForgeBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 800.dp)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
        Text(
            text = "PROGRESS & ANALYTICS",
            color = AbsForgeTextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tab Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AbsForgeSurface, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            val tabs = listOf("ACTIVITY", "BODY")
            tabs.forEachIndexed { index, label ->
                val selected = state.selectedTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (selected) AbsForgePrimary else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { viewModel.setTab(index) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (selected) AbsForgeOnPrimary else AbsForgeTextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (state.selectedTab == 0) {
            // ACTIVITY TAB
            ActivityTabContent(state)
        } else {
            // BODY TAB
            BodyTabContent(state, onAddWeightClick = { showAddWeightDialog = true })
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Start.io Progress Banner
        com.absforge.ads.AbsForgeAdBanner(
            modifier = Modifier.fillMaxWidth(),
            tag = "progress_bottom_banner"
        )

        if (showAddWeightDialog) {
            AddWeightDialog(
                onDismiss = { showAddWeightDialog = false },
                onConfirm = { weight ->
                    viewModel.addWeightEntry(weight)
                    showAddWeightDialog = false
                }
            )
        }
    }
}
}

@Composable
private fun ActivityTabContent(state: ProgressUiState) {
    // Streak Card
    AbsForgeCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = null,
                tint = AbsForgePrimary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "${state.currentStreak} DAY STREAK",
                    color = AbsForgeTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Keep working out daily to build your streak!",
                    color = AbsForgeTextSecondary,
                    fontSize = 13.sp
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // 30-Day Completion
    AbsForgeCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("30-Day Challenge", color = AbsForgeTextPrimary, fontWeight = FontWeight.Bold)
                Text("${(state.progressPercent * 100).toInt()}%", color = AbsForgePrimary, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { state.progressPercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = AbsForgePrimary,
                trackColor = AbsForgeSurfaceElevated
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${state.completedDays} of 30 days completed",
                color = AbsForgeTextSecondary,
                fontSize = 12.sp
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Stats Grid
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatsCard(
            title = "Workouts",
            value = "${state.totalWorkouts}",
            icon = Icons.Default.DirectionsRun,
            modifier = Modifier.weight(1f)
        )
        StatsCard(
            title = "Minutes",
            value = "${state.totalMinutes}",
            icon = Icons.Default.Timer,
            modifier = Modifier.weight(1f)
        )
        StatsCard(
            title = "Calories",
            value = "${state.totalCalories}",
            icon = Icons.Default.LocalFireDepartment,
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Weekly Activity Bar Chart
    AbsForgeCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Weekly Activity (Mins)", color = AbsForgeTextPrimary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            val maxMins = (state.weeklyMinutes.maxOfOrNull { it.second } ?: 30).coerceAtLeast(30)
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                val barWidth = size.width / (state.weeklyMinutes.size * 2f)
                state.weeklyMinutes.forEachIndexed { i, pair ->
                    val mins = pair.second
                    val barHeight = (mins.toFloat() / maxMins.toFloat()) * size.height
                    val x = i * (barWidth * 2) + barWidth / 2
                    val y = size.height - barHeight

                    drawRect(
                        color = if (mins > 0) AbsForgePrimary else AbsForgeSurfaceElevated,
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                state.weeklyMinutes.forEach { (label, _) ->
                    Text(label, color = AbsForgeTextSecondary, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun BodyTabContent(
    state: ProgressUiState,
    onAddWeightClick: () -> Unit
) {
    // Current Weight Card
    AbsForgeCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Current Weight", color = AbsForgeTextSecondary, fontSize = 12.sp)
                    Text("${state.currentWeight} kg", color = AbsForgeTextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = onAddWeightClick) {
                    Icon(Icons.Default.Add, contentDescription = "Add Weight", tint = AbsForgePrimary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Starting", color = AbsForgeTextSecondary, fontSize = 11.sp)
                    Text("${state.startingWeight} kg", color = AbsForgeTextPrimary, fontSize = 14.sp)
                }
                Column {
                    Text("Target", color = AbsForgeTextSecondary, fontSize = 11.sp)
                    Text("${state.targetWeight} kg", color = AbsForgePrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("BMI", color = AbsForgeTextSecondary, fontSize = 11.sp)
                    Text(String.format("%.1f", state.bmi), color = AbsForgeTextPrimary, fontSize = 14.sp)
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Weight Chart Mock
    AbsForgeCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Weight Progress", color = AbsForgeTextPrimary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                val path = Path().apply {
                    moveTo(0f, size.height * 0.4f)
                    lineTo(size.width * 0.3f, size.height * 0.5f)
                    lineTo(size.width * 0.6f, size.height * 0.35f)
                    lineTo(size.width, size.height * 0.25f)
                }
            }
        }
    }
}

@Composable
private fun AddWeightDialog(
    onDismiss: () -> Unit,
    onConfirm: (Float) -> Unit
) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AbsForgeSurface,
        title = { Text("Log Weight", color = AbsForgeTextPrimary) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Weight (kg)") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AbsForgePrimary,
                    unfocusedBorderColor = AbsForgeTextSecondary,
                    focusedLabelColor = AbsForgePrimary
                )
            )
        },
        confirmButton = {
            AbsForgeButton(
                text = "Save",
                onClick = {
                    val w = text.toFloatOrNull() ?: 70f
                    onConfirm(w)
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = AbsForgeTextSecondary)
            }
        }
    )
}
