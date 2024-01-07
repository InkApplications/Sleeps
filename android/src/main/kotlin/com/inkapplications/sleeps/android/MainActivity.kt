package com.inkapplications.sleeps.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import ink.ui.render.compose.ComposeRenderer

class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(ApplicationModule) {
            setContent {
                val renderer = ComposeRenderer(getRenderTheme())
                renderer.render(screenState.collectAsState().value)
            }
        }
    }
}
