package com.inkapplications.sleeps.android.alarms

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import com.inkapplications.sleeps.android.R
import com.inkapplications.sleeps.state.alarms.AlarmBeeper
import kimchi.Kimchi
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Uses Android's AudioManager to play an alarm sound upon request.
 */
class AlarmBeeper(
    private val context: Context,
): AlarmBeeper {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var mediaPlayer: MediaPlayer? = null

    suspend fun prepare() {
        Kimchi.trace("Preparing MediaPlayer")
        if (mediaPlayer != null) release()

        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        mediaPlayer = MediaPlayer.create(
            context,
            R.raw.alarm,
            attributes,
            audioManager.generateAudioSessionId()
        ).awaitPrepare()
    }

    private suspend fun MediaPlayer.awaitPrepare(): MediaPlayer {
        return suspendCoroutine { continuation ->
            Kimchi.trace("MediaPlayer Prepared")
            setOnPreparedListener { continuation.resume(this) }
        }
    }

    override suspend fun beep() {
        if (mediaPlayer?.isPlaying == false) {
            Kimchi.trace("Starting Alarm Beep")
            mediaPlayer?.start()
        } else {
            Kimchi.warn("Attempted to play while already in-progress.")
        }
    }

    fun release() {
        Kimchi.trace("Releasing Alarm MediaPlayer")
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
