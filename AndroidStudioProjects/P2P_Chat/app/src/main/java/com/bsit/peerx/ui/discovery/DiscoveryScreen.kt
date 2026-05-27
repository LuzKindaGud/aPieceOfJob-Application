package com.bsit.peerx.ui.discovery

import android.net.wifi.p2p.WifiP2pDevice
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bsit.peerx.ui.components.CyberBackground
import com.bsit.peerx.ui.components.CyberButton
import com.bsit.peerx.ui.components.CyberOutlineButton
import com.bsit.peerx.ui.components.CyberTextField
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
import com.bsit.peerx.ui.theme.PeerXTheme
import com.bsit.peerx.ui.theme.Spacing
import com.bsit.peerx.ui.theme.TextMuted
import com.bsit.peerx.ui.theme.TextPrimary
import com.bsit.peerx.ui.theme.TextSecondary
import com.bsit.peerx.ui.theme.glassSurface
import com.bsit.peerx.ui.theme.neonGlow

@Composable
fun DiscoveryRoute(
    viewModel: DiscoveryViewModel,
    onOpenChat: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigateToChat.collect { peerAddress ->
            onOpenChat(peerAddress)
        }
    }

    DiscoveryScreen(
        uiState = state,
        onWifiToggle = { viewModel.onWifiToggleClicked(state.isWifiEnabled) },
        onDiscoverClick = viewModel::discoverPeers,
        onModeChange = viewModel::setDiscoveryMode,
        onPeerClick = { deviceAddress ->
            if (!state.isConnecting) {
                viewModel.connectToPeer(deviceAddress)
            }
        },
        onStartServer = viewModel::startManualServer,
        onConnectToServer = viewModel::connectToManualServer,
        onProfileUpdate = viewModel::updateDisplayName,
        onJoinGroup = viewModel::joinGroup
    )
}

@Composable
fun DiscoveryScreen(
    uiState: DiscoveryUiState,
    onWifiToggle: () -> Unit,
    onDiscoverClick: () -> Unit,
    onModeChange: (DiscoveryMode) -> Unit,
    onPeerClick: (String) -> Unit,
    onJoinGroup: (String) -> Unit,
    onStartServer: () -> Unit,
    onConnectToServer: (String) -> Unit,
    onProfileUpdate: (String) -> Unit
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var showProfileDialog by remember { mutableStateOf(false) }

    val filteredPeers by remember(uiState.peers, searchQuery) {
        derivedStateOf {
            if (searchQuery.isBlank()) {
                uiState.peers
            } else {
                uiState.peers.filter { peer ->
                    peer.deviceName.contains(searchQuery, ignoreCase = true) ||
                        peer.deviceAddress.contains(searchQuery, ignoreCase = true)
                }
            }
        }
    }

    if (uiState.isConnecting) {
        ConnectingDialog(deviceName = uiState.connectingDeviceName)
    }

    if (showProfileDialog) {
        ProfileDialog(
            currentName = uiState.ownDisplayName,
            onDismiss = { showProfileDialog = false },
            onSave = { 
                onProfileUpdate(it)
                showProfileDialog = false 
            }
        )
    }

    CyberBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                DiscoveryTopBar(
                    onWifiToggle = onWifiToggle,
                    onModeChange = onModeChange,
                    onProfileClick = { showProfileDialog = true },
                    isWifiEnabled = uiState.isWifiEnabled,
                    currentMode = uiState.mode
                )
            },
            floatingActionButton = {
                if (uiState.mode == DiscoveryMode.P2P) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(NeonPrimary, NeonSecondary)), CircleShape)
                            .border(1.dp, GlassBorder, CircleShape)
                            .neonGlow(NeonPrimary, 16.dp)
                            .clickable(enabled = !uiState.isConnecting) { onDiscoverClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Discover",
                            tint = TextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            },
            bottomBar = {
                DiscoveryBottomNavBar()
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(horizontal = Spacing.md, vertical = Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                if (uiState.mode == DiscoveryMode.P2P) {
                    var groupIdInput by rememberSaveable { mutableStateOf("") }
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                        CyberTextField(
                            value = groupIdInput,
                            onValueChange = { groupIdInput = it },
                            placeholder = "Enter group id (e.g. room-1)",
                                                    leadingIcon = Icons.Default.Person,
                                                    modifier = Modifier.weight(1f)
                                                )
                        CyberButton(
                            text = "Join/Create",
                            onClick = {
                                if (groupIdInput.isNotBlank()) {
                                    onJoinGroup(groupIdInput)
                                    groupIdInput = ""
                                }
                            }
                        )
                    }

                    P2PDiscoveryContent(
                        uiState = uiState,
                        searchQuery = searchQuery,
                        onQueryChange = { searchQuery = it },
                        filteredPeers = filteredPeers,
                        onPeerClick = onPeerClick
                    )
                } else {
                    EmulatorDiscoveryContent(
                        uiState = uiState,
                        onStartServer = onStartServer,
                        onConnectToServer = onConnectToServer
                    )
                }
            }
        }
    }
}

@Composable
private fun P2PDiscoveryContent(
    uiState: DiscoveryUiState,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    filteredPeers: List<WifiP2pDevice>,
    onPeerClick: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        DiscoverySearchBar(query = searchQuery, onQueryChange = onQueryChange)

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

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            itemsIndexed(filteredPeers) { index, peer ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
                ) {
                    PeerCard(
                        peer = peer,
                        index = index,
                        enabled = !uiState.isConnecting,
                        onClick = { onPeerClick(peer.deviceAddress) }
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(88.dp))
            }
        }
    }
}

@Composable
private fun EmulatorDiscoveryContent(
    uiState: DiscoveryUiState,
    onStartServer: () -> Unit,
    onConnectToServer: (String) -> Unit
) {
    var ipAddress by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        Text(
            text = "EMULATOR SYNC",
            style = MaterialTheme.typography.headlineMedium,
            color = NeonCyan
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(GlassSurface)
                .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
                .padding(Spacing.md)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Text(
                    text = "YOUR LOCAL IP",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
                Text(
                    text = uiState.localIpAddress ?: "Detecting...",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary
                )
                Text(
                    text = "Use this IP on the other device to connect",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        CyberButton(
            text = "START SERVER",
            onClick = onStartServer,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Box(modifier = Modifier.weight(1f).height(1.dp).background(GlassBorder))
            Text(text = "OR", style = MaterialTheme.typography.labelSmall, color = TextMuted)
            Box(modifier = Modifier.weight(1f).height(1.dp).background(GlassBorder))
        }

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
            CyberTextField(
                value = ipAddress,
                onValueChange = { ipAddress = it },
                placeholder = "Enter target IP address",
                leadingIcon = Icons.Default.Search,
                modifier = Modifier.fillMaxWidth()
            )

            CyberButton(
                text = "CONNECT TO SERVER",
                onClick = { onConnectToServer(ipAddress) },
                modifier = Modifier.fillMaxWidth(),
                enabled = ipAddress.isNotBlank() && !uiState.isConnecting
            )
        }

        AnimatedVisibility(
            visible = !uiState.errorMessage.isNullOrBlank(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = uiState.errorMessage.orEmpty(),
                color = NeonDanger,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = Spacing.md)
            )
        }
    }
}

@Composable
private fun DiscoveryTopBar(
    onWifiToggle: () -> Unit,
    onModeChange: (DiscoveryMode) -> Unit,
    onProfileClick: () -> Unit,
    isWifiEnabled: Boolean,
    currentMode: DiscoveryMode
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgPrimary.copy(alpha = 0.82f))
            .border(1.dp, GlassBorder, RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
            .glassSurface()
            .padding(horizontal = Spacing.md, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onProfileClick) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = NeonPrimary
                    )
                }
                if (currentMode == DiscoveryMode.P2P) {
                    CyberOutlineButton(
                        text = if (isWifiEnabled) "ON" else "OFF",
                        onClick = onWifiToggle,
                        modifier = Modifier.height(36.dp).width(60.dp)
                    )
                }
            }

            Text(
                text = "NEONCHAT",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary
            )

            CyberOutlineButton(
                text = if (currentMode == DiscoveryMode.P2P) "P2P" else "TEST",
                onClick = { 
                    val nextMode = if (currentMode == DiscoveryMode.P2P) DiscoveryMode.EMULATOR else DiscoveryMode.P2P
                    onModeChange(nextMode)
                },
                modifier = Modifier.height(36.dp),
                strokeColor = if (currentMode == DiscoveryMode.EMULATOR) NeonCyan else GlassBorder
            )
        }
    }
}

@Composable
private fun ProfileDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(BgPrimary.copy(alpha = 0.95f))
                .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                .glassSurface()
                .padding(Spacing.lg)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "IDENTITY PROTOCOL",
                    style = MaterialTheme.typography.headlineSmall,
                    color = NeonPrimary
                )
                Spacer(modifier = Modifier.height(Spacing.md))
                
                CyberTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Enter your alias...",
                    leadingIcon = Icons.Default.Person
                )
                
                Spacer(modifier = Modifier.height(Spacing.lg))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    CyberOutlineButton(
                        text = "CANCEL",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    )
                    CyberButton(
                        text = "SAVE",
                        onClick = { onSave(name) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DiscoverySearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    CyberTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = "Search decrypted channels...",
        leadingIcon = Icons.Default.Search,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun PeerCard(
    peer: WifiP2pDevice,
    index: Int,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val title = peer.deviceName.ifBlank { "NODE ${index + 1}" }
    val accent = peerAccent(title, index)
    val shape = RoundedCornerShape(18.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(GlassSurface, shape)
            .border(1.dp, GlassBorder, shape)
            .neonGlow(accent, 12.dp)
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = Spacing.md, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        Box(modifier = Modifier.size(52.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(BgSecondary.copy(alpha = 0.95f), RoundedCornerShape(16.dp))
                    .border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (title.contains("underground", ignoreCase = true) || index == 2) {
                        Icons.Default.Menu
                    } else {
                        Icons.Default.Person
                    },
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(26.dp)
                )
            }

            // status dot (glowing when online)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(peerStatusDot(title, index))
                    .border(2.dp, BgPrimary, CircleShape)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = peerTimeLabel(title, index),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = peerSubtitle(title, peer.deviceAddress, index),
                style = MaterialTheme.typography.bodyMedium,
                color = if (title.contains("underground", ignoreCase = true)) NeonCyan else TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        peerUnreadBadge(title, index)?.let { count ->
            Surface(
                color = NeonPink,
                shape = CircleShape
            ) {
                Text(
                    text = if (count > 99) "99+" else count.toString(),
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun DiscoveryBottomNavBar() {
    val navBarShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgPrimary.copy(alpha = 0.82f), navBarShape)
            .border(1.dp, GlassBorder, navBarShape)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DiscoveryBottomNavItem(
                icon = Icons.Default.Menu,
                label = "Chats",
                isActive = true
            )
            DiscoveryBottomNavItem(
                icon = Icons.Default.Person,
                label = "Nodes",
                isActive = false
            )
            DiscoveryBottomNavItem(
                icon = Icons.Default.Search,
                label = "Sync",
                isActive = false
            )
            DiscoveryBottomNavItem(
                icon = Icons.Default.Settings,
                label = "Config",
                isActive = false
            )
        }
    }
}

@Composable
private fun DiscoveryBottomNavItem(
    icon: ImageVector,
    label: String,
    isActive: Boolean
) {
    val color = if (isActive) NeonCyan else TextSecondary.copy(alpha = 0.78f)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = if (isActive) {
            Modifier.drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(NeonCyan.copy(alpha = 0.18f), Color.Transparent),
                        radius = size.maxDimension * 0.7f
                    ),
                    radius = size.maxDimension * 0.7f
                )
            }
        } else Modifier
    ) {
        Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
        Spacer(modifier = Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .height(2.dp)
                .width(28.dp)
                .background(
                    if (isActive) NeonCyan else Color.Transparent,
                    RoundedCornerShape(1.dp)
                )
        )
    }
}

@Composable
private fun ConnectingDialog(deviceName: String?) {
    Dialog(onDismissRequest = { }) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(BgSecondary.copy(alpha = 0.96f))
                .border(1.dp, BorderNeon, RoundedCornerShape(24.dp))
                .padding(Spacing.lg),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                CircularProgressIndicator(color = NeonPrimary)
                Text(
                    text = "CONNECTING TO",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
                Text(
                    text = deviceName ?: "Unknown Device",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
            }
        }
    }
}

private fun peerAccent(title: String, index: Int): Color {
    return when {
        title.contains("zero", ignoreCase = true) -> NeonDanger
        title.contains("underground", ignoreCase = true) -> NeonCyan
        title.contains("echo", ignoreCase = true) -> NeonGreen
        index % 3 == 0 -> NeonCyan
        index % 3 == 1 -> NeonDanger
        else -> NeonGreen
    }
}

private fun peerStatusDot(title: String, index: Int): Color {
    return when {
        title.contains("zero", ignoreCase = true) -> NeonDanger
        title.contains("underground", ignoreCase = true) -> NeonCyan
        title.contains("echo", ignoreCase = true) -> NeonGreen
        index % 2 == 0 -> NeonGreen
        else -> NeonDanger
    }
}

private fun peerTimeLabel(title: String, index: Int): String {
    return when {
        title.contains("nova", ignoreCase = true) -> "14:22"
        title.contains("zero", ignoreCase = true) -> "Yesterday"
        title.contains("underground", ignoreCase = true) -> "21:05"
        title.contains("echo", ignoreCase = true) -> "Sun"
        index == 0 -> "14:22"
        index == 1 -> "Yesterday"
        index == 2 -> "21:05"
        else -> "Now"
    }
}

private fun peerSubtitle(title: String, address: String, index: Int): String {
    return when {
        title.contains("nova", ignoreCase = true) -> "Payload delivered. The grid is open. Proceed with caution..."
        title.contains("zero", ignoreCase = true) -> "System backup complete. 404 logs wiped."
        title.contains("underground", ignoreCase = true) -> "Ghost_Hacker: Who has the encryption keys for the vault?"
        title.contains("echo", ignoreCase = true) -> "Did you see the new synth-wave release?"
        address.isNotBlank() -> address
        else -> "Encrypted node ready"
    }
}

private fun peerUnreadBadge(title: String, index: Int): Int? {
    return when {
        title.contains("nova", ignoreCase = true) -> 3
        index == 0 -> 3
        else -> null
    }
}

private val previewPeers = listOf(
    WifiP2pDevice().apply {
        deviceName = "Nova_Protocol"
        deviceAddress = "AA:BB:CC:DD:EE:01"
    },
    WifiP2pDevice().apply {
        deviceName = "Zero_Day"
        deviceAddress = "AA:BB:CC:DD:EE:02"
    },
    WifiP2pDevice().apply {
        deviceName = "The_Underground"
        deviceAddress = "AA:BB:CC:DD:EE:03"
    },
    WifiP2pDevice().apply {
        deviceName = "Echo_Vibe"
        deviceAddress = "AA:BB:CC:DD:EE:04"
    }
)

@Preview(name = "Discovery - Wifi Off", showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun DiscoveryScreenPreviewWifiOff() {
    PeerXTheme {
        DiscoveryScreen(
            uiState = DiscoveryUiState(isWifiEnabled = false),
            onWifiToggle = {},
            onDiscoverClick = {},
            onModeChange = {},
            onPeerClick = {},
            onStartServer = {},
            onConnectToServer = {},
            onProfileUpdate = {},
            onJoinGroup = {},
        )
    }
}

@Preview(name = "Discovery - Discovering", showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun DiscoveryScreenPreviewDiscovering() {
    PeerXTheme {
        DiscoveryScreen(
            uiState = DiscoveryUiState(
                isWifiEnabled = true,
                isDiscovering = true,
                peers = previewPeers
            ),
            onWifiToggle = {},
            onDiscoverClick = {},
            onModeChange = {},
            onPeerClick = {},
            onStartServer = {},
            onConnectToServer = {},
            onProfileUpdate = {},
            onJoinGroup = {}
        )
    }
}

@Preview(name = "Discovery - Has Peers", showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun DiscoveryScreenPreviewHasPeers() {
    PeerXTheme {
        DiscoveryScreen(
            uiState = DiscoveryUiState(
                isWifiEnabled = true,
                peers = previewPeers
            ),
            onWifiToggle = {},
            onDiscoverClick = {},
            onModeChange = {},
            onPeerClick = {},
            onStartServer = {},
            onConnectToServer = {},
            onProfileUpdate = {},
            onJoinGroup = {}
        )
    }
}

@Preview(name = "Discovery - Emulator Mode", showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun DiscoveryScreenPreviewEmulator() {
    PeerXTheme {
        DiscoveryScreen(
            uiState = DiscoveryUiState(
                mode = DiscoveryMode.EMULATOR,
                localIpAddress = "192.168.1.100"
            ),
            onWifiToggle = {},
            onDiscoverClick = {},
            onModeChange = {},
            onPeerClick = {},
            onStartServer = {},
            onConnectToServer = {},
            onProfileUpdate = {},
            onJoinGroup = {}
        )
    }
}





