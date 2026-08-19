package com.absforge.ui.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.absforge.AbsForgeApplication
import com.absforge.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@Composable
fun SplashScreen(
    onNavigateToWelcome: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val context = LocalContext.current
    val app = (context.applicationContext as? AbsForgeApplication) ?: AbsForgeApplication.instance

    var visible by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(1000),
        label = "alpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.9f,
        animationSpec = tween(1000),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        visible = true
        delay(1200)
        try {
            val isCompleted = app.preferencesManager.onboardingCompleted.first()
            if (isCompleted) {
                // Thorough state validation before routing to Home
                val isValid = withContext(Dispatchers.IO) {
                    try {
                        val profile = app.database.userProfileDao().getProfileSync()
                        val rawPlanId = app.preferencesManager.selectedPlanId.first()
                        val planId = if (rawPlanId in 1..3) rawPlanId else 1
                        val plan = app.database.workoutDao().getPlanById(planId)
                        val days = app.database.workoutDao().getDayByNumber(planId, 1)

                        profile != null && plan != null && days != null
                    } catch (e: Exception) {
                        e.printStackTrace()
                        false
                    }
                }

                if (isValid) {
                    onNavigateToHome()
                } else {
                    // State incomplete or corrupt - safely reset onboarding state
                    app.preferencesManager.setOnboardingCompleted(false)
                    onNavigateToWelcome()
                }
            } else {
                onNavigateToWelcome()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onNavigateToWelcome()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AbsForgeBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(alpha)
                .scale(scale)
        ) {
            Text(
                text = "ABSFORGE",
                color = AbsForgePrimary,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "30 DAY ABS WORKOUT",
                color = AbsForgeTextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )
        }
    }
}
