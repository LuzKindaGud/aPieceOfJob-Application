package com.bsit.peerx.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "peers")
data class PeerEntity(
    @PrimaryKey val peerId: String,      // SHA-256 hash of their public key (Base64)
    val publicKeyBase64: String,          // their raw public key bytes (Base64)
    val displayName: String,
    val avatarHash: String?,              // SHA-256 of avatar bytes, nullable
    val macAddress: String,              // still store for WifiP2pConfig, but NOT primary key
    val firstSeenAt: Long,
    val lastSeenAt: Long
)


