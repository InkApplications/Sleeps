package com.inkapplications.sleeps.android.alarms

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import com.inkapplications.sleeps.android.createEditAlarmPendingIntent
import com.inkapplications.sleeps.state.alarms.AlarmAccess
import com.inkapplications.sleeps.state.alarms.AlarmType
import kimchi.Kimchi
import kotlinx.datetime.Instant

/**
 * Uses Android's [AlarmManager] to set alarms.
 */
class AndroidAlarmAccess(
    private val context: Context,
    private val alarmManager: AlarmManager,
): AlarmAccess {
    override fun addAlarm(id: AlarmType, time: Instant) {
        Kimchi.info("Adding Alarm $id at $time")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || !alarmManager.canScheduleExactAlarms()) {
            Kimchi.error("Unable to schedule Exact Alarms")
            return
        }
        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(
                time.toEpochMilliseconds(),
                context.createEditAlarmPendingIntent(),
            ),
            context.createAlarmBroadcastPendingIntent(id),
        )
    }

    override fun removeAlarm(id: AlarmType) {
        Kimchi.info("Removing Alarm $id")
        alarmManager.cancel(
            context.createAlarmBroadcastPendingIntent(id),
        )
    }
}

