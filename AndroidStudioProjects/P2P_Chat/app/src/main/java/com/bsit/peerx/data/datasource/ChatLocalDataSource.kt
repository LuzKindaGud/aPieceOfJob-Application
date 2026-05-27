package com.bsit.peerx.data.datasource

import com.bsit.peerx.data.local.dao.ChatMessageDao
import com.bsit.peerx.data.local.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

class ChatLocalDataSource(
    private val dao: ChatMessageDao
) {
    fun observeMessages(peerAddress: String): Flow<List<ChatMessageEntity>> = dao.observeMessages(peerAddress)

    suspend fun insert(message: ChatMessageEntity) = dao.insert(message)

    suspend fun updateStatus(packetId: String, status: String) = dao.updateStatus(packetId, status)

    suspend fun getUnreadReceived(peerAddress: String) = dao.getUnreadReceivedMessages(peerAddress)
}




