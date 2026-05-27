package com.bsit.peerx.wifi_direct

import android.Manifest
import android.net.wifi.p2p.WifiP2pDevice
import androidx.annotation.RequiresPermission
import com.bsit.peerx.data.datasource.PeerDiscoveryDataSource
import kotlinx.coroutines.flow.Flow
class WifiDirectPeerDiscoveryDataSource(
    private val manager: WifiDirectManager
) : PeerDiscoveryDataSource {

    override fun getPeers(): Flow<List<WifiP2pDevice>> {
        return manager.observePeers()
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.NEARBY_WIFI_DEVICES])
    override suspend fun discoverPeers() {
        manager.discoverPeers()
    }

    override suspend fun stopDiscoverPeers() {
        manager.stopDiscovery()
    }

    override suspend fun connectToPeer(device: WifiP2pDevice) {
        // Gá»i hÃ m connect tá»« manager
        manager.connectToPeer(device)
    }

    override suspend fun observeWifiState(): Flow<Boolean> {
        return manager.observeWifiState()
    }

    override fun observeDiscoveryError(): Flow<String?> {
        return manager.observeDiscoveryError()
    }

    override suspend fun requestToEnableWifi() {
        manager.openWifiSettings()
    }


}




