package com.bsit.peerx.ui.chat

import com.bsit.peerx.data.model.ChatMessageModel

data class ChatUiState(
    val peerAddress: String = "",
    val peerName: String? = null,
    val ownName: String = "You",
    val messages: List<ChatMessageModel> = emptyList(),
    val inputText: String = "",
    val isSending: Boolean = false,
    val isPeerTyping: Boolean = false,
    val fileProgress: Map<String, Float> = emptyMap(),
    val isInCall: Boolean = false,
    val currentCallId: String? = null,
    val errorMessage: String? = null
)




