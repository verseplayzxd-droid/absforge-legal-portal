package com.absforge.ui.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.absforge.ui.components.AbsForgeButton
import com.absforge.ui.theme.*

@Composable
fun GoalSelectionScreen(
    onNext: () -> Unit,
    viewModel: OnboardingViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val selectedGoal by viewModel.selectedGoal.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AbsForgeBackground)
            .padding(24.dp)
            .padding(top = 40.dp)
    ) {
        Text(
            text = "2 of 5",
            color = AbsForgePrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "What's your goal?",
            color = AbsForgeTextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        GoalCard(
            title = "Lose Belly Fat",
            icon = Icons.Default.LocalFireDepartment,
            isSelected = selectedGoal == "lose_fat",
            onClick = { viewModel.selectedGoal.value = "lose_fat" }
        )
        Spacer(modifier = Modifier.height(16.dp))
        GoalCard(
            title = "Build Strong Abs",
            icon = Icons.Default.FitnessCenter,
            isSelected = selectedGoal == "strong_abs",
            onClick = { viewModel.selectedGoal.value = "strong_abs" }
        )
        Spacer(modifier = Modifier.height(16.dp))
        GoalCard(
            title = "Get Six Pack Abs",
            icon = Icons.Default.Star,
            isSelected = selectedGoal == "six_pack",
            onClick = { viewModel.selectedGoal.value = "six_pack" }
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        AbsForgeButton(
            onClick = onNext,
            enabled = selectedGoal.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "NEXT", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun GoalCard(
    title: String,
    icon: ImageVector,
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
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) AbsForgePrimary else AbsForgeTextSecondary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                color = if (isSelected) AbsForgeTextPrimary else AbsForgeTextSecondary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
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
