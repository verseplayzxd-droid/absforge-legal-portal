package com.absforge.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.absforge.ui.theme.AbsForgeOnPrimary
import com.absforge.ui.theme.AbsForgePrimary
import com.absforge.ui.theme.AbsForgeShapes
import com.absforge.ui.theme.AbsForgeTextPrimary
import com.absforge.ui.theme.Typography

@Composable
fun FilterChips(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(options) { option ->
            val isSelected = option == selectedOption
            
            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) AbsForgePrimary else Color(0x1AFFFFFF),
                label = "chipBackground"
            )
            
            val textColor by animateColorAsState(
                targetValue = if (isSelected) AbsForgeOnPrimary else AbsForgeTextPrimary,
                label = "chipText"
            )

            Box(
                modifier = Modifier
                    .clip(AbsForgeShapes.chipShape)
                    .background(backgroundColor)
                    .clickable { onOptionSelected(option) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    style = Typography.labelMedium,
                    color = textColor
                )
            }
        }
    }
}
