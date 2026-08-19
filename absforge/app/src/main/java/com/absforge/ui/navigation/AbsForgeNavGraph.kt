package com.absforge.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.absforge.AbsForgeApplication
import com.absforge.ui.components.BottomNavBar
import com.absforge.ui.home.HomeScreen
import com.absforge.ui.onboarding.BuildPlanScreen
import com.absforge.ui.onboarding.FitnessLevelScreen
import com.absforge.ui.onboarding.GoalSelectionScreen
import com.absforge.ui.onboarding.SplashScreen
import com.absforge.ui.onboarding.UserInfoScreen
import com.absforge.ui.onboarding.WelcomeScreen
import com.absforge.ui.onboarding.WorkoutPreferenceScreen
import com.absforge.ui.profile.AchievementsScreen
import com.absforge.ui.profile.HealthDisclaimerScreen
import com.absforge.ui.profile.PremiumScreen
import com.absforge.ui.profile.PrivacyDataScreen
import com.absforge.ui.profile.PrivacyPolicyScreen
import com.absforge.ui.profile.ProfileScreen
import com.absforge.ui.profile.ReminderSettingsScreen
import com.absforge.ui.profile.TermsOfServiceScreen
import com.absforge.ui.profile.WorkoutHistoryScreen
import com.absforge.ui.profile.WorkoutSettingsScreen
import com.absforge.ui.progress.ProgressScreen
import com.absforge.ui.exercises.ExerciseDetailsScreen
import com.absforge.ui.exercises.ExerciseLibraryScreen
import com.absforge.ui.theme.AbsForgeBackground
import com.absforge.ui.workout.ProgramScreen
import com.absforge.ui.workout.WorkoutCompleteScreen
import com.absforge.ui.workout.WorkoutOverviewScreen
import com.absforge.ui.workout.player.WorkoutPlayerScreen

@Composable
fun AbsForgeApp() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val showBottomNav = currentRoute in listOf(
        Screen.Home.route,
        Screen.Workouts.route,
        Screen.Progress.route,
        Screen.Profile.route
    )

    Scaffold(
        containerColor = AbsForgeBackground,
        bottomBar = {
            if (showBottomNav) {
                BottomNavBar(
                    currentRoute = currentRoute ?: Screen.Home.route,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        AbsForgeNavHost(
            navController = navController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun AbsForgeNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier,
        enterTransition = { fadeIn(animationSpec = tween(250)) },
        exitTransition = { fadeOut(animationSpec = tween(250)) },
        popEnterTransition = { fadeIn(animationSpec = tween(250)) },
        popExitTransition = { fadeOut(animationSpec = tween(250)) }
    ) {
        // Splash
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToWelcome = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Onboarding
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onGetStarted = { navController.navigate(Screen.GoalSelection.route) }
            )
        }

        composable(Screen.GoalSelection.route) {
            val onboardingViewModel = getSharedOnboardingViewModel()
            GoalSelectionScreen(
                onNext = { navController.navigate(Screen.FitnessLevel.route) },
                viewModel = onboardingViewModel
            )
        }

        composable(Screen.FitnessLevel.route) {
            val onboardingViewModel = getSharedOnboardingViewModel()
            FitnessLevelScreen(
                onNext = { navController.navigate(Screen.UserInfo.route) },
                viewModel = onboardingViewModel
            )
        }

        composable(Screen.UserInfo.route) {
            val onboardingViewModel = getSharedOnboardingViewModel()
            UserInfoScreen(
                onNext = { navController.navigate(Screen.WorkoutPreference.route) },
                viewModel = onboardingViewModel
            )
        }

        composable(Screen.WorkoutPreference.route) {
            val onboardingViewModel = getSharedOnboardingViewModel()
            WorkoutPreferenceScreen(
                onNext = { navController.navigate(Screen.BuildPlan.route) },
                viewModel = onboardingViewModel
            )
        }

        composable(Screen.BuildPlan.route) {
            val onboardingViewModel = getSharedOnboardingViewModel()
            BuildPlanScreen(
                onStartJourney = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                viewModel = onboardingViewModel
            )
        }

        // Main tabs
        composable(Screen.Home.route) {
            HomeScreen(
                onStartWorkout = { planId, dayNumber ->
                    navController.navigate(Screen.WorkoutOverview.createRoute(planId, dayNumber))
                },
                onQuickWorkoutSelected = { quickId ->
                    navController.navigate(Screen.QuickWorkoutOverview.createRoute(quickId))
                },
                onNavigateToProgram = {
                    navController.navigate(Screen.Workouts.route)
                }
            )
        }

        composable(Screen.Workouts.route) {
            ProgramScreen(
                onDaySelected = { planId, dayNumber ->
                    navController.navigate(Screen.WorkoutOverview.createRoute(planId, dayNumber))
                }
            )
        }

        composable(Screen.Progress.route) {
            ProgressScreen(
                onNavigateToBodyTracker = {
                    navController.navigate(Screen.BodyTracker.route)
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToSettings = { navController.navigate(Screen.WorkoutSettings.route) },
                onNavigateToReminder = { navController.navigate(Screen.ReminderSettings.route) },
                onNavigateToAchievements = { navController.navigate(Screen.Achievements.route) },
                onNavigateToPremium = { navController.navigate(Screen.Premium.route) },
                onNavigateToPrivacy = { navController.navigate(Screen.PrivacyData.route) },
                onNavigateToHistory = { navController.navigate(Screen.WorkoutHistory.route) },
                onNavigateToExerciseLibrary = { navController.navigate(Screen.ExerciseLibrary.route) }
            )
        }

        // Workout Flow
        composable(
            route = Screen.WorkoutOverview.route,
            arguments = listOf(
                navArgument("planId") { type = NavType.IntType },
                navArgument("dayNumber") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getInt("planId") ?: 1
            val dayNumber = backStackEntry.arguments?.getInt("dayNumber") ?: 1
            WorkoutOverviewScreen(
                planId = planId,
                dayNumber = dayNumber,
                onStartWorkout = {
                    navController.navigate(Screen.WorkoutPlayer.createRoute(planId, dayNumber))
                },
                onBack = { navController.popBackStack() },
                onExerciseInfo = { exerciseId ->
                    navController.navigate(Screen.ExerciseDetails.createRoute(exerciseId))
                }
            )
        }

        composable(
            route = Screen.QuickWorkoutOverview.route,
            arguments = listOf(navArgument("quickWorkoutId") { type = NavType.StringType })
        ) { backStackEntry ->
            val quickId = backStackEntry.arguments?.getString("quickWorkoutId") ?: "qw1"
            WorkoutOverviewScreen(
                planId = 1,
                dayNumber = 1,
                onStartWorkout = {
                    navController.navigate(Screen.WorkoutPlayer.createRoute(1, 1))
                },
                onBack = { navController.popBackStack() },
                onExerciseInfo = { exerciseId ->
                    navController.navigate(Screen.ExerciseDetails.createRoute(exerciseId))
                }
            )
        }

        composable(
            route = Screen.QuickWorkoutPlayer.route,
            arguments = listOf(navArgument("quickWorkoutId") { type = NavType.StringType })
        ) { backStackEntry ->
            WorkoutPlayerScreen(
                planId = 1,
                dayNumber = 1,
                isQuickWorkout = true,
                onWorkoutComplete = { sessionId ->
                    navController.navigate(Screen.WorkoutComplete.createRoute(sessionId)) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onExit = { navController.popBackStack() }
            )
        }

        composable(Screen.BodyTracker.route) {
            ProgressScreen(
                onNavigateToBodyTracker = {}
            )
        }

        composable(
            route = Screen.WorkoutPlayer.route,
            arguments = listOf(
                navArgument("planId") { type = NavType.IntType },
                navArgument("dayNumber") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getInt("planId") ?: 1
            val dayNumber = backStackEntry.arguments?.getInt("dayNumber") ?: 1
            WorkoutPlayerScreen(
                planId = planId,
                dayNumber = dayNumber,
                isQuickWorkout = false,
                onWorkoutComplete = { sessionId ->
                    navController.navigate(Screen.WorkoutComplete.createRoute(sessionId)) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onExit = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.WorkoutComplete.route,
            arguments = listOf(
                navArgument("sessionId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getInt("sessionId") ?: 0
            WorkoutCompleteScreen(
                sessionId = sessionId,
                onFinish = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onViewProgress = {
                    navController.navigate(Screen.Progress.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        // Exercise Library
        composable(Screen.ExerciseLibrary.route) {
            ExerciseLibraryScreen(
                onExerciseClick = { exerciseId ->
                    navController.navigate(Screen.ExerciseDetails.createRoute(exerciseId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ExerciseDetails.route,
            arguments = listOf(
                navArgument("exerciseId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getInt("exerciseId") ?: 1
            ExerciseDetailsScreen(
                exerciseId = exerciseId,
                onBack = { navController.popBackStack() }
            )
        }

        // Settings & Profile Sub-screens
        composable(Screen.WorkoutSettings.route) {
            WorkoutSettingsScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.ReminderSettings.route) {
            ReminderSettingsScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Achievements.route) {
            AchievementsScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Premium.route) {
            PremiumScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.PrivacyData.route) {
            PrivacyDataScreen(
                onBack = { navController.popBackStack() },
                onNavigateToPrivacyPolicy = { navController.navigate(Screen.PrivacyPolicy.route) },
                onNavigateToTerms = { navController.navigate(Screen.TermsOfService.route) },
                onNavigateToDisclaimer = { navController.navigate(Screen.HealthDisclaimer.route) },
                onDataDeleted = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.PrivacyPolicy.route) {
            PrivacyPolicyScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.TermsOfService.route) {
            TermsOfServiceScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.HealthDisclaimer.route) {
            HealthDisclaimerScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.WorkoutHistory.route) {
            WorkoutHistoryScreen(onBack = { navController.popBackStack() })
        }
    }
}

fun android.content.Context.findActivity(): androidx.activity.ComponentActivity? {
    var ctx = this
    while (ctx is android.content.ContextWrapper) {
        if (ctx is androidx.activity.ComponentActivity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

@Composable
fun getSharedOnboardingViewModel(): com.absforge.ui.onboarding.OnboardingViewModel {
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = remember(context) { context.findActivity() }
    return if (activity != null) {
        androidx.lifecycle.viewmodel.compose.viewModel(viewModelStoreOwner = activity)
    } else {
        androidx.lifecycle.viewmodel.compose.viewModel()
    }
}
