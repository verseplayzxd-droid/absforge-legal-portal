package com.absforge

import com.absforge.data.seed.*
import org.junit.Assert.*
import org.junit.Test

class PlanSeedDataIntegrityTest {

    @Test
    fun verifyBeginnerPlanDays1To30() {
        val days = BeginnerPlanSeedData.createDays()
        assertEquals("Beginner plan must have exactly 30 days", 30, days.size)

        val validExerciseIds = ExerciseSeedData.exercises.map { it.id }.toSet()
        assertTrue("Exercise seed pool must contain 33 exercises", validExerciseIds.size >= 33)

        println("\n=== BEGINNER PLAN DAY 1..30 EXERCISE COUNTS ===")
        for (day in days) {
            assertTrue("Day number must be 1..30", day.dayNumber in 1..30)
            assertTrue("Day name must not be blank", day.name.isNotBlank())

            if (day.isRestDay) {
                assertTrue("Rest day ${day.dayNumber} should have empty exercise list", day.exercises.isEmpty())
                println("Day ${day.dayNumber}: REST DAY (0 exercises)")
            } else {
                assertTrue(
                    "Non-rest day ${day.dayNumber} must have at least 5 exercises (found ${day.exercises.size})",
                    day.exercises.size >= 5
                )
                println("Day ${day.dayNumber}: '${day.name}' -> ${day.exercises.size} exercises, est ${day.estimatedMinutes} min, ${day.estimatedCalories} kcal")
                for (ex in day.exercises) {
                    assertTrue(
                        "Exercise ID ${ex.exerciseId} on Day ${day.dayNumber} must exist in ExerciseSeedData",
                        validExerciseIds.contains(ex.exerciseId)
                    )
                }
            }
        }
    }

    @Test
    fun verifyIntermediatePlanDays1To30() {
        val days = IntermediatePlanSeedData.createDays()
        assertEquals("Intermediate plan must have exactly 30 days", 30, days.size)

        val validExerciseIds = ExerciseSeedData.exercises.map { it.id }.toSet()

        println("\n=== INTERMEDIATE PLAN DAY 1..30 EXERCISE COUNTS ===")
        for (day in days) {
            assertTrue("Day number must be 1..30", day.dayNumber in 1..30)
            assertTrue("Day name must not be blank", day.name.isNotBlank())

            if (day.isRestDay) {
                assertTrue("Rest day ${day.dayNumber} should have empty exercise list", day.exercises.isEmpty())
                println("Day ${day.dayNumber}: REST DAY (0 exercises)")
            } else {
                assertTrue(
                    "Intermediate non-rest day ${day.dayNumber} must have at least 7 exercises (found ${day.exercises.size})",
                    day.exercises.size >= 7
                )
                println("Day ${day.dayNumber}: '${day.name}' -> ${day.exercises.size} exercises, est ${day.estimatedMinutes} min, ${day.estimatedCalories} kcal")
                for (ex in day.exercises) {
                    assertTrue(
                        "Exercise ID ${ex.exerciseId} on Day ${day.dayNumber} must exist in ExerciseSeedData",
                        validExerciseIds.contains(ex.exerciseId)
                    )
                }
            }
        }
    }

    @Test
    fun verifyAdvancedPlanDays1To30() {
        val days = AdvancedPlanSeedData.createDays()
        assertEquals("Advanced plan must have exactly 30 days", 30, days.size)

        val validExerciseIds = ExerciseSeedData.exercises.map { it.id }.toSet()

        println("\n=== ADVANCED PLAN DAY 1..30 EXERCISE COUNTS ===")
        for (day in days) {
            assertTrue("Day number must be 1..30", day.dayNumber in 1..30)
            assertTrue("Day name must not be blank", day.name.isNotBlank())

            if (day.isRestDay) {
                assertTrue("Rest day ${day.dayNumber} should have empty exercise list", day.exercises.isEmpty())
                println("Day ${day.dayNumber}: REST DAY (0 exercises)")
            } else {
                assertTrue(
                    "Advanced non-rest day ${day.dayNumber} must have at least 9 exercises (found ${day.exercises.size})",
                    day.exercises.size >= 9
                )
                println("Day ${day.dayNumber}: '${day.name}' -> ${day.exercises.size} exercises, est ${day.estimatedMinutes} min, ${day.estimatedCalories} kcal")
                for (ex in day.exercises) {
                    assertTrue(
                        "Exercise ID ${ex.exerciseId} on Day ${day.dayNumber} must exist in ExerciseSeedData",
                        validExerciseIds.contains(ex.exerciseId)
                    )
                }
            }
        }
    }

    @Test
    fun verifyDay1ExerciseCountsPerDifficulty() {
        val beginnerDay1 = BeginnerPlanSeedData.createDays().first { it.dayNumber == 1 }
        val intermediateDay1 = IntermediatePlanSeedData.createDays().first { it.dayNumber == 1 }
        val advancedDay1 = AdvancedPlanSeedData.createDays().first { it.dayNumber == 1 }

        println("\n=== DAY 1 COMPARISON ===")
        println("Beginner Day 1 Count: ${beginnerDay1.exercises.size}")
        println("Intermediate Day 1 Count: ${intermediateDay1.exercises.size}")
        println("Advanced Day 1 Count: ${advancedDay1.exercises.size}")

        assertTrue("Beginner Day 1 exercise count should be 5..10", beginnerDay1.exercises.size in 5..10)
        assertTrue("Intermediate Day 1 exercise count should be 7..12", intermediateDay1.exercises.size in 7..12)
        assertTrue("Advanced Day 1 exercise count should be 9..14", advancedDay1.exercises.size in 9..14)
    }

    @Test
    fun verifyDays29And30NoOutOfBoundsException() {
        val beginnerDays = BeginnerPlanSeedData.createDays()
        val intermediateDays = IntermediatePlanSeedData.createDays()
        val advancedDays = AdvancedPlanSeedData.createDays()

        assertNotNull(beginnerDays.find { it.dayNumber == 29 })
        assertNotNull(beginnerDays.find { it.dayNumber == 30 })
        assertNotNull(intermediateDays.find { it.dayNumber == 29 })
        assertNotNull(intermediateDays.find { it.dayNumber == 30 })
        assertNotNull(advancedDays.find { it.dayNumber == 29 })
        assertNotNull(advancedDays.find { it.dayNumber == 30 })
    }

    @Test
    fun verifyQuickWorkoutsData() {
        for ((key, workout) in QuickWorkoutSeedData.workouts) {
            assertTrue("Quick workout '$key' must have at least 5 exercises", workout.exercises.size >= 5)
            assertTrue("Quick workout '$key' estimated minutes > 0", workout.estimatedMinutes > 0)
        }
    }
}
