package com.inkapplications.sleeps.android.alarms

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.inkapplications.sleeps.android.SleepApplication
import kimchi.Kimchi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val NotificationId = 3893
private const val StopServiceIntentId = 28759
private const val StopAction = "alarm.stop"
private const val StartAction = "alarm.start"

/**
 * Service used for running ongoing alarms.
 */
class AlarmService: Service() {
    private var job: Job? = Job()

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            StartAction -> start()
            StopAction -> stopSelf()
            else -> Kimchi.error("Unknown Alarm Service Action: ${intent.action}")
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() = with (SleepApplication.module) {
        job?.cancel()
        startForeground(NotificationId, notifications.createAlarmNotification())
        job = backgroundScope.launch {
            beeper.prepare()
            alarmExecutor.onStartAlarm()
        }
    }

    override fun onDestroy() {
        job?.cancel()
        SleepApplication.module.beeper.release()
        super.onDestroy()
    }
}

/**
 * Create a pending intent that can be used to stop the alarm service.
 */
fun Context.createStopAlarmServicePendingIntent(): PendingIntent = PendingIntent.getService(
    this,
    StopServiceIntentId,
    Intent(this, AlarmService::class.java).apply {
        action = StopAction
    },
    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
)

/**
 * Create an intent that can be used to start the alarm service.
 */
fun Context.createStartAlarmServiceIntent(): Intent = Intent(this, AlarmService::class.java).apply {
    action = StartAction
}
