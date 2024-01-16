package com.inkapplications.sleeps.android.alarms

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.inkapplications.sleeps.state.alarms.AlarmId
import kimchi.Kimchi

private const val AlarmBroadcastIntentId = 7876
private const val AlarmIdExtra = "alarm.id"

/**
 * Receiver registered to be called when an alarm goes off.
 */
class AndroidAlarmReceiver: BroadcastReceiver() {
    private val Intent.alarmId get() = getStringExtra(AlarmIdExtra)!!.let(::AlarmId)

    override fun onReceive(context: Context, intent: Intent) {
        Kimchi.info("Alarm Received")
        context.startService(context.createStartAlarmServiceIntent(intent.alarmId))
    }
}

/**
 * Create a pending intent used to broadcast an alarm.
 */
fun Context.createAlarmBroadcastPendingIntent(
    id: AlarmId,
): PendingIntent = PendingIntent.getBroadcast(
    this,
    AlarmBroadcastIntentId,
    Intent(this, AndroidAlarmReceiver::class.java).apply {
        putExtra(AlarmIdExtra, id.value)
    },
    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
)
