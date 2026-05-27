package com.bsit.peerx.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bsit.peerx.data.local.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {

    @Query("SELECT * FROM messages WHERE peerAddress = :peerAddress ORDER BY timestamp ASC")
    fun observeMessages(peerAddress: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: ChatMessageEntity)

    @Query("UPDATE messages SET status = :status WHERE packetId = :packetId")
    suspend fun updateStatus(packetId: String, status: String)

    @Query("DELETE FROM messages WHERE peerAddress = :peerAddress")
    suspend fun clearForPeer(peerAddress: String)

    @Query("SELECT * FROM messages WHERE peerAddress = :peerAddress AND senderType = 'RECEIVED' AND status != 'READ' ORDER BY timestamp ASC")
    suspend fun getUnreadReceivedMessages(peerAddress: String): List<ChatMessageEntity>
}




