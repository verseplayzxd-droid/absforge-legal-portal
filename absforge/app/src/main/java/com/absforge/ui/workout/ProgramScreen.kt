package com.absforge.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.absforge.ui.theme.*

@Composable
fun ProgramScreen(
    onDaySelected: (planId: Int, dayNumber: Int) -> Unit,
    viewModel: ProgramViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize().background(AbsForgeBackground), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AbsForgePrimary)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AbsForgeBackground)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(
            text = "30 DAY ABS CHALLENGE",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Progress Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${(uiState.progressPercent * 100).toInt()}% COMPLETE",
                    color = AbsForgePrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { uiState.progressPercent },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = AbsForgePrimary,
                    trackColor = AbsForgeSurfaceElevated
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x33FFA500))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.LocalFireDepartment, contentDescription = null, tint = Color(0xFFFFA500), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${uiState.currentStreak}",
                    color = Color(0xFFFFA500),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(AbsForgeSurface)
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val tabs = listOf("BEGINNER", "INTERMEDIATE", "ADVANCED")
            tabs.forEachIndexed { index, title ->
                val isSelected = index == uiState.selectedTabIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) AbsForgePrimary.copy(alpha = 0.2f) else Color.Transparent)
                        .clickable { viewModel.onTabSelected(index) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = if (isSelected) AbsForgePrimary else AbsForgeTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(30) { index ->
                val dayNumber = index + 1
                val dayState = viewModel.getDayState(dayNumber)
                DayCell(
                    dayNumber = dayNumber,
                    state = dayState,
                    onClick = {
                        if (dayState != DayState.LOCKED) {
                            onDaySelected(uiState.selectedPlanId, dayNumber)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Start.io Program Banner
        com.absforge.ads.AbsForgeAdBanner(
            modifier = Modifier.fillMaxWidth(),
            tag = "program_bottom_banner"
        )
    }
}

@Composable
fun DayCell(dayNumber: Int, state: DayState, onClick: () -> Unit) {
    val bgColor = when (state) {
        DayState.CURRENT -> AbsForgeSurfaceElevated
        DayState.COMPLETED -> AbsForgePrimary.copy(alpha = 0.1f)
        DayState.LOCKED -> AbsForgeSurface.copy(alpha = 0.5f)
        else -> AbsForgeSurface
    }
    
    val borderColor = when (state) {
        DayState.CURRENT -> AbsForgePrimary
        DayState.COMPLETED -> AbsForgePrimary.copy(alpha = 0.3f)
        else -> AbsForgeGhostBorder
    }
    
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(if (state == DayState.CURRENT) 2.dp else 1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(enabled = state != DayState.LOCKED, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "DAY",
                color = if (state == DayState.LOCKED) AbsForgeTextSecondary.copy(alpha = 0.5f) else AbsForgeTextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$dayNumber",
                color = if (state == DayState.LOCKED) AbsForgeTextSecondary.copy(alpha = 0.5f) else Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            when (state) {
                DayState.COMPLETED -> Icon(Icons.Filled.Check, contentDescription = null, tint = AbsForgePrimary, modifier = Modifier.size(16.dp))
                DayState.CURRENT -> Text("TODAY", color = AbsForgePrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                DayState.LOCKED -> Icon(Icons.Filled.Lock, contentDescription = null, tint = AbsForgeTextSecondary.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                DayState.REST -> Icon(Icons.Filled.Nightlight, contentDescription = null, tint = AbsForgePrimary, modifier = Modifier.size(16.dp))
                else -> Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
