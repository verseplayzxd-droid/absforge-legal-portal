package com.absforge.data.seed

object IntermediatePlanSeedData {
    fun createDays(): List<DayData> {
        val titles = listOf(
            listOf("Core Power", "Oblique Crusher", "Ab Circuit", "Full Core Blast", "Midsection Melt", "Power Abs", "Recovery"),
            listOf("Dynamic Core", "Ab Definer", "Twist & Tone", "Core Challenge", "Burn & Build", "Intense Abs", "Recovery"),
            listOf("Iron Core Intro", "Six Pack Primer", "Advanced Circuit", "Oblique Focus", "Core Endurance", "Power Planks", "Recovery"),
            listOf("Abs Inferno", "Core Mastery", "Ultimate Abs", "Peak Condition", "Core Final", "Strong Core", "The Crusher", "Ultimate Burn", "Final Mastery")
        )

        return (1..30).map { dayNum ->
            if (dayNum in listOf(7, 14, 21, 28)) {
                return@map DayData(dayNum, "Rest Day", true, 0, 0, emptyList())
            }

            val week = ((dayNum - 1) / 7).coerceAtMost(3)
            val dayInWeek = if (dayNum >= 29) {
                (dayNum - 22).coerceIn(0, titles[week].lastIndex)
            } else {
                ((dayNum - 1) % 7).coerceIn(0, titles[week].lastIndex)
            }

            val name = titles[week][dayInWeek]

            val pool = mutableListOf(1, 2, 4, 10, 12, 13, 14, 15, 11, 29, 27)
            if (week >= 1) pool.addAll(listOf(3, 5, 23))
            if (week >= 2) pool.addAll(listOf(6, 26, 25))
            if (week >= 3) pool.addAll(listOf(16, 28, 24))

            val stretches = listOf(30, 31, 32, 33)
            val rand = kotlin.random.Random(dayNum + 100)

            val numExercises = 8 + (week) + rand.nextInt(4) // 8-13

            val shuffledPool = pool.shuffled(rand)
            val selectedEx = shuffledPool.take(numExercises)

            val exercises = mutableListOf<DayExerciseData>()
            var order = 0

            val baseReps = 14 + (week * 2)
            val baseTimed = 25 + (week * 5)

            for (exId in selectedEx) {
                val repVariance = rand.nextInt(4)
                val reps = baseReps + repVariance

                val isTimed = exId in setOf(8, 12, 13, 14, 15, 17, 18, 27, 28, 30, 31, 32, 33)
                val timedDuration = if (exId == 13) {
                    30 + (week * 5) + rand.nextInt(5) // Plank 30s -> 45s
                } else {
                    baseTimed + (repVariance * 2) // 25-40s
                }

                val rest = 25

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

            val estMin = 8 + (week * 2) + rand.nextInt(4) // 8-16
            val estCal = 60 + (week * 15) + rand.nextInt(20) // 60-120

            DayData(dayNum, name, false, estMin, estCal, exercises)
        }
    }

    val days: List<DayData> get() = createDays()
}
