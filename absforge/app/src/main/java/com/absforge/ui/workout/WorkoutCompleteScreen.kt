package com.absforge.ui.workout

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.absforge.AbsForgeApplication
import com.absforge.ads.AdMobManager
import com.absforge.ui.components.AbsForgeButton
import com.absforge.ui.theme.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WorkoutCompleteViewModel : ViewModel() {
    private val db = AbsForgeApplication.instance.database

    data class WorkoutCompleteState(
        val dayNumber: Int = 1,
        val durationSeconds: Int = 0,
        val durationFormatted: String = "00:00",
        val calories: Int = 0,
        val completedExercises: Int = 0,
        val totalExercises: Int = 0,
        val currentStreak: Int = 1,
        val dayProgressText: String = "1 / 30",
        val percentComplete: Float = 3.3f,
        val quote: String = "Day 1 crushed. Keep the momentum going."
    )

    private val _state = MutableStateFlow(WorkoutCompleteState())
    val state: StateFlow<WorkoutCompleteState> = _state.asStateFlow()

    fun loadSession(sessionId: Int) {
        viewModelScope.launch {
            val session = db.sessionDao().getSessionById(sessionId)
            if (session != null) {
                val durationMins = session.durationSeconds / 60
                val durationSecs = session.durationSeconds % 60
                val durStr = String.format("%02d:%02d", durationMins, durationSecs)

                val allSessions = db.sessionDao().getAllSessions().first()
                val qualifying = allSessions.filter { (it.completedExercises.toFloat() / it.totalExercises.coerceAtLeast(1)) >= 0.90f }

                var streakCount = 0
                val sortedSessions = qualifying.filter { it.endTime != null }.sortedByDescending { it.endTime ?: 0L }
                if (sortedSessions.isNotEmpty()) {
                    val todayDay = System.currentTimeMillis() / 86400000L
                    var currentStreakDay = (sortedSessions.first().endTime ?: 0L) / 86400000L
                    if (todayDay - currentStreakDay <= 1) {
                        streakCount = 1
                        for (i in 1 until sortedSessions.size) {
                            val sessionDay = (sortedSessions[i].endTime ?: 0L) / 86400000L
                            if (currentStreakDay - sessionDay == 1L) {
                                streakCount++
                                currentStreakDay = sessionDay
                            } else if (currentStreakDay - sessionDay > 1L) {
                                break
                            }
                        }
                    }
                }

                val quotes = listOf(
                    "Day ${session.dayNumber} crushed. Keep the momentum going!",
                    "One workout down. ${30 - session.dayNumber} days to go.",
                    "Great start. Come back tomorrow and keep your streak alive!",
                    "Your core is getting stronger every day!"
                )

                val pct = ((session.dayNumber.toFloat() / 30f) * 1000).toInt() / 10f

                _state.update {
                    it.copy(
                        dayNumber = session.dayNumber,
                        durationSeconds = session.durationSeconds,
                        durationFormatted = durStr,
                        calories = session.caloriesBurned,
                        completedExercises = session.completedExercises,
                        totalExercises = session.totalExercises,
                        currentStreak = streakCount.coerceAtLeast(1),
                        dayProgressText = "${session.dayNumber} / 30",
                        percentComplete = pct,
                        quote = quotes[session.dayNumber % quotes.size]
                    )
                }
            }
        }
    }
}

@Composable
fun WorkoutCompleteScreen(
    sessionId: Int,
    onFinish: () -> Unit,
    onViewProgress: () -> Unit = {},
    viewModel: WorkoutCompleteViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(sessionId) {
        viewModel.loadSession(sessionId)
        AdMobManager.loadInterstitial(context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AbsForgeBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 700.dp)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Checkmark celebration icon
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(AbsForgePrimary.copy(alpha = 0.2f), shape = RoundedCornerShape(50.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("✓", color = AbsForgePrimary, fontSize = 56.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "WORKOUT COMPLETE!",
                color = AbsForgeTextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "🔥 DAY ${state.dayNumber} COMPLETE",
                color = AbsForgePrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(title = "Exercises", value = "${state.completedExercises} / ${state.totalExercises}", modifier = Modifier.weight(1f))
                StatCard(title = "Time", value = state.durationFormatted, modifier = Modifier.weight(1f))
                StatCard(title = "Calories", value = "~${state.calories} kcal", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Streak & Challenge Progress Card
            Card(
                colors = CardDefaults.cardColors(containerColor = AbsForgeSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("30-DAY CHALLENGE", color = AbsForgeTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("${state.percentComplete}%", color = AbsForgePrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = state.percentComplete / 100f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = AbsForgePrimary,
                        trackColor = AbsForgeSurfaceElevated
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = state.quote,
                        color = AbsForgeTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "I just crushed Day ${state.dayNumber} on AbsForge! ${state.calories} calories burned in ${state.durationFormatted}.")
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, null))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AbsForgeTextSecondary)
                ) {
                    Text("SHARE", color = AbsForgeTextPrimary, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onViewProgress,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AbsForgePrimary)
                ) {
                    Text("PROGRESS", color = AbsForgePrimary, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            AbsForgeButton(
                text = "DONE",
                onClick = {
                    val activity = context.findActivity()
                    if (activity != null) {
                        AdMobManager.showInterstitial(
                            activity = activity,
                            tag = "workout_complete_interstitial",
                            onClosed = onFinish
                        )
                    } else {
                        onFinish()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AbsForgeSurface),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, color = AbsForgeTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = AbsForgeTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
