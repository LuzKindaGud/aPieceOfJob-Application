package com.bsit.peerx.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bsit.peerx.ui.theme.NeonDanger
import com.bsit.peerx.ui.theme.NeonGreen
import com.bsit.peerx.ui.theme.NeonInfo
import com.bsit.peerx.ui.theme.NeonWarning
import com.bsit.peerx.ui.theme.PeerXTheme
import com.bsit.peerx.ui.theme.TextMuted

@Composable
fun StatusBadge(status: String) {
    val (background, foreground) = when (status.lowercase()) {
        "completed" -> NeonGreen.copy(alpha = 0.12f) to NeonGreen
        "pending" -> NeonWarning.copy(alpha = 0.12f) to NeonWarning
        "processing" -> NeonInfo.copy(alpha = 0.12f) to NeonInfo
        "cancelled" -> NeonDanger.copy(alpha = 0.12f) to NeonDanger
        "low stock" -> NeonWarning.copy(alpha = 0.12f) to NeonWarning
        "online" -> NeonGreen.copy(alpha = 0.12f) to NeonGreen
        "critical" -> NeonDanger.copy(alpha = 0.12f) to NeonDanger
        "scanning" -> NeonInfo.copy(alpha = 0.14f) to NeonInfo
        "wifi off" -> NeonDanger.copy(alpha = 0.14f) to NeonDanger
        "warning" -> NeonWarning.copy(alpha = 0.12f) to NeonWarning
        else -> TextMuted.copy(alpha = 0.2f) to TextMuted
    }

    Surface(color = background, shape = RoundedCornerShape(20.dp)) {
        Text(
            text = status.uppercase(),
            color = foreground,
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Preview( name = "StatusBadge", showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun StatusBarPreview() {
    PeerXTheme {
        Column (
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            StatusBadge("Online")
            StatusBadge("Scanning")
            StatusBadge("Wifi Off")
            StatusBadge("Warning")
            StatusBadge("Offline")
        }
    }
}




