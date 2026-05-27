package com.bsit.peerx.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bsit.peerx.ui.components.AdminLogoIcon
import com.bsit.peerx.ui.components.CyberBackground
import com.bsit.peerx.ui.components.CyberBanner
import com.bsit.peerx.ui.components.CyberBannerType
import com.bsit.peerx.ui.components.CyberButton
import com.bsit.peerx.ui.components.CyberProgressSteps
import com.bsit.peerx.ui.components.CyberTextField
import com.bsit.peerx.ui.components.GlassCard
import com.bsit.peerx.ui.components.OtpInputRow
import com.bsit.peerx.ui.theme.PeerXTheme
import com.bsit.peerx.ui.theme.Spacing
import com.bsit.peerx.ui.theme.TextMuted
import com.bsit.peerx.ui.theme.TextPrimary

@Composable
fun ForgotPasswordScreen(
    currentStep: Int,
    email: String,
    otp: List<String>,
    newPassword: String,
    onEmailChange: (String) -> Unit,
    onOtpChange: (Int, String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBackToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    message: String? = null,
    isSuccess: Boolean = false
) {
    CyberBackground {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            GlassCard(
                modifier = modifier
                    .widthIn(max = 420.dp)
                    .padding(horizontal = Spacing.lg)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AdminLogoIcon()
                    Text(
                        text = "Reset Access".uppercase(),
                        style = MaterialTheme.typography.displayLarge,
                        color = TextPrimary
                    )
                    Text(
                        text = "Follow the steps to secure your account.",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextMuted
                    )
                    CyberProgressSteps(
                        currentStep = currentStep,
                        labels = listOf("Email", "Verify", "Reset")
                    )
                    AnimatedVisibility(
                        visible = !message.isNullOrBlank(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        CyberBanner(
                            message = message.orEmpty(),
                            type = if (isSuccess) CyberBannerType.Success else CyberBannerType.Error
                        )
                    }
                    AnimatedVisibility(visible = currentStep == 1, enter = fadeIn(), exit = fadeOut()) {
                        CyberTextField(
                            value = email,
                            onValueChange = onEmailChange,
                            placeholder = "Email Address",
                            leadingIcon = Icons.Filled.Email
                        )
                    }
                    AnimatedVisibility(visible = currentStep == 2, enter = fadeIn(), exit = fadeOut()) {
                        OtpInputRow(otp = otp, onOtpChange = onOtpChange)
                    }
                    AnimatedVisibility(visible = currentStep == 3, enter = fadeIn(), exit = fadeOut()) {
                        CyberTextField(
                            value = newPassword,
                            onValueChange = onPasswordChange,
                            placeholder = "New Password",
                            leadingIcon = Icons.Filled.Lock,
                            visualTransformation = PasswordVisualTransformation()
                        )
                    }
                    CyberButton(
                        text = if (isLoading) "Submitting" else "Submit",
                        onClick = onSubmit,
                        isLoading = isLoading
                    )
                    TextButton(onClick = onBackToLogin) {
                        Text("Back to Login", color = TextPrimary)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun ForgotPasswordPreview() {
    PeerXTheme {
        ForgotPasswordScreen(
            currentStep = 2,
            email = "nova@cyber.io",
            otp = listOf("1", "2", "", ""),
            newPassword = "",
            onEmailChange = {},
            onOtpChange = { _, _ -> },
            onPasswordChange = {},
            onSubmit = {},
            onBackToLogin = {},
            message = "Code sent to your email.",
            isSuccess = true
        )
    }
}





