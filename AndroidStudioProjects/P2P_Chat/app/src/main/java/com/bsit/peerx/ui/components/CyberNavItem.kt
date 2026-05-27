package com.bsit.peerx.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.bsit.peerx.ui.theme.NeonCyan
import com.bsit.peerx.ui.theme.NeonPink
import com.bsit.peerx.ui.theme.Spacing
import com.bsit.peerx.ui.theme.TextSecondary

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.tooling.preview.Preview
import com.bsit.peerx.ui.theme.PeerXTheme

@Composable
fun CyberNavItem(
    label: String,
    icon: ImageVector,
    isActive: Boolean,
    badge: Int? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isActive) NeonCyan.copy(alpha = 0.15f) else Color.Transparent
    val textColor = if (isActive) NeonCyan else TextSecondary
    val borderColor = if (isActive) NeonCyan else Color.Transparent

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(8.dp))
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = 12.dp)
    ) {
        Icon(icon, contentDescription = null, tint = textColor, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(Spacing.md))
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = textColor,
            modifier = Modifier.weight(1f)
        )
        badge?.let {
            Surface(color = NeonPink, shape = CircleShape) {
                Text(
                    text = "$it",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White
                )
            }
        }
    }
}

@Preview(
    name = "CyberNavItem",
    showBackground = true,
    backgroundColor = 0xFF0F172A,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun CyberNavItemPreview() {
    PeerXTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            CyberNavItem(
                label = "Home",
                icon = Icons.Default.Home,
                isActive = true,
                onClick = {}
            )
            Spacer(Modifier.height(8.dp))
            CyberNavItem(
                label = "Settings",
                icon = Icons.Default.Settings,
                isActive = false,
                badge = 5,
                onClick = {}
            )
        }
    }
}





