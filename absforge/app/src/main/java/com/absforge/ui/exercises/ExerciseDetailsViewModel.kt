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

data class ExerciseDetailsUiState(
    val exercise: ExerciseEntity? = null,
    val alternatives: List<ExerciseEntity> = emptyList(),
    val isFavorite: Boolean = false,
    val isLoading: Boolean = true
)

class ExerciseDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = (application as AbsForgeApplication).database

    private val _uiState = MutableStateFlow(ExerciseDetailsUiState())
    val uiState: StateFlow<ExerciseDetailsUiState> = _uiState.asStateFlow()

    fun loadExercise(exerciseId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val ex = db.exerciseDao().getById(exerciseId)
                    _uiState.update { it.copy(exercise = ex, isLoading = false) }

                    if (ex != null) {
                        if (ex.alternativeExerciseIds.isNotEmpty()) {
                            val ids = ex.alternativeExerciseIds.split(",").mapNotNull { it.trim().toIntOrNull() }
                            val alts = mutableListOf<ExerciseEntity>()
                            ids.forEach { id ->
                                val altEx = db.exerciseDao().getById(id)
                                if (altEx != null) alts.add(altEx)
                            }
                            _uiState.update { it.copy(alternatives = alts) }
                        }

                        db.favoriteDao().isFavorite(ex.id).collect { isFav ->
                            _uiState.update { it.copy(isFavorite = isFav) }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun toggleFavorite() {
        val ex = _uiState.value.exercise ?: return
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    if (_uiState.value.isFavorite) {
                        db.favoriteDao().remove(ex.id)
                    } else {
                        db.favoriteDao().insert(FavoriteExerciseEntity(exerciseId = ex.id, addedAt = DateUtils.nowEpochMs()))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
