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

@Composable
fun FitnessLevelScreen(
    onNext: () -> Unit,
    viewModel: OnboardingViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val fitnessLevel by viewModel.fitnessLevel.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AbsForgeBackground)
            .padding(24.dp)
            .padding(top = 40.dp)
    ) {
        Text(
            text = "3 of 5",
            color = AbsForgePrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Select your fitness level",
            color = AbsForgeTextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        LevelCard(
            title = "Beginner",
            description = "New to structured core training.",
            isSelected = fitnessLevel == "beginner",
            onClick = { viewModel.fitnessLevel.value = "beginner" }
        )
        Spacer(modifier = Modifier.height(16.dp))
        LevelCard(
            title = "Intermediate",
            description = "Exercise regularly and want more intensity.",
            isSelected = fitnessLevel == "intermediate",
            onClick = { viewModel.fitnessLevel.value = "intermediate" }
        )
        Spacer(modifier = Modifier.height(16.dp))
        LevelCard(
            title = "Advanced",
            description = "Ready for demanding core workouts.",
            isSelected = fitnessLevel == "advanced",
            onClick = { viewModel.fitnessLevel.value = "advanced" }
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        AbsForgeButton(
            onClick = onNext,
            enabled = fitnessLevel.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "NEXT", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun LevelCard(
    title: String,
    description: String,
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
                    text = description,
                    color = AbsForgeTextSecondary,
                    fontSize = 14.sp
                )
            }
            if (isSelected) {
                Spacer(modifier = Modifier.width(16.dp))
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
