package com.inkapplications.sleeps.android.alarms

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import com.inkapplications.sleeps.android.SleepApplication
import com.inkapplications.sleeps.state.alarms.AlarmType
import kimchi.Kimchi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

private const val NotificationId = 3893
private const val StopServiceIntentId = 28759
private const val StopAction = "alarm.stop"
private const val StartAction = "alarm.start"
private const val AlarmIdExtra = "alarm.id"

/**
 * Service used for running ongoing alarms.
 */
class AlarmService: Service() {
    private var job: Job? = Job()
    private var wakeLock: PowerManager.WakeLock? = null

    private val Intent.alarmId get() = getStringExtra(AlarmIdExtra).let(AlarmType::findById)

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            StartAction -> start(intent.alarmId)
            StopAction -> stop()
            else -> Kimchi.error("Unknown Alarm Service Action: ${intent.action}")
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun start(alarm: AlarmType) = with (SleepApplication.module) {
        job?.cancel()
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            this::class.simpleName,
        ).apply { acquire() }
        startForeground(NotificationId, notifications.createAlarmNotification())
        job = backgroundScope.launch {
            beeper.prepare()
            // Allow some time for android to post the notification to post before starting the alarm.
            delay(10.seconds)
            alarmExecutor.onStartAlarm(alarm)
        }
    }

    private fun stop() {
        Kimchi.info("Stopping Service")
        stopSelf()
    }

    override fun onDestroy() {
        wakeLock?.release()
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
fun Context.createStartAlarmServiceIntent(
    alarm: AlarmType,
): Intent = Intent(this, AlarmService::class.java).apply {
    action = StartAction
    putExtra(AlarmIdExtra, alarm.id)
}
