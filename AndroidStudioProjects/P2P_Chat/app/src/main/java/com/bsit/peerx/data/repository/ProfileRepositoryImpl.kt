package com.bsit.peerx.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.bsit.peerx.crypto.KeyStoreManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val context: Context,
    private val keyStoreManager: KeyStoreManager
) : ProfileRepository {

    private val prefs: SharedPreferences = context.getSharedPreferences("user_profile", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_DISPLAY_NAME = "display_name"
    }

    override fun getDisplayName(): Flow<String> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { p, key ->
            if (key == KEY_DISPLAY_NAME) {
                trySend(p.getString(key, getDefaultName()) ?: getDefaultName())
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        trySend(prefs.getString(KEY_DISPLAY_NAME, getDefaultName()) ?: getDefaultName())
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    override suspend fun setDisplayName(name: String) {
        prefs.edit().putString(KEY_DISPLAY_NAME, name).apply()
    }

    private fun getDefaultName(): String {
        return "Agent_${keyStoreManager.getPeerIdHash().take(6)}"
    }
}



