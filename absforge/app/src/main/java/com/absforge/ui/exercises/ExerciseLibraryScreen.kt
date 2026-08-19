package com.absforge.ui.exercises

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.absforge.ui.components.FilterChips
import com.absforge.ui.components.SearchBar
import com.absforge.ui.theme.*

@Composable
fun ExerciseLibraryScreen(
    onExerciseClick: (Int) -> Unit = {},
    onBack: () -> Unit = {},
    viewModel: ExerciseLibraryViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    val categories = listOf("All", "Favorites", "Upper Abs", "Lower Abs", "Obliques", "Core", "Stretch")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AbsForgeBackground)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = AbsForgeTextPrimary)
            }
            Text(
                text = "EXERCISE LIBRARY",
                color = AbsForgeTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Search Bar
        SearchBar(
            query = state.searchQuery,
            onQueryChange = viewModel::onSearchQueryChange,
            placeholder = "Search exercises or target muscle..."
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter Chips
        FilterChips(
            options = categories,
            selectedOption = state.selectedCategory,
            onOptionSelected = viewModel::onCategorySelect
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AbsForgePrimary)
            }
        } else if (state.filteredExercises.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No exercises found", color = AbsForgeTextSecondary, fontSize = 16.sp)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                itemsIndexed(state.filteredExercises) { index, exercise ->
                    val isFav = state.favoriteIds.contains(exercise.id)
                    AbsForgeCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onExerciseClick(exercise.id) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(AbsForgeSurfaceElevated, AbsForgeShapes.smallShape)
                            ) {
                                ExerciseAnimationView(
                                    animationId = exercise.animationId,
                                    isPlaying = true,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = exercise.name,
                                    color = AbsForgeTextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = exercise.targetMuscle,
                                    color = AbsForgeTextSecondary,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    color = AbsForgeSurfaceElevated,
                                    shape = AbsForgeShapes.chipShape
                                ) {
                                    Text(
                                        text = exercise.difficulty.uppercase(),
                                        color = AbsForgePrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            IconButton(onClick = { viewModel.toggleFavorite(exercise.id) }) {
                                Icon(
                                    imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (isFav) AbsForgePrimary else AbsForgeTextSecondary
                                )
                            }
                        }
                    }

                    if (index == 2 || index == 7) {
                        com.absforge.ads.AdMobNativeCard(tag = "exercise_library_native_$index")
                    }
                }
            }
        }

    }
}
