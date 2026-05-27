package com.bsit.peerx.data.datasource

import android.annotation.SuppressLint
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

import android.content.Context
import android.net.wifi.p2p.WifiP2pManager

class WifiDirectPeerDiscoveryDataSource (
    private val context: Context
) {
    private val manager: WifiP2pManager by lazy {
        context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    }

    private val channel: WifiP2pManager.Channel by lazy {
        manager.initialize(context, Looper.getMainLooper(), null)
    }

    @SuppressLint("MissingPermission")
    suspend fun connectToPeer( device: WifiP2pDevice) {
        val config = WifiP2pConfig().apply {
            deviceAddress = device.deviceAddress
            // groupOwnerIntent = 0 // 0 Ä‘á»ƒ thiáº¿t bá»‹ nÃ y Ã­t cÃ³ kháº£ nÄƒng trá»Ÿ thÃ nh chá»§ nhÃ³m hÆ¡n
        }

        return suspendCancellableCoroutine {
            continuation ->
            manager.connect(channel, config, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Log.d("WifiDirect", "Connect call succeeded.")
                    // ThÃ nh cÃ´ng á»Ÿ Ä‘Ã¢y chá»‰ cÃ³ nghÄ©a lÃ  yÃªu cáº§u káº¿t ná»‘i Ä‘Ã£ Ä‘Æ°á»£c gá»­i Ä‘i thÃ nh cÃ´ng.
                    // Káº¿t quáº£ káº¿t ná»‘i thá»±c táº¿ sáº½ Ä‘Æ°á»£c nháº­n qua BroadcastReceiver.
                    continuation.resume(Unit)
                }

                override fun onFailure(reason: Int) {
                    val errorMessage = "Connect call failed with reason code: $reason"
                    Log.e("WifiDirect", errorMessage)
                    continuation.resumeWithException(Exception(errorMessage))
                }
            })
            continuation.invokeOnCancellation {
                manager.cancelConnect(channel , null)
            }
        }
    }
    // ... cÃ¡c hÃ m discoverPeers, stopDiscoverPeers khÃ¡c ...
}

