package com.inkapplications.sleeps.android.alarms

import android.app.AlarmManager
import android.content.Context
import com.inkapplications.sleeps.android.createEditAlarmPendingIntent
import com.inkapplications.sleeps.state.alarms.AlarmAccess
import com.inkapplications.sleeps.state.alarms.AlarmId
import kimchi.Kimchi
import kotlinx.datetime.Instant

/**
 * Uses Android's [AlarmManager] to set alarms.
 */
class AndroidAlarmAccess(
    private val context: Context,
    private val alarmManager: AlarmManager,
): AlarmAccess {
    override fun addAlarm(id: AlarmId, time: Instant) {
        Kimchi.info("Adding Alarm for $time")
        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(
                time.toEpochMilliseconds(),
                context.createEditAlarmPendingIntent(),
            ),
            context.createAlarmBroadcastPendingIntent(id),
        )
    }
}

