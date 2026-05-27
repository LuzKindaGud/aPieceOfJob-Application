package com.bsit.peerx.lock

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.bsit.peerx.ui.components.AdminLogoIcon
import com.bsit.peerx.ui.components.CyberBackground
import com.bsit.peerx.ui.components.CyberButton
import com.bsit.peerx.ui.theme.PeerXTheme
import com.bsit.peerx.ui.theme.TextPrimary

@Composable
fun LockScreen(onUnlocked: () -> Unit) {
    val context = LocalContext.current
    // BiometricPrompt requires FragmentActivity
    val activity = context as? FragmentActivity

    BackHandler {
        // Intercept back press to do nothing, preventing user from exiting lock screen
    }

    LaunchedEffect(Unit) {
        activity?.let {
            showBiometricPrompt(it, onUnlocked)
        }
    }

    CyberBackground {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AdminLogoIcon(modifier = Modifier.size(120.dp))
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "PEERX LOCKED",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Biometric authentication required",
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(48.dp))
            CyberButton(
                text = "Unlock",
                onClick = {
                    activity?.let { showBiometricPrompt(it, onUnlocked) }
                },
                modifier = Modifier.width(200.dp)
            )
        }
    }
}

private fun showBiometricPrompt(
    activity: FragmentActivity,
    onUnlocked: () -> Unit
) {
    val executor = ContextCompat.getMainExecutor(activity)
    val prompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            onUnlocked()
        }
    })

    val infoBuilder = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Unlock PeerX")
        .setSubtitle("Authenticate to access your secure chats")

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        infoBuilder.setAllowedAuthenticators(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )
    } else {
        @Suppress("DEPRECATION")
        infoBuilder.setDeviceCredentialAllowed(true)
    }
    val info = infoBuilder.build()

    prompt.authenticate(info)
}


@Preview(showBackground = true)
@Composable
fun UnLockScreenPreview() {
    PeerXTheme {
        LockScreen(onUnlocked = {})
    }
}




