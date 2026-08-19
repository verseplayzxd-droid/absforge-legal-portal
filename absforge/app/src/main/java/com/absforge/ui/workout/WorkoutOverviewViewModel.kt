package com.absforge.ui.workout

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.absforge.data.local.AbsForgeDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ExerciseDisplayItem(
    val exerciseId: Int,
    val name: String,
    val category: String,
    val targetMuscle: String,
    val reps: Int?,
    val durationSeconds: Int?,
    val restAfterSeconds: Int,
    val animationId: String,
    val repType: String
)

data class WorkoutOverviewUiState(
    val dayNumber: Int = 1,
    val workoutName: String = "",
    val estimatedMinutes: Int = 0,
    val estimatedCalories: Int = 0,
    val exerciseCount: Int = 0,
    val difficulty: String = "",
    val exercises: List<ExerciseDisplayItem> = emptyList(),
    val isLoading: Boolean = true
)

class WorkoutOverviewViewModel(
    application: Application,
    private val planId: Int,
    private val dayNumber: Int
) : AndroidViewModel(application) {

    private val db = AbsForgeDatabase.getDatabase(application)
    private val _uiState = MutableStateFlow(WorkoutOverviewUiState())
    val uiState: StateFlow<WorkoutOverviewUiState> = _uiState.asStateFlow()

    init {
        loadWorkoutData()
    }

    private fun loadWorkoutData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            withContext(Dispatchers.IO) {
                try {
                    val plan = db.workoutDao().getPlanById(planId)
                    val workoutDay = db.workoutDao().getDayByNumber(planId, dayNumber)

                    if (workoutDay != null) {
                        val dayExercises = db.workoutDao().getExercisesForDaySync(workoutDay.id)

                        val displayItems = dayExercises.mapNotNull { dayEx ->
                            val ex = db.exerciseDao().getById(dayEx.exerciseId)
                            if (ex != null) {
                                ExerciseDisplayItem(
                                    exerciseId = ex.id,
                                    name = ex.name,
                                    category = ex.category,
                                    targetMuscle = ex.targetMuscle,
                                    reps = dayEx.reps,
                                    durationSeconds = dayEx.durationSeconds,
                                    restAfterSeconds = dayEx.restAfterSeconds,
                                    animationId = ex.animationId,
                                    repType = ex.repType
                                )
                            } else null
                        }

                        val estimatedMinutes = workoutDay.estimatedMinutes
                        val estimatedCalories = workoutDay.estimatedCalories

                        _uiState.update {
                            it.copy(
                                dayNumber = dayNumber,
                                workoutName = workoutDay.name,
                                estimatedMinutes = estimatedMinutes,
                                estimatedCalories = estimatedCalories,
                                exerciseCount = displayItems.size,
                                difficulty = plan?.name ?: "Beginner",
                                exercises = displayItems,
                                isLoading = false
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    class Factory(
        private val application: Application,
        private val planId: Int,
        private val dayNumber: Int
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WorkoutOverviewViewModel(application, planId, dayNumber) as T
        }
    }
}
