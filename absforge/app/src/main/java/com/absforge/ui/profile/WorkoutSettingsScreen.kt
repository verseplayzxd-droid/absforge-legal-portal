package com.absforge.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.absforge.AbsForgeApplication
import com.absforge.audio.HapticManager
import com.absforge.audio.SoundManager
import com.absforge.audio.VoiceCoachManager
import com.absforge.ui.theme.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutSettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val preferencesManager = remember { AbsForgeApplication.instance.preferencesManager }

    var defaultRest by remember { mutableIntStateOf(30) }
    var voiceGuidance by remember { mutableStateOf(true) }
    var soundEffects by remember { mutableStateOf(true) }
    var vibration by remember { mutableStateOf(true) }
    var autoStartNext by remember { mutableStateOf(false) }
    var countdown by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val rest = preferencesManager.defaultRestSeconds.first()
        val voice = preferencesManager.voiceGuidanceEnabled.first()
        val sound = preferencesManager.soundEffectsEnabled.first()
        val vib = preferencesManager.vibrationEnabled.first()
        val autoStart = preferencesManager.autoStartNext.first()
        val count = preferencesManager.countdownEnabled.first()

        defaultRest = rest
        voiceGuidance = voice
        soundEffects = sound
        vibration = vib
        autoStartNext = autoStart
        countdown = count

        SoundManager.soundEffectsEnabled = sound
        SoundManager.countdownSoundsEnabled = count
        VoiceCoachManager.voiceGuidanceEnabled = voice
        HapticManager.vibrationEnabled = vib
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Workout Settings", color = AbsForgeTextPrimary, fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Default Rest Time", color = AbsForgeTextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(20, 30, 45, 60).forEach { time ->
                    val selected = defaultRest == time
                    Surface(
                        color = if (selected) AbsForgePrimary else AbsForgeSurface,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.clickable {
                            SoundManager.playButtonClick()
                            HapticManager.buttonPress(context)
                            defaultRest = time
                            coroutineScope.launch { preferencesManager.setDefaultRestSeconds(time) }
                        }
                    ) {
                        Text(
                            "${time}s",
                            color = if (selected) Color.White else AbsForgeTextPrimary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("AUDIO & FEEDBACK", color = AbsForgePrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))

            SettingSwitchRow("Sound Effects", soundEffects) { enabled ->
                soundEffects = enabled
                SoundManager.soundEffectsEnabled = enabled
                SoundManager.playButtonClick()
                HapticManager.buttonPress(context)
                coroutineScope.launch { preferencesManager.setSoundEffectsEnabled(enabled) }
            }

            SettingSwitchRow("Countdown Sounds", countdown) { enabled ->
                countdown = enabled
                SoundManager.countdownSoundsEnabled = enabled
                SoundManager.playButtonClick()
                HapticManager.buttonPress(context)
                coroutineScope.launch { preferencesManager.setCountdownEnabled(enabled) }
            }

            SettingSwitchRow("Voice Guidance", voiceGuidance) { enabled ->
                voiceGuidance = enabled
                VoiceCoachManager.voiceGuidanceEnabled = enabled
                SoundManager.playButtonClick()
                HapticManager.buttonPress(context)
                coroutineScope.launch { preferencesManager.setVoiceGuidanceEnabled(enabled) }
            }

            SettingSwitchRow("Vibration / Haptics", vibration) { enabled ->
                vibration = enabled
                HapticManager.vibrationEnabled = enabled
                SoundManager.playButtonClick()
                if (enabled) HapticManager.buttonPress(context)
                coroutineScope.launch { preferencesManager.setVibrationEnabled(enabled) }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("AUTOMATION", color = AbsForgePrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))

            SettingSwitchRow("Auto Start Next Exercise", autoStartNext) { enabled ->
                autoStartNext = enabled
                SoundManager.playButtonClick()
                HapticManager.buttonPress(context)
                coroutineScope.launch { preferencesManager.setAutoStartNext(enabled) }
            }
        }
    }
}

@Composable
fun SettingSwitchRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, color = AbsForgeTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AbsForgePrimary,
                uncheckedThumbColor = AbsForgeTextSecondary,
                uncheckedTrackColor = AbsForgeSurfaceElevated
            )
        )
    }
}
