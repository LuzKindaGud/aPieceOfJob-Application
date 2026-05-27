package com.bsit.peerx.wifi_direct

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import android.content.Intent
import android.net.wifi.p2p.WifiP2pInfo
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresPermission
import com.bsit.peerx.socket.SocketMessageTransport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class WifiDirectManager(
    private val context: Context,
    private val manager: WifiP2pManager,
    private val channel : WifiP2pManager.Channel,
    private val socketTransport: SocketMessageTransport // Inject SocketMessageTransport
) {
//  CÃ¡c StateFlow Ä‘á»ƒ theo dÃµi tráº¡ng thÃ¡i Wi-Fi vÃ  tráº¡ng thÃ¡i káº¿t ná»‘i.
    private val peersFlow = MutableStateFlow<List<WifiP2pDevice>>(emptyList())
    private val wifiStateFlow = MutableStateFlow(false)
    private val _connectionInfo = MutableStateFlow<WifiP2pInfo?>(null)
    private val _discoveryError = MutableStateFlow<String?>(null)

    val connectionInfo: Flow<WifiP2pInfo?> = _connectionInfo.asStateFlow()
    fun observeWifiState(): Flow<Boolean> = wifiStateFlow.asStateFlow()
    fun observePeers(): Flow<List<WifiP2pDevice>> = peersFlow.asStateFlow()
    fun observeDiscoveryError(): Flow<String?> = _discoveryError.asStateFlow()

    // Táº¡o IntentFilter Ä‘á»ƒ Ä‘Äƒng kÃ½ cÃ¡c event cá»§a Wifi Direct
    private val intentFilter = IntentFilter().apply {
        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }

    fun openWifiSettings() { //kiá»ƒm tra xem thiáº¿t bá»‹ cá»§a ngÆ°á»i dÃ¹ng cÃ³ Ä‘ang cháº¡y há»‡ Ä‘iá»u hÃ nh Android 10 (API level 29) hoáº·c má»›i hÆ¡n hay khÃ´ng
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Intent(Settings.Panel.ACTION_WIFI)
        } else {
            Intent(Settings.ACTION_WIFI_SETTINGS)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    // TÃ­ch há»£p WiFiDirectBroadcastReceiver
    @SuppressLint("MissingPermission")
    val receiver = WiFiDirectBroadcastReceiver(
        onP2pStateChanged = { // Báº¯t sá»± kiá»‡n Wi-Fi báº­t/táº¯t
            isEnabled ->
            wifiStateFlow.value = isEnabled
        },
        onPeersChanged = { // Khi há»‡ thá»‘ng phÃ¡t hiá»‡n cÃ³ thiáº¿t bá»‹ xung quanh, yÃªu cáº§u láº¥y danh sÃ¡ch (requestPeers)
            manager.requestPeers(channel) { peers ->
                peersFlow.value = peers.deviceList.toList()
                onPeersUpdated(peersFlow.value)
            }
        },
        onConnectionChanged = { // Xá»­ lÃ½ thay Ä‘á»•i káº¿t ná»‘i
            manager.requestConnectionInfo(channel) { info ->
                _connectionInfo.value = info

                if (info != null && info.groupFormed) {
                    socketTransport.startCommunicationFrom(info)
                } else {
                    socketTransport.disconnect()
                }
            }
        },
        onThisDeviceChanged = { // Xá»­ lÃ½ thay Ä‘á»•i thiáº¿t bá»‹ cá»§a báº¡n

        }
    )

    // Ra lá»‡nh xá»­ lÃ½ tÃ¬m kiáº¿m thiáº¿t bá»‹ á»Ÿ pháº§n cá»©ng
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.NEARBY_WIFI_DEVICES])
    @SuppressLint("MissingPermission") // Cáº§n Ä‘áº£m báº£o Ä‘Ã£ xin quyá»n lÃºc runtime
    fun discoverPeers() {
        try {
            // Stop any ongoing discovery first to avoid reason code 0 (busy/error)
            manager.stopPeerDiscovery(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    startDiscoveryProcess()
                }

                override fun onFailure(reason: Int) {
                    // If stopping fails, we still try to start, it might just be that no discovery was running
                    startDiscoveryProcess()
                }
            })
        } catch (e: SecurityException) {
            Log.e("WifiDirectManager", "Permission denied for discovery: ${e.message}")
            _discoveryError.value = "Permission denied. Please grant nearby devices/location permission."
        }
    }

    @SuppressLint("MissingPermission")
    private fun startDiscoveryProcess() {
        try {
            manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Log.d("WifiDirectManager", "Peer discovery started successfully.")
                    _discoveryError.value = null
                }

                override fun onFailure(reason: Int) {
                    val reasonStr = when (reason) {
                        WifiP2pManager.P2P_UNSUPPORTED -> "P2P_UNSUPPORTED"
                        WifiP2pManager.BUSY -> "BUSY"
                        WifiP2pManager.ERROR -> "ERROR (0) - Check if Location is ON and permissions are granted."
                        else -> "Unknown ($reason)"
                    }
                    Log.e("WifiDirectManager", "Peer discovery failed with reason: $reasonStr")
                    _discoveryError.value = "Discovery failed: $reasonStr"
                }
            })
        } catch (e: SecurityException) {
            Log.e("WifiDirectManager", "Permission denied for discovery: ${e.message}")
            _discoveryError.value = "Permission denied. Please grant nearby devices/location permission."
        }
    }

    @SuppressLint("MissingPermission")
    fun stopDiscovery() {
        try {
            manager.stopPeerDiscovery(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Log.d("WifiDirectManager", "Peer discovery stopped.")
                }

                override fun onFailure(reason: Int) {
                    Log.e("WifiDirectManager", "Stop peer discovery failed: $reason")
                }
            })
        } catch (e: SecurityException) {
            Log.e("WifiDirectManager", "Permission denied for stop discovery: ${e.message}")
        }
    }
    fun onPeersUpdated(devices: List<WifiP2pDevice>) {
        peersFlow.value = devices
    }

    @SuppressLint("MissingPermission")
    fun connectToPeer( device: WifiP2pDevice) {
        val config = WifiP2pConfig().apply {
            deviceAddress = device.deviceAddress
        }
        manager.connect(channel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d("WifiDirectManager", "Connection initiated successfully.")
            }

            override fun onFailure(reason: Int) {
                Log.e("WifiDirectManager", "Connection initiation failed with reason code: $reason")
            }
        })
    }

    // CÃ¡c hÃ m Ä‘á»ƒ Ä‘Äƒng kÃ½ vÃ  há»§y Ä‘Äƒng kÃ½ Receiver (Gá»i trÃªn UI Layer)
    fun registerReceiver() {
        context.registerReceiver(receiver, intentFilter)
    }

    fun unregisterReceiver() {
        context.unregisterReceiver(receiver)
    }
}




