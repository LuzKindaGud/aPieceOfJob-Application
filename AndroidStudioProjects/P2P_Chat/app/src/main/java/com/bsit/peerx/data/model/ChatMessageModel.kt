package com.bsit.peerx.data.model

data class ChatMessageModel(
    val id: Long = 0L,
    val packetId: String? = null,
    val text: String,
    val senderType: SenderType,
    val messageType: MessageType = MessageType.TEXT,
    val fileTransferId: String? = null,
    val status: MessageStatus = MessageStatus.NONE,
    val timestamp: Long,
    val peerAddress: String
)



