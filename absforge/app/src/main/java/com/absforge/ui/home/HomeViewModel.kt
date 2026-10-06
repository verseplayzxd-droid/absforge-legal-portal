package com.absforge.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.absforge.data.local.AbsForgeDatabase
import com.absforge.data.preferences.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

data class TodayExerciseItem(
    val id: Int,
    val name: String,
    val animationId: String,
    val targetMuscle: String,
    val durationSeconds: Int,
    val reps: Int? = null
)

data class DayProgressItem(
    val dayNumber: Int,
    val isCompleted: Boolean,
    val isCurrent: Boolean
)

data class QuickWorkout(
    val id: String,
    val name: String,
    val durationMinutes: Int,
    val calories: Int,
    val animationId: String,
    val targetMuscle: String
)

data class HomeUiState(
    val userName: String = "Athlete",
    val greeting: String = "Good Morning",
    val currentDay: Int = 1,
    val totalDays: Int = 30,
    val currentStreak: Int = 0,
    val planId: Int = 1,
    val planName: String = "Beginner",
    val todayWorkoutName: String = "",
    val todayExerciseCount: Int = 0,
    val todayEstimatedMinutes: Int = 0,
    val todayEstimatedCalories: Int = 0,
    val todayDifficulty: String = "beginner",
    val isTodayRestDay: Boolean = false,
    val isDayCompleted: Boolean = false,
    val totalMinutes: Int = 0,
    val totalCalories: Int = 0,
    val totalWorkouts: Int = 0,
    val motivationalQuote: String = "Day 1 crushed. Keep the momentum going.",
    val todayExercises: List<TodayExerciseItem> = emptyList(),
    val weeklyProgress: List<DayProgressItem> = emptyList(),
    val featuredAnimationId: String = "crunch",
    val isLoading: Boolean = true
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AbsForgeDatabase.getDatabase(application)
    private val prefs = PreferencesManager(application)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val quickWorkouts = listOf(
        QuickWorkout("five_min_abs", "5 Min Abs", 5, 35, "crunch", "Upper & Mid Abs"),
        QuickWorkout("lower_abs", "Lower Abs Focus", 8, 55, "leg_raise", "Lower Core & V-Line"),
        QuickWorkout("core_burner", "Core Burner", 10, 80, "mountain_climbers", "Full Core & Stamina"),
        QuickWorkout("plank_challenge", "Plank Challenge", 7, 50, "plank", "Isometric Endurance"),
        QuickWorkout("oblique_blast", "Oblique Blast", 8, 60, "russian_twist", "Side Abs & Obliques"),
        QuickWorkout("stretch_recovery", "Stretch & Recovery", 6, 25, "cobra_stretch", "Spine & Mobility")
    )

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                db.sessionDao().getAllSessions(),
                prefs.selectedPlanId,
                prefs.currentDay
            ) { sessions, rawPlanId, rawDay ->
                withContext(Dispatchers.IO) {
                    val calendar = Calendar.getInstance()
                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                    val greetStr = when {
                        hour in 5..11 -> "Good Morning"
                        hour in 12..17 -> "Good Afternoon"
                        else -> "Good Evening"
                    }

                    val userProfile = db.userProfileDao().getProfileSync()
                    val uName = userProfile?.name ?: "Athlete"

                    val pId = if (rawPlanId in 1..3) rawPlanId else 1
                    val cDay = if (rawDay in 1..30) rawDay else 1

                    val plan = db.workoutDao().getPlanById(pId)
                    val pName = plan?.name ?: "Beginner"
                    val tDays = plan?.totalDays ?: 30

                    val wDay = db.workoutDao().getDayByNumber(pId, cDay)
                    val restFlag = wDay?.isRestDay ?: false

                    var exCount = 0
                    var estMin = 0
                    var estCal = 0
                    val diff = pName.lowercase()
                    val todayExerciseList = mutableListOf<TodayExerciseItem>()
                    var featuredAnim = "crunch"

                    if (!restFlag && wDay != null) {
                        val dayExercises = db.workoutDao().getExercisesForDaySync(wDay.id)
                        exCount = dayExercises.size
                        estMin = wDay.estimatedMinutes
                        estCal = wDay.estimatedCalories

                        for (de in dayExercises) {
                            val exEntity = db.exerciseDao().getById(de.exerciseId)
                            if (exEntity != null) {
                                todayExerciseList.add(
                                    TodayExerciseItem(
                                        id = exEntity.id,
                                        name = exEntity.name,
                                        animationId = exEntity.animationId,
                                        targetMuscle = exEntity.targetMuscle,
                                        durationSeconds = de.durationSeconds?.takeIf { it > 0 }
                                            ?: exEntity.defaultDurationSeconds.takeIf { it > 0 } ?: 30,
                                        reps = de.reps ?: exEntity.defaultReps.takeIf { it > 0 }
                                    )
                                )
                            }
                        }
                        if (todayExerciseList.isNotEmpty()) {
                            featuredAnim = todayExerciseList.first().animationId
                        }
                    }

                    val qualifyingSessions = sessions.filter {
                        (it.completedExercises.toFloat() / it.totalExercises.coerceAtLeast(1)) >= 0.90f
                    }
                    val totalM = qualifyingSessions.sumOf { it.durationSeconds } / 60
                    val totalC = qualifyingSessions.sumOf { it.caloriesBurned }
                    val totalW = qualifyingSessions.size

                    // Check if current day is already completed today
                    val dayCompleted = qualifyingSessions.any { it.planId == pId && it.dayNumber == cDay }

                    // Calculate Streak
                    var streakCount = 0
                    val sortedSessions = qualifyingSessions.filter { it.endTime != null }
                        .sortedByDescending { it.endTime ?: 0L }

                    if (sortedSessions.isNotEmpty()) {
                        val todayDay = System.currentTimeMillis() / (1000 * 60 * 60 * 24)
                        var currentStreakDay = (sortedSessions.first().endTime ?: 0L) / (1000 * 60 * 60 * 24)

                        if (todayDay - currentStreakDay <= 1) {
                            streakCount = 1
                            for (i in 1 until sortedSessions.size) {
                                val sessionDay = (sortedSessions[i].endTime ?: 0L) / (1000 * 60 * 60 * 24)
                                if (currentStreakDay - sessionDay == 1L) {
                                    streakCount++
                                    currentStreakDay = sessionDay
                                } else if (currentStreakDay - sessionDay > 1L) {
                                    break
                                }
                            }
                        }
                    }

                    // 7-Day Current Week Progress
                    val weekStart = (((cDay - 1) / 7) * 7) + 1
                    val weekEnd = (weekStart + 6).coerceAtMost(30)
                    val weekDays = (weekStart..weekEnd).map { d ->
                        DayProgressItem(
                            dayNumber = d,
                            isCompleted = qualifyingSessions.any { it.planId == pId && it.dayNumber == d },
                            isCurrent = d == cDay
                        )
                    }

                    val quotes = listOf(
                        "Day $cDay crushed. Keep the momentum going!",
                        "One workout down. ${30 - cDay} days to go.",
                        "Great work, $uName. Come back tomorrow and keep your streak alive!",
                        "Consistency is key. You're building an unstoppable core."
                    )
                    val quoteStr = quotes[cDay % quotes.size]

                    HomeUiState(
                        userName = uName,
                        greeting = greetStr,
                        currentDay = cDay,
                        totalDays = tDays,
                        currentStreak = streakCount,
                        planId = pId,
                        planName = pName,
                        todayWorkoutName = wDay?.name ?: "Rest & Recovery",
                        todayExerciseCount = exCount,
                        todayEstimatedMinutes = estMin,
                        todayEstimatedCalories = estCal,
                        todayDifficulty = diff,
                        isTodayRestDay = restFlag,
                        isDayCompleted = dayCompleted,
                        totalMinutes = totalM,
                        totalCalories = totalC,
                        totalWorkouts = totalW,
                        motivationalQuote = quoteStr,
                        todayExercises = todayExerciseList,
                        weeklyProgress = weekDays,
                        featuredAnimationId = featuredAnim,
                        isLoading = false
                    )
                }
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }
}
