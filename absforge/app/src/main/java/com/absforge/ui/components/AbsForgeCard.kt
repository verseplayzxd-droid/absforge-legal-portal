package com.absforge.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.absforge.ui.theme.AbsForgeGhostBorder
import com.absforge.ui.theme.AbsForgePrimary
import com.absforge.ui.theme.AbsForgePrimaryGlow
import com.absforge.ui.theme.AbsForgeShapes
import com.absforge.ui.theme.AbsForgeSurface

@Composable
fun AbsForgeCard(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    active: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val borderColor = if (selected) AbsForgePrimary else AbsForgeGhostBorder
    
    val cardModifier = modifier
        .clip(AbsForgeShapes.cardShape)
        .background(AbsForgeSurface)
        .border(
            width = 1.dp,
            color = borderColor,
            shape = AbsForgeShapes.cardShape
        )
        .then(
            if (active) {
                Modifier.shadow(
                    elevation = 20.dp,
                    shape = AbsForgeShapes.cardShape,
                    spotColor = AbsForgePrimaryGlow,
                    ambientColor = AbsForgePrimaryGlow
                )
            } else {
                Modifier
            }
        )

    Box(
        modifier = cardModifier,
        content = content
    )
}
