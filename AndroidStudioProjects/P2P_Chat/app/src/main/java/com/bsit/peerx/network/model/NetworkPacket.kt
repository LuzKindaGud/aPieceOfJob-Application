package com.bsit.peerx.network.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import java.util.UUID

@Serializable
data class NetworkPacket(
    val type: PacketType,
    val senderId: String,         // SHA-256(publicKey) of sender
    val packetId: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val payload: JsonElement      // type-specific content
)


