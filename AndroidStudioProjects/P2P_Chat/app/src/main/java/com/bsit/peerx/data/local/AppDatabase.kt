package com.bsit.peerx.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bsit.peerx.data.local.dao.ChatMessageDao
import com.bsit.peerx.data.local.dao.PeerDao
import com.bsit.peerx.data.local.entity.ChatMessageEntity
import com.bsit.peerx.data.local.entity.PeerEntity

@Database(
    entities = [ChatMessageEntity::class, PeerEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun peerDao(): PeerDao
}




