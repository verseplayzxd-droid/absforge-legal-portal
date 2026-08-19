package com.absforge.data.seed

object AdvancedPlanSeedData {
    fun createDays(): List<DayData> {
        val titles = listOf(
            listOf("Extreme Core", "Abs Inferno", "Iron Core", "Six Pack Sculptor", "Core Destruction", "Ab Demolition", "Power Planks"),
            listOf("Unrelenting Abs", "Core Fire", "Ultimate Burn", "Six Pack Factory", "Total Core", "Ab Maximizer", "Plank Master"),
            listOf("Elite Core", "Abs of Steel", "Core Resilience", "Shredded Abs", "Core Dominance", "Iron Midsection", "Endurance Run"),
            listOf("Pro Circuit", "Abs Finisher", "Core Champion", "Peak Condition", "Final Burn", "Six Pack Pro", "Legendary Core", "Ultimate Burn", "Final Mastery")
        )

        return (1..30).map { dayNum ->
            if (dayNum in listOf(10, 20, 30)) {
                return@map DayData(dayNum, "Rest Day", true, 0, 0, emptyList())
            }

            val week = ((dayNum - 1) / 7).coerceAtMost(3)
            val dayInWeek = if (dayNum >= 29) {
                (dayNum - 22).coerceIn(0, titles[week].lastIndex)
            } else {
                ((dayNum - 1) % 7).coerceIn(0, titles[week].lastIndex)
            }

            val name = titles[week][dayInWeek]

            val pool = (1..29).toMutableList() // All exercises available

            val stretches = listOf(30, 31, 32, 33)
            val rand = kotlin.random.Random(dayNum + 200)

            val numExercises = 10 + rand.nextInt(5) // 10-14

            val shuffledPool = pool.shuffled(rand)
            val selectedEx = shuffledPool.take(numExercises)

            val exercises = mutableListOf<DayExerciseData>()
            var order = 0

            val baseReps = 16 + (week * 2) // 16-22
            val baseTimed = 30 + (week * 5) // 30-45

            for (exId in selectedEx) {
                val repVariance = rand.nextInt(5)
                val reps = baseReps + repVariance // 16-24+

                val isTimed = exId in setOf(8, 12, 13, 14, 15, 17, 18, 27, 28, 30, 31, 32, 33)
                val timedDuration = if (exId == 13) {
                    45 + (week * 5) + rand.nextInt(10) // Plank 45s -> 60s+
                } else {
                    baseTimed + (repVariance * 5) // 30-60s
                }

                val rest = 20

                if (isTimed) {
                    exercises.add(DayExerciseData(exId, order++, null, timedDuration, rest))
                } else {
                    exercises.add(DayExerciseData(exId, order++, reps, null, rest))
                }
            }

            val numStretches = 1 + rand.nextInt(2) // 1-2
            val selectedStretches = stretches.shuffled(rand).take(numStretches)
            for (strId in selectedStretches) {
                exercises.add(DayExerciseData(strId, order++, null, 30, 0))
            }

            val estMin = 12 + rand.nextInt(9) // 12-20
            val estCal = 90 + (week * 10) + rand.nextInt(41) // 90-160

            DayData(dayNum, name, false, estMin, estCal, exercises)
        }
    }

    val days: List<DayData> get() = createDays()
}
