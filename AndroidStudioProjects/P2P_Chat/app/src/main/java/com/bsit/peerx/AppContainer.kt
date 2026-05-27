package com.bsit.peerx

import android.content.Context
import android.net.wifi.p2p.WifiP2pManager
import android.os.Looper
import androidx.room.Room
import com.bsit.peerx.crypto.KeyStoreManager
import com.bsit.peerx.data.datasource.ChatLocalDataSource
import com.bsit.peerx.data.datasource.PeerDiscoveryDataSource
import com.bsit.peerx.data.local.AppDatabase
import com.bsit.peerx.data.repository.ChatRepository
import com.bsit.peerx.data.repository.ChatRepositoryImpl
import com.bsit.peerx.data.repository.ProfileRepository
import com.bsit.peerx.data.repository.ProfileRepositoryImpl
import com.bsit.peerx.socket.MessageTransport
import com.bsit.peerx.socket.SocketMessageTransport
import com.bsit.peerx.wifi_direct.WifiDirectManager
import com.bsit.peerx.wifi_direct.WifiDirectPeerDiscoveryDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class AppContainer(
    context: Context
) {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val keyStoreManager = KeyStoreManager()

    private val wifiP2pManager =
        context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    private val channel = wifiP2pManager.initialize(context, Looper.getMainLooper(), null)

    private val database: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "peerx.db"
    ).fallbackToDestructiveMigration().build()

    private val peerDao = database.peerDao()

    val profileRepository: ProfileRepository = ProfileRepositoryImpl(context, keyStoreManager)

    val socketMessageTransport = SocketMessageTransport(appScope, keyStoreManager, peerDao, profileRepository)
    val wifiDirectManager = WifiDirectManager(
        context = context,
        manager = wifiP2pManager,
        channel = channel,
        socketTransport = socketMessageTransport
    )

    private val localDataSource = ChatLocalDataSource(database.chatMessageDao())
    val messageTransport: MessageTransport = socketMessageTransport
    val peerDiscoveryDataSource: PeerDiscoveryDataSource =
        WifiDirectPeerDiscoveryDataSource(wifiDirectManager)

    val callManager = com.bsit.peerx.network.CallManager(
        context = context.applicationContext,
        scope = appScope,
        transport = socketMessageTransport,
        keyStoreManager = keyStoreManager
    )

    val chatRepository: ChatRepository = ChatRepositoryImpl(
        local = localDataSource,
        peerDao = peerDao,
        transport = messageTransport,
        keyStoreManager = keyStoreManager,
        scope = appScope,
        context = context.applicationContext,
        callManager = callManager
    )

    fun clear() {
        appScope.cancel()
    }
}



