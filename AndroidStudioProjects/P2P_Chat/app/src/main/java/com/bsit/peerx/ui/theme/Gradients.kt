package com.bsit.peerx.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush

val GradientPrimary = Brush.linearGradient(
    colors = listOf(NeonPrimary, NeonSecondary),
    start = Offset.Zero,
    end = Offset.Infinite
)

val GradientAuthBg = Brush.verticalGradient(
    colors = listOf(BgDeepIndigo, BgPrimary, BgAuthBottom)
)

val GradientGold = Brush.linearGradient(colors = listOf(GoldStart, GoldEnd))
val GradientSilver = Brush.linearGradient(colors = listOf(SilverStart, SilverEnd))
val GradientBronze = Brush.linearGradient(colors = listOf(BronzeStart, BronzeEnd))
val GradientSuccess = Brush.linearGradient(colors = listOf(NeonGreen, SuccessDeep))
val GradientDanger = Brush.linearGradient(colors = listOf(NeonDanger, DangerDeep))



