package com.absforge

import com.absforge.utils.CalorieCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalorieCalculatorTest {

    @Test
    fun testEstimateCaloriesBeginner() {
        val calories = CalorieCalculator.estimateCalories(
            durationMinutes = 10f,
            userWeightKg = 70f,
            intensity = "beginner"
        )
        // 3.0 * 70 * (10/60) = 35
        assertEquals(35, calories)
    }

    @Test
    fun testEstimateCaloriesIntermediate() {
        val calories = CalorieCalculator.estimateCalories(
            durationMinutes = 15f,
            userWeightKg = 70f,
            intensity = "intermediate"
        )
        // 4.5 * 70 * (15/60) = 78
        assertEquals(78, calories)
    }

    @Test
    fun testEstimateCaloriesAdvanced() {
        val calories = CalorieCalculator.estimateCalories(
            durationMinutes = 20f,
            userWeightKg = 80f,
            intensity = "advanced"
        )
        // 6.0 * 80 * (20/60) = 160
        assertEquals(160, calories)
    }

    @Test
    fun testExerciseCalories() {
        val calories = CalorieCalculator.estimateExerciseCalories(
            durationSeconds = 60,
            caloriesPerMinute = 6f,
            userWeightKg = 70f
        )
        assertEquals(6, calories)
    }
}
