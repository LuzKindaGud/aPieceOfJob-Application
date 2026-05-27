package com.bsit.peerx.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.bsit.peerx.ui.theme.NeonCyan
import com.bsit.peerx.ui.theme.NeonPrimary
import com.bsit.peerx.ui.theme.NeonSecondary

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import com.bsit.peerx.ui.theme.PeerXTheme

@Composable
fun CyberRing(modifier: Modifier = Modifier) {
    val rings = listOf(NeonPrimary, NeonSecondary, NeonCyan)
    val infiniteTransition = rememberInfiniteTransition(label = "ring")
    val rotations = rings.mapIndexed { index, _ ->
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                tween(durationMillis = 4000 + index * 1000, easing = LinearEasing),
                if (index % 2 == 0) RepeatMode.Restart else RepeatMode.Reverse
            ),
            label = "ring$index"
        )
    }
    Canvas(modifier = modifier.size(300.dp)) {
        rings.forEachIndexed { index, color ->
            val radius = (120.dp - (20.dp * index.toFloat())).toPx()
            rotate(rotations[index].value) {
                drawArc(
                    color = color.copy(alpha = 0.8f),
                    startAngle = -90f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(width = (3 - index * 0.5f).dp.toPx(), cap = StrokeCap.Round),
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                )
            }
        }
    }
}

@Preview(
    name = "CyberRing",
    showBackground = true,
    backgroundColor = 0xFF0F172A,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun CyberRingPreview() {
    PeerXTheme {
        CyberRing()
    }
}





