package com.absforge.ui.exercises

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.absforge.ui.animation.ExerciseAnimationView
import com.absforge.ui.components.AbsForgeCard
import com.absforge.ui.theme.*

@Composable
fun ExerciseDetailsScreen(
    exerciseId: Int,
    onBack: () -> Unit = {},
    viewModel: ExerciseDetailsViewModel = viewModel()
) {
    LaunchedEffect(exerciseId) {
        viewModel.loadExercise(exerciseId)
    }

    val state by viewModel.uiState.collectAsState()
    val exercise = state.exercise

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AbsForgeBackground)
            .statusBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = AbsForgeTextPrimary)
            }
            Text(
                text = "EXERCISE DETAILS",
                color = AbsForgeTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { viewModel.toggleFavorite() }) {
                Icon(
                    imageVector = if (state.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (state.isFavorite) AbsForgePrimary else AbsForgeTextPrimary
                )
            }
        }

        if (state.isLoading || exercise == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AbsForgePrimary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Animation View
                AbsForgeCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                ) {
                    ExerciseAnimationView(
                        animationId = exercise.animationId,
                        isPlaying = true,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title & Target Muscle
                Text(
                    text = exercise.name,
                    color = AbsForgeTextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = AbsForgeSurfaceElevated,
                        shape = AbsForgeShapes.chipShape
                    ) {
                        Text(
                            text = exercise.targetMuscle,
                            color = AbsForgePrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = AbsForgeSurfaceElevated,
                        shape = AbsForgeShapes.chipShape
                    ) {
                        Text(
                            text = exercise.difficulty.uppercase(),
                            color = AbsForgeTextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Instructions
                Text(
                    text = "Instructions",
                    color = AbsForgeTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                AbsForgeCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = exercise.instructions,
                            color = AbsForgeTextPrimary,
                            fontSize = 14.sp,
                            lineHeight = 22.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Common Mistakes
                if (exercise.commonMistakes.isNotEmpty()) {
                    Text(
                        text = "Common Mistakes",
                        color = AbsForgeTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AbsForgeCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = exercise.commonMistakes,
                                color = AbsForgeError,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Tips
                if (exercise.tips.isNotEmpty()) {
                    Text(
                        text = "Tips",
                        color = AbsForgeTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AbsForgeCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = exercise.tips,
                                color = AbsForgeTextSecondary,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
