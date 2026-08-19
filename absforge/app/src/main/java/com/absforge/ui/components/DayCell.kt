package com.absforge.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.absforge.ui.theme.AbsForgePrimary
import com.absforge.ui.theme.AbsForgePrimaryDim
import com.absforge.ui.theme.AbsForgePrimaryGlow
import com.absforge.ui.theme.AbsForgeShapes
import com.absforge.ui.theme.AbsForgeSurface
import com.absforge.ui.theme.AbsForgeSurfaceContainer
import com.absforge.ui.theme.AbsForgeTextSecondary
import com.absforge.ui.theme.Typography

enum class DayCellState {
    COMPLETED, CURRENT, AVAILABLE, LOCKED, REST
}

@Composable
fun DayCell(
    dayNumber: Int,
    state: DayCellState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val isClickable = state == DayCellState.COMPLETED || state == DayCellState.CURRENT || state == DayCellState.AVAILABLE
    
    val baseModifier = modifier
        .aspectRatio(1f)
        .clip(AbsForgeShapes.cardShape)
        .then(
            if (isClickable) Modifier.clickable { onClick() } else Modifier
        )

    val contentModifier = when (state) {
        DayCellState.COMPLETED -> Modifier
            .background(AbsForgeSurface)
            .border(1.dp, AbsForgePrimaryDim.copy(alpha = 0.3f), AbsForgeShapes.cardShape)
        DayCellState.CURRENT -> Modifier
            .background(AbsForgeSurface)
            .border(1.dp, AbsForgePrimary, AbsForgeShapes.cardShape)
            .shadow(
                elevation = 8.dp,
                shape = AbsForgeShapes.cardShape,
                spotColor = AbsForgePrimaryGlow,
                ambientColor = AbsForgePrimaryGlow
            )
        DayCellState.AVAILABLE -> Modifier
            .background(AbsForgeSurface)
        DayCellState.LOCKED -> Modifier
            .background(AbsForgeSurfaceContainer.copy(alpha = 0.5f))
        DayCellState.REST -> Modifier
            .background(AbsForgeSurfaceContainer)
    }

    Box(
        modifier = baseModifier.then(contentModifier),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dayNumber.toString(),
                style = Typography.titleLarge,
                color = if (state == DayCellState.LOCKED) AbsForgeTextSecondary.copy(alpha = 0.5f) else Color.White
            )
            
            Box(modifier = Modifier.padding(top = 4.dp).size(20.dp), contentAlignment = Alignment.Center) {
                when (state) {
                    DayCellState.COMPLETED -> Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = AbsForgePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    DayCellState.CURRENT -> Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(AbsForgeShapes.pillShape)
                            .background(AbsForgePrimary)
                    )
                    DayCellState.LOCKED -> Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = AbsForgeTextSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(14.dp)
                    )
                    DayCellState.REST -> {
                        // Using a simple unicode moon since standard icons might not have it
                        Text("🌙", style = Typography.bodySmall)
                    }
                    DayCellState.AVAILABLE -> {}
                }
            }
        }
    }
}
