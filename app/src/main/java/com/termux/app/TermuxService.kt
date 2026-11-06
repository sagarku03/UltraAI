package com.termux.app

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager

/**
 * Direct com.termux.app.TermuxService implementation for shell commands
 * (e.g., termux-wake-lock, termux-wake-unlock) targeting explicit component com.termux.app.TermuxService.
 */
class TermuxService : Service() {
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_WAKE_LOCK, "com.termux.service_wake_lock" -> {
                if (wakeLock == null) {
                    val pm = getSystemService(Context.POWER_SERVICE) as? PowerManager
                    wakeLock = pm?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Termux:WakeLock")?.apply {
                        acquire()
                    }
                }
            }
            ACTION_WAKE_UNLOCK, "com.termux.service_wake_unlock", ACTION_STOP_SERVICE, "com.termux.service_stop" -> {
                wakeLock?.let {
                    if (it.isHeld) it.release()
                }
                wakeLock = null
                if (intent.action == ACTION_STOP_SERVICE || intent.action == "com.termux.service_stop") {
                    stopSelf()
                }
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        wakeLock?.let {
            if (it.isHeld) it.release()
        }
        wakeLock = null
        super.onDestroy()
    }

    companion object {
        const val ACTION_WAKE_LOCK = "com.termux.service_wake_lock"
        const val ACTION_WAKE_UNLOCK = "com.termux.service_wake_unlock"
        const val ACTION_STOP_SERVICE = "com.termux.service_stop"
    }
}
