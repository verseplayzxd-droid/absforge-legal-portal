package com.absforge.ui.workout.player

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.absforge.AbsForgeApplication
import com.absforge.data.local.entity.WorkoutSessionEntity
import com.absforge.data.preferences.PreferencesManager
import com.absforge.data.repository.WorkoutRepository
import com.absforge.data.seed.QuickWorkoutSeedData
import com.absforge.audio.HapticManager
import com.absforge.audio.SoundManager
import com.absforge.audio.VoiceCoachManager
import com.absforge.ads.AdMobManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class WorkoutPhase {
    LOADING, COUNTDOWN, EXERCISE_ACTIVE, EXERCISE_COMPLETE, SET_COMPLETE, REST, PAUSED, WORKOUT_COMPLETE
}

data class WorkoutExercise(
    val exerciseId: Int,
    val name: String,
    val category: String,
    val targetMuscle: String,
    val instructions: String,
    val tips: String,
    val reps: Int? = null,
    val durationSeconds: Int = 30,
    val restAfterSeconds: Int = 20,
    val animationId: String,
    val isCompleted: Boolean = false,
    val isSkipped: Boolean = false
)

data class WorkoutPlayerState(
    val phase: WorkoutPhase = WorkoutPhase.LOADING,
    val exercises: List<WorkoutExercise> = emptyList(),
    val currentExerciseIndex: Int = 0,
    val totalExercises: Int = 0,
    val countdownValue: Int = 3,
    val exerciseTimeRemainingMs: Long = 30000L,
    val exerciseTotalTimeMs: Long = 30000L,
    val restTimeRemainingMs: Long = 20000L,
    val restTotalTimeMs: Long = 20000L,
    val isPaused: Boolean = false,
    val totalElapsedSeconds: Int = 0,
    val completedExerciseCount: Int = 0,
    val completedExercisesSinceLastSetAd: Int = 0,
    val currentSetNumber: Int = 1,
    val estimatedCaloriesBurned: Int = 0,
    val currentStreak: Int = 0,
    val sessionId: Int = -1,
    val planId: Int = 1,
    val dayNumber: Int = 1,
    val isQuickWorkout: Boolean = false,
    val isWorkoutComplete: Boolean = false,
    val soundEnabled: Boolean = true,
    val voiceEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val autoStartNext: Boolean = true,
    val countdownEnabled: Boolean = true,
    val defaultRestSeconds: Int = 20,
    val nextExerciseName: String = "",
    val nextExerciseReps: String = "",
    val showRecoveryDialog: Boolean = false
)

class WorkoutPlayerViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AbsForgeApplication.instance.database
    private val preferencesManager = PreferencesManager(application)
    private val workoutRepository = WorkoutRepository(db, preferencesManager)

    private val _state = MutableStateFlow(WorkoutPlayerState())
    val state: StateFlow<WorkoutPlayerState> = _state.asStateFlow()

    private var timerJob: Job? = null
    private var totalTimerJob: Job? = null
    private val voiceCoachManager: VoiceCoachManager by lazy { VoiceCoachManager(application) }

    fun initWorkout(planId: Int, dayNumber: Int, isQuickWorkout: Boolean, quickWorkoutIdStr: String? = null) {
        viewModelScope.launch {
            val sound = preferencesManager.soundEffectsEnabled.first()
            val voice = preferencesManager.voiceGuidanceEnabled.first()
            val vib = preferencesManager.vibrationEnabled.first()
            val autoStart = preferencesManager.autoStartNext.first()
            val countdown = preferencesManager.countdownEnabled.first()
            val defaultRest = preferencesManager.defaultRestSeconds.first()

            SoundManager.soundEffectsEnabled = sound
            SoundManager.countdownSoundsEnabled = countdown
            VoiceCoachManager.voiceGuidanceEnabled = voice
            HapticManager.vibrationEnabled = vib

            AdMobManager.loadInterstitial(getApplication())

            _state.update {
                it.copy(
                    soundEnabled = sound,
                    voiceEnabled = voice,
                    vibrationEnabled = vib,
                    autoStartNext = autoStart,
                    countdownEnabled = countdown,
                    defaultRestSeconds = if (defaultRest > 0) defaultRest else 20,
                    planId = planId,
                    dayNumber = dayNumber,
                    isQuickWorkout = isQuickWorkout
                )
            }

            withContext(Dispatchers.IO) {
                try {
                    val loadedExercises = mutableListOf<WorkoutExercise>()

                    if (isQuickWorkout && !quickWorkoutIdStr.isNullOrBlank()) {
                        val quickData = QuickWorkoutSeedData.workouts[quickWorkoutIdStr]
                            ?: QuickWorkoutSeedData.workouts["five_min_abs"]
                        if (quickData != null) {
                            for (dayEx in quickData.exercises) {
                                val exEntity = db.exerciseDao().getById(dayEx.exerciseId)
                                if (exEntity != null) {
                                    val durSec = dayEx.durationSeconds?.takeIf { it > 0 }
                                        ?: exEntity.defaultDurationSeconds.takeIf { it > 0 }
                                        ?: 30
                                    loadedExercises.add(
                                        WorkoutExercise(
                                            exerciseId = exEntity.id,
                                            name = exEntity.name,
                                            category = exEntity.category,
                                            targetMuscle = exEntity.targetMuscle,
                                            instructions = exEntity.instructions,
                                            tips = exEntity.tips,
                                            reps = dayEx.reps ?: exEntity.defaultReps.takeIf { it > 0 },
                                            durationSeconds = durSec,
                                            restAfterSeconds = dayEx.restAfterSeconds,
                                            animationId = exEntity.animationId
                                        )
                                    )
                                }
                            }
                        }
                    } else {
                        val workoutDay = db.workoutDao().getDayByNumber(planId, dayNumber)
                        if (workoutDay != null) {
                            val dayExercises = db.workoutDao().getExercisesForDaySync(workoutDay.id)
                            for (dayEx in dayExercises) {
                                val exEntity = db.exerciseDao().getById(dayEx.exerciseId)
                                if (exEntity != null) {
                                    val durSec = dayEx.durationSeconds?.takeIf { it > 0 }
                                        ?: exEntity.defaultDurationSeconds.takeIf { it > 0 }
                                        ?: 30
                                    loadedExercises.add(
                                        WorkoutExercise(
                                            exerciseId = exEntity.id,
                                            name = exEntity.name,
                                            category = exEntity.category,
                                            targetMuscle = exEntity.targetMuscle,
                                            instructions = exEntity.instructions,
                                            tips = exEntity.tips,
                                            reps = dayEx.reps ?: exEntity.defaultReps.takeIf { it > 0 },
                                            durationSeconds = durSec,
                                            restAfterSeconds = dayEx.restAfterSeconds,
                                            animationId = exEntity.animationId
                                        )
                                    )
                                }
                            }
                        }
                    }

                    if (loadedExercises.isEmpty()) {
                        val allEx = db.exerciseDao().getAllExercises().first()
                        val pool = if (allEx.isNotEmpty()) allEx.take(11) else emptyList()
                        for (ex in pool) {
                            val durSec = ex.defaultDurationSeconds.takeIf { it > 0 } ?: 30
                            loadedExercises.add(
                                WorkoutExercise(
                                    exerciseId = ex.id,
                                    name = ex.name,
                                    category = ex.category,
                                    targetMuscle = ex.targetMuscle,
                                    instructions = ex.instructions,
                                    tips = ex.tips,
                                    reps = ex.defaultReps.takeIf { it > 0 },
                                    durationSeconds = durSec,
                                    restAfterSeconds = 20,
                                    animationId = ex.animationId
                                )
                            )
                        }
                    }

                    val session = WorkoutSessionEntity(
                        planId = planId,
                        dayNumber = dayNumber,
                        startTime = System.currentTimeMillis(),
                        endTime = null,
                        completedExercises = 0,
                        totalExercises = loadedExercises.size,
                        caloriesBurned = 0,
                        durationSeconds = 0,
                        feedback = null
                    )
                    val sessionId = db.sessionDao().insertSession(session).toInt()

                    _state.update {
                        it.copy(
                            phase = if (it.countdownEnabled) WorkoutPhase.COUNTDOWN else WorkoutPhase.EXERCISE_ACTIVE,
                            exercises = loadedExercises,
                            totalExercises = loadedExercises.size,
                            sessionId = sessionId
                        )
                    }
                } catch (e: Exception) {
                    Log.e("WorkoutPlayerViewModel", "Error initializing workout", e)
                }
            }

            startTotalTimer()

            if (_state.value.countdownEnabled) {
                startCountdown()
            } else {
                startExercise()
            }
        }
    }

    private fun startTotalTimer() {
        totalTimerJob?.cancel()
        totalTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (!_state.value.isPaused && _state.value.phase != WorkoutPhase.WORKOUT_COMPLETE) {
                    _state.update { it.copy(totalElapsedSeconds = it.totalElapsedSeconds + 1) }
                }
            }
        }
    }

    fun startCountdown() {
        if (!_state.value.countdownEnabled) {
            startExercise()
            return
        }
        _state.update { it.copy(phase = WorkoutPhase.COUNTDOWN, countdownValue = 3) }
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            for (i in 3 downTo 1) {
                _state.update { it.copy(countdownValue = i) }
                if (_state.value.soundEnabled) {
                    HapticManager.countdownTick(getApplication())
                }
                delay(1000)
            }
            _state.update { it.copy(countdownValue = 0) }
            delay(300)
            startExercise()
        }
    }

    fun startExercise() {
        val currentEx = _state.value.exercises.getOrNull(_state.value.currentExerciseIndex) ?: return

        // Preload next interstitial ad around Exercise 2 or 3
        if (_state.value.currentExerciseIndex == 1 || _state.value.currentExerciseIndex == 2) {
            AdMobManager.loadInterstitial(getApplication())
        }

        timerJob?.cancel()

        val durationSec = currentEx.durationSeconds.takeIf { it > 0 } ?: 30
        val totalTimeMs = durationSec * 1000L

        _state.update {
            it.copy(
                phase = WorkoutPhase.EXERCISE_ACTIVE,
                isPaused = false,
                exerciseTotalTimeMs = totalTimeMs,
                exerciseTimeRemainingMs = totalTimeMs
            )
        }

        SoundManager.playExerciseStart()
        voiceCoachManager.announceExerciseStart(currentEx.name)
        startExerciseTimer()
    }

    private fun startExerciseTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_state.value.exerciseTimeRemainingMs > 0 && !_state.value.isPaused) {
                delay(100)
                val newRemaining = (_state.value.exerciseTimeRemainingMs - 100).coerceAtLeast(0)
                _state.update { it.copy(exerciseTimeRemainingMs = newRemaining) }

                if (newRemaining % 1000L == 0L) {
                    val secondsLeft = (newRemaining / 1000L).toInt()
                    if (secondsLeft == 10) {
                        voiceCoachManager.announceTenSeconds()
                    } else if (secondsLeft in 1..3) {
                        SoundManager.playCountdownTick()
                        HapticManager.countdownTick(getApplication())
                    }
                }
            }
            if (_state.value.exerciseTimeRemainingMs <= 0 && !_state.value.isPaused) {
                completeExercise()
            }
        }
    }

    fun completeExercise() {
        timerJob?.cancel()
        SoundManager.playExerciseComplete()
        HapticManager.exerciseComplete(getApplication())
        _state.update { it.copy(phase = WorkoutPhase.EXERCISE_COMPLETE) }
    }

    fun continueToRest() {
        val currentEx = _state.value.exercises.getOrNull(_state.value.currentExerciseIndex) ?: return

        val updatedExercises = _state.value.exercises.toMutableList()
        var newCompletedCount = _state.value.completedExerciseCount
        var newSinceAdCount = _state.value.completedExercisesSinceLastSetAd

        if (!currentEx.isCompleted) {
            updatedExercises[_state.value.currentExerciseIndex] = currentEx.copy(isCompleted = true)
            newCompletedCount += 1
            newSinceAdCount += 1
            val estCals = (newCompletedCount * 12)
            _state.update {
                it.copy(
                    exercises = updatedExercises,
                    completedExerciseCount = newCompletedCount,
                    completedExercisesSinceLastSetAd = newSinceAdCount,
                    estimatedCaloriesBurned = estCals
                )
            }
        }

        val isLastExercise = _state.value.currentExerciseIndex >= _state.value.totalExercises - 1

        if (newSinceAdCount >= 4 && !isLastExercise) {
            _state.update {
                it.copy(
                    phase = WorkoutPhase.SET_COMPLETE,
                    completedExercisesSinceLastSetAd = 0
                )
            }
        } else if (isLastExercise) {
            endWorkout()
        } else {
            startRest()
        }
    }

    fun proceedFromSetComplete() {
        _state.update { it.copy(currentSetNumber = it.currentSetNumber + 1) }
        val isLastExercise = _state.value.currentExerciseIndex >= _state.value.totalExercises - 1
        if (isLastExercise) {
            endWorkout()
        } else {
            startRest()
        }
    }

    fun restartExercise() {
        timerJob?.cancel()
        val currentEx = _state.value.exercises.getOrNull(_state.value.currentExerciseIndex) ?: return
        val durationSec = currentEx.durationSeconds.takeIf { it > 0 } ?: 30
        val totalTimeMs = durationSec * 1000L

        _state.update {
            it.copy(
                phase = WorkoutPhase.EXERCISE_ACTIVE,
                isPaused = false,
                exerciseTotalTimeMs = totalTimeMs,
                exerciseTimeRemainingMs = totalTimeMs
            )
        }
        SoundManager.playExerciseStart()
        voiceCoachManager.announceExerciseStart(currentEx.name)
        startExerciseTimer()
    }

    fun skipExercise() {
        timerJob?.cancel()
        val currentEx = _state.value.exercises.getOrNull(_state.value.currentExerciseIndex) ?: return
        val updatedExercises = _state.value.exercises.toMutableList()
        updatedExercises[_state.value.currentExerciseIndex] = currentEx.copy(isSkipped = true)

        _state.update { it.copy(exercises = updatedExercises) }

        if (_state.value.currentExerciseIndex >= _state.value.totalExercises - 1) {
            endWorkout()
        } else {
            startRest()
        }
    }

    private fun startRest() {
        val currentEx = _state.value.exercises.getOrNull(_state.value.currentExerciseIndex) ?: return
        val nextEx = _state.value.exercises.getOrNull(_state.value.currentExerciseIndex + 1) ?: return

        val restSec = if (currentEx.restAfterSeconds > 0) currentEx.restAfterSeconds else _state.value.defaultRestSeconds
        val restTimeMs = restSec * 1000L

        _state.update {
            it.copy(
                phase = WorkoutPhase.REST,
                restTotalTimeMs = restTimeMs,
                restTimeRemainingMs = restTimeMs,
                nextExerciseName = nextEx.name,
                nextExerciseReps = nextEx.reps?.let { r -> "TARGET: $r REPS" } ?: "${nextEx.durationSeconds}s"
            )
        }
        SoundManager.playRestStart()
        voiceCoachManager.announceRestStart()
        voiceCoachManager.announceNextExercise(nextEx.name)
        startRestTimer()
    }

    private fun startRestTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_state.value.restTimeRemainingMs > 0 && !_state.value.isPaused) {
                delay(100)
                val newRemaining = (_state.value.restTimeRemainingMs - 100).coerceAtLeast(0)
                _state.update { it.copy(restTimeRemainingMs = newRemaining) }

                if (newRemaining % 1000L == 0L) {
                    val secondsLeft = (newRemaining / 1000L).toInt()
                    if (secondsLeft in 1..3) {
                        SoundManager.playCountdownTick()
                        HapticManager.countdownTick(getApplication())
                    }
                }
            }
            if (_state.value.restTimeRemainingMs <= 0 && !_state.value.isPaused) {
                SoundManager.playRestEnd()
                _state.update { it.copy(currentExerciseIndex = it.currentExerciseIndex + 1) }
                startExercise()
            }
        }
    }

    fun addRestTime(seconds: Int) {
        SoundManager.playButtonClick()
        HapticManager.buttonPress(getApplication())
        _state.update {
            val newRemaining = it.restTimeRemainingMs + seconds * 1000L
            it.copy(restTimeRemainingMs = newRemaining, restTotalTimeMs = it.restTotalTimeMs + seconds * 1000L)
        }
    }

    fun skipRest() {
        timerJob?.cancel()
        SoundManager.playButtonClick()
        HapticManager.buttonPress(getApplication())
        _state.update { it.copy(currentExerciseIndex = it.currentExerciseIndex + 1) }
        startExercise()
    }

    fun pause() {
        SoundManager.playPause()
        HapticManager.buttonPress(getApplication())
        _state.update { it.copy(isPaused = true, phase = WorkoutPhase.PAUSED) }
        timerJob?.cancel()
    }

    fun resume() {
        SoundManager.playResume()
        HapticManager.buttonPress(getApplication())
        _state.update { it.copy(isPaused = false) }
        val currentEx = _state.value.exercises.getOrNull(_state.value.currentExerciseIndex)
        if (currentEx != null) {
            if (_state.value.restTimeRemainingMs > 0) {
                _state.update { it.copy(phase = WorkoutPhase.REST) }
                startRestTimer()
            } else {
                _state.update { it.copy(phase = WorkoutPhase.EXERCISE_ACTIVE) }
                startExerciseTimer()
            }
        }
    }

    fun toggleSound() {
        val newSound = !_state.value.soundEnabled
        SoundManager.soundEffectsEnabled = newSound
        _state.update { it.copy(soundEnabled = newSound) }
    }

    fun endWorkout() {
        timerJob?.cancel()
        totalTimerJob?.cancel()

        SoundManager.playWorkoutComplete()
        HapticManager.workoutComplete(getApplication())
        voiceCoachManager.announceWorkoutComplete()

        val sId = _state.value.sessionId
        val compEx = _state.value.completedExerciseCount
        val totEx = _state.value.totalExercises
        val durSec = _state.value.totalElapsedSeconds
        val cals = _state.value.estimatedCaloriesBurned
        val isQuick = _state.value.isQuickWorkout

        viewModelScope.launch {
            workoutRepository.completeWorkoutSession(
                sessionId = sId,
                completedExercises = compEx,
                totalExercises = totEx,
                durationSeconds = durSec,
                caloriesBurned = cals,
                isQuickWorkout = isQuick
            )
            _state.update { it.copy(phase = WorkoutPhase.WORKOUT_COMPLETE, isWorkoutComplete = true) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        totalTimerJob?.cancel()
        voiceCoachManager.shutdown()
    }
}
