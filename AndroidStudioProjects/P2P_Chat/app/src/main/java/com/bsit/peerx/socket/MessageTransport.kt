package com.bsit.peerx.socket

import com.bsit.peerx.network.model.NetworkPacket
import kotlinx.coroutines.flow.Flow

interface MessageTransport {
    suspend fun sendPacket(packet: NetworkPacket): Result<Unit>
    fun incomingPackets(): Flow<NetworkPacket>
    fun getRemoteAddress(): String?
    
    // Legacy support for simple text sending (optional, but keep for now if needed)
    suspend fun send(peerAddress: String, text: String): Result<Unit>
}




