package com.absforge.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.absforge.AbsForgeApplication
import com.absforge.data.local.entity.WorkoutSessionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class HistoryDisplayItem(
    val id: Int,
    val dateStr: String,
    val dayNumber: Int,
    val workoutName: String,
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val completionPercent: Int,
    val isQualified: Boolean
)

data class WorkoutHistoryUiState(
    val items: List<HistoryDisplayItem> = emptyList(),
    val isLoading: Boolean = true
)

class WorkoutHistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val db = (application as AbsForgeApplication).database

    private val _uiState = MutableStateFlow(WorkoutHistoryUiState())
    val uiState: StateFlow<WorkoutHistoryUiState> = _uiState.asStateFlow()

    init {
        observeHistory()
    }

    private fun observeHistory() {
        viewModelScope.launch {
            db.sessionDao().getAllSessions().collect { sessions ->
                withContext(Dispatchers.IO) {
                    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

                    // Filter out sessions that were never finished (endTime == null or 0)
                    val finishedSessions = sessions.filter { (it.endTime ?: 0L) > 0L }
                        .sortedByDescending { it.endTime ?: 0L }

                    val displayItems = finishedSessions.map { session ->
                        val time = session.endTime ?: session.startTime
                        val dateStr = dateFormat.format(Date(time))
                        val dayNum = session.dayNumber

                        val workoutDay = db.workoutDao().getDayByNumber(session.planId, dayNum)
                        val name = workoutDay?.name ?: "Abs Workout"

                        val totalEx = session.totalExercises.coerceAtLeast(1)
                        val completionPct = ((session.completedExercises.toFloat() / totalEx) * 100).toInt()
                        val isQual = completionPct >= 90

                        HistoryDisplayItem(
                            id = session.id,
                            dateStr = dateStr,
                            dayNumber = dayNum,
                            workoutName = name,
                            durationMinutes = session.durationSeconds / 60,
                            caloriesBurned = session.caloriesBurned,
                            completionPercent = completionPct,
                            isQualified = isQual
                        )
                    }

                    _uiState.update {
                        it.copy(
                            items = displayItems,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }
}
