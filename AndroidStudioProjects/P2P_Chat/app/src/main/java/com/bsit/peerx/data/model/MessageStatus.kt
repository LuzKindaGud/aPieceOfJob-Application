package com.bsit.peerx.data.model

enum class MessageStatus {
    NONE,       // For received messages (not applicable)
    SENDING,
    SENT,
    DELIVERED,
    READ,
    FAILED
}


