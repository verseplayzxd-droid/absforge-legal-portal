package com.absforge.ui.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.absforge.ui.components.AbsForgeButton
import com.absforge.ui.theme.*
import java.util.Calendar

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutPreferenceScreen(
    onNext: () -> Unit,
    viewModel: OnboardingViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val preferredTrainTime by viewModel.preferredTrainTime.collectAsState()

    var customTimeText by remember { mutableStateOf("20:00") }
    var showTimePicker by remember { mutableStateOf(false) }

    val cal = Calendar.getInstance()
    val timePickerState = rememberTimePickerState(
        initialHour = cal.get(Calendar.HOUR_OF_DAY),
        initialMinute = cal.get(Calendar.MINUTE),
        is24Hour = true
    )

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val formatted = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                    customTimeText = formatted
                    viewModel.preferredTrainTime.value = formatted
                    showTimePicker = false
                }) {
                    Text("OK", color = AbsForgePrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel", color = AbsForgeTextSecondary)
                }
            },
            title = { Text("Select Training Time", color = AbsForgeTextPrimary) },
            text = {
                TimePicker(state = timePickerState)
            },
            containerColor = AbsForgeSurfaceElevated,
            titleContentColor = AbsForgeTextPrimary,
            textContentColor = AbsForgeTextPrimary
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AbsForgeBackground)
            .padding(24.dp)
            .padding(top = 40.dp)
    ) {
        Text(
            text = "5 of 5",
            color = AbsForgePrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "When do you usually train?",
            color = AbsForgeTextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))

        TimeCard(
            title = "Morning",
            subtitle = "6:00 - 11:00 (8:00 AM)",
            isSelected = preferredTrainTime == "morning",
            onClick = { viewModel.preferredTrainTime.value = "morning" }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TimeCard(
            title = "Afternoon",
            subtitle = "12:00 - 16:00 (2:00 PM)",
            isSelected = preferredTrainTime == "afternoon",
            onClick = { viewModel.preferredTrainTime.value = "afternoon" }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TimeCard(
            title = "Evening",
            subtitle = "17:00 - 21:00 (7:00 PM)",
            isSelected = preferredTrainTime == "evening",
            onClick = { viewModel.preferredTrainTime.value = "evening" }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TimeCard(
            title = "Custom Time",
            subtitle = if (preferredTrainTime.contains(":")) "Selected: $preferredTrainTime" else "Choose your own time",
            isSelected = preferredTrainTime == "custom" || preferredTrainTime.contains(":"),
            onClick = {
                viewModel.preferredTrainTime.value = customTimeText
                showTimePicker = true
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        AbsForgeButton(
            text = "NEXT",
            onClick = onNext,
            enabled = preferredTrainTime.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun TimeCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = AbsForgeSurface),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) AbsForgePrimary else AbsForgeGhostBorder
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = if (isSelected) AbsForgeTextPrimary else AbsForgeTextSecondary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    color = AbsForgeTextSecondary,
                    fontSize = 14.sp
                )
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = AbsForgePrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
