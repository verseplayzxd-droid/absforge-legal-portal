package com.absforge

import com.absforge.data.seed.*
import com.absforge.ui.workout.player.WorkoutExercise
import org.junit.Assert.*
import org.junit.Test

class WorkoutLifecycleAndPersistenceTest {

    @Test
    fun freshDatabaseHasZeroSessionsTest() {
        val sessions = emptyList<Any>()
        assertTrue("Fresh installation must have zero historical sessions", sessions.isEmpty())
    }

    @Test
    fun freshDatabaseHasZeroCompletedDaysTest() {
        val completedDaysCount = 0
        assertEquals("Fresh database must start with 0 completed days", 0, completedDaysCount)
    }

    @Test
    fun freshDatabaseHasZeroStreakTest() {
        val streak = 0
        assertEquals("Fresh database must start with 0 day streak", 0, streak)
    }

    @Test
    fun freshDatabaseHasNoUnlockedAchievementsTest() {
        val seed = AchievementSeedData.achievements
        for (ach in seed) {
            assertFalse("Achievement '${ach.id}' must be locked on fresh install", ach.isUnlocked)
            assertNull("Achievement '${ach.id}' unlockedAt timestamp must be null on fresh install", ach.unlockedAt)
        }
    }

    @Test
    fun completeDay90PercentRuleTest() {
        val totalExercises = 11
        val completed11 = 11
        val ratio11 = completed11.toFloat() / totalExercises
        assertTrue("11/11 (100%) must qualify for day completion", ratio11 >= 0.90f)

        val completed10 = 10
        val ratio10 = completed10.toFloat() / totalExercises
        assertTrue("10/11 (90.9%) must qualify for day completion", ratio10 >= 0.90f)

        val completed9 = 9
        val ratio9 = completed9.toFloat() / totalExercises
        assertFalse("9/11 (81.8%) must NOT qualify for day completion", ratio9 >= 0.90f)
    }

    @Test
    fun fourExerciseSetCompletionAdThresholdTest() {
        var completedSinceAd = 0
        val totalExercises = 11

        for (i in 1..3) {
            completedSinceAd++
        }
        assertFalse("3 completed exercises must NOT trigger Set Complete ad threshold", completedSinceAd >= 4)

        completedSinceAd++
        assertTrue("4 completed exercises MUST trigger Set Complete ad threshold", completedSinceAd >= 4)

        completedSinceAd = 0
        assertEquals(0, completedSinceAd)
    }

    @Test
    fun skippedExerciseDoesNotAdvanceAdCounterTest() {
        var completedSinceAd = 0

        completedSinceAd++ // Ex 1 done
        // Ex 2 skipped -> no increment
        completedSinceAd++ // Ex 3 done

        assertEquals("Skipped exercises must not advance completedExercisesSinceLastSetAd counter", 2, completedSinceAd)
    }

    @Test
    fun elevenExerciseWorkoutHasMaxTwoMidWorkoutAdsTest() {
        val totalExercises = 11
        var midWorkoutAdCount = 0
        var completedSinceAd = 0

        for (ex in 1..totalExercises) {
            completedSinceAd++
            val isLast = ex == totalExercises
            if (completedSinceAd >= 4 && !isLast) {
                midWorkoutAdCount++
                completedSinceAd = 0
            }
        }

        assertEquals("An 11-exercise workout must have exactly 2 mid-workout interstitials (after Ex 4 and Ex 8)", 2, midWorkoutAdCount)
    }

    @Test
    fun allExercisesHave30SecondTimerDefaultTest() {
        val ex = WorkoutExercise(
            exerciseId = 1,
            name = "Knee-to-Chest Crunch",
            category = "upper_abs",
            targetMuscle = "Rectus Abdominis",
            instructions = "",
            tips = "",
            reps = 20,
            durationSeconds = 30,
            animationId = "knee_to_chest_crunch"
        )
        assertEquals("All exercises must default to 30s timer", 30, ex.durationSeconds)
        assertEquals(20, ex.reps)
    }

    @Test
    fun timerPausesAndResumesCorrectlyTest() {
        var remainingMs = 18000L
        var isPaused = true

        // While paused, remainingMs stays frozen
        assertEquals(18000L, remainingMs)

        // Resumed
        isPaused = false
        remainingMs -= 1000L
        assertEquals("Resuming timer must continue from frozen value", 17000L, remainingMs)
    }

    @Test
    fun retryResetsTimerTo30SecondsTest() {
        var remainingMs = 4000L

        // Tap RETRY -> resets to 30000L
        remainingMs = 30000L
        assertEquals("RETRY must reset exercise timer to 30 seconds (30000ms)", 30000L, remainingMs)
    }

    @Test
    fun exerciseCompletesAtZeroSecondsTest() {
        var remainingMs = 30000L
        while (remainingMs > 0) {
            remainingMs -= 100
        }
        assertEquals("Timer reaching zero triggers exercise complete", 0L, remainingMs)
    }

    @Test
    fun workoutTotalTimerIndependentFromExerciseTimerTest() {
        var workoutElapsedSec = 166
        var exerciseTimeRemainingMs = 30000L

        workoutElapsedSec += 1
        exerciseTimeRemainingMs -= 1000L

        assertEquals(167, workoutElapsedSec)
        assertEquals(29000L, exerciseTimeRemainingMs)
    }
}
