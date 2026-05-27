package com.bsit.peerx.ui.discovery

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bsit.peerx.data.datasource.PeerDiscoveryDataSource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import com.bsit.peerx.socket.SocketMessageTransport
import com.bsit.peerx.socket.ConnectionStatus
import com.bsit.peerx.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DiscoveryViewModel @Inject constructor(
    private val peerDataSource: PeerDiscoveryDataSource,
    private val socketTransport: SocketMessageTransport,
    private val profileRepository: ProfileRepository
) : ViewModel() {
// ... (omitting lines for brevity, will use full block in next turn if needed)

    private val _uiState = MutableStateFlow(DiscoveryUiState())
    val uiState: StateFlow<DiscoveryUiState> = _uiState.asStateFlow()

    private val _navigateToChat = MutableSharedFlow<String>(replay = 1, extraBufferCapacity = 1)
    val navigateToChat = _navigateToChat.asSharedFlow()

    init { // Khá»Ÿi cháº¡y coroutine ngay khi ViewModel Ä‘Æ°á»£c táº¡o
        observePeers()
        observeWifiState()
        observeDiscoveryErrors()
        observeSocketConnection()
        updateLocalIpAddress()
        observeProfile()
    }

    private fun observeProfile() {
        profileRepository.getDisplayName()
            .onEach { name ->
                _uiState.update { it.copy(ownDisplayName = name) }
            }
            .launchIn(viewModelScope)
    }

    fun updateDisplayName(name: String) {
        viewModelScope.launch {
            profileRepository.setDisplayName(name)
        }
    }

    private fun updateLocalIpAddress() {
        viewModelScope.launch(Dispatchers.IO) {
            val ip = getLocalIpAddress()
            _uiState.update { it.copy(localIpAddress = ip) }
        }
    }

    private fun getLocalIpAddress(): String? {
        try {
            val interfaces = java.net.NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val iface = interfaces.nextElement()
                val addresses = iface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val addr = addresses.nextElement()
                    if (!addr.isLoopbackAddress && addr is java.net.Inet4Address) {
                        return addr.hostAddress
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("DiscoveryViewModel", "Error getting local IP", e)
        }
        return null
    }

    fun setDiscoveryMode(mode: DiscoveryMode) {
        _uiState.update { it.copy(mode = mode, errorMessage = null) }
        if (mode == DiscoveryMode.EMULATOR) {
            updateLocalIpAddress()
        }
    }

    private fun observeDiscoveryErrors() {
        peerDataSource.observeDiscoveryError()
            .onEach { error ->
                _uiState.update { it.copy(errorMessage = error, isDiscovering = false) }
            }
            .launchIn(viewModelScope)
    }

    private fun observePeers() {
        peerDataSource.getPeers()
            .catch { e ->
                _uiState.update { it.copy(errorMessage = e.message) }
            }
            .onEach { peers ->
                _uiState.update { it.copy( peers = peers)  }
            }.launchIn(viewModelScope)
    }

    private fun observeWifiState() {
        viewModelScope.launch {
            peerDataSource.observeWifiState()
                .catch { e ->
                    _uiState.update { it.copy(errorMessage = e.message) }
                }
                .collect { isEnabled ->
                _uiState.update { it.copy(isWifiEnabled = isEnabled) }
            }
        }
    }

    fun discoverPeers() {
        viewModelScope.launch {
            if (!_uiState.value.isWifiEnabled) {
                _uiState.update { it.copy(errorMessage = "Wifi is off. Turn it on first.") }
                return@launch
            }
            _uiState.update { it.copy(isDiscovering = true, errorMessage = null) }
            runCatching { peerDataSource.discoverPeers() }
                .onFailure { e ->
                    _uiState.update { it.copy(isDiscovering = false, errorMessage = e.message) }
                }
        }
    }

    fun connectToPeer(peerDeviceAddress: String) {
        viewModelScope.launch {
            val peerDevice = _uiState.value.peers.firstOrNull {
                it.deviceAddress == peerDeviceAddress
            }
            if (peerDevice != null) {
                Log.d("DiscoveryViewModel", "Initiating connection to: ${peerDevice.deviceName} ($peerDeviceAddress)")
                // Update UI state to show connecting dialog
                _uiState.update { 
                    it.copy(
                        isConnecting = true, 
                        connectingDeviceName = peerDevice.deviceName 
                    ) 
                }
                
                runCatching {
                    peerDataSource.connectToPeer(peerDevice)
                }.onFailure { e ->
                    Log.e("DiscoveryViewModel", "Connection failed: ${e.message}")
                    _uiState.update { it.copy(
                        isConnecting = false,
                        errorMessage = "Connection failed: ${e.message}"
                    )}
                }
            } else {
                Log.e("DiscoveryViewModel", "Device not found in peer list: $peerDeviceAddress")
                _uiState.update { it.copy(errorMessage = "Device not found.") }
            }
        }
    }

    private fun observeSocketConnection() {
        socketTransport.connectionState
            .onEach { status ->
                Log.d("DiscoveryViewModel", "Socket connection status changed: $status")
                _uiState.update { 
                    it.copy(
                        isConnecting = (status == ConnectionStatus.CONNECTING || status == ConnectionStatus.CONNECTED)
                    ) 
                }
            }
            .launchIn(viewModelScope)

        combine(
            socketTransport.connectionState,
            socketTransport.connectedPeerId
        ) { state, peerId ->
            state to peerId
        }
        .filter { (state, peerId) -> 
            state == ConnectionStatus.CONNECTED_AND_VERIFIED && peerId != null 
        }
        .distinctUntilChanged()
        .onEach { (_, peerId) ->
            Log.d("DiscoveryViewModel", "Navigating to chat with peer: $peerId")
            _navigateToChat.emit(peerId!!)
        }
        .launchIn(viewModelScope)
    }

    fun startManualServer() {
        socketTransport.startServer()
    }

    fun connectToManualServer(ipAddress: String) {
        if (ipAddress.isNotBlank()) {
            socketTransport.connectToServer(ipAddress)
        } else {
            _uiState.update { it.copy(errorMessage = "Please enter a valid IP address.") }
        }
    }

    fun startDiscovery() {
        viewModelScope.launch {
            _uiState.update { it.copy(isDiscovering = true) }
            peerDataSource.discoverPeers()
        }
    }

    fun stopDiscovery() {
        viewModelScope.launch {
            peerDataSource.stopDiscoverPeers()
            _uiState.update { it.copy(isDiscovering = false) }
        }
    }

    fun onWifiToggleClicked(enabled: Boolean) {
        if (enabled) {
            viewModelScope.launch {
                peerDataSource.requestToEnableWifi()
            }
        }
    }

    fun joinGroup(groupId: String) {
        viewModelScope.launch {
            _navigateToChat.emit(groupId)
        }
    }
}



