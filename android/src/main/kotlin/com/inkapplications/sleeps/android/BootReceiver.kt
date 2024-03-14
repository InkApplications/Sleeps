package com.inkapplications.sleeps.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_BOOT_COMPLETED
import kimchi.Kimchi

class BootReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_BOOT_COMPLETED) return
        Kimchi.trace("BootReceiver.onReceive")
        with(SleepApplication.module) {
            bootController.onDeviceBoot()
        }
    }
}
