package com.bsit.peerx.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.neonGlow(color: Color = NeonPrimary, radius: Dp = 20.dp): Modifier = drawBehind {
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(color.copy(alpha = 0.4f), Color.Transparent),
            center = center,
            radius = radius.toPx() * 2f
        ),
        blendMode = BlendMode.Screen
    )
}

fun Modifier.glassSurface(): Modifier = this
    .background(GlassSurface, shape = RoundedCornerShape(20.dp))
    .border(1.dp, GlassBorder, shape = RoundedCornerShape(20.dp))

fun Modifier.neonBorder(
    shape: Shape = RoundedCornerShape(10.dp),
    color: Color = BorderNeon
): Modifier = border(1.dp, color, shape)

fun Modifier.gradientBackground(
    brush: Brush = GradientPrimary,
    shape: Shape = RoundedCornerShape(10.dp)
): Modifier = background(brush, shape)


