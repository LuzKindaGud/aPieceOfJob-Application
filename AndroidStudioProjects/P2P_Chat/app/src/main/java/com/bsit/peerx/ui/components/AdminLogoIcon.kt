package com.bsit.peerx.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bsit.peerx.R
import com.bsit.peerx.ui.theme.BorderNeon
import com.bsit.peerx.ui.theme.GradientPrimary
import com.bsit.peerx.ui.theme.TextPrimary
import com.bsit.peerx.ui.theme.neonBorder

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import com.bsit.peerx.ui.theme.PeerXTheme

@Composable
fun AdminLogoIcon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(70.dp)
            .background(GradientPrimary, CircleShape)
            .neonBorder(CircleShape, BorderNeon),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = null,
            tint = TextPrimary
        )
    }
}

@Preview(
    name = "AdminLogoIcon",
    showBackground = true,
    backgroundColor = 0xFF0F172A,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun AdminLogoIconPreview() {
    PeerXTheme {
        AdminLogoIcon()
    }
}





