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
import androidx.compose.material.icons.filled.Person
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
import com.bsit.peerx.ui.components.CyberTextField
import com.bsit.peerx.ui.components.GlassCard
import com.bsit.peerx.ui.theme.PeerXTheme
import com.bsit.peerx.ui.theme.Spacing
import com.bsit.peerx.ui.theme.TextMuted
import com.bsit.peerx.ui.theme.TextPrimary

@Composable
fun RegisterScreen(
    name: String,
    email: String,
    password: String,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
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
                        text = "Create Account".uppercase(),
                        style = MaterialTheme.typography.displayLarge,
                        color = TextPrimary
                    )
                    Text(
                        text = "Start secure peer-to-peer chats in seconds.",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextMuted
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
                    CyberTextField(
                        value = name,
                        onValueChange = onNameChange,
                        placeholder = "Display Name",
                        leadingIcon = Icons.Filled.Person
                    )
                    CyberTextField(
                        value = email,
                        onValueChange = onEmailChange,
                        placeholder = "Email Address",
                        leadingIcon = Icons.Filled.Email
                    )
                    CyberTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        placeholder = "Password",
                        leadingIcon = Icons.Filled.Lock,
                        visualTransformation = PasswordVisualTransformation()
                    )
                    CyberButton(
                        text = if (isLoading) "Creating" else "Create Account",
                        onClick = onRegisterClick,
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
private fun RegisterScreenPreview() {
    PeerXTheme {
        RegisterScreen(
            name = "Nova",
            email = "nova@cyber.io",
            password = "",
            onNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onRegisterClick = {},
            onBackToLogin = {},
            message = "Account created!",
            isSuccess = true
        )
    }
}





