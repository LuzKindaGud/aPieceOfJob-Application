package com.bsit.peerx.ui.components

import android.content.res.Configuration
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import com.bsit.peerx.ui.theme.GradientAuthBg
import com.bsit.peerx.ui.theme.PeerXTheme

@Composable
fun CyberBackground(
    modifier: Modifier = Modifier,
    brush: Brush = GradientAuthBg,
    content: @Composable BoxScope.() -> Unit
) {
    val transition = rememberInfiniteTransition(label = "grid")
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 24000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gridOffset"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {

//            val step = 30.dp.toPx()
//            val stroke = 0.5.dp.toPx()
//            var x = -step + (offset % step)
//            while (x <= size.width + step) {
//                drawLine(
//                    color = BorderNeon.copy(alpha = 0.12f),
//                    start = Offset(x, 0f),
//                    end = Offset(x, size.height),
//                    strokeWidth = stroke
//                )
//                x += step
//            }
//            var y = -step + (offset % step)
//            while (y <= size.height + step) {
//                drawLine(
//                    color = BorderNeon.copy(alpha = 0.08f),
//                    start = Offset(0f, y),
//                    end = Offset(size.width, y),
//                    strokeWidth = stroke
//                )
//                y += step
//            }
        }
        content()
    }
}

@Preview(
    name = "Cyber Background Only",
    showBackground = true,
    backgroundColor = 0xFF0F172A,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun CyberBackgroundPreview() {
    PeerXTheme {
        CyberBackground {
            // Empty content to preview just the background
        }
    }
}





