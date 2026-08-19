package com.absforge

import org.junit.Assert.assertEquals
import org.junit.Test

class OnboardingLogicTest {

    @Test
    fun testPlanIdMapping() {
        assertEquals(1, mapFitnessLevelToPlanId("beginner"))
        assertEquals(1, mapFitnessLevelToPlanId("Beginner"))
        assertEquals(2, mapFitnessLevelToPlanId("intermediate"))
        assertEquals(3, mapFitnessLevelToPlanId("advanced"))
        assertEquals(1, mapFitnessLevelToPlanId(""))
        assertEquals(1, mapFitnessLevelToPlanId("unknown"))
    }

    @Test
    fun testTrainTimeParsing() {
        assertEquals(Pair(8, 0), parseTrainTime("morning"))
        assertEquals(Pair(14, 0), parseTrainTime("afternoon"))
        assertEquals(Pair(19, 0), parseTrainTime("evening"))
        assertEquals(Pair(20, 0), parseTrainTime("custom"))
        assertEquals(Pair(15, 30), parseTrainTime("15:30"))
        assertEquals(Pair(7, 45), parseTrainTime("7:45"))
        assertEquals(Pair(8, 0), parseTrainTime("invalid"))
        assertEquals(Pair(8, 0), parseTrainTime(""))
    }

    @Test
    fun testUserDataParsingAndFallbacks() {
        val ageInput = ""
        val heightInput = "180"
        val weightInput = "75.5"
        val targetWeightInput = "70"

        val ageVal = ageInput.toIntOrNull() ?: 25
        val heightVal = heightInput.toFloatOrNull() ?: 170f
        val weightVal = weightInput.toFloatOrNull() ?: 70f
        val targetWeightVal = targetWeightInput.toFloatOrNull() ?: 65f

        assertEquals(25, ageVal)
        assertEquals(180f, heightVal, 0.01f)
        assertEquals(75.5f, weightVal, 0.01f)
        assertEquals(70f, targetWeightVal, 0.01f)
    }

    private fun mapFitnessLevelToPlanId(level: String): Int {
        return when (level.lowercase()) {
            "beginner" -> 1
            "intermediate" -> 2
            "advanced" -> 3
            else -> 1
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
