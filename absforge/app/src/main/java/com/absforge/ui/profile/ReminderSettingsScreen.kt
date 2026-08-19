package com.absforge.ui.profile

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.absforge.AbsForgeApplication
import com.absforge.audio.HapticManager
import com.absforge.audio.SoundManager
import com.absforge.notifications.ReminderScheduler
import com.absforge.ui.theme.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderSettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val preferencesManager = remember { AbsForgeApplication.instance.preferencesManager }

    var dailyReminder by remember { mutableStateOf(false) }
    var hour by remember { mutableIntStateOf(19) }
    var minute by remember { mutableIntStateOf(0) }
    var strongReminder by remember { mutableStateOf(false) }

    var showPrePermissionDialog by remember { mutableStateOf(false) }
    var showDeniedDialog by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Check Android 13+ Notification Permission
    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            dailyReminder = true
            coroutineScope.launch {
                preferencesManager.setReminderEnabled(true)
                preferencesManager.setReminderHour(hour)
                preferencesManager.setReminderMinute(minute)
                ReminderScheduler.scheduleReminder(context, hour, minute)
            }
        } else {
            dailyReminder = false
            showDeniedDialog = true
            coroutineScope.launch {
                preferencesManager.setReminderEnabled(false)
                ReminderScheduler.cancelReminder(context)
            }
        }
    }

    LaunchedEffect(Unit) {
        val remEnabled = preferencesManager.reminderEnabled.first()
        val remHour = preferencesManager.reminderHour.first()
        val remMinute = preferencesManager.reminderMinute.first()
        val strongRem = preferencesManager.strongReminderEnabled.first()

        dailyReminder = remEnabled && hasNotificationPermission()
        hour = remHour
        minute = remMinute
        strongReminder = strongRem
    }

    val timePickerState = rememberTimePickerState(
        initialHour = hour,
        initialMinute = minute,
        is24Hour = true
    )

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        hour = timePickerState.hour
                        minute = timePickerState.minute
                        showTimePicker = false
                        coroutineScope.launch {
                            preferencesManager.setReminderHour(hour)
                            preferencesManager.setReminderMinute(minute)
                            if (dailyReminder) {
                                ReminderScheduler.scheduleReminder(context, hour, minute)
                            }
                        }
                    }
                ) {
                    Text("OK", color = AbsForgePrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("CANCEL", color = AbsForgeTextSecondary)
                }
            },
            title = { Text("Select Reminder Time", color = AbsForgeTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                TimePicker(state = timePickerState)
            },
            containerColor = AbsForgeSurface,
            titleContentColor = AbsForgeTextPrimary,
            textContentColor = AbsForgeTextPrimary
        )
    }

    if (showPrePermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPrePermissionDialog = false },
            containerColor = AbsForgeSurface,
            title = { Text("Enable Workout Reminders", color = AbsForgeTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Enable workout reminders so AbsForge can notify you at your chosen workout time and keep your streak alive.",
                    color = AbsForgeTextSecondary,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPrePermissionDialog = false
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            dailyReminder = true
                            coroutineScope.launch {
                                preferencesManager.setReminderEnabled(true)
                                ReminderScheduler.scheduleReminder(context, hour, minute)
                            }
                        }
                    }
                ) {
                    Text("ALLOW REMINDERS", color = AbsForgePrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPrePermissionDialog = false
                        dailyReminder = false
                    }
                ) {
                    Text("NOT NOW", color = AbsForgeTextSecondary)
                }
            }
        )
    }

    if (showDeniedDialog) {
        AlertDialog(
            onDismissRequest = { showDeniedDialog = false },
            containerColor = AbsForgeSurface,
            title = { Text("Notification Permission Denied", color = AbsForgeTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Notification permission is required to receive daily workout reminders. You can enable notifications in system settings.",
                    color = AbsForgeTextSecondary,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeniedDialog = false
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }
                ) {
                    Text("OPEN SETTINGS", color = AbsForgePrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeniedDialog = false }) {
                    Text("CANCEL", color = AbsForgeTextSecondary)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Workout Reminder", color = AbsForgeTextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AbsForgeTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AbsForgeBackground)
            )
        },
        containerColor = AbsForgeBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            SettingSwitchRow("Daily Reminder", dailyReminder) { enabled ->
                SoundManager.playButtonClick()
                HapticManager.buttonPress(context)
                if (enabled) {
                    if (hasNotificationPermission()) {
                        dailyReminder = true
                        coroutineScope.launch {
                            preferencesManager.setReminderEnabled(true)
                            ReminderScheduler.scheduleReminder(context, hour, minute)
                        }
                    } else {
                        showPrePermissionDialog = true
                    }
                } else {
                    dailyReminder = false
                    coroutineScope.launch {
                        preferencesManager.setReminderEnabled(false)
                        ReminderScheduler.cancelReminder(context)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (dailyReminder) {
                Text("Reminder Time", color = AbsForgeTextSecondary, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = AbsForgeSurface,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            SoundManager.playButtonClick()
                            showTimePicker = true
                        }
                        .padding(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Daily at", color = AbsForgeTextPrimary, fontSize = 16.sp)
                        Text(
                            text = String.format("%02d:%02d", hour, minute),
                            color = AbsForgePrimary,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                SettingSwitchRow("Strong Reminder", strongReminder) { enabled ->
                    SoundManager.playButtonClick()
                    HapticManager.buttonPress(context)
                    strongReminder = enabled
                    coroutineScope.launch {
                        preferencesManager.setStrongReminderEnabled(enabled)
                    }
                }
                Text(
                    "Send persistent notification until workout is completed",
                    color = AbsForgeTextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }
    }
}
