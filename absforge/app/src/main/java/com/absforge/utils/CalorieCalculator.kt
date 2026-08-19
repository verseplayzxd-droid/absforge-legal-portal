package com.absforge.utils

/**
 * Calorie estimation calculator for AbsForge workouts.
 *
 * IMPORTANT: These are ESTIMATED values only. They should be displayed with
 * the ~ (approximately) prefix. Do not present these as medical-grade measurements.
 *
 * The formula is intentionally isolated so it can be refined later.
 */
object CalorieCalculator {

    /**
     * MET (Metabolic Equivalent of Task) values for different exercise intensities.
     * Based on general fitness activity guidelines.
     */
    private const val MET_LOW = 3.0f      // Stretching, light core work
    private const val MET_MODERATE = 4.5f  // Standard crunches, leg raises
    private const val MET_HIGH = 6.0f      // Mountain climbers, high knees, intense planks
    private const val MET_VERY_HIGH = 8.0f // Burpee-level intensity (not used much for abs)

    /**
     * Estimate calories burned for a workout.
     *
     * @param durationMinutes Total workout duration in minutes
     * @param userWeightKg User's weight in kilograms
     * @param intensity Workout intensity: "beginner", "intermediate", "advanced"
     * @return Estimated calories burned (rounded to nearest integer)
     */
    fun estimateCalories(
        durationMinutes: Float,
        userWeightKg: Float,
        intensity: String = "intermediate"
    ): Int {
        val met = when (intensity.lowercase()) {
            "beginner" -> MET_LOW
            "intermediate" -> MET_MODERATE
            "advanced" -> MET_HIGH
            else -> MET_MODERATE
        }

        // Calories = MET × weight(kg) × duration(hours)
        val durationHours = durationMinutes / 60f
        val calories = met * userWeightKg * durationHours

        return calories.toInt().coerceAtLeast(1)
    }

    /**
     * Estimate calories for a single exercise.
     */
    fun estimateExerciseCalories(
        durationSeconds: Int,
        caloriesPerMinute: Float,
        userWeightKg: Float = 70f
    ): Int {
        val durationMinutes = durationSeconds / 60f
        // Adjust base calories per minute by user weight ratio (base weight: 70kg)
        val weightFactor = userWeightKg / 70f
        return (caloriesPerMinute * durationMinutes * weightFactor).toInt().coerceAtLeast(1)
    }

    /**
     * Quick estimate for workout overview cards.
     */
    fun estimateWorkoutCalories(
        estimatedMinutes: Int,
        difficulty: String,
        userWeightKg: Float = 70f
    ): Int {
        return estimateCalories(estimatedMinutes.toFloat(), userWeightKg, difficulty)
    }
}
