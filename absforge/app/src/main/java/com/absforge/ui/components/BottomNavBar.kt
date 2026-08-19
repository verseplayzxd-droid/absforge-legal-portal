package com.absforge.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.absforge.ui.theme.AbsForgePrimary
import com.absforge.ui.theme.Typography

enum class NavTab(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    WORKOUTS("Workouts", Icons.Default.FitnessCenter),
    PROGRESS("Progress", Icons.Default.BarChart),
    PROFILE("Profile", Icons.Default.Person)
}

@Composable
fun BottomNavBar(
    selectedTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF101010))
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavTab.values().forEach { tab ->
            val isSelected = selectedTab == tab
            val color by animateColorAsState(
                targetValue = if (isSelected) AbsForgePrimary else Color(0xFF707070),
                animationSpec = tween(250),
                label = "colorAnimation"
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelected(tab) }
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = tab.icon,
                    contentDescription = tab.title,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = tab.title,
                    style = Typography.labelSmall,
                    color = color,
                    modifier = Modifier.padding(top = 4.dp)
                )
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(AbsForgePrimary)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedTab = when {
        currentRoute.contains("workouts") -> NavTab.WORKOUTS
        currentRoute.contains("progress") -> NavTab.PROGRESS
        currentRoute.contains("profile") -> NavTab.PROFILE
        else -> NavTab.HOME
    }

    BottomNavBar(
        selectedTab = selectedTab,
        onTabSelected = { tab ->
            val route = when (tab) {
                NavTab.HOME -> "home"
                NavTab.WORKOUTS -> "workouts"
                NavTab.PROGRESS -> "progress"
                NavTab.PROFILE -> "profile"
            }
            onNavigate(route)
        },
        modifier = modifier
    )
}
