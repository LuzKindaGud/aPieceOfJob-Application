package com.bsit.peerx.di

import android.content.Context
import android.net.wifi.p2p.WifiP2pManager
import android.os.Looper
import androidx.room.Room
import com.bsit.peerx.crypto.KeyStoreManager
import com.bsit.peerx.data.datasource.ChatLocalDataSource
import com.bsit.peerx.data.datasource.PeerDiscoveryDataSource
import com.bsit.peerx.data.local.AppDatabase
import com.bsit.peerx.data.local.dao.PeerDao
import com.bsit.peerx.data.repository.ChatRepository
import com.bsit.peerx.data.repository.ChatRepositoryImpl
import com.bsit.peerx.data.repository.ProfileRepository
import com.bsit.peerx.data.repository.ProfileRepositoryImpl
import com.bsit.peerx.socket.MessageTransport
import com.bsit.peerx.socket.SocketMessageTransport
import com.bsit.peerx.wifi_direct.WifiDirectManager
import com.bsit.peerx.wifi_direct.WifiDirectPeerDiscoveryDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWifiP2pManager(@ApplicationContext context: Context): WifiP2pManager {
        return context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    }

    @Provides
    @Singleton
    fun provideWifiP2pChannel(
        @ApplicationContext context: Context,
        manager: WifiP2pManager
    ): WifiP2pManager.Channel {
        return manager.initialize(context, Looper.getMainLooper(), null)
    }

    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    @Provides
    @Singleton
    fun provideSocketMessageTransport(
        @ApplicationScope scope: CoroutineScope,
        keyStoreManager: KeyStoreManager,
        peerDao: PeerDao,
        profileRepository: ProfileRepository
    ): SocketMessageTransport {
        return SocketMessageTransport(scope, keyStoreManager, peerDao, profileRepository)
    }

    @Provides
    @Singleton
    fun provideWifiDirectManager(
        @ApplicationContext context: Context,
        manager: WifiP2pManager,
        channel: WifiP2pManager.Channel,
        socketTransport: SocketMessageTransport
    ): WifiDirectManager {
        return WifiDirectManager(context, manager, channel, socketTransport)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "PeerX.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun providePeerDao(database: AppDatabase): PeerDao {
        return database.peerDao()
    }

    @Provides
    fun provideChatLocalDataSource(database: AppDatabase): ChatLocalDataSource {
        return ChatLocalDataSource(database.chatMessageDao())
    }

    @Provides
    @Singleton
    fun provideMessageTransport(socketMessageTransport: SocketMessageTransport): MessageTransport {
        return socketMessageTransport
    }

    @Provides
    @Singleton
    fun provideCallManager(
        @ApplicationContext context: Context,
        @ApplicationScope scope: CoroutineScope,
        transport: MessageTransport,
        keyStoreManager: KeyStoreManager
    ): com.bsit.peerx.network.CallManager {
        return com.bsit.peerx.network.CallManager(context, scope, transport, keyStoreManager)
    }

    @Provides
    @Singleton
    fun provideChatRepository(
        @ApplicationContext context: Context,
        localDataSource: ChatLocalDataSource,
        peerDao: PeerDao,
        transport: MessageTransport,
        keyStoreManager: KeyStoreManager,
        @ApplicationScope scope: CoroutineScope,
        callManager: com.bsit.peerx.network.CallManager
    ): ChatRepository {
        return ChatRepositoryImpl(
            context = context,
            local = localDataSource,
            peerDao = peerDao,
            transport = transport,
            keyStoreManager = keyStoreManager,
            scope = scope,
            callManager = callManager
        )
    }

    @Provides
    @Singleton
    fun providePeerDiscoveryDataSource(manager: WifiDirectManager): PeerDiscoveryDataSource {
        return WifiDirectPeerDiscoveryDataSource(manager)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(
        @ApplicationContext context: Context,
        keyStoreManager: KeyStoreManager
    ): ProfileRepository {
        return ProfileRepositoryImpl(context, keyStoreManager)
    }
}




