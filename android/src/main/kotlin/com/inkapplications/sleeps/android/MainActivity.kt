package com.inkapplications.sleeps.android

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import ink.ui.render.compose.ComposeRenderer

class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val module = ApplicationModule(this)

        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { _ ->
            module.locationProvider.onPermissionsChange()
        }.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
        with(module) {
            setContent {
                val renderer = ComposeRenderer(getRenderTheme())
                renderer.render(screenState.collectAsState().value)
            }
        }
    }
}
