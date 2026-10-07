package com.absforge.ui.workout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.ViewAgenda
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.absforge.ads.AbsForgeAdBanner
import com.absforge.ui.animation.ExerciseThumbnail
import com.absforge.ui.theme.*

@Composable
fun ProgramScreen(
    onDaySelected: (planId: Int, dayNumber: Int) -> Unit,
    viewModel: ProgramViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var isCompactGrid by remember { mutableStateOf(false) }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AbsForgeBackground),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = AbsForgePrimary)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AbsForgeBackground)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Header Row with Title and Grid View Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "30-DAY ABS FORGE",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "High-definition progressive core transformation",
                    color = AbsForgeTextSecondary,
                    fontSize = 11.sp
                )
            }

            IconButton(
                onClick = { isCompactGrid = !isCompactGrid },
                modifier = Modifier
                    .size(38.dp)
                    .background(AbsForgeSurfaceElevated, CircleShape)
                    .border(1.dp, AbsForgeGhostBorder, CircleShape)
            ) {
                Icon(
                    imageVector = if (isCompactGrid) Icons.Default.ViewAgenda else Icons.Default.GridView,
                    contentDescription = "Toggle Grid Mode",
                    tint = AbsForgePrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Progress Overview Pill & Streak Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${(uiState.progressPercent * 100).toInt()}% COMPLETED",
                        color = AbsForgePrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "${uiState.completedCount} / 30 DAYS",
                        color = AbsForgeTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { uiState.progressPercent },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = AbsForgePrimary,
                    trackColor = AbsForgeSurfaceElevated
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Surface(
                color = Color(0x28FFA500),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0x66FFA500))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.LocalFireDepartment,
                        contentDescription = null,
                        tint = Color(0xFFFFA500),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${uiState.currentStreak} DAYS",
                        color = Color(0xFFFFA500),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Difficulty Tabs (Beginner / Intermediate / Advanced)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(AbsForgeSurface)
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val tabs = listOf("BEGINNER", "INTERMEDIATE", "ADVANCED")
            tabs.forEachIndexed { index, title ->
                val isSelected = index == uiState.selectedTabIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) AbsForgePrimary.copy(alpha = 0.2f) else Color.Transparent)
                        .clickable { viewModel.onTabSelected(index) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = if (isSelected) AbsForgePrimary else AbsForgeTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.6.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 30-Day Grid with Visual 3D Anatomical Images
        LazyVerticalGrid(
            columns = GridCells.Fixed(if (isCompactGrid) 3 else 2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(30) { index ->
                val dayNumber = index + 1
                val dayState = viewModel.getDayState(dayNumber)
                val isRestDay = uiState.restDays.contains(dayNumber) || (dayNumber % 4 == 0)
                val signatureExercise = getDaySignatureExercise(dayNumber)

                if (isCompactGrid) {
                    DayCellCompact(
                        dayNumber = dayNumber,
                        state = dayState,
                        isRestDay = isRestDay,
                        exerciseId = signatureExercise,
                        onClick = {
                            if (dayState != DayState.LOCKED) {
                                onDaySelected(uiState.selectedPlanId, dayNumber)
                            }
                        }
                    )
                } else {
                    DayCellDetailed(
                        dayNumber = dayNumber,
                        state = dayState,
                        isRestDay = isRestDay,
                        exerciseId = signatureExercise,
                        onClick = {
                            if (dayState != DayState.LOCKED) {
                                onDaySelected(uiState.selectedPlanId, dayNumber)
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        AbsForgeAdBanner(
            modifier = Modifier.fillMaxWidth(),
            tag = "program_bottom_banner"
        )
    }
}

/**
 * High-impact 2-Column Detailed Card with 3D Anatomical Image and Workout Info
 */
@Composable
fun DayCellDetailed(
    dayNumber: Int,
    state: DayState,
    isRestDay: Boolean,
    exerciseId: String,
    onClick: () -> Unit
) {
    val borderColor = when (state) {
        DayState.CURRENT -> AbsForgePrimary
        DayState.COMPLETED -> AbsForgePrimary.copy(alpha = 0.35f)
        else -> AbsForgeGhostBorder
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(148.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(if (state == DayState.CURRENT) 2.dp else 1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(enabled = state != DayState.LOCKED, onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = AbsForgeSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isRestDay) {
                // Rest Day Background Theme
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF141824), Color(0xFF0D0F17))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.Nightlight,
                            contentDescription = null,
                            tint = Color(0xFF64B5F6),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "REST & RECOVER",
                            color = Color(0xFF64B5F6),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp
                        )
                    }
                }
            } else {
                // 3D Anatomical Model Image
                ExerciseThumbnail(
                    animationId = exerciseId,
                    modifier = Modifier.fillMaxSize().padding(2.dp),
                    cornerRadius = 14,
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                )

                // Dark Gradient Vignette for readable overlays
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.45f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.85f)
                                )
                            )
                        )
                    )
            }

            // Top Row: Day Badge + Status Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (state == DayState.CURRENT) AbsForgePrimary else Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "DAY %02d".format(dayNumber),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        letterSpacing = 0.5.sp
                    )
                }

                when (state) {
                    DayState.COMPLETED -> {
                        Surface(
                            color = AbsForgePrimary.copy(alpha = 0.9f),
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Done",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp).padding(2.dp)
                            )
                        }
                    }
                    DayState.CURRENT -> {
                        Surface(
                            color = AbsForgePrimary,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "TODAY",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    DayState.LOCKED -> {
                        Surface(
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = AbsForgeTextSecondary.copy(alpha = 0.6f),
                                modifier = Modifier.size(18.dp).padding(3.dp)
                            )
                        }
                    }
                    else -> {}
                }
            }

            // Bottom Info: Exercise Name / Time
            if (!isRestDay) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = exerciseId.replace("_", " ").uppercase(),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "~12 MIN • 6 EXERCISES",
                        color = AbsForgePrimary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Compact 3-Column Grid Cell with 3D Thumbnail and Status Overlay
 */
@Composable
fun DayCellCompact(
    dayNumber: Int,
    state: DayState,
    isRestDay: Boolean,
    exerciseId: String,
    onClick: () -> Unit
) {
    val borderColor = when (state) {
        DayState.CURRENT -> AbsForgePrimary
        DayState.COMPLETED -> AbsForgePrimary.copy(alpha = 0.3f)
        else -> AbsForgeGhostBorder
    }

    Box(
        modifier = Modifier
            .aspectRatio(0.85f)
            .clip(RoundedCornerShape(14.dp))
            .background(AbsForgeSurface)
            .border(if (state == DayState.CURRENT) 2.dp else 1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(enabled = state != DayState.LOCKED, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isRestDay) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Nightlight,
                    contentDescription = null,
                    tint = Color(0xFF64B5F6),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "REST",
                    color = Color(0xFF64B5F6),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            ExerciseThumbnail(
                animationId = exerciseId,
                modifier = Modifier.fillMaxSize().padding(2.dp),
                cornerRadius = 12,
                contentScale = androidx.compose.ui.layout.ContentScale.Fit
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent, Color.Black.copy(alpha = 0.75f))
                        )
                    )
            )
        }

        // Top Left Day Number
        Text(
            text = "%02d".format(dayNumber),
            color = if (state == DayState.LOCKED) AbsForgeTextSecondary.copy(alpha = 0.6f) else Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(6.dp)
        )

        // Top Right / Bottom Center State Icon
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
        ) {
            when (state) {
                DayState.COMPLETED -> Icon(Icons.Filled.Check, contentDescription = null, tint = AbsForgePrimary, modifier = Modifier.size(16.dp))
                DayState.CURRENT -> Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(AbsForgePrimary, CircleShape)
                )
                DayState.LOCKED -> Icon(Icons.Filled.Lock, contentDescription = null, tint = AbsForgeTextSecondary.copy(alpha = 0.5f), modifier = Modifier.size(14.dp))
                else -> {}
            }
        }
    }
}

/**
 * Curated 30-Day progressive signature core exercises
 */
fun getDaySignatureExercise(dayNumber: Int): String {
    val list = listOf(
        "crunch",                 // Day 1
        "v_crunch",               // Day 2
        "plank",                  // Day 3
        "childs_pose",            // Day 4 (Rest / Stretch)
        "leg_raise",              // Day 5
        "flutter_kicks",          // Day 6
        "mountain_climbers",      // Day 7
        "cobra_stretch",          // Day 8 (Rest / Stretch)
        "bicycle_crunch",         // Day 9
        "russian_twist",          // Day 10
        "heel_touch",             // Day 11
        "childs_pose",            // Day 12 (Rest / Stretch)
        "dead_bug",               // Day 13
        "bird_dog",               // Day 14
        "side_plank_left",        // Day 15
        "cobra_stretch",          // Day 16 (Rest / Stretch)
        "side_plank_right",       // Day 17
        "v_up",                   // Day 18
        "scissor_kicks",          // Day 19
        "childs_pose",            // Day 20 (Rest / Stretch)
        "knee_to_chest_crunch",   // Day 21
        "long_arm_crunch",        // Day 22
        "toe_touch",              // Day 23
        "cobra_stretch",          // Day 24 (Rest / Stretch)
        "seated_abs_circles_cw",  // Day 25
        "standing_bicycle_crunch",// Day 26
        "bent_leg_twist",         // Day 27
        "childs_pose",            // Day 28 (Rest / Stretch)
        "oblique_crunch",         // Day 29
        "v_up"                    // Day 30
    )
    return list.getOrElse((dayNumber - 1) % list.size) { "crunch" }
}
