package com.inkapplications.sleeps.android.alarms

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.inkapplications.sleeps.state.alarms.AlarmType
import kimchi.Kimchi

private const val AlarmIdExtra = "alarm.id"

/**
 * Receiver registered to be called when an alarm goes off.
 */
class AndroidAlarmReceiver: BroadcastReceiver() {
    private val Intent.alarmType get() = getStringExtra(AlarmIdExtra).let(AlarmType::findById)

    override fun onReceive(context: Context, intent: Intent) {
        Kimchi.info("Alarm Received")
        context.startService(context.createStartAlarmServiceIntent(intent.alarmType))
    }
}

/**
 * Create a pending intent used to broadcast an alarm.
 */
fun Context.createAlarmBroadcastPendingIntent(
    alarm: AlarmType,
): PendingIntent = PendingIntent.getBroadcast(
    this,
    alarm.hashCode(),
    Intent(this, AndroidAlarmReceiver::class.java).apply {
        putExtra(AlarmIdExtra, alarm.id)
    },
    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
)
