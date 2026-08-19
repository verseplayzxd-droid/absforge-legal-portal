package com.absforge.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.absforge.AbsForgeApplication
import com.absforge.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToReminder: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToPremium: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToExerciseLibrary: () -> Unit
) {
    val bgColor = AbsForgeBackground
    val surfaceColor = AbsForgeSurface
    val limeColor = AbsForgePrimary

    val context = LocalContext.current
    val app = (context.applicationContext as? AbsForgeApplication) ?: AbsForgeApplication.instance

    var userName by remember { mutableStateOf("Athlete") }
    var userGoal by remember { mutableStateOf("Strong Core") }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val profile = app.database.userProfileDao().getProfileSync()
                withContext(Dispatchers.Main) {
                    if (profile != null && profile.name.isNotBlank()) {
                        userName = profile.name
                        userGoal = profile.goal.replace("_", " ").uppercase()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(16.dp)
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(surfaceColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.take(1).uppercase(),
                        color = limeColor,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = userName,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = userGoal,
                    color = AbsForgeTextSecondary,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        item {
            Text(
                text = "WORKOUT SETTINGS",
                color = limeColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )
            ProfileMenuItem("Workout Preferences", Icons.Default.Settings, onClick = onNavigateToSettings)
            ProfileMenuItem("Daily Reminder", Icons.Default.Notifications, onClick = onNavigateToReminder)
            ProfileMenuItem("Exercise Library", Icons.Default.FitnessCenter, onClick = onNavigateToExerciseLibrary)
            ProfileMenuItem("Workout History", Icons.Default.List, onClick = onNavigateToHistory)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "MY STATS",
                color = limeColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )
            ProfileMenuItem("Achievements", Icons.Default.EmojiEvents, onClick = onNavigateToAchievements)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "OTHER",
                color = limeColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )
            ProfileMenuItem("Privacy & Data", Icons.Default.Lock, onClick = onNavigateToPrivacy)

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun ProfileMenuItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AbsForgeSurface),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = AbsForgePrimary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, color = Color.White, fontSize = 16.sp, modifier = Modifier.weight(1f))
            Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = null, tint = AbsForgeTextSecondary)
        }
    }
}
