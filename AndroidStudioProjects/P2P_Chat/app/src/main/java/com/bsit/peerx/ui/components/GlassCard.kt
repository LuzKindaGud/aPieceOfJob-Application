package com.bsit.peerx.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bsit.peerx.ui.theme.Spacing
import com.bsit.peerx.ui.theme.glassSurface

import android.content.res.Configuration
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import com.bsit.peerx.ui.theme.PeerXTheme
import com.bsit.peerx.ui.theme.TextPrimary

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .glassSurface()
            .padding(Spacing.xl),
        content = content
    )
}

@Preview(
    name = "GlassCard",
    showBackground = true,
    backgroundColor = 0xFF0F172A,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun GlassCardPreview() {
    PeerXTheme {
        GlassCard {
            Text(text = "Glass Card Content", color = TextPrimary)
        }
    }
}





