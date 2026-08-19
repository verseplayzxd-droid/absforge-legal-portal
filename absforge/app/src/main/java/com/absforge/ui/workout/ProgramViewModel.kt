package com.absforge.ui.workout

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.absforge.data.local.AbsForgeDatabase
import com.absforge.data.preferences.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class DayState { COMPLETED, CURRENT, AVAILABLE, LOCKED, REST }

data class ProgramUiState(
    val selectedTabIndex: Int = 0,
    val currentDay: Int = 1,
    val selectedPlanId: Int = 1,
    val completedDays: Set<Int> = emptySet(),
    val restDays: Set<Int> = emptySet(),
    val progressPercent: Float = 0f,
    val completedCount: Int = 0,
    val currentStreak: Int = 0,
    val isLoading: Boolean = true
)

class ProgramViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AbsForgeDatabase.getDatabase(application)
    private val prefs = PreferencesManager(application)

    private val _uiState = MutableStateFlow(ProgramUiState())
    val uiState: StateFlow<ProgramUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun onTabSelected(index: Int) {
        val planId = index + 1
        _uiState.update { it.copy(selectedTabIndex = index, selectedPlanId = planId) }
        loadPlanData(planId)
    }

    private fun loadData() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val rawPlanId = prefs.selectedPlanId.first()
                    val initialPlanId = if (rawPlanId in 1..3) rawPlanId else 1
                    val initialTabIndex = (initialPlanId - 1).coerceIn(0, 2)
                    _uiState.update { it.copy(selectedPlanId = initialPlanId, selectedTabIndex = initialTabIndex) }
                    loadPlanDataInternal(initialPlanId)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun loadPlanData(planId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                loadPlanDataInternal(planId)
            }
        }
    }

    private suspend fun loadPlanDataInternal(planId: Int) {
        _uiState.update { it.copy(isLoading = true) }
        try {
            val rawDay = prefs.currentDay.first()
            val currentDay = if (rawDay in 1..30) rawDay else 1

            val completedDaysList = db.sessionDao().getCompletedDays(planId).first()
            val completedDays = completedDaysList.toSet()

            val workoutDays = db.workoutDao().getDaysForPlan(planId).first()
            val restDays = workoutDays.filter { it.isRestDay }.map { it.dayNumber }.toSet()

            val totalDays = workoutDays.size.coerceAtLeast(30)
            val completedCount = completedDays.size
            val progressPercent = if (totalDays > 0) (completedCount.toFloat() / totalDays.toFloat()) else 0f

            _uiState.update {
                it.copy(
                    currentDay = currentDay,
                    completedDays = completedDays,
                    restDays = restDays,
                    progressPercent = progressPercent,
                    completedCount = completedCount,
                    isLoading = false
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun getDayState(dayNumber: Int): DayState {
        val state = _uiState.value
        return when {
            state.completedDays.contains(dayNumber) -> DayState.COMPLETED
            dayNumber == state.currentDay -> DayState.CURRENT
            state.restDays.contains(dayNumber) -> DayState.REST
            dayNumber < state.currentDay -> DayState.AVAILABLE
            else -> DayState.LOCKED
        }
    }
}
