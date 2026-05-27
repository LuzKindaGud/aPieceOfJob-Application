package com.bsit.peerx.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.bsit.peerx.ui.theme.BgSecondary
import com.bsit.peerx.ui.theme.BorderNeon
import com.bsit.peerx.ui.theme.NeonDanger
import com.bsit.peerx.ui.theme.NeonGreen
import com.bsit.peerx.ui.theme.Spacing

import android.content.res.Configuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.tooling.preview.Preview
import com.bsit.peerx.ui.theme.NeonPrimary
import com.bsit.peerx.ui.theme.PeerXTheme

@Composable
fun StatCard(
    label: String,
    value: String,
    change: String,
    isUp: Boolean,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(BgSecondary, RoundedCornerShape(16.dp))
            .border(1.dp, BorderNeon, RoundedCornerShape(16.dp))
            .drawBehind {
                drawRect(
                    brush = Brush.horizontalGradient(
                        listOf(accentColor, accentColor.copy(alpha = 0.3f))
                    ),
                    size = size.copy(height = 3.dp.toPx())
                )
            }
            .padding(Spacing.lg)
    ) {
        Column {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(accentColor.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(24.dp))
                }
                Surface(
                    color = if (isUp) NeonGreen.copy(alpha = 0.1f) else NeonDanger.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = change,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = if (isUp) NeonGreen else NeonDanger,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
            Spacer(Modifier.height(Spacing.md))
            Text(value, style = MaterialTheme.typography.displayMedium)
            Text(label, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Preview(
    name = "StatCard",
    showBackground = true,
    backgroundColor = 0xFF0F172A,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun StatCardPreview() {
    PeerXTheme {
        StatCard(
            label = "Total Users",
            value = "1,234",
            change = "+12%",
            isUp = true,
            icon = Icons.Default.Person,
            accentColor = NeonPrimary,
            modifier = Modifier.padding(16.dp)
        )
    }
}





