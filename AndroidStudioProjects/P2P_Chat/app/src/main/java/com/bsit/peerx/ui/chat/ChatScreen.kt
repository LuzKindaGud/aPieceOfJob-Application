package com.bsit.peerx.ui.chat

import android.net.Uri
import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bsit.peerx.R
import com.bsit.peerx.data.model.ChatMessageModel
import com.bsit.peerx.data.model.MessageType
import com.bsit.peerx.data.model.SenderType
import com.bsit.peerx.ui.components.CyberBackground
import com.bsit.peerx.ui.theme.BgPrimary
import com.bsit.peerx.ui.theme.BgSecondary
import com.bsit.peerx.ui.theme.BorderNeon
import com.bsit.peerx.ui.theme.GlassBorder
import com.bsit.peerx.ui.theme.GlassSurface
import com.bsit.peerx.ui.theme.NeonCyan
import com.bsit.peerx.ui.theme.NeonDanger
import com.bsit.peerx.ui.theme.NeonGreen
import com.bsit.peerx.ui.theme.NeonPink
import com.bsit.peerx.ui.theme.NeonPrimary
import com.bsit.peerx.ui.theme.NeonSecondary
import com.bsit.peerx.ui.theme.NeonWarning
import com.bsit.peerx.ui.theme.PeerXTheme
import com.bsit.peerx.ui.theme.Spacing
import com.bsit.peerx.ui.theme.TextMuted
import com.bsit.peerx.ui.theme.TextPrimary
import com.bsit.peerx.ui.theme.TextSecondary
import com.bsit.peerx.ui.theme.glassSurface
import com.bsit.peerx.ui.theme.neonGlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatRoute(
    viewModel: ChatViewModel,
    peerAddress: String,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.sendFile(it) }
    }

    if (state.peerAddress != peerAddress) {
        LaunchedEffect(peerAddress) {
            viewModel.initPeer(peerAddress)
        }
    }

    ChatScreen(
        uiState = state,
        onBack = onBack,
        onInputChange = viewModel::onInputChange,
        onSendClick = viewModel::sendMessage,
        onFileClick = { filePickerLauncher.launch("*/*") },
        onStartCall = viewModel::startCall,
        onHangup = viewModel::stopCall
    )
}

@Composable
fun ChatScreen(
    uiState: ChatUiState,
    onBack: () -> Unit,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onFileClick: () -> Unit,
    onStartCall: () -> Unit,
    onHangup: () -> Unit
) {
    CyberBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                ChatTopBar(
                    title = uiState.peerName ?: uiState.peerAddress.ifBlank { "THE UNDERGROUND" },
                    subtitle = if (uiState.isPeerTyping) "AGENT IS TYPING..." else "SECURE CHANNEL ACTIVE",
                    isTyping = uiState.isPeerTyping,
                    isInCall = uiState.isInCall,
                    onMenuClick = onBack,
                    onStartCall = onStartCall,
                    onHangup = onHangup
                )
            },

            bottomBar = {
                ChatComposer(
                    text = uiState.inputText,
                    onTextChange = onInputChange,
                    onSendClick = onSendClick,
                    onFileClick = onFileClick,
                    isSending = uiState.isSending
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = Spacing.md, vertical = Spacing.md)
            ) {
                AnimatedVisibility(
                    visible = !uiState.errorMessage.isNullOrBlank(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(
                        color = NeonDanger.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(999.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = uiState.errorMessage.orEmpty(),
                            color = NeonDanger,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.sm))

                if (uiState.messages.isEmpty()) {
                    EmptyConversationState()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        reverseLayout = true,
                        contentPadding = PaddingValues(bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        itemsIndexed(uiState.messages.reversed()) { index, message ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
                            ) {
                                MessageBubble(
                                    message = message,
                                    itemIndex = index,
                                    peerName = uiState.peerName,
                                    ownName = uiState.ownName,
                                    progress = message.fileTransferId?.let { uiState.fileProgress[it] }
                                )
                            }
                        }
                        item {
                            ChannelStatusChip()
                            Spacer(modifier = Modifier.height(Spacing.md))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatTopBar(
    title: String,
    subtitle: String,
    isTyping: Boolean = false,
    isInCall: Boolean = false,
    onMenuClick: () -> Unit,
    onStartCall: () -> Unit,
    onHangup: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgPrimary.copy(alpha = 0.84f))
            .border(1.dp, GlassBorder, RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
            .glassSurface()
            .padding(horizontal = Spacing.md, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Back",
                        tint = NeonPrimary
                    )
                }
                Column {
                    Text(
                        text = title.uppercase(),
                        style = MaterialTheme.typography.headlineLarge,
                        color = TextPrimary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isTyping) NeonPrimary else NeonGreen)
                                .then(if (isTyping) Modifier.neonGlow(NeonPrimary, 4.dp) else Modifier)
                        )
                        Spacer(modifier = Modifier.width(Spacing.xs))
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = (if (isTyping) NeonPrimary else NeonGreen).copy(alpha = 0.9f)
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(GlassSurface, CircleShape)
                        .border(1.dp, NeonCyan.copy(alpha = 0.65f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Secure",
                        tint = NeonCyan,
                        modifier = Modifier.size(14.dp)
                    )
                }

                // Call controls
                if (isInCall) {
                    IconButton(onClick = onHangup) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Hangup",
                            tint = NeonDanger,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else {
                    IconButton(onClick = onStartCall) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Call",
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(NeonPrimary, NeonPink)), CircleShape)
                        .border(1.dp, BorderNeon, CircleShape)
                        .neonGlow(NeonPrimary, 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_launcher_foreground),
                        contentDescription = "Profile",
                        tint = TextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChannelStatusChip() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = BgSecondary.copy(alpha = 0.42f),
            shape = RoundedCornerShape(999.dp),
            modifier = Modifier.border(1.dp, GlassBorder, RoundedCornerShape(999.dp))
        ) {
            Text(
                text = "SECURE CHANNEL RE-KEYED",
                color = TextMuted,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = Spacing.lg, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun EmptyConversationState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "SECURE CHANNEL STANDBY",
                style = MaterialTheme.typography.displaySmall,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = "Send the first encrypted packet to begin.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun MessageBubble(
    message: ChatMessageModel,
    itemIndex: Int,
    peerName: String?,
    ownName: String,
    progress: Float?
) {
    val isSent = message.senderType == SenderType.SENT

    if (isSent) {
        OutgoingMessageBubble(message = message, ownName = ownName, progress = progress)
    } else {
        IncomingMessageBubble(
            message = message,
            senderLabel = peerName ?: incomingLabel(itemIndex),
            accent = incomingAccent(itemIndex),
            progress = progress
        )
    }
}

@Composable
private fun IncomingMessageBubble(
    message: ChatMessageModel,
    senderLabel: String,
    accent: Color,
    progress: Float?
) {
    val bubbleShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 6.dp)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        Box(modifier = Modifier.size(40.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BgSecondary.copy(alpha = 0.9f), RoundedCornerShape(12.dp))
                    .border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(Spacing.sm))

        Column(modifier = Modifier.weight(1f, fill = false).widthIn(max = 280.dp)) {
            Text(
                text = senderLabel,
                style = MaterialTheme.typography.labelSmall,
                color = accent,
                modifier = Modifier.padding(start = Spacing.xs, bottom = 4.dp)
            )
            Box(
                modifier = Modifier
                    .clip(bubbleShape)
                    .background(BgSecondary.copy(alpha = 0.82f), bubbleShape)
                    .border(1.dp, accent.copy(alpha = 0.22f), bubbleShape)
                    .drawBehind {
                        // Left accent border strip
                        drawRect(
                            color = accent.copy(alpha = 0.65f),
                            topLeft = Offset(0f, 6.dp.toPx()),
                            size = androidx.compose.ui.geometry.Size(
                                2.5.dp.toPx(),
                                size.height - 12.dp.toPx()
                            )
                        )
                    }
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm)
            ) {
                Column {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                    if (message.messageType == MessageType.FILE && progress != null && progress < 1f) {
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth(),
                            color = accent,
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )
                    }
                }
            }
            Text(
                text = timeLabel(message.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                modifier = Modifier.padding(start = Spacing.xs, top = 4.dp)
            )
        }
    }
}

@Composable
private fun OutgoingMessageBubble(message: ChatMessageModel, ownName: String, progress: Float?) {
    val bubbleShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 6.dp, bottomStart = 16.dp)
    val outgoingBrush = Brush.linearGradient(
        colors = listOf(Color(0xFF4F46E5), Color(0xFF7C3AED), Color(0xFF312E81))
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = ownName.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = NeonCyan,
            modifier = Modifier.padding(end = Spacing.xs, bottom = 4.dp)
        )

        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(bubbleShape)
                .background(outgoingBrush, bubbleShape)
                .border(1.dp, NeonPrimary.copy(alpha = 0.35f), bubbleShape)
                .padding(horizontal = Spacing.md, vertical = Spacing.sm)
        ) {
            Column {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
                if (message.messageType == MessageType.FILE && progress != null && progress < 1f) {
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth(),
                        color = NeonCyan,
                        trackColor = Color.White.copy(alpha = 0.2f)
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(top = 4.dp, end = Spacing.xs)
        ) {
            Text(
                text = timeLabel(message.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
            
            val (icon, color) = when (message.status) {
                com.bsit.peerx.data.model.MessageStatus.SENDING -> Icons.Default.Check to TextMuted
                com.bsit.peerx.data.model.MessageStatus.SENT -> Icons.Default.Check to TextMuted
                com.bsit.peerx.data.model.MessageStatus.DELIVERED,
                com.bsit.peerx.data.model.MessageStatus.READ -> Icons.Default.Check to NeonCyan
                com.bsit.peerx.data.model.MessageStatus.FAILED -> Icons.Default.Lock to NeonDanger
                else -> Icons.Default.Check to TextMuted
            }

            Icon(
                imageVector = icon,
                contentDescription = message.status.name,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun ChatComposer(
    text: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onFileClick: () -> Unit,
    isSending: Boolean
) {
    val composerShape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgPrimary.copy(alpha = 0.82f), composerShape)
            .border(1.dp, GlassBorder, composerShape)
            .glassSurface()
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        IconButton(onClick = onFileClick) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = TextMuted
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(18.dp))
                .background(BgSecondary.copy(alpha = 0.72f), RoundedCornerShape(18.dp))
                .border(1.dp, GlassBorder, RoundedCornerShape(18.dp))
                .padding(horizontal = 8.dp)
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = text,
                onValueChange = onTextChange,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary),
                shape = RoundedCornerShape(18.dp),
                placeholder = {
                    Text(
                        text = "Type encrypted message...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "Emoji",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSendClick() }),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = NeonCyan
                )
            )
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.linearGradient(listOf(NeonPrimary, NeonSecondary)),
                    RoundedCornerShape(14.dp)
                )
                .neonGlow(NeonPrimary, 12.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onSendClick,
                enabled = text.isNotBlank() && !isSending
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        color = TextPrimary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = TextPrimary
                    )
                }
            }
        }
    }
}

private fun incomingLabel(index: Int): String {
    return when (index % 3) {
        0 -> "ZERO_DAY"
        1 -> "CIPHER_GHOST"
        else -> "NODE_LINK"
    }
}

private fun incomingAccent(index: Int): Color {
    return when (index % 3) {
        0 -> NeonWarning
        1 -> NeonCyan
        else -> NeonPink
    }
}

private fun timeLabel(timestamp: Long): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
}

private const val previewPeerAddress = "Device_192.168.49.1"

private val previewMessages = listOf(
    ChatMessageModel(
        id = 1L,
        text = "I'm initiating the bypass now. Keep the comms clean.",
        senderType = SenderType.RECEIVED,
        timestamp = 1713500000000L,
        peerAddress = previewPeerAddress
    ),
    ChatMessageModel(
        id = 2L,
        text = "Copy that. Relaying secondary encryption to all nodes. Hold steady.",
        senderType = SenderType.SENT,
        timestamp = 1713500060000L,
        peerAddress = previewPeerAddress
    ),
    ChatMessageModel(
        id = 3L,
        text = "Visual on the payload. It's bigger than we thought. Requesting backup at Grid-Thata.",
        senderType = SenderType.RECEIVED,
        timestamp = 1713500120000L,
        peerAddress = previewPeerAddress
    ),
    ChatMessageModel(
        id = 4L,
        text = "Acknowledged. Deploying phantom-drones to Grid-Thata. Stay dark.",
        senderType = SenderType.SENT,
        timestamp = 1713500180000L,
        peerAddress = previewPeerAddress
    )
)

@Preview(name = "Chat - Empty", showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun ChatScreenPreviewEmpty() {
    PeerXTheme {
        ChatScreen(
            uiState = ChatUiState(peerAddress = previewPeerAddress),
            onBack = {},
            onInputChange = {},
            onSendClick = {},
            onFileClick = {},
            onStartCall = {},
            onHangup = {}
        )
    }
}

@Preview(name = "Chat - Has Messages", showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun ChatScreenPreviewHasMessages() {
    PeerXTheme {
        ChatScreen(
            uiState = ChatUiState(
                peerAddress = previewPeerAddress,
                messages = previewMessages
            ),
            onBack = {},
            onInputChange = {},
            onSendClick = {},
            onFileClick = {},
            onStartCall = {},
            onHangup = {}
        )
    }
}

@Preview(name = "Chat - Sending", showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun ChatScreenPreviewSending() {
    PeerXTheme {
        ChatScreen(
            uiState = ChatUiState(
                peerAddress = previewPeerAddress,
                messages = previewMessages,
                inputText = "Dang gui tin nhan...",
                isSending = true
            ),
            onBack = {},
            onInputChange = {},
            onSendClick = {},
            onFileClick = {},
            onStartCall = {},
            onHangup = {}
        )
    }
}

@Preview(name = "Chat - Error", showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun ChatScreenPreviewError() {
    PeerXTheme {
        ChatScreen(
            uiState = ChatUiState(
                peerAddress = previewPeerAddress,
                messages = previewMessages,
                errorMessage = "Could not send message."
            ),
            onBack = {},
            onInputChange = {},
            onSendClick = {},
            onFileClick = {},
            onStartCall = {},
            onHangup = {}
        )
    }
}

@Preview(
    name = "Chat - Dark",
    showBackground = true,
    backgroundColor = 0xFF0F172A,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ChatScreenPreviewDark() {
    PeerXTheme {
        ChatScreen(
            uiState = ChatUiState(
                peerAddress = previewPeerAddress,
                messages = previewMessages,
                inputText = "Ready in cyber mode"
            ),
            onBack = {},
            onInputChange = {},
            onSendClick = {},
            onFileClick = {},
            onStartCall = {},
            onHangup = {}
        )
    }
}





