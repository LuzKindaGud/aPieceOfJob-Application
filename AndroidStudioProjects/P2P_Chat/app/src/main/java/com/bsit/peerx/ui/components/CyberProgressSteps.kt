package com.bsit.peerx.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.bsit.peerx.ui.theme.BorderNeon
import com.bsit.peerx.ui.theme.GlassSurface
import com.bsit.peerx.ui.theme.GradientPrimary
import com.bsit.peerx.ui.theme.NeonGreen
import com.bsit.peerx.ui.theme.NeonSecondary
import com.bsit.peerx.ui.theme.TextMuted
import com.bsit.peerx.ui.theme.TextPrimary

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import com.bsit.peerx.ui.theme.PeerXTheme

@Composable
fun CyberProgressSteps(
    currentStep: Int,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        labels.forEachIndexed { index, label ->
            val step = index + 1
            val isActive = step == currentStep
            val isCompleted = step < currentStep
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val fillBrush = when {
                    isCompleted -> Brush.linearGradient(listOf(NeonGreen, NeonGreen))
                    isActive -> GradientPrimary
                    else -> Brush.linearGradient(listOf(GlassSurface, GlassSurface))
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(35.dp)
                        .background(fillBrush, shape = CircleShape)
                        .border(2.dp, if (isActive) NeonSecondary else BorderNeon, CircleShape)
                ) {
                    Text(
                        text = "$step",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isActive || isCompleted) TextPrimary else TextMuted
                    )
                }
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isActive) NeonSecondary else TextMuted
                )
            }
            if (index < labels.lastIndex) {
                Box(
                    modifier = Modifier
                        .size(width = 40.dp, height = 2.dp)
                        .padding(bottom = 16.dp)
                        .background(if (isCompleted) NeonGreen else BorderNeon)
                )
            }
        }
    }
}

@Preview(
    name = "CyberProgressSteps",
    showBackground = true,
    backgroundColor = 0xFF0F172A,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun CyberProgressStepsPreview() {
    PeerXTheme {
        CyberProgressSteps(
            currentStep = 2,
            labels = listOf("Start", "Ongoing", "Finish"),
            modifier = Modifier.padding(16.dp)
        )
    }
}





