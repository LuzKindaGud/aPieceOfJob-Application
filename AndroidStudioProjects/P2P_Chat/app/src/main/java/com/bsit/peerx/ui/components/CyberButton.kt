package com.bsit.peerx.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bsit.peerx.ui.theme.BorderNeon
import com.bsit.peerx.ui.theme.GlassSurface
import com.bsit.peerx.ui.theme.GradientPrimary
import com.bsit.peerx.ui.theme.NeonPrimary
import com.bsit.peerx.ui.theme.PeerXTheme
import com.bsit.peerx.ui.theme.TextPrimary
import com.bsit.peerx.ui.theme.neonGlow

@Composable
fun CyberButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    val disabledGradient = Brush.linearGradient(
        colors = listOf(NeonPrimary.copy(alpha = 0.45f), NeonPrimary.copy(alpha = 0.35f))
    )
    val glowModifier = if (enabled) Modifier.neonGlow(NeonPrimary, 12.dp) else Modifier
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(
                brush = if (enabled) GradientPrimary else disabledGradient,
                shape = RoundedCornerShape(10.dp)
            )
            .then(glowModifier)
            .clickable(enabled = enabled && !isLoading, onClick = onClick)) {
        if (isLoading) {
            CircularProgressIndicator(
                progress = { 0.7f },
                modifier = Modifier.size(20.dp),
                color = TextPrimary,
                trackColor = GlassSurface,
                strokeWidth = 2.dp,
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                icon?.let {
                    androidx.compose.material3.Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
fun CyberOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    strokeColor: Color = BorderNeon
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(44.dp)
            .background(Color.Transparent, RoundedCornerShape(10.dp))
            .border(1.dp, strokeColor, RoundedCornerShape(10.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 24.dp)
    ) {
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = text.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary
            )
        }
    }
}

@Preview( name = "CyberButton", showBackground = true, backgroundColor = 0xFF0B1020)
@Composable
private fun CyberButtonPreview() {
    PeerXTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            CyberButton(
                text = "Connect",
                onClick = {}
            )
            CyberButton(
                text = "Connecting",
                onClick = {},
                isLoading = true
            )
            CyberButton(
                text = "Disabled",
                onClick = {},
                enabled = false
            )
            CyberOutlineButton(
                text = "Scan Again",
                onClick = {}
            )
        }
    }
}





