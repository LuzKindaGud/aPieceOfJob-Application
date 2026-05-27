package com.bsit.peerx.data.repository

import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getDisplayName(): Flow<String>
    suspend fun setDisplayName(name: String)
}


