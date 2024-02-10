package com.inkapplications.sleeps.android

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import ink.ui.render.compose.ComposeRenderer

private const val EditAlarmIntentId = 13987

class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(SleepApplication.module) {
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { _ ->
                locationAccess.onPermissionChange()
            }.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.POST_NOTIFICATIONS))

            setContent {
                ComposeRenderer(getRenderTheme()).render(screenState.collectAsState().value)
            }
        }
    }
}

/**
 * Create a pending intent that can be used to edit alarms.
 */
fun Context.createEditAlarmPendingIntent(): PendingIntent = PendingIntent.getActivity(
    this,
    EditAlarmIntentId,
    Intent(this, MainActivity::class.java),
    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE,
)
