package com.bsit.peerx

import androidx.lifecycle.ViewModel
import com.bsit.peerx.socket.SocketMessageTransport
import com.bsit.peerx.wifi_direct.WifiDirectManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val wifiDirectManager: WifiDirectManager,
    private val socketTransport: SocketMessageTransport
) : ViewModel() {

    fun registerReceiver() {
        wifiDirectManager.registerReceiver()
    }

    fun unregisterReceiver() {
        wifiDirectManager.unregisterReceiver()
    }

    override fun onCleared() {
        // ÄÆ°á»£c gá»i khi ViewModel bá»‹ há»§y (khi app Ä‘Ã³ng hoÃ n toÃ n)
        socketTransport.disconnect()
        super.onCleared()
    }
}


