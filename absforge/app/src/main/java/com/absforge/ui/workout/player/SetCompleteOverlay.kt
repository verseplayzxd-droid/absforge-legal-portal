package com.absforge.ui.workout.player

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.absforge.ads.AdMobManager
import com.absforge.ui.components.AbsForgeButton
import com.absforge.ui.theme.*

@Composable
fun SetCompleteOverlay(
    state: WorkoutPlayerState,
    onContinue: () -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = AbsForgeSurface),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Set Badge
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(AbsForgePrimary.copy(alpha = 0.2f), shape = RoundedCornerShape(32.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⚡", fontSize = 32.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "SET ${state.currentSetNumber} COMPLETE",
                    color = AbsForgeTextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${state.completedExerciseCount} / ${state.totalExercises} Exercises",
                    color = AbsForgePrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Great work! Take a short break before starting the next set.",
                    color = AbsForgeTextSecondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                AbsForgeButton(
                    text = "CONTINUE",
                    onClick = {
                        val activity = context.findActivity()
                        if (activity != null) {
                            AdMobManager.showMidWorkoutInterstitial(
                                activity = activity,
                                setIndex = state.currentSetNumber,
                                onClosed = onContinue
                            )
                        } else {
                            onContinue()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                )
            }
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
