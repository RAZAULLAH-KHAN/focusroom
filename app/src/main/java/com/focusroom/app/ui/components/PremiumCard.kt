package com.focusroom.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.focusroom.app.core.theme.CardBackground
import com.focusroom.app.core.theme.NeonCyan
import com.focusroom.app.core.theme.NeonPurple

@Composable
fun PremiumCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    glowing: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val borderBrush = if (glowing) {
        Brush.linearGradient(listOf(NeonPurple, NeonCyan))
    } else {
        Brush.linearGradient(listOf(Color(0x338B5CF6), Color(0x1106B6D4)))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(CardBackground)
            .border(
                BorderStroke(1.5.dp, borderBrush),
                shape = RoundedCornerShape(cornerRadius)
            )
            .padding(1.dp)
    ) {
        Box(
            modifier = Modifier.padding(20.dp),
            content = content
        )
    }
}
