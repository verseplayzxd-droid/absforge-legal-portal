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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.absforge.ui.animation.ExerciseThumbnail
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
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 800.dp)
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))

                    // 1. Header (Greeting + Streak Pill)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = uiState.greeting.uppercase(),
                                color = AbsForgeTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = uiState.userName,
                                color = AbsForgeTextPrimary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        // Streak badge
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0x28FFA500))
                                .border(1.dp, Color(0x66FFA500), RoundedCornerShape(14.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.LocalFireDepartment,
                                contentDescription = null,
                                tint = Color(0xFFFFA500),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${uiState.currentStreak} DAY STREAK",
                                color = Color(0xFFFFA500),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 2. Today's Workout Hero Card with Exercise Visual
                    TodayWorkoutHeroCard(
                        uiState = uiState,
                        onStartWorkout = {
                            val nextDay = (uiState.currentDay + 1).coerceAtMost(30)
                            val targetDay = if (uiState.isDayCompleted) nextDay else uiState.currentDay
                            onStartWorkout(uiState.planId, targetDay)
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 3. Today's Exercises Breakdown (with real images!)
                    if (uiState.todayExercises.isNotEmpty() && !uiState.isTodayRestDay) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "TODAY'S EXERCISES",
                                    color = AbsForgeTextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.8.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = AbsForgePrimary.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "${uiState.todayExercises.size}",
                                        color = AbsForgePrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = "TAP TO PREVIEW",
                                color = AbsForgeTextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(uiState.todayExercises) { ex ->
                                TodayExerciseCard(
                                    exercise = ex,
                                    onClick = { onStartWorkout(uiState.planId, uiState.currentDay) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // 4. Lifetime Stats Overview (Workouts, Mins, Cals)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        HomeStatCard(title = "WORKOUTS", value = "${uiState.totalWorkouts}", icon = Icons.Filled.CheckCircle, modifier = Modifier.weight(1f))
                        HomeStatCard(title = "MINUTES", value = "${uiState.totalMinutes}", icon = Icons.Filled.Timer, modifier = Modifier.weight(1f))
                        HomeStatCard(title = "CALORIES", value = "${uiState.totalCalories}", icon = Icons.Filled.LocalFireDepartment, modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(26.dp))

                    // 5. 30-Day Program Roadmap (Weekly Tracker)
                    WeeklyProgressSection(
                        uiState = uiState,
                        onViewAllClick = onNavigateToProgram
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    // 6. Targeted Quick Workouts Section (with real images!)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TARGETED QUICK WORKOUTS",
                            color = AbsForgeTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp
                        )
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

                    Spacer(modifier = Modifier.height(24.dp))

                    // 7. Pro Coach Form Tip Card
                    CoachTipCard(dayNumber = uiState.currentDay)

                    Spacer(modifier = Modifier.height(16.dp))

                    // 8. Start.io Home Banner
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
fun TodayWorkoutHeroCard(
    uiState: HomeUiState,
    onStartWorkout: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1C1D21),
                        Color(0xFF131417)
                    )
                )
            )
            .border(
                1.dp,
                if (uiState.isDayCompleted) AbsForgePrimary else Color(0x33FF3030),
                RoundedCornerShape(24.dp)
            )
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Left: Info & Badges
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = if (uiState.isDayCompleted) AbsForgePrimary else AbsForgePrimary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (uiState.isDayCompleted) "✓ DAY ${uiState.currentDay} COMPLETED" else "DAY ${uiState.currentDay}",
                                color = if (uiState.isDayCompleted) Color.White else AbsForgePrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Color(0x33FFFFFF),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = uiState.todayDifficulty.uppercase(),
                                color = AbsForgeTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (uiState.isDayCompleted) {
                        Text(
                            text = "DAY ${uiState.currentDay} CRUSHED! 🔥",
                            color = AbsForgeTextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = uiState.motivationalQuote,
                            color = AbsForgePrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    } else if (uiState.isTodayRestDay) {
                        Text(
                            text = "REST & RECOVERY",
                            color = AbsForgeTextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Muscles rebuild and grow during rest.",
                            color = AbsForgeTextSecondary,
                            fontSize = 13.sp
                        )
                    } else {
                        Text(
                            text = uiState.todayWorkoutName.uppercase(),
                            color = AbsForgeTextPrimary,
                            fontSize = 23.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                // Right: Exercise Preview Visual Thumbnail
                if (!uiState.isTodayRestDay && uiState.featuredAnimationId.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp))
                    ) {
                        ExerciseThumbnail(
                            animationId = uiState.featuredAnimationId,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stat pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WorkoutStat(icon = Icons.Filled.FitnessCenter, value = "${uiState.todayExerciseCount} EXERCISES")
                WorkoutStat(icon = Icons.Filled.Timer, value = "${uiState.todayEstimatedMinutes} MIN")
                WorkoutStat(icon = Icons.Filled.LocalFireDepartment, value = "${uiState.todayEstimatedCalories} KCAL")
            }

            Spacer(modifier = Modifier.height(18.dp))

            val nextDay = (uiState.currentDay + 1).coerceAtMost(30)
            val buttonText = when {
                uiState.isDayCompleted -> "START DAY $nextDay"
                uiState.isTodayRestDay -> "SKIP REST DAY"
                else -> "START WORKOUT"
            }

            AbsForgeButton(
                text = buttonText,
                onClick = onStartWorkout,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun TodayExerciseCard(
    exercise: TodayExerciseItem,
    onClick: () -> Unit
) {
    Surface(
        color = AbsForgeSurface,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AbsForgeGhostBorder),
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Exercise HD Thumbnail
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(86.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                ExerciseThumbnail(
                    animationId = exercise.animationId,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = exercise.name,
                color = AbsForgeTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exercise.targetMuscle,
                    color = AbsForgeTextSecondary,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = exercise.reps?.let { "${it}x" } ?: "${exercise.durationSeconds}s",
                    color = AbsForgePrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
fun WeeklyProgressSection(
    uiState: HomeUiState,
    onViewAllClick: () -> Unit
) {
    Surface(
        color = AbsForgeSurface,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AbsForgeGhostBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "30-DAY CORE BLUEPRINT",
                        color = AbsForgeTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "Week ${((uiState.currentDay - 1) / 7) + 1} Progress",
                        color = AbsForgeTextSecondary,
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = "ROADMAP →",
                    color = AbsForgePrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.clickable { onViewAllClick() }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 7 Days Circles Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                uiState.weeklyProgress.forEach { dayItem ->
                    DayCircle(
                        dayNumber = dayItem.dayNumber,
                        isCompleted = dayItem.isCompleted,
                        isCurrent = dayItem.isCurrent
                    )
                }
            }
        }
    }
}

@Composable
fun DayCircle(
    dayNumber: Int,
    isCompleted: Boolean,
    isCurrent: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isCompleted -> AbsForgePrimary
                        isCurrent -> AbsForgePrimary.copy(alpha = 0.25f)
                        else -> Color(0xFF1E2024)
                    }
                )
                .border(
                    width = if (isCurrent) 2.dp else 1.dp,
                    color = when {
                        isCurrent -> AbsForgePrimary
                        isCompleted -> AbsForgePrimary
                        else -> Color(0x22FFFFFF)
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Text(
                    text = "$dayNumber",
                    color = if (isCurrent) AbsForgePrimary else AbsForgeTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "D$dayNumber",
            color = if (isCurrent) AbsForgePrimary else AbsForgeTextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun QuickWorkoutCard(
    item: QuickWorkout,
    onClick: () -> Unit
) {
    Surface(
        color = AbsForgeSurface,
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AbsForgeGhostBorder),
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Exercise Image Thumbnail at Top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                ExerciseThumbnail(
                    animationId = item.animationId,
                    modifier = Modifier.fillMaxSize()
                )

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xCC0E0F12))
                            )
                        )
                )

                // Top duration badge
                Surface(
                    color = Color(0xDD000000),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                ) {
                    Text(
                        text = "${item.durationMinutes} MIN",
                        color = AbsForgePrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.name,
                color = AbsForgeTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.targetMuscle,
                color = AbsForgeTextSecondary,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.LocalFireDepartment,
                    contentDescription = null,
                    tint = AbsForgePrimary,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "${item.calories} KCAL",
                    color = AbsForgePrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
fun CoachTipCard(dayNumber: Int) {
    val tips = listOf(
        "Exhale on every crunch. Breathing out empties your lungs, allowing your rectus abdominis to contract 30% harder.",
        "Keep your lower back pressed into the mat during leg raises to protect your lumbar spine and isolate lower abs.",
        "Quality over speed. A 2-second controlled contraction burns twice as many core muscle fibers as fast reps.",
        "During planks, actively squeeze your glutes and quads. A rigid body creates the highest core stability.",
        "Rest days are when abdominal muscles actually repair and become visible. Don't skip your nutrition."
    )
    val tip = tips[dayNumber % tips.size]

    Surface(
        color = Color(0xFF141518),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FF3030)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(AbsForgePrimary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.TipsAndUpdates,
                    contentDescription = null,
                    tint = AbsForgePrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "COACH FORM CUE",
                    color = AbsForgePrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = tip,
                    color = AbsForgeTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
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
        border = androidx.compose.foundation.BorderStroke(1.dp, AbsForgeGhostBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = AbsForgePrimary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, color = AbsForgeTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Black)
            Text(text = title, color = AbsForgeTextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
        }
    }
}
