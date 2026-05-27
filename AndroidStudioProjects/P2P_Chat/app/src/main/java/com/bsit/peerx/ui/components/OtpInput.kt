package com.bsit.peerx.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bsit.peerx.ui.theme.BorderNeon
import com.bsit.peerx.ui.theme.GlassSurface
import com.bsit.peerx.ui.theme.NeonSecondary
import com.bsit.peerx.ui.theme.OrbitronFamily
import com.bsit.peerx.ui.theme.TextPrimary

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.tooling.preview.Preview
import com.bsit.peerx.ui.theme.PeerXTheme

@Composable
fun OtpInputRow(
    otp: List<String>,
    onOtpChange: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        otp.forEachIndexed { index, digit ->
            BasicTextField(
                value = digit,
                onValueChange = { value ->
                    if (value.length <= 1) {
                        onOtpChange(index, value)
                    }
                },
                singleLine = true,
                textStyle = TextStyle(
                    fontFamily = OrbitronFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .size(width = 48.dp, height = 56.dp)
                    .background(GlassSurface, RoundedCornerShape(12.dp))
                    .border(
                        width = 2.dp,
                        color = if (digit.isNotEmpty()) NeonSecondary else BorderNeon,
                        shape = RoundedCornerShape(12.dp)
                    ),
                decorationBox = { inner ->
                    Box(modifier = Modifier.size(48.dp, 56.dp), contentAlignment = Alignment.Center) {
                        inner()
                    }
                }
            )
        }
    }
}

@Preview(
    name = "OtpInputRow",
    showBackground = true,
    backgroundColor = 0xFF0F172A,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun OtpInputRowPreview() {
    PeerXTheme {
        OtpInputRow(
            otp = listOf("1", "2", "", ""),
            onOtpChange = { _, _ -> },
            modifier = Modifier.padding(16.dp)
        )
    }
}





