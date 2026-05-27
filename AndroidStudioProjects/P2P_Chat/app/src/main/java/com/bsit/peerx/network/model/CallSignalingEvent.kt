package com.bsit.peerx.network.model

sealed class CallSignalingEvent {
    data class Sdp(val from: String, val callId: String?, val sdp: String) : CallSignalingEvent()
    data class IceCandidate(val from: String, val callId: String?, val candidate: String) : CallSignalingEvent()
}


