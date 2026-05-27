package com.bsit.peerx.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bsit.peerx.ui.theme.NeonDanger
import com.bsit.peerx.ui.theme.NeonGreen
import com.bsit.peerx.ui.theme.NeonInfo
import com.bsit.peerx.ui.theme.Spacing

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.tooling.preview.Preview
import com.bsit.peerx.ui.theme.PeerXTheme

enum class CyberBannerType {
    Error,
    Success,
    Info
}

@Composable
fun CyberBanner(
    message: String,
    type: CyberBannerType,
    modifier: Modifier = Modifier
) {
    val (bg, border, textColor) = when (type) {
        CyberBannerType.Error -> Triple(
            NeonDanger.copy(alpha = 0.12f),
            NeonDanger.copy(alpha = 0.55f),
            NeonDanger
        )
        CyberBannerType.Success -> Triple(
            NeonGreen.copy(alpha = 0.12f),
            NeonGreen.copy(alpha = 0.55f),
            NeonGreen
        )
        CyberBannerType.Info -> Triple(
            NeonInfo.copy(alpha = 0.12f),
            NeonInfo.copy(alpha = 0.55f),
            NeonInfo
        )
    }

    Text(
        text = message,
        style = MaterialTheme.typography.labelLarge,
        color = textColor,
        modifier = modifier
            .background(bg, MaterialTheme.shapes.medium)
            .border(1.dp, border, MaterialTheme.shapes.medium)
            .padding(horizontal = Spacing.md, vertical = Spacing.sm)
    )
}

@Preview(
    name = "CyberBanner",
    showBackground = true,
    backgroundColor = 0xFF0F172A,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun CyberBannerPreview() {
    PeerXTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            CyberBanner(message = "Success message", type = CyberBannerType.Success)
            Spacer(modifier = Modifier.height(8.dp))
            CyberBanner(message = "Error message", type = CyberBannerType.Error)
            Spacer(modifier = Modifier.height(8.dp))
            CyberBanner(message = "Info message", type = CyberBannerType.Info)
        }
    }
}





