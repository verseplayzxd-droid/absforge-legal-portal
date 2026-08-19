package com.absforge

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkoutLogicTest {

    @Test
    fun testNinetyPercentCompletionRule() {
        // Rule: A workout counts as completed if completedExercises >= 90% of totalExercises
        val totalExercises = 10

        val nineCompleted = isWorkoutQualifying(completed = 9, total = totalExercises)
        val eightCompleted = isWorkoutQualifying(completed = 8, total = totalExercises)
        val tenCompleted = isWorkoutQualifying(completed = 10, total = totalExercises)

        assertTrue("9 out of 10 is 90%, should qualify", nineCompleted)
        assertFalse("8 out of 10 is 80%, should NOT qualify", eightCompleted)
        assertTrue("10 out of 10 is 100%, should qualify", tenCompleted)
    }

    @Test
    fun testNinetyPercentCompletionRuleSmallCount() {
        // 5 exercises -> 90% of 5 is 4.5 -> requires 5 completed (or 4.5 rounded, 90% of 5 is 4.5)
        val totalExercises = 5
        val fourCompleted = isWorkoutQualifying(completed = 4, total = totalExercises) // 80%
        val fiveCompleted = isWorkoutQualifying(completed = 5, total = totalExercises) // 100%

        assertFalse("4 out of 5 is 80%, should NOT qualify", fourCompleted)
        assertTrue("5 out of 5 is 100%, should qualify", fiveCompleted)
    }

    private fun isWorkoutQualifying(completed: Int, total: Int): Boolean {
        if (total <= 0) return false
        val percentage = (completed.toFloat() / total.toFloat()) * 100f
        return percentage >= 90f
    }
}
