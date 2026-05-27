package com.bsit.peerx.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val packetId: String? = null,
    val text: String,
    val senderType: String,
    val messageType: String = "TEXT",
    val fileTransferId: String? = null,
    val status: String = "NONE",
    val timestamp: Long,
    val peerAddress: String,
    val senderId: String? = null
)



