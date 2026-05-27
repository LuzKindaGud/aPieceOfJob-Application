package com.bsit.peerx.data.datasource

import android.net.wifi.p2p.WifiP2pDevice
import kotlinx.coroutines.flow.Flow

interface PeerDiscoveryDataSource {
    fun getPeers(): Flow<List<WifiP2pDevice>>
    suspend fun discoverPeers()
    suspend fun stopDiscoverPeers()
    suspend fun connectToPeer(device: WifiP2pDevice)
    suspend fun observeWifiState() : Flow<Boolean>
    fun observeDiscoveryError(): Flow<String?>
    suspend fun requestToEnableWifi()
}



