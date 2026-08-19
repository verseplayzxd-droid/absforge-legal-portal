package com.absforge.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.absforge.ui.theme.AbsForgeGhostBorder
import com.absforge.ui.theme.AbsForgePrimary
import com.absforge.ui.theme.AbsForgeTextSecondary
import com.absforge.ui.theme.Typography

@Composable
fun ExerciseCard(
    name: String,
    targetArea: String,
    durationOrReps: String,
    modifier: Modifier = Modifier,
    onInfoClick: () -> Unit = {},
    thumbnailContent: @Composable () -> Unit = {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(AbsForgeGhostBorder)
        )
    }
) {
    AbsForgeCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                thumbnailContent()
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = Typography.titleMedium
                )
                Text(
                    text = targetArea,
                    style = Typography.bodyMedium,
                    color = AbsForgeTextSecondary
                )
                Text(
                    text = durationOrReps,
                    style = Typography.labelMedium,
                    color = AbsForgePrimary
                )
            }
            
            IconButton(onClick = onInfoClick) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Info",
                    tint = AbsForgeTextSecondary
                )
            }
        }
    }
}
