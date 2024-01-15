package com.inkapplications.sleeps.android.alarms

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kimchi.Kimchi

private const val AlarmBroadcastIntentId = 7876

/**
 * Receiver registered to be called when an alarm goes off.
 */
class AndroidAlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Kimchi.info("Alarm Received")
        context.startService(context.createStartAlarmServiceIntent())
    }
}

/**
 * Create a pending intent used to broadcast an alarm.
 */
fun Context.createAlarmBroadcastPendingIntent(): PendingIntent = PendingIntent.getBroadcast(
    this,
    AlarmBroadcastIntentId,
    Intent(this, AndroidAlarmReceiver::class.java),
    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
)
