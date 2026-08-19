package com.absforge.data.seed

object BeginnerPlanSeedData {
    fun createDays(): List<DayData> {
        val titles = listOf(
            listOf("Core Foundations", "Ab Activation", "Gentle Core", "Base Builder", "Core Intro", "Start Strong", "Recovery Day"),
            listOf("Core Control", "Steady Abs", "Building Strength", "Mid-week Core", "Ab Progression", "Core Focus", "Recovery Day"),
            listOf("Core Endurance", "Abs Engagement", "Ab Toner", "Core Push", "Elevated Core", "Stronger Abs", "Recovery Day"),
            listOf("Advanced Core", "Peak Abs", "Final Push", "Core Mastery", "Burn & Tone", "Ab Challenge", "Core Finale", "Ultimate Burn", "Final Mastery")
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

            val pool = mutableListOf(1, 22, 24, 9, 19, 20, 13, 17, 18, 29)
            if (week >= 1) pool.addAll(listOf(2, 6, 7))
            if (week >= 2) pool.addAll(listOf(3, 8, 10, 21))
            if (week >= 3) pool.addAll(listOf(25, 23, 26))

            val stretches = listOf(30, 31, 32, 33)

            val numExercises = when (week) {
                0 -> 6 + (dayNum % 2) // 6-7
                1 -> 7 + (dayNum % 3) // 7-9
                2 -> 8 + (dayNum % 3) // 8-10
                else -> 9 + (dayNum % 3) // 9-11
            }

            val rand = kotlin.random.Random(dayNum)
            val shuffledPool = pool.shuffled(rand)
            val selectedEx = shuffledPool.take(numExercises)

            val exercises = mutableListOf<DayExerciseData>()
            var order = 0

            val baseReps = 8 + (week * 2)
            val baseTimed = 15 + (week * 5)

            for (exId in selectedEx) {
                val repVariance = rand.nextInt(5)
                val reps = baseReps + repVariance

                val isTimed = exId in setOf(8, 12, 13, 14, 15, 17, 18, 27, 28, 30, 31, 32, 33)
                val timedDuration = if (exId == 13) {
                    val plankBase = if (week == 0) 15 else if (week == 1) 20 else if (week == 2) 25 else 30
                    plankBase + rand.nextInt(5)
                } else {
                    baseTimed + (repVariance * 2)
                }

                val rest = 30

                if (isTimed) {
                    exercises.add(DayExerciseData(exId, order++, null, timedDuration, rest))
                } else {
                    exercises.add(DayExerciseData(exId, order++, reps, null, rest))
                }
            }

            val numStretches = 1 + rand.nextInt(2) // 1-2
            val selectedStretches = stretches.shuffled(kotlin.random.Random(dayNum * 10)).take(numStretches)
            for (strId in selectedStretches) {
                exercises.add(DayExerciseData(strId, order++, null, 30, 0))
            }

            val estMin = when (week) {
                0 -> 6 + rand.nextInt(3) // 6-8
                1 -> 8 + rand.nextInt(3) // 8-10
                2 -> 9 + rand.nextInt(4) // 9-12
                else -> 10 + rand.nextInt(4) // 10-13
            }
            val estCal = when (week) {
                0 -> 40 + rand.nextInt(16) // 40-55
                1 -> 55 + rand.nextInt(16) // 55-70
                2 -> 65 + rand.nextInt(16) // 65-80
                else -> 75 + rand.nextInt(21) // 75-95
            }

            DayData(dayNum, name, false, estMin, estCal, exercises)
        }
    }

    val days: List<DayData> get() = createDays()
}
