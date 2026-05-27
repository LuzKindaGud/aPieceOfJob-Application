package com.bsit.peerx.lock

import android.os.SystemClock
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLockManager @Inject constructor() : DefaultLifecycleObserver {

    private val _isLocked = MutableStateFlow(true)
    val isLocked: StateFlow<Boolean> = _isLocked

    private var backgroundTimestamp: Long = 0
    private val LOCK_TIMEOUT_MS = 30_000L // 30 seconds

    init {
        // Observe app-level lifecycle
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStop(owner: LifecycleOwner) {
        // App went to background
        backgroundTimestamp = SystemClock.elapsedRealtime()
    }

    override fun onStart(owner: LifecycleOwner) {
        // App came to foreground
        if (backgroundTimestamp != 0L && SystemClock.elapsedRealtime() - backgroundTimestamp > LOCK_TIMEOUT_MS) {
            _isLocked.value = true
        }
    }

    fun unlock() {
        _isLocked.value = false
    }
}


