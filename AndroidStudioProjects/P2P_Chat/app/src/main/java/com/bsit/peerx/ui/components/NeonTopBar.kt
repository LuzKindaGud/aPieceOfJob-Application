package com.bsit.peerx.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bsit.peerx.R
import com.bsit.peerx.ui.theme.BorderNeon
import com.bsit.peerx.ui.theme.GlassBorder
import com.bsit.peerx.ui.theme.GlassSurface
import com.bsit.peerx.ui.theme.NeonPrimary
import com.bsit.peerx.ui.theme.TextPrimary
import com.bsit.peerx.ui.theme.neonGlow

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import com.bsit.peerx.ui.theme.PeerXTheme

@Composable
fun NeonTopBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(GlassSurface, RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
            .neonGlow(NeonPrimary, 16.dp)
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                onBackClick?.let {
                    IconButton(onClick = it) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(GlassSurface, RoundedCornerShape(10.dp))
                                .border(1.dp, BorderNeon, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.arrow_back),
                                contentDescription = "Back",
                                tint = TextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = NeonPrimary
                )
            }
        }
    }
}

@Preview(
    name = "NeonTopBar",
    showBackground = true,
    backgroundColor = 0xFF0F172A,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun NeonTopBarPreview() {
    PeerXTheme {
        NeonTopBar(title = "Dashboard", onBackClick = {})
    }
}





