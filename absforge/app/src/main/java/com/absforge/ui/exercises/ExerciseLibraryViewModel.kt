package com.absforge.ui.exercises

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.absforge.AbsForgeApplication
import com.absforge.data.local.entity.ExerciseEntity
import com.absforge.data.local.entity.FavoriteExerciseEntity
import com.absforge.utils.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ExerciseLibraryUiState(
    val exercises: List<ExerciseEntity> = emptyList(),
    val filteredExercises: List<ExerciseEntity> = emptyList(),
    val favoriteIds: Set<Int> = emptySet(),
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    val isLoading: Boolean = true
)

class ExerciseLibraryViewModel(application: Application) : AndroidViewModel(application) {

    private val db = (application as AbsForgeApplication).database

    private val _uiState = MutableStateFlow(ExerciseLibraryUiState())
    val uiState: StateFlow<ExerciseLibraryUiState> = _uiState.asStateFlow()

    init {
        loadExercises()
        observeFavorites()
    }

    private fun loadExercises() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    db.exerciseDao().getAll().collect { list ->
                        _uiState.update {
                            it.copy(
                                exercises = list,
                                isLoading = false
                            )
                        }
                        applyFilters()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    db.favoriteDao().getAll().collect { favs ->
                        val favIds = favs.map { it.exerciseId }.toSet()
                        _uiState.update { it.copy(favoriteIds = favIds) }
                        applyFilters()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun onCategorySelect(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        applyFilters()
    }

    fun toggleFavorite(exerciseId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    if (_uiState.value.favoriteIds.contains(exerciseId)) {
                        db.favoriteDao().remove(exerciseId)
                    } else {
                        db.favoriteDao().insert(
                            FavoriteExerciseEntity(exerciseId = exerciseId, addedAt = DateUtils.nowEpochMs())
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun applyFilters() {
        val query = _uiState.value.searchQuery.lowercase().trim()
        val cat = _uiState.value.selectedCategory
        val favIds = _uiState.value.favoriteIds

        val filtered = _uiState.value.exercises.filter { ex ->
            val matchesQuery = query.isEmpty() || ex.name.lowercase().contains(query) || ex.targetMuscle.lowercase().contains(query)
            val matchesCat = when (cat) {
                "All" -> true
                "Favorites" -> favIds.contains(ex.id)
                "Upper Abs" -> ex.category == "upper_abs"
                "Lower Abs" -> ex.category == "lower_abs"
                "Obliques" -> ex.category == "obliques"
                "Core" -> ex.category == "core" || ex.category == "full_core"
                "Stretch" -> ex.category == "stretch"
                else -> true
            }
            matchesQuery && matchesCat
        }

        _uiState.update { it.copy(filteredExercises = filtered) }
    }
}
