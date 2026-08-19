package com.absforge.ui.workout

import android.app.Application
import com.absforge.AbsForgeApplication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.absforge.ui.components.AbsForgeButton
import com.absforge.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutOverviewScreen(
    planId: Int,
    dayNumber: Int,
    onStartWorkout: () -> Unit,
    onBack: () -> Unit,
    onExerciseInfo: (Int) -> Unit
) {
    val context = (LocalContext.current.applicationContext as? Application) ?: AbsForgeApplication.instance
    val viewModel: WorkoutOverviewViewModel = viewModel(
        factory = WorkoutOverviewViewModel.Factory(context, planId, dayNumber)
    )
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize().background(AbsForgeBackground), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AbsForgePrimary)
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DAY $dayNumber", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AbsForgeBackground,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .background(AbsForgeBackground)
                    .padding(16.dp)
            ) {
                AbsForgeButton(
                    text = "START WORKOUT",
                    onClick = onStartWorkout,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        containerColor = AbsForgeBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Hero Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(AbsForgeSurface)
                        .border(1.dp, AbsForgeGhostBorder, RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Placeholder for silhouette
                    Icon(
                        Icons.Filled.Timer, 
                        contentDescription = null, 
                        tint = AbsForgePrimary.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = uiState.workoutName.uppercase(),
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(text = "${uiState.estimatedMinutes} MIN", color = AbsForgeTextSecondary, fontSize = 14.sp)
                        Text(text = "•", color = AbsForgeTextSecondary)
                        Text(text = "${uiState.estimatedCalories} KCAL", color = AbsForgeTextSecondary, fontSize = 14.sp)
                        Text(text = "•", color = AbsForgeTextSecondary)
                        Text(text = "${uiState.exerciseCount} EXERCISES", color = AbsForgeTextSecondary, fontSize = 14.sp)
                    }
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AbsForgeSurfaceElevated)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = uiState.difficulty.uppercase(),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = "EXERCISES",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            items(uiState.exercises) { exercise ->
                ExerciseCard(
                    exercise = exercise,
                    onInfoClick = { onExerciseInfo(exercise.exerciseId) }
                )
                
                if (exercise.restAfterSeconds > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.height(1.dp).weight(1f).background(AbsForgeGhostBorder))
                        Text(
                            text = "REST ${exercise.restAfterSeconds}S",
                            color = AbsForgePrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Box(modifier = Modifier.height(1.dp).weight(1f).background(AbsForgeGhostBorder))
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun ExerciseCard(exercise: ExerciseDisplayItem, onInfoClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AbsForgeSurface)
            .border(1.dp, AbsForgeGhostBorder, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail placeholder
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(AbsForgeSurfaceElevated),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Timer, contentDescription = null, tint = AbsForgePrimary)
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = exercise.name,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                val metric = if (exercise.durationSeconds != null) "${exercise.durationSeconds} SEC" else "${exercise.reps} REPS"
                Text(
                    text = metric,
                    color = AbsForgePrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = exercise.targetMuscle.uppercase(),
                    color = AbsForgeTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        
        IconButton(onClick = onInfoClick) {
            Icon(Icons.Filled.Info, contentDescription = "Info", tint = AbsForgeTextSecondary)
        }
    }
}
