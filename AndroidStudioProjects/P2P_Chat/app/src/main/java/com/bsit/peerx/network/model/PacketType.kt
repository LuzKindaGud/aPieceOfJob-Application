package com.bsit.peerx.network.model

enum class PacketType {
    TEXT,
    ACK_RECEIPT,
    TYPING_STATUS,
    PROFILE_EXCHANGE,
    AUTH_PUBKEY_EXCHANGE,
    AUTH_CHALLENGE,
    AUTH_RESPONSE,
    FILE_HEADER,
    FILE_READY,
    FILE_CHUNK,
    FILE_COMPLETE,
    CALL_SDP,
    CALL_ICE_CANDIDATE,
    CALL_HANGUP
}


