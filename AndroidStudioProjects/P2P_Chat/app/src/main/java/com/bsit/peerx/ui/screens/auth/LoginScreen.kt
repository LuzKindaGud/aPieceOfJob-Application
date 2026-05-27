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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bsit.peerx.ui.components.AdminLogoIcon
import com.bsit.peerx.ui.components.CyberBackground
import com.bsit.peerx.ui.components.CyberBanner
import com.bsit.peerx.ui.components.CyberBannerType
import com.bsit.peerx.ui.components.CyberButton
import com.bsit.peerx.ui.components.CyberRing
import com.bsit.peerx.ui.components.CyberTextField
import com.bsit.peerx.ui.components.GlassCard
import com.bsit.peerx.ui.theme.PeerXTheme
import com.bsit.peerx.ui.theme.Spacing
import com.bsit.peerx.ui.theme.TextMuted
import com.bsit.peerx.ui.theme.TextPrimary

@Composable
fun LoginScreen(
    username: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    errorMessage: String? = null
) {
    CyberBackground {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CyberRing(
                modifier = Modifier
                    .align(Alignment.Center)
                    .graphicsLayer(alpha = 0.2f)
            )
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
                        text = "Secure Access".uppercase(),
                        style = MaterialTheme.typography.displayLarge,
                        color = TextPrimary
                    )
                    Text(
                        text = "Enter your credentials to continue.",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextMuted
                    )
                    AnimatedVisibility(
                        visible = !errorMessage.isNullOrBlank(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        CyberBanner(
                            message = errorMessage.orEmpty(),
                            type = CyberBannerType.Error
                        )
                    }
                    CyberTextField(
                        value = username,
                        onValueChange = onUsernameChange,
                        placeholder = "Username",
                        leadingIcon = Icons.Filled.Person
                    )
                    CyberTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        placeholder = "Password",
                        leadingIcon = Icons.Filled.Lock,
                        visualTransformation = PasswordVisualTransformation()
                    )
                    CyberButton(
                        text = if (isLoading) "Signing In" else "Sign In",
                        onClick = onLoginClick,
                        isLoading = isLoading
                    )
                    TextButton(onClick = onForgotPasswordClick) {
                        Text("Forgot password?", color = TextPrimary)
                    }
                    TextButton(onClick = onRegisterClick) {
                        Text("Create an account", color = TextPrimary)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun LoginScreenPreview() {
    PeerXTheme {
        LoginScreen(
            username = "",
            password = "",
            onUsernameChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onForgotPasswordClick = {},
            onRegisterClick = {},
            errorMessage = "Invalid credentials. Try again."
        )
    }
}





