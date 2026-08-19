package com.absforge.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.absforge.ui.components.AbsForgeButton
import com.absforge.ui.theme.*

@Composable
fun HomeScreen(
    onStartWorkout: (planId: Int, dayNumber: Int) -> Unit,
    onQuickWorkoutSelected: (workoutId: String) -> Unit,
    onNavigateToProgram: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize().background(AbsForgeBackground),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = AbsForgePrimary)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AbsForgeBackground)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 800.dp)
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = uiState.greeting.uppercase(),
                                color = AbsForgeTextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = uiState.userName,
                                color = AbsForgeTextPrimary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Streak badge
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x33FFA500))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.LocalFireDepartment, contentDescription = null, tint = Color(0xFFFFA500), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${uiState.currentStreak} DAY STREAK",
                                color = Color(0xFFFFA500),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Today's Workout Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(AbsForgeSurface)
                            .border(1.dp, if (uiState.isDayCompleted) AbsForgePrimary else AbsForgeGhostBorder, RoundedCornerShape(24.dp))
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = if (uiState.isDayCompleted) AbsForgePrimary else AbsForgePrimary.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (uiState.isDayCompleted) "✓ DAY ${uiState.currentDay} COMPLETE" else "DAY ${uiState.currentDay}",
                                        color = if (uiState.isDayCompleted) Color.White else AbsForgePrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Surface(
                                    color = AbsForgeSurfaceElevated,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = uiState.todayDifficulty.uppercase(),
                                        color = AbsForgeTextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            if (uiState.isDayCompleted) {
                                Text(
                                    text = "DAY ${uiState.currentDay} CRUSHED! 🔥",
                                    color = AbsForgeTextPrimary,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = uiState.motivationalQuote,
                                    color = AbsForgePrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            } else if (uiState.isTodayRestDay) {
                                Text(
                                    text = "REST DAY",
                                    color = AbsForgeTextPrimary,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Take time to recover. Muscles grow when you rest.",
                                    color = AbsForgeTextSecondary,
                                    fontSize = 14.sp
                                )
                            } else {
                                Text(
                                    text = uiState.todayWorkoutName.uppercase(),
                                    color = AbsForgeTextPrimary,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Black
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    WorkoutStat(icon = Icons.Filled.FitnessCenter, value = "${uiState.todayExerciseCount} EXERCISES")
                                    WorkoutStat(icon = Icons.Filled.Timer, value = "${uiState.todayEstimatedMinutes} MIN")
                                    WorkoutStat(icon = Icons.Filled.LocalFireDepartment, value = "${uiState.todayEstimatedCalories} KCAL")
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            val nextDay = (uiState.currentDay + 1).coerceAtMost(30)
                            val buttonText = when {
                                uiState.isDayCompleted -> "START DAY $nextDay"
                                uiState.isTodayRestDay -> "SKIP REST DAY"
                                else -> "START WORKOUT"
                            }
                            val targetDay = if (uiState.isDayCompleted) nextDay else uiState.currentDay

                            AbsForgeButton(
                                text = buttonText,
                                onClick = { onStartWorkout(uiState.planId, targetDay) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Real Stats Overview (Workouts, Mins, Cals)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HomeStatCard(title = "WORKOUTS", value = "${uiState.totalWorkouts}", icon = Icons.Filled.CheckCircle, modifier = Modifier.weight(1f))
                        HomeStatCard(title = "MINUTES", value = "${uiState.totalMinutes}", icon = Icons.Filled.Timer, modifier = Modifier.weight(1f))
                        HomeStatCard(title = "CALORIES", value = "${uiState.totalCalories}", icon = Icons.Filled.LocalFireDepartment, modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Quick Workouts Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("QUICK WORKOUTS", color = AbsForgeTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(viewModel.quickWorkouts) { item ->
                            QuickWorkoutCard(item = item, onClick = { onQuickWorkoutSelected(item.id) })
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Start.io Home Banner
                    com.absforge.ads.AbsForgeAdBanner(
                        modifier = Modifier.fillMaxWidth(),
                        tag = "home_bottom_banner"
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun WorkoutStat(icon: ImageVector, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = AbsForgePrimary, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = value, color = AbsForgeTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun HomeStatCard(title: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Surface(
        color = AbsForgeSurface,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = AbsForgePrimary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, color = AbsForgeTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = title, color = AbsForgeTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun QuickWorkoutCard(item: QuickWorkout, onClick: () -> Unit) {
    Surface(
        color = AbsForgeSurface,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .width(150.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(AbsForgePrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.FlashOn, contentDescription = null, tint = AbsForgePrimary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = item.name, color = AbsForgeTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "${item.durationMinutes} MIN", color = AbsForgePrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}
