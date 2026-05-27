package com.bsit.peerx.wifi_direct
//
//import android.net.wifi.p2p.WifiP2pDevice
//import com.bsit.peerx.data.datasource.PeerDiscoveryDataSource
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//
//class FakePeerDiscoveryDataSource : PeerDiscoveryDataSource {
//    private val peersFlow = MutableStateFlow<List<WifiP2pDevice>>(emptyList())
//    private val wifiStateFlow = MutableStateFlow(true)
//
//    override fun getPeers(): Flow<List<WifiP2pDevice>> = peersFlow.asStateFlow()
//
//    override suspend fun discoverPeers() {
//        delay(500)
//        peersFlow.value = listOf(
//            WifiP2pDevice().apply { deviceAddress = "AA:BB:CC:DD:EE:01"; deviceName = "Peer 1" },
//            WifiP2pDevice().apply { deviceAddress = "AA:BB:CC:DD:EE:02"; deviceName = "Peer 2" },
//            WifiP2pDevice().apply { deviceAddress = "AA:BB:CC:DD:EE:03"; deviceName = "Peer 3" }
//        )
//    }
//
//    override suspend fun stopDiscoverPeers() {
//        TODO("Not yet implemented")
//    }
//    override suspend fun connectToPeer(device: WifiP2pDevice) {
//        TODO("Not yet implemented")
//    }
//    override suspend fun observeWifiState(): Flow<Boolean> {
//        return wifiStateFlow.asStateFlow()
//    }
//    override suspend fun requestToEnableWifi() {
//        // KhÃ´ng lÃ m gÃ¬ trong báº£n fake, hoáº·c cÃ³ thá»ƒ log ra
//        println("Request to enable wifi triggered.")
//    }
//}
//




