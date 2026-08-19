package com.absforge.ui.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Welcome : Screen("onboarding/welcome")
    data object GoalSelection : Screen("onboarding/goal")
    data object FitnessLevel : Screen("onboarding/level")
    data object UserInfo : Screen("onboarding/user_info")
    data object WorkoutPreference : Screen("onboarding/preference")
    data object BuildPlan : Screen("onboarding/build_plan")

    data object Home : Screen("home")
    data object Workouts : Screen("workouts")
    data object Progress : Screen("progress")
    data object Profile : Screen("profile")

    data object WorkoutOverview : Screen("workout/overview/{planId}/{dayNumber}") {
        fun createRoute(planId: Int, dayNumber: Int) = "workout/overview/$planId/$dayNumber"
    }

    data object QuickWorkoutOverview : Screen("workout/quick/{quickWorkoutId}") {
        fun createRoute(quickWorkoutId: String) = "workout/quick/$quickWorkoutId"
    }

    data object WorkoutPlayer : Screen("workout/player/{planId}/{dayNumber}") {
        fun createRoute(planId: Int, dayNumber: Int) = "workout/player/$planId/$dayNumber"
    }

    data object QuickWorkoutPlayer : Screen("workout/quick_player/{quickWorkoutId}") {
        fun createRoute(quickWorkoutId: String) = "workout/quick_player/$quickWorkoutId"
    }

    data object WorkoutComplete : Screen("workout/complete/{sessionId}") {
        fun createRoute(sessionId: Int) = "workout/complete/$sessionId"
    }

    data object ExerciseLibrary : Screen("exercise/library")
    data object ExerciseDetails : Screen("exercise/details/{exerciseId}") {
        fun createRoute(exerciseId: Int) = "exercise/details/$exerciseId"
    }

    data object WorkoutSettings : Screen("settings/workout")
    data object ReminderSettings : Screen("settings/reminder")
    data object Achievements : Screen("profile/achievements")
    data object Premium : Screen("profile/premium")
    data object PrivacyData : Screen("profile/privacy")
    data object PrivacyPolicy : Screen("legal/privacy_policy")
    data object TermsOfService : Screen("legal/terms_of_service")
    data object HealthDisclaimer : Screen("legal/health_disclaimer")
    data object WorkoutHistory : Screen("profile/history")
    data object BodyTracker : Screen("progress/body")
}
