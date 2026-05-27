package com.bsit.peerx.network

import android.content.Context
import com.bsit.peerx.socket.MessageTransport
import com.bsit.peerx.crypto.KeyStoreManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import com.bsit.peerx.network.model.NetworkPacket
import com.bsit.peerx.network.model.PacketType
import java.util.UUID

/**
 * CallManager - lightweight stub for WebRTC integration.
 *
 * Responsibilities:
 * - Send/receive call signaling packets over MessageTransport (CALL_SDP / CALL_ICE_CANDIDATE)
 *
 * This stub lets the app compile and run without the org.webrtc native dependency.
 */
class CallManager(
    private val context: Context,
    private val scope: CoroutineScope,
    private val transport: MessageTransport,
    private val keyStoreManager: KeyStoreManager
) {
    private val incomingSignals = MutableSharedFlow<NetworkPacket>(extraBufferCapacity = 10)

    fun observeIncomingSignals() = incomingSignals.asSharedFlow()

    fun startCall(targetPeerId: String, callId: String = UUID.randomUUID().toString()) {
        val payload = buildJsonObject {
            put("callId", callId)
            put("sdp", "SDP_PLACEHOLDER_OFFER")
            put("target", targetPeerId)
        }
        val packet = NetworkPacket(type = PacketType.CALL_SDP, senderId = keyStoreManager.getPeerIdHash(), payload = payload)
        scope.launch { transport.sendPacket(packet) }
    }

    suspend fun handleRemoteSdp(from: String, callId: String?, sdp: String) {
        val payload = buildJsonObject {
            put("callId", callId ?: "")
            put("sdp", sdp)
            put("from", from)
        }
        val packet = NetworkPacket(type = PacketType.CALL_SDP, senderId = from, payload = payload)
        incomingSignals.emit(packet)
    }

    suspend fun handleRemoteIceCandidate(from: String, callId: String?, candidate: String) {
        val payload = buildJsonObject {
            put("callId", callId ?: "")
            put("candidate", candidate)
            put("from", from)
        }
        val packet = NetworkPacket(type = PacketType.CALL_ICE_CANDIDATE, senderId = from, payload = payload)
        incomingSignals.emit(packet)
    }

    fun stopCall(callId: String) {
        val payload = buildJsonObject { put("callId", callId) }
        val packet = NetworkPacket(type = PacketType.CALL_HANGUP, senderId = keyStoreManager.getPeerIdHash(), payload = payload)
        scope.launch { transport.sendPacket(packet) }
    }
}



