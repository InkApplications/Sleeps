package com.inkapplications.sleeps.android.maintenance

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kimchi.Kimchi

private const val REQUEST_CODE = 30916

/**
 * Receives scheduled maintenance events and starts the maintenance service.
 */
class MaintenanceReceiver() : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Kimchi.trace("onReceive")
        context.startService(context.createMaintenanceServiceIntent())
    }
}

/**
 * Create a pending intent used to start maintenance tasks.
 */
fun Context.createMaintenanceReceiverIntent(): PendingIntent = PendingIntent.getBroadcast(
    this,
    REQUEST_CODE,
    Intent(this, MaintenanceReceiver::class.java),
    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
)
