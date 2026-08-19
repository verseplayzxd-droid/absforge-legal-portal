package com.absforge.ui.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.absforge.AbsForgeApplication
import com.absforge.data.local.entity.UserProfileEntity
import com.absforge.data.local.entity.WeightEntryEntity
import com.absforge.data.seed.DatabaseSeeder
import com.absforge.notifications.ReminderScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {

    private val absForgeApp = application as AbsForgeApplication
    private val database = absForgeApp.database
    private val preferencesManager = absForgeApp.preferencesManager

    val selectedGoal = MutableStateFlow("")
    val fitnessLevel = MutableStateFlow("")
    val gender = MutableStateFlow("")
    val age = MutableStateFlow("")
    val height = MutableStateFlow("")
    val weight = MutableStateFlow("")
    val targetWeight = MutableStateFlow("")
    val useMetric = MutableStateFlow(true)
    val preferredTrainTime = MutableStateFlow("")

    val selectedPlanId = MutableStateFlow(1)
    val isSaving = MutableStateFlow(false)
    val saveSuccess = MutableStateFlow(false)
    val errorMessage = MutableStateFlow<String?>(null)

    private var saveStartedFlag = false

    /**
     * Called from BuildPlanScreen. Runs in viewModelScope so it survives
     * composable recomposition / navigation transitions.
     */
    fun startSaveProfile() {
        if (saveStartedFlag) return
        saveStartedFlag = true

        viewModelScope.launch {
            saveProfileInternal()
        }
    }

    private suspend fun saveProfileInternal() {
        withContext(Dispatchers.IO) {
            try {
                isSaving.value = true
                errorMessage.value = null

                val goalStr = selectedGoal.value.ifBlank { "strong_abs" }
                val levelStr = fitnessLevel.value.ifBlank { "beginner" }
                val genderStr = gender.value.ifBlank { "Male" }
                val ageVal = age.value.toIntOrNull() ?: 25
                val heightVal = height.value.toFloatOrNull() ?: 170f
                val weightVal = weight.value.toFloatOrNull() ?: 70f
                val targetWeightVal = targetWeight.value.toFloatOrNull() ?: 65f
                val trainTimeStr = preferredTrainTime.value.ifBlank { "morning" }

                val planId = when (levelStr.lowercase()) {
                    "beginner" -> 1
                    "intermediate" -> 2
                    "advanced" -> 3
                    else -> 1
                }
                selectedPlanId.value = planId

                // 1. Ensure database seeder completes seeding exercises, plans, days and exercises
                val seeder = DatabaseSeeder(database)
                seeder.seedIfNeeded()

                // 2. Validate selected plan exists in DB
                val plan = database.workoutDao().getPlanById(planId)
                if (plan == null) {
                    throw IllegalStateException("Failed to seed workout plan $planId")
                }

                // 3. User profile entity insertion
                val userProfile = UserProfileEntity(
                    id = 1,
                    name = "Athlete",
                    gender = genderStr,
                    age = ageVal,
                    heightCm = heightVal,
                    weightKg = weightVal,
                    targetWeightKg = targetWeightVal,
                    useMetric = useMetric.value,
                    goal = goalStr,
                    fitnessLevel = levelStr,
                    preferredTrainTime = trainTimeStr,
                    createdAt = System.currentTimeMillis()
                )
                database.userProfileDao().insertProfile(userProfile)

                // 4. Initial weight entry insertion
                val initialWeight = WeightEntryEntity(
                    weightKg = weightVal,
                    date = System.currentTimeMillis()
                )
                database.weightDao().insert(initialWeight)

                // 5. Save DataStore preferences
                preferencesManager.setSelectedPlanId(planId)
                preferencesManager.setCurrentDay(1)

                // 6. Safely parse and schedule reminders
                val (hour, minute) = parseTrainTime(trainTimeStr)
                preferencesManager.setReminderHour(hour)
                preferencesManager.setReminderMinute(minute)
                preferencesManager.setReminderEnabled(true)

                try {
                    ReminderScheduler.scheduleReminder(absForgeApp, hour, minute)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                // 7. ONLY mark onboarding completed after all operations succeed!
                preferencesManager.setOnboardingCompleted(true)

                isSaving.value = false
                saveSuccess.value = true
            } catch (e: Exception) {
                e.printStackTrace()
                isSaving.value = false
                saveSuccess.value = false
                errorMessage.value = e.localizedMessage ?: "Failed to save profile and initialize workout plan."
                saveStartedFlag = false // Allow retry
            }
        }
    }

    suspend fun saveProfile(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                saveProfileInternal()
                saveSuccess.value
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    private fun parseTrainTime(trainTime: String): Pair<Int, Int> {
        return when (trainTime.lowercase()) {
            "morning" -> Pair(8, 0)
            "afternoon" -> Pair(14, 0)
            "evening" -> Pair(19, 0)
            "custom" -> Pair(20, 0)
            else -> {
                if (trainTime.contains(":")) {
                    val parts = trainTime.split(":")
                    val h = parts.getOrNull(0)?.toIntOrNull() ?: 8
                    val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
                    Pair(h.coerceIn(0, 23), m.coerceIn(0, 59))
                } else {
                    Pair(8, 0)
                }
            }
        }
    }
}
