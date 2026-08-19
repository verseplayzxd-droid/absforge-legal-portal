package com.absforge.ui.progress

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.absforge.AbsForgeApplication
import com.absforge.data.local.entity.BodyMeasurementEntity
import com.absforge.data.local.entity.WeightEntryEntity
import com.absforge.utils.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

data class ProgressUiState(
    val currentStreak: Int = 0,
    val totalWorkouts: Int = 0,
    val totalMinutes: Int = 0,
    val totalCalories: Int = 0,
    val completedDays: Int = 0,
    val totalDays: Int = 30,
    val progressPercent: Float = 0f,
    val weeklyMinutes: List<Pair<String, Int>> = listOf(
        "Mon" to 0, "Tue" to 0, "Wed" to 0, "Thu" to 0, "Fri" to 0, "Sat" to 0, "Sun" to 0
    ),
    val weightEntries: List<Pair<Long, Float>> = emptyList(),
    val currentWeight: Float = 0f,
    val startingWeight: Float = 0f,
    val targetWeight: Float = 0f,
    val bmi: Float = 0f,
    val heightCm: Float = 0f,
    val selectedTab: Int = 0,
    val isLoading: Boolean = true
)

class ProgressViewModel(application: Application) : AndroidViewModel(application) {

    private val db = (application as AbsForgeApplication).database

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            db.sessionDao().getAllSessions().collect { sessions ->
                withContext(Dispatchers.IO) {
                    try {
                        val completed = sessions.filter {
                            (it.completedExercises.toFloat() / it.totalExercises.coerceAtLeast(1)) >= 0.90f
                        }
                        val totalWorkouts = completed.size
                        val totalMins = completed.sumOf { it.durationSeconds } / 60
                        val totalCals = completed.sumOf { it.caloriesBurned }
                        val completedDaysCount = completed.map { it.dayNumber }.distinct().size

                        // Dynamic weekly activity Mon..Sun
                        val cal = Calendar.getInstance()
                        cal.firstDayOfWeek = Calendar.MONDAY
                        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                        cal.set(Calendar.HOUR_OF_DAY, 0)
                        cal.set(Calendar.MINUTE, 0)
                        cal.set(Calendar.SECOND, 0)
                        cal.set(Calendar.MILLISECOND, 0)

                        val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                        val weekly = daysOfWeek.mapIndexed { index, dayName ->
                            val dayStart = cal.timeInMillis + (index * 86400000L)
                            val dayEnd = dayStart + 86400000L
                            val minsOnDay = completed.filter { session ->
                                val time = session.endTime ?: session.startTime
                                time in dayStart until dayEnd
                            }.sumOf { it.durationSeconds } / 60

                            dayName to minsOnDay
                        }

                        // Compute streak
                        var streakCount = 0
                        val sortedSessions = completed.filter { it.endTime != null }
                            .sortedByDescending { it.endTime ?: 0L }

                        if (sortedSessions.isNotEmpty()) {
                            val todayDay = System.currentTimeMillis() / 86400000L
                            var currentStreakDay = (sortedSessions.first().endTime ?: 0L) / 86400000L

                            if (todayDay - currentStreakDay <= 1) {
                                streakCount = 1
                                for (i in 1 until sortedSessions.size) {
                                    val sessionDay = (sortedSessions[i].endTime ?: 0L) / 86400000L
                                    if (currentStreakDay - sessionDay == 1L) {
                                        streakCount++
                                        currentStreakDay = sessionDay
                                    } else if (currentStreakDay - sessionDay > 1L) {
                                        break
                                    }
                                }
                            }
                        }

                        val profile = db.userProfileDao().getProfileSync()
                        val h = profile?.heightCm ?: 175f
                        val w = profile?.weightKg ?: 70f
                        val bmiVal = if (h > 0) w / ((h / 100f) * (h / 100f)) else 22.5f

                        _uiState.update {
                            it.copy(
                                currentStreak = streakCount,
                                totalWorkouts = totalWorkouts,
                                totalMinutes = totalMins,
                                totalCalories = totalCals,
                                completedDays = completedDaysCount,
                                progressPercent = (completedDaysCount.toFloat() / 30f).coerceIn(0f, 1f),
                                weeklyMinutes = weekly,
                                currentWeight = w,
                                startingWeight = profile?.weightKg ?: 70f,
                                targetWeight = profile?.targetWeightKg ?: 65f,
                                bmi = bmiVal,
                                heightCm = h,
                                isLoading = false
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
            }
        }
    }

    fun setTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun addWeightEntry(weightKg: Float) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val entry = WeightEntryEntity(weightKg = weightKg, date = DateUtils.nowEpochMs())
                    db.weightDao().insert(entry)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun addMeasurement(waistCm: Float?, chestCm: Float?, bodyFat: Float?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val m = BodyMeasurementEntity(
                        waistCm = waistCm,
                        chestCm = chestCm,
                        bodyFatPercent = bodyFat,
                        date = DateUtils.nowEpochMs()
                    )
                    db.measurementDao().insert(m)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
