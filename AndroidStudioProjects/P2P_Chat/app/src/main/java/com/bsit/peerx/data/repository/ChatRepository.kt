package com.bsit.peerx.data.repository

import android.net.Uri
import com.bsit.peerx.data.model.ChatMessageModel
import com.bsit.peerx.network.model.CallSignalingEvent
import com.bsit.peerx.network.model.NetworkPacket
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun observeMessages(peerAddress: String): Flow<List<ChatMessageModel>>
    suspend fun sendMessage(peerAddress: String, text: String): Result<Unit>

    // Group chat: send a message targeted at a group/channel id
    suspend fun sendGroupMessage(groupId: String, text: String): Result<Unit>
    
    fun observeTypingStatus(peerAddress: String): Flow<Boolean>
    suspend fun sendTypingStatus(peerAddress: String, isTyping: Boolean): Result<Unit>

    fun observeFileProgress(fileTransferId: String): Flow<Float>
    suspend fun sendFile(peerAddress: String, uri: Uri): Result<Unit>
    suspend fun handleFileHeader(packet: NetworkPacket)

    // Read receipts
    suspend fun sendReadReceiptsForPeer(peerAddress: String): Result<Unit>

    // Call signaling (SDP / ICE) events for WebRTC
    fun observeCallSignaling(): Flow<CallSignalingEvent>

    // Call control (high-level wrappers around CallManager)
    suspend fun startCall(targetPeerId: String): Result<String>
    suspend fun stopCall(callId: String): Result<Unit>

    suspend fun getPeerName(peerId: String): String?
}



