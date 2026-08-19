import os
import random

base_dir = r"e:\stitch_absforge_fitness_app_design\absforge\app\src\main\java\com\absforge\data\seed"
os.makedirs(base_dir, exist_ok=True)

# 1. CommonSeedModels.kt
with open(os.path.join(base_dir, "CommonSeedModels.kt"), "w", encoding="utf-8") as f:
    f.write("""package com.absforge.data.seed

data class DayData(
    val dayNumber: Int,
    val name: String,
    val isRestDay: Boolean,
    val estimatedMinutes: Int,
    val estimatedCalories: Int,
    val exercises: List<DayExerciseData>
)

data class DayExerciseData(
    val exerciseId: Int,
    val orderIndex: Int,
    val reps: Int? = null,
    val durationSeconds: Int? = null,
    val restAfterSeconds: Int = 30
)
""")

# 2. ExerciseSeedData.kt
exercises_info = [
    (1, "Crunch", "upper_abs", "beginner", "reps", 16, None),
    (2, "Reverse Crunch", "lower_abs", "beginner", "reps", 12, None),
    (3, "Bicycle Crunch", "full_core", "intermediate", "reps", 20, None),
    (4, "V Crunch", "upper_abs", "intermediate", "reps", 12, None),
    (5, "V-Up", "full_core", "advanced", "reps", 10, None),
    (6, "Leg Raise", "lower_abs", "intermediate", "reps", 12, None),
    (7, "Bent Leg Twist", "obliques", "beginner", "reps", 16, None),
    (8, "Flutter Kicks", "lower_abs", "intermediate", "timed", None, 30),
    (9, "Heel Touch", "obliques", "beginner", "reps", 20, None),
    (10, "Russian Twist", "obliques", "intermediate", "reps", 20, None),
    (11, "Sit-Up Twist", "full_core", "intermediate", "reps", 14, None),
    (12, "Mountain Climbers", "full_core", "advanced", "timed", None, 30),
    (13, "Plank", "core", "beginner", "timed", None, 30),
    (14, "Side Plank Left", "obliques", "intermediate", "timed", None, 20),
    (15, "Side Plank Right", "obliques", "intermediate", "timed", None, 20),
    (16, "Single Leg Drops", "lower_abs", "intermediate", "reps", 12, None),
    (17, "Seated Abs Circles CW", "core", "beginner", "timed", None, 20),
    (18, "Seated Abs Circles CCW", "core", "beginner", "timed", None, 20),
    (19, "Dead Bug", "core", "beginner", "reps", 12, None),
    (20, "Bird Dog", "core", "beginner", "reps", 12, None),
    (21, "Toe Touch", "upper_abs", "intermediate", "reps", 14, None),
    (22, "Knee-to-Chest Crunch", "upper_abs", "beginner", "reps", 14, None),
    (23, "Long Arm Crunch", "upper_abs", "intermediate", "reps", 12, None),
    (24, "Cross Arm Crunch", "upper_abs", "beginner", "reps", 16, None),
    (25, "Oblique Crunch", "obliques", "intermediate", "reps", 14, None),
    (26, "Leg In & Out", "lower_abs", "intermediate", "reps", 14, None),
    (27, "Scissor Kicks", "lower_abs", "advanced", "timed", None, 30),
    (28, "High Knees", "full_core", "advanced", "timed", None, 30),
    (29, "Standing Bicycle Crunch", "obliques", "beginner", "reps", 16, None),
    (30, "Cobra Stretch", "stretch", "beginner", "timed", None, 25),
    (31, "Child's Pose", "stretch", "beginner", "timed", None, 25),
    (32, "Lying Twist Stretch Left", "stretch", "beginner", "timed", None, 20),
    (33, "Lying Twist Stretch Right", "stretch", "beginner", "timed", None, 20),
]

with open(os.path.join(base_dir, "ExerciseSeedData.kt"), "w", encoding="utf-8") as f:
    f.write("package com.absforge.data.seed\n\nimport com.absforge.data.local.entity.ExerciseEntity\n\nobject ExerciseSeedData {\n    val exercises = listOf(\n")
    for ex in exercises_info:
        eid, name, muscle, diff, etype, reps, dur = ex
        anim = name.lower().replace(" ", "_").replace("'", "").replace("-", "_").replace("&", "and")
        cal = round(random.uniform(4.0, 7.5), 1)
        alts = f'"{random.choice(exercises_info)[0]},{random.choice(exercises_info)[0]}"'
        
        rep_str = str(reps) if reps else "null"
        dur_str = str(dur) if dur else "null"
        is_side = "true" if "Left" in name or "Right" in name else "false"
        
        f.write(f"""        ExerciseEntity(
            id = {eid},
            name = "{name}",
            description = "Strengthen your {muscle} with {name}.",
            targetMuscle = "{muscle}",
            difficulty = "{diff}",
            type = "{etype}",
            defaultReps = {rep_str},
            defaultDuration = {dur_str},
            instructions = "1. Get into starting position.\\n2. Engage your core.\\n3. Perform the movement steadily.\\n4. Return to start.",
            tips = "Keep your core tight.\\nBreathe steadily throughout the movement.",
            mistakes = "Rushing the reps.\\nArching your back.",
            animationId = "{anim}",
            estimatedCaloriesPerMinute = {cal},
            alternativeExerciseIds = {alts},
            isSideSpecific = {is_side}
        ),\n""")
    f.write("    )\n}\n")


def gen_plan(name_prefix, rest_days, total_days, pool_func, rep_mult, dur_mult, rest_time):
    out = []
    out.append(f"package com.absforge.data.seed\n\nobject {name_prefix}PlanSeedData {{\n    val days = listOf(\n")
    for day in range(1, total_days + 1):
        if day in rest_days:
            out.append(f"        DayData(dayNumber = {day}, name = \"Day {day} - Rest\", isRestDay = true, estimatedMinutes = 0, estimatedCalories = 0, exercises = emptyList()),\n")
        else:
            exercises = pool_func(day)
            ex_list = []
            for i, eid in enumerate(exercises):
                ex_info = next(e for e in exercises_info if e[0] == eid)
                r = ex_info[5]
                d = ex_info[6]
                r_val = f"{int(r * rep_mult)}" if r else "null"
                d_val = f"{int(d * dur_mult)}" if d else "null"
                ex_list.append(f"            DayExerciseData(exerciseId = {eid}, orderIndex = {i}, reps = {r_val}, durationSeconds = {d_val}, restAfterSeconds = {rest_time})")
            
            ex_str = ",\n".join(ex_list)
            mins = max(5, int(len(exercises) * ((60 + rest_time) / 60.0)))
            cals = mins * 5
            out.append(f"        DayData(dayNumber = {day}, name = \"Day {day} - {name_prefix} Workout\", isRestDay = false, estimatedMinutes = {mins}, estimatedCalories = {cals}, exercises = listOf(\n{ex_str}\n        )),\n")
    out.append("    )\n}\n")
    return "".join(out)

def beg_pool(day):
    # Week 1 (Days 1-6): 6-8 exercises, 6-8 min. Focus: Crunch, Knee-to-Chest Crunch, Heel Touch, Dead Bug, Bird Dog, Plank 20s, stretches
    # Week 2 (Days 8-13): 7-9 exercises, 8-10 min. Add: Reverse Crunch, Leg Raise, Seated Circles, Cross Arm Crunch
    # Week 3 (Days 15-20): 8-10 exercises, 9-11 min. Add: Bicycle Crunch (low reps), Flutter Kicks (20s), Russian Twist, Bent Leg Twist
    # Week 4 (Days 22-27, 29-30): 9-11 exercises, 10-13 min. Add: Oblique Crunch, Toe Touch, longer planks. Final 2 days are slightly harder
    pool = []
    if day <= 7:
        pool = [1, 22, 9, 19, 20, 13]
        cnt = random.randint(6, 8)
    elif day <= 14:
        pool = [1, 22, 9, 19, 20, 13, 2, 6, 17, 18, 24]
        cnt = random.randint(7, 9)
    elif day <= 21:
        pool = [1, 22, 9, 19, 20, 13, 2, 6, 17, 18, 24, 3, 8, 10, 7]
        cnt = random.randint(8, 10)
    else:
        pool = [1, 22, 9, 19, 20, 13, 2, 6, 17, 18, 24, 3, 8, 10, 7, 25, 21]
        cnt = random.randint(9, 11)
        
    selected = random.sample(pool, min(cnt, len(pool)))
    if 30 not in selected: selected.append(30)
    if 31 not in selected: selected.append(31)
    return selected

with open(os.path.join(base_dir, "BeginnerPlanSeedData.kt"), "w", encoding="utf-8") as f:
    f.write(gen_plan("Beginner", [7, 14, 21, 28], 30, beg_pool, 1.0, 1.0, 30))

def int_pool(day):
    pool = list(range(1, 27))
    cnt = random.randint(8, 14)
    selected = random.sample(pool, min(cnt, len(pool)))
    selected.append(32)
    selected.append(33)
    return selected

with open(os.path.join(base_dir, "IntermediatePlanSeedData.kt"), "w", encoding="utf-8") as f:
    f.write(gen_plan("Intermediate", [7, 14, 21, 28], 30, int_pool, 1.2, 1.2, 25))

def adv_pool(day):
    pool = list(range(1, 30))
    cnt = random.randint(10, 14)
    selected = random.sample(pool, min(cnt, len(pool)))
    selected.append(30)
    selected.append(31)
    return selected

with open(os.path.join(base_dir, "AdvancedPlanSeedData.kt"), "w", encoding="utf-8") as f:
    f.write(gen_plan("Advanced", [10, 20, 30], 30, adv_pool, 1.5, 1.5, 20))

with open(os.path.join(base_dir, "QuickWorkoutSeedData.kt"), "w", encoding="utf-8") as f:
    f.write("""package com.absforge.data.seed

object QuickWorkoutSeedData {
    val workouts = mapOf(
        "five_min_abs" to DayData(1, "5 Min Abs", false, 5, 25, listOf(
            DayExerciseData(1, 0, 12, null, 20),
            DayExerciseData(9, 1, 12, null, 20),
            DayExerciseData(13, 2, null, 20, 20),
            DayExerciseData(30, 3, null, 20, 20)
        )),
        "lower_abs" to DayData(1, "Lower Abs Focus", false, 8, 45, listOf(
            DayExerciseData(2, 0, 14, null, 25),
            DayExerciseData(6, 1, 12, null, 25),
            DayExerciseData(8, 2, null, 30, 25),
            DayExerciseData(16, 3, 12, null, 25),
            DayExerciseData(26, 4, 14, null, 25),
            DayExerciseData(31, 5, null, 30, 25)
        )),
        "core_burner" to DayData(1, "Core Burner", false, 10, 70, listOf(
            DayExerciseData(5, 0, 12, null, 20),
            DayExerciseData(12, 1, null, 40, 20),
            DayExerciseData(27, 2, null, 40, 20),
            DayExerciseData(28, 3, null, 40, 20),
            DayExerciseData(3, 4, 24, null, 20),
            DayExerciseData(11, 5, 16, null, 20),
            DayExerciseData(30, 6, null, 30, 20),
            DayExerciseData(31, 7, null, 30, 20)
        )),
        "plank_challenge" to DayData(1, "Plank Challenge", false, 7, 35, listOf(
            DayExerciseData(13, 0, null, 45, 20),
            DayExerciseData(14, 1, null, 30, 20),
            DayExerciseData(15, 2, null, 30, 20),
            DayExerciseData(13, 3, null, 30, 20)
        )),
        "oblique_blast" to DayData(1, "Oblique Blast", false, 8, 45, listOf(
            DayExerciseData(7, 0, 20, null, 20),
            DayExerciseData(9, 1, 24, null, 20),
            DayExerciseData(10, 2, 24, null, 20),
            DayExerciseData(25, 3, 16, null, 20),
            DayExerciseData(29, 4, 20, null, 20),
            DayExerciseData(32, 5, null, 30, 20),
            DayExerciseData(33, 6, null, 30, 20)
        )),
        "stretch_recovery" to DayData(1, "Stretch & Recovery", false, 6, 20, listOf(
            DayExerciseData(19, 0, 12, null, 15),
            DayExerciseData(20, 1, 12, null, 15),
            DayExerciseData(30, 2, null, 40, 15),
            DayExerciseData(31, 3, null, 40, 15),
            DayExerciseData(32, 4, null, 30, 15),
            DayExerciseData(33, 5, null, 30, 15)
        ))
    )
}
""")

with open(os.path.join(base_dir, "AchievementSeedData.kt"), "w", encoding="utf-8") as f:
    f.write("""package com.absforge.data.seed

import com.absforge.data.local.entity.AchievementEntity

object AchievementSeedData {
    val achievements = listOf(
        AchievementEntity("first_workout", "First Step", "Complete your first workout", "ic_ach_first", "workouts", 1),
        AchievementEntity("streak_3", "On a Roll", "Achieve a 3-day streak", "ic_ach_streak_3", "streak", 3),
        AchievementEntity("streak_7", "Unstoppable", "Achieve a 7-day streak", "ic_ach_streak_7", "streak", 7),
        AchievementEntity("streak_14", "Core Committed", "Achieve a 14-day streak", "ic_ach_streak_14", "streak", 14),
        AchievementEntity("streak_30", "Iron Will", "Achieve a 30-day streak", "ic_ach_streak_30", "streak", 30),
        AchievementEntity("workouts_5", "Getting Started", "Complete 5 workouts", "ic_ach_work_5", "workouts", 5),
        AchievementEntity("workouts_10", "Regular", "Complete 10 workouts", "ic_ach_work_10", "workouts", 10),
        AchievementEntity("workouts_25", "Veteran", "Complete 25 workouts", "ic_ach_work_25", "workouts", 25),
        AchievementEntity("minutes_100", "Time Invested", "Workout for 100 minutes", "ic_ach_min_100", "minutes", 100),
        AchievementEntity("calories_500", "Burner", "Burn 500 calories", "ic_ach_cal_500", "calories", 500),
        AchievementEntity("calories_1000", "Incinerator", "Burn 1000 calories", "ic_ach_cal_1000", "calories", 1000)
    )
}
""")

with open(os.path.join(base_dir, "DatabaseSeeder.kt"), "w", encoding="utf-8") as f:
    f.write("""package com.absforge.data.seed

import com.absforge.data.local.AbsForgeDatabase
import com.absforge.data.local.entity.*

class DatabaseSeeder(private val database: AbsForgeDatabase) {
    suspend fun seedIfNeeded() {
        if (database.exerciseDao().getCount() > 0) return
        
        // Seed exercises
        database.exerciseDao().insertAll(ExerciseSeedData.exercises)
        
        // Seed plans
        seedPlan(1, "Beginner", "beginner", BeginnerPlanSeedData.days)
        seedPlan(2, "Intermediate", "intermediate", IntermediatePlanSeedData.days)
        seedPlan(3, "Advanced", "advanced", AdvancedPlanSeedData.days)
        
        // Seed achievements
        database.achievementDao().insertAll(AchievementSeedData.achievements)
    }
    
    private suspend fun seedPlan(planId: Int, name: String, difficulty: String, days: List<DayData>) {
        val plan = WorkoutPlanEntity(
            id = planId,
            name = name,
            difficulty = difficulty,
            totalDays = 30,
            description = "$name 30-day abs program"
        )
        database.workoutDao().insertPlan(plan)
        
        for (day in days) {
            val dayEntity = WorkoutDayEntity(
                planId = planId,
                dayNumber = day.dayNumber,
                name = day.name,
                isRestDay = day.isRestDay,
                estimatedMinutes = day.estimatedMinutes,
                estimatedCalories = day.estimatedCalories
            )
            val dayId = database.workoutDao().insertDay(dayEntity).toInt()
            
            val exercises = day.exercises.map { ex ->
                WorkoutDayExerciseEntity(
                    dayId = dayId,
                    exerciseId = ex.exerciseId,
                    orderIndex = ex.orderIndex,
                    reps = ex.reps,
                    durationSeconds = ex.durationSeconds,
                    restAfterSeconds = ex.restAfterSeconds
                )
            }
            if (exercises.isNotEmpty()) {
                database.workoutDao().insertDayExercises(exercises)
            }
        }
    }
}
""")
