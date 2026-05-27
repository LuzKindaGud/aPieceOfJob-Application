package com.bsit.peerx.wifi_direct

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import androidx.annotation.RequiresPermission

class WiFiDirectBroadcastReceiver(
    // Sá»­ dá»¥ng Callback (Higher-Order Functions) cá»§a Kotlin Ä‘á»ƒ truyá»n cÃ¡c hÃ nh Ä‘á»™ng cáº§n thá»±c hiá»‡n khi nháº­n Ä‘Æ°á»£c cÃ¡c sá»± kiá»‡n tá»« há»‡ thá»‘ng
    private val onP2pStateChanged: (Boolean) -> Unit = {},
    private val onPeersChanged: () -> Unit = {},
    private val onConnectionChanged: (p2pInfo: WifiP2pInfo) -> Unit = {},
    private val onThisDeviceChanged: (WifiP2pDevice?) -> Unit = {},
) : BroadcastReceiver() {

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.NEARBY_WIFI_DEVICES])
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                // Truyá»n ra ngoÃ i má»™t giÃ¡ trá»‹ Boolean cho dá»… xá»­ lÃ½: Wi-Fi P2P cÃ³ Ä‘ang báº­t hay khÃ´ng.
                onP2pStateChanged(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED)
            }

            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                // Sá»± kiá»‡n nÃ y chá»‰ thÃ´ng bÃ¡o ráº±ng danh sÃ¡ch peer Ä‘Ã£ thay Ä‘á»•i.
                // Viá»‡c gá»i requestPeers() sáº½ Ä‘Æ°á»£c thá»±c hiá»‡n á»Ÿ lá»›p quáº£n lÃ½.
                onPeersChanged()
            }

            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                val p2pInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO, WifiP2pInfo::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO)
                }
                p2pInfo?.let {
                    // Thay vÃ¬ kiá»ƒm tra networkInfo.isConnected, kiá»ƒm tra groupFormed
                    onConnectionChanged(it)
                }
            }

            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                // Láº¥y thÃ´ng tin thiáº¿t bá»‹ hiá»‡n táº¡i vÃ  truyá»n ra ngoÃ i.
                val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE, WifiP2pDevice::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
                }
                onThisDeviceChanged(device)
            }
        }
    }
}

