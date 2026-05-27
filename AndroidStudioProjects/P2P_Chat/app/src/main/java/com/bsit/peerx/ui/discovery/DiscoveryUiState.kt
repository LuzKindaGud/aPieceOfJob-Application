package com.bsit.peerx.ui.discovery
import android.net.wifi.p2p.WifiP2pDevice

enum class DiscoveryMode {
    P2P,
    EMULATOR
}

data class DiscoveryUiState(
    val mode: DiscoveryMode = DiscoveryMode.P2P,
    val isWifiEnabled: Boolean = false,
    val isDiscovering: Boolean = false,
    val peers: List<WifiP2pDevice> = emptyList(),
    val errorMessage: String? = null,
    val isConnecting: Boolean = false,
    val connectingDeviceName: String? = null,
    val localIpAddress: String? = null,
    val ownDisplayName: String = ""
)



