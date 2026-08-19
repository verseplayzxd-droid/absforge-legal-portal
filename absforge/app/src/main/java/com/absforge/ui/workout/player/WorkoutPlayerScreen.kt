package com.absforge.ui.workout.player

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.absforge.ui.theme.AbsForgePrimary

@Composable
fun WorkoutPlayerScreen(
    planId: Int,
    dayNumber: Int,
    isQuickWorkout: Boolean,
    onWorkoutComplete: (sessionId: Int) -> Unit,
    onExit: () -> Unit,
    viewModel: WorkoutPlayerViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        if (state.sessionId == -1) {
            viewModel.initWorkout(planId, dayNumber, isQuickWorkout, null)
        }
    }

    if (state.isWorkoutComplete) {
        LaunchedEffect(Unit) {
            onWorkoutComplete(state.sessionId)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (state.phase) {
            WorkoutPhase.LOADING -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = AbsForgePrimary)
            }
            WorkoutPhase.COUNTDOWN -> {
                val exercise = state.exercises.getOrNull(state.currentExerciseIndex)
                PreCountdownScreen(
                    countdownValue = state.countdownValue,
                    exerciseName = exercise?.name ?: ""
                )
            }
            WorkoutPhase.EXERCISE_ACTIVE, WorkoutPhase.PAUSED, WorkoutPhase.EXERCISE_COMPLETE, WorkoutPhase.SET_COMPLETE -> {
                val exercise = state.exercises.getOrNull(state.currentExerciseIndex)
                if (exercise != null) {
                    ActiveExerciseScreen(
                        exercise = exercise,
                        state = state,
                        onPause = viewModel::pause,
                        onResume = viewModel::resume,
                        onComplete = viewModel::completeExercise,
                        onToggleSound = viewModel::toggleSound,
                        onExit = onExit
                    )
                }

                if (state.phase == WorkoutPhase.EXERCISE_COMPLETE && exercise != null) {
                    val repOrDurStr = exercise.reps?.let { "$it Reps" } ?: "${exercise.durationSeconds ?: 30} Seconds"
                    ExerciseCompleteOverlay(
                        exerciseName = exercise.name,
                        repsOrDuration = repOrDurStr,
                        onContinue = viewModel::continueToRest,
                        onRetry = viewModel::restartExercise
                    )
                }

                if (state.phase == WorkoutPhase.SET_COMPLETE) {
                    SetCompleteOverlay(
                        state = state,
                        onContinue = viewModel::proceedFromSetComplete
                    )
                }

                if (state.phase == WorkoutPhase.PAUSED) {
                    PauseOverlay(
                        exerciseName = exercise?.name ?: "",
                        onResume = viewModel::resume,
                        onRestart = viewModel::restartExercise,
                        onEndWorkout = viewModel::endWorkout
                    )
                }
            }
            WorkoutPhase.REST -> {
                RestTimerScreen(
                    state = state,
                    onAddRestTime = { viewModel.addRestTime(20) },
                    onSkipRest = viewModel::skipRest,
                    onPause = viewModel::pause
                )
            }
            WorkoutPhase.WORKOUT_COMPLETE -> {
                // Handled by LaunchedEffect above
            }
        }
    }
}
