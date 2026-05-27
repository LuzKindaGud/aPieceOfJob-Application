package com.bsit.peerx

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.bsit.peerx.data.datasource.PeerDiscoveryDataSource
import com.bsit.peerx.data.repository.ChatRepository
import com.bsit.peerx.data.repository.ProfileRepository
import com.bsit.peerx.socket.SocketMessageTransport
import com.bsit.peerx.ui.chat.ChatViewModel
import com.bsit.peerx.ui.discovery.DiscoveryViewModel
import com.bsit.peerx.wifi_direct.WifiDirectManager

class MainViewModelFactory(
    private val wifiDirectManager: WifiDirectManager,
    private val socketTransport: SocketMessageTransport
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(wifiDirectManager, socketTransport) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

class DiscoveryViewModelFactory(
    private val peerDataSource: PeerDiscoveryDataSource,
    private val socketTransport: SocketMessageTransport,
    private val profileRepository: ProfileRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiscoveryViewModel::class.java)) {
            return DiscoveryViewModel(peerDataSource, socketTransport, profileRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

class ChatViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val repository: ChatRepository,
    private val profileRepository: ProfileRepository,
    defaultArgs: Bundle?
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(repository, profileRepository, handle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}



