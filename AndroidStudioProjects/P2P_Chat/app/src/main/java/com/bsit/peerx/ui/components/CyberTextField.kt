package com.bsit.peerx.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import com.bsit.peerx.ui.theme.Spacing
import com.bsit.peerx.ui.theme.BorderNeon
import com.bsit.peerx.ui.theme.GlassSurface
import com.bsit.peerx.ui.theme.NeonSecondary
import com.bsit.peerx.ui.theme.PeerXTheme
import com.bsit.peerx.ui.theme.TextMuted
import com.bsit.peerx.ui.theme.TextPrimary
import com.bsit.peerx.ui.theme.neonGlow

@Composable
fun CyberTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    var isFocused by remember { mutableStateOf(false) }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = keyboardOptions,
        singleLine = singleLine,
        visualTransformation = visualTransformation,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary),
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(
                color = if (isFocused) NeonSecondary.copy(alpha = 0.12f) else GlassSurface,
                shape = MaterialTheme.shapes.medium
            )
            .border(
                width = 1.dp,
                color = if (isFocused) NeonSecondary else BorderNeon,
                shape = MaterialTheme.shapes.medium
            )
            .then(if (isFocused) Modifier.neonGlow(NeonSecondary, 10.dp) else Modifier)
            .onFocusChanged { isFocused = it.isFocused }
            .padding(horizontal = Spacing.md),
        decorationBox = { innerTextField ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(leadingIcon, contentDescription = null, tint = TextMuted)
                Spacer(modifier = Modifier.width(Spacing.sm))
                Box {
                    if (value.isBlank()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                    }
                    innerTextField()
                }
            }
        }
    )
}

@Preview( name = "CyberTextField", showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun CyberTextFieldPreviewEmpty() {
    PeerXTheme {
        CyberTextField(
            value = "",
            onValueChange = {},
            placeholder = "Enter your username",
            leadingIcon = Icons.Filled.Person,
            modifier = Modifier.padding(16.dp)
        )
    }
}


@Preview(name = "CyberTextField - Filled", showBackground = true, backgroundColor = 0xFF0B1020)
@Composable
private fun CyberTextFieldPreviewFilled() {
    PeerXTheme {
        CyberTextField(
            value = "alice_01",
            onValueChange = {},
            placeholder = "Enter username",
            leadingIcon = Icons.Filled.Person,
            modifier = Modifier.padding(16.dp)
        )
    }
}




